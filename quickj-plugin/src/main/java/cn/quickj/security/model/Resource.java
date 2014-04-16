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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "RESOURCES")
public class Resource implements Serializable {

	private static final long serialVersionUID = 333333322342356L;
	Integer id;
	Resource parent;
	String name;
	String url;
	String action;
	Set<Resource> childrens;
	Set<Role> roles;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "resources"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "RES_ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(name = "NAME", length = 64, nullable = false)
	public String getName() {
		return name;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, mappedBy = "resources")
	public Set<Role> getRoles() {
		return roles;
	}

	@Column(name = "URL", length = 256, nullable = false)
	public String getUrl() {
		return url;
	}

	@Column(name = "ACTION", length = 256, nullable = true)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = Resource.class, mappedBy = "parent")
	public Set<Resource> getChildrens() {
		return childrens;
	}

	public void setChildrens(Set<Resource> childrens) {
		this.childrens = childrens;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "PARENT_ID")
	public Resource getParent() {
		return parent;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}

}
