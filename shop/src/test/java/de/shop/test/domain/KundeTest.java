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
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.test.util.AbstractTest;

@RunWith(Arquillian.class)
public class KundeTest extends AbstractTest {
	private static final String NAME_VORHANDEN = "Simpson";
	private static final String NAME_NICHT_VORHANDEN = "Nicht";
	private static final Long ID_VORHANDEN = Long.valueOf(2);
	private static final Date ERZEUGT = new Date();
	private static final String GESCHLECHT = "w";
	
	private static final String USERNAME_NEU = "TTest";
	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final String PLZ_NEU = "11111";
	private static final String ORT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg 2";

	@Test
	public void findTest() {
	assertThat(true, is(true));
	}
	
	@Test
	public void findKundeByIdVorhanden() {
		final Long id = ID_VORHANDEN;

		final Kunde kunde = em.find(Kunde.class, id);

		assertThat(kunde.getKundenId(), is(id));
	}
	
	@Test
	public void findKundeByNameVorhanden() {
		final String name = NAME_VORHANDEN;

		final TypedQuery<Kunde> query = em.createNamedQuery(Kunde.FIND_KUNDE_BY_NAME,
				                                                    Kunde.class);
		query.setParameter("name", name);
		final List<Kunde> kunden = query.getResultList();
		
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getName(), is(name));
		}
	}
	
	@Test
	public void findKundeByNachnameNichtVorhanden() {
		final String name = NAME_NICHT_VORHANDEN;
		
		final TypedQuery<Kunde> query = em.createNamedQuery(Kunde.FIND_KUNDE_BY_NAME,
				                                                    Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NAME, name);
		final List<Kunde> kunden = query.getResultList();

		assertThat(kunden.isEmpty(), is(true));
	}
	
	@Test
	public void createKunde() {
		Kunde kunde = new Kunde();
		kunde.setName(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setRegistrierdatum(ERZEUGT);
		kunde.setGeschlecht(GESCHLECHT);
		kunde.setNewsletter(true);
		kunde.setUsername(USERNAME_NEU);
		kunde.setPasswort("passwort");
		kunde.setKundenart("p");
		kunde.setErzeugt(ERZEUGT);
		kunde.setAktualisiert(ERZEUGT);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setOrt(ORT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		try {
			em.persist(kunde);         
		}
		catch (ConstraintViolationException e) {
			final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			
			throw new RuntimeException(e);
		}

		final TypedQuery<Kunde> query = em.createNamedQuery(Kunde.FIND_KUNDE_BY_NAME,
				                                                    Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NAME, NACHNAME_NEU);

		assertThat(kunde.getName(), is(NACHNAME_NEU));
	}
}