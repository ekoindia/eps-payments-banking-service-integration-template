/*
 * 
 */
package in.eko.service.model;

import java.util.Date;

import in.eko.service.hibernate.HibernateDataAccess;


public class CallBackRequestBO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String ekoTrxnId;
	private Date requestAt;
	private Integer source;
	private Integer status;

	
	public CallBackRequestBO() {
		super();
	}

	public CallBackRequestBO(String ekoTrxnId, Date date, Integer callbackstatus, Integer source) {
		this.ekoTrxnId = ekoTrxnId;
		this.requestAt = date;
		this.status = callbackstatus;
		this.source = source;
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

	public Date getRequestAt() {
		return requestAt;
	}

	public void setRequestAt(Date requestAt) {
		this.requestAt = requestAt;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}
}
