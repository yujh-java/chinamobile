package com.actionsoft.apps.cmcc.lxAddress;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
/**
 * 研究院
 * 立项、结项等流程---项目管理人员地址博
 * @author chenxf
 *
 */
public class GetProductManagerAddress implements AddressUIFilterInterface{

	//获取项目管理人员的sql
	public String sql = "select glr from BO_ACT_PM_MAINTENANCEINFO_S";
	//存放项目管理人员的部门ID
	public List<String> list = new ArrayList<String>();
	//存放项目管理人员账号
	public List<String> list_user = new ArrayList<String>();
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel arg2) {
		
		if(list == null || list.size() == 0){
			List<RowMap> list_map = DBSql.getMaps(sql);
			for(RowMap map : list_map){
				//项目管理人员
				String userid = map.get("glr").toString();
				/*
				 * 获取当前人部门全路径，并拆分成数组
				 */
				String productDeptId = UserCache.getModel(userid).getDepartmentId();
				String productPathDeptId = DepartmentCache.getModel(productDeptId).getPathIdOfCache();
				String[] arys = productPathDeptId.split("/");
				for(int i = 0;i < arys.length;i++){
					list.add(arys[i]);
				}
			}
		}
		if(list != null && list.size() > 0){
			if(list.toString().contains(model.getId())){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel arg2) {
		/*
		 * 获取项目管理人员账号集合
		 */
		if(list_user == null || list_user.size() == 0){
			List<RowMap> list_map = DBSql.getMaps(sql);
			for(RowMap map : list_map){
				//项目管理人员
				String userid = map.get("glr").toString();
				list_user.add(userid);
			}
		}
		if(list_user != null && list_user.size() > 0){
			if(list_user.toString().contains(model.getUID())){
				return true;
			}
		}
		return false;
	}
}
