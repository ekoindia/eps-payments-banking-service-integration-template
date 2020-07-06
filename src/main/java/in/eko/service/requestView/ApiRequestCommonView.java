package in.eko.service.requestView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiRequestCommonView {

	private Integer requestTypeId;
	private String ekoTrxnId;
	private String depositorCell;
	private String depositorName;
	private String beneficiaryName;
	private Integer transactionMode;

	public Integer getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(Integer requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	public String getEkoTrxnId() {
		return ekoTrxnId;
	}

	public void setEkoTrxnId(String ekoTrxnId) {
		this.ekoTrxnId = ekoTrxnId;
	}

	public String getDepositorCell() {
		return depositorCell;
	}

	public void setDepositorCell(String depositorCell) {
		this.depositorCell = depositorCell;
	}

	public String getDepositorName() {
		return depositorName;
	}

	public void setDepositorName(String depositorName) {
		
		if(depositorName!=null) {
			this.depositorName = depositorName.replaceAll("\\P{L}", " ").replaceAll(" +", " ").trim();
		}else {
			this.depositorName = depositorName;			
		}
	}	
	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		
		if(beneficiaryName != null) {
			this.beneficiaryName = beneficiaryName.replaceAll("\\P{L}", " ").replaceAll(" +", " ").trim();
		}else {			
			this.beneficiaryName = beneficiaryName;
		}
	}

	public Integer getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(Integer transactionMode) {
		this.transactionMode = transactionMode;
	}
}
