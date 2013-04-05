package de.shop.artikelverwaltung.service;

import java.util.Collection;
import java.util.Date;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Produkt;
//import de.shop.artikelverwaltung.service.AbstractArtikelverwaltungException;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte 
 * eines Produkts nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ProduktValidationException extends AbstractArtikelverwaltungException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Date erzeugt;
	private final Long produktId;
	private final Collection<ConstraintViolation<Produkt>> violations;

	public ProduktValidationException(Produkt produkt,
			                             Collection<ConstraintViolation<Produkt>> violations) {
		super(violations.toString());
		
		if (produkt == null) {
			this.erzeugt = null;
			this.produktId = null;
		}
		else {
			this.erzeugt = produkt.getErzeugt();
//			final Produkt produkt = produkt.getProdukt();
			this.produktId = produkt == null ? null : produkt.getProduktId();
		}
		
		this.violations = violations;
	}
	
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}
	
	public Long getProduktId() {
		return produktId;
	}
	
	public Collection<ConstraintViolation<Produkt>> getViolations() {
		return violations;
	}
}
