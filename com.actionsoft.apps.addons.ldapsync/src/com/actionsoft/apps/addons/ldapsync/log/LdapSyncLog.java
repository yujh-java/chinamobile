package com.actionsoft.apps.addons.ldapsync.log;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.lifecycle.api.AppsAPIManager;
import com.actionsoft.bpms.util.UtilFile;

public class LdapSyncLog {
	private static String pathName = AppsAPIManager.getInstance().getAppContext(LdapSyncConstant.ADDONS_IOX_APPID).getPath() + "sync.log";

	public static void writeLogInfoToFile(String logInfo) {
		UtilFile utilFile = new UtilFile(pathName);
		utilFile.writeUTF8(logInfo);
	}

	public static String readLogInfoFromFile() {
		UtilFile utilFile = new UtilFile(pathName);
		String str = utilFile.readStrUTF8();
		return str;
	}

}
