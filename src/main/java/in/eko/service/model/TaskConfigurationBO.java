/*
 * 
 */
package in.eko.service.model;

import in.eko.service.hibernate.HibernateDataAccess;

public class TaskConfigurationBO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer taskId;
	private String taskName;
	private Integer taskInterval;
	private Integer status;
	private Integer source;

	public TaskConfigurationBO() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Integer getTaskInterval() {
		return taskInterval;
	}

	public void setTaskInterval(Integer taskInterval) {
		this.taskInterval = taskInterval;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}
	
	public String getUniqueKey(){
		return this.getTaskId()+"_"+this.getSource();
	}
	
	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}
}
