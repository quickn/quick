package cn.quickj.simpleui.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.action.UserAction;
import cn.quickj.simpleui.model.ClickPath;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;
import cn.quickj.utils.QuickUtils;

import com.google.inject.Inject;

public class MenuAction extends SimpleUIActionSupport {
	public static final String CLICK_PATH = "QUICKJ_CLICK_PATH";
	@Inject
	MenuService menuService;
	Collection<SimpleMenu> menus;
	@Inject
	SimpleMenu menu;
	boolean resource;
	String ids;

	public void create() {
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/ajax_form.html");
	}

	public void search() {
		if (menu.getTitle() != null && menu.getTitle().equals("") == false) {
			menus = menuService.search(menu,getPaginate());
		} else {
			menus = menuService.getTreeMenus(getPaginate());
		}
		render("menu/list.html");
	}

	public void edit(String id) {
		int menuId = Integer.parseInt(id);
		menus = menuService.getTreeMenus(getPaginate());
		menu = menuService.getMenu(menuId);
		if (menu == null) {
			setErrorMsg("指定的菜单不存在，或许已经被其他用户删除!");
		}
		render("menu/ajax_form.html");
	}

	public void save() {
		if (menu.getId() == null) {
			setMessage("菜单创建成功!");
		} else
			setMessage("菜单保存成功!");
		menuService.save(menu, resource);
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/list.html");
	}

	public void index() {
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/list.html");
	}

	public void list() {
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/list.html");
	}

	/**
	 * ajax提交(beta v1.0)
	 */
	public void ajax() {
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/ajax_list.html");
	}

	public void delete(String id) {
		int menuId = Integer.parseInt(id);
		menuService.delete(menuId);
		setMessage("删除成功!");
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/list.html");
	}

	public void delete() {
		int[] menuIds = QuickUtils.splitIdsToIntArray(ids);
		if (menuIds != null) {
			menuService.delete(menuIds);
			setMessage("删除成功!");
		}
		menus = menuService.getTreeMenus(getPaginate());
		render("menu/ajax_list.html");
	}

	/**
	 * 用户点击了菜单
	 * 
	 * @param id
	 */
	public void click(String id) {
		int menuId = Integer.parseInt(id);
		SimpleMenu menu = menuService.getMenu(menuId);
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		// 如果是根菜单，并且没有URL，则渲染子菜单。
		if (menu.getLevel() == 1
				&& (menu.getUrl() == null || menu.getUrl().length() == 0)) {
			menus = menuService.getSubMenus(menuId, user.getId());
			render("submenus.html");
		} else {
			// 如果是最后的菜单，则直接重定向到菜单指定的URL。
			SimpleMenu parentMenu = menuService.getMenu(menu.getParent().getId());
			List<ClickPath> clickPaths = new ArrayList<ClickPath>();
			if (parentMenu != null) {
				ClickPath cp = new ClickPath(parentMenu.getTitle(),
						"menu/click/" + parentMenu.getId());
				clickPaths.add(cp);
			}
			ClickPath cp = new ClickPath(menu.getTitle(), menu.getUrl());
			clickPaths.add(cp);
			redirect(menu.getUrl());
		}
	}

	public Collection<SimpleMenu> getMenus() {
		return menus;
	}

	public SimpleMenu getMenu() {
		return menu;
	}

	public boolean isResource() {
		return resource;
	}

	public void setResource(boolean resource) {
		this.resource = resource;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
