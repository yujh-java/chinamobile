package com.actionsoft.apps.cmcc.address;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.sdk.local.SDK;

/** 
* @author yujh: 
* @version 创建时间：2020年4月28日 上午10:19:53 
* 立项，结项2.0获取接口人会签节点参与者(加签获取所有处长，再加签获取所有人)
*/
public class GetXQBMUserAddress implements AddressUIFilterInterface{

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model, AdvancedAddressModel advanceModel) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		
		String currentPathDeptId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String pathDeptId = model.getPathIdOfCache();
		String choiceType = advanceModel.getChoiceType();
		//委托办理
		if("single".equals(choiceType)){
			//过滤部门，显示本部门
			if(currentPathDeptId.contains(pathDeptId)){
				return true;
			}
		}else{
			if(uc.getUID().contains("@hq.cmcc")){
				//当前人部门层级
				int layer = uc.getDepartmentModel().getLayer();
				if(layer == 1){
					if(currentPathDeptId.contains(pathDeptId)){
						return true;
					}
				}else if(layer == 2){
					if(currentPathDeptId.contains(pathDeptId) 
							|| pathDeptId.contains(currentPathDeptId)){
						return true;
					}
				}else if(layer == 3){
					/*
					 * 获取当前人父部门ID、父部门全路径
					 */
					String parentDeptid = uc.getDepartmentModel().getParentDepartmentId();
					String pathParentDeptid = DepartmentCache.getModel(parentDeptid).getPathIdOfCache();
					if(pathParentDeptid.contains(pathDeptId) 
							|| pathDeptId.contains(pathParentDeptid)){
						return true;
					}
				}
			}else{
				//跟部门ID
				String rootDeptid = currentPathDeptId.split("/")[0];
				if(pathDeptId.contains(rootDeptid)){
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel advanceModel) {
		
		//当前人部门层级
		int userLayer = uc.getDepartmentModel().getLayer();
		//过滤部门ID
		String deptid_model = model.getDepartmentId();
		//部门层级
		int layer = DepartmentCache.getModel(deptid_model).getLayer();
		//当前提交人userId
		String userId = uc.getUID();
		String choiceType = advanceModel.getChoiceType();
		TaskInstance taskInstance = SDK.getTaskAPI().getTaskInstance(advanceModel.getTaskId());
		int state = taskInstance.getState();
		//委托办理
		if("single".equals(choiceType)){
			if(userLayer==layer){
				return true;
			}
		}else if(state==1){//接口人加签到处长环节
			String zwmc_model = model.getExt1();
			if(!userId.contains("@hq.cmcc")){
				return true;
			}else if(userId.contains("@hq.cmcc") && CmccConst.user_leave3.equals(zwmc_model)){ //所有部所领导
				return true;
			}
		}else if(state==11){//处长加签到项目经理
			return true;
		}
		return false;
	}
	
}
