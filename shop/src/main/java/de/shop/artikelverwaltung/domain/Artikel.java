package de.shop.artikelverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.util.IdGroup;

/**
 * The persistent class for the artikel database table.
 * 
 */
// Definieren der NamedQueries die für die Tests benötigt werden
@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name = Artikel.FIND_ARTIKEL,
				query = "SELECT		 a"
						+ " FROM	 Artikel a"),
	@NamedQuery(name = Artikel.FIND_ARTIKEL_ORDER_BY_ID,
			   	query = "SELECT a"
			   			+ " FROM Artikel a"
			   			+ " ORDER BY a.id ASC"),
	@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_ID,
				query = "SELECT		 a"
						+ " FROM	 Artikel a"
						+ " WHERE	 a.id = :" + Artikel.PARAM_ID),
	@NamedQuery(name  = Artikel.FIND_VERFUEGBARE_ARTIKEL,
            	query = "SELECT      a"
            	        + " FROM     Artikel a"
						+ " WHERE    a.anzahl > 0"
            	        + " AND 	 a.aktiv = 1"
                        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_BEZ,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
						+ " AND 	 a.aktiv = 1"
			 	        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_MAX_PREIS,
				query = "SELECT      a"
						+ " FROM     Artikel a"
						+ " WHERE    a.preis < :" + Artikel.PARAM_PREIS
						+ " ORDER BY a.id ASC")
})

public class Artikel implements Serializable {
	private static final long serialVersionUID = -6802969680175916248L;
	
	// Namen für die NamedQueries und ggf. Parameter
	private static final String PREFIX = "Artikel.";
	public static final String FIND_ARTIKEL = PREFIX + "findArtikel";
	public static final String FIND_ARTIKEL_ORDER_BY_ID = PREFIX + "findArtikelOrdnerById";
	public static final String FIND_ARTIKEL_BY_ID = PREFIX + "findArtikelById";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX + "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_BEZ = PREFIX + "findArtikelByBez";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX + "findArtikelByMaxPreis";
	public static final String PARAM_ID = "id";
	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_PREIS = "preis";
	
	// Festlegen der minimalen/maximalen Länge von Attributen
	private static final int BEZEICHNUNG_LENGTH_MAX = 64;
	private static final int BESCHREIBUNG_LENGTH_MAX = 64;
	private static final int MARKE_LENGTH_MAX = 64;
	private static final int BILDLINK_LENGTH_MAX = 64;
	private static final int PREIS_LENGTH_MAX = 10;
	private static final int MIN_PREIS = 1;
	private static final int EINKAUFSPREIS_LENGTH_MAX = 10;
	private static final int MIN_EINKAUFSPREIS = 1;
	private static final int GROESSE_LENGTH_MAX = 3;
	private static final int FARBE_LENGTH_MAX = 20;
	private static final int TYP_LENGTH_MAX = 32;
	private static final int ANZAHL_LENGTH_MAX = 5;
	private static final int MIN_ANZAHL = 1;
	private static final int AKTIV_LENGTH_MAX = 5;
	private static final int ERSTE_VERSION = 1;

	// Enums für Jahreszeit und Geschlecht
	public enum GeschlechtTyp { 
		m,
		w 
	}
	
	public enum JahreszeitTyp { 
		f,
		s, 
		h, 
		w 
	}
	
	// Definieren der Attribute und deren Annotationen
	@Id
	@GeneratedValue
	@Column(name = "idArtikel", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Column(length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String bezeichnung;
	
	@Column(length = BESCHREIBUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.beschreibung.notNull}")
	@Size(max = BESCHREIBUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.beschreibung.length}")
	private String beschreibung;
	
	@Column(length = MARKE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.marke.notNull}")
	@Size(max = MARKE_LENGTH_MAX, message = "{artikelverwaltung.artikel.marke.length}")
	private String marke;
	
	@Column(length = BILDLINK_LENGTH_MAX, nullable = true)
	@Size(max = BILDLINK_LENGTH_MAX, message = "{artikelverwaltung.artikel.bildlink.length}")
	private String bildlink;
	
	@Column(precision = 8, scale = 2, length = PREIS_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.preis.notNull}")
	@Min(value = MIN_PREIS, message = "{artikelverwaltung.artikel.preis.min}")
	private BigDecimal preis;
	
	@Column(precision = 8, scale = 2, length = EINKAUFSPREIS_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.einkaufspreis.notNull}")
	@Min(value = MIN_EINKAUFSPREIS, message = "{artikelverwaltung.artikel.einkaufspreis.min}")
	private BigDecimal einkaufspreis;
	
	@Column(length = GROESSE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.groesse.notNull}")
	@Size(max = GROESSE_LENGTH_MAX, message = "{artikelverwaltung.artikel.groesse.length}")
	private String groesse;
	
	@Column(length = FARBE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.farbe.notNull}")
	@Size(max = FARBE_LENGTH_MAX, message = "{artikelverwaltung.artikel.farbe.length}")
	private String farbe;
	
	@Transient
	private List<GeschlechtTyp> geschlecht;
	@Column(name = "geschlecht")
	@Enumerated(EnumType.STRING)
	private GeschlechtTyp geschlechtDB;
	
	@Transient
	private List<JahreszeitTyp> jahreszeit;
	@Column(name = "jahreszeit", nullable = false)
	@Enumerated(EnumType.STRING)
	private JahreszeitTyp jahreszeitDB;
	
	@Column(length = TYP_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.typ.notNull}")
	@Size(max = TYP_LENGTH_MAX, message = "{artikelverwaltung.artikel.typ.length}")
	private String typ;
	
	@Column(length = ANZAHL_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.anzahl.notNull}")
	@Min(value = MIN_ANZAHL, message = "{artikelverwaltung.artikel.anzahl.min}")
	private int anzahl;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;
	
	@Column(length = AKTIV_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.aktiv.notNull}")
	private Boolean aktiv = true;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	public Artikel() {
		super();
	}

	public Artikel(String bezeichnung, BigDecimal preis) {
		super();
		this.bezeichnung = bezeichnung;
		this.preis = preis;
	}
	
	public void setValues(Artikel a) {
		bezeichnung = a.bezeichnung;
		beschreibung = a.beschreibung;
		marke = a.marke;
		bildlink = a.bildlink;
		preis = a.preis;
		einkaufspreis = a.einkaufspreis;
		groesse = a.groesse;
		farbe = a.groesse;
		geschlechtDB = a.geschlechtDB;
		jahreszeitDB = a.jahreszeitDB;
		typ = a.typ;
		anzahl = a.anzahl;
		aktiv = a.aktiv;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	// Getter und Setter der Attribute
	public Long getIdArtikel() {
		return id;
	}

	public void setIdArtikel(Long idArtikel) {
		this.id = idArtikel;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getMarke() {
		return marke;
	}

	public void setMarke(String marke) {
		this.marke = marke;
	}
	
	public String getBildlink() {
		return bildlink;
	}

	public void setBildlink(String bildlink) {
		this.bildlink = bildlink;
	}
	
	public BigDecimal getPreis() {
		return preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}
	
	public BigDecimal getEinkaufspreis() {
		return einkaufspreis;
	}

	public void setEinkaufspreis(BigDecimal einkaufspreis) {
		this.einkaufspreis = einkaufspreis;
	}
	
	public String getGroesse() {
		return groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}
	
	public String getFarbe() {
		return farbe;
	}

	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}
	
	public GeschlechtTyp getGeschlecht() {
		return geschlechtDB;
	}
	
	public void setGeschlecht(GeschlechtTyp geschlecht) {
		this.geschlechtDB = geschlecht;
	}
	
	public JahreszeitTyp getJahreszeit() {
		return jahreszeitDB;
	}
	
	public void setJahreszeit(JahreszeitTyp jahreszeit) {
		this.jahreszeitDB = jahreszeit;
	}
	
	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}
	
	public int getAnzahl() {
		return anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}
	
	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}
	
	public Boolean getAktiv() {
		return aktiv;
	}

	public void setAktiv(Boolean aktiv) {
		this.aktiv = aktiv;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	// Überschreiben von ToString(), HashCode() und Equals()
	@Override
	public String toString() {
		return "Artikel [idArtikel=" + id + ", beschreibung="
				+ beschreibung + ", bezeichnung=" + bezeichnung + ", marke="
				+ marke + ", preis=" + preis + ", aktualisiert=" + aktualisiert
				+ ", erzeugt=" + erzeugt + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beschreibung == null) ? 0 : beschreibung.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
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
		Artikel other = (Artikel) obj;
		if (beschreibung == null) {
			if (other.beschreibung != null)
				return false;
		} else if (!beschreibung.equals(other.beschreibung))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		} else if (!preis.equals(other.preis))
			return false;
		if (version != other.version)
			return false;
		return true;
	}


}