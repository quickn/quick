package cn.quickj.imexport.convert;

public class FloatConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return Float.valueOf(data);
		} catch (Exception e) {
			return null;
		}
	}

}
