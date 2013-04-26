package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.artikelverwaltung.domain.Artikel;


@Entity
@Table(name = "bestellposition")
@NamedQueries({
		@NamedQuery(name = Bestellposition.FIND_ALL_BESTELLPOSITION,
		query = "SELECT		 b"
				+ " FROM	 Bestellposition b"),
		@NamedQuery(name = Bestellposition.FIND_BESTELLPOSITION_BY_ID,
		query = "SELECT		 b"
				+ " FROM	 Bestellposition b"
				+ " WHERE	 b.id = :" + Bestellposition.PARAM_ID),
		@NamedQuery(name = Bestellposition.FIND_MAX_GESAMTPREIS,
		query = "SELECT MAX(b.anzahl*b.einzelpreis)"
				+ " FROM Bestellposition b")
})

public class Bestellposition implements Serializable {

	private static final long serialVersionUID = 9009447922768264413L;
	
	private static final String PREFIX = "Bestellposition.";
	public static final String FIND_ALL_BESTELLPOSITION = PREFIX + "findAlleBestellpositionen";
	public static final String FIND_BESTELLPOSITION_BY_ID = PREFIX + "findBestellpositionById";
	public static final String PARAM_ID = "id";
	public static final String FIND_MAX_GESAMTPREIS = PREFIX + "findMaxGesamtpreis";
	public static final String FIND_BESTELLPOSITION_MAX_GESAMTPREIS = PREFIX  
		+ "findBestellpositionMaxGesamtpreis";
	public static final String FIND_BESTELLPOSITION_BY_MAX_PREIS = PREFIX
		+ "findBestellpositionByMaxPreis";
	public static final String PARAM_MAX_PREIS = "einzelpreis";
	
	private static final int ANZAHL_MIN = 1;
	private static final int ERSTE_VERSION = 1;
	
	@Id
	@GeneratedValue
	@Column(name = "idBestellposition", unique = true, nullable = false, 
			updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long id = KEINE_ID;
	
	@Column(nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.anzahl.notNull}")
	@Min(value = ANZAHL_MIN, message = "{artikelverwaltung.artikel.anzahl.length}")
	private int anzahl;
	
	@Transient
	private URI artikelUri;
	
	@ManyToOne(optional = false)	
	@JoinColumn(name = "artikel_fk", nullable = false)
	@NotNull(message = "{bestellverwaltung.bestellposition.artikel.notNull}")
	@JsonIgnore
	private Artikel artikel;

	@Column(precision = 8, scale = 2, nullable = false)
	private BigDecimal einzelpreis;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(nullable = false)
	private Boolean aktiv = true;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}
	
	public Bestellposition() {
	super();
	}

	public Bestellposition(Artikel artikel) {
		super();
		this.artikel = artikel;
		this.anzahl = 1;
	}
	
	public Bestellposition(Artikel artikel, int anzahl) {
		super();
		this.artikel = artikel;
		this.anzahl = anzahl;
	}
	
	public URI getArtikelUri() {
		return this.artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getAktiv() {
		return this.aktiv;
	}

	public void setAktiv(Boolean aktiv) {
		this.aktiv = aktiv;
	}

	public Date getAktualisiert() {
		return this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	public BigDecimal getEinzelpreis() {
		return this.einzelpreis;
	}

	public void setEinzelpreis(BigDecimal einzelpreis) {
		this.einzelpreis = einzelpreis;
	}

	public Date getErzeugt() {
		return this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Artikel getArtikel() {
		return this.artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Bestellposition [id=" + id + ", anzahl=" + anzahl
				+ ", einzelpreis=" + einzelpreis + ", aktiv=" + aktiv + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aktiv == null) ? 0 : aktiv.hashCode());
		result = prime * result + anzahl;
		result = prime * result
				+ ((einzelpreis == null) ? 0 : einzelpreis.hashCode());
		result = prime * result + version;
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
		if (aktiv == null) {
			if (other.aktiv != null)
				return false;
		} else if (!aktiv.equals(other.aktiv))
			return false;
		if (anzahl != other.anzahl)
			return false;
		if (einzelpreis == null) {
			if (other.einzelpreis != null)
				return false;
		} else if (!einzelpreis.equals(other.einzelpreis))
			return false;
		if (version != other.version)
			return false;
		return true;
	}


}