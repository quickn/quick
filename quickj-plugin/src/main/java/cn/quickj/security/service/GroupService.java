package cn.quickj.security.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Group;
import cn.quickj.security.model.User;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("unchecked")
public class GroupService {
	@Inject
	HibernateTemplate ht;

	// 列出所有部门
	public List<Group> listGroups(String id) {
		String hql = "";
		if (id != null && !"-1".equals(id.trim())) {
			hql = "from Group where group_id <>" + id
					+ " order by group_id asc";
		} else {
			hql = "from Group order by group_id asc";
		}
		return ht.query(hql).list();
	}

	// 列出组里的子组
	public List<Group> findChildGroup(Integer id) {
		if (id.equals(0)) {
			return ht.query("from Group g where g.id=1").list();
		} else {
			return ht.query("from Group g where g.parent.id=?")
					.setInteger(0, id).list();
		}
	}

	// 添加了当group_id=1既为查询所有部门时的数据
	public Set<User> getUserByGroup(String id, Paginate paginate) {
		String hqls = "";
		Set<User> user = new HashSet<User>();
		if (id != null && "1".equals(id)) {
			hqls = "from Group";
			List<Group> group = ht.query(hqls).list();
			for (Group group2 : group) {
				if (!group2.getUsers().isEmpty()) {
					for (User users : group2.getUsers()) {
						user.add(users);
					}

				}
			}
		} else {
			hqls = "from Group g where g.id=" + id;
			Group group = (Group) ht.query(hqls).uniqueResult();
			if (!group.getUsers().isEmpty()) {
				for (User users : group.getUsers()) {
					user.add(users);
				}

			}
		}
		String ids = "";
		if (paginate != null) {
			paginate.setTotal(user.size());
			for (User user2 : user) {
				ids += user2.getId().toString() + ",";
			}
			if (ids.length() > 1) {
				ids = ids.substring(0, ids.length() - 1);
				String hql = "from User u where u.id in (" + ids + ")";
				user.clear();
				user = new HashSet<User>(ht.query(hql)
						.setFirstResult(paginate.getOffset())
						.setMaxResults(paginate.getCount()).list());
			}
		}
		return user;
	}

	public Collection<Group> findGroupByExample(Group group, Paginate paginate) {
		Criteria criteria;
		if (paginate != null) {
			criteria = ht.getSession().createCriteria(Group.class);
			if (group.getName() != null)
				criteria = criteria.add(Restrictions.ilike("name",
						"%" + group.getName() + "%"));
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = ht.getSession().createCriteria(Group.class);
		if (group.getName() != null)
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + group.getName() + "%"));
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		return criteria.list();
	}

	public Group getGroup(Serializable id) {
		return (Group) ht.getSession().get(Group.class, id);
	}

	public Collection<Group> findGroupByExample(Group group) {
		Criteria criteria = ht.getSession().createCriteria(Group.class);
		if (group.getName() != null)
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + group.getName() + "%"));
		return criteria.list();
	}

	/**
	 * 查找所有用户（指定一个限值是为了防止大量数据引起服务器内存消耗完。
	 * 
	 * @param limit
	 * @return
	 */
	public Collection<Group> findAllGroup(Paginate paginate) {
		int count = ((Long) ht.query("select count(*) from Group g")
				.uniqueResult()).intValue();
		paginate.setTotal(count);
		return ht.query("from Group").setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getCount()).list();
	}

	// FIXME:save 和update不一致，命名应该具有一致性。
	@SuppressWarnings("rawtypes")
	@Transaction
	public void save(Group group, String roleids) {
		if (roleids != null && roleids.length() > 0) {
			if (roleids != null && roleids.length() > 0) {
				roleids = roleids.substring(0, roleids.length() - 1);
			}
			// ht
			// .querySql(
			// "delete from GROUP_HAS_ROLE where role_id in (?) and group_id
			// =?")
			// .setString(0, roleids).setInteger(1, group.getId())
			// .executeUpdate();
			HashSet ro = new HashSet();
			ro.addAll(ht.query("from Role r where r.id in(" + roleids + ")")
					.list());
			if (ro.size() > 0)
				group.setRoles(ro);
		}
		if (group.getId() != null && group.getId() != 0) {
			ht.getSession().merge(group);
		} else {
			ht.save(group);
		}

	}

	@Transaction
	public void setUser(Group group, String userids) {
		if (userids.length() > 0) {
			HashSet<User> us = new HashSet<User>();
			us.addAll(ht.query("from User u where u.id in(" + userids + ")")
					.list());
			if (us.size() > 0)
				group.setUsers(us);
		}
		ht.update(group);
	}

	@Transaction
	public void setNone(Group group) {
		ht.querySql(
				"delete from USER_HAS_GROUP where GROUP_ID=" + group.getId())
				.executeUpdate();
	}

	/**
	 * 删除指定的用户。
	 * 
	 * @param id
	 */
	@Transaction
	public void deleteGroup(Serializable id) {
		ht.querySql("delete from GROUPS where PARENT_GROUP_ID=?")
				.setInteger(0, Integer.valueOf(id.toString())).executeUpdate();
		ht.querySql("delete from USER_HAS_GROUP where GROUP_ID=:id")
				.setInteger("id", Integer.valueOf(id.toString()))
				.executeUpdate();
		ht.querySql("delete from USER_HAS_ROLE where GROUP_ID=:id")
				.setInteger("id", Integer.valueOf(id.toString()))
				.executeUpdate();
		ht.querySql("delete from GROUPS where id=?")
				.setInteger(0, Integer.valueOf(id.toString())).executeUpdate();
	}

	public Group findGroupById(String id) {
		List<Group> result = ht.query("from Group g where g.id=?")
				.setInteger(0, Integer.valueOf(id)).list();
		return (result.size() > 0 ? result.get(0) : null);
	}

	/**
	 * 删除选中的一群组。
	 * 
	 * @param checkedId
	 */
	@Transaction
	public void deleteGroups(String checkedId) {
		ht.querySql("delete from GROUP_HAS_ROLE where GROUP_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from USER_HAS_GROUP where GROUP_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from GROUPS where PARENT_GROUP_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from GROUPS where GROUP_ID in(:ids)")
				.setParameterList("ids", checkedId.split(",")).executeUpdate();
	}

	/**
	 * 通过ID load Group
	 */
	public Group loadObcById(Group o, Integer id) {
		return ht.load(Group.class, id);
	}

	/**
	 * 跟新用户组
	 */
	@Transaction
	public void updateGroup(String ids, String groupid, String action) {
		ht.querySql(
				"delete from USER_HAS_GROUP where user_id in (" + ids
						+ ") and group_id = " + groupid).executeUpdate();
		String[] str = ids.split(",");
		if (action != null && action.equals("update")) {
			for (int i = 0; i < str.length; i++) {
				ht.querySql(
						"insert into USER_HAS_GROUP(user_id,group_id) values(?,?)")
						.setInteger(0, Integer.valueOf(str[i]))
						.setInteger(1, Integer.valueOf(groupid))
						.executeUpdate();
			}
		} else {
			if (!"1".equals(groupid))
				for (int i = 0; i < str.length; i++) {
					ht.querySql(
							"insert into USER_HAS_GROUP(user_id,group_id) values(?,1)")
							.setInteger(0, Integer.valueOf(str[i]))
							.executeUpdate();
				}

		}
	}
}
