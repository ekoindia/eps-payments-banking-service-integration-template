/*
 * 
 */
package in.eko.service.model;

import java.util.Date;

import in.eko.service.hibernate.HibernateDataAccess;

public class MessageLogBO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String ekoTrxnId;
	private Integer typeId;
	private String request;
	private Date requestedAt;
	private String response;
	private Date responseReceivedAt;
	private String actCode;

	public MessageLogBO() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public String getRequest() {
		return this.request;
	}

	public void setRequest(final String request) {
		this.request = request;
	}

	public Date getRequestedAt() {
		return this.requestedAt;
	}

	public void setRequestedAt(final Date requestedAt) {
		this.requestedAt = requestedAt;
	}

	public String getResponse() {
		return this.response;
	}

	public void setResponse(final String response) {
		this.response = response;
	}

	public Date getResponseReceivedAt() {
		return this.responseReceivedAt;
	}

	public void setResponseReceivedAt(final Date responseReceivedAt) {
		this.responseReceivedAt = responseReceivedAt;
	}

	public String getActCode() {
		return this.actCode;
	}

	public void setActCode(final String actCode) {
		this.actCode = actCode;
	}

	public String getEkoTrxnId() {
		return this.ekoTrxnId;
	}

	public void setEkoTrxnId(final String ekoTrxnId) {
		this.ekoTrxnId = ekoTrxnId;
	}

	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}
}
