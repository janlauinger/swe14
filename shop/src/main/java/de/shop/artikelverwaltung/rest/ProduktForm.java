package de.shop.artikelverwaltung.rest;

import javax.ws.rs.FormParam;

public class ProduktForm {
	
	@FormParam("bezeichnung")
	private String bezeichnung;
	
	@FormParam("preis")
	private Integer preis;
	
	@Override
	public String toString() {
		return "ProduktForm [bezeichnung=" + bezeichnung + ", preis=" + preis + "]";
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public Integer getPreis() {
		return preis;
	}

	public void setPreis(Integer preis) {
		this.preis = preis;
	}

}
