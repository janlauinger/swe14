package de.shop.bestellverwaltung.rest;


import java.net.URI;
//import java.util.List;

//import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

//import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;


@Singleton
@Log
public class UriHelperBestellung {
//	@Inject
//	private UriHelperKunde uriHelperKunde;
	
//	@Inject
//	private UriHelperArtikel uriHelperArtikel;
	
//	public void updateUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
//		// URL fuer Kunde setzen
//		final Kunde kunde = bestellung.getKunde();
//		if (kunde != null) {
//			
//			final URI kundeUri = uriHelperKunde.getUriKunde(bestellung.getKunde(), uriInfo);
//			bestellung.setKundeUri(kundeUri);
//		}
//		
//		// URLs fuer Artikel in den Bestellpositionen setzen
//		final List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
//		if (bestellpositionen != null && !bestellpositionen.isEmpty()) {
//			for (Bestellposition bp : bestellpositionen) {
//				final URI artikelUri = uriHelperArtikel.getUriArtikel(bp.getArtikel(), uriInfo);
//				bp.setArtikelUri(artikelUri);
//			}
//		}	
//	}

	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellverwaltungResource.class)
		                             .path(BestellverwaltungResource.class, "findBestellung");
		final URI uri = ub.build(bestellung.getBestellId());
		return uri;
	}
}
