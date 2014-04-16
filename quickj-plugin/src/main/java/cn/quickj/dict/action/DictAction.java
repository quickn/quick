package cn.quickj.dict.action;

import java.util.List;

import cn.quickj.dict.model.DictType;
import cn.quickj.dict.model.Dictionary;
import cn.quickj.dict.service.DictionaryService;
import cn.quickj.simpleui.action.SimpleUIActionSupport;

import com.google.inject.Inject;

public class DictAction extends SimpleUIActionSupport {
	public static final String CLICK_PATH = "QUICKJ_CLICK_PATH";
	@Inject
	DictionaryService dictionaryService;
	List<Dictionary> dictList;
	List<Dictionary> childList;
	List<DictType> dictTypes;
	@Inject
	DictType dictType;
	@Inject
	Dictionary dictionary;
	private String ids;

	public void index() {
		dictionaryService.init();
		dictList = dictionaryService.getDicts(getPaginate());
		render("list.html");
	}

	public void ajax() {
		dictList = dictionaryService.getDicts(getPaginate());
		render("ajax_list.html");
	}

	/**
	 * 按字典的名称取字典
	 */
	public void search() {
		dictList = dictionaryService.getDict(dictionary.getName());
		render("list.html");
	}

	/**
	 * 根据逻辑ID取字典下的所有子集
	 * 
	 * @param id
	 */
	public void getChild(String id) {
		childList = dictionaryService.getDicts(Integer.valueOf(id));
		render("list.html");
	}

	public void edit(String id) {
		dictionary = dictionaryService.getDictById(Integer.valueOf(id));
		dictList = dictionaryService.getDicts(getPaginate());
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("form.html");
	}

	public void create() {
		dictList = dictionaryService.getDicts(getPaginate());
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("form.html");
	}

	/**
	 * save method
	 */
	public void save() {
		if (dictionary.getId() == null) {
			dictionary = dictionaryService.createDict(dictionary);
		} else {
			dictionary = dictionaryService.updateDict(dictionary);
		}
		dictList = dictionaryService.getDicts(getPaginate());
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("list.html");
	}

	public void delete(String id) {
		dictionaryService.deleteDict(Integer.valueOf(id));
		dictList = dictionaryService.getDicts(getPaginate());
		render("list.html");
	}

	/**
	 * 删除选中
	 */
	public void delete() {
		if (ids != null && ids.length() > 0) {
			for (String id : ids.split(",")) {
				dictionaryService.deleteDict(Integer.valueOf(id));
			}
			setMessage("删除成功！");
		}
		dictList = dictionaryService.getDicts(getPaginate());
		render("ajax_list.html");
	}
	public List<Dictionary> getChildList() {
		return childList;
	}

	public void setChildList(List<Dictionary> childList) {
		this.childList = childList;
	}

	public DictType getDictType() {
		return dictType;
	}

	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}

	public DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public List<Dictionary> getDictList() {
		return dictList;
	}

	public void setDictList(List<Dictionary> dictList) {
		this.dictList = dictList;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public List<DictType> getDictTypes() {
		return dictTypes;
	}

	public void setDictTypes(List<DictType> dictTypes) {
		this.dictTypes = dictTypes;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

}
