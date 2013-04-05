package de.shop.kundenverwaltung.dao;

import static de.shop.util.Dao.QueryParameter.with;

//import java.util.ArrayList;
import java.util.List;

//import javax.cache.annotation.CachePut;
//import javax.cache.annotation.CacheRemoveEntry;
//import javax.cache.annotation.CacheResult;
//import javax.cache.annotation.CacheValue;
//import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
//import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Dao;
import de.shop.util.Log;

//@Named
@Log
public class KundenverwaltungDao extends Dao<Kunde, Long> {
	private static final long serialVersionUID = -6166455307123578665L;

	public enum FetchType {
		NUR_KUNDE,
		MIT_BESTELLUNGEN,
		MIT_BESTELLUNGEN_UND_LIEFERUNGEN
	}
	
	public enum Order {
		KEINE,
		ID
	}

	public Kunde findKunde(Long id, FetchType fetch) {
		Kunde kunde = null;
		
		switch (fetch) {
			case NUR_KUNDE:
				kunde = find(id);
				break;
			
			case MIT_BESTELLUNGEN:
				kunde = findSingle(Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
                                   with(Kunde.PARAM_KUNDE_ID, id).build());
				break;

			default:
				kunde = find(id);
				break;
		}

		return kunde;
	}
	
//	@CacheResult(cacheName = "kunde-cache")
//	public Kunde findKunde(Long id) {
//		Kunde kunde = find(id);
//		return kunde;
//	}
//
//	@CacheResult(cacheName = "kunde-cache")
//	public Kunde findKundeByEmail(String email) {
//		final Kunde kunde = findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
//				                               with(Kunde.PARAM_KUNDE_EMAIL, email).build());
//		return kunde;
//	}
//
//	@CachePut(cacheName = "user-cache")
//	public void createKunde(Long id, @CacheValue Kunde kunde) {
//		create(kunde);
//	}
//
//	@CacheRemoveEntry(cacheName = "user-cache")
//	public void removeKunde(Long id) {
//		deleteUsingId(id);
//	}
	
	/**
	 */
	public List<Kunde> findAllKunden(FetchType fetch, Order order) {
		List<Kunde> kunden = null;
		
		switch (fetch) {
			case NUR_KUNDE:
				kunden = Order.ID.equals(order)
				         ? find(Kunde.FIND_KUNDEN_ORDER_BY_ID)
						 : find(Kunde.FIND_KUNDEN);
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = find(Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN);
				break;

			default:
				kunden = Order.ID.equals(order)
				         ? find(Kunde.FIND_KUNDEN_ORDER_BY_ID)
						 : find(Kunde.FIND_KUNDEN);
				break;
		}

		return kunden;
	}
	
	public Kunde findKundeByEmail(String email) {
		final Kunde kunde = findSingle(Kunde.FIND_KUNDE_BY_EMAIL,
				                               with(Kunde.PARAM_KUNDE_EMAIL, email).build());
		return kunde;
	}
	

	/**
	 */
	public List<Kunde> findKundenByName(String name, FetchType fetch) {
		List<Kunde> kunden = null;
		
		switch (fetch) {
			case NUR_KUNDE:
				kunden = find(Kunde.FIND_KUNDE_BY_NAME,
        	                  with(Kunde.PARAM_KUNDE_NAME, name).build());
				break;
			
			case MIT_BESTELLUNGEN:
				kunden = find(Kunde.FIND_KUNDEN_BY_NAME_FETCH_BESTELLUNGEN,
			                  with(Kunde.PARAM_KUNDE_NAME, name).build());

			default:
				kunden = find(Kunde.FIND_KUNDE_BY_NAME,
        	                  with(Kunde.PARAM_KUNDE_NAME, name).build());
				break;
		}

		return kunden;
	}


	
	
	public List<Kunde> findKundenByNameCriteria(String name) {
		// SELECT k
		// FROM   AbstractKunde k
		// WHERE  k.nachname = ?
		
		final EntityManager em = getEntityManager();
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		//final Path<String> nachnamePath = k.get(AbstractKunde_.nachname);
		final Path<String> namePath = k.get("name");
		
		final Predicate pred = builder.equal(namePath, name);
		criteriaQuery.where(pred);

		final TypedQuery<Kunde> query = em.createQuery(criteriaQuery);
		final List<Kunde> kunden = query.getResultList();
		return kunden;
	}
	
	public List<Kunde> findKundenMitMinBestMenge(short minMenge) {
		// SELECT DISTINCT k
		// FROM   AbstractKunde k
		//        JOIN k.bestellungen b
		//        JOIN b.bestellpositionen bp
		// WHERE  bp.anzahl >= ?
		
		final EntityManager em = getEntityManager();
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery  = builder.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		//final Join<AbstractKunde, Bestellung> b = k.join(AbstractKunde_.bestellungen);
		//final Join<Bestellung, Bestellposition> bp = b.join(Bestellung_.bestellpositionen);
		//criteriaQuery.where(builder.gt(bp.<Short>get(Bestellposition_.anzahl), minMenge))
		//             .distinct(true);
		final Join<Kunde, Bestellung> b = k.join("bestellungen");
		final Join<Bestellung, Bestellposition> bp = b.join("bestellpositionen");
		criteriaQuery.where(builder.gt(bp.<Short>get("anzahl"), minMenge))
		             .distinct(true);

		final TypedQuery<Kunde> query = em.createQuery(criteriaQuery);
		final List<Kunde> kunden = query.getResultList();
		return kunden;
	}
}