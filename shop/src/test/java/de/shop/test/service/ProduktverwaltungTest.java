package de.shop.test.service;

//import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.artikelverwaltung.service.Produktverwaltung;
import de.shop.artikelverwaltung.dao.ArtikelverwaltungDao.FetchType;
import de.shop.test.util.AbstractTest;

@RunWith(Arquillian.class)
public class ProduktverwaltungTest extends AbstractTest {
	private static final Integer PREIS_VORHANDEN = 40;
	private static final Long PRODUKT_VORHANDEN = Long.valueOf(1);
//	private static final String PRODUKT_BEZ_VORHANDEN = "Hammer";
	private static final String PRODUKT_BEZ_NICHT_VORHANDEN = "Pinsel";
	private static final String PRODUKT_NEU_BEZ = "Schraubendreher";
	private static final Integer PRODUKT_NEU_PREIS = 15;
	private static final Long PRODUKT_ID_VORHANDEN = Long.valueOf(1);
	
	@Inject
	private Produktverwaltung av;
	
//	passt
	@Test
	public void findProduktByPreis() {
		
		final Integer preis = PREIS_VORHANDEN;
		
		final Collection<Produkt> produkte = av.findProduktByPreis(preis);
		
		assertThat(produkte, is(notNullValue()));
		assertThat(produkte.isEmpty(), is(false));
		
		for (Produkt p : produkte) {
			assertThat(p.getPreis() < PREIS_VORHANDEN, is(true));
		}
		
	}
	//passt
	@Test
	public void findProduktById() {
		final Long produktId = PRODUKT_VORHANDEN;
		
		final Produkt produkt = av.findProduktById(produktId);
		assertThat(produkt, is(notNullValue()));
		assertThat(produkt.getProduktId(), is(produktId));
	}
	
//	passt nicht
//	@Test
//	public void findProduktByBezCriteria() {
//		final String bez = PRODUKT_BEZ_VORHANDEN;
//		
//		final List<Produkt> produkte = av.findProduktByBezCriteria(bez);
//		
//		for (Produkt p : produkte) {
//			assertThat(p.getBezeichnung(), is(bez));
//		}
//	}
	
//	passt
	@Test
	public void findKundenMitBezNichtVorhanden() {
		final String bezeichnung = PRODUKT_BEZ_NICHT_VORHANDEN;

		final List<Produkt> produkt = av.findProduktByBez(bezeichnung, FetchType.NUR_PRODUKT);
		
		assertThat(produkt.isEmpty(), is(true));
	}
	
//	passt
	@Test
	public void createProdukt() {

		final String bezeichnung = PRODUKT_NEU_BEZ;
		final Integer preis = PRODUKT_NEU_PREIS;

//		@SuppressWarnings("unused")
//		final Collection<Produkt> produkteVorher = av.findAllProdukt(FetchType.NUR_PRODUKT, null);

		Produkt neuesProdukt = new Produkt();
		neuesProdukt.setBezeichnung(bezeichnung);
		neuesProdukt.setPreis(preis);
		
		neuesProdukt = av.createProdukt(neuesProdukt, LOCALE);

		assertThat(neuesProdukt.getBezeichnung(), is(bezeichnung));
		assertThat(neuesProdukt.getPreis(), is(preis));

		neuesProdukt = (Produkt) neuesProdukt;
	}
	
//	passt
	@Test
	public void neueBezFuerProdukt() {

		final Long produktId = PRODUKT_ID_VORHANDEN;
//		, FetchType.NUR_PRODUKTE
		Produkt produkt = av.findProduktById(produktId);
		
		final String alteBez = produkt.getBezeichnung();
		final String neueBez = alteBez + alteBez.charAt(alteBez.length() - 1);
		produkt.setBezeichnung(neueBez);
	
		produkt = av.updateProdukt(produkt, LOCALE);
		
		assertThat(produkt.getBezeichnung(), is(neueBez));
//		, FetchType.NUR_PRODUKTE
		produkt = av.findProduktById(produktId);
		assertThat(produkt.getBezeichnung(), is(neueBez));
	}
	
//	passt
	@Test
	public void updateProdukt() {
		
		final Long prid = PRODUKT_VORHANDEN;
		
		Produkt produkt = av.findProduktById(prid);
		
		produkt = av.updateProdukt(produkt, LOCALE);
		
		produkt = av.findProduktById(prid);
	}
	
//	@Test
//	public void findProduktByPreis() {
//		
//		final Integer preis = PREIS_VORHANDEN;
//		
//		final Collection<Produkt> produkte = av.findProduktByPreis(preis);
//		
//		assertThat(produkte, is(notNullValue()));
//		assertThat(produkte.isEmpty(), is(false));
//		
//		for (Produkt p : produkte) {
//			assertThat(p.getPreis() < PREIS_VORHANDEN, is(true));
//		}
//		
//	}
}
