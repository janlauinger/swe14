package de.shop.artikelverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.shop.artikelverwaltung.domain.Produkt;

@XmlRootElement(name = "produkte")
public class ProduktList {
	
	@XmlElementRef
	private List<Produkt> produkte;
	
	public ProduktList() {
		super();
	}
	
	public ProduktList(List<Produkt> produkte) {
		this.produkte = produkte;
	}
	
	public void setProdukte(List<Produkt> produkte) {
		this.produkte = produkte;
	}
	
	public List<Produkt> getProdukte() {
		return produkte;
	}

}
