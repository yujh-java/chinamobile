package com.actionsoft.apps.cmcc.integration.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.apps.cmcc.integration.util.LdapUtil;
import com.actionsoft.apps.cmcc.integration.util.UserUtil;
import com.actionsoft.bpms.schedule.IJob;

/** 
* @author yujh
* @version 创建时间：2020年5月18日 下午3:00:54 
* 同步外协账号（科研管理部）
*/
public class SyncVPNUserJob implements IJob{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//UserUtil userUtil =new UserUtil();
		//userUtil.SyncVPNUser();
		LdapUtil ldapUtil =new LdapUtil();
		ldapUtil.SyncVPNUser();
	}
}


