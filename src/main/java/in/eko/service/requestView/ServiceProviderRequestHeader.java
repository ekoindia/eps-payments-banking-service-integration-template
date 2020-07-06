package in.eko.service.requestView;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class ServiceProviderRequestHeader {

	String clientId;

	String authKey;

	private ServiceProviderRequestHeader() {
		super();
	}

	public ServiceProviderRequestHeader(String clientId, String authKey) {
		this.clientId = clientId;
		this.authKey = authKey;
	}

	@JsonProperty("ClientId")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@JsonProperty("AuthKey")
	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}
}
