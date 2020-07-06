package in.eko.service.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.ConfigurationBO;
import in.eko.service.model.MockConfigBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskConfigurationBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.tasks.TaskSchedulerConstants;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;

public abstract class TransactionService implements BaseTransactionInterface {

	private static Logger logger = Logger.getLogger(TransactionService.class);

	public String getConfiguration() {

		String out = "<html><body><br><table align='center' border='1' cellpadding='10' cellspacing='10' width='70%'><th>Config Name</th><th>Config Value</th>";
		Map<String, ConfigurationBO> map = StartupCache.getInstance().getConfigurationMap();

		for (Map.Entry<String, ConfigurationBO> entry : map.entrySet()) {
			out += "<tr><td>" + entry.getValue().getConfigKey() + "</td><td>" + entry.getValue().getConfigValue() + "</td></tr>";
		}
		out += "</table></body></html>";
		return out;
	}

	public String getMockConfiguration() {

		String out = "<html><body><br><h3>AXIS Mock Responses</h3><hr><table align='center' border='1' cellpadding='10' cellspacing='10' width='70%'><th>Config Name</th><th>Config Value</th>";
		Map<String, MockConfigBO> map = StartupCache.getInstance().getMockConfigMap();

		for (Map.Entry<String, MockConfigBO> entry : map.entrySet()) {
			out += "<tr><td>" + entry.getValue().getConfigKey() + "</td><td>" + entry.getValue().getConfigValue() + "</td></tr>";
		}
		out += "</table></body></html>";
		return out;
	}

	public String getTaskConfiguration() {
		Map<String, TaskConfigurationBO> map = StartupCache.getInstance().getTaskConfigurationMap();
		String out = "<html><body><br><h3>Task Configuration</h3><hr><table align='center' border='1' cellpadding='10' cellspacing='10' width='70%'><th>Task Id</th><th>Task Name</th><th>Interval</th><th>Status</th>";

		for (Map.Entry<String, TaskConfigurationBO> entry : map.entrySet()) {
			out += "<tr><td>" + entry.getValue().getTaskId() + "</td><td>" + entry.getValue().getTaskName() + "</td><td>"
					+ entry.getValue().getTaskInterval() + "</td><td>"
					+ (entry.getValue().getStatus().intValue() == TaskSchedulerConstants.ACTIVE ? "Active" : "Inactive") + "</td></tr>";
		}
		out += "</table></body></html>";
		return out;
	}

	public String getServerLog(Integer lines) {
		String line = "";
		int maxLineToTech = 10;

		try {
			String strpath = StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.SERVER_LOG_FILE_PATH)).getConfigValue();
			ReversedLinesFileReader fr = new ReversedLinesFileReader(new File(strpath), Charset.forName("UTF-8"));
			if (lines != null) {
				maxLineToTech = lines;
			}
			int count = 0;
			String ch;
			do {
				count++;
				ch = fr.readLine();
				ch += "\n" + line;
				line = ch;
			} while (maxLineToTech >= count && ch != null);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			line = "Exception occured while accessing log file";
		}
		return line;

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

	public void commitTransaction() {
		HibernateDataAccess.commitTransaction();
	}

	public void closeHibernateSession() {
		HibernateDataAccess.closeSessionTL();
	}

	public String reloadResponseCode(Integer source) {

		logger.info("Inside reloadResponseCode.....Source : " + source);

		String response = "Response code has been reloaded...";

		try {
			List<ResponseCodeBO> config = TransactionPersistence.getInstance().getResponseCodesBySource(source);
			for (ResponseCodeBO conf : config) {
				logger.info("Response Code :"+conf.getResponseCode());
				StartupCache.getInstance().addToResponseCodeMap(conf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = "Exception while reloading response code for source : " + source;
		}

		return response;
	}

	public String reloadConfiguration(Integer source) {

		logger.info("Inside reloadConfiguration.....Source : " + source);

		String response = "Configuration code has been reloaded...";

		try {
			List<ConfigurationBO> config = TransactionPersistence.getInstance().getConfigurationsBySource(source);

			for (ConfigurationBO conf : config) {
				logger.info("Configuration Config Key : "+conf.getConfigKey()+"\tConfig Value : "+conf.getConfigValue());
				StartupCache.getInstance().addToConfigurationMap(conf);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response = "Exception while reloading Configuration for source : " + source;
		}

		return response;
	}

}
