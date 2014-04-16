package cn.quickj.extui.action.bean;

import java.io.Serializable;
import java.util.Date;

public class SessionUser implements Serializable {

	private static final long serialVersionUID = 1888456666342L;
	Integer id;
	Date lastLogin;
	Date deadLogin;
	String name;
	String userName;
	String password;
	String email;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getDeadLogin() {
		return deadLogin;
	}

	public void setDeadLogin(Date deadLogin) {
		this.deadLogin = deadLogin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
