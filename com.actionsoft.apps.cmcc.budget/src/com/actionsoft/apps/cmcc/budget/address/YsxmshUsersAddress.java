package com.actionsoft.apps.cmcc.budget.address;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.budget.CmccConst;
import com.actionsoft.apps.cmcc.budget.util.CmccCommon;
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
/**
 * 预算流程项目审核节点地址簿事件
 * 显示研究院下的所有部所领导和项目总监的角色
 * 部所领导的层级为3，只显示3的领导
 * @author wxx
 *
 */
public class YsxmshUsersAddress implements AddressUIFilterInterface{
	private String leaderDeptId = "";//处长所在部门ID
	private List<String> list_userPathids  = new ArrayList<String>();//部长所在部门全路径 
	private List<String> list_userids  = new ArrayList<String>();//部长角色所有人员账号
	/*
	 * 在企标角色维护表中：BO_ACT_ENTERPRISE_ROLEDATA
	 * 查询项目总监
	 */
	public String sql_demandPeople = "";
	//判断是否是项目总监
	public String count;
	//项目总监所在部门全路径集合
	public List<String> userid_list = new ArrayList<String>();

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
        String deptPathId_model1 = DepartmentCache.getModel(CmccCommon.deptid_yjy).getPathIdOfCache();
        
		
		if(deptPathId_model1.contains(deptPathId_model) || deptPathId_model.contains(deptPathId_model1)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String deptPathId_model1 = DepartmentCache.getModel(CmccCommon.deptid_yjy).getPathIdOfCache();
		
		//查询项目总监角色
        if(UtilString.isEmpty(sql_demandPeople)){
			sql_demandPeople = "SELECT GROUP_CONCAT(userid) userid "
					  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
					  		+ "WHERE ROLENAME = '项目总监' AND locate(deptid, '"+deptPathId_model1+"' ) > 0";
			String userids = DBSql.getString(sql_demandPeople);
			if(!UtilString.isEmpty(userids)){
				//获取多个人
				String[] peoples = userids.split(",");
				for(int i = 0;i<peoples.length;i++){
					//人员账号
					String people = peoples[i];
					//人员集合
					userid_list.add(people);
				}
			}
		}
        String userid = uc.getUID();//用户账号
        String kzzd=model.getExt1();
        if(userid_list.size() > 0 
				&& userid_list.toString().contains(model.getUID()) && CmccConst.user_leave3.equals("3") || "3".equals(kzzd)){
			return true;
		}
		return false;
		
		
		
			/*if(UtilString.isEmpty(count)){
				count = DBSql.getString(roleName_sql, new Object[]{CmccCommon.xmsz_role, userid});
			}
			//判断当前人是否为企标管理员
			if(!UtilString.isEmpty(count) && Integer.parseInt(count) > 0){
			}else{
				if(userid.contains("@hq.cmcc")){
					//人员层级
					if(UtilString.isEmpty(lay)){
						lay = uc.getUserModel().getExt1();
					}
					//部领导可以分发所有人
					if(!lay.equals(CmccCommon.user_leave2)){
						String ext1 = model.getExt1();
						if(CmccCommon.user_leave2.contains(ext1)){
							return false;
						}
					}
				}
			}*/
		
		/*String userid = uc.getUID();//用户账号
		String kzzd=model.getExt1();
		System.err.println("kzzd"+kzzd);
		if(!CmccConst.user_leave3.equals("3") || !"3".equals(kzzd)){
			return false;
		}

		return true;*/
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
