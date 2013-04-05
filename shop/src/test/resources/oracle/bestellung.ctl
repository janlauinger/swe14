OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
bestell_id,
bezahlart,
lieferart,
gesamtpreis,
status,
erzeugt,
aktualisiert,
idx,
kunde_fk,
version)