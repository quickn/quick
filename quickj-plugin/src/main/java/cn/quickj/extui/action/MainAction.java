package cn.quickj.extui.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.model.User;
import cn.quickj.security.service.UserService;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;
import cn.quickj.utils.Constant;

import com.google.inject.Inject;

/**
 * 封装在extjs框架中，其他插件的json数据提供方法。 一般是调用相关的插件service，然后返回json数据。
 * 
 * @author lbj
 * 
 */
public class MainAction extends ExtBaseAction {
	@Inject
	private MenuService menuService;
	@Inject
	private UserService userService;
	@Inject
	private User user;
	private Collection<SimpleMenu> rootMenus;
	private String code;
	private String username;
	private String password;
	private Integer pwdUselessNum;// 密码失效天数
	private Integer nodeId;// 菜单ID，可默认展开菜单内容

	public void logout() {
		setAttribute("USER_IN_SESSION_KEY", null);
		getRequest().getSession().invalidate();
		render("index.html");
	}

	public void index() {
		render("main.html");
	}

	public String checkVerifyCode() {
		String check = "";
		String realCode = (String) getAttribute("verifyCode");
		if (code.equals(realCode)) {
			check = "success";
		} else {
			check = "fail";
		}
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("check", check);
		return toJson(data);
	}

	public String login() {
		user = userService.login(username, password);
		HashMap<String, Object> data = new HashMap<String, Object>();
		if (user != null) {
			if (!"admin".equals(username) && user.getDeadLogin() != null
					&& user.getDeadLogin().getTime() < getDate().getTime()) {
				data.put("targetUrl", "");
				data.put("msgs", "密码已失效");
			} else {
				if (!"admin".equals(username) && pwdUselessNum != null
						&& user.getDeadLogin() == null)
					user.setDeadLogin(getDeadLoginDate(pwdUselessNum));
				// 修改登入时间
				user.setLastLogin(getDate());
				userService.update(user);
				setAttribute("USER_IN_SESSION_KEY",
						userService.getSessionUser(user));
				data.put("msgs", "true");
				data.put("targetUrl", "extui/main/index");
				String host = getRequest().getServerName();
				Cookie cookie = new Cookie("MORE_FUNC", "MORE_FUNC"); // 保存用户名到Cookie
				cookie.setPath("/");
				cookie.setDomain(host);
				cookie.setMaxAge(60 * 60 * 24);
				getResponse().addCookie(cookie);
			}
		} else {
			data.put("targetUrl", "");
			data.put("msgs", "密码错误");
		}
		return toJson(data);
	}

	private Date getDeadLoginDate(Integer pwdUselessNum) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate());
		cal.add(Calendar.DAY_OF_YEAR, pwdUselessNum);
		return cal.getTime();
	}

	public void menus() {
		Collection<SimpleMenu> menus;
		SessionUser user = (SessionUser) getAttribute("USER_IN_SESSION_KEY");
		if (user == null || user.getUserName().equals(Constant.THEINIT_NAME))
			menus = menuService.getMenus();
		else
			menus = menuService.getMenus(user.getId());
		rootMenus = new ArrayList<SimpleMenu>();
		Map<Integer, SimpleMenu> map = new HashMap<Integer, SimpleMenu>();
		for (SimpleMenu menu : menus) {
			if (menu.getParent() != null) {
				map.put(menu.getId(), menu.getParent());
			}
		}
		for (SimpleMenu menu : menus) {
			menu.getChildren().clear();
		}
		for (SimpleMenu menu : menus) {
			if (menu.getParent() != null) {
				map.get(menu.getId()).getChildren().add(menu);
			} else {
				rootMenus.add(menu);
			}
		}
		render("menus.html");
	}

	public Collection<SimpleMenu> getRootMenus() {
		return rootMenus;
	}

	public Integer getNodeId() {
		return nodeId;
	}
}
