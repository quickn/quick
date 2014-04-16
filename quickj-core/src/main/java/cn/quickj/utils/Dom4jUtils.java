package cn.quickj.utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 封装DOM4J的xpath查询，简化Node的text设置和获取。
 * 
 * @author 李百军
 * 
 */
public class Dom4jUtils {
	private static final Logger LOG = Logger.getLogger(Dom4jUtils.class);

	/**
	 * 分析一个xml字符串，并产生一个Document对象。
	 * 
	 * @param xml
	 * @return
	 */
	public static Document parseXml(String xml) {
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			LOG.fatal(e);
		}
		return null;
	}

	/**
	 * 给一个节点设置文本内容
	 * 
	 * @param xml
	 * @param xpath
	 * @param text
	 */
	public static void setElementText(Element xml, String xpath, String text) {
		Element e = getElement(xml, xpath, true);
		if (text == null)
			e.setText("");
		else
			e.setText(text);
	}

	public static String getElementText(Element xml, String xpath) {
		return getElementText(xml, xpath, "");
	}

	public static String getElementText(Element xml, String xpath,
			String defaultValue) {
		Element e = getElement(xml, xpath);
		if (e == null)
			return defaultValue;
		return e.getText();
	}

	public static String getElementAttribute(Element xml, String xpath,
			String attrName) {
		Element e = getElement(xml, xpath);
		return e.attributeValue(attrName);
	}

	public static void setElementAttribute(Element xml, String xpath,
			String attrName, String attrValue) {
		Element e = getElement(xml, xpath, true);
		e.addAttribute(attrName, attrValue);
	}

	public static Element getElement(Element xml, String xpath) {
		return getElement(xml, xpath, false);
	}

	public static Element getElement(Element xml, String xpath,
			boolean autoCreate) {
		Element p = (Element) xml.selectSingleNode(xpath);
		if (p != null)
			return p;
		// 如果不存在，并要求自动创建这个Node
		if (autoCreate) {
			return DocumentHelper.makeElement(xml, xpath);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Element xml, String xpath) {
		return xml.selectNodes(xpath);
	}
	public static void main(String[] args) {
		Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("root");
		doc.setRootElement(root);
		setElementAttribute(root,"/abc","name","lbj");
		setElementAttribute(root,"/abc","value","123");
		System.out.println(doc.asXML());
	}
}
