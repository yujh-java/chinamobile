package com.actionsoft.apps.cmcc.address;
/**
 * 参与者地址薄过滤事件，领导(部长、副部长)所有部门中.立项、结项相关部门落实节点
 * @author nch
 * @date 2017-06-22
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;

public class AllDeptLeaderUsersAddress implements AddressUIFilterInterface {
	private List<String> list_userPathids  = new ArrayList<String>();//部长所在部门全路径 
	private List<String> list_userids  = new ArrayList<String>();//部长角色所有人员账号
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		/*		if(list_userPathids == null || list_userPathids.size() == 0){
			//查询职位为1或2（部门负责人，hq.cmcc取2，非hq.cmcc取1和2），未注销的人员信息
			List<RowMap> listRowMap = DBSql.getMaps("SELECT * FROM ORGUSER WHERE EXT1 = ? OR EXT1 = ? AND CLOSED = ?", new Object[]{CmccConst.user_leave1,CmccConst.user_leave2,0});
			if(listRowMap != null && listRowMap.size() > 0){
				for(int i =0;i<listRowMap.size();i++){
					String userid = listRowMap.get(i).getString("USERID");
					String ext1 = listRowMap.get(i).getString("EXT1");//职位
					String deptid = listRowMap.get(i).getString("DEPARTMENTID");
					if(!userid.contains("@hq.cmcc")){
						//部门全路径
						String deptPathid = DepartmentCache.getModel(deptid).getPathIdOfCache();
						list_userPathids.add(deptPathid);
						list_userids.add(userid);
					}else if(CmccConst.user_leave2.equals(ext1)){
						String deptPathid = DepartmentCache.getModel(deptid).getPathIdOfCache();
						list_userPathids.add(deptPathid);
						list_userids.add(userid);
					}
				}
			}
		}
		String deptPathidModel = model.getPathIdOfCache();//过滤部门全路径
		if(list_userPathids.toString().contains(deptPathidModel)){
			return true;
		}
		return false;*/
		return true;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
/*		String zwmc = model.getExt1();//用户职位名称
		String userid = model.getUID();//用户账号
		if(CmccConst.user_leave2.equals(zwmc)){
			return true;
		}else if(!userid.contains("@hq.cmcc") && CmccConst.user_leave1.equals(zwmc)){
			return true;
		}
		return false;*/
		String zwmc = model.getExt1();
		String userid = model.getUID();
		if(userid.contains("@hq.cmcc") && !CmccConst.user_leave2.equals(zwmc)){
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
