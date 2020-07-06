package in.eko.service.model;

public class ResponseCodeBO {
	Integer id;
	String responseCode;
	String description;
	String detailedDescription;
	String declineType;
	int status;
	int enquiryStatus;
	private Integer source;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDetailedDescription() {
		return detailedDescription;
	}
	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}
	public String getDeclineType() {
		return declineType;
	}
	public void setDeclineType(String declineType) {
		this.declineType = declineType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getEnquiryStatus() {
		return enquiryStatus;
	}
	public void setEnquiryStatus(int enquiryStatus) {
		this.enquiryStatus = enquiryStatus;
	}
	public Integer getSource() {
		return source;
	}
	public String getUniqueKey(){
		return this.responseCode+"_"+this.source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}
	
}
