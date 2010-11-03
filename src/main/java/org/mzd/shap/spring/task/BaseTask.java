/**
 *
 * Copyright 2010 Matthew Z DeMaere.
 * 
 * This file is part of SHAP.
 *
 * SHAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SHAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SHAP.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.mzd.shap.spring.task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.mzd.shap.exec.SimpleExecutable;

@Entity
@Table(name="Tasks")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TASK_TYPE",discriminatorType=DiscriminatorType.STRING)
public abstract class BaseTask implements Task {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="TASK_ID")
	private Integer id;
	@Version
	private Integer version;
	@Temporal(TemporalType.TIME)
	private Date start;
	@Temporal(TemporalType.TIME)
	private Date finish;
	@Enumerated(EnumType.STRING)
	@Index(name="tasks_status_jobid_index")
	@NotNull
	private Status status = Status.NEW;
	@Type(type="text")
	private String comment;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="JOB_ID")
	@Index(name="tasks_status_jobid_index")
	@NotNull
	private Job job;
	@Transient
	private SimpleExecutable executable;

	public void markStart() {
		setStart(new Date());
		setStatus(Status.STARTED);
	}
	
	public void markQueued() {
		setStatus(Status.QUEUED);
	}
	
	public void markFinish() {
		setFinish(new Date());
		setStatus(Status.DONE);
	}
	
	public void markError() {
		setFinish(new Date());
		setStatus(Status.ERROR);
	}
	
	public SimpleExecutable getExecutable() {
		return executable;
	}
	public void setExecutable(SimpleExecutable executable) {
		this.executable = executable;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(getId())
			.append(getVersion())
			.append(getStart())
			.append(getFinish())
			.append(getStatus())
			.append(getComment())
			.toString();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getFinish() {
		return finish;
	}
	public void setFinish(Date finish) {
		this.finish = finish;
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}

}
