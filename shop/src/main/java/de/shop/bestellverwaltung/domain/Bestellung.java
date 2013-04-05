package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static java.util.logging.Level.FINEST;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
//import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;


/**
 * The persistent class for the bestellung database table.
 * 
 */
@Entity
//@Table(name = "bestellung")
@NamedQueries({
	@NamedQuery(name = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
				query = "SELECT b FROM Bestellung b " 
						+ "WHERE b.kunde.kundenId = :" + Bestellung.PARAM_KUNDEID),					
	@NamedQuery(name = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE_JOIN_POSITIONEN,	
				query = "SELECT p FROM Bestellung b JOIN b.bestellpositionen p " 
						+ "WHERE b.bestellId = :" + Bestellung.PARAM_KUNDEID),
	@NamedQuery(name = Bestellung.FIND_BESTELLUNG_BY_PREIS,
				query = "SELECT b FROM Bestellung b WHERE b.gesamtpreis >= :" + Bestellung.PARAM_PREIS 
						+ " ORDER BY b.gesamtpreis desc")
})
@Cacheable
@XmlRootElement(name = "bestellung")
@XmlAccessorType(FIELD)
public class Bestellung implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(
			MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX + "findBestellungenByKunde";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE_JOIN_POSITIONEN = PREFIX 
								+ "findBestellungenByKundeJoinPositionen";
	public static final String FIND_BESTELLUNG_BY_PREIS = PREFIX + "findBestellungByPreis";
	
	public static final String PARAM_KUNDEID = "kunden_id";
	public static final String PARAM_PREIS = "gesamtpreis";

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "bestell_id", unique = true, nullable = false, updatable = false)
		@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
		@XmlAttribute
		private Long bestellId = KEINE_ID;
		
		@Version
		@XmlTransient
		private int version = ERSTE_VERSION;
		
//		@Version
		@Column(nullable = false)
		@Temporal(TIMESTAMP)
		@XmlTransient
		private Date aktualisiert = null;
		
		@XmlElement(name = "bezahlart")
		private String bezahlart;
		
		@Column(nullable = false)
		@Temporal(TIMESTAMP)
		@XmlTransient
		private Date erzeugt = null;

		private int gesamtpreis;
		
		private String lieferart;

		private String status;

		//bi-directional many-to-one association to Bestellposition
		@OneToMany(fetch = FetchType.EAGER, cascade = { PERSIST, REMOVE })
		@JoinColumn(name = "bestell_fk", nullable = false)

		@NotEmpty(message = "{bestellverwaltung.bestellung.bestellposition.notEmpty}")
		@XmlElementWrapper(name = "Bestellpositionen", required =  true)
		@XmlElement(name = "Bestellposition", required = true)
		private List<Bestellposition> bestellpositionen;
		

		//bi-directional many-to-one association to Kunde
		@ManyToOne(optional = false)
		@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
		@NotNull (message = "{bestellverwaltung.bestellung.kunde.notNull}", 
								groups = PreExistingGroup.class)
		@XmlTransient
		private Kunde kunde;
		
		@Transient
		@XmlElement(name = "kunde", required = true)
		private URI kundeUri;

	    public Bestellung() {
	    	super();
	    }
	    
		public Bestellung(Kunde kunde, List<Bestellposition> bestellpositionen) {
			super();
			this.kunde = kunde;
			this.bestellpositionen = bestellpositionen;
		}
	    
	    
	    @PrePersist
	    private void prePersist() {
	    	erzeugt = new Date();
	    	aktualisiert = new Date();
	    }
	    
		@PostPersist
		private void postPersist() {
			LOGGER.log(FINEST, "Neue Bestellung mit ID=%d", bestellId);
		}
		

		@PreUpdate
		private void preUpdate() {
			aktualisiert = new Date();
		}
		
		@PostUpdate
		protected void postUpdaate() {
			LOGGER.log(FINEST, "Bestellung mit ID = {0} aktualisiert:  = {1}", new Object[] {bestellId, version });
			aktualisiert = new Date();
		}


		public Long getBestellId() {
			return bestellId;
		}

		public void setBestellId(Long bestellId) {
			this.bestellId = bestellId;
		}
		
		public int getVersion() {
			return version;
		}
		
		public void setVersion(int version) {
			this.version = version;
		}

		public Date getAktualisiert() {
			return aktualisiert == null ? null : (Date) erzeugt.clone();
		}

		public void setAktualisiert(Date aktualisiert) {
			this.aktualisiert = aktualisiert == null ? null : (Date)
					aktualisiert.clone();
		}


		public String getBezahlart() {
			return this.bezahlart;
		}

		public void setBezahlart(String bezahlart) {
			this.bezahlart = bezahlart;
		}

		public Date getErzeugt() {
			return erzeugt == null ? null : (Date) erzeugt.clone();
		}

		public void setErzeugt(Date erzeugt) {
			this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
		}

		public Integer getGesamtpreis() {
			return this.gesamtpreis;
		}

		public void setGesamtpreis(Integer gesamtpreis) {
			this.gesamtpreis = gesamtpreis;
		}

		public String getLieferart() {
			return this.lieferart;
		}

		public void setLieferart(String lieferart) {
			this.lieferart = lieferart;
		}

		public String getStatus() {
			return this.status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<Bestellposition> getBestellpositionen() {
			if (bestellpositionen == null) {
			return null; 
			}
			return Collections.unmodifiableList(bestellpositionen);
		}

		public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
			if (this.bestellpositionen == null) {
				this.bestellpositionen = bestellpositionen;
				return;
			}
			
			this.bestellpositionen.clear();
			if (bestellpositionen != null) {
				this.bestellpositionen.addAll(bestellpositionen);
			}
		}


		public Bestellung addBestellposition(Bestellposition bestellposition) {
			if (bestellpositionen == null) {
				bestellpositionen = new ArrayList<>();
			}
			bestellpositionen.add(bestellposition);
			return this;
		}
		
		public Kunde getKunde() {
			return kunde;
		}

		public void setKunde(Kunde kunde) {
			this.kunde = kunde;
		}
		
		public URI getKundeUri() {
			return kundeUri;
		}
		
		public void setKundeUri(URI kundeUri) {
			this.kundeUri = kundeUri;
		}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
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
		if (erzeugt == null) {
			if (other.erzeugt != null)
				return false;
		}
		else if (!erzeugt.equals(other.erzeugt))
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		}
		else if (!kunde.equals(other.kunde))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [bestellId=" + bestellId + ", aktualisiert="
				+ aktualisiert + ", bezahlart=" + bezahlart 
				+ ", erzeugt=" + erzeugt + ", gesamtpreis=" 
				+ gesamtpreis + ", lieferart=" + lieferart
				+ ", status=" + status + "]";
	}

	public void setValues(Bestellung b) {
		status = b.status;
	}
	
}
