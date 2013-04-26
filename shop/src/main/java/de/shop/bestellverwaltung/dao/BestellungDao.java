package de.shop.bestellverwaltung.dao;

import static de.shop.util.AbstractDao.QueryParameter.with;

import java.util.List;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.AbstractDao;
import de.shop.util.Log;

@Log
public class BestellungDao extends AbstractDao<Bestellung, Long> {
	private static final long serialVersionUID = -6739243612185905236L;

	public enum FetchType {
		NUR_BESTELLUNG,
		MIT_KUNDE
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	public List<Bestellung> findAllBestellungen(FetchType fetch, OrderType order) {
		List<Bestellung> bestellung = null;
		
		switch (fetch) {
		case NUR_BESTELLUNG:
			bestellung = OrderType.ID.equals(order)
			         ? find(Bestellung.FIND_ALL_BESTELLUNGEN_ORDER_BY_ID)
					 : find(Bestellung.FIND_ALL_BESTELLUNGEN);
			break;
		
		case MIT_KUNDE:
			bestellung = find(Bestellung.FIND_ALL_BESTELLUNGEN_FETCH_KUNDE);
			break;
		
		default:
			bestellung = OrderType.ID.equals(order)
	         ? find(Bestellung.FIND_ALL_BESTELLUNGEN_ORDER_BY_ID)
			: find(Bestellung.FIND_ALL_BESTELLUNGEN);
	         break;
		}
		
		return bestellung;
	}
	
	public Bestellung findBestellungById(Long id, FetchType fetch) {
		Bestellung bestellung = null;
		
		switch (fetch) {
		case NUR_BESTELLUNG:
			bestellung = find(id);
			break;
		case MIT_KUNDE:
			bestellung = findSingle(Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_KUNDE,
					with(Bestellung.PARAM_ID_BESTELLUNG, id).build());
			break;
			
		default:
			bestellung = find(id);
			break;
		}
		
		return bestellung;
	}
}
