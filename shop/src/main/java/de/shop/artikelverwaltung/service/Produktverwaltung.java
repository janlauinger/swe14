package de.shop.artikelverwaltung.service;

//import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Dao.QueryParameter.with;
//import static de.shop.util.Constants.KEINE_ID;
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
//import javax.validation.groups.Default;
//import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.dao.ArtikelverwaltungDao;
import de.shop.artikelverwaltung.dao.ArtikelverwaltungDao.FetchType;
import de.shop.artikelverwaltung.domain.Produkt;
//import de.shop.artikelverwaltung.service.ProduktValidationException;
import de.shop.util.ValidationService;

@Stateless
@TransactionAttribute(MANDATORY)
public class Produktverwaltung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
//	private static final boolean TRACE = LOGGER.isTraceEnabled();
	
//	@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject
	private ArtikelverwaltungDao dao;
	
	@Inject
	private transient Event<Produkt> event;
	
	@Inject
	private ValidationService validationService;
	
	public Produkt findProduktById(Long id) {
		final Produkt produkt = dao.find(id);
		return produkt;
	}
	
	public List<Produkt> findProduktByPreis(Integer preis) {
		final List<Produkt> produkte = dao.find(Produkt.class, Produkt.FIND_PRODUKT_MAX_PREIS,
														with(Produkt.PARAM_PREIS, preis).build());
		return produkte;
	}
	
	public Produkt findProduktByBez(String bezeichnung) {
		final Produkt produkt = dao.finde(Produkt.class, bezeichnung);
		return produkt;
	}
	
	public List<Produkt> findProduktByBez(String bezeichnung, FetchType fetch) {
		final List<Produkt> produkt = dao.find(Produkt.class, Produkt.FIND_PRODUKT_BY_BEZ,
													with(Produkt.PARAM_BEZEICHNUNG, bezeichnung).build());
		return produkt;	
	}
	
	private void validateProdukt(Produkt produkt, Locale locale, Class<?>... groups) {
	final Validator validator = validationService.getValidator(locale);
	
	final Set<ConstraintViolation<Produkt>> violations = validator.validate(produkt, groups);
	if (violations != null && !violations.isEmpty()) {
		LOGGER.debugf("END createProdukt: %s", violations);
		throw new ProduktValidationException(produkt, violations);
	}
}

	public Produkt updateProdukt(Produkt produkt, Locale locale) { 
		if (produkt == null) {
		return null;
		}
		
		validateProdukt(produkt, locale);
		
		final Produkt vorhandeneProdukt = findProduktById(produkt.getProduktId());
		
		if 	(vorhandeneProdukt == null) {
			return null;
		}
		
		produkt = dao.update(produkt);
		
		return produkt;
	}

	public List<Produkt> findProduktByIds(List<Long> produktIds) {
		final List<Produkt> produkt = dao.findProdukteByIds(produktIds);
		return produkt;
	}
	
	public Produkt createProdukt(Produkt produkt, Locale locale) {
		if (produkt == null) {
			return null;			
		}
			
			validateProdukt(produkt, locale);
			dao.create(produkt);
			event.fire(produkt);
			
			return produkt;
		}

	public void deleteProduktById(Long produktId) {
		// TODO Auto-generated method stub
		final Produkt produkt = findProduktById(produktId);
		if (produkt == null) {
			//Das Produkt existiert nicht oder ist bereits gelöscht
			return;
		}
		
//		if(!produkt.getProduktId().isEmpty()) {
//			throw new ProduktDeleteBestellungException(produkt);
//		}
		
		dao.delete(produkt);
		
	}

}
