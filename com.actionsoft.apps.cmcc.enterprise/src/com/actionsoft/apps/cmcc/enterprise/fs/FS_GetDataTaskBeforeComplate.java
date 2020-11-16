package com.actionsoft.apps.cmcc.enterprise.fs;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListener;
import com.actionsoft.bpms.bpmn.engine.listener.InterruptListenerInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;

import net.sf.json.JSONObject;
/**
 * 企标复审流程
 * 任务结束前，调用第三方接口获取数据
 * @author chenxf
 *
 */
public class FS_GetDataTaskBeforeComplate extends InterruptListener
	implements InterruptListenerInterface{

	@Override
	public String getDescription() {
		return "任务结束前，调用第三方接口获取数据!";
	}
	@Override
	public boolean execute(ProcessExecutionContext param) throws Exception {

		String processId = param.getProcessInstance().getId();
	    
	    String title = param.getProcessInstance().getTitle();
	    
	    String submitterid = param.getProcessInstance().getCreateUser();
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date date = new Date();
	    	
	    //调取应用需求部门ID的接口URL
	    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "QBXQBMID");
	    //拼接
	    StringBuffer sb = new StringBuffer(url);
	    sb.append(processId);
//	    sb.append("&state=pending");
	    System.err.println("====企标管理URL流程审批接口："+sb.toString()+"===========");
	    //获取返回值
	    String json = CmccUrlUtil.get(sb.toString());
	    if (!"".equals(json))
	    {
	    	System.err.println("---返回需求部门ID的json："+json+"---------");
	    	
	      JSONObject returnJson = JSONObject.fromObject(json);
	      
	      String result = returnJson.getString("result");
	      
	      String checkResult = returnJson.getString("checkResult");
	      if ("1".equals(checkResult))
	      {
	        JSONObject resultJson = JSONObject.fromObject(result);
	        
	        String processType = (String)resultJson.get("processType");
	        //承担部门ID
	        String undertake = (String)resultJson.get("undertake");
	        //需求部门ID
	        String demand = (String)resultJson.get("demand");
	        
	        Connection con = DBSql.open();
	        try
	        {
	          String id = DBSql.getString("select id from BO_ACT_CMCC_PROCESSDATA where PROCESSID = ?", 
	            new Object[] { processId });
	          BO bo = new BO();
	          bo.set("PROCESSTYPE", processType);
	          bo.set("PROCESSID", processId);
	          bo.set("TITLE", title);
	          bo.set("CREAETUSERID", submitterid);
	          bo.set("PROSUBTIME", sdf.format(date));
	          bo.set("UNDERTAKE", CmccCommon.getDeptidByOuterid(undertake));
	          bo.set("DEMAND", CmccCommon.getDeptidByOuterid(demand));
	          if ((id == null) || ("".equals(id)))
	          {
	            SDK.getBOAPI().createDataBO("BO_ACT_CMCC_PROCESSDATA", bo, UserContext.fromUID("admin"), con);
	          }
	          else
	          {
	            bo.setId(id);
	            //更新数据
	            SDK.getBOAPI().update("BO_ACT_CMCC_PROCESSDATA", bo, con);
	          }
	        }
	        catch (Exception e)
	        {
	          e.printStackTrace(System.err);
	        }
	        finally
	        {
	          DBSql.close(con);
	        }
	      }
	    }
		return true;
	}
}
