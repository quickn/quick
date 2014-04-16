package cn.quickj.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import cn.quickj.Setting;
import cn.quickj.guice.SessionScoped;

@SessionScoped
public class MenuConfig {
	static Map<String,MenuComponent> menus;
	public Collection<MenuComponent> getMenus(PermissionsAdapter permission,String [] roles){
		Collection<MenuComponent> userMenus;
		userMenus = new ArrayList<MenuComponent>();
		for (MenuComponent menu : menus.values()) {
			if(permission.isAllowed(menu, roles))
				userMenus.add(menu);
		}
		return userMenus;
	}
	public MenuComponent selectMenu(String name){
		for(MenuComponent menu :menus.values())
			menu.selected = false;
		MenuComponent menu = menus.get(name);
		if(menu!=null)
			menu.selected = true;
		return menu;
	}
	public MenuComponent selectMenu(String name,String subname){
		selectMenu(name);
		MenuComponent menu = menus.get(name);
		if(menu!=null){
			Map<String, MenuComponent> submenus = menu.getSubMenu();
			for (MenuComponent submenu : submenus.values()) {
				submenu.selected = false;
			}
			menu = submenus.get(subname);
			if(menu!=null){
				menu.selected = true;
				return menu;
			}
		}
		return null;		
	}
	/**
	 * 取激活的菜单。
	 * @return
	 */
	public MenuComponent getSelectedMenu(){
		for (MenuComponent menu : menus.values()) {
			if(menu.isSelected()){
				return menu;
			}
		}
		Iterator<MenuComponent> iter = menus.values().iterator();
		if(iter.hasNext()){
			MenuComponent menu = iter.next();
			menu.selected = true;
			return menu;
		}
		return null;
	}
	public MenuConfig() {
		this(Setting.webRoot + "WEB-INF/menu.xml");
	}

	@SuppressWarnings("unchecked")
	public MenuConfig(String configFile) {
		Node root = loadXmlConfig(configFile);
		MenuConfig.menus = new LinkedHashMap<String,MenuComponent>();
		List<Element> menus = (List<Element>) root.selectNodes("/menus/*");
		for (Element menu : menus) {
			MenuComponent m = new MenuComponent(menu);
			List<Element> items = menu.elements("item");
			for (Element item : items) {
				MenuComponent i = new MenuComponent(item);
				m.addItem(i);
			}
			MenuConfig.menus.put(m.name,m);
		}

	}

	public static void main(String[] argc) {
		MenuConfig mc = new MenuConfig(
				"D:/eclipse/workspace/quickj/web/WEB-INF/menu.xml");
		System.out.println(mc.toString());
		Collection<MenuComponent> mymenus = mc.getMenus(new RolesPermissionAdapter(),new String[]{ "admin"});
		System.out.println("**************************");
		for (MenuComponent menuComponent : mymenus) {
			System.out.println(menuComponent.toString());
		}
	}

	/**
	 * 导入指定XML文件。
	 * 
	 * @param configFile
	 * @return
	 */
	private Node loadXmlConfig(String configFile) {
		SAXReader reader = new SAXReader();
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(configFile));
			Document d = reader.read(fis);
			return d.getRootElement();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (MenuComponent menu : MenuConfig.menus.values()) {
			buffer.append("\n" + menu.toString());
			if (menu.getSubMenu() != null) {
				Map<String, MenuComponent> submenus = menu.getSubMenu();
				for (MenuComponent submenu : submenus.values()) {
					buffer.append("\n    " + submenu.toString());
				}
			}
		}
		return buffer.toString();
	}
}
