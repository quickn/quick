package cn.quickj.session;

import java.util.Calendar;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import cn.quickj.Setting;
import cn.quickj.utils.QuickUtils;

public abstract class AbstractHttpSession implements Session {
	protected String sessionId;
	protected long updateAccessTime;

	public void create(String sessionId,HttpServletRequest request) {
		updateAccessTime();
		this.sessionId = QuickUtils.md5(request.getRemoteAddr()
				+ System.currentTimeMillis() + (new Random().nextLong()));
	}

	public String getSessionId() {
		return sessionId;
	}



	public boolean isexpired() {
		return (Calendar.getInstance().getTimeInMillis() - updateAccessTime) > (Setting.sessionTimeOut*1000);
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public void updateAccessTime() {
		updateAccessTime = Calendar.getInstance().getTimeInMillis();
	}
}
