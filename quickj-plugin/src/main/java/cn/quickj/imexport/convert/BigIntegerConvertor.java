package cn.quickj.imexport.convert;

import java.math.BigInteger;

public class BigIntegerConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return BigInteger.valueOf(Long.valueOf(data));
		} catch (Exception e) {
			return null;
		}
	}

}
