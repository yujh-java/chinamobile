package com.actionsoft.apps.report;

import com.actionsoft.apps.report.web.ReportWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/**
 * @author wuxx
 * @version 创建时间：2019年1月29日
 */
@Controller
public class EnterpriceController {
	@Mapping("com.actionsoft.apps.report.enterpriceshow")
	public String photoShow(UserContext sid, String processId, String processDefid) {
		ReportWeb web = new ReportWeb();
		System.err.println("表单快照cmd" + 66666);
		return web.enterprice(sid, processId, processDefid);
	}

}
