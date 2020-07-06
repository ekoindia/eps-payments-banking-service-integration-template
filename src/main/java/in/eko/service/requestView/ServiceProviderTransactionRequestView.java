package in.eko.service.requestView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ServiceProviderTransactionRequestView {
	String clientUniqueID;
	String customerMobileNo;
	String beneIFSCCode;
	String beneAccountNo;
	String beneName;
	String amount;
	String customerName;
	String rfu1="";
	String rfu2="";
	String rfu3="";

	@JsonProperty("ClientUniqueID")
	public String getClientUniqueID() {
		return clientUniqueID;
	}

	public void setClientUniqueID(String clientUniqueID) {
		this.clientUniqueID = clientUniqueID;
	}

	@JsonProperty("CustomerMobileNo")
	public String getCustomerMobileNo() {
		return customerMobileNo;
	}

	public void setCustomerMobileNo(String customerMobileNo) {
		this.customerMobileNo = customerMobileNo;
	}

	@JsonProperty("BeneIFSCCode")
	public String getBeneIFSCCode() {
		return beneIFSCCode;
	}

	public void setBeneIFSCCode(String beneIFSCCode) {
		this.beneIFSCCode = beneIFSCCode;
	}

	@JsonProperty("BeneAccountNo")
	public String getBeneAccountNo() {
		return beneAccountNo;
	}

	public void setBeneAccountNo(String beneAccountNo) {
		this.beneAccountNo = beneAccountNo;
	}

	@JsonProperty("BeneName")
	public String getBeneName() {
		return beneName;
	}

	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}

	@JsonProperty("Amount")
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@JsonProperty("CustomerName")
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	@JsonProperty("RFU1")
	public String getRfu1() {
		return rfu1;
	}

	public void setRfu1(String rfu1) {
		this.rfu1 = rfu1;
	}

	@JsonProperty("RFU2")
	public String getRfu2() {
		return rfu2;
	}

	public void setRfu2(String rfu2) {
		this.rfu2 = rfu2;
	}

	@JsonProperty("RFU3")
	public String getRfu3() {
		return rfu3;
	}

	public void setRfu3(String rfu3) {
		this.rfu3 = rfu3;
	}

}
