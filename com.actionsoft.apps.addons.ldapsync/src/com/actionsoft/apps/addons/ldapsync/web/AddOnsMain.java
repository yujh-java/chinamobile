package com.actionsoft.apps.addons.ldapsync.web;

import com.actionsoft.bpms.commons.addons.AddOnsInterface;
import com.actionsoft.bpms.server.UserContext;

public class AddOnsMain implements AddOnsInterface {
	public AddOnsMain() {
	}

	public String mainPage(UserContext context) {
		return new AddonsLdapWeb(context).showMainPage();
	}
}
