OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellposition
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
position_id,
anzahl,
idx,
erzeugt,
aktualisiert,
bestell_fk,
produkt_fk)