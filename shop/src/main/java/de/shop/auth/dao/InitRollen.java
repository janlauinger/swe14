package de.shop.auth.dao;

import static java.util.logging.Level.INFO;

import java.math.BigInteger;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Startup
@Singleton
public class InitRollen {
	private static final String TBL_ROLLE = "rolle";
	private static final String COL_ID = "id";
	private static final String COL_NAME = "bezeichnung";
	
	@Inject
	private Logger logger;
	
	@PersistenceContext
	private EntityManager em;
	
	@PostConstruct
	private void initDb() {
		final Query anzahlRollen = em.createNativeQuery("SELECT COUNT(*) FROM rolle");
		final BigInteger anzahl = (BigInteger) anzahlRollen.getSingleResult();
		if (anzahl.intValue() != 0) {
			logger.info("Rollen fuer Security sind bereits in der DB");
			return;
		}
		
		logger.info("Rollen fuer Security in die DB laden");		
		int i = 1;
		for (RolleType rolle : RolleType.values()) {
			logger.log(INFO, "   Rolle: {0}", rolle);		
			final StringBuilder sb = new StringBuilder("INSERT INTO " + TBL_ROLLE
                                                       + " (" + COL_ID + ", " + COL_NAME + ") VALUES(");
			sb.append(i++)
			  .append(", '")
			  .append(rolle.name().toLowerCase())
			  .append("')");
			em.createNativeQuery(sb.toString())
			  .executeUpdate();
		}
	}
}
