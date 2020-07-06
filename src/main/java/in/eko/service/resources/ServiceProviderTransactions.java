package in.eko.service.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.responseView.CallbackResponseView;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.service.TransactionFactory;
import in.eko.service.service.TransactionService;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.WebInstanceConstants;

@Path("finotransactions")
public class ServiceProviderTransactions implements BaseTransactions {

	TransactionService transactionService = new TransactionFactory().getTransactionFactory(WebInstanceConstants.DEFAULT);

	@Override
	public String welcome() {
		return "<h1>Welcome to EPS Payment Integration Service</h1><hr>" + " This application is for the use of "
				+ "authorized Eko only and by accessing this " + "application you hereby consent to the application "
				+ "being monitored by Eko. Any unauthorized use will be " + "considered a breach of Eko's Information Security policies and may "
				+ "also be unlawful under law. Eko reserves the right to take any action "
				+ "including disciplinary action or legal proceedings in a court of law "
				+ "against persons involved in the violation of the access restrictions herein.";
	}

	@Override
	public String getConfiguration(){
		return transactionService.getConfiguration();
	}

	@Override
	public String getMockConfiguration() {
		return transactionService.getMockConfiguration();
	}

	@Override
	public String getTaskConfiguration() {
		return transactionService.getTaskConfiguration();
	}

	@PUT
	@Path("/tasks/{taskId}/{action}")
	@Produces(MediaType.TEXT_HTML)
	@Override
	public String updateTaskStatus(@PathParam("action") Integer action, @PathParam("taskId") Integer taskId) {
		return transactionService.updateTaskStatus(action, taskId + "_" + WebInstanceConstants.DEFAILT_ID);
	}

	@Override
	public String changeMockResponseFlag(Integer flag) {
		String response = transactionService.changeMockResponseFlag(flag);
		return response;
	}

	@Override
	public String getServerLog(Integer line) {
		return transactionService.getServerLog(line);
	}

	@POST
	@Path("/mock/npciresponse/{mockcode}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	@Override
	public String changeMockResponseForTransactionNpciResponseCode(@PathParam("mockcode") String mockcode) {
		String response = transactionService.changeMockResponse(mockcode, ConfigurationConstant.MOCK_RESPONSE_FOR_TRANSACTION_RESPONSE_CODE);
		return response;
	}

	@Override
	public TransactionResponseView impsP2ARequest(TransactionRequestView trxnReq) {
		TransactionResponseView response = transactionService.sendMoney(trxnReq);
		return response;
	}

	@Override
	public TransactionResponseView sendMoney(TransactionRequestView trxnReq) {
		TransactionResponseView response = transactionService.sendMoney(trxnReq);
		return response;
	}

	@PUT
	@Path("callbackRequest/{tid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public CallbackResponseView callBackRequest(@PathParam("tid") String ekoTrxnId) {
		CallbackResponseView response = transactionService.doCallBackRequest(ekoTrxnId);
		return response;
	}

	@GET
	@Path("/pushFile/{fileName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String pushFile(@PathParam("fileName") String fileName) {
		return transactionService.pushFile(fileName);
	}

	@GET
	@Path("/pullFile/{fileName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String pullFile(@PathParam("fileName") String fileName) {
		return transactionService.pullFile(fileName);
	}

	@Override
	public TransactionResponseView transactionEnquiry(String tid) {
		TransactionResponseView response = transactionService.doTransactionEnquiry(tid);
		return response;
	}

	@Override
	public String reloadResponseCode(Integer source) {
		String response =transactionService.reloadResponseCode(source);
		return response;
	}

	@Override
	public String reloadConfiguration(Integer source) {
		String response =transactionService.reloadConfiguration(source);
		return response;
	}

}
