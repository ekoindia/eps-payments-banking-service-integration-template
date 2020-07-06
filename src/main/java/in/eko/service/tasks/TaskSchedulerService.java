package in.eko.service.tasks;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import in.eko.service.model.TaskConfigurationBO;

public class TaskSchedulerService {

	private static Logger logger = Logger.getLogger(TaskSchedulerService.class);
	Scheduler scheduler;

	private static TaskSchedulerService taskSchedulerService = null;

	public TaskSchedulerService() {
		super();
	}

	public static TaskSchedulerService getInstance() {

		if (taskSchedulerService == null) {
			synchronized (TaskSchedulerService.class) {
				taskSchedulerService = new TaskSchedulerService();
			}
		}
		return taskSchedulerService;
	}

	public void initializeScheduler() {
		try {
			this.scheduler = new StdSchedulerFactory().getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			logger.info("Unable to get scheduler");
		}
	}

	public JobDetail getJobDetail(Integer jobId) {
		JobDetail job = null;
		switch (jobId) {

		case TaskSchedulerConstants.TRANSACTION_ENQUIRY_TAKS_ID:
			job = JobBuilder.newJob(ServiceProviderTransactionEnquiryTask.class).withIdentity("TransactionEnquiryTask", "group1").build();
			break;
		
		case TaskSchedulerConstants.PENDING_CALLBACK_ENQUIRY_TAKS_ID:
			job = JobBuilder.newJob(PendingCallbackRequestTask.class).withIdentity("PendingCallbackRequestTask", "group1").build();
			break;
		
		case TaskSchedulerConstants.TRANSACTION_REPOST_TAKS_ID:
			job = JobBuilder.newJob(ServiceProviderTransactionRepostingTask.class).withIdentity("TransactionRepostingTask", "group1").build();
			break;
			
		}

		return job;
	}

	public void scheduleJob(JobDetail jobDetail, TaskConfigurationBO taskConfiguration) throws SchedulerException {
		Trigger trigger = null;
		switch (taskConfiguration.getTaskId()) {
		
		case TaskSchedulerConstants.TRANSACTION_ENQUIRY_TAKS_ID:
			trigger = TriggerBuilder.newTrigger().withIdentity("TransactionEnquiryTask", "group1")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(taskConfiguration.getTaskInterval()).repeatForever())
					.build();
			this.scheduler.start();
			this.scheduler.scheduleJob(jobDetail, trigger);
			break;
			
		case TaskSchedulerConstants.PENDING_CALLBACK_ENQUIRY_TAKS_ID:
			trigger = TriggerBuilder.newTrigger().withIdentity("PendingCallbackRequestTask", "group1")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(taskConfiguration.getTaskInterval()).repeatForever())
					.build();
			this.scheduler.start();
			this.scheduler.scheduleJob(jobDetail, trigger);
			break;
			
		case TaskSchedulerConstants.TRANSACTION_REPOST_TAKS_ID:
			trigger = TriggerBuilder.newTrigger().withIdentity("TransactionRepostingTask", "group1")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(taskConfiguration.getTaskInterval()).repeatForever())
					.build();
			this.scheduler.start();
			this.scheduler.scheduleJob(jobDetail, trigger);
			break;
		}
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<String> getScheduledJobNames() {
		try {
			return scheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			logger.info("Exception while fetching job names");
			e.printStackTrace();
			return null;
		}

	}

}
