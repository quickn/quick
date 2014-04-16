package cn.quickj.security.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ROLES")
public class Role implements Serializable {
	private static final long serialVersionUID = 1652366664456L;
	String description;
	Integer id;
	String name;
	Integer position;
	Set<Group> groups;
	Set<User> users;
	Set<Resource> resources;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "roles"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")	
	@Column(name = "ROLE_ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(name = "DESCRIPTION", length = 128, nullable = true)
	public String getDescription() {
		return description;
	}

	@Column(name = "NAME", length = 64, nullable = false)
	public String getName() {
		return name;
	}

	@Column(name = "POSITION")
	public Integer getPosition() {
		return position;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "roles")
	public Set<Group> getGroups() {
		return groups;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "roles")
	public Set<User> getUsers() {
		return users;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = Resource.class)
	@JoinTable(name = "ROLE_HAS_RESOURCE", joinColumns = { @JoinColumn(name = "ROLE_ID", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "RESOURCE_ID", updatable = false) })
	public Set<Resource> getResources() {
		return resources;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
}
