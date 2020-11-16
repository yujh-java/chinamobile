package com.actionsoft.apps.report.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.alibaba.fastjson.JSONObject;

public class ReportWeb extends ActionWeb{
	/**
	 * 
	 * @param 
	 * @return
	 */
	public String Kzmanage(UserContext sid, String processId,String processDefid){
		//String uid = getContext().getUID();
		JSONObject result = new JSONObject();
        //String sql = "SELECT ID FROM WFH_FORMSNAPSHOT WHERE PROCESSINSTID = ? ORDER BY RECORDTIME DESC";
        List<RowMap> listRowMap =DBSql.getMaps("SELECT ID FROM WFH_FORMSNAPSHOT WHERE PROCESSINSTID = ? ORDER BY RECORDTIME DESC", new Object[]{processId});
		//List<RowMap> listRowMap = DBSql.getMaps(conn, sql, new Object[]{processId});
		if(listRowMap != null && listRowMap.size() > 0){
			String version_id = listRowMap.get(0).getString("ID");
			System.err.println("表单快照cmd"+version_id);
			//result.put("version_id", version_id);
			//String url="http://keyan.test.cmri.cn/workflow/r/w?sid=790ce39b-d233-4e2e-9b8a-2a2c8d2584c1&cmd=com.actionsoft.apps.report.photoshow&processInstId=25bb4205-c685-4a28-9f41-6a40172a2188&processDefid=obj_d1d45ba44ad145b68b4d9dd842277ee3";
			String url="http://keyan.test.cmri.cn/workflow/r/or?cmd=CLIENT_BPM_FORM_PAGE_SNAPSHOT_OPEN&oauthName=chinamobile&processInstId="+processId+ "&versionId="+version_id+ "&openState=2";
			result.put("url", url);
			result.put("success", true);
			result.put("msg", "请求成功");
	        return result.toString();
		}
		result.put("url", "");
		return result.toString();
		 
		
        
        //return HtmlPageTemplate.merge("com.actionsoft.apps.report", "photo.html", result);
		
		
	}
	
	/**
	 * @author wuxx
	 * @version 创建时间：2019年1月29日
	 * 展示企标快照
	 */
	public String enterprice(UserContext sid, String processId,String processDefid){
		
		JSONObject result = new JSONObject();
        //String sql = "SELECT ID FROM WFH_FORMSNAPSHOT WHERE PROCESSINSTID = ? ORDER BY RECORDTIME DESC";
        List<RowMap> listRowMap =DBSql.getMaps("SELECT ID FROM WFH_FORMSNAPSHOT WHERE PROCESSINSTID = ? ORDER BY RECORDTIME DESC", new Object[]{processId});
		
		if(listRowMap != null && listRowMap.size() > 0){
			String version_id = listRowMap.get(0).getString("ID");
			System.err.println("表单快照cmd"+version_id);
			//String url="http://keyan.test.cmri.cn/workflow/r/w?sid=790ce39b-d233-4e2e-9b8a-2a2c8d2584c1&cmd=com.actionsoft.apps.report.photoshow&processInstId=25bb4205-c685-4a28-9f41-6a40172a2188&processDefid=obj_d1d45ba44ad145b68b4d9dd842277ee3";
			String url="http://keyan.test.cmri.cn/workflow/r/or?cmd=CLIENT_BPM_FORM_PAGE_SNAPSHOT_OPEN&oauthName=chinamobile&processInstId="+processId+ "&versionId="+version_id+ "&openState=2";
			result.put("url", url);
			result.put("success", true);
			result.put("msg", "请求成功");
	        return result.toString();
		}
		result.put("url", "");
		return result.toString();
		 
		
		
	}

}
