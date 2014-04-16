package cn.quickj.dict.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

/**
 * 字典一般按照下列方式分类 <br />
 * 1.parentId=0:对于那些系统级的字典归到这一类。
 * 2.parentId!=0:如果某些分类属于系统级分类的子字典，则parentId=前一级的Id, 此种分类方法可以在下拉选择时进行多级联动。
 * 
 * 
 * @author Administrator
 * 
 */
@Entity
@Table(name = "SYS_DICTIONARY")
public class Dictionary implements Serializable {

	private static final long serialVersionUID = 8778712546995362044L;
	private Integer id;
	private Dictionary parent;
	private String name;
	private String value;
	private DictType dictType = new DictType();
	private Integer sort;
	private Integer status;

	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "sys_dictionary"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")
	@Column(name = "dict_id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "value", nullable = false, length = 32)
	@Index(name = "dict_search_idx", columnNames = { "id", "value" })
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDictType(DictType dictType) {
		this.dictType = dictType;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "dict_type_id", unique = false, nullable = true)
	public DictType getDictType() {
		return dictType;
	}

	@Column(name = "sort")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * 获取父菜单
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "PARENT_ID")
	public Dictionary getParent() {
		return parent;
	}

	public void setParent(Dictionary parent) {
		this.parent = parent;
	}

	@Column(name="status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
