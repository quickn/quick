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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "GROUPS")
public class Group implements Serializable {

	private static final long serialVersionUID = 1253323333781L;
	Integer id;
	Group parent;
	String name;
	String description;
	Integer position;
	Set<Group> childrens;
	Set<Role> roles;
	Set<User> users;

	@Column(name = "DESCRIPTION", length = 128, nullable = true)
	public String getDescription() {
		return description;
	}

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "groups"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "GROUP_ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(name = "NAME", length = 64, nullable = false)
	public String getName() {
		return name;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = Group.class, mappedBy = "parent")
	@OrderBy("position desc")
	public Set<Group> getChildrens() {
		return childrens;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "PARENT_GROUP_ID")
	public Group getParent() {
		return parent;
	}

	@Column(name = "POSITION")
	public Integer getPosition() {
		return position;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Role.class)
	@JoinTable(name = "GROUP_HAS_ROLE", joinColumns = { @JoinColumn(name = "GROUP_ID", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", updatable = false) })
	public Set<Role> getRoles() {
		return roles;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = User.class)
	@JoinTable(name = "USER_HAS_GROUP", joinColumns = { @JoinColumn(name = "GROUP_ID", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "USER_ID", updatable = false) })
	public Set<User> getUsers() {
		return users;
	}

	public void setChildrens(Set<Group> childrens) {
		this.childrens = childrens;
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

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
