package cn.quickj.extui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.AbstractApplication;
import cn.quickj.Setting;

/**
 * Ext类的应用应该继承这个类，因为这个类处理了ajax的错误处理。
 * @author Administrator
 *
 */
public abstract class ExtuiApplication extends AbstractApplication {
	private static Log log = LogFactory.getLog(ExtuiApplication.class);

	public void handleException(Exception e, HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		StringWriter writer = new StringWriter();
		PrintWriter w = new PrintWriter(writer);
		e.printStackTrace(w);
		log.error("调用action发生异常,错误信息为:\n" + writer.toString());

		String respFormat = request.getHeader("resp-format");
		if (respFormat != null) {
			if (respFormat.equalsIgnoreCase("json")) {
				response.setContentType("text/html; charset="
						+ Setting.DEFAULT_CHARSET);
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("success", false);
				data.put("msg", "系统内部错误,错误信息为" + e.toString());
				data.put("detailError", writer.toString());
				try {
					response.getWriter().write(
							jsonObjectMapper.writeValueAsString(data));
				} catch (Exception e1) {
					e1.printStackTrace();
					throw new ServletException(e1);
				}
			}
		} else {
			throw new ServletException(e);
		}
	}
}
