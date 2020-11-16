package com.actionsoft.apps.budget.address;
/**
 * 参与者地址薄过滤事件，根据当前登录人找部长、副部长。所有流程
 * @author nch
 * @date 2017-06-22
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.actionsoft.apps.budget.BudgetConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.UtilString;

public class DeptLeaderUsersAddress implements AddressUIFilterInterface {
	private List<String> list_subDeptids = new ArrayList<String>();//子部门ID集合
	private List<String> list_truePathDeptids = new ArrayList<String>();//正确返回部门ID集合
	private String DeptLeadererPathDeptId = "";//逐级上找，部长所在位置

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		/*String userid = uc.getUID();
		String deptPathIdMoedl = model.getPathIdOfCache();
		String deptIdMoedl = model.getId();
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
		if(list_truePathDeptids==null || list_truePathDeptids.size() ==0){
			if(list_subDeptids == null || list_subDeptids.size() ==0){
				//根路径下所有子部门
				List<DepartmentModel> list_subDeptModels = DepartmentCache.getSubDepartments(rootDeptId);
				if(list_subDeptModels != null && list_subDeptModels.size() > 0){
					for(int i = 0;i < list_subDeptModels.size();i++){
						String subDeptid = list_subDeptModels.get(i).getId();
						list_subDeptids.add(subDeptid);
					}
				}
			}
			//list_truePathDeptids：子部门中含有部门负责人（hq.cmcc:2;非hq.cmcc:1或2）的部门全路径
			if(list_subDeptids != null && list_subDeptids.size() > 0){
				for(int k =0;k<list_subDeptids.size();k++){
					String subDeptId = list_subDeptids.get(k);
					DepartmentModel departmentModel = DepartmentCache.getModel(subDeptId);
					if(departmentModel != null){
						String subPathIdDept = departmentModel.getPathIdOfCache();
						if(subPathIdDept.contains(rootDeptId)){
							Iterator<UserModel> subUserModels = UserCache.getUserListOfDepartment(subDeptId);
							//部门下人员有部长的，显示
							while(subUserModels.hasNext()){
								UserModel userModel = subUserModels.next();
								String zwmc = userModel.getExt1();
								if(CmccConst.user_leave2.equals(zwmc) || (!userid.contains("@hq.cmcc") && CmccConst.user_leave1.equals(zwmc))){
									String trueDeptId = userModel.getDepartmentId();
									DepartmentModel trueDepartMentModel = DepartmentCache.getModel(trueDeptId);
									//DeptLeadererPathDeptId:登录人所在部门逐级向上，存在部门负责人部门全路径
									list_truePathDeptids.add(trueDepartMentModel.getPathIdOfCache());
									if(userpathIdofCache.contains(trueDepartMentModel.getPathIdOfCache())){
										DeptLeadererPathDeptId = trueDepartMentModel.getPathIdOfCache();
									}
								}
							}
						}
					}
				}
			}
		}
		//显示根部门
		if(rootDeptId.equals(deptIdMoedl)){
			return true;
		}
		//登录人所在部门逐级向上存在部长，显示DeptLeadererPathDeptId；否则，显示所有部长所在部门
		if(!UtilString.isEmpty(DeptLeadererPathDeptId)){
			if(DeptLeadererPathDeptId.contains(deptPathIdMoedl)){
				return true;
			}
		}else if(list_truePathDeptids != null && list_truePathDeptids.size() > 0){
			if(list_truePathDeptids.toString().contains(deptPathIdMoedl)){
				return true;
			}
		}
		return false;*/
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String rootDeptId = userpathIdofCache.split("/")[0];//登录人所在部门根路径
		
		String userid = uc.getUID();//登录人账号
		int layer = model.getLayer();//部门层级
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
		if(userid.contains("hq.cmcc")){
			if(layer == 1 || layer == 2){
				if(userpathIdofCache.contains(deptPathId_model)){
					return true;
				}
			}
		}else if(deptPathId_model.contains(rootDeptId)){
			return true;
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
	/*	String zwmc = model.getExt1();
		String userid = model.getUID();
		if(CmccConst.user_leave2.equals(zwmc)){
			return true;
		}else if(!userid.contains("@hq.cmcc") && CmccConst.user_leave1.equals(zwmc)){
			return true;
		}
		return false;*/
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if(userid.contains("@hq.cmcc") && !BudgetConst.user_leave2.equals(zwmc)){
			return false;
		}
		return true;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
