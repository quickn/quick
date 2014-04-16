package cn.quickj.imexport.convert;

import java.util.HashMap;
import java.util.Map;

public class ScriptTextConvertor extends AbstractDataConvertor{

	public Object convert(String data) {
		Map<String,Object> vars = new HashMap<String, Object>();
		vars.put("text", data);
		return org.mvel2.MVEL.eval(cdata,vars).toString();
	}

}
