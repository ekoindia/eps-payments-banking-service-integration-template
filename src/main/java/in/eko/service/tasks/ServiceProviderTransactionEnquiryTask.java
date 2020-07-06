/**
 * 
 */
package in.eko.service.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.TaskHistoryBO;
import in.eko.service.model.TransactionBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.service.CallbackService;
import in.eko.service.service.TransactionFactory;
import in.eko.service.service.TransactionService;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.WebInstanceConstants;

@DisallowConcurrentExecution
public class ServiceProviderTransactionEnquiryTask extends TaskHelper implements Job {

	private static Logger logger = Logger.getLogger(ServiceProviderTransactionEnquiryTask.class);
	
	private static int requeryDay = 0;
	
	private static Integer source = WebInstanceConstants.DEFAILT_ID;
	
	private static ZonedDateTime lastEnqyiryTime = ZonedDateTime.now();
	

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("FinoTransactionEnquiryTask enquiry task called...");
		try {

			startTransaction();
			TaskHistoryBO taskHistory = new TaskHistoryBO(TaskSchedulerConstants.TRANSACTION_ENQUIRY_TAKS_ID, new Date(),
					TaskSchedulerConstants.TRANSACTION_ENQUIRY_TAKS_NAME, TaskSchedulerConstants.INPROGRESS);
			taskHistory.save();
			HibernateDataAccess.commitTransaction();
			HibernateDataAccess.startTransaction();

			if(canPostTransactions(source)) {
				executeTask();				
			}else {
				logger.info("Non transaction hours running");
			}

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

		startTransaction();

		List<String> trxnids = new ArrayList<String>();
		
		List<String> neftTrxnIds = new ArrayList<String>();

		List<String> currentDayTransactions = TransactionPersistence.getInstance().getAllRnRTransactionIdsOfToday(getSource());
		
		List<TransactionResponseView> callbacklist = new ArrayList<TransactionResponseView>();

		if (currentDayTransactions != null && currentDayTransactions.size() > 0) {
			trxnids.addAll(currentDayTransactions);
		}

		logger.info("Total transaction found for enquiry : "
				+ (currentDayTransactions != null ? currentDayTransactions.size() + "\nTids : " + trxnids.toString() : "No tids found for enquiry"));

		neftTrxnIds = TransactionPersistence.getInstance().getAllNeftTransactionsToEnquiryOfToday(getSource());

		logger.info("Total NEFT transaction found for enquiry : "
				+ (neftTrxnIds != null ? neftTrxnIds.size() + "\nTids : " + neftTrxnIds.toString() : "No tids found for enquiry"));
		
		
		if (!neftTrxnIds.isEmpty()) {
			trxnids.addAll(neftTrxnIds);
		}

		trxnids = getOldRnRTransactions(trxnids, currentDayTransactions);

		HibernateDataAccess.closeSessionTL();
		
		trxnids.forEach(ekoTrxnId -> {
			startTransaction();
			TransactionService transactionService = new TransactionFactory().getTransactionFactory(WebInstanceConstants.DEFAULT);

			TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(ekoTrxnId);

			TransactionResponseView response = transactionService.doTransactionEnquiry(ekoTrxnId);
			transaction.setReconAttempt(transaction.getReconAttempt() + 1);
			transaction.setLastReconAt(new Date());
			updateTransactionData(transaction, response);

			if (response != null && response.getStatus().intValue() != TransactionConstant.ENQUIRY_FAILED && response.getResponseCode() != null
					&& !response.getResponseCode().trim().equalsIgnoreCase(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER)
					&& !response.getResponseCode().trim().equalsIgnoreCase(TransactionConstant.READ_TIME_OUT)
					&& (transaction.getStatus().intValue() != response.getStatus().intValue() || !response.getResponseCode().trim().equalsIgnoreCase(transaction.getResponsecode()))) {
				logger.info("old Response : " + transaction.getResponsecode() + "\t New Response Code : " + response.getResponseCode());

				transaction.setResponsecode(response.getResponseCode());
				transaction.setStatus(response.getStatus());
				transaction.setCallbackStatus(TransactionConstant.CALLBACK_REQUIRED);
				callbacklist.add(response);
			}

			try {
				transaction.save();
				HibernateDataAccess.commitTransaction();
				HibernateDataAccess.startTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				rollBackTransaction();
			}
			
			HibernateDataAccess.closeSessionTL();
		});

		

		logger.info("Callback transaction list : " + callbacklist.size());
		if (callbacklist.size() > 0) {
			new Thread(new CallbackService(callbacklist)).start();
		}
	}

	private void updateTransactionData(TransactionBO transaction, TransactionResponseView response) {
		if (response != null) {
			if (transaction.getTrackingNumber() == null && (response.getRrn() != null && response.getRrn().trim().length() > 0)) {
				transaction.setTrackingNumber(response.getRrn());
			}
			if (transaction.getBankTrxnId() == null && (response.getBankTrxnId() != null && response.getBankTrxnId().trim().length() > 0)) {
				transaction.setBankTrxnId(response.getBankTrxnId());
			}
			if(transaction.getUtrNumber() == null && (response.getUtrNumber() !=null && response.getUtrNumber().trim().length() > 0)) {
				transaction.setUtrNumber(response.getUtrNumber());
			}
		}
	}

	private List<String> getOldRnRTransactions(List<String> trxnids, List<String> currentDayTransactions) {
		List<String> Oldtransactions;
		List<String> OldNefttransactions;

		LocalDateTime today = LocalDateTime.now();

		int eodHour = Integer.parseInt(
				StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.EOD_RECON_HOUR, getSource())).getConfigValue());

		long enquiryInterval = Long.parseLong(StartupCache.getInstance()
				.getConfigByKey(getUniqueKey(ConfigurationConstant.OLD_TRXN_RECON_FREQUENCY_IN_MIN, getSource())).getConfigValue());

		ZonedDateTime newDate = ZonedDateTime.now();

		Duration duration = Duration.between(lastEnqyiryTime, newDate);

		logger.info("Old Time " + lastEnqyiryTime + "\t New Time : " + newDate + "\tTotal Diff in min : " + duration.toMinutes()
				+ " \t Diff in sec : " + duration.toMillis());

		if (duration.toMinutes() >= enquiryInterval) {
			requeryDay = today.getDayOfMonth();
			lastEnqyiryTime = ZonedDateTime.now();

			int pickTransactionDaysBefore = Integer.parseInt(StartupCache.getInstance()
					.getConfigByKey(getUniqueKey(ConfigurationConstant.MAX_DAYS_FOR_RECON, WebInstanceConstants.GLOBAL)).getConfigValue());

			logger.info("inside getting old transactions....");

			Oldtransactions = TransactionPersistence.getInstance().getAllRnRTransactionFromXDays(getSource(), pickTransactionDaysBefore);

			OldNefttransactions = TransactionPersistence.getInstance().getAllNeftRnRTransactionExcludingTidsOfToday(getSource(),
					pickTransactionDaysBefore);

			if (Oldtransactions != null && Oldtransactions.size() > 0) {
				logger.info("Old Transactions found : " + Oldtransactions.size());
				trxnids.addAll(Oldtransactions);
				Set<String> hs = new HashSet();
				hs.addAll(trxnids);
				trxnids.clear();
				trxnids.addAll(hs);
			}

			if (OldNefttransactions != null && OldNefttransactions.size() > 0) {
				logger.info("Old Transactions found : " + OldNefttransactions.size());
				trxnids.addAll(OldNefttransactions);
				Set<String> hs = new HashSet();
				hs.addAll(trxnids);
				trxnids.clear();
				trxnids.addAll(hs);
			}

		}
		return trxnids;
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

	public Integer getSource() {
		return ServiceProviderTransactionEnquiryTask.source;
	}
}
