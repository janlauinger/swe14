package de.shop.bestellverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.BESTELLPOSITIONEN_ID_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.BESTELLUNGEN_PATH;
import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)

public class BestellungResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(3001);
	private static final Long BESTELLUNG_ID_NICHT_VORHANDEN = Long.valueOf(3999);
	private static final Long KUNDE_ID = Long.valueOf(1);
	private static final Long ARTIKEL_ID1 = Long.valueOf(2000);
	private static final Long ARTIKEL_ID2 = Long.valueOf(2001);
	private static final Integer ANZAHL1 = Integer.valueOf(2);
	private static final Integer ANZAHL2 = Integer.valueOf(3);
	private static final BigDecimal EINZELPREIS1 = BigDecimal.valueOf(89.99);
	private static final BigDecimal EINZELPREIS2 = BigDecimal.valueOf(19.99);	
	private static final Long BESTELLUNG_UPDATE_ID = Long.valueOf(3002);
	private static final Boolean BESTELLUNG_AKTIV = false;
		
	@Test
	public void findBestellungById() {
		 LOGGER.finer("BEGINN findBestellungById");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("idBestellung").longValue(), is(bestellungId.longValue()));
		}
		
		 LOGGER.finer("ENDE findBestellungById");
	}
	
	@Test
	public void findBestellungByIdNichtVorhanden() {
		LOGGER.finer("BEGINN findBestellungByIdNichtVorhanden");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE findBestellungByIdNichtVorhanden");
	}
	
	@Ignore
	@Test
	public void findBestellpositionenByBestellungId() {
		LOGGER.finer("BEGINN findBestellpositionenByBestellungId");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .queryParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
										 .get(BESTELLPOSITIONEN_ID_PATH);
		
		// Then
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
	    	assertThat(jsonArray.size() > 0, is(true));
	    	
//	    	final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
//	    	for (JsonObject jsonObject : jsonObjectList) {
//	    		assertThat(jsonObject.getString("name"), is(nachname));
//	    	}
		}
	}
	
	@Test
	public void createBestellung() {
		LOGGER.finer("BEGINN createBestellung");
		
		// Given
		final Long kundeId = KUNDE_ID;
		final Long artikelId1 = ARTIKEL_ID1;
		final Long artikelId2 = ARTIKEL_ID2;
		final Integer anzahl1 = ANZAHL1;
		final Integer anzahl2 = ANZAHL2;
		final BigDecimal einzelpreis1 = EINZELPREIS1;
		final BigDecimal einzelpreis2 = EINZELPREIS2;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
										.add("kundeUri",KUNDEN_URI + "/" + kundeId)
										.add("bestellpositionen", getJsonBuilderFactory().createArrayBuilder()
												.add(getJsonBuilderFactory().createObjectBuilder()
													.add("einzelpreis",einzelpreis1)
													.add("anzahl",anzahl1)
													.add("artikelUri", ARTIKEL_URI + "/" + artikelId1))
												.add(getJsonBuilderFactory().createObjectBuilder()
													.add("einzelpreis",einzelpreis2)
													.add("anzahl",anzahl2)
													.add("artikelUri", ARTIKEL_URI + "/" +artikelId2)))
										.build();
		
		System.out.println("JsonBestellungsObjekt: " + jsonObject.toString());
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                         .body(jsonObject.toString())
		                                         .post(BESTELLUNGEN_PATH);
				
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		
		LOGGER.finer("ENDE createBestellung");
	}
	
	@Test
	public void updateBestellung() {
	LOGGER.finer("BEGINN updateBestellung");

	//Given
	final Long bestellungId = BESTELLUNG_UPDATE_ID;
	final Boolean aktiv = BESTELLUNG_AKTIV;

	// When
	Response response = given().header(ACCEPT, APPLICATION_JSON)
	.pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
	.get(BESTELLUNGEN_ID_PATH);

	JsonObject jsonObject;
	try (final JsonReader jsonReader =
	getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
	jsonObject = jsonReader.readObject();
	}
	assertThat(jsonObject.getJsonNumber("idBestellung").longValue(), is(bestellungId.longValue()));

	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt bauen
	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	final Set<String> keys = jsonObject.keySet();
	for (String k : keys) {
	if ("aktiv".equals(k)) {
	job.add("aktiv", aktiv);
	}
	else {
	job.add(k, jsonObject.get(k));
	}
	}
	jsonObject = job.build();

	response = given().contentType(APPLICATION_JSON)
	.body(jsonObject.toString())
	.put(BESTELLUNGEN_PATH);

	// Then
	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));

	LOGGER.finer("ENDE updateBestellung");
	}
}
