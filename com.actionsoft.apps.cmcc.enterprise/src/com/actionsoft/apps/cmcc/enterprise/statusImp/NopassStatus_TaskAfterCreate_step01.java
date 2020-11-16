package com.actionsoft.apps.cmcc.enterprise.statusImp;

import com.actionsoft.apps.cmcc.enterprise.util.CmccUrlUtil;
import com.actionsoft.bpms.api.Utils;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListenerInterface;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.AppAPI;
import com.actionsoft.sdk.local.api.BOAPI;
import java.io.PrintStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JSONObject;
/**
 * 企标管理
 * 第一个节点任务创建后，返回退回起草状态
 * @author chenxf
 *
 */
public class NopassStatus_TaskAfterCreate_step01
  extends ExecuteListener
  implements ExecuteListenerInterface
{
  public String getDescription()
  {
    return "退回起草状态返回";
  }
  
  public void execute(ProcessExecutionContext param)
    throws Exception
  {
	//流程实例ID
	String processId = param.getProcessInstance().getId();
	//流程实例ID
	String taskid = param.getTaskInstance().getId();
	//调取应用需求部门ID的接口URL
    String url = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.enterprise", "RETURNSTATE");
    boolean flag = SDK.getTaskAPI().isChoiceActionMenu(taskid, "退回起草");
    if(flag){
    	//拼接
        StringBuffer sb = new StringBuffer(url);
        sb.append(processId);
        sb.append("&state=nopass");
        System.err.println("====企标管理URL流程结束接口："+sb.toString()+"===========");
    	
        //获取返回值
        String json = CmccUrlUtil.get(sb.toString());
    }
  }
}
