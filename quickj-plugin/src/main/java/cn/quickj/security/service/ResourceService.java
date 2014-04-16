package cn.quickj.security.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ResourceService {
	@Inject
	private HibernateTemplate ht;

	public Resource getResource(Serializable id) {
		return (Resource) ht.getSession().get(Resource.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Resource> listFirstResources() {
		return ht.query("from Resource").list();
	}

	@SuppressWarnings("unchecked")
	public Collection<Resource> findResourceByExample(Resource searchForm,
			Paginate paginate) {
		Criteria criteria;
		if (paginate != null) {
			criteria = createCriteria(searchForm);
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = createCriteria(searchForm);
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		return criteria.list();
	}

	@Transaction
	public void save(Resource resource) {
		ht.save(resource);
	}

	@Transaction
	public void delete(String ids) {
		// 删除角色资源关联
		ht.querySql("delete from ROLE_HAS_RESOURCE where RESOURCE_ID in(:id)")
				.setParameterList("id", ids.split(",")).executeUpdate();
		// 删除资源
		ht.querySql("delete from RESOURCES where res_id in(:id)")
				.setParameterList("id", ids.split(",")).executeUpdate();
	}

	private Criteria createCriteria(Resource searchForm) {
		Criteria criteria = ht.getSession().createCriteria(Resource.class);
		if (searchForm.getName() != null && searchForm.getName().length() > 0) {
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + searchForm.getName() + "%"));
		}
		if (searchForm.getParent() != null
				&& searchForm.getParent().getId() != null) {
			criteria = criteria.add(Restrictions.eq("parent.id", searchForm
					.getParent().getId()));
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public Collection<Resource> findAllResource(int limit) {
		return ht.query("from Resource").setMaxResults(limit).list();
	}
}
