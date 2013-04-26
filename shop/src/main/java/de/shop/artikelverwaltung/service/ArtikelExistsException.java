package de.shop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ArtikelExistsException extends AbstractArtikelverwaltungException {
	private static final long serialVersionUID = 4867667611097919943L;
	private final Long id;
	
	public ArtikelExistsException(Long id) {
		super("Der Artikel " + id + " existiert bereits");
		this.id = id;
	}

	public Long getIdArtikel() {
		return id;
	}
}