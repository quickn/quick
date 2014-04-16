package cn.quickj.extui.action;

import java.util.Collection;
import java.util.HashMap;

import cn.quickj.dict.model.DictType;
import cn.quickj.dict.service.DictionaryService;
import cn.quickj.hibernate.Paginate;

import com.google.inject.Inject;

public class DictTypeAction extends ExtBaseAction {
	@Inject
	private DictionaryService dicService;
	@Inject
	private DictType dictType;

	public String list() {
		Paginate paginate = new Paginate(start, limit);
		Collection<DictType> sysDictTypes = dicService.findDictTypeByExample(
				dictType, paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("dictTypes", sysDictTypes);
		return toJson(data);
	}

	public String queryNameExists(String name) {
		boolean status = false;
		status = dicService.queryNameExists(name);
		return toJson("{'status':" + status + "}");
	}

	public String load(String id) {
		if (id != null)
			dictType = dicService.getDictTypeById(Integer.parseInt(id));
		return toJson(dictType);
	}

	public String save() {
		dicService.createDictType(dictType, null);
		return toJson(null);
	}

	public String delete(String ids) {
		if (ids != null && ids.length() > 0 && ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
			String aa[] = ids.split(",");
			for (int i = 0; i < aa.length; i++) {
				dicService.deleteDictType(Integer.valueOf(aa[i]));
			}
		}
		return toJson(null);
	}

}
