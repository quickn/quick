package cn.quickj.security;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import cn.quickj.plugin.AbstractPlugin;
import cn.quickj.security.model.Group;
import cn.quickj.security.model.OperateAuditLog;
import cn.quickj.security.model.Resource;
import cn.quickj.security.model.Role;
import cn.quickj.security.model.User;

public class SecurityPlugin  extends AbstractPlugin {
	/* (non-Javadoc)
	 * @see cn.quickj.security.Plugin#getName()
	 */
	public String getName(){
		return "基于用户，部门，角色的权限控制插件";
	}
	
	
	/* (non-Javadoc)
	 * @see cn.quickj.security.Plugin#getModels()
	 */
	public ArrayList<Class<?>>getModels(){
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		result.add(Group.class);
		result.add(User.class);
		result.add(Resource.class);
		result.add(Role.class);
		result.add(OperateAuditLog.class);
		return result;
	}
	/* (non-Javadoc)
	 * @see cn.quickj.security.Plugin#getId()
	 */
	public String getId(){
		return "rbac";
	}
	/* (non-Javadoc)
	 * @see cn.quickj.security.Plugin#getRootPackage()
	 */
	public String getRootPackage(){
		return "cn.quickj.security";
	}
	public void init(Configuration c) {
	}
	public Map<String, Class<?>> depend() {
		return null;
	}
}
