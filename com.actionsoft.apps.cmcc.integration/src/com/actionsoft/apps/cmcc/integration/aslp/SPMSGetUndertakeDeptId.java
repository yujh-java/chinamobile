package com.actionsoft.apps.cmcc.integration.aslp;

import java.util.Map;

import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSDataAccessException;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;
/**
 * 立项、结项、里程碑等共用接口类
 * 获取承担部门ID
 * @author chenxf
 *
 */
public class SPMSGetUndertakeDeptId implements ASLP{

	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'获取承担部门ID'"}) 
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		//流程实例ID
		String processID = (String) params.get("processID");
		//流程类型
		String processType = (String) params.get("processType");
		
		String url = "";
		//判断是否为研究院，从而调用不同的接口参数
		if(processType.indexOf("cmri") != -1){
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK_CMRI");
		}else{
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		}
		url = url+"?type="+processType+"&process_id="+processID;
		System.err.println("====获取承担部门ID的url："+url+"======");
		String str = CmccUrlUtil.get(url);	
		System.err.println("====返回流程平台的数据str："+str+"======");
		
		try {
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson  = resultJson.getJSONObject("data");
			int code = (Integer)datajson.get("code");
			String msg = datajson.getString("msg");
			if(code == 1){
				//记录项目信息
				JSONObject json = JSONObject.fromObject(datajson.get("data"));
				String QTYFJGBM = json.getString("lead_research_ou");//牵头研发机构部门编码
				String QTYFJGBMID = "";//牵头研发机构部门ID
				if(!UtilString.isEmpty(QTYFJGBM)){
					QTYFJGBMID =  DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", 
													new Object[]{QTYFJGBM});
				}
				if(UtilString.isEmpty(QTYFJGBM) || UtilString.isEmpty(QTYFJGBMID)){
					ro.msg("牵头研发机构部门为空或错误");
					ro.errorCode("2");
				}else{
					ro.msg(msg);
					ro.errorCode(QTYFJGBMID+"");
				}
			}else{
				ro.msg("牵头研发机构部门为空或错误");
				ro.errorCode("2");
			}
		} catch (AWSDataAccessException e) {
			ro.msg("牵头研发机构部门为空或错误");
			ro.errorCode("2");
		}
		return ro;
	}
}
