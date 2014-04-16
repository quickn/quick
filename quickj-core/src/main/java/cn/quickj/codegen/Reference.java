package cn.quickj.codegen;

public class Reference {
	private String id;
	private String name;
	private String code;
	private String foreignKeyConstraintName;
	private Table parent;
	private Table child;
	private Column parentColumn;
	private Column childColumn;
	public Reference(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getForeignKeyConstraintName() {
		return foreignKeyConstraintName;
	}
	public void setForeignKeyConstraintName(String foreignKeyConstraintName) {
		this.foreignKeyConstraintName = foreignKeyConstraintName;
	}
	public Table getParent() {
		return parent;
	}
	public void setParent(Table parent) {
		this.parent = parent;
		parent.addOneToManyRef(this);
	}
	public Table getChild() {
		return child;
	}
	public void setChild(Table child) {
		this.child = child;
		child.addManyToOneRef(this);
	}
	public Column getParentColumn() {
		return parentColumn;
	}
	public void setParentColumn(Column parentColumn) {
		this.parentColumn = parentColumn;
	}
	public Column getChildColumn() {
		return childColumn;
	}
	public void setChildColumn(Column childColumn) {
		this.childColumn = childColumn;
		this.childColumn.setReference(this);
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("reference==>{name:"+name);
		sb.append(",id:"+id);
		sb.append(",code:"+code);
		sb.append(",parent:"+parent.getName()+"("+parentColumn.getName()+")");
		sb.append(",child:"+child.getName()+"("+childColumn.getName()+")");
		return sb.toString();
	}
	
}
