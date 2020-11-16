package com.actionsoft.apps.cmcc.address;
/**
 * 配合研发机构接口人.立项、结项配合研发机构接口人节点
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.actionsoft.bpms.util.UtilString;

public class PhyfjgDeptImperAddress implements AddressUIFilterInterface {
	private String processId = "";
	private List<RowMap> list_phjg = new ArrayList<RowMap>();
	private List<String> listUser = new ArrayList<String>();
	private List<String> listPathDeptId = new ArrayList<String>();

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@SuppressWarnings("resource")
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advance) {
		Connection conn = null;//创建连接
		try{
			conn = DBSql.open();
			//根据配合研发机构部门，查询部门下所有研发机构项目接口人所在部门
			String deptId = model.getId();//循环部门ID
			String deptpath_model = model.getPathIdOfCache();//循环部门全路径
			if(UtilString.isEmpty(processId)){
				processId = advance.getInstanceId();//流程实例ID
				//配合研发机构部门ID
				list_phjg = DBSql.getMaps(conn,"SELECT PHYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID=?", new Object[]{processId});
			}		
			if(list_phjg != null && list_phjg.size() > 0){
				for(int i = 0;i<list_phjg.size();i++){
					String phyfjgbmIds = list_phjg.get(i).getString("PHYFJGBMID");
					if(!UtilString.isEmpty(phyfjgbmIds)){
						String[] phyfjgbmIdArr = phyfjgbmIds.split(",");
						for(int j = 0;j<phyfjgbmIdArr.length;j++){
							String phyfjgbmId = phyfjgbmIdArr[j];
							//配合研发机构部门全路径
							String deptid_path = DepartmentCache.getModel(phyfjgbmId).getPathIdOfCache();
							if(deptid_path.contains(deptId)){
								return true;
							}else if(deptpath_model.contains(deptid_path)){
								//过滤人员；有人员的显示
								if(listPathDeptId == null || listPathDeptId.size() == 0){
									String rolename = "研发机构项目接口人";//过滤角色名称
									String roleId = DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{rolename});
									List<RowMap> list = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{roleId});
									if(list != null && list.size() > 0){
										for(int k = 0;k<list.size();k++){
											String userid = list.get(k).getString("USERID");
											String deptid = UserCache.getModel(userid).getDepartmentId();
											String deptpathID = DepartmentCache.getModel(deptid).getPathIdOfCache();
											listUser.add(userid);
											listPathDeptId.add(deptpathID);
										}
									}
								}
								//只显示有人员的部门
								if(listPathDeptId != null && listPathDeptId.size() > 0 ){
									for(int m=0;m<listPathDeptId.size();m++){
										if(listPathDeptId.get(m).contains(deptpath_model)){
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			DBSql.close(conn);
		}
		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advance) {
		Connection conn = null;
		try{
			conn = DBSql.open();
			String model_userID = model.getUID();//循环人员账号
			if(listUser == null || listUser.size() == 0){
				String rolename = "研发机构项目接口人";//过滤角色名称
				String roleId = DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{rolename});
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ROLEID", roleId);
				List<RowMap> list = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{roleId});
				if(list != null && list.size() > 0){
					for(int k = 0;k<list.size();k++){
						String userid = list.get(k).getString("USERID");
						listUser.add(userid);
					}
				}
			}
			if(listUser != null && listUser.size() > 0 && listUser.toString().contains(model_userID)){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			if(conn != null){
				DBSql.close(conn);
			}
		}
		return false;
	}

}
