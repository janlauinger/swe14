OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE produkt
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
produkt_id,
bezeichnung,
preis,
erzeugt,
aktualisiert,
version)