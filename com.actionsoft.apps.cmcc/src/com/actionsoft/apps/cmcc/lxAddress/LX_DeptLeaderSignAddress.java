package com.actionsoft.apps.cmcc.lxAddress;
/**
 * 立项流程
 * 立项部门领导加签地址薄事件,逐级审批。立项、结项需求部门领导节点
 * @author nch
 * @date 20170622
 */
import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

public class LX_DeptLeaderSignAddress implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		// 当前登陆账户部门全路径ID
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); 
		String rootDeptId = userDeptPathId.split("/")[0];//登录人所在部门根路径
		String deptPathId = model.getPathIdOfCache();
		String choiceType = advanceModel.getChoiceType();
		if("single".equals(choiceType)){//委托办理
			//过滤部门，显示本部门
			if(userDeptPathId.contains(deptPathId)){
				return true;
			}
		}else{
			String userid = uc.getUID();
			if(userid.contains("@hq.cmcc")){
				//显示本部门及以下
				if(userDeptPathId.contains(deptPathId) || deptPathId.contains(userDeptPathId)){
					return true;
				}
			}else if(deptPathId.contains(rootDeptId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {

		int userLayer = uc.getDepartmentModel().getLayer();
		String deptid_model = model.getDepartmentId();//过滤部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		String choiceType = advanceModel.getChoiceType();
		if("single".equals(choiceType)){
			if(userLayer==layer){
				return true;
			}
		}else{
			String userid = uc.getUID();//当前登录人账号
			if(userid.contains("hq.cmcc")){
				if(layer==2){
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
