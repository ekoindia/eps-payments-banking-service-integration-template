package in.eko.service.responseView;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TransactionResponseView extends ApiResponseCommonView {

	private String ekoTrxnId;

	private String responseCode;

	private String errorReason;

	private String rrn;

	private Date transactionDate;

	private String checkSum;

	private String utrNumber;

	public String getEkoTrxnId() {
		return ekoTrxnId;
	}

	public void setEkoTrxnId(String ekoTrxnId) {
		this.ekoTrxnId = ekoTrxnId;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

}
