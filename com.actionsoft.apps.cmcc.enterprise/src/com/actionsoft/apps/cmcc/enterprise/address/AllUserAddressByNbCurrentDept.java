package com.actionsoft.apps.cmcc.enterprise.address;

import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

/**
 * 企标管理--查询子流程中部门内部处理节点
 * 查询部门所有人
 * @author wuxx
 *
 */
public class AllUserAddressByNbCurrentDept implements AddressUIFilterInterface{
	public String currentDeptid;//当前人部门ID
	public String pathCurrentDeptid;//当前人部门ID全路径
	public String userid;//当前人账号

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	
	/*@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel advancedAddress) {
		//当前人部门全路径
		String currentPathDept = uc.getDepartmentModel().getPathIdOfCache();
		//遍历的部门全路径
		String pathDept = model.getPathIdOfCache();
		
		if(currentPathDept.contains(pathDept)){
			return true;
		}else{
			if(pathDept.contains(currentPathDept)){
				return true;
			}
		}
		return false;
	}*/
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
		String rootDeptId = userDeptPathId.split("/")[0];//登录人所在部门根路径
		String deptPathId = model.getPathIdOfCache();
		
		String userid = uc.getUID();
		if(userid.contains("hq.cmcc")){
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

	/**
	 * 过滤人员
	 */
	
	/*@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel arg2) {
		//当前人部门层级
		int currentLayer = uc.getDepartmentModel().getLayer();
		//遍历人员的部门层级
		int layer = DepartmentCache.getModel(model.getDepartmentId()).getLayer();
		if(currentLayer < layer){
			System.err.println("false++=");
			return false;
		}
		return true;
	}*/
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {
		String deptid_model = model.getDepartmentId();//过滤部门ID
		int layer = DepartmentCache.getModel(deptid_model).getLayer();//部门层级
		String userid = uc.getUID();//当前登录人账号
		if(layer==0 || layer==1){
			return false;
		}
		
		return true;
	}
	

}
