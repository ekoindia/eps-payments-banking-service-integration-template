package in.eko.service.requestView;

import java.text.DecimalFormat;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequestView extends ApiRequestCommonView {

	// Possible values R-Request, E-enquiry

	private String requestType;

	private Double amount;

	private String remarks;


	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Double getAmount() {
		return Double.parseDouble(new DecimalFormat("#.##").format(amount));
	}

	public void setAmount(Double amount) {
		this.amount = Double.parseDouble(new DecimalFormat("#.##").format(amount));
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
}
