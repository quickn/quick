package cn.quickj.extui.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.quickj.extui.action.bean.CheckBoxGroupsBean;
import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;
import cn.quickj.security.service.RoleService;
import cn.quickj.security.service.UserService;

import com.google.inject.Inject;

public class UserAction extends ExtBaseAction {
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;
	@Inject
	private User user;
	private String groupIds;
	private String roleIds;
	private String query;
	private String invokeAction;
	private String oldpassword;

	// 查找角色和用户角色生成json对象
	public String initRoles(String id) {
		Set<Role> rs = new HashSet<Role>();
		User user = new User();
		List<CheckBoxGroupsBean> checks = new ArrayList<CheckBoxGroupsBean>();
		if (!id.equals("0")) {
			user = userService.getUser(Integer.parseInt(id));
			rs = user.getRoles();
		}
		Paginate p = new Paginate(100);
		Collection<Role> roles = roleService.findAllRole(p);
		if (roles != null && roles.size() > 0) {
			for (Role r : roles) {
				CheckBoxGroupsBean c = new CheckBoxGroupsBean();
				c.setValue(r.getId().toString());
				if (rs != null && rs.size() > 0) {
					for (Role rr : rs) {
						if (rr.getId() == r.getId()) {
							c.setChecked(true);
						}
					}
				}
				if (c.getChecked() == null) {
					c.setChecked(false);
				}
				c.setBoxLabel(r.getName());
				checks.add(c);
			}
		}
		return toTreeJson(checks);
	}

	public String list() {
		Paginate paginate = new Paginate(start, limit);
		Collection<User> users = userService.findUserByExample(user, paginate,
				sort, dir, invokeAction);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("users", users);
		return toJson(data);
	}

	public String queryNameExists(String userName) {
		boolean status = userService.queryUserNameExists(userName);
		return toJson("{'status':" + status + "}");
	}

	public String listAll(String invokeAction) {
		Paginate paginate = new Paginate(0, 100);
		user.setUserName(query);
		Collection<User> users = userService.findUserByExample(user, paginate,
				sort, dir, invokeAction);
		if (users != null && users.size() > 0) {
			for (User u : users) {
				if (u.getRoles() != null) {
					u.setRoles(null);
				}
			}
		}
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("users", users);
		return toJson(data);
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public String changePwd() {
		SessionUser userSession = (SessionUser) getAttribute("USER_IN_SESSION_KEY");
		if (!userSession.getPassword().equals(userService.md5(oldpassword))) {
			return "{success:false,msg:'原密码错误！'}";
		} else {
			User u = userService.getUser(userSession.getId());
			String password = userService.md5(user.getPassword());
			u.setPassword(password);
			userService.update(u);
			userSession.setPassword(password);
			setAttribute("USER_IN_SESSION_KEY", userSession);
			return "{success:true,msg:'原密码错误！'}";
		}
	}

	public String load(String id) {
		User user = userService.getUser(Integer.parseInt(id));
		return toJson(user);
	}

	public String save() {
		userService.save(user, null, roleIds);
		return toJson(null);
	}

	public String delete(String ids) {
		userService.deleteUsers(ids);
		return toJson(null);
	}

	public String getTargetList(String id) {
		Paginate paginate = new Paginate(start, limit);
		List<User> user = userService.getUserList(id, paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("users", user);
		return toJson(data);
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public String getOldpassword() {
		return oldpassword;
	}

}
