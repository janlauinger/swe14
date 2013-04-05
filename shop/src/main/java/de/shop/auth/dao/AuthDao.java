package de.shop.auth.dao;

import static de.shop.util.Constants.KUNDE_ROLLE_TABELLE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.WARNING;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import de.shop.util.InternalError;
import de.shop.util.Log;

@Log
public class AuthDao implements Serializable {
	private static final long serialVersionUID = -3010834529523823878L;

	@Inject
	private transient Logger logger;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Resource(lookup = "java:jboss/UserTransaction")
	private transient UserTransaction trans;
	
	@PostConstruct
	private void postConstruct() {
		logger.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		logger.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	
	public List<RolleType> findRollenByUsername(String username) {
		final String queryStr = "SELECT r.name"
				                + " FROM kunde k"
				                + "      INNER JOIN kunde_rolle kr ON k.kunden_id = kr.kunde_fk"
				                + "      INNER JOIN rolle r ON kr.rolle_fk = r.id"
				                + " WHERE k.username = '" + username + '\'';
		logger.log(FINEST, queryStr);
		
		
		
		final Query query = em.createNativeQuery(queryStr);
		@SuppressWarnings("unchecked")
		final List<String> rolleStrList = query.getResultList();
		
		final List<RolleType> rollen = new ArrayList<>(rolleStrList.size());
		for (String rolleStr : rolleStrList) {
			final RolleType rolle = RolleType.valueOf(rolleStr.toUpperCase());
			rollen.add(rolle);
		}
		
		return rollen;
	}

	/**
	 */
	public void addRollen(Long kundeId, Collection<RolleType> rollen) {
		if (rollen == null || rollen.isEmpty()) {
			return;
		}

		final String insertTemplate = "INSERT INTO " + KUNDE_ROLLE_TABELLE + " VALUES(" + kundeId + ", ''{0}'')";
		for (RolleType rolle: rollen) {
			final int rolleId = rolle.getValue();
			final String insertStr = MessageFormat.format(insertTemplate, rolleId);
			logger.log(FINEST, "INSERT string = {0}", insertStr);
			
			final Query query = em.createNativeQuery(insertStr);
			try {
				query.executeUpdate();
			}
			catch (EntityExistsException e) {
				logger.log(WARNING, "Der Kunde mit ID {0} hat bereits die Rolle {1}",
						   new Object[] {kundeId, rolle.name() });
				try {
					trans.setRollbackOnly();
				}
				catch (IllegalStateException | SystemException e2) {
					throw new InternalError(e2);
				}
				return;
			}
		}
	}
	
	/**
	 */
	public void removeRollen(Long kundeId, Collection<RolleType> rollen) {
		if (rollen == null || rollen.size() == 0) {
			return;
		}

		final String deleteTemplate = "DELETE FROM " + KUNDE_ROLLE_TABELLE + " WHERE kunde_fk = " + kundeId
                                      + " AND rolle_fk = ''{0}''";
		for (RolleType rolle: rollen) {
			final int rolleId = rolle.getValue();
			final String deleteStr = MessageFormat.format(deleteTemplate, rolleId);
			logger.log(FINEST, "DELETE string = {0}", deleteStr);
			final Query query = em.createNativeQuery(deleteStr);
			query.executeUpdate();
		}
	}
}
