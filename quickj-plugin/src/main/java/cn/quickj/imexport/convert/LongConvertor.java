package cn.quickj.imexport.convert;

public class LongConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return Long.valueOf(data);
		} catch (Exception e) {
			return null;
		}
	}

}
