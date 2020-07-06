package in.eko.service.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import in.eko.service.model.ConfigurationBO;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;

public class TaskHelper {
	private static Logger taskHelperLogger = Logger.getLogger(TaskHelper.class);
	
	protected boolean canPostTransactions(int source) {
		ConfigurationBO transactionPostingHours = StartupCache.getInstance()
				.getConfigByKey(getUniqueKey(ConfigurationConstant.TRANSACTION_POSTING_HOURS, source));

		if (transactionPostingHours != null) {
			return isWithinTimeWindow(transactionPostingHours.getConfigValue());
		} else {
			this.taskHelperLogger.info("Time window configuration not found...");
			return true;
		}
	}
	
	private String getUniqueKey(String transactionPostingHours, int source) {
		return transactionPostingHours+"_"+source;
	}

	private boolean isWithinTimeWindow(String transactionTimeWindow) {

		try {
			if (checkTimeWindow(new Date(), transactionTimeWindow)) {
				return true;
			} else {
				this.taskHelperLogger.info("Transaction cant be send at this time valid time window : " + transactionTimeWindow);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.taskHelperLogger.info("Exception while validating time window : " + transactionTimeWindow);
			return false;
		}
	}

	private boolean checkTimeWindow(final Date currentDate, final String transactionTimeWindow) throws Exception {
		String[] timeWindowParams = transactionTimeWindow.split("_");
		SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf_HH = new SimpleDateFormat("HH");
		SimpleDateFormat sdf_MM = new SimpleDateFormat("mm");
		Date windowStartTime = sdf_HHmm.parse(timeWindowParams[0]);
		Date windowEndTime = sdf_HHmm.parse(timeWindowParams[1]);
		Date currentTime = sdf_HHmm.parse(sdf_HH.format(currentDate) + ":" + sdf_MM.format(currentDate));
		if (currentTime.after(windowStartTime) && currentTime.before(windowEndTime)) {
			return true;
		}
		return false;
	}

}
