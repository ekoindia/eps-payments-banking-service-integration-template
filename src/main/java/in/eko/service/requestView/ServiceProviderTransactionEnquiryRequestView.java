package in.eko.service.requestView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ServiceProviderTransactionEnquiryRequestView {

	String clientUniqueId;

	@JsonProperty("ClientUniqueID")
	public String getClientUniqueId() {
		return clientUniqueId;
	}

	@JsonProperty("ClientUniqueID")
	public void setClientUniqueId(String clientUniqueId) {
		this.clientUniqueId = clientUniqueId;
	}
}
