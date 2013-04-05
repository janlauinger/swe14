package de.shop.kundenverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.shop.kundenverwaltung.domain.Kunde;

@XmlRootElement(name = "kunden")
public class KundeList {
//	@XmlElements({
//		@XmlElement(name = "firmenkunde", type = Firmenkunde.class),
//		@XmlElement(name = "privatkunde", type = Privatkunde.class)
//	})
	@XmlElementRef
	private List<Kunde> kunden;

	public KundeList() {
		super();
	}
	
	public KundeList(List<Kunde> kunden) {
		this.kunden = kunden;
	}

	public void setKunden(List<Kunde> kunden) {
		this.kunden = kunden;
	}

	public List<Kunde> getKunden() {
		return kunden;
	}

	@Override
	public String toString() {
		return "KundeList [kunden=" + kunden + "]";
	}
}
