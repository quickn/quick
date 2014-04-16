package cn.quickj.plugin;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import cn.quickj.utils.QuickUtils;

public abstract class AbstractPlugin implements Plugin {
	private Pattern pattern;

	public boolean uriMatch(String uri) {
		if (pattern == null)
			pattern = Pattern.compile("/" + getId() + "/[\\S]*|/" + getId());
		return pattern.matcher(uri).matches();
	}

	public ArrayList<Class<?>> getModels() {
		// TODO:下面这个情况只适合plugin以源码进行编译的方式存在，model会自动被解析。如果plugin以jar
		// 的形式存在，则需要对lib目录下的所有jar进行遍历寻找，效率较低。
		return QuickUtils.getPackageClasses(getRootPackage() + ".model", null,
				Entity.class);
	}
}
