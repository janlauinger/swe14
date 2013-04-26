package de.shop.bestellverwaltung.rest;

import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
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

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.artikelverwaltung.service.Artikelverwaltung;
import de.shop.bestellverwaltung.dao.BestellungDao.FetchType;
import de.shop.bestellverwaltung.dao.BestellungDao.OrderType;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.Bestellverwaltung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/bestellungen")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class BestellverwaltungResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private Bestellverwaltung bv;
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private Artikelverwaltung av;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@Context 
	UriInfo uriInfo;
	
	@GET
	public Collection<Bestellung> findAllBestellungen() {
		Collection<Bestellung> bestellungen = bv.findAllBestellungen(FetchType.NUR_BESTELLUNG, OrderType.ID);
		for (Bestellung bestellung : bestellungen) {
			BigDecimal summe = new BigDecimal(0);
			for (Bestellposition bestellposition : bestellung.getBestellpositionen()) {
				summe.add(bestellposition.getEinzelpreis());
			}	
			bestellung.setSumme(summe);
			
			// URLs innerhalb des gefundenen Bestellungen anpassen
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}
		return bestellungen;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bv.findBestellungById(id, FetchType.NUR_BESTELLUNG);
		if (bestellung == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Bestellung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		
		BigDecimal summe = new BigDecimal(0);
		for (Bestellposition bestellposition : bestellung.getBestellpositionen()) {
			summe.add(bestellposition.getEinzelpreis());
		}	
		bestellung.setSumme(summe);
		
		// URLs innerhalb des gefundenen Bestellungen anpassen
		uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		
		return bestellung;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/kunde")
	public Kunde findKundeByBestellungId(@PathParam("id") Long id) {
		Bestellung bestellung = bv.findBestellungById(id, FetchType.MIT_KUNDE);
		Kunde kunde = bestellung.getKunde();
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden fuer Bestellung: " + bestellung.getIdBestellung();
			throw new NotFoundException(msg);
		}
		return kunde;
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}/bestellpositionen")
	public List<Bestellposition> findBestellpositionenByBestellungId(@PathParam("id") Long id) {
		Bestellung bestellung = bv.findBestellungById(id, FetchType.NUR_BESTELLUNG);
		
		return bestellung.getBestellpositionen();
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}/bestellpositionen/{id:[1-9][0-9]*}")
	public Bestellposition findBestellpositionById(@PathParam("id") Long id) {
		Bestellposition bestellposition = bv.findBestellpositionById(id);
		bestellposition.setArtikelUri(uriHelperArtikel.getUriArtikel(bestellposition.getArtikel(), uriInfo));
		return bestellposition;
	}
	
	@POST
	@Consumes(APPLICATION_JSON)
	public Response createBestellung(Bestellung bestellung, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		// Schluessel des Kunden extrahieren
		final String kundeUriStr = bestellung.getKundeUri().toString();
		int startPos = kundeUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = kundeUriStr.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeIdStr, e);
		}
		
		// persistente Artikel ermitteln
		final Collection<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
		final List<Long> artikelIds = new ArrayList<>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {
			final String artikelUriStr = bp.getArtikelUri().toString();
			startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException e) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			// keine einzige gueltige Artikel-ID
			final StringBuilder sb = new StringBuilder("Keine Artikel vorhanden mit den IDs: ");
			for (Bestellposition bp : bestellpositionen) {
				final String artikelUriStr = bp.getArtikelUri().toString();
				startPos = artikelUriStr.lastIndexOf('/') + 1;
				sb.append(artikelUriStr.substring(startPos));
				sb.append(' ');
			}
			throw new NotFoundException(sb.toString());
		}
		
		final List<Artikel> gefundeneArtikel = av.findArtikelByIds(artikelIds);
		if (gefundeneArtikel.isEmpty()) {
			// TODO msg passend zu locale
			throw new NotFoundException("Keine Artikel gefunden mit den IDs " + artikelIds);
		}
		
		// Bestellpositionen haben URIs fuer persistente Artikel.
		// Diese persistenten Artikel wurden in einem DB-Zugriff ermittelt (s.o.)
		// Fuer jede Bestellposition wird der Artikel passend zur Artikel-URL bzw. Artikel-ID gesetzt.
		// Bestellpositionen mit nicht-gefundene Artikel werden eliminiert.
		int i = 0;
		final List<Bestellposition> neueBestellpositionen =
			                        new ArrayList<>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {
			// Artikel-ID der aktuellen Bestellposition (s.o.):
			// artikelIds haben gleiche Reihenfolge wie bestellpositionen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getIdArtikel().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					bp.setArtikel(artikel);
					neueBestellpositionen.add(bp);
					break;					
				}
			}
		}
		bestellung.setBestellpositionen(neueBestellpositionen);
		
		// Kunde mit den vorhandenen ("alten") Bestellungen ermitteln
		Kunde kunde = kv.findKundeById(kundeId, de.shop.kundenverwaltung.dao.KundeDao.FetchType.MIT_BESTELLUNGEN);
		bestellung = bv.createBestellung(bestellung, kunde, locale);
		
		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
		
		final Response response = Response.created(bestellungUri).build();
		return response;
	}
	
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateBestellung(Bestellung bestellung, @Context HttpHeaders headers) {
		// Vorhandene Bestellung ermitteln
		Bestellung origBestellung = bv.findBestellungById(bestellung.getIdBestellung(), FetchType.NUR_BESTELLUNG);
		if (origBestellung == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Bestellung gefunden mit der ID " + bestellung.getIdBestellung();
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Bestellung vorher: %s", origBestellung);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origBestellung.setStorno(bestellung.getStorno());
		origBestellung.setAktiv(bestellung.getAktiv());
		LOGGER.log(FINEST, "Bestellung nachher: %s", origBestellung);
		
		// Update durchfuehren
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		bestellung = bv.updateBestellung(origBestellung, locale);
		if (bestellung == null) {
			// TODO msg passend zu locale
			final String msg = "Keine Bestellung gefunden mit der ID " + origBestellung.getIdBestellung();
			throw new NotFoundException(msg);
		}
	}
	
//  Alter Code von uns -> konnte nicht mit uris umgehen!	
//	@POST
//	@Consumes(APPLICATION_JSON)
//	@Produces
//	public Response createBestellung(Bestellung bestellung, 
//			@Context UriInfo uriInfo, @Context HttpHeaders headers) {
//		
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//	
//		Long kundeId = Long.valueOf(bestellung.getKundeUri().toString());
//		Kunde kunde = kv.findKundeById(kundeId, de.shop.kundenverwaltung.dao.KundeDao.FetchType.MIT_BESTELLUNGEN);
//		
//		if (kunde == null) {
//			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeId);
//		}
//		
//		bestellung.setKunde(kunde);
//		
//		List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
//
//		for (Bestellposition bestellposition : bestellpositionen) {
//			Long artikelId = Long.valueOf(bestellposition.getArtikelUri().toString());
//			Artikel artikel = av.findArtikelById(artikelId);
//			if (artikel == null) {
//				throw new NotFoundException("Kein Artikel vorhanden mit der ID " + artikelId);
//			}
//			bestellposition.setArtikel(artikel);
//			bestellposition.setEinzelpreis(artikel.getPreis());
//		}		
//		bestellung = bv.createBestellung(bestellung, kunde, locale);
//	
//		LOGGER.log(FINEST, "Bestellung: {0}", bestellung);
//		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
//		LOGGER.finest(bestellungUri.toString());
//
//		return Response.created(bestellungUri).build();
//	}
//	
}