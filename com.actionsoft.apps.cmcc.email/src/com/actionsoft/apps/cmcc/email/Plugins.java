package com.actionsoft.apps.cmcc.email;
/**
 * 注册类
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.email.aslp.TaskSendMsgASPLP;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.ASLPPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
    }
	public List<AWSPluginProfile> register(AppContext context) {
	    List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
	    list.add(new ASLPPluginProfile("TaskSendMsgASPLP", TaskSendMsgASPLP.class.getName(), "针对任务发送邮件、短信接口", null)); 
	    return list;
	}
}
