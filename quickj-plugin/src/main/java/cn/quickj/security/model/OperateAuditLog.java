package cn.quickj.security.model;
// Generated from PowerDesigner file ,Written by lbj.

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

/**
* 系统操作审计日志 
*/

@Entity
@Table(name="OPERATE_AUDIT_LOG")
public class OperateAuditLog implements Serializable {
	private static final long serialVersionUID = 13837056995L;

	/**
	*ID
	*/
	private Integer id;
	/**
	*用户id
	*/
	private Integer userId;
	/**
	*用户名
	*/
	private String username;
	/**
	*操作时间
	*/
	private Date operateTime;
	/**
	*操作说明
	*/
	private String remark;
		
	public OperateAuditLog(){
	}
	/**
	* 获取ID
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	@Column(name="LOG_ID",nullable=false)
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
	* 获取用户id
	*/
	
	@Column(name="USER_ID")
	public Integer getUserId(){
		return userId;
	}
	/**
	* 设置用户id
	*/	
	public void setUserId(Integer userId){
		this.userId = userId;
	} 
	/**
	* 获取用户名
	*/
	
	@Column(name="USER_NAME",length=32)
	public String getUsername(){
		return username;
	}
	/**
	* 设置用户名
	*/	
	public void setUsername(String username){
		this.username = username;
	} 
	/**
	* 获取操作时间
	*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="OPERATE_TIME")
	public Date getOperateTime(){
		return operateTime;
	}
	/**
	* 设置操作时间
	*/	
	public void setOperateTime(Date operateTime){
		this.operateTime = operateTime;
	} 
	/**
	* 获取操作说明
	*/
	
	@Column(name="REMARK")
	public String getRemark(){
		return remark;
	}
	/**
	* 设置操作说明
	*/	
	public void setRemark(String remark){
		this.remark = remark;
	} 
	
}