package com.actionsoft.apps.cmcc.ressubAddress;

/**
 * 成果提交
 * 参与者地址薄过滤事件
 * 成果提交根据需求部门，查询领导(处长)
 * @author nch
 * @date 20170622
 */
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.apps.cmcc.resultSub.ResultConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.HistoryTaskInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class LeaderForXqbmAddress implements AddressUIFilterInterface {

	public String userid;
	public String userpathIdofCache;
	
	public String processid;
	public String taskid;
	public TaskInstance taskInstance;
	public String activityDefid;
	
	public List<HistoryTaskInstance> list;
	public TaskInstance taskInstance_last;
	public String targetDeptid;
	public String targetpathDeptid;
	
	public String target;
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		/*
		 * 当前人信息
		 */
		if(UtilString.isEmpty(userid)){
			//登录人账号
			userid = uc.getUID();
			//登录人部门ID全路径
			userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();
		}
		/*
		 * 流程信息
		 */
		if(UtilString.isEmpty(processid)){
			processid = advancedAddressModel.getInstanceId();
		}
		if(UtilString.isEmpty(taskid)){
			// 任务ID
			taskid = advancedAddressModel.getTaskId();
			taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			// 节点ID
			activityDefid = taskInstance.getActivityDefId();
		}
		
		// 项目经理回退处室领导
		if (activityDefid.equals(ResultConst.xqbmxmjl)) {
			if(list == null || list.size() <= 0){
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ResultConst.xqbmcsld)
						.userTaskOfWorking().addQuery("TASKSTATE = ", 1).addQuery("PROCESSINSTID = ", processid)
						.orderByEndTime().asc().list();
				if (list != null && list.size() > 0) {
					taskInstance_last = list.get(list.size() - 1);
					// 历史办理人部门ID
//					targetDeptid = taskInstance_last.getTargetDepartmentId();
					
					//chenxf modify 20181101
					//修改通过账号获取部门ID的方法
					String target = taskInstance_last.getTarget();
//					String targetDeptid = DBSql.getString("SELECT DEPARTMENTID FROM ORGUSER WHERE USERID = ?",
//							new Object[] { target });
					String targetDeptid = UserContext.fromUID(target).getDepartmentModel().getId();
					
					targetpathDeptid = DepartmentCache.getModel(targetDeptid).getPathIdOfCache();
				}
			}
		}
		
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(!UtilString.isEmpty(targetpathDeptid)){
			if(targetpathDeptid.contains(deptPathId_model)){
				return true;
			}
		}else{
			if(uc.getUID().contains("@hq.cmcc")){
				//当前人部门层级
				int layer = uc.getDepartmentModel().getLayer();
				if(layer == 1){
					if(userpathIdofCache.contains(deptPathId_model)){
						return true;
					}
				}else if(layer == 2){
					if(userpathIdofCache.contains(deptPathId_model) 
							|| deptPathId_model.contains(userpathIdofCache)){
						return true;
					}
				}else if(layer == 3){
					/*
					 * 获取当前人父部门ID、父部门全路径
					 */
					String parentDeptid = uc.getDepartmentModel().getParentDepartmentId();
					String pathParentDeptid = DepartmentCache.getModel(parentDeptid).getPathIdOfCache();
					if(pathParentDeptid.contains(deptPathId_model) 
							|| deptPathId_model.contains(pathParentDeptid)){
						return true;
					}
				}
			}else {
				String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
				if(deptPathId_model.contains(rootDeptId)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid_model = model.getUID();
		/*
		 * 流程信息
		 */
		if(UtilString.isEmpty(processid)){
			processid = advancedAddressModel.getInstanceId();
		}
		if(UtilString.isEmpty(taskid)){
			// 任务ID
			taskid = advancedAddressModel.getTaskId();
			taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
			// 节点ID
			activityDefid = taskInstance.getActivityDefId();
		}
		// 项目经理回退处室领导
		if (activityDefid.equals(ResultConst.xqbmxmjl)) {
			if(list == null || list.size() <= 0){
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ResultConst.xqbmcsld)
						.userTaskOfWorking().addQuery("TASKSTATE = ", 1).addQuery("PROCESSINSTID = ", processid)
						.orderByEndTime().asc().list();
				if (list != null && list.size() > 0) {
					TaskInstance taskInstance_last = list.get(list.size() - 1);
					target = taskInstance_last.getTarget();
				}
			}
		}
		
		if (UtilString.isEmpty(target)) {
			String userid = uc.getUID();
			String deptid_model = model.getDepartmentId();//部门ID
			String deptid = uc.getDepartmentModel().getId();//当前人部门ID
			int layer_model = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
			int layer = DepartmentCache.getModel(deptid).getLayer();//部门层级
			if(layer==1){
				return true;
			}else{
				if(layer_model==1){
					return false;
				}else if(userid.contains("@hq.cmcc")){
					if(layer_model==2){
						return false;
					}
				}
				return true;
			}
		}else{
			if (target.equals(userid_model)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
