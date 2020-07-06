package in.eko.service.responseView;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ApiResponseCommonView {
	
	String description;
	Integer status;
	String bankTrxnId;
	Integer realTimeResponse=0;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getBankTrxnId() {
		return bankTrxnId;
	}

	public void setBankTrxnId(String bankTrxnId) {
		this.bankTrxnId = bankTrxnId;
	}

	public Integer getRealTimeResponse() {
		return realTimeResponse;
	}

	public void setRealTimeResponse(Integer realTimeResponse) {
		this.realTimeResponse = realTimeResponse;
	}
	
}
