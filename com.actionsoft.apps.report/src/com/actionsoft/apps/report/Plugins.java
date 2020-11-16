package com.actionsoft.apps.report;

/**
 * 注册类
 * @author zhaoxs
 * @date 20170928
 */
import java.util.ArrayList;
import java.util.List;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.report.at.getAllRoleIDExpress;
import com.actionsoft.apps.report.at.getProcessAppExpress;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
	}

	public List<AWSPluginProfile> register(AppContext context) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		// 注册AT公式
		list.add(new AtFormulaPluginProfile("获取指定用户的所有角色ID", "@getAllRoleID(userid)",
				getAllRoleIDExpress.class.getName(), "用户ID", "返回该用户的所有角色ID"));
		list.add(new AtFormulaPluginProfile("获取流程应用或流程类型", "@getProocessApp(type,allroleID)",
				getProcessAppExpress.class.getName(), "类型,用户的所有角色ID", "返回流程的相关信息"));
		return list;
	}
}
