package com.actionsoft.apps.cmcc.integration.aslp;
/**
 * 成果提交获取需求部门接口aslp，调用spms接口。所有流程通用
 * @author nch
 * @date 20170627
 */
import java.util.Map;

import net.sf.json.JSONObject;

import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.sdk.local.SDK;

public class SPMSResultSubAslp implements ASLP{

	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'流程状态信息'"}) 
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		String process_type = (String) params.get("process_type");
		/*
		 * chenxf modify
		 */
		String url = "";
		//判断流程类型是否为研究院，从而调用不同的接口参数
		if(process_type.indexOf("cmri") != -1){
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK_CMRI");
		}else{
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		}
		String process_id = (String) params.get("process_id");
		url = url + "?type="+process_type+"&process_id="+process_id;//需求部门接口地址
		String str = CmccUrlUtil.get(url);
		try{
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson = resultJson.getJSONObject("data");
			int code = (Integer)datajson.get("code");
			if(code == 1){
				JSONObject json = JSONObject.fromObject(datajson.get("data"));
				//需求部门ID
				ro.put("xqbmid", json.getString("lead_req_ou"));
				//牵头研发机构部门编码
				ro.put("cdbmid", json.getString("lead_research_ou"));
				ro.errorCode(code+"");
			}else{
				String msg = datajson.getString("msg");
				ro.msg(msg);
				ro.errorCode(code+"");
				SDK.getLogAPI().getLogger(this.getClass()).error("成果提交获取需求部门接口失败,流程实例ID："+process_id);
			}
		}catch(Exception e){
			SDK.getLogAPI().getLogger(this.getClass()).error("成果提交获取需求部门接口失败,流程实例ID："+process_id);
			ro.msg("调用spms接口失败,返回信息格式错误");
			ro.errorCode("0");
		}
		return ro;
	}
}
