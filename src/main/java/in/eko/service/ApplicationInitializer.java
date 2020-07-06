package in.eko.service;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.ConfigurationBO;
import in.eko.service.model.MockConfigBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskConfigurationBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.tasks.TaskSchedulerConstants;
import in.eko.service.tasks.TaskSchedulerService;
import in.eko.service.util.helper.StartupCache;

public class ApplicationInitializer
		implements ServletContextListener {

	private static Logger logger = Logger
			.getLogger(ApplicationInitializer.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		init(ctx);
	}

	public void init(final ServletContextEvent servletContextEvent) {
		try {
			synchronized (ApplicationInitializer.class) {
				ApplicationInitializer.initializeHibernate();
				ApplicationInitializer.initializeConfigurations();
				ApplicationInitializer.initializeMockConfig();
				ApplicationInitializer.initializeResponseCode();
				ApplicationInitializer.initializeTasks();
			}
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private static void initializeMockConfig() {

		List<MockConfigBO> config = TransactionPersistence
				.getInstance().getMockConfigurations();

		for (MockConfigBO conf : config) {
			StartupCache.getInstance().addToMockConfigMap(conf);
		}
	}

	private static void initializeConfigurations() {
		List<ConfigurationBO> config = TransactionPersistence.getInstance()
				.getConfigurations();

		for (ConfigurationBO conf : config) {
			StartupCache.getInstance().addToConfigurationMap(conf);
		}
	}

	public static void initializeHibernate() {
		logger.info("Initialize hibernate.....logger");

		HibernateDataAccess.buildSessionFactory("hibernate.cfg.xml");
	}

	private static void initializeResponseCode() {
		List<ResponseCodeBO> config = TransactionPersistence.getInstance()
				.getResponseCodes();
		for (ResponseCodeBO conf : config) {
			StartupCache.getInstance().addToResponseCodeMap(conf);
		}
	}

	private static void initializeTasks() {
		logger.info("Initialize initializeTasks.....");
		TaskSchedulerService.getInstance().initializeScheduler();
		List<TaskConfigurationBO> tasks = TransactionPersistence.getInstance().getAllTasks();
		for(TaskConfigurationBO task : tasks){
			StartupCache.getInstance().addToTaskConfigurationMap(task);
			if (task.getStatus().intValue() == TaskSchedulerConstants.ACTIVE) {
				JobDetail job = TaskSchedulerService.getInstance().getJobDetail(task.getTaskId());
				try {
					logger.info("Scheduling Task :"+task.getTaskId()+"\t Task Name : "+task.getTaskName());
					TaskSchedulerService.getInstance().scheduleJob(job, task);
				} catch (SchedulerException e) {
					logger.info("Unable to schedule job : " + task.getTaskName());
					e.printStackTrace();
				} 
			}
		}
	}
}
