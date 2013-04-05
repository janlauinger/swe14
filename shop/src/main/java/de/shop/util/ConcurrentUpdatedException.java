package de.shop.util;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ConcurrentUpdatedException extends AbstractShopException {
	private static final long serialVersionUID = -5705818853925553302L;
	private final Object id;
	
//	@Resource(lookup = TRANSACTION_NAME)
//	private UserTransaction trans;

	public ConcurrentUpdatedException(Object id, Throwable t) {
		super("Das Objekt mit der ID " + id + " wurde konkurrierend modifiziert", t);
		this.id = id;
	}
	
//	@PostConstruct
//	@SuppressWarnings("unused")
//	private void setRollbackOnly() {
//		try {
//			if (trans.getStatus() == STATUS_ACTIVE) {
//				trans.setRollbackOnly();
//			}
//		}
//		catch (IllegalStateException | SystemException e) {
//			throw new InternalError(e);
//		}
//	}
	
	public Object getId() {
		return id;
	}
}
