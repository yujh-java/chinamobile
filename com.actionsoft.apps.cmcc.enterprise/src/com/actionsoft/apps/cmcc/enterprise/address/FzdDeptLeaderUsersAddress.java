package com.actionsoft.apps.cmcc.enterprise.address;

import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
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
 * 参与者地址薄过滤事件，根据当前登录人找该部门的2级领导
 * @author wxx
 * @date 2017-07-17
 */
public class FzdDeptLeaderUsersAddress implements AddressUIFilterInterface{

	//任务实例ID
	public String taskId;
	//节点ID
	public String activityId;
	//节点ID
	public String processId;
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
		
		 if(UtilString.isEmpty(taskId)){
	    	processId = advancedAddressModel.getInstanceId();
	    	taskId = advancedAddressModel.getTaskId();
	    	activityId = SDK.getTaskAPI().getTaskInstance(taskId).getActivityDefId();
	    	//System.err.println("=====activityId:"+activityId+"=======");
	    	
	    }
		String userid = uc.getUID();//登录人账号
		int layer = model.getLayer();//部门层级
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		
		//判断当前节点ID是否为--需求部门内部处理节点
	    if(activityId.contains(CmccCommon.sub_xqbmnbjkr)){
	    	 /**
	    	 * 获取需求部门企标管理员的历史审批人
	    	 */
	    	List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().
	    										activityDefId(CmccCommon.sub_xqbmldjkr).
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
	    	if(userid.contains("@hq.cmcc")){
				if(layer == 1 || layer == 2){
					if(userpathIdofCache.contains(deptPathId_model)){
						return true;
					}
				}
			}else if(deptPathId_model.contains(rootDeptId)){
				return true;
			}
	    }
		return false;
	}


	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String zwmc = model.getExt1();
		String userid = model.getUID();
		
		if(UtilString.isEmpty(taskId)){
	    	processId = advancedAddressModel.getInstanceId();
	    	taskId = advancedAddressModel.getTaskId();
	    	activityId = SDK.getTaskAPI().getTaskInstance(taskId).getActivityDefId();
	    }
	    //判断当前节点ID是否为--承担单位内部处理节点
	    if(activityId.contains(CmccCommon.sub_xqbmnbjkr)){
	    	 /**
	    	 * 获取承担单位企标管理员的历史审批人
	    	 */
	    	List<HistoryTaskInstance> list = SDK.getHistoryTaskQueryAPI().
	    										activityDefId(CmccCommon.sub_xqbmldjkr).
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
	    	if(userid.contains("@hq.cmcc") && !CmccConst.user_leave2.equals(zwmc)){
	    		return false;
	    	}
	    	return true;
	    }
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
