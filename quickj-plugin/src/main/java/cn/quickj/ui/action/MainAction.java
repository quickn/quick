package cn.quickj.ui.action;

import java.util.Collection;

import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.action.UserAction;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;

import com.google.inject.Inject;

public class MainAction extends UIActionSupport {
	@Inject
	private MenuService menuService;
	private Collection<SimpleMenu> menus;
	private SimpleMenu curMenu;

	private void loadRootMenu() {
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		if (user == null)
			menus = menuService.getRootMenus();
		else
			menus = menuService.getRootMenus(user.getId());
		if (menus != null && menus.size() > 0)
			curMenu = menus.iterator().next();
	}

	public void index() {
		loadRootMenu();
		render("main.html");
	}

	public void head() {
		loadRootMenu();
		render("menu/list.html");
	}

	/*
	 * 点击根菜单，返回子菜单
	 */
	public void click(String menuId) {
		//curMenu = menuService.getMenu(Integer.parseInt(menuId));
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		if (user == null)
			menus = menuService.getSubMenus(Integer.parseInt(menuId));
		else
		    menus = menuService.getSubMenus(Integer.parseInt(menuId),user.getId());
		render("submenu.html");
	}

	public Collection<SimpleMenu> getMenus() {
		return menus;
	}

	public SimpleMenu getCurMenu() {
		return curMenu;
	}
}
