package de.shop.artikelverwaltung.rest;

import de.shop.artikelverwaltung.service.AbstractArtikelverwaltungException;

public class InvalidDateException extends AbstractArtikelverwaltungException {
	private static final long serialVersionUID = 2113917506853352685L;
	
	private final String invalidDate;
	
	public InvalidDateException(String invalidDate) {
		super("Ungueltiges Datum: " + invalidDate);
		this.invalidDate = invalidDate;
	}
	
	public String getInvalidDate() {
		return invalidDate;
	}

}
