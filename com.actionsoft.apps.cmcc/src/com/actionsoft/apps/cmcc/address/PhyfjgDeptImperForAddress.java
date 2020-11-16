package com.actionsoft.apps.cmcc.address;
/**
 * 配合研发机构接口人.立项、结项配合研发机构接口人节点
 * 显示公司下兼职人员，不显示兼字
 * @author nch
 * @date 20170721
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class PhyfjgDeptImperForAddress implements AddressUIFilterInterface {
	private String processId = "";
	private List<RowMap> list_phjg = new ArrayList<RowMap>();
	private List<String> listUser = new ArrayList<String>();
	private List<String> listPathDeptId = new ArrayList<String>();
	private String roleId = "";
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advance) {
		String itDept_1 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_1).getPathIdOfCache();
		String itDept_2 = SDK.getORGAPI().getDepartmentById(CmccConst.IT_DEPT_2).getPathIdOfCache();
		String userDept = uc.getDepartmentModel().getPathIdOfCache();
		if((userDept.contains(itDept_1) || userDept.contains(itDept_2)) && (model.getPathIdOfCache().contains(itDept_1) || model.getPathIdOfCache().contains(itDept_2))){
			return true;
		}
		Connection conn = null;
		try{
			conn = DBSql.open();
			if(UtilString.isEmpty(processId)){
				processId = advance.getInstanceId();//流程实例ID
				//配合研发机构部门ID
				list_phjg = DBSql.getMaps(conn,"SELECT PHYFJGBMID FROM BO_ACT_CMCC_PROCESSDATA WHERE PROCESSID=?", new Object[]{processId});
			}
			if(list_phjg != null && list_phjg.size() > 0){
				for(int i = 0;i < list_phjg.size();i++){
					String phyfjgbmids = list_phjg.get(i).getString("PHYFJGBMID");
					if(!UtilString.isEmpty(phyfjgbmids)){
						String[] phyfjgbmidArr = phyfjgbmids.split(",");
						for(int j = 0; j< phyfjgbmidArr.length;j++){
							String phyfjgbmid = phyfjgbmidArr[j];
							if(!UtilString.isEmpty(phyfjgbmid)){
								String phyfjgPathDeptid = DepartmentCache.getModel(phyfjgbmid).getPathIdOfCache();
								listPathDeptId.add(phyfjgPathDeptid);	
							}
						}
					}
				}
			}
			String modelPathId = model.getPathIdOfCache();
			if(listPathDeptId.toString().contains(modelPathId)){
				return true;
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
				if(UtilString.isEmpty(roleId)){
					roleId = DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{rolename});
				}
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
