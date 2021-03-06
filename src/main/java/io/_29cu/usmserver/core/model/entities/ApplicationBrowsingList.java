package io._29cu.usmserver.core.model.entities;

import java.util.List;

import javax.persistence.*;

@MappedSuperclass
public class ApplicationBrowsingList {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appbrowselist_seq")
	@SequenceGenerator(name = "appbrowselist_seq", sequenceName = "appbrowselist_seq", allocationSize = 1)
	private Long id;

	@OneToMany
	List<Application> applications;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

}
