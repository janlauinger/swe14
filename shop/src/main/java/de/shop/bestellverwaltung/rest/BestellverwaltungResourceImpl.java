package de.shop.bestellverwaltung.rest;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URI;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.artikelverwaltung.service.Produktverwaltung;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.Bestellverwaltung;
import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Kunde;
//import de.shop.kundenverwaltung.rest.UriHelperKunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.NotFoundException;


@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class BestellverwaltungResourceImpl implements BestellverwaltungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private Bestellverwaltung bv;
	
	@Inject
	private Kundenverwaltung kv;
	
	@Inject
	private Produktverwaltung pv;
	
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
//	@Inject
//	private UriHelperKunde uriHelperKunde;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Bestellung findBestellung(Long id, UriInfo uriInfo) {
		final Bestellung bestellung = bv.findBestellungById(id);
		if (bestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		return bestellung;
	}
	
	@Override
	public List<Bestellung> findBestellungenByPreis(Integer gespreis, UriInfo uriInfo) {
		final List<Bestellung> bestellungen = bv.findBestellungByPreis(gespreis);
		if (bestellungen == null || bestellungen.isEmpty()) {
			final String msg = "Keine Bestellungen gefunden mti dem Gesamtpreis von " + gespreis;
			throw new NotFoundException(msg);
		}
		return bestellungen;
	}
	
	@Override
	public List<Bestellung> findBestellungenByKundeId(Long id, UriInfo uriInfo) {
		final List<Bestellung> bestellungen = bv.findBestellungenByKundeId(id);
		if (bestellungen == null || bestellungen.isEmpty()) {
			final String msg = "Keine Bestellungen mit der Kundenid " + id + " gefunden";
			throw new NotFoundException(msg);
		}
		return bestellungen;
	}
	
	
	@Override
	public void updateBestellung(Bestellung bestellung, UriInfo uriInfo, HttpHeaders headers) {
		Bestellung  orgBestellung = bv.findBestellungById(bestellung.getBestellId());
		if (orgBestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + bestellung.getBestellId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("%s", orgBestellung);
		orgBestellung.setValues(bestellung);
		LOGGER.tracef("%s", orgBestellung);
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		bestellung = bv.updateBestellung(orgBestellung, locale);
		if (bestellung == null) {
			final String msg = "Keine Bestellung gefunden mit der ID " + orgBestellung.getBestellId();
			throw new NotFoundException(msg);
		}
	}

	
	@Override
	public Response createBestellung(Bestellung bestellung, UriInfo uriInfo, HttpHeaders headers) {
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
		
		final Kunde kunde = kv.findKundeById(kundeId, FetchType.NUR_KUNDE, null);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID " + kundeId);
		}
		
		List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
		List<Long> produktIds = new ArrayList<>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {
			final String produktUriStr = bp.getProduktUri().toString();
			startPos = produktUriStr.lastIndexOf('/') + 1;
			final String produktIdStr = produktUriStr.substring(startPos);
			Long produktId = null;
			try {
				produktId = Long.valueOf(produktIdStr);
			}
			catch (NumberFormatException e) {
				continue;
			}
			produktIds.add(produktId);
		}
		
		if (produktIds.isEmpty()) {
			final StringBuilder sb = new StringBuilder("Keine Produkte vorhanden mit den IDs: ");
			for (Bestellposition bp : bestellpositionen) {
				final String produktUriStr = bp.getProduktUri().toString();
				startPos = produktUriStr.lastIndexOf('/') + 1;
				sb.append(produktUriStr.substring(startPos));
				sb.append(" ");
			}
			throw new NotFoundException(sb.toString());
		}
		int gesamtpreis = 0;
		List<Produkt> gefundeneProdukte = new ArrayList<>();
		for (Long pro: produktIds) {
			Produkt pr = pv.findProduktById(pro);
			gefundeneProdukte.add(pr);
		}
		
		
		int i = 0;
		final List<Bestellposition> neueBestellpositionen = new ArrayList<>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {
			final long produktId = produktIds.get(i++);
			
			for (Produkt produkt : gefundeneProdukte) {
				if (produkt.getProduktId().longValue() == produktId) {
					bp.setProdukte(produkt);
					neueBestellpositionen.add(bp);
					break;					
				}
			}
		}
		for (Bestellposition bp: neueBestellpositionen) {
			gesamtpreis = gesamtpreis + bp.getAnzahl() * bp.getProdukt().getPreis();
		}
		bestellung.setBestellpositionen(neueBestellpositionen);
		bestellung.setGesamtpreis(gesamtpreis);

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		bestellung = bv.createBestellung(bestellung, kunde, locale);

		final URI bestellungUri = uriHelperBestellung.getUriBestellung(bestellung, uriInfo);
		final Response response = Response.created(bestellungUri).build();
		LOGGER.trace(bestellungUri);
		
		return response;
	}
}
