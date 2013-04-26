package de.shop.kundenverwaltung.service;

import static de.shop.util.AbstractDao.QueryParameter.with;
import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.dao.KundeDao;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.dao.KundeDao.OrderType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class Kundenverwaltung implements Serializable {
	private static final long serialVersionUID = 3692819050477194655L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private KundeDao dao;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		final List<Kunde> kunden = dao.findAllKunden(fetch, order);
		return kunden;
	}
	
	public Kunde findKundeById(Long id, FetchType fetch) {
		final Kunde kunde = dao.findKundeById(id, fetch);
		return kunde;
	}
	
	public List<Bestellung> findBestellungenByKundeId(Long id) {
		Kunde k = dao.findKundeById(id, FetchType.MIT_BESTELLUNGEN);
		return k.getBestellungen();
	}
	
	public Kunde findKundeByEmail(String email, FetchType fetch) {
		final Kunde kunde = dao.findKundeByEmail(email, fetch);
		return kunde;
	}
	
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch) {
		final List<Kunde> kunden = dao.findKundenByNachname(nachname, fetch);
		return kunden;
	}
	
	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		// Werden alle Constraints beim Einfuegen gewahrt?
		validateKunde(kunde, locale, Default.class, PasswordGroup.class);
		
		// Pruefung, ob die Email-Adresse schon existiert
		final Kunde vorhandenerKunde = dao.findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
                                                              with(Kunde.PARAM_KUNDE_EMAIL,
                                                               	   kunde.getEmail()).build());
		if (vorhandenerKunde != null) {
			throw new EmailExistsException(kunde.getEmail());
		}
		LOGGER.finest("Email-Adresse existiert noch nicht");
		
		kunde.setIdKunde(KEINE_ID);
		kunde = dao.create(kunde);
		
		return kunde;
	}

	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde, groups);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	// Fehlerausgabe in Console
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}
		
		validateKunde(kunde, locale, Default.class, PasswordGroup.class);
		
		final Kunde vorhandenerKunde = dao.findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
													with(Kunde.PARAM_KUNDE_EMAIL,
														kunde.getEmail()).build());
		if (vorhandenerKunde != null && vorhandenerKunde.getIdKunde().longValue() != kunde.getIdKunde().longValue()) {
			throw new EmailExistsException(kunde.getEmail());
		}
		LOGGER.finest("Email-Adresse existiert noch nicht");

		kunde = dao.update(kunde);
		return kunde;
	}
	
}