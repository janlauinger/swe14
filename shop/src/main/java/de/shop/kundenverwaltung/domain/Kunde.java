package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.ScriptAssert;

import de.shop.auth.service.jboss.AuthService.RolleType;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;

@Entity
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name = Kunde.FIND_KUNDEN,
			query = "SELECT k"
					+ " FROM Kunde k"),
	@NamedQuery(name = Kunde.FIND_KUNDE_BY_EMAIL,
			query = "SELECT DISTINCT k"
   			            + " FROM   Kunde k"
   			            + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),
   	@NamedQuery(name = Kunde.FIND_KUNDE_BY_EMAIL_FETCH_BESTELLUNGEN,
   			query = "SELECT DISTINCT k"
   		   			    + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
   		   			    + " WHERE  k.email = :" + Kunde.PARAM_KUNDE_EMAIL),  			            
   	@NamedQuery(name = Kunde.FIND_KUNDEN_ORDER_BY_ID,
   			query = "SELECT k"
   					+ " FROM Kunde k"
   					+ " ORDER BY idKunde"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
			query = "SELECT  DISTINCT k"
			+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
	@NamedQuery(name = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
			query = "SELECT k"
			 		+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"
			 		+ " WHERE k.id = :" + Kunde.PARAM_KUNDE_ID),
	@NamedQuery(name = Kunde.FIND_KUNDEN_BY_NACHNAME,
				query = "SELECT k"
						+ " FROM Kunde k"
						+ " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
			     query = "SELECT DISTINCT k"
					    + " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
					    + " WHERE  UPPER(k.nachname) = UPPER(:" + Kunde.PARAM_KUNDE_NACHNAME + ")"),
	@NamedQuery(name  = Kunde.FIND_KUNDE_BY_USERNAME,
			            query = "SELECT   k"
						        + " FROM  Kunde k"
			            		+ " WHERE CONCAT('', k.id) = :" + Kunde.PARAM_KUNDE_USERNAME),
})
@ScriptAssert(lang = "javascript",
		      script = "(this.passwort == null && this.passwortWdh == null)"
		    		  + "|| (this.passwort != null && this.passwort.equals(this.passwortWdh))",
		      message = "{kundenverwaltung.kunde.password.notEqual}",
              groups = PasswordGroup.class)
public class Kunde implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN = PREFIX + "findKundeByIdFetchBestellungen";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX + "findKundenOrderByID";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX + "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN =
            				   PREFIX + "findKundenByNachnameFetchBestellungen";
	public static final String FIND_KUNDE_BY_USERNAME = PREFIX + "findKundeByUsername";
	public static final String FIND_USERNAME_BY_USERNAME_PREFIX = PREFIX + "findKundeByUsernamePrefix";
	public static final int EMAIL_LENGTH_MAX = 128;
	private static final int ERSTE_VERSION = 1;
	
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String FIND_KUNDE_BY_EMAIL_FETCH_BESTELLUNGEN = PREFIX + "findKundeByEmailFetchBestellungen";
	public static final String PARAM_KUNDE_EMAIL = "email";
	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_USERNAME = "username";
	public static final String PARAM_USERNAME_PREFIX = "usernamePrefix";
	
	public static final int NACHNAME_LENGTH_MIN = 2;
	public static final int NACHNAME_LENGTH_MAX = 32;
	public static final int PASSWORD_LENGTH_MAX = 32;
	public static final int VORNAME_LENGTH_MAX = 32;
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	
	public static final String NACHNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	
	@Id
	@GeneratedValue
	@Column(name = "idKunde", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Column(unique = true)
	@NotNull(message = "{kundenverwaltung.kunde.username.notNull}")
	private String username;
	
	@Column(length = NACHNAME_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX,
	      message = "{kundenverwaltung.kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname;
	
	@Column(length = VORNAME_LENGTH_MAX, nullable = false)
	@Size(max = VORNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;
	
	@Column(length = EMAIL_LENGTH_MAX, nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	@Past(message = "{kundenverwaltung.kunde.geburtsdatum.vergangenheit}")
	@Temporal(DATE)
	private Date geburtsdatum;
	
	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;
	
	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	private URI bestellungenUri;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;
	
	@Column(length = PASSWORD_LENGTH_MAX, nullable = false)
	private String passwort;
	
	//TODO Prüfung, ob beide Passwörter gleich sind
	@Transient
	private String passwortWdh;
	
	@Column (name = "aktiv")
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

	public void setValues(Kunde k) {
		nachname = k.nachname;
		vorname = k.vorname;
		geburtsdatum = k.geburtsdatum;
		email = k.email;
		passwort = k.passwort;
		passwortWdh = k.passwortWdh;
		aktiv = k.aktiv;
	}

	public Long getIdKunde() {
		return this.id;
	}

	public void setIdKunde(Long idKunde) {
		this.id = idKunde;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getAktiv() {
		return this.aktiv;
	}
	
	public void setAktiv(Boolean bool) {
		this.aktiv = bool;
	}
	
	public Date getAktualisiert() {
		return this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getErzeugt() {
		return this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getGeburtsdatum() {
		return this.geburtsdatum == null ? null : (Date) geburtsdatum.clone();
	}

	public void setGeburtsdatum(Date geburtsdatum) {
		this.geburtsdatum = geburtsdatum == null ? null : (Date) geburtsdatum.clone();
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String name) {
		this.nachname = name;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getPasswortWdh() {
		return passwortWdh;
	}

	public void setPasswortWdh(String passwortWdh) {
		this.passwortWdh = passwortWdh;
	}
	
	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public List<Bestellung> getBestellungen() {
		return this.bestellungen;
	}

	public void setBestellungen(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen;
	}
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<Bestellung>();
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
	
	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}

	public Adresse getAdresse() {
		return this.adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "Kunde [id=" + id + ", username=" + username + ", nachname=" + nachname
				+ ", vorname=" + vorname + ", email=" + email
				+ ", geburtsdatum=" + geburtsdatum + ", aktualisiert="
				+ aktualisiert + ", erzeugt=" + erzeugt + ", passwort="
				+ passwort + ", aktiv=" + aktiv + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
}