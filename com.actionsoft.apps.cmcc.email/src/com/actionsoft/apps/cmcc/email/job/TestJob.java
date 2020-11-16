/**
 * @author zhaoxs
 * @date 2017年12月6日下午3:24:36
 */
package com.actionsoft.apps.cmcc.email.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;

public class TestJob implements IJob{
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		/*StringBuffer sbf = new StringBuffer();
		//String url = "http://10.2.5.187/spms/cmri/calendar/getday";
		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.email", "Email_Holiday");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strTime = "2018-02-06 11:16:25";
		
		long nowtime = 0;
		try {
			nowtime = sdf.parse(strTime).getTime()/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sbf.append(url).append("?start="+date.getTime()/1000+"&end="+nowtime);
		String str = UtilURL.get(sbf.toString());
		System.err.println("url="+sbf.toString());
		System.err.println("str="+str);
		
		JSONObject json = JSONObject.fromObject(str);
		JSONObject datajson = (JSONObject) json.get("data");
		JSONObject time =  (JSONObject) datajson.get("list");
		int day = time.getInt("holiday");
		System.err.println("day="+day);
		*/
		List<BO> list_email = SDK.getBOAPI().query("BO_ACT_EMAIL_TEMPLETE",true).addQuery("ID = ", "fdce7c9b-f94c-494c-8f19-798b5f0ae638").list();
		System.out.println(list_email);
		System.err.println(list_email);
		
		if(list_email!=null&&list_email.size()>0){
			
			String str = (String) list_email.get(0).get("EMAIL_CONTENT");	
			System.err.println(str);
		}
	
	}
	

}
