package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.util.List;

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

import org.jboss.resteasy.annotations.Form;

import de.shop.artikelverwaltung.domain.Produkt;

@Path("/produkte")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
public interface ArtikelverwaltungResource {
	
	/**
	 * Mit der URL /produkte/{id} eines Produktes ermitteln
	 * @param id ID des Produkts
	 * @return Objekt mit Produktdaten, falls die ID vorhanden ist
	 */
	@GET
	@Path("{id:[0-9]+}")
	Produkt findProdukte(@PathParam("id") Long id, @Context UriInfo uriInfo);
	
	@GET
	@Path("{maxpreis:[0-9]+}/maxpreis")
	List<Produkt> findProdukteByMaxPreis(@PathParam("maxpreis") Integer gespreis, @Context UriInfo uriInfo);
	
	@GET
	@Path("{id:[0-9]}/Produkt")
	List<Produkt> findProduktByID(@PathParam("id") Long id, @Context UriInfo uriInfo);
	
	@GET
	@Path("{bez:[0-9]}/Produkt")
	List <Produkt> findProduktByBez(@PathParam("bez") String bez, @Context UriInfo uriInfo);
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	void updateProdukt(Produkt produkt, @Context UriInfo uriInfo,
			@Context HttpHeaders headers);

	Produkt findProdukt(Long id, UriInfo uriInfo);
	
	/**
	 * Mit der URL /produkt per POST anlegen.
	 * @param produkt neues Produkt
	 * @return Response-Objekt mit URL des neuen Produktes
	 */
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createProdukt(Produkt produkt, @Context UriInfo uriInfo, @Context HttpHeaders headers);
//	Response createProdukt(Produkt produkt, @Context UriInfo uriInfo, @Context HttpHeaders headers);
	
	/**
	 * Mit der URL /produkt/form per POST anlegen wie in einem HTML-Formular.
	 * @param produkt Formular-Objekt mit den Daten des neuen Produktes
	 * @return Response-Objekt mit URL des neuen Produktes
	 */
	@Path("form")
	@POST
//	@Consumes(APPLICATION_FORM_URLENCODED)
	@Produces
	Response createProdukt(@Form ProduktForm produkt, @Context UriInfo uriInfo, @Context HttpHeaders headers);

//	/**
//	 * Mit der URL /produkt Produkte per PUT aktualisieren
//	 * @param produkt zu aktualisierende Daten des Produktes
//	 */
//	@PUT
//	@Consumes({ APPLICATION_XML, TEXT_XML })
//	@Produces
//	void updateProdukt(Produkt produkt, @Context UriInfo uriInfo, @Context HttpHeaders headers);

	/**
	 * Mit der URL /produkt{id} Produkt per DELETE l&ouml;schen
	 * @param produkt_Id des zu l&ouml;schenden Produktes
	 */
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	void deleteProdukt(@PathParam("id") Long produktId);
	
}
