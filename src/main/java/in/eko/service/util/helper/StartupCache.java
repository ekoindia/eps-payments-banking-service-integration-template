package in.eko.service.util.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import in.eko.service.model.ConfigurationBO;
import in.eko.service.model.MockConfigBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskConfigurationBO;

public class StartupCache {
	
	private Map<String, ConfigurationBO> configurationMap = new HashMap<String, ConfigurationBO>();
	private Map<String, MockConfigBO> mockConfigMap = new HashMap<String, MockConfigBO>();
	private Map<String, ResponseCodeBO> responseCodeMap = new HashMap<String, ResponseCodeBO>();
	private Map<String, TaskConfigurationBO> taskConfigurationMap = new HashMap<String, TaskConfigurationBO>();
	
	public static ConcurrentHashMap<Integer, Integer> transactionInProgress = new ConcurrentHashMap<Integer, Integer>();
	public static ConcurrentLinkedDeque<Integer> callBackRequestQueue = new ConcurrentLinkedDeque<Integer>();

	public static StartupCache startupCache = null;
	
	public static StartupCache getInstance(){
		if(startupCache == null){
			synchronized (StartupCache.class) {
				startupCache = new StartupCache();
			}
		}
		return startupCache;
	}

	public void addToConfigurationMap(ConfigurationBO config) {
		if ((config != null)/* && (configurationMap.get(config.getConfigId()) == null)*/){
			configurationMap.put(config.getUniqueKey(), config);			
		}
	}

	public ConfigurationBO getConfigByKey(String key) {
		ConfigurationBO config = configurationMap.get(key);
		return config;
	}

	public  Map<String, ConfigurationBO> getConfigurationMap() {
		return configurationMap;
	}

	
	//methods for mock configuration
	public void addToMockConfigMap(MockConfigBO config) {
		if ((config != null) /*&& (mockConfigMap.get(config.getConfigId()) == null)*/){
			mockConfigMap.put(config.getUniqueKey(), config);			
		}
	}

	public MockConfigBO getMockConfigByKey(String key) {
		MockConfigBO config = mockConfigMap.get(key);
		return config;
	}

	public  Map<String, MockConfigBO> getMockConfigMap() {
		return mockConfigMap;
	}
	
	public void addToResponseCodeMap(ResponseCodeBO config) {
		if ((config != null) /*&& (responseCodeMap.get(config.getResponseCode()) == null)*/){
			responseCodeMap.put(config.getUniqueKey(), config);			
		}
	}

	public ResponseCodeBO getResponseCodeBoByCode(String responseCode) {
		ResponseCodeBO config = responseCodeMap.get(responseCode);
		return config;
	}

	public  Map<String, ResponseCodeBO> getResponseCodeMap() {
		return responseCodeMap;
	}
	
	public void addToTaskConfigurationMap(TaskConfigurationBO config) {
		if ((config != null) /*&& (taskConfigurationMap.get(config.getTaskId()) == null)*/){
			taskConfigurationMap.put(config.getUniqueKey(), config);			
		}
	}

	public TaskConfigurationBO getTaskByTaskId(String key) {
		TaskConfigurationBO config = taskConfigurationMap.get(key);
		return config;
	}

	public  Map<String, TaskConfigurationBO> getTaskConfigurationMap() {
		return taskConfigurationMap;
	}
}
