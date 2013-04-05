OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
adresse_id,
plz,
strasse,
erzeugt,
aktualisiert,
kunde_fk)