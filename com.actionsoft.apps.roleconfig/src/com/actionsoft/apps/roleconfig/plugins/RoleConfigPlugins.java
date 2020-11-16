package com.actionsoft.apps.roleconfig.plugins;


import java.util.ArrayList;
import java.util.List;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AppCustomActionPluginProfile;
import com.actionsoft.apps.roleconfig.event.AppInstallListenerEvent;

public class RoleConfigPlugins implements PluginListener{

	@Override
	public List<AWSPluginProfile> register(AppContext arg0) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		list.add(new AppCustomActionPluginProfile(AppInstallListenerEvent.class.getName()));
		return list;
	}
	
}
