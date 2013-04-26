package de.shop.artikelverwaltung.dao;

import javax.enterprise.inject.Alternative;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;

@Alternative
@Log
public class ArtikelDaoMock extends ArtikelDao {
	private static final long serialVersionUID = -2919310633845009282L;

	@Override
	public Artikel find(Long id) {
		final Artikel artikel = new Artikel();
		artikel.setIdArtikel((Long) id);
		artikel.setBezeichnung("Bezeichnung" + id);
		return artikel;
	}
}
