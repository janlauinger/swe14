package de.shop.kundenverwaltung.rest;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Log;


@Singleton
@Log
public class UriHelperKunde {
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(KundenverwaltungResource.class)
		                             .path(KundenverwaltungResource.class, "findKunde");
		final URI kundeUri = ub.build(kunde.getKundenId());
		return kundeUri;
	}
	
	
//	public void updateUriKunde(Kunde kunde, UriInfo uriInfo) {
//		// URL fuer Bestellungen setzen
//		final UriBuilder ub = uriInfo.getBaseUriBuilder()
//                                     .path(KundenverwaltungResource.class)
//                                     .path(KundenverwaltungResource.class, "findBestellungenByKundeId");
//		final URI bestellungenUri = ub.build(kunde.getKundenId());
//		kunde.setBestellungenUri(bestellungenUri);
//	}
}
