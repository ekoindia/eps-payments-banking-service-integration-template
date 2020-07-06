
package in.eko.service.util.helper;

public class TransactionConstant {

	public final static String MONEY_TRANSFER_TYPE = "R";
	public final static String ENQUIRY_TYPE = "E";

	// application for all bank
	public final static int REQUEST_READ_TIME_OUT = -1;
	public final static int SUCCESS = 1;
	public final static int FAIL = 0;
	public final static int REQUEST_NOT_POSTED_TO_BANK = 2;
	public final static int SUSPICIOUS_RESPONSE_RECEIVED = 3;
	public final static int DUPLICATE_TID = 6;
	public final static int ENQUIRY_FAILED = 7;

	public final static String EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER = "ENPTSP";
	public final static String READ_TIME_OUT="ERTOUT";

	public final static String TID_NOT_FOUND_AT_SERVICE_PROVIDER = "100";

	// callback status constants
	public final static Integer CALLBACK_SUCCESS = 1;

	public final static Integer CALLBACK_REQUIRED = 2;

	// service provider integration end points
	public final static String MONEY_TRANSFER = "moneytransfer";

	public final static String TRANSACTION_ENQUIRY = "txnstatusrequest";

	public final static Integer IMPS = 1;

	public final static Integer NEFT = 0;

	public final static String REQUEST_SUCCESS = "0";

	public final static String REQUEST_FAIL = "1";

	public final static String SUCCESS_RESPONSE_CODE = "0";

	// file processing status
	// public final static int PROCESSED= 1;
	// public final static int PENDING=2;
	// public final static int SUSPICIOUS_RECORD=3;

	// public final static int FILE_TYPE_= 1;
	public final static String TX_SUCCESS = "SUCCESS";
	public final static String TX_REVERSED = "REVERSED";
	public final static String TX_PENDING = "PENDING";
	public final static String TX_REVERSAL_SUCCESS = "REVERSAL SUCCESS";

	public final static int REAL_TIME_CALLBACK = 1;

}
