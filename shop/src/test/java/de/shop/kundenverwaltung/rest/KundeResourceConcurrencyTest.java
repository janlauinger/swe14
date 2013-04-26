package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class KundeResourceConcurrencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long KUNDE_ID_UPDATE = Long.valueOf(1);
	private static final String KUNDE_NEUE_EMAIL = "UneueMail1@web.de";
	private static final String KUNDE_NEUE_EMAIL2 = "UneueMail2@web.de";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN updateUpdate");
		
		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
		final String emailNeu = KUNDE_NEUE_EMAIL;
		final String emailNeu2 = KUNDE_NEUE_EMAIL2;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
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
    		if ("email".equals(k)) {
    			job.add("email", emailNeu2);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, KUNDEN_PATH);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("email".equals(k)) {
    			job.add("email", emailNeu);
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
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.finer("ENDE updateUpdate");
	}
}
