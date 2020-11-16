package com.actionsoft.apps.cmcc.integration.aslp;

/**
 * 状态回写接口aslp，调用spms接口注入项目信息
 * @author Zhaoxs
 * @date 2017-06-27
 */
import java.util.Map;

import net.sf.json.JSONObject;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.sdk.local.SDK;

public class SPMSProjectInjectionAslp implements ASLP {
	@SuppressWarnings("unchecked")
	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'项目信息注入接口'" })
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "ProjectInjection");
		Map<String, String> map = (Map<String, String>) params.get("mapMsg");
		String str = CmccUrlUtil.post(url, map);
		String processid = map.get("process_id");
		try{
			SDK.getLogAPI().getLogger(this.getClass()).error("str="+str);
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson = resultJson.getJSONObject("data");
			int code = (Integer) datajson.get("code");
			String msg = datajson.getString("msg");
			ro.msg(msg);
			ro.errorCode(code + "");
			if(code != 1){
			SDK.getLogAPI().getLogger(this.getClass()).error("msg="+msg);
			SDK.getLogAPI().getLogger(this.getClass()).error("调用spms注入项目信息接口失败,返回信息格式错误。流程实例ID:"+processid);

			}
		}catch(Exception e){
			SDK.getLogAPI().getLogger(this.getClass()).error("调用spms注入项目信息接口失败,返回信息格式错误。流程实例ID:"+processid);
			ro.msg("调用spms接口失败,返回信息格式错误");
			ro.errorCode("0");
		}
		return ro;
	}
}
