package de.shop.artikelverwaltung.rest;


import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.util.Log;

@Singleton
@Log
public class UriHelperProdukt {
	public URI getUriProdukt(Produkt produkt, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(ArtikelverwaltungResource.class)
		                             .path(ArtikelverwaltungResource.class, "findProdukt");
		final URI produktUri = ub.build(produkt.getProduktId());
		return produktUri;
	}

//	public URI updateUriProdukt(Produkt produkt, UriInfo uriInfo) {
//		// URL fuer Produkt setzen
//		final Produkt produkt = produkt.getProdukt();
//			if (produkt!= null) {
//			
//				final URI produktUri = UriHelperProdukt.getUriProdukt(produkt.getProdukt(), uriInfo);
//				produkt.setProduktUri(produktUri);
//			}	
//	}

}
