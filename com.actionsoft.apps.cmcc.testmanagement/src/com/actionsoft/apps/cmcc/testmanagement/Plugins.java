package com.actionsoft.apps.cmcc.testmanagement;

import java.util.ArrayList;
import java.util.List;
import com.actionsoft.apps.cmcc.testmanagement.at.GetProjectManager;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;


/** 
* @author yujh
* @version 创建时间：2019年7月25日 上午10:02:14 
* 类说明 
*/
public class Plugins implements PluginListener {
	@Override
	public List<AWSPluginProfile> register(AppContext arg0) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		list.add(new AtFormulaPluginProfile("获取项目经理ID", "@getProjectManager(*processId)", GetProjectManager.class.getName(), "流程ID","根据流程ID获取项目经理"));
		return list;
	}
}


