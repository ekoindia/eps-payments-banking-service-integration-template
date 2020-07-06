package in.eko.service.responseView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class ServiceProviderResponseData {

	String actCode;
	String txnId;
	Double amountRequested;
	Double chargesDeducted;
	Double totalAmount;
	String beneName;
	String rfu1;
	String rfu2;
	String rfu3;
	String transactionDateTime;
	String txnDescription;
	

	@JsonProperty("ActCode")
	public String getActCode() {
		return actCode;
	}

	@JsonProperty("ActCode")
	public void setActCode(String actCode) {
		this.actCode = actCode;
	}

	@JsonProperty("TxnID")
	public String getTxnId() {
		return txnId;
	}

	@JsonProperty("TxnID")
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@JsonProperty("AmountRequested")
	public Double getAmountRequested() {
		return amountRequested;
	}

	@JsonProperty("AmountRequested")
	public void setAmountRequested(Double amountRequested) {
		this.amountRequested = amountRequested;
	}

	@JsonProperty("ChargesDeducted")
	public Double getChargesDeducted() {
		return chargesDeducted;
	}

	@JsonProperty("ChargesDeducted")
	public void setChargesDeducted(Double chargesDeducted) {
		this.chargesDeducted = chargesDeducted;
	}

	@JsonProperty("TotalAmount")
	public Double getTotalAmount() {
		return totalAmount;
	}

	@JsonProperty("TotalAmount")
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@JsonProperty("BeneName")
	public String getBeneName() {
		return beneName;
	}

	@JsonProperty("BeneName")
	public void setBeneName(String beneName) {
		this.beneName = beneName;
	}

	@JsonProperty("Rfu1")
	public String getRfu1() {
		return rfu1;
	}

	@JsonProperty("Rfu1")
	public void setRfu1(String rfu1) {
		this.rfu1 = rfu1;
	}

	@JsonProperty("Rfu2")
	public String getRfu2() {
		return rfu2;
	}

	@JsonProperty("Rfu2")
	public void setRfu2(String rfu2) {
		this.rfu2 = rfu2;
	}

	@JsonProperty("Rfu3")
	public String getRfu3() {
		return rfu3;
	}

	@JsonProperty("Rfu3")
	public void setRfu3(String rfu3) {
		this.rfu3 = rfu3;
	}

	@JsonProperty("TransactionDatetime")
	public String getTransactionDateTime() {
		return transactionDateTime;
	}

	@JsonProperty("TransactionDatetime")
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	@JsonProperty("TxnDescription")
	public String getTxnDescription() {
		return txnDescription;
	}

	@JsonProperty("TxnDescription")
	public void setTxnDescription(String txnDescription) {
		this.txnDescription = txnDescription;
	}
	
	

}
