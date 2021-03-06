package com.actionsoft.apps.cmcc.testmanagement.event;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2018年12月21日 上午10:56:22 测试管理确认单审批流程创建后，状态信息保存
 */
public class ProcessCreateSaveEvent extends ExecuteListener implements ExecuteListenerInterface {
	public String getDescription() {
		return "测试管理确认单审批流程创建后，状态信息保存";
	}

	@Override
	public void execute(ProcessExecutionContext param) throws Exception {
		System.err.println(">>>ononon");
		String process_id = param.getProcessInstance().getId();
		String process_definid = param.getProcessInstance().getProcessDefId();// 流程定义ID
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String PROSUBTIME = sdf.format(date);
		Connection conn = null;
		try {
			conn = DBSql.open();
			// 记录流程提交时间
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", process_id)
					.connection(conn).list();
			if (list != null && list.size() > 0) {
				BO bo = list.get(0);
				String tjsj = bo.getString(PROSUBTIME);
				if (UtilString.isEmpty(tjsj)) {
					bo.set("PROSUBTIME", PROSUBTIME);
					SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo, conn);
				}
			} else {
				BO bo = new BO();
				String createUser = param.getProcessInstance().getCreateUser();
				String title = param.getProcessInstance().getTitle();
				String PROCESSTYPE = DBSql.getString(conn,
						"SELECT PROCESSTYPE FROM BO_ACT_PROCESS_DATA WHERE PROCESSDEFNID = ?",
						new Object[] { process_definid });
				bo.set("PROSUBTIME", PROSUBTIME);
				bo.set("PROCESSID", process_id);
				bo.set("CREATEUSERID", createUser);
				bo.set("TITLE", title);
				bo.set("PROCESSTYPE", PROCESSTYPE);
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, param.getUserContext(), conn);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
	}

}
