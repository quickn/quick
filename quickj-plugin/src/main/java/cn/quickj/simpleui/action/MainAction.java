package cn.quickj.simpleui.action;

import java.util.Collection;

import cn.quickj.action.Action;
import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.action.UserAction;
import cn.quickj.security.model.User;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;

import com.google.inject.Inject;

public class MainAction extends Action {
	@Inject
	private MenuService menuService;
	private Collection<SimpleMenu> menus;
	private SimpleMenu curMenu;

	public void head() {
		render("head.html");
	}

	public void menu() {
		loadMenu();
		render("menu.html");
	}

	/**
	 * SimpleUI只用到一层菜单，所以只需load root menu即可。
	 */
	private void loadMenu() {
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		if (user == null)
			menus = menuService.getRootMenus();
		else
			menus = menuService.getRootMenus(user.getId());
		if (menus != null && menus.size() > 0)
			curMenu = menus.iterator().next();
	}

	public void index() {
		loadMenu();
		render("main.html");
	}

	public Collection<SimpleMenu> getMenus() {
		return menus;
	}

	public SimpleMenu getCurMenu() {
		return curMenu;
	}
}
