package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Produkt;
//import javax.xml.bind.annotation.XmlElementWrapper;
//import java.util.List;


/**
 * The persistent class for the bestellposition database table.
 * 
 */
@Entity
@XmlRootElement
@XmlAccessorType(FIELD)
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "position_id")
	@XmlAttribute
	private Long positionId = KEINE_ID;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert = null;

	@Column(name = "anzahl", nullable = false)
	@XmlElement
	private int anzahl;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt = null;

	//bi-directional many-to-one association to Bestellung
//	@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestell_fk", insertable = false, updatable = false, nullable = false)
	@XmlTransient
	private Bestellung bestellung;

	//bi-directional many-to-one association to Produkt
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produkt_fk", nullable = false) //von laja1011 hinzugefÃ¼gt
	@NotNull(message = "{bestellverwaltung.bestellposition.anzahl.min}")
//	@XmlElement(required = true)
	@XmlTransient
	private Produkt produkt;
	
	@Transient
	@XmlElement(name = "produkt", required = true)
	private URI produktUri;

    public Bestellposition() {
		super();
	}

	public Bestellposition(Produkt produkt) {
    	super();
    	this.produkt = produkt;
    }

	public Long getPositionId() {
		return this.positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}
	
//    @SuppressWarnings("unused")
    @PrePersist
    private void prePersist() {
    	erzeugt = new Date();
    	aktualisiert = new Date();
    }
	
//	@SuppressWarnings("unused")
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) erzeugt.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date)
				aktualisiert.clone();
	}

	public Integer getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(Integer anzahl) {
		this.anzahl = anzahl;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Bestellung getBestellung() {
		return this.bestellung;
	}

	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}
	
	public Produkt getProdukt() {
		return this.produkt;
	}

	public void setProdukte(Produkt produkt) {
		this.produkt = produkt;
	}

	public URI getProduktUri() {
		return produktUri;
	}

	public void setProduktUri(URI produktUri) {
		this.produktUri = produktUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aktualisiert == null) ? 0 : aktualisiert.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bestellposition other = (Bestellposition) obj;
		if (aktualisiert == null) {
			if (other.aktualisiert != null)
				return false;
		} 
		else if (!aktualisiert.equals(other.aktualisiert))
			return false;
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		} 
		else if (!erzeugt.equals(other.erzeugt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellposition [ aktualisiert="
				+ aktualisiert + ", anzahl=" + anzahl + ", erzeugt=" + erzeugt + "]";
	}
	
}