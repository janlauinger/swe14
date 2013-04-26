package de.shop.bestellverwaltung.dao;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.util.AbstractDao;
import de.shop.util.Log;

@Log
public class BestellpositionDao extends AbstractDao<Bestellposition, Long> {

	private static final long serialVersionUID = 1032200849726733677L;

	public Bestellposition findBestellpositionById(Long id) {
		Bestellposition bestellposition = null;
		
			bestellposition = find(id);
		return bestellposition;
	}
}