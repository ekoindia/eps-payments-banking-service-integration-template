/**
 * 
 */
package in.eko.service.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.CallBackRequestBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskHistoryBO;
import in.eko.service.model.TransactionBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.service.CallbackService;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.WebInstanceConstants;

/**
 * @author Rohit Lakshykar
 *
 */
@DisallowConcurrentExecution
public class PendingCallbackRequestTask implements Job {

	private static Logger logger = Logger.getLogger(PendingCallbackRequestTask.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("PendingCallbackRequestTask task called...");
		try {
			startTransaction();
			TaskHistoryBO taskHistory = new TaskHistoryBO(TaskSchedulerConstants.PENDING_CALLBACK_ENQUIRY_TAKS_ID, new Date(),
					TaskSchedulerConstants.PENDING_CALLBACK_ENQUIRY_TAKS_NAME, TaskSchedulerConstants.INPROGRESS);
			taskHistory.save();
			HibernateDataAccess.commitTransaction();
			HibernateDataAccess.startTransaction();
			executeTask();
			startTransaction();
			taskHistory.setEndTime(new Date());
			taskHistory.setStatus(TaskSchedulerConstants.COMPLETED);
			taskHistory.save();
			HibernateDataAccess.commitTransaction();
			HibernateDataAccess.startTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateDataAccess.closeSessionTL();
		}
	}

	private void executeTask() {

		List<String> trxnids = new ArrayList<String>();
		List<String> tidsNotExists = new ArrayList<String>();

		markCallbackPendigTidForCallBack(tidsNotExists);

		List<String> callbacktid = TransactionPersistence.getInstance().getAllPendingCallbackTransaction();
		List<TransactionResponseView> callbacklist = new ArrayList<TransactionResponseView>();

		if (callbacktid != null && callbacktid.size() > 0) {
			trxnids.addAll(callbacktid);
		}

		logger.info("Total callback tid found for enquiry : " + (callbacktid != null ? callbacktid.size() : "No tids found for enquiry"));

		trxnids.forEach(ekoTrxnId -> {
			TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(ekoTrxnId);
			TransactionResponseView response = new TransactionResponseView();
			response.setStatus(transaction.getStatus());
			response.setEkoTrxnId(transaction.getEkoTrxnId());
			response.setTransactionDate(transaction.getTxTime());
			response.setResponseCode(transaction.getResponsecode());

			if (transaction.getTrackingNumber() != null) {
				response.setRrn(transaction.getTrackingNumber());
			} else {
				response.setRrn(null);
			}
			
			if (transaction.getUtrNumber() != null) {
				response.setUtrNumber(transaction.getUtrNumber());
			} else {
				response.setUtrNumber(null);
			}

			ResponseCodeBO responseCodeBo = StartupCache.getInstance()
					.getResponseCodeBoByCode(getUniqueKey(response.getResponseCode(), transaction.getSource()));

			if (responseCodeBo != null) {
				if (transaction.getSource().equals(WebInstanceConstants.DEFAILT_ID) && transaction.getTransactionMode().equals(TransactionConstant.NEFT)
						&& transaction.getResponsecode().equals(TransactionConstant.SUCCESS_RESPONSE_CODE)) {
					response.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
				} else {
					response.setStatus(transaction.getStatus());
				}
				response.setDescription(responseCodeBo.getDescription());
			} else {
				response.setDescription("Suspicious response code : " + response.getResponseCode());
				response.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
			}
			callbacklist.add(response);

		});

		if (tidsNotExists.size() > 0) {
			tidsNotExists.forEach(ekoTrxnId -> {
				TransactionResponseView response = new TransactionResponseView();
				response.setEkoTrxnId(ekoTrxnId);
				response.setStatus(TransactionConstant.FAIL);
				response.setDescription("Tid not found at ctrls");
				callbacklist.add(response);
			});
		}

		logger.info("Callback transaction list : " + callbacklist.size());
		if (callbacklist.size() > 0) {
			new Thread(new CallbackService(callbacklist)).start();
		}
	}

	private void markCallbackPendigTidForCallBack(List<String> tidsNotExists) {
		try {
			logger.info("Inside markCallbackPendigTidForCallBack method of PendingCallBackRequestTask...start");
			startTransaction();
			List<String> callbackRequest = TransactionPersistence.getInstance().getAllCallBackRequest();

			for (String ekoTrxnId : callbackRequest) {
				TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(ekoTrxnId);
				if (transaction != null) {
					try {
						if (transaction.getCallbackStatus() != TransactionConstant.CALLBACK_REQUIRED) {
							transaction.setCallbackStatus(TransactionConstant.CALLBACK_REQUIRED);
							transaction.save();
							HibernateDataAccess.commitTransaction();
							HibernateDataAccess.startTransaction();
						}
						List<CallBackRequestBO> callbacklist = TransactionPersistence.getInstance().getAllCallBackRequestByTidandStatus(ekoTrxnId,
								TransactionConstant.CALLBACK_REQUIRED);
						for (CallBackRequestBO callback : callbacklist) {
							callback.setStatus(TransactionConstant.CALLBACK_SUCCESS);
							callback.save();
							HibernateDataAccess.commitTransaction();
							HibernateDataAccess.startTransaction();
						}
					} catch (Exception e) {
						e.printStackTrace();
						rollBackTransaction();
					}
				} else {
					try {
						List<CallBackRequestBO> callbacklist = TransactionPersistence.getInstance().getAllCallBackRequestByTidandStatus(ekoTrxnId,
								TransactionConstant.CALLBACK_REQUIRED);
						for (CallBackRequestBO callback : callbacklist) {
							callback.setStatus(TransactionConstant.CALLBACK_SUCCESS);
							callback.save();
							HibernateDataAccess.commitTransaction();
							HibernateDataAccess.startTransaction();
						}
						tidsNotExists.add(ekoTrxnId);
					} catch (Exception e) {
						e.printStackTrace();
						rollBackTransaction();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateDataAccess.closeSessionTL();
		}
		logger.info("Inside markCallbackPendigTidForCallBack method of PendingCallBackRequestTask... End");
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

	public String getUniqueKey(String key, Integer source) {
		return key + "_" + source;
	}

}
