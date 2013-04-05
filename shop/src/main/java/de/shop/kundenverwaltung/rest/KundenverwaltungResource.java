package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Formatted;

import de.shop.kundenverwaltung.domain.Kunde;


@Path("/kunden")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
public interface KundenverwaltungResource {

	@GET
	@Path("{id:[0-9]+}")
	@Formatted
	Kunde findKunde(@PathParam("id") Long id, @Context UriInfo uriInfo);

	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers);

	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	void updateKunde(Kunde kunde, @Context UriInfo uriInfo, @Context HttpHeaders headers);

	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	void deleteKunde(@PathParam("id") Long kundenId);


}
