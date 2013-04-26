package de.shop.kundenverwaltung.rest;
import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_NACHNAME_QUERY_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
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
public class KundeResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(1);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(55);
	private static final String NACHNAME_VORHANDEN = "Epp";
	private static final String NACHNAME_NICHT_VORHANDEN = "Buntfix";
	
	private static final String KUNDE_NEU_NACHNAME = "Baumann";
	private static final String KUNDE_NEU_VORNAME = "Hans";
	private static final String KUNDE_NEU_USERNAME = "BaHa";
	private static final String KUNDE_NEU_EMAIL = "abcd65@web.de";
	private static final String KUNDE_NEU_GEBDAT = "1988-01-31";
	private static final String KUNDE_NEU_PLZ = "76676";
	private static final String KUNDE_NEU_ORT = "Graben-Neudorf";
	private static final String KUNDE_NEU_STRASSE = "Baumweg 17";
	private static final String KUNDE_NEU_PWD = "supersicher4";
	private static final Long KUNDE_UPDATE_ID = Long.valueOf(3);
	private static final String KUNDE_UPDATE_EMAIL = "neueEmail@gmx.de";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findKundeById() {
		LOGGER.finer("BEGINN findKundeById");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("idKunde").longValue(), is(kundeId.longValue()));
		}
		
		LOGGER.finer("ENDE findKundeById");
	}
	
	@Test
	public void findKundeByIdNichtVorhanden() {
		LOGGER.finer("BEGINN findKundeByIdNichtVorhanden");
		
		// Given
		final Long kundeId = KUNDE_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE findKundeByIdNichtVorhanden");
	}
	
	@Ignore
	@Test
	public void findKundenByNachnameVorhanden() {
		LOGGER.finer("BEGINN findKundenByNachnameVorhanden");
		
		// Given
		final String nachname = NACHNAME_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
	    	assertThat(jsonArray.size() > 0, is(true));
	    	
	    	final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
	    	for (JsonObject jsonObject : jsonObjectList) {
	    		assertThat(jsonObject.getString("nachname"), is(nachname));
	    	}
		}

		LOGGER.finer("ENDE findKundenByNachnameVorhanden");
	}
	
	@Ignore
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		LOGGER.finer("BEGINN findKundenByNachnameNichtVorhanden");
		
		// Given
		final String nachnameNv = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachnameNv)
                                         .get(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.finer("ENDE findKundenByNachnameNichtVorhanden");
	}
	
	@Test
	public void createKunde() {
		LOGGER.finer("BEGINN createKunde");
		
		// Given
		final String nachname = KUNDE_NEU_NACHNAME;
		final String vorname = KUNDE_NEU_VORNAME;
		final String email = KUNDE_NEU_EMAIL;
		final String geburtsdatum = KUNDE_NEU_GEBDAT;
		final String passwort = KUNDE_NEU_PWD;
		final String strasse = KUNDE_NEU_STRASSE;
		final String plz = KUNDE_NEU_PLZ;
		final String ort = KUNDE_NEU_ORT;
		final String username = KUNDE_NEU_USERNAME;
		final String usernameAdmin = USERNAME_ADMIN;
		final String passwordAdmin = PASSWORD_ADMIN;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
										.add("nachname",nachname)
										.add("vorname",vorname)
										.add("email",email)
										.add("geburtsdatum",geburtsdatum)
										.add("passwort",passwort)
										.add("username", username)
										.add("adresse", getJsonBuilderFactory().createObjectBuilder() 
													.add("strasse",strasse)
													.add("plz",plz)
													.add("ort", ort)
													.build())
										.build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                         .body(jsonObject.toString())
						                         .auth()
						                         .basic(usernameAdmin, passwordAdmin)
		                                         .post(KUNDEN_PATH);
				
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
		
		LOGGER.finer("ENDE createKunde");
	}
	
	@Ignore
	@Test
	public void createKundeEmailKonflikt() {
		LOGGER.finer("BEGINN createKundeEmailKonflikt");
		
		// Given
		final String nachname = KUNDE_NEU_NACHNAME + "u";
		final String vorname = KUNDE_NEU_VORNAME + "u";
		final String email = KUNDE_NEU_EMAIL;
		final String geburtsdatum = KUNDE_NEU_GEBDAT;
		final String passwort = KUNDE_NEU_PWD + "u";
		final String strasse = KUNDE_NEU_STRASSE + "u";
		final String plz = KUNDE_NEU_PLZ + "u";
		final String ort = KUNDE_NEU_ORT + "u";
		final String username = KUNDE_NEU_USERNAME;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
										.add("nachname",nachname)
										.add("vorname",vorname)
										.add("email",email)
										.add("username", username)
										.add("geburtsdatum",geburtsdatum)
										.add("passwort", passwort)
										.add("adresse", getJsonBuilderFactory().createObjectBuilder() 
													.add("strasse",strasse)
													.add("plz",plz)
													.add("ort", ort)
													.build())
										.build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
						                         .body(jsonObject.toString())
		                                         .post(KUNDEN_PATH);
				
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		// TODO einzelne Meldungen durch Bean Validation ueberpruefen
		assertThat(response.asString().isEmpty(), is(false));
		
		LOGGER.finer("ENDE createKundeEmailKonflikt");
	}
	
	@Ignore
	@Test
	public void updateKunde() {
		LOGGER.finer("BEGINN updateKunde");
		
		//Given
		final Long kundeId = KUNDE_UPDATE_ID;
		final String neueEmail = KUNDE_UPDATE_EMAIL;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
						                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
		                                   .get(KUNDEN_ID_PATH);
				
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("idKunde").longValue(), is(kundeId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("email".equals(k)) {
    			job.add("email", neueEmail);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .put(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
				
		LOGGER.finer("ENDE updateKunde");
	}
}
