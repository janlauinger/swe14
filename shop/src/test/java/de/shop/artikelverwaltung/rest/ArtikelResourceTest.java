package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_BEZEICHNUNG_QUERY_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.artikelverwaltung.domain.Artikel.GeschlechtTyp;
import de.shop.artikelverwaltung.domain.Artikel.JahreszeitTyp;
import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(2001);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(3001);
	private static final String BEZEICHNUNG_VORHANDEN = "T-Shirt";
	private static final String BEZEICHNUNG_NICHT_VORHANDEN = "Mütze";
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(2000);
	private static final String NEUE_BESCHREIBUNG = "Blub";
	
	// Setzen der Daten für die Create Anweisungen
	private static final String BEZEICHNUNG_NEU = "Handschuhe";
	private static final String BESCHREIBUNG_NEU = "Handschuhe mit weichem Innenfutter für den Winter";
	private static final String MARKE_NEU = "Esprit";
	private static final BigDecimal PREIS_NEU =  new BigDecimal(34.99);
	private static final BigDecimal PREIS_NEU_KONFLIKT =  new BigDecimal(0.99);
	private static final BigDecimal EINKAUFSPREIS_NEU = new BigDecimal(15.99);
	private static final String GROESSE_NEU = "M";
	private static final String FARBE_NEU = "schwarz";
	private static final GeschlechtTyp GESCHLECHT_NEU = GeschlechtTyp.m;
	private static final JahreszeitTyp JAHRESZEIT_NEU = JahreszeitTyp.s;
	private static final String TYP_NEU = "Winterbekleidung";
	private static final int ANZAHL_NEU = 100;
	
	
	@Test
	public void findArtikelById() {
		 LOGGER.finer("BEGINN findArtikelById");
		
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .get(ARTIKEL_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("idArtikel").longValue(), is(artikelId.longValue()));
		}
		
		 LOGGER.finer("ENDE findArtikelById");
	}
	
	
	@Test
	public void findArtikelByIdNichtVorhanden() {
		LOGGER.finer("BEGINN findArtikelByIdNichtVorhanden");
		
		// Given
		final Long kundeId = ARTIKEL_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, kundeId)
                                         .get(ARTIKEL_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE findArtikelByIdNichtVorhanden");
	}
	
	
	@Test
	public void findArtikelByBezeichnungVorhanden() {
		LOGGER.finer("BEGINN findArtikelByBezeichnungVorhanden");
		
		// Given
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(ARTIKEL_BEZEICHNUNG_QUERY_PARAM, bezeichnung)
                                         .get(ARTIKEL_PATH);
		
		// Then
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
	    	assertThat(jsonArray.size() > 0, is(true));
	    	
	    	final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
	    	for (JsonObject jsonObject : jsonObjectList) {
	    		assertThat(jsonObject.getString("bezeichnung"), is(bezeichnung));
	    	}
		}

		LOGGER.finer("ENDE findArtikelByBezeichnungVorhanden");
	}
	
	
	@Test
	public void findArtikelByBezeichnungNichtVorhanden() {
		LOGGER.finer("BEGINN findArtikelByBezeichnungNichtVorhanden");
		
		// Given
		final String bezeichnung = BEZEICHNUNG_NICHT_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(ARTIKEL_BEZEICHNUNG_QUERY_PARAM, bezeichnung)
                                         .get(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.finer("ENDE findArtikelByBezeichnungNichtVorhanden");
	}
	
	
	@Test
	public void createArtikel() {
		LOGGER.finer("BEGINN createArtikel");
		
		// Zuweisungen für die create Anweisungen
		final String bezeichnung = BEZEICHNUNG_NEU;
		final String beschreibung = BESCHREIBUNG_NEU;
		final String marke = MARKE_NEU;
		final BigDecimal preis = PREIS_NEU;
		final BigDecimal einkaufspreis = EINKAUFSPREIS_NEU;
		final String groesse = GROESSE_NEU;
		final String farbe = FARBE_NEU;
		final String geschlecht = GESCHLECHT_NEU.toString();
		final String jahreszeit = JAHRESZEIT_NEU.toString();
		final String typ = TYP_NEU;
		final int anzahl = ANZAHL_NEU;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				.add("bezeichnung",bezeichnung)
				.add("beschreibung",beschreibung)
				.add("marke",marke)
				.add("preis",preis)
				.add("einkaufspreis",einkaufspreis)
				.add("groesse",groesse)
				.add("farbe",farbe)
				.add("geschlecht", geschlecht)
				.add("jahreszeit", jahreszeit)
				.add("typ",typ)
				.add("anzahl",anzahl)
				.build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                         .body(jsonObject.toString())
		                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		
		LOGGER.finer("ENDE createArtikel");
	}
	
	@Test
	public void createArtikelConflictPreis() {
		LOGGER.finer("BEGINN createArtikelConflictPreis");
		
		// Zuweisungen für die create Anweisungen
		final String bezeichnung = BEZEICHNUNG_NEU;
		final String beschreibung = BESCHREIBUNG_NEU;
		final String marke = MARKE_NEU;
		final BigDecimal preis = PREIS_NEU_KONFLIKT;
		final BigDecimal einkaufspreis = EINKAUFSPREIS_NEU;
		final String groesse = GROESSE_NEU;
		final String farbe = FARBE_NEU;
		final String geschlecht = GESCHLECHT_NEU.toString();
		final String jahreszeit = JAHRESZEIT_NEU.toString();
		final String typ = TYP_NEU;
		final int anzahl = ANZAHL_NEU;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				.add("bezeichnung",bezeichnung)
				.add("beschreibung",beschreibung)
				.add("marke",marke)
				.add("preis",preis)
				.add("einkaufspreis",einkaufspreis)
				.add("groesse",groesse)
				.add("farbe",farbe)
				.add("geschlecht", geschlecht)
				.add("jahreszeit", jahreszeit)
				.add("typ",typ)
				.add("anzahl",anzahl)
				.build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                         .body(jsonObject.toString())
		                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		assertThat(response.asString().isEmpty(), is(false));
		
		LOGGER.finer("ENDE createArtikelConflictPreis");
	}
	
	@Test
	public void updateArtikel() {
		LOGGER.finer("BEGINN updateArtikel");
		
		// Given
		final Long id = ARTIKEL_ID_UPDATE;
		final String neueBeschreibung = NEUE_BESCHREIBUNG;

		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, id)
                                   .get(ARTIKEL_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("idArtikel").longValue(), is(id.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("beschreibung".equals(k)) {
    			job.add("beschreibung", neueBeschreibung);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
   	}
}
