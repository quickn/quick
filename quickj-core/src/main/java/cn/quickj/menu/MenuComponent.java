package cn.quickj.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.Element;

public class MenuComponent {
	String name;
	String title;
	HashSet<String> roles;
	String url;
	boolean selected;
	String tips;
	Map<String,MenuComponent>submenus;
	public MenuComponent(Element e) {
		name = e.attributeValue("name");
		title = e.attributeValue("title");
		String [] roles =e.attributeValue("roles").split(",");
		this.roles = new HashSet<String>();
		for (String role : roles) {
			this.roles.add(role);
		}
		url = e.attributeValue("url");
		selected = "true".equalsIgnoreCase(e.attributeValue("selected"));
		tips = e.attributeValue("tips");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public HashSet<String> getRoles() {
		return roles;
	}
	public void setRoles(HashSet<String> roles) {
		this.roles = roles;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	public void addItem(MenuComponent mc) {
		if(submenus==null)
			submenus = new LinkedHashMap<String, MenuComponent>();
		submenus.put(mc.name, mc);
	}	
	@Override
	public String toString() {
		return "title:"+title+" name:"+name+" selected:"+selected+" tips:"+tips+" url:"+url;
	}
	public Map<String, MenuComponent> getSubMenu() {
		return submenus;
	}
	public Collection<MenuComponent> getSubMenus(PermissionsAdapter permission,String []roles){
		Collection<MenuComponent> userSubMenus;
		userSubMenus = new ArrayList<MenuComponent>();
		for (MenuComponent menu : submenus.values()){
			if(permission.isAllowed(menu, roles))
				userSubMenus.add(menu);
		}
		return userSubMenus;
	}
}
