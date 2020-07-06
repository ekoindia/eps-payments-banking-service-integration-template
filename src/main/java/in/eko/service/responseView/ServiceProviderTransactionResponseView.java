package in.eko.service.responseView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class ServiceProviderTransactionResponseView {

	String requestID;
	String responseCode;
	String messageString;
	String displayMessage;
	String clientUniqueID;
	String responseData;
	
	
	@JsonProperty("RequestID")
	public String getRequestID() {
		return requestID;
	}

	@JsonProperty("RequestID")
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	@JsonProperty("ResponseCode")
	public String getResponseCode() {
		return responseCode;
	}

	@JsonProperty("ResponseCode")
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@JsonProperty("MessageString")
	public String getMessageString() {
		return messageString;
	}

	@JsonProperty("MessageString")
	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}
	
	@JsonProperty("DisplayMessage")
	public String getDisplayMessage() {
		return displayMessage;
	}

	@JsonProperty("DisplayMessage")
	public void setDisplayMessage(String dispalyMessage) {
		this.displayMessage = dispalyMessage;
	}

	@JsonProperty("ClientUniqueID")
	public String getClientUniqueID() {
		return clientUniqueID;
	}

	@JsonProperty("ClientUniqueID")
	public void setClientUniqueID(String clientUniqueID) {
		this.clientUniqueID = clientUniqueID;
	}

	@JsonProperty("ResponseData")
	public String getResponseData() {
		return responseData;
	}

	@JsonProperty("ResponseData")
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

}
