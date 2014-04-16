package cn.quickj.menu;

public class RolesPermissionAdapter implements PermissionsAdapter {

	public boolean isAllowed(MenuComponent menu,String [] roles) {
		for (String role : roles) {
			if(menu.getRoles().contains(role))
				return true;
		}
		return false;
	}

}
