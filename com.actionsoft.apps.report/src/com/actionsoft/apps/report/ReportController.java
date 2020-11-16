package com.actionsoft.apps.report;

import com.actionsoft.apps.report.web.ReportWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/**
 * @author wuxx
 * @version 创建时间：2019年1月17日 测试管理CMD
 */
@Controller
public class ReportController {
	@Mapping("com.actionsoft.apps.report.photoshow")
	public String photoShow(UserContext sid, String processId, String processDefid) {
		ReportWeb web = new ReportWeb();
		System.err.println("表单快照cmd" + 123);
		return web.Kzmanage(sid, processId, processDefid);
	}

}


