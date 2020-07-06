/**
 * 
 */
package in.eko.service.service;

import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.responseView.CallbackResponseView;
import in.eko.service.responseView.TransactionResponseView;


public interface BaseTransactionInterface {

	public String getConfiguration();
	public String getMockConfiguration();
	public String getTaskConfiguration();
	public String getServerLog(Integer lines);
	public TransactionResponseView sendMoney(TransactionRequestView requestView) ;
	public TransactionResponseView analyzeResponse(TransactionResponseView responseView, String requestType);
	public String changeMockResponseFlag(Integer flag);
	public String changeMockResponse(String responseToset, String key);
	public CallbackResponseView doCallBackRequest(String ekoTrxnId);
	public String pushFile(String fileName);
	public String pullFile(String fileName);
	public String updateTaskStatus(Integer action, String taskId);
	public String getUniqueKey(String key);
	public TransactionResponseView doTransactionEnquiry(String tid);
	public TransactionResponseView repostTransaction(TransactionRequestView requestView);
	
	public String reloadResponseCode(Integer source);
	public String reloadConfiguration(Integer source);
	
}
