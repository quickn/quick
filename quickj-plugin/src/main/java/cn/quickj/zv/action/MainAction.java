package cn.quickj.zv.action;

import java.util.Collection;

import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.action.UserAction;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;

import com.google.inject.Inject;

public class MainAction extends ZvUIActionSupport {
	@Inject
	private MenuService menuService;
	private Collection<SimpleMenu> menus;
	private Collection<SimpleMenu> submenus;
	private SimpleMenu curMenu;

	private void loadRootMenu() {
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		if (user == null) {
			menus = menuService.getRootMenus();
			submenus = menuService.getSubMenus(menus.iterator().next().getId());
		} else {
			menus = menuService.getRootMenus(user.getId());
			submenus = menuService.getSubMenus(menus.iterator().next().getId());
		}
		System.out.println(submenus.size());
		// if (menus != null && menus.size() > 0)
		// curMenu = menus.iterator().next();
	}

	public void index() {
		loadRootMenu();
		render("main.html");
	}

	public void head() {
		loadRootMenu();
		render("menu/list.html");
	}

	public void click(String menuId) {
		submenus = menuService.getSubMenus(Integer.parseInt(menuId));
		render("submenu.html");
	}

	public Collection<SimpleMenu> getMenus() {
		return menus;
	}

	public SimpleMenu getCurMenu() {
		return curMenu;
	}

	public Collection<SimpleMenu> getSubmenus() {
		return submenus;
	}

	public void setSubmenus(Collection<SimpleMenu> submenus) {
		this.submenus = submenus;
	}
}
