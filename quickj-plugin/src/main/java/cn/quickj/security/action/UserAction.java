package cn.quickj.security.action;

import java.util.Collection;
import java.util.Set;

import cn.quickj.security.model.Group;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;
import cn.quickj.security.service.GroupService;
import cn.quickj.security.service.RoleService;
import cn.quickj.security.service.UserService;
import cn.quickj.simpleui.action.SimpleUIActionSupport;

import com.google.inject.Inject;

public class UserAction extends SimpleUIActionSupport {
	final public static String USER_IN_SESSION_KEY = "QUICKJ_USER_BEAN";
	@Inject
	private User user;
	@Inject
	UserService userService;
	private Collection<User> users;
	private String ids;
	private Collection<Group> groups;

	@Inject
	GroupService groupService;
	private Collection<Role> roles;
    private Set<Role> seletedRoles;
	@Inject
	RoleService roleService;
	private String groupids;
	private String roleids;

	/**
	 * 列出用户列表。
	 */
	public void index() {
		users = userService.findAllUser(getPaginate());
		render("user/list.html");
	}
	public void ajax(){
		users = userService.findAllUser(getPaginate());
		render("user/ajax_list.html");
	}

	/**
	 * 根据User Example查找user.
	 */
	public void list() {
		users = userService.findUserByExample(user);
		render("user/list.html");
	}

	/**
	 * 新建一个用户
	 */
	public void create() {
		setGroups(groupService.findAllGroup(getPaginate()));
		setRoles(roleService.findAllRole(getPaginate()));
		render("user/ajax_form.html");
	}

	/**
	 * 准备编辑一个用户。
	 * 
	 * @param id
	 */
	public void edit(String id) {
		setGroups(groupService.findAllGroup(getPaginate()));
		setRoles(roleService.findAllRole(getPaginate()));
		user = userService.getUser(Integer.parseInt(id));
		seletedRoles = user.getRoles();
		render("user/ajax_form.html");
	}

	/**
	 * 新增或者修改后保存。
	 */
	public void save() {
		if (user.getId() == null) {
			userService.save(user, groupids, roleids);
			setMessage("新增成功!");
		} else {
			userService.save(user, groupids, roleids);
			setMessage("保存成功!");
		}
		index();
	}

	/**
	 * 删除一个
	 * 
	 * @param id
	 */
	public void delete(String id) {
		User user = userService.getUser(Integer.valueOf(id));
		if (user != null) {
			userService.deleteUser(Integer.valueOf(id));
			setMessage("删除成功");
		}
		index();
	}

	/**
	 * 删除选中
	 */
	public void delete() {
		if (ids != null && ids.length() > 0) {
			userService.deleteUsers(ids);
			setMessage("成功删除");
		}
		users = userService.findAllUser(getPaginate());
		render("user/ajax_list.html");
	}

	public Collection<User> getUsers() {
		return users;
	}

	public User getUser() {
		return user;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public String getGroupids() {
		return groupids;
	}

	public String getRoleids() {
		return roleids;
	}

	public boolean hasGroup(Integer id) {
		for (Group group : user.getGroups()) {
			if (group.getId().equals(id))
				return true;
		}
		return false;
	}

	public boolean hasRole(Integer id) {
		for (Role role : user.getRoles()) {
			if (role.getId().equals(id))
				return true;
		}
		return false;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}
	public Set<Role> getSeletedRoles() {
		return seletedRoles;
	}

	
	
}
