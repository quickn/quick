package cn.quickj.imexport.convert;


import org.dom4j.Element;

public interface IDataConvertor {
	public Object convert(String data);
	public String toString(Object obj);
	public void config(Element e);
}
