package de.shop.kundenverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractKundenverwaltungException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractKundenverwaltungException(String msg) {
		super(msg);
	}
	
	public AbstractKundenverwaltungException(String msg, Throwable t) {
		super(msg, t);
	}
}
