package com.actionsoft.apps.oauth.chinamobile;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.OauthPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
	}

	public List<AWSPluginProfile> register(AppContext context) {
		List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
		
		list.add(new OauthPluginProfile("chinamobile",HeaderOauth.class.getName(), ""));
		
		return list;
	}

}
