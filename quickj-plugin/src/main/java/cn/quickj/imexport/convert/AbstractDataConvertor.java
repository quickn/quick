package cn.quickj.imexport.convert;

import org.dom4j.Element;


public abstract class AbstractDataConvertor implements IDataConvertor {
	protected String cdata;
	public void config(Element e) {
		cdata = e.getText();
	}
	public String toString(Object obj) {
		if(obj!=null)
			return obj.toString();
		return "";
	}
}
