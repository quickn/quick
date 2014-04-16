package cn.quickj.codegen;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class PDMHandler extends DefaultHandler2 {
	private static final String DBMS = "/" + Target.dbms.id;

	private static final String DBMS_NAME = DBMS + "/" + Target.dbmsName.id;

	private static final String DBMS_CODE = DBMS + "/" + Target.dbmsCode.id;

	private static final String TABLES = "/" + Target.tables.id;

	private static final String TABLES_TABLE = TABLES + "/" + Target.table.id;

	private static final String TABLES_TABLE_NAME = TABLES_TABLE + "/"
			+ Target.tableName.id;

	private static final String TABLES_TABLE_CODE = TABLES_TABLE + "/"
			+ Target.tableCode.id;
	private static final String REFERENCES = "/" + Target.references.id;

	private static final String REFERENCES_REFERENCE = REFERENCES + "/"
			+ Target.reference.id;

	private static final String REFERENCES_REFERENCE_NAME = REFERENCES_REFERENCE
			+ "/" + Target.refName.id;

	private static final String REFERENCES_REFERENCE_CODE = REFERENCES_REFERENCE
			+ "/" + Target.refCode.id;
	private static final String REFERENCES_REFERENCE_OBJECT2 = REFERENCES_REFERENCE
			+ "/" + Target.refObject2.id;
	private static final String REFERENCES_REFERENCE_OBJECT2_TABLE = REFERENCES_REFERENCE_OBJECT2
			+ "/" + Target.table.id;

	private static final String TABLES_TABLE_COLUMNS = TABLES_TABLE + "/"
			+ Target.columns.id;

	private static final String TABLES_TABLE_COLUMNS_COLUMN = TABLES_TABLE_COLUMNS
			+ "/" + Target.column.id;

	private static final String TABLES_TABLE_KEYS = TABLES_TABLE + "/"
			+ Target.keys.id;

	private static final String TABLES_TABLE_KEYS_KEY = TABLES_TABLE_KEYS + "/"
			+ Target.key.id;
	private static final String TABLES_TABLE_KEYS_KEY_COLUMNS = TABLES_TABLE_KEYS_KEY
			+ "/" + Target.keyColumns.id;
	private static final String TABLES_TABLE_KEYS_KEY_COLUMNS_COLUMN = TABLES_TABLE_KEYS_KEY_COLUMNS
			+ "/" + Target.column.id;

	private static final String TABLES_TABLE_PRIMARYKEYS = TABLES_TABLE + "/"
			+ Target.primaryKey.id;
	private static final String TABLES_TABLE_PRIMARYKEYS_KEY = TABLES_TABLE_PRIMARYKEYS
			+ "/" + Target.key.id;

	private static final String REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT1 = REFERENCES_REFERENCE
			+ "/c:Joins/o:ReferenceJoin" + "/" + Target.refObject1.id;
	private static final String REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT2 = REFERENCES_REFERENCE
			+ "/c:Joins/o:ReferenceJoin" + "/" + Target.refObject2.id;
	private static final String REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT1_COLUMN = REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT1
			+ "/" + Target.column.id;
	private static final String REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT2_COLUMN = REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT2
			+ "/" + Target.column.id;

	private static Log log = LogFactory.getLog(PDMHandler.class);

	// 是否正在处理
	private boolean doing;

	// 结果对象
	private DatabaseModel models = new DatabaseModel();

	// 上一次的表对象
	private Table lastTable;

	// 上一次的列对象
	private Column lastColumn;

	// 上一次处理的对象
	private Target lastObject;

	// 上一次路径,元素之间用/进行分隔,属性则采用＠来标识，这是借用了XPath的规范
	private String lastPath = "";

	private Key lastKey;

	private Reference lastRef;

	/** */
	/**
	 * 开始文档解析
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		log.debug("powerdesigner pdm document parse start.");
	}

	/** */
	/**
	 * 结束文档解析
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
		log.debug("powerdesigner pdm document parse finish.");
	}

	/** */
	/**
	 * 启动元素解析
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		// 首先处理dbms的解释
		if (Target.dbms.id.equals(name) || Target.tables.id.equals(name)
				|| Target.references.id.equals(name))
			doing = true;

		// 如果没有开始，则直接退出
		if (!doing)
			return;

		// 设置路径
		String tempPath = lastPath + "/" + name;

		// System.out.println("开始解析:" + name);
		// DBMS
		if (lastPath.equals(DBMS)) {
			if (tempPath.equals(DBMS_NAME)) {
				lastObject = Target.dbmsName;
			} else if (tempPath.equals(DBMS_CODE)) {
				lastObject = Target.dbmsCode;
			}
			// REFERENCE
		} else if (lastPath.equals(REFERENCES)) {
			if (tempPath.equals(REFERENCES_REFERENCE)) {
				// 新建一个REFERENCE
				lastRef = new Reference(attributes.getValue("Id"));
				models.addRef(lastRef);
			}
			// REFERENCE的属性
		} else if (lastPath.equals(REFERENCES_REFERENCE)) {
			if (tempPath.equals(REFERENCES_REFERENCE_NAME)) {
				lastObject = Target.refName;
			} else if (tempPath.equals(REFERENCES_REFERENCE_CODE)) {
				lastObject = Target.refCode;
			}

		} /*
		 * else if (lastPath.equals(REFERENCES_REFERENCE_OBJECT1)) { if
		 * (tempPath.equals(REFERENCES_REFERENCE_OBJECT1_TABLE)) {
		 * lastRef.setParent(models.getTable(attributes.getValue("Ref"))); } }
		 * else if (lastPath.equals(REFERENCES_REFERENCE_OBJECT2)) { if
		 * (tempPath.equals(REFERENCES_REFERENCE_OBJECT2_TABLE)) {
		 * lastRef.setChild(models.getTable(attributes.getValue("Ref"))); } }
		 */else if (lastPath
				.equals(REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT1)) {
			if (tempPath
					.equals(REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT1_COLUMN)) {
				Table table = models.findTableByColumnId(attributes
						.getValue("Ref"));
				if (table != null) {
					lastRef.setParent(table);
					lastRef.setParentColumn(table.getColumn(attributes
							.getValue("Ref")));
				}
			}
		} else if (lastPath
				.equals(REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT2)) {
			if (tempPath
					.equals(REFERENCES_REFERENCE_JOINS_ReferenceJoin_OBJECT2_COLUMN)) {
				Table table = models.findTableByColumnId(attributes
						.getValue("Ref"));
				if (table != null) {
					lastRef.setChild(table);
					lastRef.setChildColumn(table.getColumn(attributes
							.getValue("Ref")));
				}
			}
		} else if (lastPath.equals(REFERENCES_REFERENCE_OBJECT2)) {
			if (tempPath.equals(REFERENCES_REFERENCE_OBJECT2_TABLE)) {
				lastRef.setChild(models.getTable(attributes.getValue("Ref")));
			}
		} else if (lastPath.equals(TABLES)) {
			if (tempPath.equals(TABLES_TABLE)) {
				// 新建一个表
				lastTable = new Table(attributes.getValue("Id"));
				models.add(lastTable);
			}
			// TABLE的属性
		} else if (lastPath.equals(TABLES_TABLE)) {
			if (tempPath.equals(TABLES_TABLE_NAME)) {
				lastObject = Target.tableName;
			} else if (tempPath.equals(TABLES_TABLE_CODE)) {
				lastObject = Target.tableCode;
			}
			// 列
		} else if (lastPath.equals(TABLES)) {
			if (tempPath.equals(TABLES_TABLE)) {
				// 新建一个表
				lastTable = new Table(attributes.getValue("Id"));
				models.add(lastTable);
			}
			// TABLE的属性
		} else if (lastPath.equals(TABLES_TABLE)) {
			if (tempPath.equals(TABLES_TABLE_NAME)) {
				lastObject = Target.tableName;
			} else if (tempPath.equals(TABLES_TABLE_CODE)) {
				lastObject = Target.tableCode;
			}

		} else if (lastPath.equals(TABLES)) {
			if (tempPath.equals(TABLES_TABLE)) {
				// 新建一个表
				lastTable = new Table(attributes.getValue("Id"));
				models.add(lastTable);
			}
			// TABLE的属性
		} else if (lastPath.equals(TABLES_TABLE)) {
			if (tempPath.equals(TABLES_TABLE_NAME)) {
				lastObject = Target.tableName;
			} else if (tempPath.equals(TABLES_TABLE_CODE)) {
				lastObject = Target.tableCode;
			}
			// 列
		}
		// 列
		else if (lastPath.equals(TABLES_TABLE_COLUMNS)) {
			if (tempPath.equals(TABLES_TABLE_COLUMNS_COLUMN)) {
				// 新建列
				lastColumn = new Column(attributes.getValue("Id"));
				lastTable.add(lastColumn);
			}
		} else if (lastPath.equals(TABLES_TABLE_COLUMNS_COLUMN)) {
			// 只处理列的属性
			lastObject = findTarget(Group.column, name);
		} else if (lastPath.equals(TABLES_TABLE_KEYS_KEY)) {
			lastObject = findTarget(Group.key, name);
		} else if (lastPath.equals(TABLES_TABLE_KEYS)) {
			if (tempPath.equals(TABLES_TABLE_KEYS_KEY)) {
				lastKey = new Key(attributes.getValue("Id"));
				lastTable.add(lastKey);
			}
		} else if (lastPath.equals(TABLES_TABLE_PRIMARYKEYS)) {
			if (tempPath.equals(TABLES_TABLE_PRIMARYKEYS_KEY)) {
				lastTable.setPrimaryKey(attributes.getValue("Ref"));
			}
		} else if (lastPath.equals(TABLES_TABLE_KEYS_KEY_COLUMNS)) {
			if (tempPath.equals(TABLES_TABLE_KEYS_KEY_COLUMNS_COLUMN)) {
				lastKey.addColumn(lastTable.getColumn(attributes
						.getValue("Ref")));
			}
		}
		lastPath = tempPath;
	}

	/** */
	/**
	 * 结束元素解析
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		// 管理是否结束schema的解析
		if (!doing)
			return;

		// System.out.println(("完成解析:" + name));
		if (Target.dbms.id.equals(name) || Target.tables.id.equals(name)
				|| Target.references.id.equals(name))
			doing = false;

		// 恢复路径状态
		lastPath = lastPath.substring(0, lastPath.lastIndexOf("/" + name));
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (lastObject != null && lastObject.ob != null) {
			Object dest = Group.reference.equals(lastObject.og) ? lastRef
					: Group.table.equals(lastObject.og) ? lastTable : Group.key
							.equals(lastObject.og) ? lastKey : Group.column
							.equals(lastObject.og) ? lastColumn : models;
			try {
				BeanUtils.setProperty(dest, lastObject.ob, new String(ch,
						start, length));
				lastObject = null;
			} catch (Exception e) {
				throw new SAXException(e);
			}
		}
	}

	/** */
	/**
	 * 取得解析成功的Table定义信息
	 * 
	 * @return
	 */
	public DatabaseModel getTables() {
		return models;
	}

	/** */
	/**
	 * 根据标识找符合标识的东西
	 * 
	 * @param group
	 * @param id
	 * @return
	 */
	private static Target findTarget(Group group, String id) {
		for (Target item : Target.class.getEnumConstants()) {
			if (group == item.og && item.id.equals(id))
				return item;
		}
		return null;
	}

	/** */
	/**
	 * 操作的组分类
	 * 
	 * @author wilesun
	 * @create 2007-11-3
	 */
	private enum Group {
		dbms, table, column, key, reference
	}

	/** */
	/**
	 * 解析操作码
	 * 
	 * @author wilesun
	 * @create 2007-11-3
	 */
	private enum Target {

		dbms("o:DBMS"),
		/** */
		/**
		 * dbms名称
		 */
		dbmsName("a:Name", "dbmsName", Group.dbms),
		/** */
		/**
		 * dbms编码
		 */
		dbmsCode("a:Code", "dbmsCode", Group.dbms),
		/** */
		/**
		 * 表集合
		 */
		tables("c:Tables"),
		/** */
		/**
		 * 表
		 */
		table("o:Table"),
		/** */
		/**
		 * 表名称
		 */
		tableName("a:Name", "name", Group.table),
		/** */
		/**
		 * 表编码
		 */
		tableCode("a:Code", "code", Group.table),
		/** 关系集合 */
		references("c:References"), reference("o:Reference"), refName("a:Name",
				"name", Group.reference), refCode("a:Code", "code",
				Group.reference), refObject1("c:Object1"), refObject2(
				"c:Object2"),
		/**
		 * 列集合
		 */
		columns("c:Columns"),
		/** */
		/**
		 * 列
		 */
		column("o:Column"), keys("c:Keys"), key("o:Key"), keyColumns(
				"c:Key.Columns"), primaryKey("c:PrimaryKey"),
		/** */
		/**
		 * 列名称
		 */
		columnName("a:Name", "name", Group.column),
		/** */
		/**
		 * 列编号
		 */
		columnCode("a:Code", "code", Group.column),
		/** */
		/**
		 * 列格式
		 */
		columnFormat("a:Format", "format", Group.column),
		/** */
		/**
		 * 列下拉
		 */
		columnList("a:ListOfValues", "list", Group.column),
		/** */
		/**
		 * 列类型
		 */
		columnDataType("a:DataType", "dataType", Group.column),
		/**
		 * 列备注
		 */
		columnComment("a:Comment", "comment", Group.column),
		/** */
		/**
		 * 列长度
		 */
		columnLength("a:Length", "length", Group.column), columnScale(
				"a:Precision", "scale", Group.column),
		/** */
		/**
		 * 列强制
		 */
		columnMandatory("a:Mandatory", "mandatory", Group.column), keyName(
				"a:Name", "name", Group.key), keyCode("a:Code", "code",
				Group.key), ;

		// 操作码标识
		private String id;

		private String ob;

		private Group og;

		/** */
		/**
		 * 只根据标识,无组和操作构造
		 * 
		 * @param id
		 */
		Target(String id) {
			this.id = id;
		}

		/** */
		/**
		 * 根据标识和操作做内部构造
		 * 
		 * @param id
		 */
		Target(String id, String ob, Group og) {
			this.id = id;
			this.ob = ob;
			this.og = og;
		}
	}

	public DatabaseModel getModels() {
		return models;
	}
}
