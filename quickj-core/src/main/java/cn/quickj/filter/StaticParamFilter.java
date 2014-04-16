package cn.quickj.filter;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.quickj.Setting;
import cn.quickj.action.Action;
import cn.quickj.test.mock.MockHttpServletRequest;
import cn.quickj.utils.JakartaMultiPartRequest;
import cn.quickj.utils.QuickUtils;

/**
 * 把Form中的参数设置到Action中，如果是类似foo__b的参数名称，则Filter会
 * 首先调用Action的getFoo()获得foo对象，然后调用foo.setB()的方法进行参数 设置。
 * 
 * @author lbj
 * 
 */
public class StaticParamFilter implements ActionFilter {
	/**
	 * 对于参数是否启用反跨站攻击的功能，如果启用了反跨站攻击，则会主动过滤参数中的攻击脚本。
	 */
	private boolean enableAntiXss;
	private static Log log = LogFactory.getLog(TimeFilter.class);

	public int after(Action action) {
		return 0;
	}

	/**
	 * 对每个参数做了指定的转换过程。
	 */
	public int before(Action action) {
		HttpServletRequest request = action.getRequest();
		if (request instanceof MockHttpServletRequest) {
			doMockFilter(action);
			return 0;
		}
		JakartaMultiPartRequest mpRequest = null;
		Enumeration<?> params = request.getParameterNames();
		// multidata/form,要以流的形式读取。
		if (params.hasMoreElements() == false) {
			try {
				mpRequest = new JakartaMultiPartRequest(action.getRequest(),
						Setting.uploadDir, Setting.uploadMaxSize);
				params = mpRequest.getParameterNames();
			} catch (Exception e1) {
				e1.printStackTrace();
				mpRequest = null;
			}
		}
		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			try {
				String value;
				String[] values = (mpRequest != null) ? mpRequest
						.getParameterValues(param) : request
						.getParameterValues(param);
				if (values != null) {
					if (values.length == 1)
						value = values[0];
					else {
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < values.length;) {
							sb.append(values[i]);
							i++;
							if (i < values.length)
								sb.append(",");
						}
						value = sb.toString();
					}
				} else
					value = (mpRequest != null) ? mpRequest.getParameter(param)
							: request.getParameter(param);
				//如果启用了跨站脚本攻击检测，并且检测到跨站脚本攻击，则立刻返回，不在进行任何处理。
				if (enableAntiXss && detectXSSScript(value)){
					log.error("检测到跨站脚本攻击!代码为:"+value);
					return ActionFilter.NO_PROCESS;
				}
				if (param.length() > 0)
					setFieldValue(action, param, value);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		// 设置文件
		if (mpRequest != null) {
			params = mpRequest.getFileParameterNames();
			while (params.hasMoreElements()) {
				String param = (String) params.nextElement();
				try {
					File file = mpRequest.getFile(param)[0];
					String newFileName = file.getAbsolutePath().replace(
							".tmp",
							"."
									+ QuickUtils.getExtname(mpRequest
											.getFileNames(param)[0]));
					File newFile = new File(newFileName);
					file.renameTo(newFile);
					setFieldValue(action, param, newFile);
					String filename = mpRequest.getFileNames(param)[0];
					setFieldValue(action, param + "Name", filename);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	/**
	 * 过滤跨站脚本攻击
	 * 
	 * @param value
	 * @return
	 */
	private boolean detectXSSScript(String value) {
		Pattern pattern = Pattern.compile("((\\%3C)|<)[^\\n]+((\\%3E)|>)");
		Matcher m = pattern.matcher(value);
		return m.find();
	}

	private void doMockFilter(Action action) {
		MockHttpServletRequest request = (MockHttpServletRequest) action
				.getRequest();
		Enumeration<?> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			try {
				String value;
				String[] values = request.getParameterValues(param);
				if (values != null) {
					if (values.length == 1)
						value = values[0];
					else {
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < values.length;) {
							sb.append(values[i]);
							i++;
							if (i < values.length)
								sb.append(",");
						}
						value = sb.toString();
					}
				} else
					value = request.getParameter(param);
				if (param.length() > 0)
					setFieldValue(action, param, value);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		HashMap<String, File> files = request.getFiles();
		for (String param : files.keySet()) {
			try {
				setFieldValue(action, param, files.get(param));
				setFieldValue(action, param + "Name", files.get(param)
						.getName());
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void setFieldValue(Action action, String param, Object value)
			throws NoSuchMethodException {
		if (Setting.fieldBySetter)
			QuickUtils.setFieldValue(action, param, value);
		else
			QuickUtils.setFieldValue2(action, param, value);
	}

	public void init(HashMap<String, String> params) {
		if(params!=null)
			this.enableAntiXss = Boolean.parseBoolean(params.get("enableAntiXss"));
	}

}
