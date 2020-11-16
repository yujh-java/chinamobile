package com.actionsoft.apps.cmcc.integration.util;

/**
 * 创建session 未使用
 * @author nch
 * @date 20170622
 */

import org.json.JSONException;
import org.json.JSONObject;

import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;

public class CreateSession {
	/**
	 * 创建用户sessionID
	 * @param userid
	 * @return
	 */
	public static String createSessionId(String userid){
		UserContext uc = UserContext.fromUID(userid);
		String sid = "";
		if (uc.getSessionId() != null && !uc.getSessionId().equals("")) {// 该用户的会话没有失效
			boolean checkSidOnLine = SDK.getPortalAPI().checkSession(uc.getSessionId());// 检查sessionid是否存在
			if (!checkSidOnLine) {
				SDK.getPortalAPI().refreshSession(uc.getSessionId());// 刷新session
			}
			sid = uc.getSessionId();
		} else {// 用用户创建一个session
			String sidString = SDK.getPortalAPI().createClientSession(userid, "123456");
			try {// 解析出返回的sid
				JSONObject sidJson = new JSONObject(sidString);
				String data = sidJson.getString("data");
				JSONObject dataJson = new JSONObject(data);
				sid = dataJson.getString("sid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return sid;
	}
}
