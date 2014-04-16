package cn.quickj.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.quickj.Setting;
import cn.quickj.action.Action;
import freemarker.template.TemplateException;

/**
 * 页面缓存Filter，带有这个Filter的Action的Method将会在第一次访问后 生成一个html页面，如果访问的URI为
 * /aa/bb/cc，则生成一个在public目录 里面的aa/bb/cc/index.html文件。
 * 下次访问的时候检测文件的时间和当前的时间差距，只要在timeout范围内的， 则均不再重新生成。
 * 
 * @author lbj
 * 
 */
public class PageCacheFilter implements ActionFilter {
	private String fileName;

	private long timeout = -1;

	public int after(Action action) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			StringWriter writer = new StringWriter();
			action.getTemplate().process(action, writer);
			fos.write(writer.getBuffer().toString().getBytes(
					Setting.DEFAULT_CHARSET));
			fos.close();
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ActionFilter.NEED_PROCESS;
	}

	/**
	 * 检查文件系统中是否存在该文件。
	 */
	public int before(Action action) {
		String uri2 = action.getRequest().getRequestURI();
		if (!uri2.endsWith("/"))
			uri2 = uri2 + "/";
		fileName = Setting.webRoot + uri2;
		File f = new File(fileName);
		f.mkdirs();
		fileName = fileName + "index.html";
		f = new File(fileName);
		if ((Calendar.getInstance().getTimeInMillis() - f.lastModified()) < timeout) {
			HttpServletResponse response = action.getResponse();
			response.setContentType("text/html; charset="
					+ Setting.DEFAULT_CHARSET);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
				ServletOutputStream out = response.getOutputStream();
				byte[] buf = new byte[1024];
				int length = 0;
				length = fis.read(buf);
				while (length >= 0) {
					out.write(buf, 0, length);
					length = fis.read(buf);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return ActionFilter.NO_PROCESS; // 不需要进一步处理了。
		}
		return ActionFilter.NEED_PROCESS;
	}

	public void init(String param) {
		if (param != null) {
			String[] s = param.split("=");
			if (s.length >= 2 && s[0].startsWith("timeout"))
				timeout = Integer.parseInt(s[1]) * 1000;
		}
	}

	public void init(HashMap<String, String> params) {
		if(params.get("timeout")!=null){
			timeout = Integer.parseInt(params.get("timeout"));
		}
	}

}
