package cn.quickj.filter;

import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;

import cn.quickj.action.Action;
/**
 * 可以防止一个表单被重复提交的Filter，如果要限制表单被重复提交，
 * 可以在Action中加入此Filter，并在需要的地方调用Action的checkToken方法。
 * @author Administrator
 *
 */
public class FormTokenFilter implements ActionFilter {
	public static final String FORM_TOKEN ="QUICKJ_FORM_TOKEN";
	public int after(Action action) {
		action.setAttribute(FORM_TOKEN, null);
		action.setToken(generatorToken());
		return NEED_PROCESS;
	}

	private String generatorToken() {
		return RandomStringUtils.randomAscii(20);
	}

	public int before(Action action) {
		action.setAttribute(FORM_TOKEN, action.getToken());
		return NEED_PROCESS;
	}


	public void init(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		
	}
}
