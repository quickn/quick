package cn.quickj.imexport.convert;

public class DoubleConvertor extends AbstractDataConvertor {
	public Object convert(String data) {
		try {
			return Double.valueOf(data);
		} catch (Exception e) {
			return null;
		}
	}
}
