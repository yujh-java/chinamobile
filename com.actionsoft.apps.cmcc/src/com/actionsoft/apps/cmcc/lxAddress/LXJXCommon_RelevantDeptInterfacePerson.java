package com.actionsoft.apps.cmcc.lxAddress;

import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
/**
 * 集团立项、结项流程
 * 相关部门落实节点
 * 全集团所有研发机构接口人、需求部门接口人
 * @author chenxf
 *
 */
public class LXJXCommon_RelevantDeptInterfacePerson 
	implements AddressUIFilterInterface{
	//获取研发机构接口人、需求部门接口人的人员部门ID
	public String sql = "SELECT m.USERID userid,u.DEPARTMENTID deptId "
						+ "FROM ORGUSERMAP m, ORGROLE r, ORGUSER u "
						+ "WHERE m.ROLEID = r.ID "
							+ "AND m.USERID = u.USERID "
							+ "AND ( r.rolename = '需求部门接口人' OR r.rolename = '研发机构项目接口人' )";
	public static List<RowMap> list;
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, 
			DepartmentModel model, AdvancedAddressModel arg2) {
		//遍历的部门ID的全路径
		String pathDeptId = model.getPathIdOfCache();
		//接口人部门ID集合
		if(list == null || list.size() == 0){
			list = DBSql.getMaps(sql);
		}
		if(list != null && list.size() > 0){
			//判断是否部门下有接口人
			boolean flag = false;
			for(RowMap map : list){
				//获取接口人部门ID
				String filter_deptId = (String) map.get("deptId");
				//获取接口人的部门全路径
				System.err.println(">>>>filter_deptId:"+filter_deptId);
				String filter_pathDeptid = DepartmentCache.getModel(filter_deptId).getPathIdOfCache();
				if(filter_pathDeptid.contains(pathDeptId)){
					flag = true;
					break;
				}
			}
			return flag;
		}
		return false;
	}
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
			UserModel model, AdvancedAddressModel arg2) {
		//遍历人员
		String userid = model.getUID();
		if(list != null && list.size() > 0){
			//判断是否部门下有接口人
			boolean flag = false;
			for(RowMap map : list){
				//获取接口人部门ID
				String filter_userid = (String) map.get("userid");
				if(filter_userid.equals(userid)){
					flag = true;
					break;
				}
			}
			return flag;
		}
		return false;
	}
}
