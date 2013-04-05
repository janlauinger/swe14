CREATE SEQUENCE hibernate_sequence START WITH ${sequence.start};

CREATE TABLE kunde(
	kunden_id INTEGER NOT NULL PRIMARY KEY,
	name VARCHAR2(32) NOT NULL,
	vorname VARCHAR2(32) NOT NULL,
	geschlecht CHAR(1),
	registrierdatum DATE,
	kundenart CHAR(1),
	newsletter CHAR(1),
	email VARCHAR2(128) NOT NULL UNIQUE,
	passwort VARCHAR2(256),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	version INTEGER DEFAULT 0,
	username VARCHAR2(32) NOT NULL UNIQUE
);

CREATE TABLE rolle(
id INTEGER NOT NULL PRIMARY KEY,
bezeichnung VARCHAR2(32)
);

CREATE TABLE kunde_rolle(
	kunde_fk INTEGER not null references kunde(kunden_id),
	rollen_fk INTEGER not null references rolle(id)
	);


CREATE TABLE adresse(
	adresse_id INTEGER NOT NULL PRIMARY KEY,
	plz CHAR(5) NOT NULL,
	strasse VARCHAR2(32),
	ort VARCHAR2(32) NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	kunde_fk INTEGER not null references kunde(kunden_id)
);

CREATE TABLE bestellung(
	bestell_id INTEGER NOT NULL PRIMARY KEY,
	bezahlart VARCHAR2(10),
	lieferart VARCHAR2(20),
	gesamtpreis INTEGER not null,
	status VARCHAR2(20),
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	idx SMALLINT NOT NULL,
	kunde_fk INTEGER NOT NULL REFERENCES kunde(kunden_id),
	version INTEGER DEFAULT 0
);

CREATE TABLE produkt(
	produkt_id INTEGER NOT NULL PRIMARY KEY,
	bezeichnung VARCHAR2(32) NOT NULL,
	preis INTEGER,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	version INTEGER DEFAULT 0
);

CREATE TABLE bestellposition(
	position_id INTEGER NOT NULL PRIMARY KEY,
	anzahl SMALLINT NOT NULL,
	idx SMALLINT,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL,
	bestell_fk INTEGER NOT NULL REFERENCES bestellung(bestell_id),
	produkt_fk INTEGER NOT NULL REFERENCES produkt(produkt_id)
);

CREATE INDEX bestpos_bestellung_index ON bestellposition(bestell_fk);
CREATE INDEX bestpos_produkt_index ON bestellposition(produkt_fk);
CREATE INDEX adresse_kunde_index ON adresse(kunde_fk);
CREATE INDEX bestellung_kunde_index ON bestellung(kunde_fk);

