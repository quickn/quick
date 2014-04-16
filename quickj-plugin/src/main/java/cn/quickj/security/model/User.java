package cn.quickj.security.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import cn.quickj.imexport.model.ExportSchema;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "USERS")
public class User implements Serializable {
	private static final long serialVersionUID = 4770769020304451578L;
	Integer id;
	Date lastLogin;
	Date deadLogin;
	String name;
	String userName;
	String password;
	String email;
	Set<Role> roles;
	Set<Group> groups;
	Set<ExportSchema> exportSchemas;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "users"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "USER_ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(name = "USER_NAME", length = 32)
	public String getUserName() {
		return userName;
	}

	@Column(name = "LAST_LOGIN")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastLogin() {
		return lastLogin;
	}

	@Column(name = "DEAD_LOGIN")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeadLogin() {
		return deadLogin;
	}

	@Column(name = "NAME", length = 64, nullable = false)
	public String getName() {
		return name;
	}

	@Column(name = "PASSWD", length = 32)
	public String getPassword() {
		return password;
	}

	@Column(name = "email", length = 128, nullable = true)
	public String getEmail() {
		return email;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Role.class)
	@JoinTable(name = "USER_HAS_ROLE", joinColumns = { @JoinColumn(name = "USER_ID", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID", updatable = false) })
	public Set<Role> getRoles() {
		return roles;
	}

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Group.class)
	@JoinTable(name = "USER_HAS_GROUP", joinColumns = { @JoinColumn(name = "USER_ID", updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "GROUP_ID", updatable = false) })
	public Set<Group> getGroups() {
		return groups;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = ExportSchema.class, mappedBy = "user")
	public Set<ExportSchema> getExportSchemas() {
		return exportSchemas;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void setDeadLogin(Date deadLogin) {
		this.deadLogin = deadLogin;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public void setExportSchemas(Set<ExportSchema> exportSchemas) {
		this.exportSchemas = exportSchemas;
	}
}
