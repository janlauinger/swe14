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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

import de.shop.artikelverwaltung.domain.Artikel.GeschlechtTyp;
import de.shop.artikelverwaltung.domain.Artikel.JahreszeitTyp;
import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurrencyTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(2001);
	private static final String NEUE_BESCHREIBUNG = "Blub";
	private static final String NEUE_BESCHREIBUNG_2 = "Blub2";
	
	//@Ignore
	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
    	final String neueBeschreibung = NEUE_BESCHREIBUNG;
    	final String neueBeschreibung2 = NEUE_BESCHREIBUNG_2;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

    	// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("beschreibung".equals(k)) {
    			job.add("beschreibung", neueBeschreibung2);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ARTIKEL_PATH);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
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
		                 // .auth()
		                 // .basic(username, password)
		                  .put(ARTIKEL_PATH);
    	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.finer("ENDE");
	}
}