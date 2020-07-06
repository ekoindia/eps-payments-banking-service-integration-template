package in.eko.service.service;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientProperties;
import org.jboss.logging.Logger;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.TransactionBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.WebInstanceConstants;

public class CallbackService implements Runnable {

	private static Logger logger = Logger.getLogger(CallbackService.class);
	List<TransactionResponseView> callbackTransactionList;

	public CallbackService(List<TransactionResponseView> callbacklist) {
		super();
		this.callbackTransactionList = callbacklist;
	}

	public CallbackService() {
		super();
	}

	public boolean postCallbackRequest(TransactionResponseView request) {
		logger.info("Inside Post callbackrequest of CallbackService class ..");
		boolean result = false;
		try {
			Client client = ClientBuilder.newClient();
			client.property(ClientProperties.CONNECT_TIMEOUT, 4000);
			client.property(ClientProperties.READ_TIMEOUT, 4000);
			WebTarget baseTarget = client
					.target(StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.CALL_BACK_URL, WebInstanceConstants.GLOBAL))
							.getConfigValue() + "/" + request.getEkoTrxnId() + "/update");
			Response res = baseTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(request));

			if (res.getStatus() == Status.OK.getStatusCode()) {
				result = true;
			}
			logger.info("Callback response " + res.getStatus());

		} catch (Exception e) {
			e.printStackTrace();

		}
		return result;
	}

	public void notifySimplibank(List<TransactionResponseView> callbacklist) {
		logger.info("Inside notify simplibank block of CallbackService class...");
		callbacklist.forEach(request -> {
			boolean res = postCallbackRequest(request);
			if (res) {
				try {
					startTransaction();

					TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(request.getEkoTrxnId());
					if (transaction != null) {
						transaction.setCallbackStatus(TransactionConstant.CALLBACK_SUCCESS);
						transaction.save();
						HibernateDataAccess.commitTransaction();
					}
				} catch (Exception e) {
					e.printStackTrace();
					rollBackTransaction();
				} finally {
					HibernateDataAccess.closeSessionTL();
				}
			}

		});

	}

	public String getUniqueKey(String key, Integer source) {
		return key + "_" + source;
	}

	@Override
	public void run() {
		logger.info("Inside run method of CallbackService....");
		notifySimplibank(this.callbackTransactionList);

	}

	public void startTransaction() {
		HibernateDataAccess.getSessionTL();
		HibernateDataAccess.startTransaction();
	}

	public void rollBackTransaction() {
		HibernateDataAccess.rollbackTransaction();
		HibernateDataAccess.closeSessionTL();
		HibernateDataAccess.getSessionTL();
		HibernateDataAccess.startTransaction();
	}
}
