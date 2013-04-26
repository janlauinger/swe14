package de.shop.artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.dao.ArtikelDao.OrderType;
import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.Artikelverwaltung;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;


@Path("/artikel")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class ArtikelverwaltungResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private Artikelverwaltung av;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	public List<Artikel> findArtikelByBezeichnung(@QueryParam("bezeichnung") @DefaultValue("") String bezeichnung) {
		 List<Artikel> artikel = null;
	
		if("".equals(bezeichnung)){
			artikel = av.findAllArtikel(OrderType.ID);
		}
		else{
			artikel = av.findArtikelByBezeichnung(bezeichnung);
			if (artikel.isEmpty()) {
				final String msg = "Kein Artikel gefunden mit Bezeichnung " + bezeichnung;
				throw new NotFoundException(msg);
			}
		}
		return artikel;
	}
		
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelById(@PathParam("id") Long id) {
		final Artikel artikel = av.findArtikelById(id);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		return artikel;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		System.out.println("testEintritt: " + artikel.toString() + " ende Artikel. ERROR");
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		artikel = av.createArtikel(artikel, locale);
		LOGGER.log(FINEST, "Artikel: {0}", artikel);
		System.out.println("test: " + artikel.toString() + " ende Artikel. ERROR");
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		// Vorhandenen Artikel ermitteln
		Artikel origArtikel = av.findArtikelById(artikel.getIdArtikel());
		if (origArtikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getIdArtikel();
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Artikel vorher: %s", origArtikel);
	
		// Daten des vorhandenen Artikel ueberschreiben
		origArtikel.setValues(artikel);
		LOGGER.log(FINEST, "Artikel nachher: %s", origArtikel);
		
		// Update durchfuehren
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		artikel = av.updateArtikel(origArtikel, locale);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + origArtikel.getIdArtikel();
			throw new NotFoundException(msg);
		}
	}
}