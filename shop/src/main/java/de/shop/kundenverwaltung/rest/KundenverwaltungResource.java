package de.shop.kundenverwaltung.rest;

import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

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

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.kundenverwaltung.dao.KundeDao.FetchType;
import de.shop.kundenverwaltung.dao.KundeDao.OrderType;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class KundenverwaltungResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@GET
	public Collection<Kunde> findAllKunden() {
		Collection<Kunde> kunden = kv.findAllKunden(FetchType.NUR_KUNDE, OrderType.ID);
		return kunden;
	}
	
	@GET
	public Collection<Kunde> findKundenByNachname(@QueryParam("nachname") @DefaultValue("") String nachname) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = kv.findAllKunden(FetchType.NUR_KUNDE, OrderType.ID);
		}
		else {
			kunden = kv.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname " + nachname;
				throw new NotFoundException(msg);
			}
		}
		
		// URIs innerhalb der gefundenen Kunden anpassen
		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}
		return kunden;
	}
	
	@GET
	@Path("{id:[0-9][0-9]*}")
	public Kunde findKundeById(@PathParam("id") Long id) {
		final Kunde kunde = kv.findKundeById(id, FetchType.NUR_KUNDE);
		if (kunde == null) {
			// TODO msg passend zu locale
			// kundenverwaltung.kunde.id.wrongID bereits erstellt
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
	
		// URLs innerhalb des gefundenen Kunden anpassen
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		
		return kunde;
	}
	
	@GET
	@Path("{id:[0-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(@PathParam("id") Long kundeId) {
		final Collection<Bestellung> bestellungen = kv.findBestellungenByKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kunde " + kundeId + " hat noch keine Bestellungen";
			throw new NotFoundException(msg);
		}

		// URLs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			bestellung.setBestellungUri(uriHelperBestellung.getUriBestellung(bestellung, uriInfo));
		}
		
		return bestellungen;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKunde(Kunde kunde,@Context HttpHeaders headers) {
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

		kunde = kv.createKunde(kunde, locale);
		LOGGER.log(FINEST, "Kunde: {0}", kunde);
	
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateKunde(Kunde kunde, @Context HttpHeaders headers) {
		// Vorhandenen Kunden ermitteln
		Kunde origKunde = kv.findKundeById(kunde.getIdKunde(), FetchType.NUR_KUNDE);
		if (origKunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getIdKunde();
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Kunde vorher: %s", origKunde);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.log(FINEST, "Kunde nachher: %s", origKunde);
		
		// Update durchfuehren
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		kunde = kv.updateKunde(origKunde, locale);
		if (kunde == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getIdKunde();
			throw new NotFoundException(msg);
		}
	}
}