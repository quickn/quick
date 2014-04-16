package cn.quickj.simpleui.model;
// Generated from PowerDesigner file ,Written by lbj.

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import cn.quickj.security.model.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
* 菜单配置表 
*/

@Entity
@Table(name="SIMPLE_MENUS")
public class SimpleMenu implements Serializable {
	private static final long serialVersionUID = 10210367736L;

	/**
	*ID
	*/
	private Integer id;
	/**
	*HTML代码
	*/
	String html;
	/**
	 * 菜单的级别，level = 1表示根菜单，level=2:表示二级菜单，依次类推。
	 */
	Integer level = 1;
	/**
	*父菜单
	*/
	private SimpleMenu parent;
	/**
	 * 菜单的排序位置。
	 */
	Integer position =0;
	/**
	*标题
	*/
	private String title;
	/**
	*URL
	*/
	private String url;
	/**
	 * 图标样式
	 */
	private String iconCls;
	/**
	 * 菜单关联的资源，当创建菜单的时候可以同时创建资源。
	 */
	Resource resource;
	private List<SimpleMenu> children = new ArrayList<SimpleMenu>();	
	public SimpleMenu(){
	}
	/**
	* 获取ID
	*/

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "SIMPLE_MENUS"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")	
	@Column(name = "menu_id", unique = true, nullable = false)
	public Integer getId(){
		return id;
	}
	/**
	* 设置ID
	*/	
	public void setId(Integer id){
		this.id = id;
	} 
	/**
	* 获取HTML代码
	*/
	
	@Column(name="HTML",nullable=true,length=512)
	public String getHtml(){
		return html;
	}
	/**
	* 设置HTML代码
	*/	
	public void setHtml(String html){
		this.html = html;
	} 
	@Column(name="M_LEVEL",nullable=true,length=10)
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	@Column(name="POSITION",nullable=true,length=10)
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	/**
	* 获取父菜单
	*/
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "PARENT_ID")
	public SimpleMenu getParent() {
		return parent;
	}
	/**
	* 设置父菜单
	*/	
	public void setParent(SimpleMenu parent) {
		this.parent = parent;
	}

	/**
	* 获取标题
	*/
	
	@Column(name="TITLE",nullable=false,length=64)
	public String getTitle(){
		return title;
	}
	/**
	* 设置标题
	*/	
	public void setTitle(String title){
		this.title = title;
	} 
	/**
	* 获取URL
	*/
	
	@Column(name="URL",length=512)
	public String getUrl(){
		return url;
	}
	/**
	* 设置URL
	*/	
	public void setUrl(String url){
		this.url = url;
	} 
	/**
	* 获取图标样式
	*/
	
	@Column(name="ICON_CLS",length=32)
	public String getIconCls() {
		return iconCls;
	}
	/**
	* 设置图标样式
	*/	
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	/**
	* 获取关联的资源id
	*/
	
	@OneToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL,optional=true)
	@JoinColumn(name="resource_id")
	public Resource getResource() {
		return resource;
	}
	/**
	* 设置关联的资源id
	*/	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	@JsonIgnore
	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = SimpleMenu.class, mappedBy = "parent")
	@OrderBy("position")
	public List<SimpleMenu> getChildren() {
		return children;
	}
	public void setChildren(List<SimpleMenu> children) {
		this.children = children;
	}
	
}