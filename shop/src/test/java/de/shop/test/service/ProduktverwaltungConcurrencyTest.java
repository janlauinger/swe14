package de.shop.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
//import static org.hamcrest.CoreMatchers.notNullValue;


//import java.util.Collection;
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
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

//import de.shop.artikelverwaltung.dao.ArtikelverwaltungDao.FetchType;
import de.shop.artikelverwaltung.domain.Produkt;
import de.shop.artikelverwaltung.service.Produktverwaltung;
//import de.shop.bestellverwaltung.domain.Bestellung;
//import de.shop.kundenverwaltung.domain.Adresse;
//import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.test.util.AbstractConcurrentTransaction;
import de.shop.test.util.AbstractTest;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.ConcurrentUpdatedException;

@RunWith(Arquillian.class)
public class ProduktverwaltungConcurrencyTest extends AbstractTest {
	private static final String BEZ_NEU_UPDATEUPDATE = "Update";
	private static final String BEZ_NEU_UPDATEDELETE = "Updatedelte";
	private static final String BEZ_NEU_DELETEUPDATE = "Deleteupdate";
	private static final Integer PREIS = 10;
	
	@Inject
	private Logger logger;
	
	@Inject
	private Produktverwaltung pv;
//	@Ignore
	@Test
	public void updateUpdateProdukt() throws InterruptedException, LoginException, RollbackException,
				HeuristicMixedException, HeuristicRollbackException, SystemException,
				ExecutionException, NotSupportedException {
		logger.finer("updateUpdateProdukt BEGINN");

		final String bezeichnung = BEZ_NEU_UPDATEUPDATE;
		final Integer preis = PREIS;

		final Produkt tempProdukt = new Produkt();
		tempProdukt.setBezeichnung(bezeichnung);
		tempProdukt.setPreis(preis);
		
		final Produkt neuesProdukt = pv.createProdukt(tempProdukt, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(neuesProdukt.getBezeichnung(), is(bezeichnung));
		assertThat(neuesProdukt.getPreis(), is(preis));
		
		
		final Callable<Void> concurrentUpdate = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(">>> concurrentBody BEGIN");
				
				final Produkt produkt = pv.findProduktById(neuesProdukt.getProduktId());
				produkt.setBezeichnung(produkt.getBezeichnung() + "concurrent");
				pv.updateProdukt(produkt, LOCALE);
				
				logger.finer("<<<concurrentBody ENDE");
			}
			
		};
		final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentUpdate);
		future.get();
		
		neuesProdukt.setBezeichnung(neuesProdukt.getBezeichnung() + "updated");
		
		trans.begin();
		try {
			pv.updateProdukt(neuesProdukt, LOCALE);
			fail("ConcurrentUpdateExcpetion wurde nicht geworfen");
		}
		catch (ConcurrentUpdatedException e) {
			trans.rollback();
			logger.finer("updateUpdateBestellung ENDE");
		}

	}
	@Ignore
	@Test
	public void updateDeleteProdukt() throws InterruptedException,
			RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, ExecutionException,
			NotSupportedException, SecurityException, IllegalStateException,
			javax.transaction.RollbackException, javax.transaction.NotSupportedException {
		logger.finer("updateDeleteProdukt BEGINN");

		final String bezeichnung = BEZ_NEU_UPDATEDELETE;
		final Integer preis = PREIS;

		final Produkt tempProdukt = new Produkt();
		tempProdukt.setBezeichnung(bezeichnung);
		tempProdukt.setPreis(preis);
		
		final Produkt neuesProdukt = pv.createProdukt(tempProdukt, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(neuesProdukt.getBezeichnung(), is(bezeichnung));
		assertThat(neuesProdukt.getPreis(), is(preis));
		
		
		final Callable<Void> concurrentDelete = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(">>> concurrentBody BEGINN");
				pv.deleteProduktById(neuesProdukt.getProduktId());
				logger.finer("<<< concurrentBody ENDE");
			}
		};
		final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentDelete);
		future.get(); 

		neuesProdukt.setBezeichnung(neuesProdukt.getBezeichnung() + "up");

		// Then
		thrown.expect(ConcurrentDeletedException.class);
		thrown.expectMessage(" konkurrierend geloescht");
		trans.begin();
		pv.updateProdukt(neuesProdukt, LOCALE);

		logger.finer("updateDeleteProdukt ENDE");
	}
	
	@Test
	public void deleteUpdateKunde() throws InterruptedException,
			LoginException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, ExecutionException,
			NotSupportedException {
		logger.finer("deleteUpdateKunde BEGINN");

		final String bezeichnung = BEZ_NEU_DELETEUPDATE;
		final Integer preis = PREIS;

		final Produkt tempProdukt = new Produkt();
		tempProdukt.setBezeichnung(bezeichnung);
		tempProdukt.setPreis(preis);
		
		final Produkt neuesProdukt = pv.createProdukt(tempProdukt, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(neuesProdukt.getBezeichnung(), is(bezeichnung));
		assertThat(neuesProdukt.getPreis(), is(preis));
		
		final Callable<Void> concurrentUpdate = new AbstractConcurrentTransaction() {
			@Override
			protected void concurrentBody() {
				logger.finer(">>> concurrentBody BEGINN");

				final Produkt pro = pv.findProduktById(
						neuesProdukt.getProduktId());
				pro.setBezeichnung(pro.getBezeichnung() + "concurrent");
				pv.updateProdukt(pro, LOCALE);

				logger.finer("<<< concurrentBody ENDE");
			}
		};
		final ExecutorService executorService = Executors
				.newSingleThreadExecutor();
		final Future<Void> future = executorService.submit(concurrentUpdate);
		future.get(); // Warten bis der "parallele" Thread fertig ist

		trans.begin();
		pv.deleteProduktById(neuesProdukt.getProduktId());
		trans.commit();

		// Then
		trans.begin();
		final Produkt neuPro2 = pv.findProduktById(neuesProdukt.getProduktId());
		trans.commit();
		assertThat(neuPro2, is(nullValue()));

		logger.finer("deleteUpdateKunde ENDE");
	}

}
