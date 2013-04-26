-- ===============================================================================
-- Jede SQL-Anweisung muss in genau 1 Zeile
-- Kommentare durch -- am Zeilenanfang
-- ===============================================================================

-- ===============================================================================
-- Tabellen fuer Enum-Werte *einmalig* anlegen und jeweils Werte einfuegen
-- Beim ALLERERSTEN Aufruf die Zeilen mit "DROP TABLE ..." durch -- auskommentieren
-- ===============================================================================

DROP TABLE rolle;
CREATE TABLE rolle(id NUMBER(1) NOT NULL PRIMARY KEY, name VARCHAR2(32) NOT NULL) CACHE;
INSERT INTO rolle VALUES (0, 'admin');
INSERT INTO rolle VALUES (1, 'mitarbeiter');
INSERT INTO rolle VALUES (2, 'abteilungsleiter');
INSERT INTO rolle VALUES (3, 'kunde');

-- ===============================================================================
-- Fremdschluessel in den bereits *generierten* Tabellen auf die obigen "Enum-Tabellen" anlegen
-- ===============================================================================

ALTER TABLE kunde_rolle ADD CONSTRAINT kunde_rolle__rolle_fk FOREIGN KEY (rolle_fk) REFERENCES rolle;