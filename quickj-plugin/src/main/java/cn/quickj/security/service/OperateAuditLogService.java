package cn.quickj.security.service;

// Generated from PowerDesigner file ,Written by lbj.

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.OperateAuditLog;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OperateAuditLogService {
	@Inject
	private HibernateTemplate ht;

	public OperateAuditLog getOperateAuditLog(Serializable id) {
		return (OperateAuditLog) ht.getSession().get(OperateAuditLog.class, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<OperateAuditLog> findOperateAuditLogByExample(
			OperateAuditLog searchForm, String startDate, String endDate,
			Paginate paginate, String sort, String dir) throws Exception {
		Criteria criteria;
		if (paginate != null) {
			criteria = createCriteria(searchForm, startDate, endDate);
			criteria.setProjection(Projections.rowCount());
			List list = criteria.list();
			if (list != null && list.size() > 0) {
				paginate.setTotal(Integer.parseInt(criteria.list().get(0)
						.toString()));
			} else {
				paginate.setTotal(0);
			}
		}
		criteria = createCriteria(searchForm, startDate, endDate);
		if (paginate != null) {
			criteria.setFirstResult(paginate.getOffset());
			criteria.setMaxResults(paginate.getCount());
			if (sort != null && !"".equals(sort))
				if (dir != null && "ASC".equals(dir.toUpperCase())) {
					criteria.addOrder(Order.asc(sort));
				} else {
					criteria.addOrder(Order.desc(sort));
				}
		}
		return criteria.list();
	}

	@Transaction
	public void save(OperateAuditLog operateAuditLog) {
		ht.save(operateAuditLog);
	}

	@Transaction
	public void delete(String ids) {
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}

		ht.getSession()
				.createQuery(
						"delete from OperateAuditLog where id in (" + ids + ")")
				.executeUpdate();
	}

	private Criteria createCriteria(OperateAuditLog searchForm,
			String startDate, String endDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Criteria criteria = ht.getSession().createCriteria(
				OperateAuditLog.class);
		if (searchForm.getUserId() != null) {
			criteria = criteria.add(Restrictions.eq("userId",
					Integer.valueOf(searchForm.getUserId())));
		} else {
			criteria = criteria.add(Restrictions.eq("userId", 0));
		}
		if (searchForm.getUsername() != null
				&& searchForm.getUsername().length() > 0) {
			criteria = criteria.add(Restrictions.ilike("username", "%"
					+ searchForm.getUsername() + "%"));
		}
		if (startDate != null && startDate.length() > 0) {
			criteria = criteria.add(Restrictions.ge("operateTime",
					sdf.parse(startDate)));
		}
		if (endDate != null && endDate.length() > 0) {
			criteria = criteria.add(Restrictions.le("operateTime",
					sdf.parse(endDate)));
		}
		return criteria;
	}
}
