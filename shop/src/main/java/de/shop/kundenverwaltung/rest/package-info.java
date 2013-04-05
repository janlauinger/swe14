@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED,
           namespace = KUNDENVERWALTUNG_NS,
           xmlns = @XmlNs(prefix = "", namespaceURI = KUNDENVERWALTUNG_NS))
@XmlAccessorType(FIELD)

package de.shop.kundenverwaltung.rest;

import static de.shop.util.Constants.KUNDENVERWALTUNG_NS;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
