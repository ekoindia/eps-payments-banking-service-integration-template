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


public interface BaseTransactions {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String welcome();
	
	@GET
	@Path("/configuration")
	@Produces(MediaType.TEXT_HTML)
	public String getConfiguration();

	@GET
	@Path("/serverlog/{lines}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServerLog(@PathParam("lines") Integer line);
	
	@GET
	@Path("/mock")
	@Produces(MediaType.TEXT_HTML)
	public String getMockConfiguration();
	
	@POST
	@Path("/mock/{enabledisableflag}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String changeMockResponseFlag(@PathParam("enabledisableflag") Integer flag);
	
	@POST
	@Path("/mock/npciresponse/{mockcode}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_HTML)
	public String changeMockResponseForTransactionNpciResponseCode(@PathParam("mockcode") String mockcode);
	
	@GET
	@Path("/tasks")
	@Produces(MediaType.TEXT_HTML)
	public String getTaskConfiguration();
	
	@PUT
	@Path("/tasks/{taskId}/{action}")
	@Produces(MediaType.TEXT_HTML)
	public String updateTaskStatus(@PathParam("action") Integer action, @PathParam("taskId") Integer taskId);
	
	@POST
	@Path("/moneyTransfer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TransactionResponseView sendMoney(TransactionRequestView trxnReq);
	
	@POST
	@Path("/transactionEnquiry/{tid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TransactionResponseView transactionEnquiry(@PathParam("tid") String tid);
	
	@POST
	@Path("/impsP2ARequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TransactionResponseView impsP2ARequest(TransactionRequestView trxnReq);
	
	
	
	@PUT
	@Path("/callbackRequest/{tid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public CallbackResponseView callBackRequest(@PathParam("tid") String ekoTrxnId);
	
	@PUT
	@Path("/reload/responsecode/{source}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String reloadResponseCode(@PathParam("source") Integer source);
	
	@PUT
	@Path("/reload/configuraton/{source}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String reloadConfiguration(@PathParam("source") Integer source);
	
}
