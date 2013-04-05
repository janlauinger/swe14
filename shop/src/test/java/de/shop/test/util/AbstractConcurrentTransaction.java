package de.shop.test.util;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 */
public abstract class AbstractConcurrentTransaction implements Callable<Void> {
	// @Inject mittels Arquillian/CDI funktioniert nicht, weil ein neuer Thread gestartet wird
	// und dort folglich CDI-Funktionalitaet nicht verfuegbar ist. Das betrifft
	//    @Inject Logger           und
	//    @Inject UserTransaction
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Override
	public Void call() {
		LOGGER.finer("call BEGINN");
		
		UserTransaction trans;
		Context ctx = null;
		try {
			try {
				ctx = new InitialContext();
				trans = (UserTransaction) ctx.lookup("java:jboss/UserTransaction");
			}
			finally {
				if (ctx != null) {
					ctx.close();
				}
			}
		}
		catch (NamingException e) {
			throw new RuntimeException(e);
		}

		try {
			trans.begin();
		}
		catch (NotSupportedException | SystemException e) {
			throw new RuntimeException(e);
		}


		try {
			concurrentBody();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		try {
			trans.commit();
		}
		catch (SecurityException |
			   IllegalStateException |
			   RollbackException |
			   HeuristicMixedException |
			   HeuristicRollbackException |
			   SystemException e) {
			throw new RuntimeException(e);
		}
		
		LOGGER.finer("call ENDE");
		return null;
	}
	
	protected abstract void concurrentBody() throws Exception;
}
