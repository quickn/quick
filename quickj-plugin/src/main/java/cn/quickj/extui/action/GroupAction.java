package cn.quickj.extui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;

import cn.quickj.AbstractApplication;
import cn.quickj.extui.action.bean.CheckBoxGroupsBean;
import cn.quickj.extui.action.bean.TreeBean;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Group;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;
import cn.quickj.security.service.GroupService;
import cn.quickj.security.service.RoleService;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.Inject;

public class GroupAction extends ExtBaseAction {
	@Inject
	private GroupService groupService;
	@Inject
	private Group group;
	@Inject
	private RoleService roleService;
	private String roleIds;
	private String parentId;

	// 查找角色和部门角色生成json对象
	public String initRoles(String id) {
		Set<Role> rs = new HashSet<Role>();
		Group group = new Group();
		List<CheckBoxGroupsBean> checks = new ArrayList<CheckBoxGroupsBean>();
		if (!id.equals("0")) {
			group = groupService.findGroupById(id);
			rs = group.getRoles();
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

	// 转换为树
	public String listGroups(String id) {
		List<TreeBean> beans = new ArrayList<TreeBean>();
		beans = findChildTreeNodes(Integer.valueOf(id));
		try {
			return AbstractApplication.jsonObjectMapper
					.writeValueAsString(beans);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 递归查询所有部门树
	private List<TreeBean> findChildTreeNodes(Integer id) {
		List<TreeBean> nodes = new ArrayList<TreeBean>();
		Collection<Group> groups = groupService.findChildGroup(id);
		if (groups != null && groups.size() > 0) {
			for (Group g : groups) {
				TreeBean node = new TreeBean();
				node.setId(g.getId().toString());
				node.setText(g.getName());
				node.setChildren(findChildTreeNodes(g.getId()));
				if (node.getChildren() == null) {
					node.setLeaf(true);
				}
				nodes.add(node);
			}
			return nodes;
		}
		return null;
	}

	public String list(String id) {
		List<Group> groups = groupService.listGroups(id);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("groups", groups);
		return toJson(data);
	}

	public String load(String id) {
		Group group = groupService.getGroup(Integer.parseInt(id));
		return toJson(group);
	}

	public String save() {
		try {
			Group pgroup = groupService.findGroupById(parentId);
			group.setParent(pgroup);
			Set<User> users = new HashSet<User>();
			if (group.getId() != null)
				users = groupService.getUserByGroup(group.getId().toString(),
						null);
			group.setUsers(users);
			groupService.save(group, roleIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toJson(null);

	}

	public String getAllUser() {
		Paginate paginate = new Paginate(start, limit);
		Set<User> user = groupService.getUserByGroup("1", paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("users", user);
		return toJson(data);
	}

	public String getListByGroup(String id) {
		Paginate paginate = new Paginate(start, limit);
		Set<User> user = groupService.getUserByGroup(id, paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("users", user);
		return toJson(data);
	}

	public String updateGroup(String ids, String groupid, String action) {
		ids = ids.substring(0, ids.length() - 1);
		groupService.updateGroup(ids, groupid, action);
		return toJson(null);
	}

	public String delete(String ids) {
		groupService.deleteGroups(ids);
		return toJson(null);
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
