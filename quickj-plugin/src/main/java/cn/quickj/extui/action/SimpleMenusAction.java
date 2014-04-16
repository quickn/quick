package cn.quickj.extui.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.security.service.SimpleMenusService;
import cn.quickj.simpleui.model.SimpleMenu;

import com.google.inject.Inject;

public class SimpleMenusAction extends ExtBaseAction {
	@Inject
	private SimpleMenusService simpleMenusService;
	@Inject
	private SimpleMenu simpleMenus;

	@Inject
	private SimpleMenu parent;
	@Inject
	private Resource resource;

	public String list() {
		Paginate paginate = new Paginate(start, limit);
		Collection<SimpleMenu> simpleMenuss = simpleMenusService
				.findSimpleMenusByExample(simpleMenus, paginate, sort, dir);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("simpleMenuss", simpleMenuss);
		return toJson(data);
	}

	public String load(String id) {
		if (id != null)
			simpleMenus = simpleMenusService.getSimpleMenus(Integer
					.parseInt(id));
		return toJson(simpleMenus);
	}

	@SuppressWarnings("unchecked")
	public String listAll() {
		String hql = "from SimpleMenu";
		List<SimpleMenu> simpleMenuss = (List<SimpleMenu>) simpleMenusService
				.queryAll(hql);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", simpleMenuss.size());
		data.put("listAll", simpleMenuss);
		return toJson(data);
	}

	@SuppressWarnings("unchecked")
	public String reslistAll() {
		String hql = "from Resource";
		List<Resource> resources = (List<Resource>) simpleMenusService
				.queryAll(hql);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", resources.size());
		data.put("listAll", resources);
		return toJson(data);
	}

	public String save() {
		if (parent != null && parent.getId() != null) {
			SimpleMenu s = simpleMenusService.getSimpleMenus(parent.getId());
			simpleMenus.setParent(s);
		}
		if (resource != null && resource.getId() != null) {
			Resource r = simpleMenusService.findResourceById(resource.getId());
			simpleMenus.setResource(r);
		}
		simpleMenusService.save(simpleMenus);
		return toJson(null);
	}

	public String delete(String ids) {
		simpleMenusService.delete(ids);
		return toJson(null);
	}

}
