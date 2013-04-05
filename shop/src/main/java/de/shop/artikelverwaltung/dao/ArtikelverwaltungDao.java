package de.shop.artikelverwaltung.dao;

import static de.shop.util.Dao.QueryParameter.with;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.shop.artikelverwaltung.domain.Produkt;
//import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
//import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Dao;

public class ArtikelverwaltungDao extends Dao<Produkt, Long> {
	
private static final long serialVersionUID = 1L;

	public enum FetchType {
		NUR_PRODUKTE, NUR_PRODUKT
	}

	public enum Order {
		KEINE,
		ID
	}
	
	public List<Produkt> findProduktByBez(String bez) {
		List<Produkt> produkt = null;
				produkt = find(Produkt.FIND_PRODUKT_BY_BEZ,
        	                  with(Produkt.FIND_PRODUKT_BY_BEZ, bez).build());
		return produkt;
	}
	
	public List<Produkt> findProduktByMaxPreis(List<Integer> preis) {
		if (preis == null || preis.isEmpty()) {
			return null;
		}
		final EntityManager em = getEntityManager();
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Produkt> criteriaQuery = builder.createQuery(Produkt.class);
		final Root<Produkt> b = criteriaQuery.from(Produkt.class);
		
		final Path<Integer> maxpreisPath = b.get("maxpreis");
		final List<Predicate> predList = new ArrayList<>();
		for (Integer gp : preis) {
			final Predicate equal = builder.equal(maxpreisPath, gp);
			predList.add(equal);
		}
		
		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);
		
		final TypedQuery<Produkt> query = em.createQuery(criteriaQuery);
		final List<Produkt> produkte = query.getResultList();
		return produkte;
			
	}
	
	public Produkt findProduktById(Long id, FetchType fetch) {
		Produkt produkt = null;
		if (fetch == null || FetchType.NUR_PRODUKT.equals(fetch)) {
			produkt = find(id);
		}
		return produkt;
	}
	
	public List<Produkt> findAllProdukt(FetchType fetch, Order order) {
		List<Produkt> produkte = null;
		
		if (fetch == null || FetchType.NUR_PRODUKT.equals(fetch)) {
			if (Order.ID.equals(order)) {
				produkte = find(Produkt.class, Produkt.FIND_PRODUKT_ORDER_BY_ID);
			}
			else {
				produkte = find(Produkt.class, Produkt.FIND_PRODUKT);
			}
		}
		return produkte;
		
	}
	// TODO von dao
	public Produkt finde(Class<Produkt> clazz, String bezeichnung) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	public List<Produkt> findProduktByBez(String bez) {
//		final CriteriaBuilder builder = em.getCriteriaBuilder();
//		final CriteriaQuery<Produkt> criteriaQuery = builder.createQuery(Produkt.class);
//		final Root<Produkt> p = criteriaQuery.from(Produkt.class);
//
//		final Path<String> bezPath = p.get("bez");
//		final Predicate pred = builder.equal(bezPath, bez);
//		criteriaQuery.where(pred);
//
//		final TypedQuery<Produkt> query = em.createQuery(criteriaQuery);
//		final List<Produkt> produkte = query.getResultList();
//		return produkte;
//	}
//	
//	public List<Produkt> findProduktByMaxPreis(List<Integer> preis) {
//		if (preis == null || preis.isEmpty()) {
//			return null;
//		}
//		
//		final CriteriaBuilder builder = em.getCriteriaBuilder();
//		final CriteriaQuery<Produkt> criteriaQuery = builder.createQuery(Produkt.class);
//		final Root<Produkt> b = criteriaQuery.from(Produkt.class);
//		
//		final Path<Integer> maxpreisPath = b.get("maxpreis");
//		final List<Predicate> predList = new ArrayList<>();
//		for (Integer gp : preis) {
//			final Predicate equal = builder.equal(maxpreisPath, gp);
//			predList.add(equal);
//		}
//		
//		final Predicate[] predArray = new Predicate[predList.size()];
//		final Predicate pred = builder.or(predList.toArray(predArray));
//		criteriaQuery.where(pred).distinct(true);
//		
//		final TypedQuery<Produkt> query = em.createQuery(criteriaQuery);
//		final List<Produkt> produkte = query.getResultList();
//		return produkte;
//			
//	}
//
	public List<Produkt> findProdukteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		final EntityManager em = getEntityManager();
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Produkt> criteriaQuery = builder.createQuery(Produkt.class);
		final Root<Produkt> a = criteriaQuery.from(Produkt.class);

		final Path<Long> idPath = a.get("produktId");
				
		Predicate pred = null;
		if (ids.size() == 1) {
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Produkt> query = em.createQuery(criteriaQuery);

		final List<Produkt> produkt = query.getResultList();
		return produkt;
	}
}
