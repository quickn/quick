package cn.quickj.imexport.convert;

import cn.quickj.utils.PinyinUtils;

public class PinyinTextConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		try {
			return PinyinUtils.getPinyinCode(data, true);
		} catch (Exception e) {
			return null;
		}
	}

}
