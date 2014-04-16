

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import cn.quickj.extui.action.ExtBaseAction;
import cn.quickj.extui.action.bean.SessionUser;
import cn.quickj.security.action.UserAction;
import cn.quickj.simpleui.model.SimpleMenu;
import cn.quickj.simpleui.service.MenuService;

import com.google.inject.Inject;

/**
 * 封装在extjs框架中，其他插件的json数据提供方法。
 * 一般是调用相关的插件service，然后返回json数据。
 * @author lbj
 *
 */
public class UIAction extends ExtBaseAction {
	@Inject
	private MenuService menuService;
	private Collection<SimpleMenu>rootMenus;
	public void menus(){
		Collection<SimpleMenu> menus;
		SessionUser user = (SessionUser) getAttribute(UserAction.USER_IN_SESSION_KEY);
		if (user == null)
			menus = menuService.getMenus();
		else
			menus = menuService.getMenus(user.getId());
		HashMap<Integer,SimpleMenu> allmenus = new HashMap<Integer, SimpleMenu>();
		rootMenus = new ArrayList<SimpleMenu>();
		for (SimpleMenu menu : menus) {
			allmenus.put(menu.getId(), menu);
		}
		for (SimpleMenu menu : menus) {
			if(menu.getParent()!=null){
				SimpleMenu parentMenu = allmenus.get(menu.getParent().getId());
				if(parentMenu!=null){
					if(parentMenu.getChildren()==null)
						parentMenu.setChildren(new ArrayList<SimpleMenu>());
					parentMenu.getChildren().add(menu);
				}
			}else{
				rootMenus.add(menu);
			}
		}
		render("menus.html");
	}
	public Collection<SimpleMenu> getRootMenus() {
		return rootMenus;
	}
}
