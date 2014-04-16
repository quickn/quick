package cn.quickj.codegen;
import java.util.Collection;
import java.util.HashMap;


public class Key {
	private String id;
	private String ObjectID;
	private String name;
	private String code;
	private boolean primary =false;
	private HashMap<String,Column> keyColumns = new HashMap<String, Column>();
	public void addColumn(Column column){
		if(column!=null)
			keyColumns.put(column.getId(), column);
	}
	public Key(String id) {
		this.id = id;
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
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	@Override
	public String toString() {
		StringBuffer sb =new StringBuffer();
		sb.append("{name:"+name);
		sb.append(",code:"+code);
		sb.append(",primary:"+primary);
		sb.append(",id:"+id);
		sb.append(",column:{");
		for (Column column : keyColumns.values()) {
			sb.append(column.getId()+",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		return sb.toString();
	}
	public Collection<Column>getColumns(){
		return keyColumns.values();
	}
}
