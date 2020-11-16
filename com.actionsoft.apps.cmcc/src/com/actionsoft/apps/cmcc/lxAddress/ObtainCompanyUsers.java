package com.actionsoft.apps.cmcc.lxAddress;
/**
 * @author nch
 * @date 2017-10-20
 * 立项、结项牵头研发机构接口人节点，获取本单位所有人员
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

public class ObtainCompanyUsers implements AddressUIFilterInterface {

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel arg2) {
		//当前登录人部门全路径
		String userPathId = uc.getDepartmentModel().getPathIdOfCache();
		String fistDeptid = uc.getDepartmentModel().getId();//一级部门ID，默认取当前登录人部门ID，避免获取部门编号时报错
		String secondDeptid = "";//二级部门ID
		String[] userDeptPathIdArr = null;
		if(!UtilString.isEmpty(userPathId)){
			userDeptPathIdArr = userPathId.split("/");
			fistDeptid = userDeptPathIdArr[0];
			if(userDeptPathIdArr.length >0){
				secondDeptid = userDeptPathIdArr[1];
			}
		}
		String firstDeptno = DepartmentCache.getModel(fistDeptid).getOuterId();
		//过滤部门全路径
		String deptPathId = model.getPathIdOfCache();
		//当前登录人部门属于集团，部门去2级下所有部门；非集团，取一级部门下所有部门
		if(firstDeptno.equals(CmccConst.deptNo_jt)){
			if(!UtilString.isEmpty(secondDeptid)){
				String secondPathId = DepartmentCache.getModel(secondDeptid).getPathIdOfCache();
				if(deptPathId.contains(secondPathId) || secondPathId.contains(deptPathId)){
					return true;
				}
			}
		}else{
			if(deptPathId.contains(fistDeptid)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel arg2) {
		return true;
	}


}
