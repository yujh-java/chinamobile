package com.actionsoft.apps.cmcc.enterprise.address;

import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理
 * 复审流程--承担单位主管领导
 * 判断是否是承担单位内部处理返回的，如果是，查找历史参与者
 * @author chenxf
 *
 */
public class FS_UndertakeDeptMangerAddress
  implements AddressUIFilterInterface{
	//任务实例ID
	public String taskId;
	//节点ID
	public String activityId;
	//节点ID
	public String processId;
	
  public boolean addressUIFlexCompanyFilter(UserContext arg0, 
		  CompanyModel arg1, AdvancedAddressModel arg2)
  {
    return true;
  }
  
  public boolean addressUIFlexDepartmentFilter(UserContext uc,
		  DepartmentModel model, AdvancedAddressModel advancedAddressModel)
  {
	//当前人所在部门ID全路径
    String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();
    //当前人账号
    String userid = uc.getUID();
    //部门层级
    int layer = model.getLayer();
    //遍历部门ID全路径
    String deptPathId_model = model.getPathIdOfCache();
    
    if(UtilString.isEmpty(taskId)){
    	processId = advancedAddressModel.getInstanceId();
    	taskId = advancedAddressModel.getTaskId();
    	activityId = SDK.getTaskAPI().getTaskInstance(taskId).getActivityDefId();
    	//System.err.println("=====activityId:"+activityId+"========");
    	
    }
    //判断当前节点ID是否为--承担单位内部处理节点
    if(activityId.contains(CmccCommon.cddwnbcl_stepid)){
    	 /**
    	 * 获取承担单位企标管理员的历史审批人
    	 */
    	List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().
    										activityDefId(CmccCommon.cddwzgld_stepid).
    										processInstId(processId).
    										userTaskOfWorking().addQuery("TASKSTATE = ", 1).
    										orderByEndTime().asc().list();
    	if(list != null && list.size() > 0){
    		//获取历史参与者
    		HistoryTaskInstance histask = list.get(list.size() - 1);
    		String target = histask.getTarget();
    		//获取历史参与者部门ID
    		String deptid_zgld = UserContext.fromUID(target).getDepartmentModel().getId();
    		//获取历史参与者部门全路径ID
    		String pathDeptId_zgld = DepartmentCache.getModel(deptid_zgld).getPathIdOfCache();
    		if(pathDeptId_zgld.contains(deptPathId_model)){
    			return true;
    		}
    	}
    }else{
    	if (userid.contains("@hq.cmcc")) {
    		if(layer == 1){
    			if(userpathIdofCache.contains(deptPathId_model)){
    				return true;
    			}
    		}else{
    			if(layer == 2 && userpathIdofCache.contains(deptPathId_model)){
    				return true;
    			}
    		}
        }else{
        	//一级部门ID
    		String firstDeptid = userpathIdofCache.split("/")[0];
    		//遍历的一级部门ID
    		String firstDeptid_model = deptPathId_model.split("/")[0];
    		if(firstDeptid.equals(firstDeptid_model)){
    			return true;
    		}
        }
    }
    return false;
  }
  
  public boolean addressUIFlexUserFilter(UserContext uc, 
		  UserModel model, AdvancedAddressModel advancedAddressModel)
  {
	 //职位等级
    String zwmc = model.getExt1();
    //当前人员
    String userid = uc.getUID();
    
    if(UtilString.isEmpty(taskId)){
    	processId = advancedAddressModel.getInstanceId();
    	taskId = advancedAddressModel.getTaskId();
    	activityId = SDK.getTaskAPI().getTaskInstance(taskId).getActivityDefId();
    }
    //判断当前节点ID是否为--承担单位内部处理节点
    if(activityId.contains(CmccCommon.cddwnbcl_stepid)){
    	 /**
    	 * 获取承担单位企标管理员的历史审批人
    	 */
    	List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().
    										activityDefId(CmccCommon.cddwzgld_stepid).
    										processInstId(processId).
    										userTaskOfWorking().addQuery("TASKSTATE = ", 1).
    										orderByEndTime().asc().list();
    	if(list != null && list.size() > 0){
    		//获取历史参与者
    		HistoryTaskInstance histask = list.get(list.size() - 1);
    		String target = histask.getTarget();
    		if(!UtilString.isEmpty(target) 
    				&& target.equals(model.getUID())){
    			return true;
    		}
    	}
    }else{
    	if ((userid.contains("@hq.cmcc")) 
    			&& (!CmccCommon.user_leave2.equals(zwmc))) {
    		return false;
    	}
    	return true;
    }
    return false;
  }
}
