package de.shop.bestellverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.bestellverwaltung.domain.Bestellung;


/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Kunden nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class BestellungValidationException extends AbstractBestellverwaltungException {
	private static final long serialVersionUID = 4255133082483647701L;
	private final Bestellung bestellung;
	private final Collection<ConstraintViolation<Bestellung>> violations;
	
//	@Resource(lookup = "java:jboss/UserTransaction")
//	private UserTransaction trans;

	public BestellungValidationException(Bestellung bestellung,
			                        Collection<ConstraintViolation<Bestellung>> violations) {
		super("Ungueltige Bestellung: " + bestellung + ", Violations: " + violations);
		this.bestellung = bestellung;
		this.violations = violations;
	}
	
//	@PostConstruct
//	private void setRollbackOnly() {
//		try {
//			if (trans.getStatus() == STATUS_ACTIVE) {
//				trans.setRollbackOnly();
//			}
//		}
//		catch (IllegalStateException | SystemException e) {
//			throw new InternalError(e);
//		}
//	}
	public Bestellung getBestellung() {
		return bestellung;
	}
	
	public Collection<ConstraintViolation<Bestellung>> getViolations() {
		return violations;
	}
}
