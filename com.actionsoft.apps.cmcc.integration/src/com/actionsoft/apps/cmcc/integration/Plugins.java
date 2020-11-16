package com.actionsoft.apps.cmcc.integration;
/**
 * 注册类
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.integration.aslp.SPMSAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSGetProjectAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSGetUndertakeDeptId;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSProcessDataAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSProjectInforAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSProjectInjectionAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSResultSubAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SPMSVirtualIncomeAslp;
import com.actionsoft.apps.cmcc.integration.aslp.SendEmainAslp;
import com.actionsoft.apps.cmcc.integration.aslp.StateWriteBackAslp;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.ASLPPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
    }
	public List<AWSPluginProfile> register(AppContext context) {
	    List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
	    list.add(new ASLPPluginProfile("SPMSAslp", SPMSAslp.class.getName(), "调用spms状态回写接口", null));
	    list.add(new ASLPPluginProfile("SPMSGetProjectAslp", SPMSGetProjectAslp.class.getName(), "调用spms项目信息接口", null));
	    list.add(new ASLPPluginProfile("SPMSProcessDataAslp", SPMSProcessDataAslp.class.getName(), "调用spms需求部门项目信息接口", null));
	    list.add(new ASLPPluginProfile("SPMSProjectInforAslp", SPMSProjectInforAslp.class.getName(), "调用spms项目信息修改接口", null));
	    list.add(new ASLPPluginProfile("SPMSProjectInjectionAslp", SPMSProjectInjectionAslp.class.getName(), "调用spms项目信息注入接口", null));
	    list.add(new ASLPPluginProfile("SPMSResultSubAslp", SPMSResultSubAslp.class.getName(), "调用spms需求部门接口", null));
	    list.add(new ASLPPluginProfile("SPMSVirtualIncomeAslp", SPMSVirtualIncomeAslp.class.getName(), "调用spms需求部门接口", null));
	   
	    //获取承担部门ID
	    list.add(new ASLPPluginProfile("SPMSGetUndertakeDeptId", SPMSGetUndertakeDeptId.class.getName(), "调用spms承担部门接口", null));
	   
	    list.add(new ASLPPluginProfile("SendEmainAslp", SendEmainAslp.class.getName(), "发送邮件、短信接口", null));
	    
	    list.add(new ASLPPluginProfile("StateWriteBackAslp", StateWriteBackAslp.class.getName(), "状态回写接口，支持所有流程调用", null));

	    return list;
	}
}
