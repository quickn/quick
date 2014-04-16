package cn.quickj.extui.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonGenerationException;

import cn.quickj.AbstractApplication;
import cn.quickj.Setting;
import cn.quickj.action.Action;
import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.model.User;
import cn.quickj.security.service.UserService;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.Inject;

public class ExtBaseAction extends Action {

	@Inject
	private UserService userService;
	protected String sort;// 排列的字段
	protected String dir;// 排列类型asc,desc
	protected String _dc;
	protected String node;
	protected boolean success;
	protected int start;// 需要查询的数量
	protected int limit;// 当前页码

	/**
	 * 重载getAttribute，改用容器session
	 */
	@Override
	public Object getAttribute(String key) {
		if (Setting.cacheClass != null
				&& Setting.cacheClass.indexOf("JuaeCache") != -1) {// 淘宝缓存时，切换成容器session
			HttpSession session = getRequest().getSession();
			return session.getAttribute(key);
		} else
			return super.getAttribute(key);
	}

	/**
	 * 重载setAttribute，改用容器session
	 */
	@Override
	public void setAttribute(String key, Object value) {
		if (Setting.cacheClass != null
				&& Setting.cacheClass.indexOf("JuaeCache") != -1) {
			HttpSession session = getRequest().getSession();
			session.setAttribute(key, value);
		} else
			super.setAttribute(key, value);
	}

	public String toJson(boolean success, String msg, Object data) {
		this.success = success;
		this.message = msg;
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("success", success);
		if (getMessage() != null)
			result.put("msg", getMessage());
		else
			result.put("msg", getErrorMsg());
		if (data != null)
			result.put("data", data);
		try {
			return AbstractApplication.jsonObjectMapper
					.writeValueAsString(result);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{success:false,msg:'数据转换错误!'}";
	}

	public String toTreeJson(Object data) {
		try {
			return AbstractApplication.jsonObjectMapper
					.writeValueAsString(data);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getUserName() {
		SessionUser user = getAdminUser();
		if (user == null) {
			return "no User";
		} else {
			return user.getName();
		}
	}

	public SessionUser getAdminUser() {
		SessionUser user = (SessionUser) getAttribute("USER_IN_SESSION_KEY");
		return user;
	}

	public User getUserInfo() {
		SessionUser user = getAdminUser();
		return userService.getUser(user.getId());
	}

	public String toJson(Object data) {
		return toJson(true, "成功", data);
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean getSuccess() {
		return success;
	}

	public Date getDate() {
		return new Date();
	}

	public String getDir() {
		return dir;
	}

	public String getSort() {
		return sort;
	}

	public String get_dc() {
		return _dc;
	}

	public String getNode() {
		return node;
	}
}
