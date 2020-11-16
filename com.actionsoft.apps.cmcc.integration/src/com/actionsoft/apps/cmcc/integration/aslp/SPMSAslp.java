package com.actionsoft.apps.cmcc.integration.aslp;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * 状态回写接口aslp，调用spms接口。所有流程通用
 * @author nch
 * @date 20170622
 */
import java.util.Map;

import net.sf.json.JSONObject;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.sdk.local.SDK;

public class SPMSAslp implements ASLP{

	@SuppressWarnings("unchecked")
	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'流程状态信息'"}) 
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "STATE_WRITEBACK");
		Map<String, String> map = (Map<String, String>)params.get("mapMsg");
		String processid = map.get("process_id");
		String task_id=map.get("task_id");
		String status=map.get("status");

		String type = map.get("type");
		System.err.println("=======流程类型type："+type+"============");
		/*
		 * chenxf modify
		 */
		String url = "";
		//判断是否为研究院，从而调用不同的接口参数
		if(type.indexOf("cmri") != -1){
			System.err.println("=======院内。。。。。。。。============");
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "STATE_WRITEBACK_CMRI");
		}else{
			System.err.println("=======集团。。。。。。。。============");
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "STATE_WRITEBACK");
		}
		String title = map.get("title");
		String statename = map.get("statename");
		String submitterid= map.get("submitterid");
		String ownerids= map.get("ownerids");
		String submittime = map.get("submittime");
		String closetime= map.get("closetime");
		String passedtime = map.get("passedtime");
		String iscancelworkflow = map.get("iscancelworkflow");
		String providetype = map.get("providetype");
		try {
		submittime = URLEncoder.encode(submittime,"UTF-8");
		closetime = URLEncoder.encode(closetime,"UTF-8");
	    passedtime = URLEncoder.encode(passedtime,"UTF-8");
		statename =URLEncoder.encode(statename,"UTF-8");
		title = URLEncoder.encode(title,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		SDK.getLogAPI().getLogger(this.getClass()).error("closetime="+closetime);
//		String str = CmccUrlUtil.post(url, map);
		StringBuilder sb = new StringBuilder(url);
		sb.append("?process_id=");
		sb.append(processid);
		sb.append("&task_id=");
		sb.append(task_id);
		sb.append("&status=");
		sb.append(status);
		sb.append("&type=");
		sb.append(type);
		sb.append("&title=");
		sb.append(title);
		sb.append("&statename=");
		sb.append(statename);
		sb.append("&submitterid=");
		sb.append(submitterid);
		sb.append("&ownerids=");
		sb.append(ownerids);
		sb.append("&submittime=");
		sb.append(submittime);
		sb.append("&closetime=");
		sb.append(closetime);
		sb.append("&passedtime=");
		sb.append(passedtime);
		sb.append("&iscancelworkflow=");
		sb.append(iscancelworkflow);
		sb.append("&providertype=");
		sb.append(providetype);
		SDK.getLogAPI().getLogger(this.getClass()).error("sbf="+sb.toString());
		String str = CmccUrlUtil.get(sb.toString());
		try{
			SDK.getLogAPI().getLogger(this.getClass()).error("str="+str);
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson  = resultJson.getJSONObject("data");
			int code = (Integer)datajson.get("code");
			String msg = datajson.getString("msg");
			ro.msg(msg);
			ro.errorCode(code+"");
			if(code != 1){
				SDK.getLogAPI().getLogger(this.getClass()).error("状态回写接口失败,流程实例ID:"+processid+";任务ID:"+task_id+";状态:"+status);
				SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
			}
		}catch(Exception e){
			ro.msg("状态回写接口失败,返回信息格式错误");
			ro.errorCode("0");
			SDK.getLogAPI().getLogger(this.getClass()).error("状态回写接口失败,流程实例ID:"+processid+";任务ID:"+task_id+";状态:"+status);
		}
		return ro;
	}
}
