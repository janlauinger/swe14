package de.shop.bestellverwaltung.service;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static java.net.HttpURLConnection.HTTP_CONFLICT;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
@ApplicationScoped
public class BestellungValidationExceptionMapper implements ExceptionMapper<BestellungValidationException> {
	@Override
	public Response toResponse(BestellungValidationException e) {
		final String msg = e.getMessage();
		final Response response = Response.status(HTTP_CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(msg)
		                                  .build();
		return response;
	}

}