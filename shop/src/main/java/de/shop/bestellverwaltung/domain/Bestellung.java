package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;


@Entity
@Table(name = "bestellung")
@NamedQueries({
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNG_BY_ID,
            	query = "SELECT b"
                        + " FROM     Bestellung b"
						+ " WHERE    b.idBestellung LIKE :" + Bestellung.PARAM_ID_BESTELLUNG),
	@NamedQuery(name = Bestellung.FIND_ALL_BESTELLUNGEN,
				query = "SELECT b"
						+ " FROM Bestellung b"),
	@NamedQuery(name = Bestellung.FIND_ALL_BESTELLUNGEN_ORDER_BY_ID,
				query = "SELECT b"
						+ " FROM Bestellung b"
						+ " ORDER BY b.id"),
	@NamedQuery(name = Bestellung.FIND_ALL_BESTELLUNGEN_FETCH_KUNDE,
				query = "SELECT DISTINCT b"
						+ " FROM Bestellung b"
						+ " LEFT JOIN FETCH b.kunde"),
	@NamedQuery(name = Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_KUNDE,
				query = "SELECT  DISTINCT b"
						+ " FROM Bestellung b LEFT JOIN FETCH b.kunde"
						+ " WHERE b.idBestellung LIKE :" + Bestellung.PARAM_ID_BESTELLUNG)
})

public class Bestellung implements Serializable {
	
	private static final long serialVersionUID = -535070455702712931L;
	
	public static final int ID_LENGTH_MAX = 20;
	public static final int AKTIV_LENGTH_MAX = 3;
	public static final int IDX_LENGTH_MAX = 5;
	private static final int ERSTE_VERSION = 1;
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_ALL_BESTELLUNGEN = PREFIX + "findAllBestellungen";
	public static final String FIND_ALL_BESTELLUNGEN_ORDER_BY_ID = PREFIX + "findAllBestellungenOrderById";
	public static final String FIND_ALL_BESTELLUNGEN_FETCH_KUNDE = PREFIX + "findAllBestellungenFetchKunde";
	public static final String FIND_BESTELLUNG_BY_ID_FETCH_KUNDE = PREFIX + "findBestellungByIdFetchKunde";
	public static final String FIND_BESTELLUNG_BY_ID = PREFIX + "findBestellungenByID";
	public static final String PARAM_ID_BESTELLUNG = "idBestellung";

	@Id
	@GeneratedValue
	@Column(length = ID_LENGTH_MAX, nullable = false, unique = true, updatable = false)
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	private Long idBestellung = KEINE_ID;	
	
	@Transient
	private URI bestellungUri;
	
	@Transient
	private URI kundeUri;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
	@NotNull(message = "{bestellverwaltung.bestellung.kunde.notNull}", groups = PreExistingGroup.class)
	@JsonIgnore
	private Kunde kunde;

	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@NotEmpty(message = "{bestellverwaltung.bestellung.bestellpositionen.notEmpty}")
	@Valid
	private List<Bestellposition> bestellpositionen;
	
	@Transient
	@Column(precision = 8, scale = 2)
	private BigDecimal summe;
	
	@Column(nullable = false)
	private Boolean storno = false;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(name = "aktiv")
	private Boolean aktiv = true;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@PrePersist
	private void prePersist() {	
		erzeugt = new Date();
		aktualisiert = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {	
		aktualisiert = new Date();
	}

	public Long getIdBestellung() {
		return this.idBestellung;
	}

	public void setIdBestellung(Long idBestellung) {
		this.idBestellung = idBestellung;
	}
	
	public Kunde getKunde() {		
		return kunde;
	}
	
	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public void setSumme(BigDecimal summe) {
		this.summe = summe;
	}
	
	public BigDecimal getSumme() {
		return this.summe;
	}
	
	public URI getBestellungUri() {
		return bestellungUri;
	}
	
	public void setBestellungUri(URI bestellungUri) {
		this.bestellungUri = bestellungUri;
	}
	
	public URI getKundeUri() {
		return kundeUri;
	}
	
	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}
	
	public Boolean getAktiv() {
		return this.aktiv;
	}

	public void setAktiv(Boolean aktiv) {
		this.aktiv = aktiv;
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Boolean getStorno() {
		return this.storno;
	}

	public void setStorno(Boolean storno) {
		this.storno = storno;
	}

	public List<Bestellposition> getBestellpositionen() {
		return bestellpositionen == null ? null : Collections.unmodifiableList(bestellpositionen);
	}

	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		// Wiederverwendung der vorhandenen Collection
		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}
	
	public void addBestellposition(Bestellposition bestellposition) {
		if (bestellpositionen == null) {
			this.bestellpositionen = new ArrayList<>();
		}
		this.bestellpositionen.add(bestellposition);
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Bestellung [idBestellung=" + idBestellung + ", storno="
				+ storno + ", aktualisiert=" + aktualisiert + ", erzeugt="
				+ erzeugt + ", aktiv=" + aktiv + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aktiv == null) ? 0 : aktiv.hashCode());
		result = prime * result + ((storno == null) ? 0 : storno.hashCode());
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
		Bestellung other = (Bestellung) obj;
		if (aktiv == null) {
			if (other.aktiv != null)
				return false;
		} else if (!aktiv.equals(other.aktiv))
			return false;
		if (storno == null) {
			if (other.storno != null)
				return false;
		} else if (!storno.equals(other.storno))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}