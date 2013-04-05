USE ${dbname.mysql};
SHOW WARNINGS;

DROP TABLE IF EXISTS kunde;
CREATE TABLE kunde(
	kunden_id BIGINT NOT NULL auto_increment, 
	name NVARCHAR(32) NOT NULL,
	vorname NVARCHAR(32),
	geschlecht CHAR(1),
	registrierdatum DATE NOT NULL,
	kundenart CHAR(1),
	newsletter CHAR(1),
	email NVARCHAR(128) NOT NULL UNIQUE,
	passwort NVARCHAR(256),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	version int,
	username NVARCHAR(32) NOT NULL UNIQUE,
	PRIMARY KEY (kunden_id)
);

DROP TABLE IF EXISTS rolle;
CREATE TABLE rolle(
id bigint NOT NULL PRIMARY KEY,
bezeichnung NVARCHAR(32)
);

DROP TABLE IF EXISTS kunde_rolle;
CREATE TABLE kunde_rolle(
	kunde_fk bigint not null references kunde(kunden_id),
	rollen_fk bigint not null references rolle(id)
	);


DROP TABLE IF EXISTS adresse;
CREATE TABLE adresse(
	adresse_id BIGINT NOT NULL auto_increment,
	plz CHAR(5) NOT NULL,
	strasse NVARCHAR(32),
	ort NVARCHAR(32) NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	kunde_fk bigint not null references kunde(kunden_id),
	PRIMARY KEY (adresse_id)
);

DROP TABLE IF EXISTS bestellung;
CREATE TABLE bestellung(
	bestell_id BIGINT NOT NULL auto_increment,
	bezahlart NVARCHAR(10),
	lieferart NVARCHAR(20),
	gesamtpreis double not null,
	status NVARCHAR(20),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	idx SMALLINT,
	kunde_fk integer NOT NULL REFERENCES kunde(kunden_id),
	version int,
	PRIMARY KEY (bestell_id)
);


DROP TABLE IF EXISTS produkt;
CREATE TABLE produkt(
	produkt_id BIGINT NOT NULL auto_increment,
	bezeichnung NVARCHAR(32) NOT NULL,
	preis DOUBLE,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	version int,
	PRIMARY KEY (produkt_id)
);


DROP TABLE IF EXISTS bestellposition;
CREATE TABLE bestellposition(
	position_id BIGINT NOT NULL auto_increment,
	anzahl SMALLINT NOT NULL,
	idx SMALLINT,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	bestell_fk BIGINT NOT NULL REFERENCES bestellung(bestell_id),
	produkt_fk BIGINT NOT NULL REFERENCES produkt(produkt_id),
	PRIMARY KEY (position_id)
);


	DROP TABLE IF EXISTS hibernate_sequence;
	CREATE TABLE hibernate_sequence(next_val BIGINT NOT NULL auto_increment,
	PRIMARY KEY(next_val));


CREATE INDEX bestpos_bestellung_index ON bestellposition(bestell_fk);
CREATE INDEX bestpos_produkt_index ON bestellposition(produkt_fk);
CREATE UNIQUE INDEX kunde_email ON kunde(email);
CREATE INDEX adresse_kunde_index ON adresse(kunde_fk);
CREATE INDEX bestellung_kunde_index ON bestellung(kunde_fk);


