USE ${dbname.mysql};

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/kunde.csv'
INTO TABLE kunde
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/adresse.csv'
INTO TABLE adresse
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/bestellung.csv'
INTO TABLE bestellung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/produkt.csv'
INTO TABLE produkt
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/bestellposition.csv'
INTO TABLE bestellposition
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/Software/eclipse-workspace-git/swe03/shop/src/test/resources/mysql/kunde_rolle.csv'
INTO TABLE kunde_rolle
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;