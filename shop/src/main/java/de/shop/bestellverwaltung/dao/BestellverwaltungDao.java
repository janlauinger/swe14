package de.shop.bestellverwaltung.dao;

import static de.shop.util.Dao.QueryParameter.with;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.Dao;

public class BestellverwaltungDao extends Dao<Bestellung, Long> {
	private static final long serialVersionUID = 1L;
	
	public List<Bestellung> findBestellungenByKunde(Long id) {
		
		final List<Bestellung> bestellungen = find(Bestellung.class, Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, 
				                                   with(Bestellung.PARAM_KUNDEID, id).build());
		
		return bestellungen;
	}
	
	public List<Bestellung> findBestellungByPreis(List<Integer> gesamtpreis) {
		if (gesamtpreis == null || gesamtpreis.isEmpty()) {
			return null;
		}
		final EntityManager em = getEntityManager();
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Bestellung> criteriaQuery = builder.createQuery(Bestellung.class);
		final Root<Bestellung> b = criteriaQuery.from(Bestellung.class);
		
		final Path<Integer> gesamtpreisPath = b.get("gesamtpreis");
		final List<Predicate> predList = new ArrayList<>();
		for (Integer gp : gesamtpreis) {
			final Predicate equal = builder.equal(gesamtpreisPath, gp);
			predList.add(equal);
		}
		
		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);
		
		final TypedQuery<Bestellung> query = em.createQuery(criteriaQuery);
		final List<Bestellung> bestellungen = query.getResultList();
		return bestellungen;
		
		
	}

	public Bestellung findBestellungById(Long id) {
		Bestellung bestellung = null;
		bestellung = find(id);
		return bestellung;
	}

}
