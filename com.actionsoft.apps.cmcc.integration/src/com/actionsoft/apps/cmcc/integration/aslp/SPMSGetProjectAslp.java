package com.actionsoft.apps.cmcc.integration.aslp;
/**
 * 获取项目信息接口ASLP，所有流程通用
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.apps.cmcc.integration.util.CmccUrlUtil;
import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class SPMSGetProjectAslp implements ASLP{

	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'流程信息'"}) 
	public ResponseObject call(Map<String, Object> params) {
		ResponseObject ro = ResponseObject.newOkResponse();
		String processID = (String) params.get("processID");
		String processType = (String) params.get("processType");
//		String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		/*
		 * chenxf modify
		 */
		String url = "";
		//判断是否为研究院，从而调用不同的接口参数
		if(processType.indexOf("cmri") != -1){
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK_CMRI");
		}else{
			url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "PROJECT_DATA_WRITEBACK");
		}
		url = url+"?type="+processType+"&process_id="+processID;
		System.err.println("====调取项目信息的url："+url+"======");
		String str = CmccUrlUtil.get(url);	
		System.err.println("====返回流程平台的数据str："+str+"======");
		Connection conn = null;
		try{
			conn = DBSql.open();
			JSONObject resultJson = JSONObject.fromObject(str);
			JSONObject datajson  = resultJson.getJSONObject("data");
			int code = (Integer)datajson.get("code");
			String msg = datajson.getString("msg");
			if(code == 1){
				//记录项目信息
				JSONObject json = JSONObject.fromObject(datajson.get("data"));
				String PROJECTNAME = json.getString("name");
				String PROJECTID = json.getString("id");//项目ID
				String PROJECTTYPE = json.getString("type");//项目类型
				String QTYFJGBM = json.getString("lead_research_ou");//牵头研发机构部门编码
				String QTYFJGBMID = "";//牵头研发机构部门ID
				if(!UtilString.isEmpty(QTYFJGBM)){
					QTYFJGBMID =  DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{QTYFJGBM});
				}
				if(UtilString.isEmpty(QTYFJGBM) || UtilString.isEmpty(QTYFJGBMID)){
					ro.msg("牵头研发机构部门为空或错误");
					ro.errorCode("2");
				}else{
					String PHYFJGBM = json.getString("involved_research_ous");//配合研发机构编码
					StringBuffer PHYFJGBMIDBuffer = new StringBuffer();//配合研发机构部门ID
					if(PHYFJGBM != null 
							&& !"null".equals(PHYFJGBM) 
							&& !UtilString.isEmpty(PHYFJGBM)){
						String[] PHYFJGBMArr =  PHYFJGBM.split(",");
						for(int i = 0;i<PHYFJGBMArr.length;i++){
							String PHYFJGBMNO = PHYFJGBMArr[i];
							String sql = "SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = '"+PHYFJGBMNO+"'";
							String PHYFJGDEPTID =  DBSql.getString(conn, sql);
							PHYFJGBMIDBuffer.append(PHYFJGDEPTID+",");
						}
					}
					String QTXQBM = json.getString("lead_req_ou");//牵头需求部门编码
					StringBuffer QTXQBMIDBuffer = new StringBuffer();//牵头需求部门ID
					if(QTXQBM != null 
							&& !"null".equals(QTXQBM) 
							&& !UtilString.isEmpty(QTXQBM)){
						String[] QTXQBMArr =  QTXQBM.split(",");
						for(int i = 0;i<QTXQBMArr.length;i++){
							String QTXQBMNO = QTXQBMArr[i];
							String QTXQBMID =  DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{QTXQBMNO});
							QTXQBMIDBuffer.append(QTXQBMID+",");
						}
					}
					String PHXQBM = json.getString("involved_req_ous");//配合需求部门编码
					StringBuffer PHXQBMIDBuffer = new StringBuffer();//配合需求部门ID
					if(PHXQBM != null 
							&& !"null".equals(PHXQBM) 
							&& !UtilString.isEmpty(PHXQBM)){
						String[] PHXQBMArr =  PHXQBM.split(",");
						for(int i = 0;i<PHXQBMArr.length;i++){
							String PHXQBMNO = PHXQBMArr[i];
							String PHXQBMID =  DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{PHXQBMNO});
							PHXQBMIDBuffer.append(PHXQBMID+",");
						}
					}
					BO bo = new BO();
					List<BO> list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processID).connection(conn).list();

					if(list != null && list.size() > 0){
						bo = list.get(0);
					}
					bo.set("PROCESSID", processID);
					bo.set("PROJECTNAME", PROJECTNAME);
					bo.set("PROJECTID", PROJECTID);
					bo.set("PROJECTTYPE", PROJECTTYPE);
					bo.set("QTYFJGBM", QTYFJGBM);
					bo.set("QTYFJGBMID", QTYFJGBMID);
					bo.set("PHYFJGBM", PHYFJGBM);
					if(!UtilString.isEmpty(PHYFJGBMIDBuffer.toString())){
						bo.set("PHYFJGBMID", PHYFJGBMIDBuffer.toString().subSequence(0, PHYFJGBMIDBuffer.length()-1));
					}else{
						bo.set("PHYFJGBMID", "");
					}
					bo.set("QTXQBM", QTXQBM);
					if(!UtilString.isEmpty(QTXQBMIDBuffer.toString())){
						bo.set("QTXQBMID", QTXQBMIDBuffer.toString().subSequence(0, QTXQBMIDBuffer.length()-1));
					}else{
						bo.set("QTXQBMID", "");
					}
					bo.set("PHXQBM", PHXQBM);
					if(!UtilString.isEmpty(PHXQBMIDBuffer.toString())){
						bo.set("PHXQBMID", PHXQBMIDBuffer.toString().subSequence(0, PHXQBMIDBuffer.length()-1));
					}else{
						bo.set("PHXQBMID", "");
					}

					//根据流程实例ID判断是否已存在
					if(list != null && list.size() > 0){
						SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo,conn);
					}else{
						SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"),conn);
					}
					ro.msg(msg);
					ro.errorCode(code+"");
				}
			}else{
				ro.msg(msg);
				ro.errorCode(code+"");
			}
		}catch(Exception e){
			ro.msg("项目信息接口获取失败,返回信息格式错误");
			ro.errorCode("0");
		}finally{
			DBSql.close(conn);
		}
		return ro;
	}
}
