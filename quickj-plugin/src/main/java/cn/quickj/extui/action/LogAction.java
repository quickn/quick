package cn.quickj.extui.action;

import java.util.Collection;
import java.util.HashMap;

import cn.quickj.extui.action.ExtBaseAction;
import cn.quickj.hibernate.Paginate;
import cn.quickj.security.model.OperateAuditLog;
import cn.quickj.security.service.OperateAuditLogService;

import com.google.inject.Inject;

public class LogAction extends ExtBaseAction {
	@Inject
	private OperateAuditLogService operateAuditLogService;
	@Inject
	private OperateAuditLog operateAuditLog;
	private String startDate;
	private String endDate;

	public String list() throws Exception {
		Paginate paginate = new Paginate(start, limit);
		Collection<OperateAuditLog> operateAuditLogs = operateAuditLogService
				.findOperateAuditLogByExample(operateAuditLog, startDate,
						endDate, paginate, sort, dir);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("total", paginate.getTotal());
		data.put("operateAuditLogs", operateAuditLogs);
		return toJson(data);
	}

	public String load(String id) {
		if (id != null)
			operateAuditLog = operateAuditLogService.getOperateAuditLog(Integer
					.parseInt(id));
		return toJson(operateAuditLog);
	}

	public String save() {
		operateAuditLogService.save(operateAuditLog);
		return toJson(null);
	}

	public String delete(String ids) {
		operateAuditLogService.delete(ids);
		return toJson(null);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
