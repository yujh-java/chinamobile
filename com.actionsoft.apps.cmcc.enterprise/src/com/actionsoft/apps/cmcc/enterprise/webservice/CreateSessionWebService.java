package com.actionsoft.apps.cmcc.enterprise.webservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.actionsoft.bpms.commons.login.constant.LoginConst;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.server.SSOUtil;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
import com.actionsoft.bpms.server.bind.annotation.Param;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 创建sessionID的公共webservice类
 * @author chenxf
 *
 */
@Controller(type = HandlerType.NORMAL, apiName = "CreateSessionWebService Api", desc = "创建sid的接口")
@WebService(serviceName = "CreateSessionWebServiceApi")
public class CreateSessionWebService {
	
	/**
	 * 创建sid会话
	 * @param uid
	 * @return
	 */
	@Mapping(value = "CreateSessionWebServiceApi.createSession")
	public String createSession(
			@Param(value = "uid", desc = "账号", required = true) 
			@WebParam(name = "uid") String uid){
		//如果传入的账号没有后缀，则加入集团后缀
		if(uid != null && !"".equals(uid) && uid.indexOf("@") == -1){
			uid += "@hq.cmcc";
		}
		
		try {
			//判断账号是否合法
			if(!UtilString.isEmpty(uid) 
					&& UserCache.getModel(uid) != null
					&& UtilString.isEmpty(SDK.getORGAPI().validateUsers(uid))){
				//获取本地IP
				String host = InetAddress.getLocalHost().getHostAddress();
				//调用平台API，创建sid会话
//				String sessionid = SDK.getPortalAPI().createClientSession(uid, "123456");
				//创建无密码的session会话
				String sessionid = new SSOUtil().
										registerClientSessionNoPassword(
															uid, 
															LoginConst.DEFAULT_LANG, 
															host, 
															LoginConst.DEVICE_PC);
				return sessionid;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace(System.err);
		}
		return null;
	}
}
