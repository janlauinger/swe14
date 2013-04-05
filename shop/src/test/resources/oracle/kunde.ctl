OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
kunden_id,
name,
vorname,
geschlecht,
registrierdatum,
kundenart,
email,
passwort,
erzeugt,
aktualisiert,
version,
username)