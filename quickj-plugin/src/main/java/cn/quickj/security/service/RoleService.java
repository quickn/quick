package cn.quickj.security.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.security.model.Role;
import cn.quickj.utils.StringUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("unchecked")
public class RoleService {

	@Inject
	HibernateTemplate ht;

	private Criteria createCriteria(Role searchForm) {
		Criteria criteria = ht.getSession().createCriteria(Role.class);
		if (searchForm.getName() != null && !"".equals(searchForm.getName())) {
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + searchForm.getName() + "%"));
		}
		return criteria;
	}

	@SuppressWarnings("rawtypes")
	public Collection<Role> findRoleByExample(Role searchForm,
			Paginate paginate, String sort, String dir) {
		Criteria criteria;
		Collection<Role> rolecollection = new ArrayList<Role>();
		if (paginate != null) {
			criteria = createCriteria(searchForm);
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = createCriteria(searchForm);
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		if (sort != null && !"".equals(sort)) {
			if ("ASC".equals(dir)) {
				criteria.addOrder(Order.asc(sort));
			} else {
				criteria.addOrder(Order.desc(sort));
			}
		}
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("description"));
		projectionList.add(Projections.property("id"));
		projectionList.add(Projections.property("name"));
		projectionList.add(Projections.property("position"));
		criteria.setProjection(projectionList);
		List list = criteria.list();
		for (int i = 0; i < list.size(); i++) {
			Role role = new Role();
			Object[] tmpObjArray = (Object[]) list.get(i);
			for (int j = 0; j < tmpObjArray.length; j++) {
				if (tmpObjArray[j] != null) {
					switch (j) {
					case 0:
						role.setDescription(tmpObjArray[j].toString());
						break;
					case 1:
						role.setId(Integer.valueOf(tmpObjArray[j].toString()));
						break;
					case 2:
						role.setName(tmpObjArray[j].toString());
						break;
					case 3:
						if (StringUtil.isNumeric(tmpObjArray[j].toString())) {
							role.setPosition(Integer.valueOf(tmpObjArray[j]
									.toString()));
						}
						break;
					default:
						break;
					}
				}
			}
			rolecollection.add(role);
		}
		return rolecollection;
	}

	public Role getRole(Serializable id) {
		return (Role) ht.getSession().get(Role.class, id);
	}

	public Collection<Role> findRoleByExample(Role role) {
		Criteria criteria = ht.getSession().createCriteria(Role.class);
		if (role.getName() != null)
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + role.getName() + "%"));
		return criteria.list();
	}

	/**
	 * 查找所有用户（指定一个限值是为了防止大量数据引起服务器内存消耗完。(过期)
	 * 
	 * @param limit
	 * @return
	 */
	public Collection<Role> findAllRole(Paginate paginate) {
		paginate.setTotal(Integer.valueOf(ht.query("select count(*) from Role")
				.uniqueResult().toString()));
		return ht.query("from Role").setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getCount()).list();
	}

	public Role findRoleById(String id) {
		Query query = ht.query("from Role r where r.id=?").setInteger(0,
				Integer.valueOf(id));
		List<Role> result = query.list();
		return result.size() > 0 ? result.get(0) : null;
	}

	// FIXME:save 和update不一致，命名应该具有一致性。
	@SuppressWarnings("rawtypes")
	@Transaction
	public void save(Role role, String checkedIds) {
		if (checkedIds != null && checkedIds.length() > 0) {
			checkedIds = checkedIds.substring(0, checkedIds.length() - 1);
			Collection<Resource> r = ht.query(
					"from Resource r where r.id in(" + checkedIds + ")").list();
			if (r.size() > 0) {
				HashSet res = new HashSet();
				res.addAll(r);
				role.setResources(res);
			}
		}
		if (role.getId() != null) {
			ht.getSession().merge(role);
		} else {
			ht.save(role);
		}
	}

	/**
	 * 删除指定的用户。
	 * 
	 * @param id
	 */
	@Transaction
	public void deleterole(Serializable id) {
		Role role = getRole(Integer.valueOf(id.toString()));
		if (!role.getResources().isEmpty()) {
			ht.querySql("delete from ROLE_HAS_RESOURCE where ROLE_ID=:id")
					.setInteger("id", role.getId()).executeUpdate();
		}
		if (!role.getUsers().isEmpty()) {
			ht.querySql("delete from USER_HAS_ROLE where ROLE_ID=:id")
					.setInteger("id", role.getId()).executeUpdate();
		}
		if (!role.getGroups().isEmpty()) {
			ht.querySql("delete from GROUP_HAS_ROLE where ROLE_ID=:id")
					.setInteger("id", role.getId()).executeUpdate();
		}
		ht.querySql("delete from ROLES where role_id in(:ids)")
				.setString("ids", id.toString()).executeUpdate();
	}

	/**
	 * 删除选中的一群用户。
	 * 
	 * @param checkedId
	 */
	@Transaction
	public void deleteroles(String checkedId) {
		ht.querySql("delete from ROLE_HAS_RESOURCE where ROLE_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from USER_HAS_ROLE where ROLE_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from GROUP_HAS_ROLE where ROLE_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from ROLES where id in(:ids)")
				.setParameterList("ids", checkedId.split(",")).executeUpdate();
	}
}
