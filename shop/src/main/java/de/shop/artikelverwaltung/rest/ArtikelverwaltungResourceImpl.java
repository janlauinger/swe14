package de.shop.artikelverwaltung.rest;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

//import java.lang.invoke.MethodHandles;
//import java.net.URI;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.List;
//import java.util.Locale;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
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
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.UriInfo;

//import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.Form;

//import de.shop.artikelverwaltung.dao.ArtikelverwaltungDao.FetchType;
import de.shop.artikelverwaltung.domain.Produkt;
//import de.shop.artikelverwaltung.rest.ProduktList;
//import de.shop.artikelverwaltung.rest.UriHelperProdukt;
import de.shop.artikelverwaltung.service.Produktverwaltung;

//import de.shop.artikelverwaltung.rest.ArtikelverwaltungResource;

import de.shop.util.NotFoundException;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ArtikelverwaltungResourceImpl implements ArtikelverwaltungResource {
//	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String VERSION = "1.0";
	
//	@SuppressWarnings("unused")
//	@PersistenceContext
//	private EntityManager em;
	
	@Inject
	private Produktverwaltung av;
	
	@Inject
	private UriHelperProdukt uriHelperProdukt;

	
	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {
		return VERSION;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Produkt findProdukt(Long id, UriInfo uriInfo) {
		final Produkt produkt = av.findProduktById(id);
		if (produkt == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Produkt gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

//		// Da findProdukt als Transaktion ablaeuft, koennen persistente 
//		// Daten auch nachgeladen werden.
//		// Fetch-Joins sind bei ertraeglichen Performance-Einbussen nicht zwingend notwendig.
//	
//		// URLs innerhalb des gefundenen Produktes anpassen
//		uriHelperProdukt.updateUriProdukt(produkt, uriInfo);
		
		return produkt;
	}


	@Override
	@GET
	@Path("{id:[0-9]+}")
	public
	Produkt findProdukte(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@GET
	@Path("{maxpreis:[0-9]+}/maxpreis")
	public
	List<Produkt> findProdukteByMaxPreis(
			@PathParam("maxpreis") Integer gespreis, @Context UriInfo uriInfo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@GET
	@Path("{id:[0-9]}/Produkt")
	public
	List<Produkt> findProduktByID(@PathParam("id") Long id,
			@Context UriInfo uriInfo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@GET
	@Path("{bez:[0-9]}/Produkt")
	public
	List<Produkt> findProduktByBez(@PathParam("bez") String bez,
			@Context UriInfo uriInfo) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@PUT
	@Consumes({ "application/xml", "text/xml" })
	@Produces
	public
	void updateProdukt(Produkt produkt, @Context UriInfo uriInfo,
			@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		
	}


	@Override
	@POST
	@Consumes({ "application/xml", "text/xml" })
	@Produces
	public
	Response createProdukt(Produkt produkt, @Context UriInfo uriInfo,
			@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Path("form")
	@POST
	@Produces
	public Response createProdukt(@Form ProduktForm produkt,
			@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteProdukt(@PathParam("id") Long produktId) {
		// TODO Auto-generated method stub
		
	}
	

	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public ProduktList findProdukt(String bezeichnung, UriInfo uriInfo) {
//		List<Produkt> produkte = null;
//		if ("".equals(bezeichnung)) {
//			produkte = av.findAllProdukt(FetchType.NUR_PRODUKT, null);
//			if (produkte.isEmpty()) {
//				final String msg = "Keine Produkte vorhanden";
//				throw new NotFoundException(msg);
//			}
//		}
//		else {
//			produkte = av.findProduktByBez(bezeichnung, FetchType.NUR_PRODUKT);
//			if (produkte.isEmpty()) {
//				final String msg = "Kein Produkt gefunden mit Bezeichnung " + bezeichnung;
//				throw new NotFoundException(msg);
////			}
////		}
//		
//		// URLs innerhalb der gefundenen Produkt anpassen
////		for (Produkt produkt : produkte) {
////			uriHelperProdukt.updateUriProdukt(produkt, uriInfo);
////		}
//		
//		// Konvertierung in eigene List-Klasse wg. Wurzelelement
//		final ProduktList produktList = new ProduktList(produkte);
//		
//		return produktList;
//	}	

	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public Response createProdukt(Produkt produkt, UriInfo uriInfo, HttpHeaders headers) {
//		
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//		produkt = av.createProdukt(produkt, locale);
//		LOGGER.tracef("%s", produkt);
//		
//		final URI produktUri = uriHelperProdukt.getUriProdukt(produkt, uriInfo);
//		return Response.created(produktUri).build();
//	}
	
	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public Response createProdukt(ProduktForm produktForm, UriInfo uriInfo, HttpHeaders headers) {
//		Produkt produkt = new Produkt();
//		produkt.setBezeichnung(produktForm.getBezeichnung());
//		produkt.setPreis(produktForm.getPreis());
//
//		
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//		produkt = kv.createProdukt(produkt, locale);
//		LOGGER.tracef("%s", produkt);
//		
//		final URI produktUri = uriHelperProdukt.getUriProdukt(produkt, uriInfo);
//		return Response.created(produktUri).build();	
//	}	
	
	/**
	 * {@inheritDoc}
	 */
//	@Override
//	public void updateProdukt(Produkt produkt, UriInfo uriInfo, HttpHeaders headers) {
//		// Vorhandenes Produkt ermitteln
//		Produkt origProdukt = av.findProduktById(produkt.getProduktId(), FetchType.NUR_PRODUKT);
//		if (origProdukt == null) {
//			// TODO msg passend zu locale
//			final String msg = "Kein Produkt gefunden mit der ID " + produkt.getProduktId();
//			throw new NotFoundException(msg);
//		}
//		LOGGER.tracef("%s", origProdukt);
//	
//		// Daten des vorhandenen Produktes ueberschreiben
//		origProdukt.setValues(produkt);
//		LOGGER.tracef("%s", origProdukt);
//		
//		// Update durchfuehren
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//		produkt = av.updateProdukt(origProdukt, locale);
//		if (produkt == null) {
//			// TODO msg passend zu locale
//			final String msg = "Kein Produkt gefunden mit der ID " + origProdukt.getProduktId();
//			throw new NotFoundException(msg);
//		}
//	}
//	
//	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public void deleteProdukt(Long produktId) {
//		final Produkt produkt = av.findProduktById(produnktId, FetchType.NUR_PRODUKT);
//		av.deleteProdukt(produkt);
//	}
}
