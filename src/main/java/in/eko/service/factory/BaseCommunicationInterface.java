package in.eko.service.factory;

import in.eko.service.requestView.ApiRequestCommonView;

public interface BaseCommunicationInterface {

	public String postRequest(String request, Integer integer) throws Exception;
	
	public String postRequest(String request) throws Exception;
	
	public ApiRequestCommonView postRequest(ApiRequestCommonView request);
		
	public String getUniqueKey(String key);
	
	public Integer getSource();
}
