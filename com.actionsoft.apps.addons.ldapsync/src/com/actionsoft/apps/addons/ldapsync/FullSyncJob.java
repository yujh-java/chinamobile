package com.actionsoft.apps.addons.ldapsync;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.search.FullSync;
import com.actionsoft.apps.addons.ldapsync.util.ConfigUtil;
import com.actionsoft.bpms.commons.cache.iae.IAECache;
import com.actionsoft.bpms.server.UserContext;

@DisallowConcurrentExecution
public class FullSyncJob implements Runnable, Job {

	private UserContext userContext;

	@Override
	public void run() {
		try {
			execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		} finally {
			IAECache.removeValue(LdapSyncConstant.IAE_KEY);
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LdapConf conf = ConfigUtil.getConf();
		FullSync ldapSyncSearch = new FullSync(conf);
		ldapSyncSearch.sync(userContext);
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

}
