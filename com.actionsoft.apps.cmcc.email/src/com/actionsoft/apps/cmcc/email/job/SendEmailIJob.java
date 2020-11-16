package com.actionsoft.apps.cmcc.email.job;
/**
 * 发送邮件定时器
 * @author niech
 * @date 20170922
 */
import java.sql.Connection;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.apps.cmcc.email.util.JobSendEmailUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

public class SendEmailIJob implements IJob {
	private static String jobid = "e0199daa-91b7-4527-b8bf-722e92959d90";//定时器ID
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn = null;
		try {
			conn = DBSql.open();
			//查询适用此定时器流程邮件配置
			List<BO> list = SDK.getBOAPI().query("BO_ACT_EMAIL_APPCONFIG").addQuery("APPLYJOB = ", jobid).addQuery("ISVALID = ", 1).connection(conn).list();
			//发送邮件
			JobSendEmailUtil util = new JobSendEmailUtil();
			util.sendMsg(list, conn);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
	}
}
