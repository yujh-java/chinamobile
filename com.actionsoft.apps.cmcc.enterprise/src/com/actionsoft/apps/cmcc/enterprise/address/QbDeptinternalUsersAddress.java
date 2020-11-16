package com.actionsoft.apps.cmcc.enterprise.address;
/**
 * 企标流程
 * 企标流程企标归口人员审核送归口部门内部处理的地址簿事件
 * 范围是归口部门内所有人
 * @author wxx
 * @date 20190326
 */
import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

public class QbDeptinternalUsersAddress implements AddressUIFilterInterface{

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
			
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
        
		
	
		if(userpathIdofCache.contains(deptPathId_model)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		/*	String userid = uc.getUID();
		String zwmc = model.getExt1();
		if(CmccConst.user_leave3.equals(zwmc) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave4.equals(zwmc))){
			return true;
		}
		return false;*/
		String userid = uc.getUID();//用户账号
		String kzzd=model.getExt1();
		
		if(CmccCommon.user_leave3.equals("0") || "0".equals(kzzd)){
			return false;
		}
		/*String deptid_model = model.getDepartmentId();//部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		if(layer==1){
			return false;
		}else if(userid.contains("hq.cmcc")){
			if(layer==2){
				return false;
			}
		}*/
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
