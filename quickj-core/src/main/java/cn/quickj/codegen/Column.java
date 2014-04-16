package cn.quickj.codegen;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.quickj.action.LabelValue;
import cn.quickj.utils.QuickUtils;

public class Column {
	private String id;
	private String ObjectID;
	private String format;
	private String name;
	private String code;
	private String list;
	private String dataType;
	private String comment;
	private Integer length;
	private Integer precision;
	private Integer scale;
	private boolean mandatory = false;
	private boolean primary = false;
	private Reference reference;
	/**
	 * 如果是下拉框，或者radio group,则需要一个值列表。
	 */
	private List<LabelValue> values;
	private static final String[] STRING_TYPE = { "varchar", "varchar2",
			"nvarchar", "nvarchar2", "clob", "char", "text", "tinytext" };
	private static final String[] DATE_TYPE = { "date", "datetime", "time",
			"timestamp" };
	private static final String[] INT_TYPE = { "int", "integer" };
	private static final String[] LONG_TYPE = { "number", "numeric", "bigint" };
	private static final String[] BOOLEAN_TYPE = { "smallint", "tinyint", "bit" };
	private static final String[] DOUBLE_TYPE = { "numeric", "real", "double",
			"number", "float" };
	static private final Logger logger = LoggerFactory.getLogger(Column.class);

	public Column(String id) {
		this.id = id;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
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

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		int pos = dataType.indexOf("(");
		if (pos != -1)
			dataType = dataType.substring(0, pos);
		this.dataType = dataType;
		// 更新进度
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{name:" + name);
		sb.append(",id:" + id);
		sb.append(",code:" + code);
		sb.append(",format:" + format);
		sb.append(",list:" + list);
		sb.append(",dataType:" + dataType);
		sb.append(",length:" + length);
		sb.append(",mandatory:" + mandatory + "}");
		return sb.toString();
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
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

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getJavaName() {
		if (primary)
			return "id";
		if (reference != null)
			return reference.getParent().getJavaName();
		// 如果是布尔类型的，去掉IS_开头。
		if (code.toUpperCase().startsWith("IS_")) {
			return QuickUtils.getPropertyName(code.substring(3));
		}
		return QuickUtils.getPropertyName(code);
	}

	public String getJavaType() {
		// TODO 需要完善使用类似于Hibernate的配置文件进行。
		if (reference != null) {
			return reference.getParent().getClassName();
		}
		if ("char".equalsIgnoreCase(dataType) && length == 1)
			return "Boolean";
		for (String type : STRING_TYPE) {
			if (type.equalsIgnoreCase(dataType))
				return "String";
		}
		for (String type : DATE_TYPE) {
			if (type.equalsIgnoreCase(dataType))
				return "Date";
		}

		for (String type : INT_TYPE) {
			if (type.equalsIgnoreCase(dataType))
				return "Integer";
		}
		for (String type : LONG_TYPE) {
			if (type.equalsIgnoreCase(dataType)) {
				if (scale != null) {
					precision = length;
					length = null;
					return "Double";
				} else
					return "Long";
			}

		}
		for (String type : BOOLEAN_TYPE) {
			if (type.equalsIgnoreCase(dataType))
				return "Boolean";
		}
		for (String type : DOUBLE_TYPE) {
			dataType = dataType.toLowerCase();
			if (dataType.startsWith(type)) {
				precision = length;
				length = null;
				return "Double";
			}
		}
		logger.warn("未知的数据类型:" + dataType);
		return "Serializable";
	}

	public Integer getPrecision() {
		return precision;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setComment(String comment) {
		this.comment = comment;
		String[] dicts = comment.split(",");
		if (dicts.length > 0) {
			values = new ArrayList<LabelValue>();
			for (String dict : dicts) {
				String[] lv = dict.split(":");
				if (lv.length == 2) {
					values.add(new LabelValue(lv[1], lv[0]));
				}
			}
		}
	}

	public String getComment() {
		return comment;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public String getGetterName() {
		if (primary)
			return "getId";
		else if (reference != null) {
			return "get" + reference.getParent().getClassName();
		}
		if (code.toUpperCase().startsWith("IS_")) {
			return "get"
					+ QuickUtils.capitalName(QuickUtils.getPropertyName(code
							.substring(3)));
		}

		return "get" + QuickUtils.capitalName(QuickUtils.getPropertyName(code));
	}

	public String getSetterName() {
		if (primary)
			return "setId";
		if (reference != null) {
			return "set" + reference.getParent().getClassName();
		}
		if (code.toUpperCase().startsWith("IS_")) {
			return "set"
					+ QuickUtils.capitalName(QuickUtils.getPropertyName(code
							.substring(3)));
		}
		return "set" + QuickUtils.capitalName(QuickUtils.getPropertyName(code));
	}

	public String getTemporal() {
		if (dataType.equalsIgnoreCase("date"))
			return "DATE";
		if (dataType.equalsIgnoreCase("time"))
			return "TIME";
		if (dataType.equalsIgnoreCase("timestamp")
				|| dataType.equalsIgnoreCase("datetime"))
			return "TIMESTAMP";
		return null;
	}

	public boolean isSearchable() {
		// 格式里包含"查询",并且不是外键字段则可以作为搜索条件。
		// 如果外键需要查询，则应该手工编写代码。
		if (format != null && format.contains("查询") && reference == null)
			return true;
		return false;
	}

	public List<LabelValue> getValues() {
		return values;
	}

	/**
	 * 
	 * 显示类型，如果不指定，则由数据类型决定。 <br />
	 * 类型包括:输入框,密码,日期,复选框,下拉框,单选框,文本区,起止日期
	 * 
	 * @return
	 */
	public String getDisplayType() {
		String javaType = getJavaType();
		// 如果format里指定了显示的格式，则直接使用指定的格式并返回，否则根据数据类型决定。
		String types = "输入框,密码,日期,复选框,下拉框,单选框,文本区,文件";
		if (format != null) {
			String[] formats = format.split(",");
			for (String f : formats) {
				if (types.contains(f))
					return f;
			}
		}
		if (javaType.equals("Date")) {
			return "日期";
		}
		if (javaType.equals("Boolean")) {
			return "复选框";
		} else if (values != null) {
			return "下拉框";
		} else
			return "输入框";
	}

}
