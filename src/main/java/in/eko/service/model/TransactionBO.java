/*
 * 
 */
package in.eko.service.model;

import java.util.Calendar;
import java.util.Date;

import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.util.helper.TransactionConstant;

public class TransactionBO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String ekoTrxnId;
	private Date txTime;
	private String requestType;
	private Integer transactionMode;
	private Double amount;
	private String remarks;
	private String trackingNumber;
	private Integer status;
	private int reconAttempt;
	private String responsecode;
	private String bankTrxnId;
	private Integer callbackStatus;
	private Integer source;
	private String utrNumber;
	private Date lastReconAt;
	
	//add service provider specific parameters here

	public TransactionBO() {
	}

	public TransactionBO(TransactionRequestView trxnReq, Integer source) {
		super();
		this.ekoTrxnId = trxnReq.getEkoTrxnId();
		this.txTime = new Date();
		this.requestType = trxnReq.getRequestType();
		this.amount = trxnReq.getAmount();
		this.remarks = trxnReq.getRemarks();
		this.source = source;
		this.status = TransactionConstant.REQUEST_READ_TIME_OUT;
		this.responsecode = TransactionConstant.READ_TIME_OUT;
		this.transactionMode = trxnReq.getTransactionMode();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEkoTrxnId() {
		return ekoTrxnId;
	}

	public void setEkoTrxnId(String ekoTrxnId) {
		this.ekoTrxnId = ekoTrxnId;
	}

	public Date getTxTime() {
		return txTime;
	}

	public void setTxTime(Date txTime) {
		this.txTime = txTime;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Integer getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(Integer transactionMode) {
		this.transactionMode = transactionMode;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getReconAttempt() {
		return reconAttempt;
	}

	public void setReconAttempt(int reconAttempt) {
		this.reconAttempt = reconAttempt;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getBankTrxnId() {
		return bankTrxnId;
	}

	public void setBankTrxnId(String bankTrxnId) {
		this.bankTrxnId = bankTrxnId;
	}

	public Integer getCallbackStatus() {
		return callbackStatus;
	}

	public void setCallbackStatus(Integer callbackStatus) {
		this.callbackStatus = callbackStatus;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	
	public String getUtrNumber() {
		return utrNumber;
	}

	public void setUtrNumber(String utrNumber) {
		this.utrNumber = utrNumber;
	}

	public Date getLastReconAt() {
		return lastReconAt;
	}

	public void setLastReconAt(Date lastReconAt) {
		this.lastReconAt = lastReconAt;
	}

	public boolean isSameMonthTrxn() {
		Calendar trxnDate = Calendar.getInstance();
		trxnDate.setTime(getTxTime());
		Calendar curDate = Calendar.getInstance();

		if (trxnDate.get(trxnDate.MONTH) == curDate.get(curDate.MONTH)
				&& trxnDate.get(trxnDate.YEAR) == curDate.get(curDate.YEAR)) {
			return true;
		}
		return false;
	}
	
	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}
}
