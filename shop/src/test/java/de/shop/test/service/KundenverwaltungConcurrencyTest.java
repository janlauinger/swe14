package de.shop.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.Kundenverwaltung;
import de.shop.test.util.AbstractConcurrentTransaction;
import de.shop.test.util.AbstractTest;
import de.shop.kundenverwaltung.dao.KundenverwaltungDao.FetchType;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.ConcurrentUpdatedException;

@RunWith(Arquillian.class)
public class KundenverwaltungConcurrencyTest extends AbstractTest {
	private static final String NACHNAME_NEU_UPDATEUPDATE = "Updateupdate";
	private static final String NACHNAME_NEU_UPDATEDELETE = "Updatedelete";
	private static final String NACHNAME_NEU_DELETEUPDATE = "Deleteupdate";
	private static final String VORNAME_NEU = "Vorname";
	private static final String EMAIL_NEU_UPDATEUPDATE = "Updateupdate@Updateupdate.de";
	private static final String EMAIL_NEU_UPDATEDELETE = "Updatedelete@Updatedelete.de";
	private static final String EMAIL_NEU_DELETEUPDATE = "Deleteupdate@Deleteupdate.de";
	private static final String PLZ_NEU = "76133";
	private static final String ORT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final int TAG = 31;
	private static final int MONAT = 0;
	private static final int JAHR = 2001;
	private static final Date SEIT_VORHANDEN = new GregorianCalendar(JAHR, MONAT, TAG).getTime();
	private static final String PASSWORD_NEU = "rod";
	private static final String KUNDE_NEU_KUNDENART = "p";
	private static final String KUNDE_NEU_GESCHLECHT = "m";

	@Inject
	private Logger logger;

	@Inject
	private Kundenverwaltung kv;

	@Ignore
	@Test
	public void updateUpdateKunde() throws InterruptedException,
			LoginException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, ExecutionException,
			NotSupportedException, javax.transaction.NotSupportedException {
		logger.finer("updateUpdateKunde START");

		// Given
		final String nachname = NACHNAME_NEU_UPDATEUPDATE;
		final String vorname = VORNAME_NEU;
		final String vornameSuffixNeu = "updated";
		final String email = EMAIL_NEU_UPDATEUPDATE;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final Date erzeugt = SEIT_VORHANDEN;
		final String kundenart = KUNDE_NEU_KUNDENART;
		final String geschlecht = KUNDE_NEU_GESCHLECHT;
		final String passwort = PASSWORD_NEU;
		final String USERNAME_NEU = "TTest";


		// When
		final Kunde tmpKunde = new Kunde();
		tmpKunde.setName(nachname);
		tmpKunde.setVorname(vorname);
		tmpKunde.setEmail(email);
		tmpKunde.setGeschlecht(geschlecht);
		tmpKunde.setNewsletter(true);
		tmpKunde.setPasswort(passwort);
		tmpKunde.setKundenart(kundenart);
		tmpKunde.setUsername(USERNAME_NEU);

		final Adresse adresse = new Adresse();
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setKunde(tmpKunde);
		tmpKunde.setAdresse(adresse);

		final Kunde neuerKunde = kv.createKunde(tmpKunde, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(neuerKunde.getKundenId().longValue() > 0, is(true));

		final Callable<Void> concurrentUpdate = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(" >>> concurrentBody BEGINN");

				final Kunde kunde = kv.findKundeById(neuerKunde.getKundenId(), FetchType.NUR_KUNDE);
						kunde.setVorname(kunde.getVorname() + "concurrent");
				kv.updateKunde(kunde, LOCALE);

				logger.finer(" <<< concurrentBody ENDE");
			}
		};

		final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentUpdate);
		future.get(); // Warten bis "parallele" Thread fertig ist

		neuerKunde.setVorname(neuerKunde.getVorname() + vornameSuffixNeu);

		trans.begin();
		try {
			kv.updateKunde(neuerKunde, LOCALE);
			fail("ConcurrentUpdatedException wurde nicht geworfen");
		}

		catch (ConcurrentUpdatedException e) {
			// then
			trans.rollback();

			trans.begin();
			kv.deleteKundeById(neuerKunde.getKundenId());
			logger.finer("updateUpdateKunde ENDE");
		}

	}

	
	@Test
	public void updateDeleteKunde() throws InterruptedException,
			RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, ExecutionException,
			NotSupportedException, SecurityException, IllegalStateException, 
			javax.transaction.RollbackException, javax.transaction.NotSupportedException {
		logger.finer("updateDeleteKunde BEGINN");

		// Given
		final String nachname = NACHNAME_NEU_UPDATEDELETE;
		final String vorname = VORNAME_NEU;
		final String vornameSuffixNeu = "updated";
		final String email = EMAIL_NEU_UPDATEDELETE;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String kundenart = KUNDE_NEU_KUNDENART;
		final String geschlecht = KUNDE_NEU_GESCHLECHT;
		final String passwort = PASSWORD_NEU;
		final String USERNAME_NEU = "TTest";

		// When
		final Kunde tmpKunde = new Kunde();
		tmpKunde.setName(nachname);
		tmpKunde.setVorname(vorname);
		tmpKunde.setEmail(email);
		tmpKunde.setGeschlecht(geschlecht);
		tmpKunde.setNewsletter(true);
		tmpKunde.setPasswort(passwort);
		tmpKunde.setKundenart(kundenart);
		tmpKunde.setUsername(USERNAME_NEU);
		
		final Adresse adresse = new Adresse(plz, ort, strasse);
		tmpKunde.setAdresse(adresse);
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setKunde(tmpKunde);
		final Kunde neuerKunde = kv.createKunde(tmpKunde, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(neuerKunde.getKundenId().longValue() > 0, is(true));

		final Callable<Void> concurrentDelete = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(">>> concurrentBody BEGINN");
				kv.deleteKundeById(neuerKunde.getKundenId());
				logger.finer("<<< concurrentBody ENDE");
			}
		};
		final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentDelete);
		future.get(); // Warten bis der "parallele" Thread fertig ist

		neuerKunde.setVorname(neuerKunde.getVorname() + vornameSuffixNeu);

		// Then
		thrown.expect(ConcurrentDeletedException.class);
		thrown.expectMessage(" konkurrierend geloescht");
		trans.begin();
		kv.updateKunde(neuerKunde, LOCALE);

		logger.finer("updateDeleteKunde ENDE");
	}


	@Test
	public void deleteUpdateKunde() throws InterruptedException,
			LoginException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, ExecutionException,
			NotSupportedException {
		logger.finer("deleteUpdateKunde BEGINN");

		// Given
		final String nachname = NACHNAME_NEU_DELETEUPDATE;
		final String vorname = VORNAME_NEU;
		final String email = EMAIL_NEU_DELETEUPDATE;
		final String plz = PLZ_NEU;
		final String ort = ORT_NEU;
		final String strasse = STRASSE_NEU;
		final String kundenart = KUNDE_NEU_KUNDENART;
		final String geschlecht = KUNDE_NEU_GESCHLECHT;
		final String passwort = PASSWORD_NEU;
		final String USERNAME_NEU = "TTest";

		// When
		final Kunde tmpKunde = new Kunde();
		tmpKunde.setName(nachname);
		tmpKunde.setVorname(vorname);
		tmpKunde.setEmail(email);
		tmpKunde.setGeschlecht(geschlecht);
		tmpKunde.setNewsletter(true);
		tmpKunde.setPasswort(passwort);
		tmpKunde.setKundenart(kundenart);
		tmpKunde.setUsername(USERNAME_NEU);
		
		final Adresse adresse = new Adresse(plz, ort, strasse);
		tmpKunde.setAdresse(adresse);
		adresse.setPlz(plz);
		adresse.setOrt(ort);
		adresse.setStrasse(strasse);
		adresse.setKunde(tmpKunde);
		final Kunde neuerKunde = kv.createKunde(tmpKunde, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		assertThat(neuerKunde.getKundenId().longValue() > 0, is(true));

		final Callable<Void> concurrentUpdate = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(">>> concurrentBody BEGINN");

				final Kunde kunde = kv.findKundeById(
						neuerKunde.getKundenId(), FetchType.NUR_KUNDE);
				kunde.setVorname(kunde.getVorname() + "concurrent");
				kv.updateKunde(kunde, LOCALE);

				logger.finer("<<< concurrentBody ENDE");
			}
		};
		final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentUpdate);
		future.get(); // Warten bis der "parallele" Thread fertig ist

		trans.begin();
		kv.deleteKunde(neuerKunde);
		trans.commit();

		// Then
		trans.begin();
		final Kunde neuerKunde2 = kv.findKundeById(neuerKunde.getKundenId(),
				FetchType.NUR_KUNDE);
		trans.commit();
		assertThat(neuerKunde2, is(nullValue()));

		logger.finer("deleteUpdateKunde ENDE");
	}

}
