package com.actionsoft.apps.cmcc.testmanagement.util;

import java.util.List;

import com.actionsoft.apps.cmcc.testmanagement.constant.TestManagementConst;
import com.actionsoft.apps.cmcc.testmanagement.web.TestmanagementWeb;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.sun.xml.fastinfoset.sax.Features;

import jodd.util.URLDecoder;
import net.sf.json.JSONObject;

/** 
* @author yujh:
* @version 创建时间：2019年7月8日 下午3:11:27 
* 接口相关工具类
*/
public class InterfaceUtil {
	/**
	 * 根据流程ID获取项目信息
	 * @param processId
	 * @return
	 */
	public String getProjectInfo(String processId){
		JSONObject json =new JSONObject();
		String url = SDK.getAppAPI().getProperty(TestManagementConst.APPID, TestManagementConst.GET_PROJECT_INFO);
		url+=processId;
		String str = UrlUtil.get(url.toString());
		JSONObject resultJson = JSONObject.fromObject(str);
		JSONObject dataJson  = resultJson.getJSONObject("data");
		JSONObject leadDeptJson = dataJson.getJSONObject("lead_dept");
		JSONObject projectManager = dataJson.getJSONObject("manager");
		String projectName = dataJson.getString("name");//项目名称
		String qtxqbmBM = leadDeptJson.getString("id");//牵头部门编码
		String qtxqbmId = DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",new Object[] {qtxqbmBM});//牵头部门ID
		String projectManagerId =  projectManager.getString("user_id");//项目经理ID
		projectManagerId = SDK.getORGAPI().getUserAliasNames(projectManagerId);//转换成账户<姓名>格式
		List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processId).list();
		if (list != null && list.size() > 0) {
			BO bo = list.get(0);
			bo.set("PROJECTNAME", projectName);
			bo.set("QTXQBM", qtxqbmBM);
			bo.set("QTXQBMID", qtxqbmId);
			bo.set("PROJECTMANAGER", projectManagerId);
			SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo);
			json.put("projectManagerId", projectManagerId);
			json.put("qtxqbmId", qtxqbmId);
			json.put("success", "true");
			json.put("msg", "获取成功！");
			return json.toString();
		}
		json.put("projectManagerId", projectManagerId);
		json.put("qtxqbmId", qtxqbmId);
		json.put("success", "false");
		json.put("msg", "获取失败！");
		return json.toString();
	}
}
