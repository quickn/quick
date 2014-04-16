package cn.quickj.security.action;

import java.util.Collection;

import cn.quickj.security.model.Resource;
import cn.quickj.security.model.Role;
import cn.quickj.security.service.ResourceService;
import cn.quickj.security.service.RoleService;
import cn.quickj.simpleui.action.SimpleUIActionSupport;

import com.google.inject.Inject;

public class RoleAction extends SimpleUIActionSupport {
	final public static String USER_IN_SESSION_KEY = "QUICKJ_USER_BEAN";
	@Inject
	private Role role;
	@Inject
	RoleService roleService;
	private Collection<Role> roles;
	private Collection<Resource> resources;
	@Inject
	ResourceService resourceService;

	private String checkedIds;
	private String ids;

	public void index() {
		roles = roleService.findAllRole(getPaginate());
		render("role/list.html");
	}
	public void ajax(){
		roles = roleService.findAllRole(getPaginate());
		render("role/ajax_list.html");
	}

	/**
	 * 根据User Example查找user.
	 */
	public void list() {
		roles = roleService.findRoleByExample(role);
		render("role/list.html");
	}

	/**
	 * 新建一个用户
	 */
	public void create() {
		resources = resourceService.findAllResource(100);
		render("role/ajax_form.html");
	}

	/**
	 * 准备编辑一个用户。
	 * 
	 * @param id
	 */
	public void edit(String id) {
		resources = resourceService.findAllResource(100);
		role = roleService.getRole(Integer.parseInt(id));
		render("role/ajax_form.html");
	}

	/**
	 * 新增或者修改后保存。
	 */
	public void save() {
		if (role.getId() == null) {
			roleService.save(role, checkedIds);
			setMessage("新增成功!");
		} else {
			roleService.save(role, checkedIds);
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
		Role role = roleService.getRole(Integer.valueOf(id));
		if (role != null) {
			roleService.deleterole(role.getId());
			setMessage("删除成功！");
		}
		index();
	}

	/**
	 * 删除选中
	 */
	public void delete() {
		if (ids != null && ids.length() > 0) {
			roleService.deleteroles(ids);
			setMessage("删除成功！");
		}
		roles = roleService.findAllRole(getPaginate());
		render("role/ajax_list.html");
	}

	public void setCheckedIds(String checkedIds) {
		this.checkedIds = checkedIds;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public String getCheckedIds() {
		return checkedIds;
	}

	public void setResources(Collection<Resource> resources) {
		this.resources = resources;
	}

	public Collection<Resource> getResources() {
		return resources;
	}

	public boolean hasResource(Integer id) {
		for (Resource re : role.getResources()) {
			if (re.getId().equals(id))
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

}
