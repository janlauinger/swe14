package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellposition;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class BestellpositionValidationException extends AbstractBestellverwaltungException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Bestellposition bestellposition;
	private final Collection<ConstraintViolation<Bestellposition>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public BestellpositionValidationException(Bestellposition bestellposition,
			                        Collection<ConstraintViolation<Bestellposition>> violations) {
		super("Ungueltige Bestellposition: " + bestellposition + ", Violations: " + violations);
		this.bestellposition = bestellposition;
		this.violations = violations;
	}
	
	public Bestellposition getBestellposition() {
		return bestellposition;
	}
	
	public Collection<ConstraintViolation<Bestellposition>> getViolations() {
		return violations;
	}
}
