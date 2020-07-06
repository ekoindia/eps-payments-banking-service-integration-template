package in.eko.service.persistence;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.CallBackRequestBO;
import in.eko.service.model.ConfigurationBO;
import in.eko.service.model.MockConfigBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskConfigurationBO;
import in.eko.service.model.TransactionBO;
import in.eko.service.tasks.TaskSchedulerConstants;
import in.eko.service.util.OperationHelper;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;

public class TransactionPersistence {

	public static TransactionPersistence transactionPersistence = new TransactionPersistence();

	public static TransactionPersistence getInstance() {
		return transactionPersistence;
	}

	public ConfigurationBO getConfigByConfigKeyAndSource(String configKey, Integer source) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ConfigurationBO.class);
		criteria.add(Restrictions.eq("configKey", configKey));
		criteria.add(Restrictions.eq("source", source));
		return (ConfigurationBO) criteria.uniqueResult();
	}

	public ConfigurationBO getConfigByConfigId(int configId) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ConfigurationBO.class);
		criteria.add(Restrictions.eq("configId", configId));
		return (ConfigurationBO) criteria.uniqueResult();
	}

	public List<ConfigurationBO> getConfigurations() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ConfigurationBO.class);
		return criteria.list();
	}

	public List<ConfigurationBO> getConfigurationsBySource(Integer source) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ConfigurationBO.class);
		criteria.add(Restrictions.eq("source", source));
		return criteria.list();
	}

	public List<MockConfigBO> getMockConfigurations() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(MockConfigBO.class);
		return criteria.list();
	}

	public List<ResponseCodeBO> getResponseCodes() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ResponseCodeBO.class);
		return criteria.list();
	}

	public TransactionBO getTransactionByEkoTrxnId(String ekoTrxnId) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.eq("ekoTrxnId", ekoTrxnId));
		return (TransactionBO) criteria.uniqueResult();
	}

	public TransactionBO getTransactionByEkoTrxnId(String ekoTrxnId, Integer source) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.eq("ekoTrxnId", ekoTrxnId));
		criteria.add(Restrictions.eq("source", source));
		return (TransactionBO) criteria.uniqueResult();
	}

	public List<String> getAllRnRTransactionExcludingTidsOfToday(Integer source, int days) {

		Double ignoreAmount = 1.0;

		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.or(Restrictions.eq("status", TransactionConstant.REQUEST_READ_TIME_OUT),
				Restrictions.eq("status", TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED)));
		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));

		criteria.add(Restrictions.or(Restrictions.eq("transactionMode", TransactionConstant.IMPS), Restrictions.isNull("transactionMode")));

		criteria.add(Restrictions.and(Restrictions.le("txTime", OperationHelper.getInstance().getCurdate()),
				Restrictions.ge("txTime", OperationHelper.getInstance().getDateOfBeforeXdays(days))));
		criteria.add(Restrictions.gt("amount", ignoreAmount));
		criteria.add(Restrictions.eq("source", source));
		criteria.setProjection(Projections.property("ekoTrxnId"));
		return criteria.list();
	}
	
	public List<String> getAllRnRTransactionFromXDays(Integer source, int days) {

		Double ignoreAmount = 1.0;

		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.or(Restrictions.eq("status", TransactionConstant.REQUEST_READ_TIME_OUT),
				Restrictions.eq("status", TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED)));
		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));

		criteria.add(Restrictions.or(Restrictions.eq("transactionMode", TransactionConstant.IMPS), Restrictions.isNull("transactionMode")));

		criteria.add(Restrictions.ge("txTime", OperationHelper.getInstance().getDateOfBeforeXdays(days)));
		criteria.add(Restrictions.gt("amount", ignoreAmount));
		criteria.add(Restrictions.eq("source", source));
		criteria.setProjection(Projections.property("ekoTrxnId"));
		return criteria.list();
	}
	

	public List<String> getAllNeftRnRTransactionExcludingTidsOfToday(Integer source, int days) {
		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };

		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);

		Integer status[] = { TransactionConstant.REQUEST_READ_TIME_OUT, TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED,
				TransactionConstant.SUCCESS };

		String[] successResponseCode = { "26", "S", "11" };

		criteria.add(Restrictions.and(Restrictions.in("status", status), Restrictions.not(Restrictions.in("responsecode", successResponseCode))));

		/*
		 * criteria.add(Restrictions.or(Restrictions.eq("status",
		 * TransactionConstant.REQUEST_READ_TIME_OUT), Restrictions.eq("status",
		 * TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED)));
		 */

		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));

		criteria.add(Restrictions.eq("transactionMode", TransactionConstant.NEFT));

		criteria.add(Restrictions.and(Restrictions.le("txTime", OperationHelper.getInstance().getCurdate()),
				Restrictions.ge("txTime", OperationHelper.getInstance().getDateOfBeforeXdays(days))));
		criteria.add(Restrictions.eq("source", source));
		criteria.setProjection(Projections.property("ekoTrxnId"));

		return criteria.list();
	}

	public List<String> getAllRnRTransactionIdsOfToday(Integer source) {

		Double ignoreAmount = 1.0;

		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };

		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);

		criteria.add(Restrictions.or(Restrictions.eq("status", TransactionConstant.REQUEST_READ_TIME_OUT),
				Restrictions.eq("status", TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED)));
		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));

		criteria.add(Restrictions.and(Restrictions.ge("txTime", OperationHelper.getInstance().getCurdate()),
				Restrictions.le("txTime", OperationHelper.getInstance().getDateBeforeXMinutes(new Date(), 2))));

		criteria.add(Restrictions.or(Restrictions.eq("transactionMode", TransactionConstant.IMPS), Restrictions.isNull("transactionMode")));

		criteria.add(Restrictions.le("reconAttempt", Integer.parseInt(
				StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.MAX_RECON_ATTMEPT, source)).getConfigValue())));

		// criteria.add(Restrictions.gt("amount", ignoreAmount));
		criteria.add(Restrictions.eq("source", source));
		criteria.setProjection(Projections.property("ekoTrxnId"));

		return criteria.list();
	}

	public List<String> getAllNeftTransactionsToEnquiryOfToday(Integer source) {

		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };

		Integer status[] = { TransactionConstant.REQUEST_READ_TIME_OUT, TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED,
				TransactionConstant.SUCCESS };

		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);

		String[] successResponseCode = { "26", "S", "11" };

		criteria.add(Restrictions.and(Restrictions.in("status", status), Restrictions.not(Restrictions.in("responsecode", successResponseCode))));

		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));

		criteria.add(Restrictions.or(Restrictions.eq("responsecode", TransactionConstant.READ_TIME_OUT),
				Restrictions.le("lastReconAt", OperationHelper.getInstance().getDateBeforeXMinutes(new Date(), 45))));

		// criteria.add(Restrictions.le("lastReconAt",
		// OperationHelper.getInstance().getDateBeforeXMinutes(new Date(), 45)));

		criteria.add(Restrictions.and(Restrictions.ge("txTime", OperationHelper.getInstance().getCurdate()),
				Restrictions.le("txTime", OperationHelper.getInstance().getDateBeforeXMinutes(new Date(), 30))));

		criteria.add(Restrictions.eq("transactionMode", TransactionConstant.NEFT));

		criteria.add(Restrictions.le("reconAttempt", Integer.parseInt(
				StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.MAX_RECON_ATTMEPT, source)).getConfigValue())));

		criteria.add(Restrictions.eq("source", source));

		criteria.setProjection(Projections.property("ekoTrxnId"));

		return criteria.list();
	}

	public List<TaskConfigurationBO> getAllActiveTasks() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TaskConfigurationBO.class);
		criteria.add(Restrictions.eq("status", TaskSchedulerConstants.ACTIVE));
		return criteria.list();
	}

	public List<TaskConfigurationBO> getAllTasks() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TaskConfigurationBO.class);
		return criteria.list();
	}

	public TaskConfigurationBO getTaskConfigurationByTaskId(Integer taskId) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TaskConfigurationBO.class);
		criteria.add(Restrictions.eq("taskId", taskId));
		return (TaskConfigurationBO) criteria.uniqueResult();
	}

	public List<String> getAllPendingCallbackTransaction() {
		String ignoreResponseCodes[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER };
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.and(Restrictions.le("txTime", OperationHelper.getInstance().getDateBeforeXMinutes(new Date(),5)),
				Restrictions.ge("txTime", OperationHelper.getInstance().getDateOfBeforeXdays(30))));
		criteria.add(Restrictions.not(Restrictions.in("responsecode", ignoreResponseCodes)));
		criteria.add(Restrictions.eq("callbackStatus", TransactionConstant.CALLBACK_REQUIRED));
		criteria.setProjection(Projections.property("ekoTrxnId"));
		return criteria.list();
	}

	public String getUniqueKey(String key, Integer source) {
		return key + "_" + source;
	}

	public List<String> getAllCallBackRequest() {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(CallBackRequestBO.class);
		criteria.add(Restrictions.and(Restrictions.le("requestAt", OperationHelper.getInstance().getDateBeforeXMinutes(new Date(), 5)),
				Restrictions.ge("requestAt", OperationHelper.getInstance().getDateOfBeforeXdays(3))));
		criteria.add(Restrictions.eq("status", TransactionConstant.CALLBACK_REQUIRED));
		criteria.setProjection(Projections.property("ekoTrxnId"));
		return criteria.list();
	}

	public List<CallBackRequestBO> getAllCallBackRequestByTidandStatus(String ekoTrxnId, Integer status) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(CallBackRequestBO.class);
		criteria.add(Restrictions.eq("status", status));
		criteria.add(Restrictions.eq("ekoTrxnId", ekoTrxnId));
		return criteria.list();
	}

	public List<String> getAllTransactionToRepost(Integer source) {
		String responsecodeToConsider[] = { TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER, TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER };
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(TransactionBO.class);
		criteria.add(Restrictions.in("responsecode", responsecodeToConsider));
		criteria.add(Restrictions.eq("status", TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED));
		criteria.add(Restrictions.eq("source", source));
		criteria.setProjection(Projections.property("ekoTrxnId"));
		return criteria.list();
	}

	public List<ResponseCodeBO> getResponseCodesBySource(Integer source) {
		Criteria criteria = HibernateDataAccess.getSessionTL().createCriteria(ResponseCodeBO.class);
		criteria.add(Restrictions.eq("source", source));
		return criteria.list();
	}
}
