package cn.quickj.extui.action;

import java.util.Collection;

import java.util.HashMap;
import java.util.List;

import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.Resource;
import cn.quickj.security.service.ResourceService;

import com.google.inject.Inject;

public class ResourceAction extends ExtBaseAction {
	@Inject
	ResourceService resourceService;
	@Inject
	private Resource resource;
	@Inject
	private Resource parent;// 添加修改父类
	@Inject
	private Resource par;// 查询父类

	public String listAll() {
		List<Resource> res = resourceService.listFirstResources();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("ress", res);
		return toJson(data);
	}

	public String list() {
		Paginate paginate = new Paginate(start, limit);
		if (par != null && par.getId() != null) {
			resource.setParent(par);
		}
		Collection<Resource> resources = resourceService.findResourceByExample(
				resource, paginate);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("resources", resources);
		return toJson(data);
	}

	public String load(String id) {
		if (id != null)
			resource = resourceService.getResource(Integer.parseInt(id));
		return toJson(resource);
	}

	public String save() {
		if (parent != null && parent.getId() != null) {
			resource.setParent(parent);
		}
		resourceService.save(resource);
		return toJson(null);
	}

	public String delete(String ids) {
		resourceService.delete(ids);
		return toJson(null);
	}

}
