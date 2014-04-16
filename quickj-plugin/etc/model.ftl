package ${packageName};
// Generated from PowerDesigner file ,Written by libaijun.

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

/**
* ${name} 
*/

@Entity
@Table(name="${code?upper_case}")
public class ${className} implements Serializable {
	private static final long serialVersionUID = ${serialVersionUID}L;

	<#list columns?values as column>
	/**
	*${column.name}
	*/
	private ${column.javaType} ${column.javaName};
	</#list>
	<#list oneToMany as oneToMany>
	/**
	* 关联的${oneToMany.child.name}集合
	*/
	private Set<${oneToMany.child.className}> ${oneToMany.child.javaName}s;
	</#list>
		
	public ${className}(){
	}
	<#list columns?values as column>
	/**
	* 获取${column.name}
	*/
	<#if column.primary==true>
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	</#if>
	<#if column.reference??>
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "${column.code?upper_case}")
	<#else>
	<#if column.temporal??>@Temporal(TemporalType.${column.temporal})</#if>
	@Column(name="${column.code?upper_case }"<#if column.mandatory>,nullable=false</#if><#if column.length??>,length=${column.length}</#if><#if column.precision??>,precision=${column.precision}</#if><#if column.scale??>,scale=${column.scale}</#if>)
	</#if>
	public ${column.javaType} ${column.getterName}(){
		return ${column.javaName};
	}
	/**
	* 设置${column.name}
	*/	
	public void ${column.setterName}(${column.javaType} ${column.javaName}){
		this.${column.javaName} = ${column.javaName};
	} 
	</#list>
	
	<#list oneToMany as oneToMany>
	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, targetEntity = ${oneToMany.child.className}.class, mappedBy = "${oneToMany.parent.javaName}")
	public Set<${oneToMany.child.className}> get${oneToMany.child.className}s(){
		return this.${oneToMany.child.javaName}s;
	}
	public void set${oneToMany.child.className}s(Set<${oneToMany.child.className}> ${oneToMany.child.javaName}s){
		this.${oneToMany.child.javaName}s = ${oneToMany.child.javaName}s;
	}
	</#list>
}