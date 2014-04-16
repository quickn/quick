package cn.quickj.extui.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.quickj.dict.model.DictType;
import cn.quickj.dict.model.Dictionary;
import cn.quickj.dict.service.DictionaryService;
import cn.quickj.hibernate.Paginate;

import com.google.inject.Inject;

public class DictAction extends ExtBaseAction {
	@Inject
	private DictionaryService dictService;
	@Inject
	private Dictionary dict;
	@Inject
	private Dictionary parent;
	@Inject
	private Dictionary part;

	public String list() {
		Paginate paginate = new Paginate(start, limit);
		dict.setParent(part);
		Collection<Dictionary> sysDictionarys = dictService.findDictByExample(
				dict, paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("Dicts", sysDictionarys);
		return toJson(data);
	}

	public String queryNameExists(String name) {
		boolean status = false;
		status = dictService.queryNameExistsD(name);
		return toJson("{'status':" + status + "}");
	}

	public String queryValueExists(String value) {
		boolean status = false;
		status = dictService.queryValueExists(value);
		return toJson("{'status':" + status + "}");
	}

	@SuppressWarnings("unchecked")
	public String listAllTypes() {
		String hql = "from DictType d ";
		Collection<DictType> types = (Collection<DictType>) dictService
				.queryAll(hql);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("types", types);
		return toJson(data);
	}

	public String load(String id) {
		if (id != null)
			dict = dictService.getDictById(Integer.parseInt(id));
		return toJson(dict);
	}

	public String save() {
		if (parent.getId() != null) {
			dict.setParent(parent);
		}
		dictService.createDict(dict);
		return toJson(null);
	}

	public String delete(String ids) {
		if (ids != null && ids.length() > 0 && ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
			String aa[] = ids.split(",");
			for (int i = 0; i < aa.length; i++) {
				dictService.deleteDict(Integer.valueOf(aa[i]));
			}
		}
		return toJson(null);
	}

	public String list(String id) {
		Paginate paginate = new Paginate(start, limit);
		Collection<Dictionary> sysDictionarys = dictService.listDictionarys(id);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("Dicts", sysDictionarys);
		return toJson(data);
	}

	/*****
	 * 根据分类查出没有父类的字典
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public String noParentDicts(String type) {
		String hql = "from Dictionary d where d.dictType.id = " + type
				+ " and d.parent.id is null";
		List<Dictionary> dicts = (List<Dictionary>) dictService.queryAll(hql);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", dicts.size());
		data.put("noParentDicts", dicts);
		return toJson(data);
	}

	/*****
	 * 根据分类查出字典
	 * 
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public String findByType(String type) {
		String hql = "from Dictionary d where d.dictType.id = " + type;
		List<Dictionary> dicts = (List<Dictionary>) dictService.queryAll(hql);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", dicts.size());
		data.put("dictsByType", dicts);
		return toJson(data);
	}

	public Dictionary getDict() {
		return dict;
	}

	public Dictionary getParent() {
		return parent;
	}
	
	public Dictionary getPart() {
		return part;
	}
}
