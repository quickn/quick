package cn.quickj.dict.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import cn.quickj.annotation.Transaction;
import cn.quickj.dict.model.DictType;
import cn.quickj.dict.model.Dictionary;
import cn.quickj.hibernate.HibernateTemplate;
import cn.quickj.hibernate.Paginate;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author lbj
 * 
 */
@Singleton
public class DictionaryService {
	private ConcurrentHashMap<String, List<Dictionary>> dictCaches;
	private ConcurrentHashMap<Integer, List<Dictionary>> dictLevelCaches;
	private static Log log = LogFactory.getLog(DictionaryService.class);

	@Inject
	private HibernateTemplate ht;

	public boolean queryNameExists(String name) {
		boolean status = false;
		String hql = "from DictType where name='" + name + "'";
		if (ht.query(hql).list().size() == 0) {
			status = true;
		}
		return status;
	}

	public boolean queryNameExistsD(String name) {
		boolean status = false;
		String hql = "from Dictionary where name='" + name + "'";
		if (ht.query(hql).list().size() == 0) {
			status = true;
		}
		return status;
	}

	public boolean queryValueExists(String value) {
		boolean status = false;
		String hql = "from Dictionary where value='" + value + "'";
		if (ht.query(hql).list().size() == 0) {
			status = true;
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public Collection<DictType> findDictTypeByExample(DictType dictType,
			Paginate paginate) {
		Criteria criteria;
		if (paginate != null) {
			criteria = ht.getSession().createCriteria(DictType.class);
			if (dictType.getName() != null)
				criteria = criteria.add(Restrictions.ilike("name", "%"
						+ dictType.getName() + "%"));
			criteria.setProjection(Projections.rowCount());
			paginate.setTotal(Integer.parseInt(criteria.list().get(0)
					.toString()));
		}
		criteria = ht.getSession().createCriteria(DictType.class);
		if (dictType.getName() != null)
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + dictType.getName() + "%"));
		criteria.setFirstResult(paginate.getOffset());
		criteria.setMaxResults(paginate.getCount());
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<Dictionary> findDictByExample(Dictionary searchForm,
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

	private Criteria createCriteria(Dictionary searchForm) {
		Criteria criteria = ht.getSession().createCriteria(Dictionary.class);
		if (searchForm.getName() != null)
			criteria = criteria.add(Restrictions.ilike("name",
					"%" + searchForm.getName() + "%"));
		if (searchForm.getDictType() != null
				&& searchForm.getDictType().getId() != null) {
			criteria = criteria.add(Restrictions.eq("dictType.id", searchForm
					.getDictType().getId()));
		}

		if (searchForm.getParent() != null
				&& searchForm.getParent().getId() != null) {
			criteria = criteria.add(Restrictions.eq("parent.id", searchForm
					.getParent().getId()));
		}
		if (searchForm.getStatus() != null) {
			criteria = criteria.add(Restrictions.eq("status",
					searchForm.getStatus()));
		}
		return criteria;
	}

	/*
	 * 字典
	 */
	@Transaction
	public Dictionary createDict(Dictionary d) {
		ht.save(d);
		reload();
		return d;
	}

	@Transaction
	public Dictionary updateDict(Dictionary d) {
		ht.update(d);
		reload();
		return d;
	}

	@Transaction
	public void deleteDict(int id) {
		String hql = "delete from Dictionary where id=" + id;
		ht.query(hql).executeUpdate();
	}

	public Dictionary getDictByTypeValue(String type, String value) {
		return (Dictionary) ht
				.query("from Dictionary d where d.value = ? and d.dictType.name = ?")
				.setString(0, value).setString(1, type).uniqueResult();
	}

	/*
	 * 类型
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transaction
	public DictType createDictType(DictType dt, String ids) {
		if (ids != null && ids.length() > 0) {
			Collection<Dictionary> list = ht.query(
					"from Dictionary d where d.id in(" + ids
							+ ") order by d.sort").list();
			if (list.size() > 0) {
				HashSet dicts = new HashSet();
				dicts.addAll(list);
				dt.setDictionaries(dicts);
			}
		}
		ht.save(dt);
		reload();

		return dt;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transaction
	public DictType updateDictType(DictType dt, String ids) {
		if (ids != null && ids.length() > 0) {
			Collection<Dictionary> list = ht.query(
					"from Dictionary d where d.id in(" + ids
							+ ") order by d.sort").list();
			if (list.size() > 0) {
				HashSet dicts = new HashSet();
				dicts.addAll(list);
				dt.setDictionaries(dicts);
			}
		}
		ht.update(dt);
		reload();
		return dt;
	}

	@Transaction
	public void deleteDictType(int id) {
		ht.delete((DictType) ht.load(DictType.class, id));
		// TODO 删除相关字典信息。
		reload();
	}

	/**
	 * 默认取所有字典数据,不用于多级联动
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionary> getDicts(Paginate paginate) {
		int count = ((Long) ht.query("select count(*) from Dictionary")
				.uniqueResult()).intValue();
		paginate.setTotal(count);
		List<Dictionary> result = ht
				.query("from Dictionary dict order by dict.sort")
				.setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getCount()).list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<DictType> getDictTypes(Paginate paginate) {
		int count = ((Long) ht.query("select count(*) from DictType")
				.uniqueResult()).intValue();
		paginate.setTotal(count);
		List<DictType> result = ht.query("from DictType dict")
				.setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getCount()).list();
		return result;
	}

	/**
	 * 取父类下的所有子字典,用于多级联动的情况。
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionary> getDicts(Integer id) {
		if (dictLevelCaches == null || dictCaches == null)
			init();
		List<Dictionary> result = dictLevelCaches.get(id);
		if (result == null) {
			result = ht
					.query("from Dictionary dict where dict.parent.id=?  order by dict.sort")
					.setInteger(0, id).list();
			if (result != null) {
				dictLevelCaches.put(id, result);
				return result;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Dictionary> getDictsByParentId(Integer parentId,
			Paginate paginate) {
		String hql = " from Dictionary dict where dict.parent.id=?  ";
		List<Dictionary> result = ht.query(hql + " order by dict.sort ")
				.setInteger(0, parentId).setFirstResult(paginate.getOffset())
				.setMaxResults(paginate.getPagesize()).list();
		Long total = (Long) ht.query("select count(*) " + hql)
				.setInteger(0, parentId).uniqueResult();
		paginate.setTotal(total.intValue());
		return result;
	}

	/**
	 * 按照数据字典的类型取对应的字典列表，比如getDicts("质量等级")就可以获取
	 * 质量等级类别对应的数据字典列表，用名称而不是类型id主要是为了方便使用。
	 * 
	 * @param dictTypeName
	 * @return
	 */
	public List<Dictionary> getDicts(String dictTypeName) {
		if (dictLevelCaches == null || dictCaches == null)
			init();
		List<Dictionary> result = dictCaches.get(dictTypeName);
		if (result == null && log.isDebugEnabled()) {
			log.debug(dictTypeName + "找不到对应的数据字典!");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Dictionary> getDict(String name) {
		return ht
				.query("from Dictionary dict where dict.name like ? order by dict.sort")
				.setString(0, "%" + name + "%").list();
	}

	public Dictionary getDictById(int id) {
		return (Dictionary) ht.query("from Dictionary dict where dict.id=?")
				.setInteger(0, id).list().get(0);
	}

	@SuppressWarnings("unchecked")
	public List<DictType> getDictType(String name) {
		return ht.query("from DictType dict where dict.name like ?")
				.setString(0, "%" + name + "%").list();
	}

	public DictType getDictTypeById(int id) {
		return (DictType) ht.query("from DictType dict where dict.id=?")
				.setInteger(0, id).list().get(0);
	}

	/**
	 * 初始化load所有的Dictionary到dictCaches中，并按照DictType索引，这样可以方便的取得 对应类型的字典信息。
	 * 同时字典本身也用每个字典值做为key，用于通过值来快速或得字典信息，用于列表的数据的 字典解释。
	 */
	@SuppressWarnings({ "rawtypes" })
	public void init() {
		dictCaches = new ConcurrentHashMap<String, List<Dictionary>>();
		dictLevelCaches = new ConcurrentHashMap<Integer, List<Dictionary>>();
		Iterator iter = ht.query("from DictType").iterate();
		while (iter.hasNext()) {
			DictType dt = (DictType) iter.next();
			Set<Dictionary> dictionaries = dt.getDictionaries();
			if (dictionaries != null) {
				Iterator iter2 = dt.getDictionaries().iterator();
				ArrayList<Dictionary> dicts = new ArrayList<Dictionary>();
				while (iter2.hasNext()) {
					Dictionary dict = (Dictionary) iter2.next();
					dicts.add(dict);
				}
				dictCaches.put(dt.getName(), dicts);
			}
		}
	}

	public void reload() {
		init();
	}

	public List<?> queryAll(String hql) {
		Query query = ht.getSession().createQuery(hql);
		return query.list();
	}

	/*
	 * @SuppressWarnings("unchecked") public Dictionary
	 * findDictionaryByName(String name) { String hql =
	 * "from Dictionary d where d.name = ?"; List<Dictionary> list =
	 * ht.query(hql).setString(0, name).list(); if (!list.isEmpty()) { return
	 * list.get(0); } return null; }
	 */
	// 列出字典类型的id，列出所属类id下的字典
	@SuppressWarnings("unchecked")
	public List<Dictionary> listDictionarys(String id) {
		String hql = "from Dictionary where dictType.id = " + id
				+ " order by id asc";
		return ht.query(hql).list();
	}

}
