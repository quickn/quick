package cn.quickj.imexport.convert;

public class BooleanConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		if("true".equalsIgnoreCase(data))
				return true;
		else if("是".equals(data))
			return true;
		else if("1".equals(data))
			return true;
		return false;
	}

}
