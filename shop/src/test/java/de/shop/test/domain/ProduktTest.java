package de.shop.test.domain;

import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.junit.Assert.fail;
import static org.junit.Assert.assertThat;

//import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

//import org.hamcrest.Matcher;
//import org.hibernate.validator.constraints.impl.MaxValidatorForNumber;
import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Ignore;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.test.util.AbstractTest;
import de.shop.artikelverwaltung.domain.Produkt;

@RunWith(Arquillian.class)
public class ProduktTest extends AbstractTest {
	

	private static final Long ID_VORHANDEN = Long.valueOf(1);
	private static final String BEZ_VORHANDEN = "Hammer";
	private static final Integer PREIS = Integer.valueOf(1);
	private static final String BEZEICHNUNG_NEU = "Test";
	private static final Integer PREIS_NEU = 10000;
	
	//FIND_PRODUKT_BY_ID passt
//	@Ignore("passt")
	@Test
	public void findProduktById() {
		final Long id = ID_VORHANDEN;
		
		final TypedQuery<Produkt> query = em.createNamedQuery(
				Produkt.FIND_PRODUKT_BY_ID, Produkt.class);
		query.setParameter("produktId", id);
		final List<Produkt> produkt = query.getResultList();
		
		assertThat(produkt.isEmpty(), is(false));
		for (Produkt p:produkt) {
			assertThat(p.getProduktId(), is(id));
		}
	}
	
	//FIND_PRODUKT_BY_BEZ
	@SuppressWarnings("unchecked")
	@Ignore("noch nicht getestet")
	@Test
	public void findProduktByBez() {
		final String bez = BEZ_VORHANDEN;
		
		final TypedQuery<Produkt> query = (TypedQuery<Produkt>) em.createNativeQuery(
				Produkt.FIND_PRODUKT_BY_BEZ, Produkt.class);
		query.setParameter("bezeichnung", bez);
		final List<Produkt> produkt = query.getResultList();
		
		assertThat(produkt.isEmpty(), is(false));
		for (Produkt p:produkt) {
			assertThat(p.getBezeichnung(), is(bez));
		}
	}
	
//	FIND_PRODUKT_BY_MAX_PREIS
	@SuppressWarnings("unchecked")
	@Ignore("noch nicht getestet")
	@Test
	public void findProduktByMaxPreis() {
		final Integer p = PREIS;
		
		final TypedQuery<Produkt> query = (TypedQuery<Produkt>) em.createNativeQuery(
				Produkt.FIND_PRODUKT_MAX_PREIS, Produkt.class);
		query.setParameter("preis", p);
		final List<Produkt> produkt = query.getResultList();
		
		assertThat(produkt.isEmpty(), is(false));
		for (Produkt pr:produkt) {
			assertThat(pr.getPreis(), is(p));	
		}	
	}
//	test passt
	@Test
	public void createProdukt() {
		// Given
		Produkt produkt = new Produkt();
		produkt.setBezeichnung(BEZEICHNUNG_NEU);
		produkt.setPreis(PREIS_NEU);
		
		// When
		try {
			em.persist(produkt);         // abspeichern einschl. Adresse
		}
		catch (ConstraintViolationException e) {
			// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}
		
		// Then
		
		// Den abgespeicherten Kunden ueber eine Named Query ermitteln
		final TypedQuery<Produkt> query = em.createNamedQuery(Produkt.FIND_PRODUKT_BY_BEZ,
				                                                    Produkt.class);
		query.setParameter(Produkt.PARAM_BEZEICHNUNG, BEZEICHNUNG_NEU);
		final List<Produkt> produkte = query.getResultList();
		
		// Ueberpruefung des ausgelesenen Objekts
		assertThat(produkte.size(), is(1));
		assertThat(produkt.getProduktId().longValue() > 0, is(true));
		assertThat(produkt.getPreis(), is(PREIS_NEU));
		assertThat(produkt.getBezeichnung(), is(BEZEICHNUNG_NEU));
	}
	
}