package cn.quickj.dict.action;

import java.util.ArrayList;
import java.util.List;

import cn.quickj.dict.model.DictType;
import cn.quickj.dict.model.Dictionary;
import cn.quickj.dict.service.DictionaryService;
import cn.quickj.simpleui.action.SimpleUIActionSupport;

import com.google.inject.Inject;

public class TypeAction extends SimpleUIActionSupport {
	@Inject
	DictionaryService dictionaryService;
	@Inject
	DictType dictType;
	List<Dictionary> dictList;
	List<DictType> dictTypes;
	private String dictIds;
	@Inject
	Dictionary dictionary;
	private boolean ajax = false;
	private String ids;

	public void index() {
		dictionaryService.init();
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("type_list.html");
	}
	public void ajax(){
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("ajax_type_list.html");
	}

	public void search() {
		dictTypes = dictionaryService.getDictType(dictType.getName());		
		render("type_list.html");
	}

	public void create() {
		dictList = dictionaryService.getDicts(getPaginate());
		render("type_form.html");
	}

	public void edit(String id) {
		dictType = dictionaryService.getDictTypeById(Integer.valueOf(id));
		dictList = dictionaryService.getDicts(getPaginate());
		render("type_form.html");
	}

	public void delete(String id) {
		String msg=null;
		try{
		dictionaryService.deleteDictType(Integer.valueOf(id));
		}catch(Exception e){
			msg = "该数据与其它数据关联，无法删除！";
		}
		setMessage(msg);
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("type_list.html");
	}

	public void delete() {
		if (ids != null && ids.length() > 0) {
			for (String id : ids.split(",")) {
				dictionaryService.deleteDictType(Integer.valueOf(id));
			}
			setMessage("删除成功！");
		}
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("ajax_type_list.html");
	}

	public void add(String id) {
		dictType = dictionaryService.getDictTypeById(Integer.valueOf(id));
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		dictList = dictionaryService.getDicts(getPaginate());
		dictionary.setDictType(dictType);
		render("ajax_form.html");
	}

	public void show(String id) {
		dictType = dictionaryService.getDictTypeById(Integer.valueOf(id));
		dictList = new ArrayList<Dictionary>();
		for (Dictionary d : dictType.getDictionaries()) {
			dictList.add(d);
		}
		if(ajax)
			render("ajax_list.html");
		else
			render("list.html");
	}

	/**
	 * 类型dictTypes
	 */
	public void save() {
		if (dictType.getId() == null) {
			dictType = dictionaryService.createDictType(dictType, dictIds);
		} else {
			dictType = dictionaryService.updateDictType(dictType, dictIds);
		}
		dictTypes = dictionaryService.getDictTypes(getPaginate());
		render("type_list.html");
	}

	/**
	 * 按类别取字典
	 * 
	 * @param typename
	 */
	public void getDict(String typename) {
		dictList = dictionaryService.getDicts(typename);
		render("type_list.html");
	}

	public List<Dictionary> getDictList() {
		return dictList;
	}

	public void setDictList(List<Dictionary> dictList) {
		this.dictList = dictList;
	}

	public DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}

	public void setDictTypes(List<DictType> dictTypes) {
		this.dictTypes = dictTypes;
	}

	public DictType getDictType() {
		return dictType;
	}

	public List<DictType> getDictTypes() {
		return dictTypes;
	}

	public boolean hasDict(Integer id) {
		for (Dictionary d : dictType.getDictionaries()) {
			if (d.getId().equals(id))
				return true;
		}
		return false;
	}

	public String getDictIds() {
		return dictIds;
	}

	public void setDictIds(String dictIds) {
		this.dictIds = dictIds;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
