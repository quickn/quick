package cn.quickj.security.service;
// Generated from PowerDesigner file ,Written by lbj.

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.utils.StringUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SimpleMenusService {
	@Inject
	private HibernateTemplate ht;

	public SimpleMenu getSimpleMenus(Serializable id) {
		return (SimpleMenu) ht.getSession().get(SimpleMenu.class, id);
	}

	private Criteria createCriteria(SimpleMenu searchForm) {
		Criteria criteria = ht.getSession().createCriteria(SimpleMenu.class);
		if(StringUtil.isEmpty(searchForm.getTitle())){
			criteria.add(Restrictions.ilike("title", "%"+searchForm.getTitle()+"%"));
		}
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<SimpleMenu> findSimpleMenusByExample(SimpleMenu simpleMenus,
			Paginate paginate,String sort,String dir) {
		Criteria criteria;
		if (paginate != null) {
			criteria = createCriteria(simpleMenus);
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = createCriteria(simpleMenus);
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		if(sort!=null&&!"".equals(sort)){
			if("ASC".equals(dir)){
				criteria.addOrder(Order.asc(sort));
			}else{
				criteria.addOrder(Order.desc(sort));
			}
		}
		return criteria.list();
	}

	@Transaction
	public void save(SimpleMenu simpleMenus) {
		ht.save((Serializable) ht.getSession().merge(simpleMenus));
	}
	
	public List<?> queryAll(String hql){
		Query query = ht.getSession().createQuery(hql);
		return query.list();
	}

	@Transaction
	public void delete(String ids) {
		if(ids.endsWith(",")){
			ids = ids.substring(0,ids.length()-1);
		}
	
		ht.getSession().createQuery(
				"delete from SimpleMenu where id in ("+ids+")").executeUpdate();
	}

	public Resource findResourceById(Integer id) {
		return ht.load(Resource.class, id);
	}


}
