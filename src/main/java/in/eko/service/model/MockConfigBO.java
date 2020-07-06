package in.eko.service.model;

import in.eko.service.hibernate.HibernateDataAccess;

public class MockConfigBO {
	
	private int configId;   
	
	private String configKey;  
	
	private String configValue;
	
	private String description;
	
	private Integer source;

	public int getConfigId() {
		return configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}
	
	public String getUniqueKey(){
		return this.configKey+"_"+this.source;
	}
	
	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}

}
