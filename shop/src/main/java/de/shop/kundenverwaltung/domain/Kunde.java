package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static de.shop.util.Constants.ERSTE_VERSION;
import static java.util.logging.Level.FINEST;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
//import javax.persistence.Table;
import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
//import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;
//import de.shop.util.XmlDateAdapter;
//import javax.persistence.DiscriminatorColumn;

@Inheritance
@Entity
//@Table(name = "kunde")
//@DiscriminatorColumn(name = "art", length = 1)
@NamedQueries({
	@NamedQuery(name = Kunde.FIND_KUNDE_BY_NAME,
			query = Kunde.PARAM_KUNDE_SELECT 
			 + Kunde.PARAM_KUNDE_FROM 
			 + " WHERE k.name = :name")
	,
	@NamedQuery(name  = Kunde.FIND_KUNDEN,
    		query = Kunde.PARAM_KUNDE_SELECT
	         + " FROM   Kunde k")
	,
	@NamedQuery(name = Kunde.FIND_KUNDE_BY_PLZ,
			query = Kunde.PARAM_KUNDE_SELECT
			 + Kunde.PARAM_KUNDE_FROM
			 + " WHERE k.adresse.plz = :plz")
	,
	@NamedQuery(name = Kunde.FIND_KUNDE_BY_EMAIL,
			query = Kunde.PARAM_KUNDE_SELECT
			 + Kunde.PARAM_KUNDE_FROM
			 + " WHERE k.email = :email")
	,
	@NamedQuery(name = Kunde.FIND_KUNDEN_BY_DATE,
			query = Kunde.PARAM_KUNDE_SELECT
			+ Kunde.PARAM_KUNDE_FROM
			+ " WHERE k.registrierdatum = :" + Kunde.PARAM_KUNDE_SEIT)
	,
	@NamedQuery(name  = Kunde.FIND_KUNDEN_ORDER_BY_ID,
    query = Kunde.PARAM_KUNDE_SELECT
	        + Kunde.PARAM_KUNDE_FROM
            + " ORDER BY k.kundenId")
	,
	@NamedQuery(name  = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
			query = "SELECT  DISTINCT k"
			+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"
			+ " WHERE  k.kundenId = :" + Kunde.PARAM_KUNDE_ID)
	,
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NAME_FETCH_BESTELLUNGEN,
			query = "SELECT DISTINCT k"
			+ " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
			+ " WHERE  UPPER(k.name) = UPPER(:" + Kunde.PARAM_KUNDE_NAME + ")")
	,
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_USERNAME,
    query = "SELECT   k"
	        + " FROM  Kunde k"
    		+ " WHERE k.username = :" + Kunde.PARAM_KUNDE_USERNAME)
	,
	@NamedQuery(name  = Kunde.FIND_USERNAME_BY_USERNAME_PREFIX,
	            query = "SELECT   k.username"
				        + " FROM  Kunde k"
	            		+ " WHERE UPPER(k.username) LIKE UPPER(:" + Kunde.PARAM_USERNAME_PREFIX + ")")

})
@ScriptAssert(lang = "javascript",
	          script = "(_this.password == null && _this.passwordWdh == null)"
	                   + "|| (_this.password != null && _this.password.equals(_this.passwordWdh))",
	          message = "{kundenverwaltung.kunde.password.notEqual}",
	          groups = PasswordGroup.class)
@XmlRootElement(name = "kunde")
@XmlSeeAlso({
    Kunde.class
})
@XmlAccessorType(FIELD)


public class Kunde implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(
			MethodHandles.lookup().lookupClass().getName());
	
	private static final String NAMEN_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	
	public static final String NAME_PATTERN = PREFIX_ADEL + NAMEN_PATTERN + "(-" + NAMEN_PATTERN + ")?";
	public static final int NAME_LENGTH_MIN = 2;
	public static final int NAME_LENGTH_MAX = 32;
	public static final int PASSWORT_LENGTH_MAX = 256;
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int VORNAME_LENGTH_MAX = 32;
	
	public static final String PREFIX = "Kunde."; 
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDE_BY_NAME = PREFIX + "findKundeByName";
	public static final String FIND_KUNDE_BY_PLZ = PREFIX + "findKundeByPlz";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_DATE = PREFIX + "findKundeByDate";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_BY_NAME_FETCH_BESTELLUNGEN =
            PREFIX + "findKundenByNameFetchBestellungen";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderById";
	public static final String FIND_KUNDE_BY_USERNAME = PREFIX + "findKundeByUsername";
	public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
	
	public static final String PARAM_KUNDE_NAME = "name";
	public static final String PARAM_KUNDE_EMAIL = "email";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_ID = "kundenId";
	public static final String PARAM_KUNDE_SELECT = "SELECT k";
	public static final String PARAM_KUNDE_SEIT = "registrierdatum";
	public static final String PARAM_KUNDE_FROM = " FROM Kunde k ";
	public static final String PARAM_KUNDE_USERNAME = "username";
	public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kunden_id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "kundenId")
	private Long kundenId = KEINE_ID;
	
	@Version
	@XmlTransient
	private int version = ERSTE_VERSION;
	
//	@Version
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert;

	@Column(length = EMAIL_LENGTH_MAX, nullable = false, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	private String geschlecht;

	private String kundenart;

	@Column(length = NAME_LENGTH_MAX)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NAME_PATTERN, message = "{kundenverwaltung.kunde.name.pattern}")
	@XmlElement(name = "name")
	private String name;

	private Boolean newsletter;

	@Column(length = PASSWORT_LENGTH_MAX)
	private String passwort;

//    @Temporal(TemporalType.DATE)
//	//@Past(message = "{kundenverwaltung.kunde.seit.past}")
//	@XmlJavaTypeAdapter(XmlDateAdapter.class)
//    @XmlElement(name = "registrierdatum")
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date registrierdatum;

	@Column(length = VORNAME_LENGTH_MAX)
	@Size(max = VORNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;
	
	@Column(name = "username")
//	@Column//(length = NAME_LENGTH_MAX) //unique = true)
//	@Size(max = NAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.username.length}")
	private String username;

	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	@Transient
	@XmlElement(name = "bestellungen")
	private URI bestellungenUri;

    public Kunde() {
    	super();
    }

    @OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	@XmlElement(name = "adresse")
	private Adresse adresse;

    @PrePersist
    private void prePersist() {
    	erzeugt = new Date();
    	aktualisiert = new Date();
    	registrierdatum = new Date();
    }
    
	@PostPersist
	private void postPersist() {
		setUsername(getKundenId().toString());
		LOGGER.log(FINEST, "Neuer Kunde mit ID=%d", kundenId);
	}
    
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}
	
	@PostUpdate
	protected void postUpdate() {
		LOGGER.log(FINEST, "Kunde mit ID={0} aktualisiert: version={1}", new Object[] {kundenId, version });
	}

	public void setValues(Kunde k) {
		name = k.name;
		vorname = k.vorname;
		geschlecht = k.geschlecht;
		kundenart = k.kundenart;
		newsletter = k.newsletter;
		username = k.username;
		email = k.email;
		passwort = k.passwort;
	}
	
	public Long getKundenId() {
		return this.kundenId;
	}

	public void setKundenId(Long kundenId) {
		this.kundenId = kundenId;
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
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) aktualisiert.clone();
	}
	
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public String getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(String geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getKundenart() {
		return this.kundenart;
	}

	public void setKundenart(String kundenart) {
		this.kundenart = kundenart;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getNewsletter() {
		return this.newsletter;
	}

	public void setNewsletter(Boolean newsletter) {
		this.newsletter = newsletter;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public Date getRegistrierdatum() {
		return this.registrierdatum == null ? null : (Date) registrierdatum.clone();
	}

	public void setRegistrierdatum(Date registrierdatum) {
		this.registrierdatum = registrierdatum == null ? null : (Date) registrierdatum.clone();
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public List<Bestellung> getBestellungen() {
		if (bestellungen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}

		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
	}
	
	public URI getBestellungenUri() {
		return bestellungenUri;
	}
	
	public void setBestellungenUri(URI bestellungenUri) {
		this.bestellungenUri = bestellungenUri;
	}
	
	public Adresse getAdresse() {
		return this.adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());

		result = prime * result
				+ ((geschlecht == null) ? 0 : geschlecht.hashCode());
		result = prime * result
				+ ((kundenart == null) ? 0 : kundenart.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((newsletter == null) ? 0 : newsletter.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
		result = prime * result
				+ ((registrierdatum == null) ? 0 : registrierdatum.hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());

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
		Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (geschlecht == null) {
			if (other.geschlecht != null)
				return false;
		}
		else if (!geschlecht.equals(other.geschlecht))
			return false;
		if (kundenart == null) {
			if (other.kundenart != null)
				return false;
		}
		else if (!kundenart.equals(other.kundenart))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (newsletter == null) {
			if (other.newsletter != null)
				return false;
		}
		else if (!newsletter.equals(other.newsletter))
			return false;
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		}
		else if (!passwort.equals(other.passwort))
			return false;
		if (registrierdatum == null) {
			if (other.registrierdatum != null)
				return false;
		}
		else if (!registrierdatum.equals(other.registrierdatum))
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		}
		else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [email= " + email + ", geschlecht= " + geschlecht
				+ ", name= " + name + ", vorname= " + vorname + ", username= " 
				+ username + ", passwort= " + passwort + "]";
	}

}