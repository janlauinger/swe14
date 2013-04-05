package de.shop.kundenverwaltung.rest;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.util.NotFoundException;


@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class KundenverwaltungResourceImpl implements KundenverwaltungResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
//	private static final String VERSION = "1.0";
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private Kundenverwaltung kv;
	
//	@Inject
//	private Bestellverwaltung bv;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
//	@Inject
//	private UriHelperBestellung uriHelperBestellung;

	@Override
	public Kunde findKunde(Long id, UriInfo uriInfo) {
		final Kunde kunde = kv.findKundeById(id, FetchType.NUR_KUNDE);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}
		return kunde;
	}
//final Kunde kunde = kv.findKundeById(id, FetchType.NUR_KUNDE, null);
	@Override
	public Response createKunde(Kunde kunde, UriInfo uriInfo, HttpHeaders headers) {
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		kunde = kv.createKunde(kunde, locale);
		LOGGER.tracef("%s", kunde);
		
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}
	
	
	@Override
	public void updateKunde(Kunde kunde, UriInfo uriInfo, HttpHeaders headers) {
		Kunde origKunde = kv.findKundeById(kunde.getKundenId(), FetchType.NUR_KUNDE);
		if (origKunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + kunde.getKundenId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("%s", origKunde);
		origKunde.setValues(kunde);
		LOGGER.tracef("%s", origKunde);

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		kunde = kv.updateKunde(origKunde, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + origKunde.getKundenId();
			throw new NotFoundException(msg);
		}
	}
//	Kunde origKunde = kv.findKundeById(kunde.getKundenId(), FetchType.NUR_KUNDE, null);
	@Override
	public void deleteKunde(Long kundenId) {
		final Kunde kunde = kv.findKundeById(kundenId, FetchType.NUR_KUNDE);
		kv.deleteKunde(kunde);
	}
//		final Kunde kunde = kv.findKundeById(kundenId, FetchType.NUR_KUNDE, null);
}
