/*
 * 
 */
package in.eko.service.model;

import java.util.Date;

import in.eko.service.hibernate.HibernateDataAccess;

public class TaskHistoryBO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer taskId;
	private Date startTime;
	private Date endTime;
	private String taskName;
	private Integer status;

	
	public TaskHistoryBO() {
		super();
	}

	public TaskHistoryBO(Integer taskId, Date startTime, String taskName, Integer status) {
		super();
		this.taskId = taskId;
		this.startTime = startTime;
		this.taskName = taskName;
		this.status = status;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void save() throws Exception {
		new HibernateDataAccess().save(this);
	}
}
