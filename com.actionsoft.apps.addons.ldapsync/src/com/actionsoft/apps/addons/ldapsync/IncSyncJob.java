package com.actionsoft.apps.addons.ldapsync;

import java.util.Date;

import net.sf.json.util.JSONUtils;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.search.IncSync;
import com.actionsoft.bpms.schedule.model.AWSScheduleModel;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilSerialize;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;

public class IncSyncJob implements Job {

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		String json = SDK.getJobAPI().getJobParameter(ctx);
		Date startTime = ctx.getPreviousFireTime();
		String ctls = null;
		if (startTime == null) {
			if (!UtilString.isEmpty(json) && JSONUtils.mayBeJSON(json)) {
				JSONObject jo = UtilSerialize.parseObject(json);
				String s = jo.getString("startTime");
				if (UtilString.isEmpty(s)) {
					return;
				}

				ctls = jo.getString("delCtls");
				startTime = UtilDate.parseDatetime(s);
			}
		}

		if (startTime == null) {
			AWSScheduleModel sm = SDK.getJobAPI().getJobModel(ctx);
			System.err.println("第一次增量同步未执行，原因：缺少JSON格式参数，如：{startTime:\"" + UtilDate.datetimeFormat(new Date()) + "\"}：" + sm.getAppId() + " -> " + sm.getId() + "，" + sm.getName() + "，" + sm.getClassz());
			return;
		}

		IncSync sync = new IncSync(new LdapConf(), startTime);
		sync.setDelCtls(ctls);
		try {
			sync.sync();
		} catch (Exception e) {
			e.printStackTrace();
			sync.log(e);
		}
	}

}
