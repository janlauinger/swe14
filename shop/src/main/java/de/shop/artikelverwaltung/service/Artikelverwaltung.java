package de.shop.artikelverwaltung.service;

import static de.shop.util.AbstractDao.QueryParameter.with;
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

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.dao.ArtikelDao;
import de.shop.artikelverwaltung.dao.ArtikelDao.OrderType;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class Artikelverwaltung implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelDao dao;
	
	@Inject
	private ValidatorProvider ValidatorProvider;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	/**
	 */
 	public List<Artikel> findVerfuegbareArtikel() {
 		final List<Artikel> artikelListe = dao.find(Artikel.FIND_VERFUEGBARE_ARTIKEL);
 		return artikelListe;
 	}
 	
	
	/**
	 */
 	public Artikel findArtikelById(Long id) {
		final Artikel artikel = dao.find(id);
		return artikel;
	}
	
	/**
	 */
 	public List<Artikel> findArtikelByIds(List<Long> ids) {
		final List<Artikel> artikel = dao.findArtikelByIds(ids);
		return artikel;
 	}

 	
	public List<Artikel> findAllArtikel(OrderType order) {
		final List<Artikel> artikel = dao.findAllArtikel(order);
		return artikel;
	}
	
	/**
	 */
 	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			final List<Artikel> artikelListe = findVerfuegbareArtikel();
			return artikelListe;
		}
		
		final List<Artikel> artikelListe = dao.find(Artikel.FIND_ARTIKEL_BY_BEZ,
				                                    with(Artikel.PARAM_BEZEICHNUNG,
				                                    	 "%" + bezeichnung + "%").build());
		
		return artikelListe;
	}

	
	/**
	 */
 	public List<Artikel> findArtikelByMaxPreis(float preis) {
		final List<Artikel> artikelListe = dao.find(Artikel.FIND_ARTIKEL_MAX_PREIS,
				                                    with(Artikel.PARAM_PREIS, preis).build());
		return artikelListe;
	}
 	
 	/**
 	 */
 	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}
		
		// Werden alle Constraints beim Einfuegen gewahrt?
		validateArtikel(artikel, locale, Default.class);
		
		artikel = dao.create(artikel);
		
		return artikel;
	}
 	
	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
		// Werden alle Constraints beim Einfuegen gewahrt?
		final Validator validator = ValidatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
		if (!violations.isEmpty()) {
			throw new ArtikelValidationException(artikel, violations);
		}
	}
	// Fehlerausgabe in Console
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return null;
		}
		validateArtikel(artikel, locale, Default.class, IdGroup.class);
		artikel = dao.update(artikel);
		return artikel;
	}
}