package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;

/**
 * 参与者地址薄过滤事件，需求部门内部审核节点，显示所有人
 * @author wxx
 * @date 2017-08-08
 */
public class XqbmDeptUsersAddress implements AddressUIFilterInterface{
	
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String rootDeptId = userDeptPathId.split("/")[0];//登录人所在部门根路径
		String deptPathId = model.getPathIdOfCache();
		String userid = uc.getUID();
		if(userid.contains("@hq.cmcc")){
			//研究院下的去第二个部门ID
			String parentDeptId = userDeptPathId.split("/")[1];
			//获取部门全路径
			String pathParentDeptid = DepartmentCache.getModel(parentDeptId).getPathIdOfCache();
			//显示本部门及以下
			if(pathParentDeptid.contains(deptPathId) || deptPathId.contains(pathParentDeptid)){
				return true;
			}
		}else if(deptPathId.contains(rootDeptId)){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {
		
		/*String deptid_model = model.getDepartmentId();//过滤部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		String userid = uc.getUID();//当前登录人账号
		if(userid.contains("@hq.cmcc")){
			
			if(layer==1){//因为要显示需求单位下的所有部门下的所有人，所有部门层级限制为1，这样就能显示所有的人了，如果layer=2，二级部门的人就不显示了
				return false;
			}
			if(layer==2){//因为需求部门不显示领导，所以层级为2
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
