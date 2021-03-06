/**
 * Copyright 2016 - 29cu.io and the authors of alpha-umi open source project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io._29cu.usmserver.core.model.entities;

import io._29cu.usmserver.core.model.enumerations.AppState;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class ApplicationBundle {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appbundle_seq")
	@SequenceGenerator(name = "appbundle_seq", sequenceName = "appbundle_seq", allocationSize = 1)
	private Long id;
	@NotNull
	private String name;
	@NotNull
	private String description;
	@NotNull
	private AppState state;
	@ManyToOne
	private User developer;
	@ManyToOne
	private Category category;
	@OneToMany
	private List<Application> applications;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppState getState() {
		return state;
	}

	public void setState(AppState state) {
		this.state = state;
	}

	public User getDeveloper() {
		return developer;
	}

	public void setDeveloper(User developer) {
		this.developer = developer;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application>  applications) {
		this.applications = applications;
	}

	public void addApplication(Application app) {
		this.applications.add(app);
	}

	public void removeApplication(Application app) {
		this.applications.remove(app);		//to find the application from list and then remove it.
	}
}
