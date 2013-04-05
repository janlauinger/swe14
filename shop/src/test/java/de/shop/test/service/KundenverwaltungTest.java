package de.shop.test.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
//import static org.junit.matchers.JUnitMatchers.both;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeValidationException;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.test.util.AbstractTest;


@RunWith(Arquillian.class)
public class KundenverwaltungTest extends AbstractTest {
	private static final String KUNDE_NAME_VORHANDEN = "Simpson";
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(1);
	private static final String KUNDE_NAME_NICHT_VORHANDEN = "Beta";
	private static final int TAG = 31;
	private static final int MONAT = 0;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String PLZ_VORHANDEN = "76133";
	private static final String PLZ_NICHT_VORHANDEN = "111";
	private static final String KUNDE_NEU_NAME = "Flanders";
	private static final String KUNDE_NEU_VORNAME = "Ned";
	private static final String KUNDE_NEU_EMAIL = "ned.flanders@hska.de";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final String PASSWORD_NEU = "rod";
	private static final String KUNDE_NEU_KUNDENART = "p";
	private static final String KUNDE_NEU_GESCHLECHT = "m";
	private static final String USERNAME_NEU = "TTest";

	@Inject
	private Kundenverwaltung kv;
	
	@SuppressWarnings("unchecked")
	@Test
	public void findKundenMitNameVorhanden() {
		final String name = KUNDE_NAME_VORHANDEN;

		final Collection<Kunde> kunden = kv.findKundenByName(name, FetchType.NUR_KUNDE);
		
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getName(), is(name));
//			assertThat(k.getName(), both(is(notNullValue())).and(is(name)));
			assertThat(k.getName(), allOf(is(notNullValue()), is(name)));
		}
	}

	@Test
	public void findKundenMitNameNichtVorhanden() {
		final String name = KUNDE_NAME_NICHT_VORHANDEN;

		final List<Kunde> kunden = kv.findKundenByName(name, FetchType.NUR_KUNDE);
		
		assertThat(kunden.isEmpty(), is(true));
	}
	
	@Test
	public void findKundenMitPLZVorhanden() {
		final String plz = PLZ_VORHANDEN;

		final Collection<Kunde> kunden = kv.findKundenByPLZ(plz);

		assertThat(kunden, is(notNullValue()));
		
		for (Kunde k : kunden) {
			assertThat(k.getAdresse(), is(notNullValue()));
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}
	
	@Test
	public void findKundenMitPLZNichtVorhanden() {
		final String plz = PLZ_NICHT_VORHANDEN;
		
		final List<Kunde> kunden = kv.findKundenByPLZ(plz);
		
		assertThat(kunden.isEmpty(), is(true));	}

//	@Ignore
	@Test
	public void findKundenByNameCriteria() {
		final String name = KUNDE_NAME_VORHANDEN;
		
		final List<Kunde> kunden = kv.findKundenByNameCriteria(name);
		
		for (Kunde k : kunden) {
			assertThat(k.getName(), is(name));
		}
	}
		
	@Test
	public void createKunde() {

		final String name = KUNDE_NEU_NAME;
		final String vorname = KUNDE_NEU_VORNAME;
		final String email = KUNDE_NEU_EMAIL;
		final Date erzeugt = SEIT_VORHANDEN;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String kundenart = KUNDE_NEU_KUNDENART;
		final String geschlecht = KUNDE_NEU_GESCHLECHT;
		final String passwort = PASSWORD_NEU;
		final String username = USERNAME_NEU;
		
		@SuppressWarnings("unused")
		final Collection<Kunde> kundenVorher = kv.findAllKunden(FetchType.NUR_KUNDE, null);

		Kunde neuerKunde = new Kunde();
		neuerKunde.setName(name);
		neuerKunde.setVorname(vorname);
		neuerKunde.setEmail(email);
		neuerKunde.setRegistrierdatum(erzeugt);
		neuerKunde.setGeschlecht(geschlecht);
		neuerKunde.setNewsletter(true);
		neuerKunde.setPasswort(passwort);
		neuerKunde.setKundenart(kundenart);
		neuerKunde.setUsername(username);
		neuerKunde.setErzeugt(erzeugt);
		neuerKunde.setAktualisiert(erzeugt);
		
		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setKunde(neuerKunde);
		neuerKunde.setAdresse(adresse);
		
		neuerKunde = kv.createKunde(neuerKunde, LOCALE);

		assertThat(neuerKunde.getName(), is(name));
		assertThat(neuerKunde.getEmail(), is(email));
		assertThat(neuerKunde.getVorname(), is(vorname));
		assertThat(neuerKunde.getKundenart(), is(kundenart));
		assertThat(neuerKunde.getGeschlecht(), is(geschlecht));
		assertThat(neuerKunde.getNewsletter(), is(true));
		assertThat(neuerKunde.getPasswort(), is(passwort));
	//TODO Prüfen auf findKundenBySeit (Regestrierdatum)
		neuerKunde = (Kunde) neuerKunde;
	}
	
	//TODO createKundeOhneAdresse
	@Ignore
	@Test
	public void createKundeOhneAdresse() {
		
		final String name = KUNDE_NEU_NAME;
		final String vorname = KUNDE_NEU_VORNAME;
		final String email = KUNDE_NEU_EMAIL;
		final Date erzeugt = SEIT_VORHANDEN;
		final String kundenart = KUNDE_NEU_KUNDENART;
		final String geschlecht = KUNDE_NEU_GESCHLECHT;
		final String passwort = PASSWORD_NEU;

		final Kunde neuerKunde = new Kunde();
		neuerKunde.setName(name);
		neuerKunde.setVorname(vorname);
		neuerKunde.setEmail(email);
		neuerKunde.setRegistrierdatum(erzeugt);
		neuerKunde.setGeschlecht(geschlecht);
		neuerKunde.setNewsletter(true);
		neuerKunde.setPasswort(passwort);
		neuerKunde.setKundenart(kundenart);
		neuerKunde.setErzeugt(erzeugt);
		neuerKunde.setAktualisiert(erzeugt);
		
		thrown.expect(KundeValidationException.class);
		thrown.expectMessage("Ungueltiger Kunde:");
		kv.createKunde(neuerKunde, LOCALE);
	} 
	
	@Test
	public void neuerNameFuerKunde() {

		final Long kundeId = KUNDE_ID_VORHANDEN;

		Kunde kunde = kv.findKundeById(kundeId, FetchType.NUR_KUNDE);
		
		final String alterName = kunde.getName();
		final String neuerName = alterName + alterName.charAt(alterName.length() - 1);
		kunde.setName(neuerName);
	
		kunde = kv.updateKunde(kunde, LOCALE);
		
		assertThat(kunde.getName(), is(neuerName));
		kunde = kv.findKundeById(kundeId, FetchType.NUR_KUNDE);
		assertThat(kunde.getName(), is(neuerName));
	}
}
