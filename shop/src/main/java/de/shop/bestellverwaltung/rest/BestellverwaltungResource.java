package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
//import javax.ws.rs.POST;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.kundenverwaltung.domain.Kunde;

@Path("/bestellungen")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
public interface BestellverwaltungResource {

	/**
	 * Mit der URL /bestellungen/{id} eine Bestellung ermitteln
	 * @param id ID der Bestellung
	 * @return Objekt mit Bestelldaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[0-9]+}")
	Bestellung findBestellung(@PathParam("id") Long id, @Context UriInfo uriInfo);
	
	@GET
	@Path("/mitGesamtpreisUeber/{gesamtpreis:[0-9]+}")
	List<Bestellung> findBestellungenByPreis(@PathParam("gesamtpreis") Integer gespreis, @Context UriInfo uriInfo);
	
	@GET
	@Path("/mitKundenId/{id:[0-9]}")
	List<Bestellung> findBestellungenByKundeId(@PathParam("id") Long id, @Context UriInfo uriInfo);
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	void updateBestellung(Bestellung bestellung, @Context UriInfo uriInfo,
			@Context HttpHeaders headers);
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createBestellung(Bestellung bestellung, @Context UriInfo uriInfo, @Context HttpHeaders headers);
}
