package com.actionsoft.apps.cmcc.enterprise.util;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.actionsoft.bpms.schedule.IJob;
import com.actionsoft.sdk.local.SDK;

public class CreateUserMap implements IJob{

	/**
	 * 给指定账号创建兼职角色
	 * @param userid
	 * @param roleid
	 * @param deptid
	 */
	public static void createUserMapData(String userid,String roleid,String deptid){
		SDK.getORGAPI().createUserMap(
							"wangxiaoqiyf@hq.cmcc", 
							"7e014777-84fb-43ff-a885-dcc0ee2a9308", 
							"98c8f825-76f8-40e5-8de5-fb578043e649", 
							false, 
							false);
	}
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		createUserMapData(null,null,null);
	}
}
