package cn.quickj.imexport.convert;

public class ShortConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try{
			return Short.valueOf(data);
		} catch (Exception e) {
			return null;
		}
	}

}
