package com.actionsoft.apps.cmcc.integration.aslp;

/**
 * 状态回写接口aslp，调用spms接口修改项目信息
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

public class SPMSProjectInforAslp implements ASLP {

	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'项目信息修改接口'" })
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject res = ResponseObject.newOkResponse();
		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "ProjectInjection");
		String processId = (String) params.get("processid");
		int i = (int) params.get("isback");
		if(i==0){
			url = url + "?process_id=" + processId + "&complete=1";
		}else{
			url = url + "?process_id=" + processId + "&complete=1"+"&isback="+i;
		}
		String str = CmccUrlUtil.get(url);
		try{
			SDK.getLogAPI().getLogger(this.getClass()).error("str="+str);
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson = resultJson.getJSONObject("data");
			int code = (Integer) datajson.get("code");
			String msg = datajson.getString("msg");
			res.msg(msg);
			res.errorCode(code + "");
			if(code != 1){
				SDK.getLogAPI().getLogger(this.getClass()).error("msg="+msg);
				SDK.getLogAPI().getLogger(this.getClass()).error("调用spms修改项目信息失败,返回信息格式错误 。流程实例ID:"+processId);

				}
			}catch(Exception e){
		    SDK.getLogAPI().getLogger(this.getClass()).error("调用spms修改项目信息失败,返回信息格式错误。流程实例ID:"+processId);
			res.msg("调用spms接口失败,返回信息格式错误");
			res.errorCode("0");
		}
		return res;

	}
}
