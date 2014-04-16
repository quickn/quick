package cn.quickj.imexport.model;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import cn.quickj.security.model.User;

@Entity
@Table(name = "EXPORT_SCHEMA")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExportSchema implements Serializable {
	private static final long serialVersionUID = 565845668415631634L;
	private Integer id;
	private User user = new User();
	private String name;
	private String exportId;
	private String defColumns;
	private Integer isShare;
	
	@Id
	@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.TableGenerator", parameters = {
			@Parameter(name = "optimizer", value = "pooled"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "segment_value", value = "users"),
			@Parameter(name = "increment_size", value = "1") })
	@GeneratedValue(generator = "idGenerator")	
	@Column(name = "EXPORT_ID", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "USER_ID")
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	@Column(name = "EXPORTID")
	public String getExportId() {
		return exportId;
	}
	public void setExportId(String exportId) {
		this.exportId = exportId;
	}
	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "DEFCOLUMNS")
	public String getDefColumns() {
		return defColumns;
	}
	public void setDefColumns(String defColumns) {
		this.defColumns = defColumns;
	}
	@Column(name = "IS_SHARE")
	public Integer getIsShare() {
		return isShare;
	}
	public void setIsShare(Integer isShare) {
		this.isShare = isShare;
	}
}
