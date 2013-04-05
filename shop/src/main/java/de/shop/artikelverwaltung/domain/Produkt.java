package de.shop.artikelverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.util.Constants.ERSTE_VERSION;
//import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINEST;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
//import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
//import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the produkt database table.
 * 
 */
@Entity
@Table(name = "produkt")
@NamedQueries({
	@NamedQuery(name = Produkt.FIND_PRODUKT_BY_ID,
				query = "SELECT a "
						+ "FROM Produkt a "
						+ "WHERE a.produktId = :" + Produkt.PARAM_ID
						+ " ORDER BY a.produktId ASC")
	,
	@NamedQuery(name  = Produkt.FIND_PRODUKT_BY_BEZ,
            	query = "SELECT      a "
                        + " FROM     Produkt a "
						+ " WHERE    a.bezeichnung LIKE :" + Produkt.PARAM_BEZEICHNUNG
			 	        + " ORDER BY a.produktId ASC"),
   	@NamedQuery(name  = Produkt.FIND_PRODUKT_MAX_PREIS,
            	query = "SELECT      a "
                        + " FROM     Produkt a "
						+ " WHERE    a.preis < :" + Produkt.PARAM_PREIS
			 	        + " ORDER BY a.produktId ASC")
})
public class Produkt implements Serializable {
	private static final Logger LOGGER = Logger.getLogger(
			MethodHandles.lookup().lookupClass().getName());

	private static final long serialVersionUID = 1L;
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	
	private static final String PREFIX = "Produkt.";
	public static final String FIND_PRODUKT_BY_ID = PREFIX + "findProdukteByID";
	public static final String FIND_PRODUKT_BY_BEZ = PREFIX + "findProdukteByBez";
	public static final String FIND_PRODUKT_MAX_PREIS = PREFIX + "findProdukteByMaxPreis";
	public static final String FIND_PRODUKT_ORDER_BY_ID = PREFIX + "findProduktOrderByID";
	public static final String FIND_PRODUKT = PREFIX + "findProdukt";

	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_PREIS = "preis";
	public static final String PARAM_ID = "produktId";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "produkt_id", unique = true, nullable = false, updatable = false) 
	//precision = LONG_ANZ_ZIFFERN)
	private Long produktId = KEINE_ID;
	
	@Version
	@XmlTransient
	private int version = ERSTE_VERSION;

	@Column(name = "bezeichnung", length = BEZEICHNUNG_LENGTH_MAX, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String bezeichnung = "";

	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt = null;
	
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	private Date aktualisiert = null;

	private Integer preis;

    public Produkt() {
    	super();
    }
    
    public Produkt(String bezeichnung, Integer preis) {
		super();
		this.bezeichnung = bezeichnung;
		this.preis = preis;
	}
    
//    @SuppressWarnings("unused")
    @PrePersist
    private void prePersist() {
    	erzeugt = new Date();
    	aktualisiert = new Date();
    }
    
	@PostPersist
//	@SuppressWarnings("unused")
	private void postPersist() {
		LOGGER.log(FINEST, "Neues Produkt mit ID={0}", produktId);
	}
	
	@PostUpdate
	protected void postUpdate() {
		LOGGER.log(FINEST, "Produkt mit ID={0} aktualisiert: version={1}", new Object[] {produktId, version });
	}
    
	@SuppressWarnings("unused")
	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

    
	public Long getProduktId() {
		return this.produktId;
	}

	public void setProduktId(Long produktId) {
		this.produktId = produktId;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public String getBezeichnung() {
		return this.bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
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
	
	public Integer getPreis() {
		return this.preis;
	}

	public void setPreis(Integer preis) {
		this.preis = preis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
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
		Produkt other = (Produkt) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} 
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Produkt [produktId=" + produktId + ", aktualisiert="
				+ aktualisiert + ", bezeichnung=" + bezeichnung + ", erzeugt="
				+ erzeugt + ", preis=" + preis + "]";
	}
}