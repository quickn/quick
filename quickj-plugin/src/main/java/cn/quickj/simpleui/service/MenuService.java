package cn.quickj.simpleui.service;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.simpleui.model.SimpleMenu;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("unchecked")
public class MenuService {
	@Inject
	HibernateTemplate ht;

	/**
	 * 查询用户可以拥有的菜单。
	 * 
	 * @param userid
	 * @return
	 */
	public Collection<SimpleMenu> getMenus(int userId) {
		String roleIds = "0,";
		// 用户角色
		String sql = "select role_id from USER_HAS_ROLE where user_id = "
				+ userId;
		List<Integer> list = ht.querySql(sql).list();
		for (Integer roleId : list) {
			roleIds += roleId + ",";
		}
		// 部门角色
		sql = "select role_id from GROUP_HAS_ROLE gr,USER_HAS_GROUP ug "
				+ "where gr.group_id = ug.group_id and ug.user_id = " + userId;
		list = ht.querySql(sql).list();
		for (Integer roleId : list) {
			roleIds += roleId + ",";
		}
		roleIds = roleIds.substring(0, roleIds.length() - 1);
		String resourceIds = "0,";
		// 角色资源
		sql = "select resource_id from ROLE_HAS_RESOURCE where role_id in ("
				+ roleIds + ")";
		list = ht.querySql(sql).list();
		for (Integer resourceId : list) {
			resourceIds += resourceId + ",";
		}
		resourceIds = resourceIds.substring(0, resourceIds.length() - 1);
		return ht.query(
				"from SimpleMenu m where resource.id in (" + resourceIds
						+ ") order by m.position").list();
	}

	/**
	 * 查询所有菜单项
	 * 
	 * @return
	 */
	public Collection<SimpleMenu> getMenus() {
		return ht.query("from SimpleMenu m order by m.position").list();
	}

	public Collection<SimpleMenu> getTreeMenus(Paginate paginate) {
		return getTreeMenus(null, paginate);
	}

	public Collection<SimpleMenu> getTreeMenus(SimpleMenu menu,
			Paginate paginate) {
		// 计算总数。
		Criteria criteria = ht.getSession().createCriteria(SimpleMenu.class);
		criteria.setProjection(Projections.rowCount());
		int total = ((Integer) criteria.uniqueResult()).intValue();
		paginate.setTotal(total);
		criteria = ht.getSession().createCriteria(SimpleMenu.class);
		for (Order order : paginate.getOrders()) {
			criteria = criteria.addOrder(order);
		}
		criteria.addOrder(Order.asc("position"));
		if (menu != null) {
			criteria.add(Restrictions.ilike("title", menu.getTitle()));
		}
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		return criteria.list();
	}

	@Transaction
	public void create(SimpleMenu menu, boolean createResource) {
		if (createResource) {
			Resource res = new Resource();
			res.setName(menu.getTitle());
			res.setUrl(menu.getUrl());
			ht.save(res);
			menu.setResource(res);
		}
		ht.save(menu);
	}

	@Transaction
	public void update(SimpleMenu menu, int resourceId, boolean updateResource) {
		if (updateResource) {
			Resource resource = (Resource) ht.load(Resource.class, resourceId);
			resource.setName(menu.getTitle());
			resource.setUrl(menu.getUrl());
			ht.update(resource);
			menu.setResource(resource);
		}
		ht.update(menu);
	}

	public SimpleMenu getMenu(int menuId) {
		return (SimpleMenu) ht.load(SimpleMenu.class, menuId);
	}

	public Collection<SimpleMenu> getSubMenus(int menuId) {
		return ht
				.query("from SimpleMenu menu where menu.parentId = :menuId order by position asc")
				.setInteger("menuId", menuId).list();
	}

	public Collection<SimpleMenu> getSubMenus(int menuId, int userid) {
		SQLQuery query = ht
				.querySql(
						"select * from SIMPLE_MENUS where resource_id in"
								+ "(select resource_id from ROLE_HAS_RESOURCE where role_id in "
								+ "(select role_id from USER_HAS_ROLE where user_id=? ) "
								+ "or role_id in (select role_id from GROUP_HAS_ROLE gr,USER_HAS_GROUP ug "
								+ "where gr.group_id =ug.group_id and ug.user_id = ?)) and parent_id = ? order by position asc")
				.addEntity(SimpleMenu.class);
		query.setInteger(0, userid);
		query.setInteger(1, userid);
		query.setInteger(2, menuId);

		return query.list();
	}

	public Collection<SimpleMenu> getRootMenus() {
		return getSubMenus(0);
	}

	public Collection<SimpleMenu> getRootMenus(int userid) {
		return getSubMenus(0, userid);
	}

	@Transaction
	public void save(SimpleMenu menu, boolean createResource) {
		Resource res = null;
		if (menu.getId() != null) {
			SimpleMenu oldMenu = getMenu(menu.getId());
			res = oldMenu.getResource();
			if (res == null) {
				res = new Resource();
				oldMenu.setResource(res);
			}
			res.setName(menu.getTitle());
			res.setUrl(menu.getUrl());
			oldMenu.setHtml(menu.getHtml());
			oldMenu.setLevel(menu.getLevel());
			oldMenu.setParent(menu.getParent());
			oldMenu.setPosition(menu.getPosition());
			oldMenu.setTitle(menu.getTitle());
			oldMenu.setUrl(menu.getUrl());
			ht.save(oldMenu);

		}
		if (res == null) {
			if (createResource) {
				res = new Resource();
				res.setName(menu.getTitle());
				res.setUrl(menu.getUrl());
				menu.setResource(res);
			}
			ht.save(menu);
		}
	}

	/**
	 * 递归删除菜单和其子菜单。
	 * 
	 * @param id
	 */
	@Transaction
	public void delete(int id) {
		List<Integer> result = ht
				.query("select m.id from SimpleMenu m where m.parentId = ?")
				.setInteger(0, id).list();
		for (Integer menuId : result) {
			delete(menuId);
		}
		SimpleMenu menu = (SimpleMenu) ht.load(SimpleMenu.class, id);
		ht.delete(menu);
	}

	/**
	 * 搜索条件，可以根据需要丰富
	 * 
	 * @param menu
	 * @param limit
	 */
	public Collection<SimpleMenu> search(SimpleMenu menu, Paginate paginate) {
		return getTreeMenus(menu, paginate);
	}

	public void delete(int[] menuIds) {
		for (int i : menuIds) {
			delete(i);
		}
	}
}
