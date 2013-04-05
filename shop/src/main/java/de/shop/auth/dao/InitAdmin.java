package de.shop.auth.dao;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;

@Startup
@DependsOn("InitRollen")
@Singleton
public class InitAdmin {
	@Inject
	private Logger logger;
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private Kundenverwaltung kv;
	
	private static final Long ID_VORHANDEN = Long.valueOf(5);
	
	@PostConstruct
	private void initDb() {
		
		Kunde admin = kv.findKundeById(ID_VORHANDEN, FetchType.NUR_KUNDE);
		if (admin != null) {
			logger.info("Admin ist bereits in der DB");
			return;
		}
		
		logger.info("Admin in die DB laden");
	}
}
