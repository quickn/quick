package cn.quickj.codegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import cn.quickj.utils.QuickUtils;

public class Table {
	private String id;
	private String ObjectID;
	private String name;
	private String code;
	private LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>();
	private LinkedHashMap<String, Key> keys = new LinkedHashMap<String, Key>();
	private ArrayList<Reference> oneToMany = new ArrayList<Reference>();
	private ArrayList<Reference> manyToOne = new ArrayList<Reference>();

	private String packageName;
	//数据库表的前缀，如果PDM文件中的name已经有前缀，则取Java Name的时候去掉这个前缀。
	private String prefix;
	public void add(Key key) {
		keys.put(key.getId(), key);
	}

	public Table(String id) {
		this.id = id;
	}

	public void add(Column column) {
		columns.put(column.getId(), column);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("table==>{name:" + name);
		sb.append(",id:" + id);
		sb.append(",code:" + code);
		sb.append(",columns==>{\n");
		for (Column column : columns.values()) {
			sb.append("\t\t" + column.toString() + "\n");
		}
		sb.append("},keys==>");
		for (Key key : keys.values()) {
			sb.append("\t\t" + key.toString() + "\n");
		}
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObjectID() {
		return ObjectID;
	}

	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}

	public void setPrimaryKey(String id) {
		Key key = keys.get(id);
		if (key != null) {
			key.setPrimary(true);
			Collection<Column> keyColumns = key.getColumns();
			//TODO 复合主键的处理。
			for (Column column : keyColumns) {
				column.setPrimary(true);
			}
		}

	}

	public Column getColumn(String columnId) {
		return columns.get(columnId);
	}

	public ArrayList<Reference> getOneToMany() {
		return oneToMany;
	}

	public ArrayList<Reference> getManyToOne() {
		return manyToOne;
	}
	
	public void addOneToManyRef(Reference ref) {
		oneToMany.add(ref);
	}

	public void addManyToOneRef(Reference ref) {
		manyToOne.add(ref);
	}
	public String getClassName(){
		String tCode = code;
		if(prefix!=null && code.startsWith(prefix)){
			tCode = code.substring(prefix.length());
		}
		return QuickUtils.getCapitalName(QuickUtils.getPropertyName(tCode));
	}
	public String getJavaName(){
		String tCode = code;
		if(prefix!=null && code.startsWith(prefix)){
			tCode = code.substring(prefix.length());
		}
		return QuickUtils.getPropertyName(tCode);
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getPackageName() {
		return packageName;
	}
	public LinkedHashMap<String, Column> getColumns() {
		return columns;
	}
	public String getSerialVersionUID(){
		return "1"+RandomStringUtils.randomNumeric(10);
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public List<Column> getSearchableColumns(){
		ArrayList<Column> result = new ArrayList<Column>();
		for(Column column :columns.values()){
			if(column.isSearchable())
				result.add(column);
		}
		return result;
	}
	public List<Column> getNonReferenceColumns(){
		ArrayList<Column> result = new ArrayList<Column>();
		for(Column column :columns.values()){
			if(column.getReference()==null)
				result.add(column);
		}
		return result;
	}
}
