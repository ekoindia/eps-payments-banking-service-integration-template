
package in.eko.service.tasks;

public class TaskSchedulerConstants {

	public final static int TRANSACTION_ENQUIRY_TAKS_ID = 1001;
	public final static String TRANSACTION_ENQUIRY_TAKS_NAME = "TransactionEnquiryTask";

	public final static int PENDING_CALLBACK_ENQUIRY_TAKS_ID = 1002;
	public final static String PENDING_CALLBACK_ENQUIRY_TAKS_NAME = "PendingCallbackRequestTask";
	
	public final static int TRANSACTION_REPOST_TAKS_ID= 1003;
	public final static String TRANSACTION_REPOST_TAKS_NAME="TransactionRepostingTask";

	// status
	public final static int ACTIVE = 1;
	public final static int INACTIVE = 2;

	// task_history
	public final static int COMPLETED = 1;
	public final static int INPROGRESS = 2;
	public final static int EXCEPTION_OCCURED = 3;

}
