package cn.quickj.dict.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name= "SYS_DICT_TYPE")
public class DictType implements Serializable{

	private static final long serialVersionUID = 4339832829632515792L;
	private Integer id;
	private String name;
	private String description;
	private Set<Dictionary> dictionaries;
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "sys_dict_type"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")	
	@Column(name = "dt_id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="name", length=32,nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="description", length=128,nullable=true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setDictionaries(Set<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	
	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "dictType")
	@OrderBy(value="sort")
	public Set<Dictionary> getDictionaries() {
		return dictionaries;
	}
}
