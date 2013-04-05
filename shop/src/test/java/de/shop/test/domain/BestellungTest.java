package de.shop.test.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.test.util.AbstractTest;



@RunWith(Arquillian.class)
public class BestellungTest extends AbstractTest {
	

	private static final Long KUNDE_VORHANDEN = Long.valueOf(1);
	private static final Long KUNDE_NICHT_VORHANDEN = Long.valueOf(99999);
	private static final String BEZAHLART = "BAR";
	private static final String LIEFERART = "Frei Haus";
	private static final String STATUS = "wird geliefert";
	private static final Date AKTUALLDAT_NEU = new Date();
	private static final int ANZAHL_NEU1 = 400;
	private static final int ANZAHL_NEU2 = 800;
	
	private static final Long PRODUKTID_VORHANDEN1 = Long.valueOf(1);
	private static final Long PRODUKTID_VORHANDEN2 = Long.valueOf(2);
	
	@Test
	public void findBestellungByKunde() {
		final Long id = KUNDE_VORHANDEN;
		
		final TypedQuery<Bestellung> query = em.createNamedQuery(
				Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		query.setParameter("kunden_id", id);
		final List<Bestellung> bestellungen = query.getResultList();
		
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b:bestellungen) {
			assertThat(b.getKunde().getKundenId(), is(id));
		}
	}
	
	@Ignore
	@Test
	public void findBestellungByKundeFalsch() {
		final Long id = KUNDE_NICHT_VORHANDEN;
		final TypedQuery<Bestellung> query = em.createNamedQuery(
				Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		query.setParameter("kunden_id", id);
		//TODO Eine Bestellung suchen mit einem Kunden der nicht vorhanden ist
		
	}
	
	
	@Test
	public void createBestellung() {
		
		int bestellungGesamtpreis = 0;
		
		Kunde k = em.find(Kunde.class, KUNDE_VORHANDEN);
		Bestellung b = new Bestellung();

		
		Produkt p1 = em.find(Produkt.class, PRODUKTID_VORHANDEN1);
		Produkt p2 = em.find(Produkt.class, PRODUKTID_VORHANDEN2);
		
		Bestellposition bpos = new Bestellposition(p1);
		bpos.setAnzahl(ANZAHL_NEU1);
		b.addBestellposition(bpos);
		bpos = new Bestellposition(p2);
		bpos.setAnzahl(ANZAHL_NEU2);
		b.addBestellposition(bpos);
		
		bestellungGesamtpreis = p1.getPreis() * ANZAHL_NEU1 + p2.getPreis() * ANZAHL_NEU2;
		
		b.setGesamtpreis(bestellungGesamtpreis);
		b.setBezahlart(BEZAHLART);
		b.setAktualisiert(AKTUALLDAT_NEU);
		b.setErzeugt(AKTUALLDAT_NEU);
		b.setStatus(STATUS);
		b.setLieferart(LIEFERART);
		//b.setKunde(k);
		
		k.addBestellung(b);
		try {
			em.persist(b);
			
		}
		catch (ConstraintViolationException e) {
			
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.out.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.out.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.out.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
	    		
	    	}
	    	
	    	throw new RuntimeException(e);
			}
		   final TypedQuery<Bestellung> query = 
				   em.createNamedQuery(Bestellung.FIND_BESTELLUNG_BY_PREIS, Bestellung.class);
		    
		    query.setParameter("gesamtpreis", bestellungGesamtpreis);
//		    final Bestellung be = query.getSingleResult(); >>laja1011<<
//		    
//		    assertThat(be.getGesamtpreis(), is(bestellungGesamtpreis));
		}
	
	@Ignore
	@Test
	public void createBestellungOhneBespo() {
		//TODO Bestellung anlegen ohne Bestellposition
	}
	
}
	

