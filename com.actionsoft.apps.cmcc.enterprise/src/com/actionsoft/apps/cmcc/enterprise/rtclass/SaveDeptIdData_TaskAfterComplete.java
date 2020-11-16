package com.actionsoft.apps.cmcc.enterprise.rtclass;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JSONObject;
/**
 * 企标管理
 * 第一个节点提交时事件
 * 存储流程信息状态
 * @author chenxf
 *
 */
public class SaveDeptIdData_TaskAfterComplete
  extends ExecuteListener
  implements ExecuteListenerInterface
{
  public String getDescription()
  {
    return "任务完成后，调用对方接口获取数据";
  }
  
  public void execute(ProcessExecutionContext param)
    throws Exception
  {
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
//    sb.append("&state=pending");
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
          String id = DBSql.getString("select id from BO_ACT_CMCC_PROCESSDATA where PROCESSTYPE = ? and PROCESSID = ?", 
            new Object[] { processType, processId });
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
  }
}
