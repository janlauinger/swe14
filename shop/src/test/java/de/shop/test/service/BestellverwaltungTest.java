package de.shop.test.service;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Collection;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.artikelverwaltung.service.Produktverwaltung;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.Bestellverwaltung;
import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.test.util.AbstractTest;

@RunWith(Arquillian.class)
public class BestellverwaltungTest extends AbstractTest {
	private static final Integer PREIS_VORHANDEN = 40;
	private static final String NEUER_STATUS = "VERSENDET";
	private static final Long BESTELLUNG_VORHANDEN = Long.valueOf(1);
//	private static final String KUNDE_VORHANDEN = "Homer.Jay.Simpson@gelb.com";
	private static final Long KUNDE_VORHANDEN = Long.valueOf(1);
	private static final Long PRODUKT_1 = Long.valueOf(1);
	private static final Long PRODUKT_2 = Long.valueOf(2);
	private static final Long PRODUKT_3 = Long.valueOf(3);
	private static final int ANZAHL = 30;
	private static final int ANZAHL2 = 3;
	
	@Inject
	private Bestellverwaltung bv;
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private Produktverwaltung pv;
	
	
	
	@Test
	public void findBestellungByPreis() {
		
		final Integer preis = PREIS_VORHANDEN;
		
		final Collection<Bestellung> bestellungen = bv.findBestellungByPreis(preis);
		
		assertThat(bestellungen, is(notNullValue()));
		assertThat(bestellungen.isEmpty(), is(false));
		
		for (Bestellung b : bestellungen) {
			assertThat(b.getGesamtpreis() >= PREIS_VORHANDEN, is(true));
		}
		
	}
	
	@Test
	public void findBestellungById() {
		final Long id = BESTELLUNG_VORHANDEN;
		
		final Bestellung bestellung = bv.findBestellungById(id);
		assertThat(bestellung, is(notNullValue()));
		assertThat(bestellung.getBestellId(), is(id));
	}
	
	
	@Test
	public void updateBestellung() {
		
		final String status = NEUER_STATUS;
		final Long beid = BESTELLUNG_VORHANDEN;
		
		Bestellung bestellung = bv.findBestellungById(beid);
		bestellung.setStatus(status);
		
		bestellung = bv.updateBestellung(bestellung, LOCALE);
		
		assertThat(bestellung.getStatus(), is(status));
		bestellung = bv.findBestellungById(beid);
		assertThat(bestellung.getStatus(), is(status));
	}
	
	@Test
	public void createBestellung() {
		final Long kundeId = KUNDE_VORHANDEN;
		final Long produkt1 = PRODUKT_1;
		final Long produkt2 = PRODUKT_2;
		final Long produkt3 = PRODUKT_3;
		final int artikelAnzahl = ANZAHL;
		
		Bestellung bestellung = new Bestellung();
		
		Produkt produkt = pv.findProduktById(produkt1);
		Bestellposition bpos = new Bestellposition(produkt);
		bpos.setAnzahl(artikelAnzahl);
		bestellung.addBestellposition(bpos);
		
		produkt = pv.findProduktById(produkt2);
		bpos = new Bestellposition(produkt);
		bpos.setAnzahl(artikelAnzahl);
		bestellung.addBestellposition(bpos);
		
		produkt = pv.findProduktById(produkt3);
		bpos = new Bestellposition(produkt);
		bpos.setAnzahl(artikelAnzahl);
		bestellung.addBestellposition(bpos);
		
		Kunde kunde = kv.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN);
		
		bestellung = bv.createBestellung(bestellung, kunde, LOCALE);
		
		assertThat(bestellung.getBestellpositionen().size(), is(ANZAHL2));
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
				assertThat(bp.getProdukt().getProduktId(), anyOf(is(produkt1), is(produkt2), is(produkt3)));
		}
		
		kunde = bestellung.getKunde();
		assertThat(kunde.getKundenId(), is(kundeId));
		
	}
	

}
