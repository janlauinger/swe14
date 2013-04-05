package de.shop.bestellverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.Log;

@Singleton
@Log
public class BestellverwaltungObserver implements Serializable {
	private static final long serialVersionUID = -1567643645881819340L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Resource(lookup = "java:jboss/mail/Default")
	private transient Session mailSession;
	
	private String mailAbsender;   // in META-INF\seam-beans.xml setzen

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		if (mailAbsender == null) {
			LOGGER.warn("Der Absender fuer Bestellung-Emails ist nicht gesetzt.");
			return;
		}
		LOGGER.infof("Absender fuer Bestellung-Emails: %s", mailAbsender);
	}
	
	public void onCreateBestellung(@Observes @NotifyKunde Bestellung bestellung) {
		final String mailEmpfaenger = bestellung.getKunde().getEmail();
		if (mailAbsender == null || mailEmpfaenger == null) {
			return;
		}
		
		final MimeMessage message = new MimeMessage(mailSession);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(mailAbsender);
			message.setFrom(absenderObj);
			
			// Empfaenger setzen
			final InternetAddress empfaenger = new InternetAddress(mailEmpfaenger);
			message.setRecipient(RecipientType.TO, empfaenger);   // RecipientType: TO, CC, BCC

			// Subject setzen
			message.setSubject("Neue Bestellung Nr. " + bestellung.getBestellId());
			
			// Text setzen mit MIME Type "text/plain"
			final StringBuilder sb = new StringBuilder("Neue Bestellung Nr. "
                                                       + bestellung.getBestellId() + NEWLINE);
			for (Bestellposition bp : bestellung.getBestellpositionen()) {
				sb.append(bp.getAnzahl() + "\t" + bp.getProdukt().getBezeichnung() + NEWLINE);
			}
			final String text = sb.toString();
			LOGGER.trace(text);
			message.setText(text);

			// Hohe Prioritaet einstellen
			//message.setHeader("Importance", "high");
			//message.setHeader("Priority", "urgent");
			//message.setHeader("X-Priority", "1");

			Transport.send(message);
		}
		catch (MessagingException e) {
			LOGGER.error(e.getMessage());
			return;
		}
	}
}
