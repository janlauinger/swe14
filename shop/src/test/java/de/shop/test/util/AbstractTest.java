package de.shop.test.util;



import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Locale;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.dbunit.DatabaseUnitException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


public abstract class AbstractTest {
	protected static final Locale LOCALE = Locale.GERMAN;
	
	@Resource(lookup = "java:jboss/UserTransaction")
	protected UserTransaction trans;
	
	@PersistenceContext
	@Produces
	protected EntityManager em;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Inject
	protected DbService dbService;
	
	/**
	 */
	@Deployment
	public static Archive<?> deployment() {
		return ArchiveService.getInstance().getArchive();
	}
	
	/**
	 */
	@Before
	public void setup() throws SystemException, SQLException, DatabaseUnitException {   // NotSupportedException
		dbService.reload();
		
		assertThat(em, is(notNullValue()));
		
		// Arquillian mit Servlet-Protokoll: impliziter Start der Transaktion durch Seam Faces
		assertThat(trans.getStatus(), is(Status.STATUS_ACTIVE));

		// Arquillian mit JMX-Protokoll: manueller Start der Transaktion erforderlich
		//assertThat(trans.getStatus(), is(Status.STATUS_NO_TRANSACTION));
		//trans.begin();
	}
	
	/**
	 */
	@After
	public void teardown() {
		//      throws SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		// Arquillian mit JMX-Protokoll: manuelles Beenden der Transaktion erforderlich
		//
		//if (trans == null) {
		//	return;
		//}
		//
		//try {
		//	switch (trans.getStatus()) {
		//		case Status.STATUS_ACTIVE:
		//			trans.commit();
		//			break;
		//		    
		//		case Status.STATUS_MARKED_ROLLBACK:
		//			trans.rollback();
		//			break;
	    //            
	    //        default:
	    //        	fail();
	    //        	break;
		//	}
		//}
		//catch (RollbackException e) {
		//	// Commit ist fehlgeschlagen
		//	final Throwable t = e.getCause();
		//	// Gibt es "Caused by"
		//	if (t instanceof ConstraintViolationException) {
		//		// Es gibt Verletzungen bzgl. Bean Validation: auf der Console ausgeben
		//		final ConstraintViolationException cve = (ConstraintViolationException) t;
		//		final Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
		//		for (ConstraintViolation<?> v : violations) {
		//			System.err.println("!!! MESSAGE>>> " + v.getMessage());
		//			System.err.println("!!! INVALID VALUE>>> " + v.getInvalidValue());
		//			System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
		//		}
		//	}
		//
		//	throw new RuntimeException(e);
		//}
	}
	
	protected UserTransaction getUserTransaction() {
		return trans;
	}
}