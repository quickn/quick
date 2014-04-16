package cn.quickj.extui.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.quickj.extui.action.bean.CheckBoxGroupsBean;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.security.model.Role;
import cn.quickj.security.service.ResourceService;
import cn.quickj.security.service.RoleService;

import com.google.inject.Inject;

public class RoleAction extends ExtBaseAction {
	@Inject
	private RoleService roleService;
	@Inject
	private Role role;
	private String checkedIds;
	@Inject
	private ResourceService resourceService;

	// 查找角色资源和选中的资源生成json对象
	public String initResources(String id) {
		Set<Resource> ress = new HashSet<Resource>();
		Role role = new Role();
		List<CheckBoxGroupsBean> checks = new ArrayList<CheckBoxGroupsBean>();
		if (!id.equals("0")) {
			role = roleService.getRole(Integer.parseInt(id));
			ress = role.getResources();
		}
		Collection<Resource> resources = resourceService.findAllResource(500);
		if (resources != null && resources.size() > 0) {
			for (Resource r : resources) {
				CheckBoxGroupsBean c = new CheckBoxGroupsBean();
				c.setValue(r.getId().toString());
				if (ress != null && ress.size() > 0) {
					for (Resource rr : ress) {
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
		Collection<Role> roles = roleService.findRoleByExample(role, paginate,
				sort, dir);
		// if(roles != null && roles.size() > 0){
		// for(Role r : roles ){
		// if(r.getResources() != null){
		// r.setResources(null);
		// }
		// if(r.getUsers() != null){
		// r.setUsers(null);
		// }
		// if(r.getGroups() != null){
		// r.setGroups(null);
		// }
		// }
		// }
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("roles", roles);
		return toJson(data);
	}

	public String load(String id) {
		Role role = roleService.getRole(Integer.parseInt(id));
		return toJson(role);
	}

	public String save() {
		roleService.save(role, checkedIds);
		return toJson(null);
	}

	public String delete(String ids) {
		if (ids != null && ids.length() > 0 && ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String[] arr = ids.split(",");
		for (int i = 0; i < arr.length; i++) {
			roleService.deleterole(arr[i]);
		}
		return toJson(null);
	}

	public String getCheckedIds() {
		return checkedIds;
	}

	public void setCheckedIds(String checkedIds) {
		this.checkedIds = checkedIds;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}
}
