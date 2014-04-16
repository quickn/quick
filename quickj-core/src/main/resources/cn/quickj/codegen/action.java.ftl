package ${package+".action"}
// Generated ${date} by Hibernate Tools ${version}

<#assign classbody>
<#assign declarationName = pojo.importType(pojo.getDeclarationName())>
<#assign serviceName = pojo.shortName.toLowerCase()+"Service">
<#assign entityList = pojo.shortName.toLowerCase() + "s">
<#assign entityvar = pojo.shortName.toLowerCase()>
<#assign entityClass = pojo.importType(pojo.qualifiedDeclarationName)>

/**
 * ${entityClass}的Action类，包括了常用CRUD操作.
 * @see ${pojo.getQualifiedDeclarationName()}
 * @author Hibernate Tools(quickj generator v1.0)
 */

public class ${entityClass}Action extends ${pojo.importType("cn.quickj.simpleui.action.SimpleUIActionSupport")}{
	@${pojo.importType("com.google.inject.Inject")}
	private ${declarationName} ${entityvar};
	@${pojo.importType("com.google.inject.Inject")}
	${pojo.importType(package+".service."+entityClass+"Service")} ${serviceName};
	private ${pojo.importType("java.utils.Collection<"+entityClass+">")} ${entityList};
	private String checkedIds;
	/**
	 * 列出Company列表。
	 */	
	public void index(){
		${entityList} = ${serviceName}.findAll${entityClass}(1000);
		render("${entityvar}/list.html");
	}
	/**
	 * 根据${entityClass} Example查找${entityvar}.
	 */	
	public void list(){
		${entityList} = ${serviceName}.find${entityClass}ByExample(${entityvar});
		render("${entityvar}/list.html");
	}
	/**
	 * 新建一个${entityvar}
	 */	
	public void create(){
		render("${entityvar}/edit.html");
	}
	/**
	 * 准备编辑一个${entityvar}。
	 * @param id
	 */	 
	public void edit(String id){
		${entityvar} = ${serviceName}.get${entityClass}(id);
		render("${entityvar}/edit.html");
	}	
	/**
	 * 新增或者修改后保存。
	 */	 
	public void save(){
		if(${entityvar}.getId()==null){
			${serviceName}.save(${entityvar});
			setMessage("新增成功!");
		}else{
			${serviceName}.save(${entityvar});
			setMessage("保存成功!");
		}
		render("${entityvar}/list.html");
	}
	
	/**
	 * 删除一个
	 * @param id
	 */
	public void delete(String id){
		${serviceName}.delete${entityClass}(id);
	}
	/**
	 * 删除所有选中的变量
	 */
	public void delete(){
		${serviceName}.delete${entityList}(checkedIds);
	}
	
	public Collection<${entityClass}> get${entityClass}s() {
		return ${entityList}
	}
	public ${entityClass} get${entityClass}() {
		return ${entityvar}
	}
	public void setCheckedIds(String checkedIds) {
		this.checkedIds = checkedIds;
	}
}
</#assign>
 
${pojo.generateImports()}
${classbody}
