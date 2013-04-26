package de.shop.artikelverwaltung.dao;

import java.util.Collections;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractDao;
import de.shop.util.Log;

@Log
public class ArtikelDao extends AbstractDao<Artikel, Long> {
	private static final long serialVersionUID = -375419199459985721L;

	public enum OrderType {
		KEINE,
		ID
	}
	
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		/**
		 * SELECT a
		 * FROM   Artikel a
		 * WHERE  a.id = ? OR a.id = ? OR ...
		 */
		final CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		//final Path<String> idPath = a.get(Artikel_.id);   // Metamodel-Klassen funktionieren nicht mit Eclipse
		
		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			// Mind. 2x id, durch OR verknuepft
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = getEntityManager().createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}
	
	public List<Artikel> findAllArtikel(OrderType order) {
		List<Artikel> artikel = null;

		artikel = OrderType.ID.equals(order)
			     ? find(Artikel.FIND_ARTIKEL_ORDER_BY_ID)
				 : find(Artikel.FIND_ARTIKEL);
		
		return artikel;
	}
}
