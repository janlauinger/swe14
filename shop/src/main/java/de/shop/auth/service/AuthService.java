package de.shop.auth.service;

import static de.shop.kundenverwaltung.domain.Kunde.FIND_USERNAME_BY_USERNAME_PREFIX;
import static de.shop.kundenverwaltung.domain.Kunde.PARAM_USERNAME_PREFIX;
import static de.shop.util.Dao.QueryParameter.with;
import static de.shop.util.Constants.HASH_ALGORITHM;
import static de.shop.util.Constants.HASH_CHARSET;
import static de.shop.util.Constants.HASH_ENCODING;
import static de.shop.util.Constants.SECURITY_DOMAIN;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static org.jboss.security.auth.spi.Util.createPasswordHash;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.security.SimpleGroup;

import de.shop.auth.dao.AuthDao;
import de.shop.auth.dao.RolleType;
import de.shop.kundenverwaltung.dao.KundenverwaltungDao;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.InternalError;
import de.shop.util.Log;


/**
 */
@ApplicationScoped
@Log
public class AuthService implements Serializable {
	private static final long serialVersionUID = -2736040689592627172L;
	
	private static final String LOCALHOST = "localhost";
	private static final int MANAGEMENT_PORT = 9999;
	
	@Inject
	private transient Logger logger;
	
	@Inject
	private AuthDao dao;
	
	@Inject
	private KundenverwaltungDao kundeDao;
	
	@PostConstruct
	private void postConstruct() {
		logger.log(INFO, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.log(INFO, "CDI-faehiges Bean {0} wird geloescht", this);
	}


	/**
	 * In Anlehnung an org.jboss.test.PasswordHasher von Scott Stark
	 */
	public String verschluesseln(String password) {
		if (password == null) {
			return null;
		}
		
		// Alternativ:
		// org.jboss.crypto.CryptoUtil.createPasswordHash
		final String passwordHash = createPasswordHash(HASH_ALGORITHM, HASH_ENCODING, HASH_CHARSET,
				                                       null, password);
		return passwordHash;
	}
	
	/**
	 */
	public boolean validatePassword(Kunde kunde, String passwort) {
		if (kunde == null) {
			return false;
		}
		
		final String verschluesselt = verschluesseln(passwort);
		final boolean result = verschluesselt.equals(kunde.getPasswort());
		return result;
	}
	
	/**
	 */
	public void addRollen(Long kundeId, Collection<RolleType> rollen) {
		dao.addRollen(kundeId, rollen);
		flushSecurityCache(kundeId.toString());
	}

	/**
	 */
	public void removeRollen(Long kundeId, Collection<RolleType> rollen) {
		dao.removeRollen(kundeId, rollen);
		flushSecurityCache(kundeId.toString());
	}
	
	/**
	 * siehe http://community.jboss.org/thread/169263
	 * siehe https://docs.jboss.org/author/display/AS7/Management+Clients
	 * siehe https://github.com/jbossas/jboss-as/blob/master/controller-client/src/main/java/ \
	 * \org/jboss/as/controller/client/ModelControllerClient.java
	 * siehe http://community.jboss.org/wiki/FormatOfADetypedOperationRequest
	 * siehe http://community.jboss.org/wiki/DetypedDescriptionOfTheAS7ManagementModel
	 * 
	 * Gleicher Ablauf mit CLI (= command line interface):
	 * cd %JBOSS_HOME%\bin
	 * jboss-admin.bat
	 *    connect
	 *    /subsystem=security/security-domain=shop:flush-cache(principal=myUserName)
	 */
	private void flushSecurityCache(String username) {
		ModelControllerClient client;
		try {
			client = ModelControllerClient.Factory.create(LOCALHOST, MANAGEMENT_PORT);
		}
		catch (UnknownHostException e) {
			// Kann nicht passieren: sonst waere "localhost" nicht bekannt
			throw new IllegalStateException(e);
		}
		
		try {
			final ModelNode address = new ModelNode();
			address.add("subsystem", "security");
			address.add("security-domain", SECURITY_DOMAIN);

			final ModelNode operation = new ModelNode();
			operation.get("address").set(address);
			operation.get("operation").set("flush-cache");
			operation.get("principal").set(username);

			try {
				final ModelNode result = client.execute(operation);
				final String resultString = result.get("outcome").asString();
				if (!"success".equals(resultString)) {
					throw new IllegalStateException("FEHLER bei der Operation \"flush-cache\" fuer den Security-Cache: "
							                        + resultString);
				}
			}
			catch (IOException e) {
				throw new IllegalStateException("FEHLER bei der Operation \"flush-cache\" fuer den Security-Cache", e);
			}

		}
		finally {
			if (client != null) {
				try {
					client.close();
				}
				catch (IOException e) {
					throw new IllegalStateException("FEHLER bei der Methode close() fuer den Management-Client", e);
				}
			}
		}
	}
	
	/**
	 */
	public List<RolleType> getEigeneRollen() {		
		final List<RolleType> rollen = new LinkedList<>();
		
		// Authentifiziertes Subject ermitteln
		Subject subject = null;
		try {
			subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
		}
		catch (PolicyContextException e) {
			final InternalError error = new InternalError(e);
			logger.log(SEVERE, error.getMessage(), error);
			throw error;
		}
		if (subject == null) {
			return null;
		}

		// Gruppe "Roles" ermitteln
		final Set<Principal> principals = subject.getPrincipals(Principal.class);
		for (Principal p : principals) {
			if (!(p instanceof SimpleGroup)) {
				continue;
			}

			final SimpleGroup sg = (SimpleGroup) p;
			if (!"Roles".equals(sg.getName())) {
				continue;
			}
			
			// Rollen ermitteln
			final Enumeration<Principal> members = sg.members();
			while (members.hasMoreElements()) {
				final String rolle = members.nextElement().toString();
				if (rolle != null) {
					rollen.add(RolleType.valueOf(rolle.toUpperCase()));
				}
			}
		}
		return rollen;
	}

	/**
	 */
	public List<String> findUsernameListByUsernamePrefix(String usernamePrefix) {
		final List<String> usernameList = kundeDao.find(String.class,
				                                        FIND_USERNAME_BY_USERNAME_PREFIX,
				                                        with(PARAM_USERNAME_PREFIX, usernamePrefix + '%').build());
		return usernameList;
	}

	/**
	 */
	public List<RolleType> findRollenByUsername(String username) {
		final List<RolleType> rollen = dao.findRollenByUsername(username); 
		return rollen;		
	}
}
