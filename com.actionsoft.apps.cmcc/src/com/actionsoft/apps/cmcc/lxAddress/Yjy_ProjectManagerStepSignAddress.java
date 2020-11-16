package com.actionsoft.apps.cmcc.lxAddress;

import com.actionsoft.apps.cmcc.CmccConst;
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
 * 研究院立项流程
 * 适用所有研究院流程
 * 项目管理部门领导节点---加签、传阅公用
 * @author chenxf
 *
 */
public class Yjy_ProjectManagerStepSignAddress implements AddressUIFilterInterface{

	public String taskid;
	public boolean flag;
	public boolean flag2;
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
			flag = SDK.getTaskAPI().isChoiceActionMenu(taskid, "送相关部所会签");
			flag2 = SDK.getTaskAPI().isChoiceActionMenu(taskid, "送相关部所内部会签");
		}
		String pathDeptId = model.getPathIdOfCache();
		/*
		 * 判断是否加签
		 */
		if(flag){//一级加签
			/*
			 * 查出所有部所领导
			 */
			String parentDeptId = uc.getDepartmentModel().getParentDepartmentId();
			String parentPathDeptId = DepartmentCache.getModel(parentDeptId).getPathIdOfCache();
			if(parentPathDeptId.contains(pathDeptId)
					|| pathDeptId.contains(parentPathDeptId)){
				return true;
			}
		}else if(flag2){//二级加签
			//当前人所在部所
			String currentPathDeptid = uc.getDepartmentModel().getPathIdOfCache();
			if(currentPathDeptid.contains(pathDeptId)){
				return true;
			}
		}else{
			/*
			 * 传阅给院主管领导及处领导
			 */
			int layer = uc.getDepartmentModel().getLayer();
			String parentDeptId = "";
			if(layer == 2){
				parentDeptId = uc.getDepartmentModel().getId();
			}else{
				parentDeptId = uc.getDepartmentModel().getParentDepartmentId();
			}
			String parentPathDeptId = DepartmentCache.getModel(parentDeptId).getPathIdOfCache();
			if(parentPathDeptId.contains(pathDeptId)
					|| pathDeptId.contains(parentPathDeptId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
				UserModel model, AdvancedAddressModel advancedAddressModel) {
		
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
			flag = SDK.getTaskAPI().isChoiceActionMenu(taskid, "送相关部所会签");
			flag2 = SDK.getTaskAPI().isChoiceActionMenu(taskid, "送相关部所内部会签");
		}
		String zwdj = model.getExt1();
		/*
		 * 判断是否加签
		 */
		if(flag){
			if(CmccConst.user_leave3.equals(zwdj)){
				return true;
			}
		}else if(flag2){
			if(!CmccConst.user_leave2.equals(zwdj)){
				return true;
			}
		}else{
			if(CmccConst.user_leave3.equals(zwdj)){
				return true;
			}else if(CmccConst.user_leave2.equals(zwdj)){
				return true;
			}
		}
		
		return false;
	}

}
