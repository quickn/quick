package cn.quickj.imexport.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertor extends AbstractDataConvertor {

	public Object convert(String data) {
		if(cdata==null){
			cdata = (data.length()>10)?"yyyy-MM-dd hh:mm:ss":"yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(cdata);
		try {
			return sdf.parse(data);
		} catch (ParseException e) {
			return new Date();
		}
	}

}
