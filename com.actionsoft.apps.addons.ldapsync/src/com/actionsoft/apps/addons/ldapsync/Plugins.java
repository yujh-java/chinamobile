package com.actionsoft.apps.addons.ldapsync;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.addons.ldapsync.web.AddOnsMain;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AddOnsPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
	}
	public List<AWSPluginProfile> register(AppContext context) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		list.add(new AddOnsPluginProfile(AddOnsMain.class.getName()));
		return list;
	}
}
