package cn.quickj.imexport.convert;

import java.math.BigDecimal;

public class BigDecimalConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return BigDecimal.valueOf(Double.valueOf(data));
		} catch (Exception e) {
			return null;
		}
	}
}
