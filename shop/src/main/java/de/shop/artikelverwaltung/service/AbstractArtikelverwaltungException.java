package de.shop.artikelverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractArtikelverwaltungException extends AbstractShopException {
		private static final long serialVersionUID = -626920099480136224L;

		public AbstractArtikelverwaltungException(String msg) {
			super(msg);
		}
	}

