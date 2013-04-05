package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.MIN_ID;
//import static de.shop.util.Constants.ERSTE_VERSION;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
//import javax.persistence.PreUpdate;
//import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.logging.Logger;

import de.shop.util.IdGroup;
//import static de.shop.util.Constants.KEINE_ID;
//import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;

//import de.shop.kundenverwaltung.domain.*;

/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
//@Table(name = "adresse")
@XmlAccessorType(FIELD)
public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());


	public static final int PLZ_LENGTH_MAX = 5;
	public static final int ORT_LENGTH_MIN = 2;
	public static final int ORT_LENGTH_MAX = 32;
	public static final int STRASSE_LENGTH_MIN = 2;
	public static final int STRASSE_LENGTH_MAX = 32;
	public static final int HAUSNR_LENGTH_MAX = 4;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "adresse_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private long adressId;
	
//	@Version
////	@XmlTransient
//	private int version = ERSTE_VERSION;
	@Version
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date aktualisiert = null;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@XmlTransient
	private Date erzeugt = null;

	@Column(length = ORT_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.ort.notNull}")
	@Size(min = ORT_LENGTH_MIN, max = ORT_LENGTH_MAX, message = "{kundenverwaltung.adresse.ort.length}")
	@XmlElement(required = true)
	private String ort;

	@Column(length = PLZ_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Pattern(regexp = "\\d{5}", message = "{kundenverwaltung.adresse.plz}")
	@XmlElement(required = true)
	private String plz;

	@Column(length = STRASSE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = STRASSE_LENGTH_MIN, max = STRASSE_LENGTH_MAX, message = "{kundenverwaltung.adresse.strasse.length}")
	@XmlElement(required = true)
	private String strasse;
	
	@SuppressWarnings("unused")
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PostPersist
	@SuppressWarnings("unused")
	private void postPersist() {
		LOGGER.tracef("Neue Adresse mit ID=%d", adressId);
	}
	
//	@SuppressWarnings("unused")
//	@PreUpdate
//	private void preUpdate() {
//		aktualisiert = new Date();
//	}

	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.kunde.notNull}")
	@XmlTransient
	private Kunde kunde;
	
	public Adresse() {
		super();
	}
	
	public Adresse(String plz, String ort, String strasse) {
		super();
		this.plz = plz;
		this.ort = ort;
		this.strasse = strasse;
	}

	public Long getAdressId() {
		return this.adressId;
	}

	public void setAdressId(Long adressId) {
		this.adressId = adressId;
	}
	
//	public int getVersion() {
//		return version;
//	}
//	
//	public void setVersion(int version) {
//		this.version=version;
//	}
	
	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		Adresse other = (Adresse) obj;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} 
		else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		}
		else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		}
		else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [adressId=" + adressId + ", aktualisiert="
				+ aktualisiert + ", erzeugt=" + erzeugt + ", ort=" + ort
				+ ", plz=" + plz + ", strasse=" + strasse + "]";
	}
	
}