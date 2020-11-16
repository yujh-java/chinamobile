package com.actionsoft.apps.cmcc.integration.aslp;
/**
 * 虚拟收入获取需求部门接口aslp，调用spms接口。所有流程通用
 * @author zhaoxs
 * @date 2017-09-13
 */
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.sdk.local.SDK;

public class SPMSVirtualIncomeAslp implements ASLP{

	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'保存接口信息'" })
	public ResponseObject call(Map<String, Object> param) {
		String xqbmDeptId = "";//需求部门ID
		String xqbm = "";
		String yfjg = "";
		String yfjgDept="";//研发机构部门ID
		ResponseObject res = ResponseObject.newOkResponse();
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		String process_type = (String) param.get("processtype");
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
		String process_id = (String) param.get("processid");
		url = url + "?type=" + process_type + "&process_id=" + process_id;// 需求部门接口地址
		String str = CmccUrlUtil.get(url);
		SDK.getLogAPI().getLogger(this.getClass()).error("str="+str);
		Connection conn = null;
		try{
			conn = DBSql.open();
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson = resultJson.getJSONObject("data");
			int code = (Integer) datajson.get("code");
			String alldata = datajson.getString("data");
			JSONObject xqbmdata = JSONObject.fromObject(alldata);
			if (code == 1) {
				   xqbm = xqbmdata.getString("req_ou");
				   xqbmDeptId = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[] { xqbm });
					yfjg = xqbmdata.getString("research_ou");
					yfjgDept = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",
							new Object[] { yfjg });
			} else {
				String msg = datajson.getString("msg");
				res.put("msg", msg);
				SDK.getLogAPI().getLogger(this.getClass()).error("调用虚拟收入需求部门信息获取接口失败,流程实例ID:"+process_id);
				SDK.getLogAPI().getLogger(this.getClass()).error("errormsg:"+msg);
			}
			BO bo = new BO();
			List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", process_id).connection(conn).list();
			if (list != null && list.size() > 0) {
				bo = list.get(0);
			}
			bo.set("PROCESSID", process_id);
			bo.set("QTXQBMID", xqbmDeptId);// 需求部门id
			bo.set("QTXQBM", xqbm);// 需求部门
			bo.set("QTYFJGBM", yfjg);// 研发机构
			bo.set("QTYFJGBMID", yfjgDept);// 研发机构id
			bo.set("PROCESSTYPE", process_type);
			if (list != null && list.size() > 0) {
				SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo,conn);
			} else {
				SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"),conn);
			}
			res.errorCode(code + "");
		}catch(Exception e){
			res.errorCode("0");
			res.msg("获取信息失败,返回信息格式错误");
			SDK.getLogAPI().getLogger(this.getClass()).error("调用虚拟收入需求部门信息获取接口失败,流程实例ID:"+process_id);
		}finally{
			DBSql.close(conn);
		}
		return res;
	}
}
