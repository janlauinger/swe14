package de.shop.bestellverwaltung.service;


import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.dao.BestellpositionDao;
import de.shop.bestellverwaltung.dao.BestellungDao;
import de.shop.bestellverwaltung.dao.BestellungDao.FetchType;
import de.shop.bestellverwaltung.dao.BestellungDao.OrderType;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class Bestellverwaltung implements Serializable {
	private static final long serialVersionUID = 4949585031232145208L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private BestellungDao dao;
	
	@Inject
	private BestellpositionDao bpdao;
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		
		if (bestellung == null) return null;
		
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			validateBestellposition(bp, locale, Default.class, PasswordGroup.class);
			LOGGER.log(FINEST, "Bestellposition: {0}", bp);				
		}
		
		validateBestellung(bestellung, locale, Default.class, PasswordGroup.class);
		
		
		// damit "kunde" dem EntityManager bekannt ("managed") ist
		kunde = kv.findKundeById(kunde.getIdKunde(), de.shop.kundenverwaltung.dao.KundeDao.FetchType.MIT_BESTELLUNGEN);
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);
		dao.create(bestellung);
		
		event.fire(bestellung);
		return bestellung;
	}
	// Fehlerausgabe in Console
	public Bestellung updateBestellung(Bestellung bestellung, Locale locale) {
		if (bestellung == null) return null;
		validateBestellung(bestellung, locale, Default.class, PasswordGroup.class);
		return dao.update(bestellung);
	}
	
 	public List<Bestellung> findAllBestellungen(FetchType fetch, OrderType order) {
		final List<Bestellung> bestellungen = dao.findAllBestellungen(fetch, order);
		return bestellungen;
	}
 	
	public Bestellung findBestellungById(Long id, FetchType fetch) {
		final Bestellung bestellung = dao.findBestellungById(id, fetch);
		return bestellung;
	}
	
	public Bestellposition findBestellpositionById(Long id) {
		final Bestellposition bestellposition = bpdao.findBestellpositionById(id);
		return bestellposition;
	}
	 
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung, groups);
		if (!violations.isEmpty()) {
			throw new BestellungValidationException(bestellung, violations);
		}
	}
	
	private void validateBestellposition(Bestellposition bestellposition, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellposition>> violations = validator.validate(bestellposition, groups);
		if (!violations.isEmpty()) {
			throw new BestellpositionValidationException(bestellposition, violations);
		}
	}
}