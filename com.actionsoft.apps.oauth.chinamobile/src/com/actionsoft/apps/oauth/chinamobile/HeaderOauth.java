package com.actionsoft.apps.oauth.chinamobile;

import java.io.IOException;

import com.actionsoft.bpms.commons.oauth.AbstractOauth;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.DispatcherRequest;
import com.actionsoft.bpms.server.RequestParams;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * @author zhanghq
 *
 */

public class HeaderOauth extends AbstractOauth {

	@Override
	public String getOauthPage(RequestParams params) {
		return null;
	}

	@Override
	public String validate(RequestParams params) throws IOException {
		String user = DispatcherRequest.getContext().getExchange().getHeader().get("iv-user");//用户账号
		if(user.indexOf("@")==-1){
			user=user+"@hq.cmcc";
		}
		if(!UtilString.isEmpty(user) && UserCache.getModel(user)!=null
				&& UtilString.isEmpty(SDK.getORGAPI().validateUsers(user))){
			return user;
		}else{
			return null;
		}
	}

	@Override
	public boolean hasOauthPage() {
		return false;
	}

	@Override
	public long getCookieTime(UserModel userModel) {
		// TODO Auto-generated method stub
//		long time = userModel.getSessionTime();
		return 0;
	}
}
