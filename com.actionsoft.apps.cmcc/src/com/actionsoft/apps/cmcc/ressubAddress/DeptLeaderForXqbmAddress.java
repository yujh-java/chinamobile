package com.actionsoft.apps.cmcc.ressubAddress;

/**
 * 成果提交
 * 参与者地址薄过滤事件
 * 根据需求部门，查询领导(部长)，成果提交需求部门领导节点
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
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

public class DeptLeaderForXqbmAddress implements AddressUIFilterInterface {
	private String deptid = "";
	private String activityDefid = "";
	private List<HistoryTaskInstance> list = new ArrayList<HistoryTaskInstance>();
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		String processid = advancedAddressModel.getInstanceId();
		String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
		//过滤历史任务
		if(list == null || list.size() == 0){
			if(UtilString.isEmpty(activityDefid)){
				String taskid = advancedAddressModel.getTaskId();// 任务ID
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				activityDefid = taskInstance.getActivityDefId();// 节点ID
			}
			if(ResultConst.xqbmcsld.equals(activityDefid)){
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ResultConst.xqbmfzr)
						.userTaskOfWorking().addQuery("TASKSTATE = ", 1).addQuery("PROCESSINSTID = ", processid)
						.orderByEndTime().asc().list();
			}
		}
		String targetDeptPathids = "";
		if(list != null && list.size() >0){
			TaskInstance taskInstance_last = list.get(list.size() - 1);
//			String targetDeptid = taskInstance_last.getTargetDepartmentId();
			
			//chenxf modify 20181101
			//修改通过账号获取部门ID的方法
			String target = taskInstance_last.getTarget();
//			String targetDeptid = DBSql.getString("SELECT DEPARTMENTID FROM ORGUSER WHERE USERID = ?",
//					new Object[] { target });
			
			String targetDeptid = UserContext.fromUID(target).getDepartmentModel().getId();
			
			targetDeptPathids = DepartmentCache.getModel(targetDeptid).getPathIdOfCache();
		}
		if(UtilString.isEmpty(targetDeptPathids)){
			if (UtilString.isEmpty(deptid)) {
				deptid = DBSql.getString("SELECT SUBRESULTXQBM FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID = ?",
						new Object[] { processid });
			}
			String userpathIdofCache = "";
			if (!UtilString.isEmpty(deptid)) {
				DepartmentModel deptModel = DepartmentCache.getModel(deptid);
				userpathIdofCache = deptModel.getPathIdOfCache();
			}
			String userid = uc.getUID();
			int layer = model.getLayer();
			if(userid.contains("hq.cmcc")){
				if(userpathIdofCache.contains(deptpathIdofCache) && (layer==1 || layer==2)){
					return true;
				}
			}else if(userpathIdofCache.contains(deptpathIdofCache) || deptpathIdofCache.contains(userpathIdofCache)){
				return true;
			}
		}else{
			if(targetDeptPathids.contains(deptpathIdofCache)){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		String userid = model.getUID();
		if(list == null || list.size() == 0){
			if(UtilString.isEmpty(activityDefid)){
				String taskid = advancedAddressModel.getTaskId();// 任务ID
				TaskInstance taskInstance = SDK.getTaskAPI().getInstanceById(taskid);
				activityDefid = taskInstance.getActivityDefId();// 节点ID
			}
			if(ResultConst.xqbmcsld.equals(activityDefid)){
				String processid = advancedAddressModel.getInstanceId();
				// 查找处室领导历史办理人
				list = SDK.getHistoryTaskQueryAPI().activityDefId(ResultConst.xqbmfzr)
						.userTaskOfWorking().addQuery("TASKSTATE = ", 1).addQuery("PROCESSINSTID = ", processid)
						.orderByEndTime().asc().list();
			}
		}
		String target = "";// 历史办理人账号
		if (list != null && list.size() > 0) {
			TaskInstance taskInstance_last = list.get(list.size() - 1);
			target = taskInstance_last.getTarget();
		}
		/*if (UtilString.isEmpty(target)) {
			String zwmc = model.getExt1();
			if (CmccConst.user_leave2.equals(zwmc) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave1.equals(zwmc))) {
				return true;
			}
		} else {
			if (target.equals(userid)) {
				return true;
			}
		}*/
		if (UtilString.isEmpty(target)) {
			String zwmc = model.getExt1();
			if(userid.contains("@hq.cmcc") && !CmccConst.user_leave2.equals(zwmc)){
				return false;
			}else{
				return true;
			}
		}else{
			if (target.equals(userid)) {
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
