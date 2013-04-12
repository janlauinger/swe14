package de.shop.bestellverwaltung.service;

import static de.shop.util.Dao.QueryParameter.with;
import static de.shop.util.Constants.KEINE_ID;
import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
//import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.dao.BestellverwaltungDao;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Stateless
@TransactionAttribute(MANDATORY)
@Log
public class Bestellverwaltung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final boolean TRACE = LOGGER.isTraceEnabled();
	
	@Inject
	private BestellverwaltungDao dao;
	
	@Inject 
	private Kundenverwaltung kv;
	
	@Inject
	private transient Event<Bestellung> event;
	
	@Inject
	private ValidatorProvider ValidatorProvider;
	
	public Bestellung findBestellungById(Long id) {
		final Bestellung bestellung = dao.findBestellungById(id);
		return bestellung;
	}
	
	public List<Bestellung> findBestellungByPreis(Integer preis) {
		final List<Bestellung> bestellungen = dao.find(Bestellung.class, Bestellung.FIND_BESTELLUNG_BY_PREIS,
														with(Bestellung.PARAM_PREIS, preis).build());
		return bestellungen;
	}
	
	public List<Bestellung> findBestellungenByKundeId(Long id) {
		final List<Bestellung> bestellungen = dao.findBestellungenByKunde(id);
		return bestellungen;
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
	final Validator validator = ValidatorProvider.getValidator(locale);
	
	final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung, groups);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debugf("END createBestellung: %s", violations);
		throw new BestellungValidationException(bestellung, violations);
		}
	}
	
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		if (bestellung == null) {
			return null;			
		}
		
		if (TRACE) {
			for (Bestellposition bp : bestellung.getBestellpositionen()) {
				LOGGER.tracef("Bestellposition: %s", bp);
			}
		}
			
			kunde = kv.findKundeByEmail(kunde.getEmail());
			kunde.addBestellung(bestellung);
			bestellung.setKunde(kunde);
			bestellung.setBestellId(KEINE_ID);
			
			for (Bestellposition bp : bestellung.getBestellpositionen()) {
				bp.setPositionId(KEINE_ID);
			}
			
			validateBestellung(bestellung, locale, Default.class);
			dao.create(bestellung);
			event.fire(bestellung);
			
			return bestellung;
		}
	

	public Bestellung updateBestellung(Bestellung bestellung, Locale locale) { 
		if (bestellung == null) {
		return null;
		}
		
		validateBestellung(bestellung, locale, Default.class);
		
		final Bestellung vorhandeneBestellung = findBestellungById(bestellung.getBestellId());
		
		if 	(vorhandeneBestellung == null) {
			return null;
		}
		
		bestellung = dao.update(bestellung);
		
		return bestellung;
	}
	
	

}

