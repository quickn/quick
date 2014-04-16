package cn.quickj.imexport.convert;

public class IntegerConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return Integer.valueOf(data);
		} catch (Exception e) {
			return null;
		}
	}

}
