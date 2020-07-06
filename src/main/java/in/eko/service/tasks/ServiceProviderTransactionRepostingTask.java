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
import in.eko.service.model.TaskHistoryBO;
import in.eko.service.model.TransactionBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.requestView.ServiceProviderTransactionRequestView;
import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.service.TransactionFactory;
import in.eko.service.service.TransactionService;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.TransactionType;
import in.eko.service.util.helper.WebInstanceConstants;


@DisallowConcurrentExecution
public class ServiceProviderTransactionRepostingTask extends TaskHelper implements Job {

	private static Logger logger = Logger.getLogger(ServiceProviderTransactionRepostingTask.class);
	private static Integer source = WebInstanceConstants.DEFAILT_ID;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Transaction reposting task called...");
		try {
			startTransaction();
			TaskHistoryBO taskHistory = new TaskHistoryBO(TaskSchedulerConstants.TRANSACTION_REPOST_TAKS_ID,
					new Date(), TaskSchedulerConstants.TRANSACTION_REPOST_TAKS_NAME,
					TaskSchedulerConstants.INPROGRESS);
			taskHistory.save();
			HibernateDataAccess.commitTransaction();
			HibernateDataAccess.startTransaction();

			if (canPostTransactions(source)) {
				executeTask();
			} else {
				logger.info("Non transaction hours running");
			}

			startTransaction();
			taskHistory.setEndTime(new Date());
			taskHistory.setStatus(TaskSchedulerConstants.COMPLETED);
			taskHistory.save();
			HibernateDataAccess.commitTransaction();
			HibernateDataAccess.startTransaction();

		} catch (Exception e) {

			logger.error("Exception : ", e);

		} finally {
			HibernateDataAccess.closeSessionTL();
		}
	}

	private void executeTask() {

		List<String> trxnids = new ArrayList<String>();

		List<String> tidsToRepost = TransactionPersistence.getInstance().getAllTransactionToRepost(getSource());

		if (tidsToRepost != null && tidsToRepost.size() > 0) {

			trxnids.addAll(tidsToRepost);

		}

		HibernateDataAccess.closeSessionTL();

		logger.info("Tids found to repost : " + trxnids.toString());

		trxnids.forEach(ekoTrxnId -> {

			startTransaction();

			TransactionService transactionService = new TransactionFactory()
					.getTransactionFactory(WebInstanceConstants.DEFAULT);

			logger.info("Will try to respost tid " + ekoTrxnId + "\t first perform tid enquiry");

			TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(ekoTrxnId);

			HibernateDataAccess.closeSessionTL();

			TransactionResponseView response = transactionService.doTransactionEnquiry(ekoTrxnId);

			if (response != null && response.getStatus().intValue() != TransactionConstant.ENQUIRY_FAILED
					&& response.getResponseCode() != null
					&& !response.getResponseCode().equalsIgnoreCase(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER)
					&& !response.getResponseCode().equalsIgnoreCase(TransactionConstant.READ_TIME_OUT)) {

				logger.info("Enquiry response before reposting transaction Response code : "
						+ response.getResponseCode() + "   Description : " + response.getDescription());

				if (response.getResponseCode().equalsIgnoreCase(TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER)) {

					if (!transaction.isSameMonthTrxn()) {

						logger.info("Different Month Transaction Ignoring ");

						transaction.setStatus(TransactionConstant.FAIL);

						transaction.setCallbackStatus(TransactionConstant.CALLBACK_REQUIRED);

						try {
							transaction.save();
							HibernateDataAccess.commitTransaction();
						} catch (Exception e) {
							logger.error("Execption : ", e);
							rollBackTransaction();
						} finally {

						}

					} else {
						TransactionRequestView requestView = formRequest(transaction);

						response = transactionService.repostTransaction(requestView);

						logger.info("Response received Response code : " + response.getResponseCode());
					}

				} else {

					startTransaction();

					updateTransactionData(transaction, response);

					transaction.setStatus(response.getStatus());

					transaction.setResponsecode(response.getResponseCode());

					transaction.setCallbackStatus(TransactionConstant.CALLBACK_REQUIRED);

					try {
						transaction.save();
						HibernateDataAccess.commitTransaction();
					} catch (Exception e) {
						logger.error("Exception : ", e);
						rollBackTransaction();
					} finally {

					}
				}
			}
			HibernateDataAccess.closeSessionTL();

		});

	}

	private TransactionRequestView formRequest(TransactionBO transaction) {
		TransactionRequestView requestView = new TransactionRequestView();
		requestView.setAmount(transaction.getAmount());
		requestView.setEkoTrxnId(transaction.getEkoTrxnId());
		requestView.setTransactionMode(transaction.getTransactionMode());
		requestView.setRequestTypeId(TransactionType.MONEY_TRANSFER_TXTYPEID);
		requestView.setRequestType(TransactionConstant.MONEY_TRANSFER_TYPE);
		return requestView;
	}

	public ServiceProviderTransactionRequestView formRequest(TransactionRequestView requestView) {
		ServiceProviderTransactionRequestView request = new ServiceProviderTransactionRequestView();
		request.setAmount(String.valueOf(requestView.getAmount()));
		request.setBeneName(requestView.getBeneficiaryName());
		request.setClientUniqueID(String.valueOf(requestView.getEkoTrxnId()));
		request.setCustomerMobileNo(requestView.getDepositorCell());
		request.setCustomerName(requestView.getDepositorName());
		
		return request;
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

	public String getUniqueKey(String key) {
		return key + "_" + getSource();
	}

	public Integer getSource() {
		return ServiceProviderTransactionRepostingTask.source;
	}

	private void updateTransactionData(TransactionBO transaction, TransactionResponseView response) {
		if (response != null) {
			if (transaction.getTrackingNumber() == null && response.getRrn() != null) {
				transaction.setTrackingNumber(response.getRrn());
			}
			if (transaction.getBankTrxnId() == null && response.getBankTrxnId() != null) {
				transaction.setBankTrxnId(response.getBankTrxnId());
			}
			if (transaction.getUtrNumber() == null
					&& (response.getUtrNumber() != null && response.getUtrNumber().trim().length() > 0)) {
				transaction.setUtrNumber(response.getUtrNumber());
			}
		}
	}
}
