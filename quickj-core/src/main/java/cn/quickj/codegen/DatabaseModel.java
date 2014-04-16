package cn.quickj.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

import cn.quickj.Setting;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DatabaseModel {
	private String rootPackage;
	private String outputFolder;
	private String tblPrefix;
	private LinkedHashMap<String, Table> tables = new LinkedHashMap<String, Table>();
	private LinkedHashMap<String, Reference> references = new LinkedHashMap<String, Reference>();
	private String modelPath;

	// TODO 使用XML文件存储需要过滤的表名和表名--》类名的对应关系。
	public void add(Table table) {
		tables.put(table.getId(), table);
	}

	public void addRef(Reference ref) {
		references.put(ref.getId(), ref);
	}

	public Table findTableByColumnId(String colId) {
		for (Table table : tables.values()) {
			if (table.getColumn(colId) != null)
				return table;
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Table table : tables.values()) {
			sb.append(table.toString());
		}
		for (Reference ref : references.values()) {
			sb.append(ref.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public Table getTable(String id) {
		return tables.get(id);
	}

	public void generateForm(String folder) {
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new File("etc"));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setOutputEncoding(Setting.DEFAULT_CHARSET);
			// TODO 如何做国际化。
			cfg.setDefaultEncoding("utf-8");
			cfg.setNumberFormat("#");

			cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
			Template template = cfg.getTemplate("form.ftl");
			for (Table table : tables.values()) {
				FileWriter writer = new FileWriter(new File(folder + table.getClassName() + ".html"));
				template.process(table, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void generate(String templateFile, String subPackage, String suffixName, String extName) {
		generate(templateFile, subPackage, suffixName, extName, true);
	}

	public void generate(String templateFile, String subPackage, String suffixName, String extName, boolean capitalFileName) {
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new File("etc/codegen"));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setOutputEncoding(Setting.DEFAULT_CHARSET);
			cfg.setDefaultEncoding("utf-8");
			cfg.setNumberFormat("#");
			cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));

			Template template = cfg.getTemplate(templateFile);
			for (Table table : tables.values()) {
				table.setPackageName(rootPackage);
				table.setPrefix(tblPrefix);
			}
			for (Table table : tables.values()) {
				String folder = outputFolder + (table.getPackageName() + subPackage).replace(".", "/") + "/";
				modelPath = folder;
				new File(folder).mkdirs();
				File file = new File(folder + (capitalFileName ? table.getClassName() : table.getJavaName()) + suffixName
						+ extName);
				if (!file.exists()) {
					System.out.println("正在生成：" + table.getName() + "  class:" + table.getJavaName());
					FileWriter writer = new FileWriter(file);
					template.process(table, writer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	public void config(String rootPackage, String outputFolder, String tblPrefix) {
		this.rootPackage = rootPackage;
		this.outputFolder = outputFolder;
		this.tblPrefix = tblPrefix;
	}

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

}
