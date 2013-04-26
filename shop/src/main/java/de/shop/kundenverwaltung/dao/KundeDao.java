package de.shop.kundenverwaltung.dao;

import static de.shop.util.AbstractDao.QueryParameter.with;

//import java.util.ArrayList;
import java.util.List;

//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Join;
//import javax.persistence.criteria.JoinType;
//import javax.persistence.criteria.Path;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;

//import de.shop.bestellverwaltung.domain.Bestellposition;
//import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDao;
//import de.shop.util.Log;

public class KundeDao extends AbstractDao<Kunde, Long> {
	private static final long serialVersionUID = 9072716732555202442L;

	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN
	}
	
	public enum OrderType {
		KEINE,
		ID
	}

	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		List<Kunde> kunden = null;
		
		switch (fetch) {
		case NUR_KUNDE:
			kunden = OrderType.ID.equals(order)
			         ? find(Kunde.FIND_KUNDEN_ORDER_BY_ID)
					 : find(Kunde.FIND_KUNDEN);
			break;
		
		case MIT_BESTELLUNGEN:
			kunden = find(Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN);
			break;
		
		default:
			kunden = OrderType.ID.equals(order)
	         ? find(Kunde.FIND_KUNDEN_ORDER_BY_ID)
			 : find(Kunde.FIND_KUNDEN);
	         break;
		}
		
		return kunden;
	}
	
	public Kunde findKundeById(Long id, FetchType fetch) {
		Kunde kunde = null;
		
		switch (fetch) {
		case NUR_KUNDE:
			kunde = find(id);
			break;
		case MIT_BESTELLUNGEN:
			kunde = findSingle(Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
					with(Kunde.PARAM_KUNDE_ID, id).build());
			break;
			
		default:
			kunde = find(id);
			break;
		}
		
		return kunde;
	}

	public Kunde findKundeByEmail(String email, FetchType fetch) {
		Kunde kunde = null;
		
		switch (fetch) {
		case NUR_KUNDE:
			kunde = findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
					with(Kunde.PARAM_KUNDE_EMAIL, email).build());
			break;
			
		case MIT_BESTELLUNGEN:
			kunde = findSingle(Kunde.FIND_KUNDE_BY_EMAIL_FETCH_BESTELLUNGEN,
					with(Kunde.PARAM_KUNDE_EMAIL, email).build());
			
		default:
			kunde = findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
					with(Kunde.PARAM_KUNDE_EMAIL, email).build());
			break;	
		}
		return kunde;
	}
	
	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch) {
		List<Kunde> kunden = null;
		
		switch(fetch) {
		case NUR_KUNDE:
			kunden = find(Kunde.FIND_KUNDEN_BY_NACHNAME,
					 with(Kunde.PARAM_KUNDE_NACHNAME, nachname).build());
			break;
		case MIT_BESTELLUNGEN:
			kunden = find(Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
					 with(Kunde.PARAM_KUNDE_NACHNAME, nachname).build());
			break;
		default:
			kunden = find(Kunde.FIND_KUNDEN_BY_NACHNAME,
					 with(Kunde.PARAM_KUNDE_NACHNAME, nachname).build());
			break;
		}
		
		return kunden;
	}
}