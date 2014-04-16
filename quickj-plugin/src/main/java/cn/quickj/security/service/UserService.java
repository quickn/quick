package cn.quickj.security.service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Group;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;
import cn.quickj.utils.Constant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@SuppressWarnings("unchecked")
public class UserService {
	@Inject
	HibernateTemplate ht;

	public User getUser(Serializable id) {
		return (User) ht.getSession().get(User.class, id);
	}

	public Collection<User> findUserByExample(User user) {
		return findUserByExample(user, null, null, null, null);
	}

	public boolean queryUserNameExists(String userName) {
		String hql = "from User where USER_NAME='" + userName + "'";
		boolean status = false;
		int count = ht.query(hql).list().size();
		if (count == 0) {
			status = true;
		}
		return status;
	}

	private Criteria createCriteria(User searchForm) {
		Criteria criteria = ht.getSession().createCriteria(User.class);
		if (searchForm.getUserName() != null
				&& !"".equals(searchForm.getUserName())) {
			String querykey = "%" + searchForm.getUserName() + "%";
			if ("".equals(searchForm.getUserName().trim())) {
				querykey = "%%";
			}
			criteria = criteria.add(Restrictions.ilike("userName", querykey));
		}
		return criteria;
	}

	@SuppressWarnings("rawtypes")
	public Collection<User> findUserByExample(User searchForm,
			Paginate paginate, String sort, String dir, String invokeAction) {
		Criteria criteria;
		Collection<User> usercollection = new ArrayList<User>();
		if (paginate != null) {
			criteria = createCriteria(searchForm);
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = createCriteria(searchForm);
		if (invokeAction == null || !invokeAction.trim().equals("login")) {
			criteria.add(Restrictions.ne("userName", Constant.THEINIT_NAME));
		}
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		if (sort != null && !"".equals(sort)) {
			if ("ASC".equals(dir)) {
				criteria.addOrder(Order.asc(sort));
			} else {
				criteria.addOrder(Order.desc(sort));
			}
		}
		if (invokeAction != null && invokeAction.trim().equals("login")) {
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("userName"));
			projectionList.add(Projections.property("id"));
			projectionList.add(Projections.property("name"));
			criteria.setProjection(projectionList);
			List list = criteria.list();
			for (int i = 0; i < list.size(); i++) {
				User user = new User();
				Object[] tmpObjArray = (Object[]) list.get(i);
				for (int j = 0; j < tmpObjArray.length; j++) {
					if (tmpObjArray[j] != null) {
						switch (j) {
						case 2:
							user.setName(tmpObjArray[j].toString());
							break;
						case 0:
							user.setUserName(tmpObjArray[j].toString());
							break;
						case 1:
							user.setId(Integer.valueOf(tmpObjArray[j]
									.toString()));
							break;
						default:
							break;
						}
					}
				}
				if (!user.getUserName().equals("admin")) {
					usercollection.add(user);
				}
			}
		} else {
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("userName"));
			projectionList.add(Projections.property("id"));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("password"));
			projectionList.add(Projections.property("email"));
			projectionList.add(Projections.property("lastLogin"));
			projectionList.add(Projections.property("deadLogin"));
			criteria.setProjection(projectionList);
			List list = criteria.list();
			for (int i = 0; i < list.size(); i++) {
				User user = new User();
				Object[] tmpObjArray = (Object[]) list.get(i);
				for (int j = 0; j < tmpObjArray.length; j++) {
					if (tmpObjArray[j] != null) {
						switch (j) {
						case 2:
							user.setName(tmpObjArray[j].toString());
							break;
						case 0:
							user.setUserName(tmpObjArray[j].toString());
							break;
						case 1:
							user.setId(Integer.valueOf(tmpObjArray[j]
									.toString()));
							break;
						case 3:
							user.setPassword(tmpObjArray[j].toString());
							break;
						case 4:
							user.setEmail(tmpObjArray[j].toString());
							break;
						case 5:
							user.setLastLogin(Timestamp.valueOf(tmpObjArray[j]
									.toString()));
							break;
						case 6:
							user.setDeadLogin(Timestamp.valueOf(tmpObjArray[j]
									.toString()));
							break;
						default:
							break;
						}
					}
				}
				if (!user.getUserName().equals("admin")) {
					usercollection.add(user);
				}
			}
			// usercollection = criteria.list();
		}
		return usercollection;
	}

	/**
	 * 查找所有用户（指定一个限值是为了防止大量数据引起服务器内存消耗完。
	 * 
	 * @param limit
	 * @return
	 */
	public Collection<User> findAllUser(Paginate paginate) {
		if (paginate != null) {
			int count = ((Long) ht.query("select count(*) from User")
					.uniqueResult()).intValue();
			paginate.setTotal(count);
			return ht.query("from User").setFirstResult(paginate.getOffset())
					.setMaxResults(paginate.getCount()).list();
		} else {
			return ht.query("from User").list();
		}
	}

	public Collection<User> findUsersByids(String ids) {
		Collection<User> users = ht.query(
				"from User u where u.id in(" + ids + ")").list();
		return users.size() > 0 ? users : null;
	}

	// FIXME:save 和update不一致，命名应该具有一致性。
	@SuppressWarnings("rawtypes")
	@Transaction
	public void save(User user, String groupids, String roleids) {
		if (groupids != null && groupids.length() > 0) {
			Collection<Group> g = ht.query(
					"from Group gr where gr.id in(" + groupids + ")").list();
			if (g.size() > 0) {
				HashSet groups = new HashSet();
				groups.addAll(g);
				user.setGroups(groups);
			}
		}
		if (roleids != null && roleids.length() > 0) {
			roleids = roleids.substring(0, roleids.length() - 1);
			Collection<Role> r = ht.query(
					"from Role ro where ro.id in(" + roleids + ")").list();
			if (r.size() > 0) {
				HashSet roles = new HashSet();
				roles.addAll(r);
				user.setRoles(roles);
			}
		}
		if (user.getId() != null && user.getId() != 0) {
			User u = ht.load(User.class, user.getId());
			if (u != null && !u.getPassword().equals(user.getPassword())) {
				user.setPassword(this.md5(user.getPassword()));
				user.setDeadLogin(null);
			} else if (u != null) {
				user.setDeadLogin(u.getDeadLogin());
			}
			ht.save((Serializable) ht.getSession().merge(user));
		} else {
			user.setPassword(this.md5(user.getPassword()));
			ht.save(user);
		}
	}

	/**
	 * 删除指定的用户。
	 * 
	 * @param id
	 */
	@Transaction
	public void deleteUser(Serializable id) {
		User user = getUser(id);
		if (!user.getGroups().isEmpty()) {
			ht.querySql("delete from USER_HAS_GROUP where USER_ID =?")
					.setInteger(0, user.getId()).executeUpdate();
		}
		if (!user.getRoles().isEmpty()) {
			ht.querySql("delete from USER_HAS_ROLE where USER_ID=?")
					.setInteger(0, user.getId()).executeUpdate();
		}
		ht.querySql("delete from USERS where id=?").setInteger(0, user.getId())
				.executeUpdate();
	}

	/**
	 * 删除选中的一群用户。
	 * 
	 * @param checkedId
	 */
	@Transaction
	public void deleteUsers(String checkedId) {
		ht.querySql("delete from USER_HAS_GROUP where USER_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from USER_HAS_ROLE where USER_ID in(:id)")
				.setParameterList("id", checkedId.split(",")).executeUpdate();
		ht.querySql("delete from USERS where USER_ID in(:ids)")
				.setParameterList("ids", checkedId.split(",")).executeUpdate();

	}

	public List<User> findUserByRoleName(String roleName) {
		return ht.query(
				"select user from User user left join user.roles role where role.name like '%"
						+ roleName + "%')").list();
	}

	public SessionUser getSessionUser(User user) {
		SessionUser u = new SessionUser();
		u.setDeadLogin(user.getDeadLogin());
		u.setEmail(user.getEmail());
		u.setId(user.getId());
		u.setLastLogin(user.getLastLogin());
		u.setName(user.getName());
		u.setPassword(user.getPassword());
		u.setUserName(user.getUserName());
		return u;
	}

	// 用户登录
	public User login(String username, String passwd) {
		passwd = this.md5(passwd);
		List<User> users = ht
				.query("from User where userName=? and password=?")
				.setString(0, username).setString(1, passwd).list();
		if (users != null && users.size() > 0) {
			return users.get(0);
		} else {
			return null;
		}
	}

	// 用户查询所在的部门
	public List<User> getUserList(String id, Paginate paginate) {
		Group group = (Group) ht.query("from Group where id=" + id)
				.uniqueResult();
		if (group == null)
			return null;
		Set<User> user = group.getUsers();
		String ids = "";
		if (user.size() == 0) {
			ids = "-1";
		}
		for (User u : user) {
			ids += u.getId() + ",";
		}
		if (ids.length() > 2) {
			ids = ids.substring(0, ids.length() - 1);
		}
		paginate.setTotal(ht
				.query("from User where id not in (" + ids
						+ ") and name != 'admin'").list().size());
		List<User> userList = ht
				.query("from User where id not in (" + ids
						+ ") and name != 'admin'")
				.setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getCount()).list();
		return userList;
	}

	// 根据用户姓名查找用户角色
	@SuppressWarnings("rawtypes")
	public List findRolesByUserName(String name) {
		return ht
				.querySql(
						"select name from ROLES where role_id in (select role_id from USER_HAS_ROLE where user_id in (select user_id from USERS where name =?))")
				.setString(0, name).list();
	}

	@Transaction
	public void update(User user) {
		ht.update((Serializable) ht.getSession().merge(user));
	}

	public String md5(String clientHash) {
		return new String(
				Base64.encodeBase64(org.apache.commons.codec.digest.DigestUtils
						.md5(clientHash)));
	}
}
