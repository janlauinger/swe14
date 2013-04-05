package de.shop.bestellverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.shop.bestellverwaltung.domain.Bestellung;

@XmlRootElement(name = "bestellungen")
public class BestellungList {
	@XmlElementRef
	private List<Bestellung> bestellungen;
	
	public BestellungList() {
		super();
	}
	
	public BestellungList(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen;
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen;
	}
	
	public List<Bestellung> getBestellungen() {
		return bestellungen;
	}
}
