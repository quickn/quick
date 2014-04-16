package cn.quickj.security.action;

import java.util.Collection;

import cn.quickj.security.model.Group;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;
import cn.quickj.security.service.GroupService;
import cn.quickj.security.service.RoleService;
import cn.quickj.security.service.UserService;
import cn.quickj.simpleui.action.SimpleUIActionSupport;

import com.google.inject.Inject;

public class GroupAction extends SimpleUIActionSupport {
	final public static String USER_IN_SESSION_KEY = "QUICKJ_USER_BEAN";
	@Inject
	private Group group;
	@Inject
	private User user;
	@Inject
	GroupService groupService;
	private Integer parentId;
	private Collection<Group> groups;
	private String ids;
	private Collection<Role> roles;
	@Inject
	RoleService roleService;
	private String roleids;
	private Collection<User> users;
	private String userids;
	@Inject
	UserService userService;

	/**
	 * 列出用户列表。
	 */
	public void index() {
		groups = groupService.findAllGroup(getPaginate());
		render("group/list.html");
	}

	/**
	 * 根据Group Example查找group.
	 */
	public void list() {
		groups = groupService.findGroupByExample(group);
		render("group/list.html");
	}

	/**
	 * 新建一个用户
	 */
	public void create() {
		groups = groupService.findAllGroup(getPaginate());
		roles = roleService.findAllRole(getPaginate());
		render("group/ajax_form.html");
	}

	/**
	 * 准备编辑一个用户。
	 * 
	 * @param id
	 */
	public void edit(String id) {
		groups = groupService.findAllGroup(getPaginate());
		roles = roleService.findAllRole(getPaginate());
		group = groupService.getGroup(Integer.parseInt(id));
		if (group.getParent() != null) {
			parentId = group.getParent().getId();
		}
		render("group/ajax_form.html");
	}

	public void ajax() {
		groups = groupService.findAllGroup(getPaginate());
		render("group/ajax_list.html");
	}

	/**
	 * 新增或者修改后保存。
	 */
	public void save() {
		if (group.getId() == null) {
			if (parentId != null) {
				group.setParent(groupService.getGroup(parentId));
			}
			groupService.save(group, roleids);
			setMessage("新增成功!");
		} else {
			if (parentId != null) {
				group.setParent(groupService.getGroup(parentId));
			}
			groupService.save(group, roleids);
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
		Group group = groupService.getGroup(Integer.valueOf(id));
		if (group != null) {
			groupService.deleteGroup(Integer.parseInt(id));
			setMessage("成功删除");
		}
		index();
	}

	/**
	 * 删除选中
	 */
	public void delete() {
		if (ids != null && ids.length() > 0) {
			groupService.deleteGroups(ids);
			setMessage("成功删除");
		}
		groups = groupService.findAllGroup(getPaginate());
		render("group/ajax_list.html");
	}

	public void addchild(String parentid) {
		parentId = Integer.valueOf(parentid);
		create();
	}

	public void addpeople(String groupid) {
		users = userService.findAllUser(getPaginate());
		group = groupService.getGroup(Integer.valueOf(groupid));
		render("group/ajax_user.html");
	}

	public void saveUser() {
		if (userids != null && userids.length() > 0) {
			Group g = groupService.getGroup(group.getId());
			groupService.setUser(g, userids);
			setMessage("分配成功");
		} else {
			groupService.setNone(group);
			setMessage("清空成功");
		}
		index();
	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public Group getGroup() {
		return group;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}

	public void setRoleids(String roleids) {
		this.roleids = roleids;
	}

	public String getRoleids() {
		return roleids;
	}

	public boolean hasRoles(Integer id) {
		for (Role ro : group.getRoles()) {
			if (ro.getId().equals(id))
				return true;
		}
		return false;
	}

	public boolean hasUsers(Integer id) {
		for (User u : group.getUsers()) {
			if (u.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserids(String userids) {
		this.userids = userids;
	}

	public String getUserids() {
		return userids;
	}

}
