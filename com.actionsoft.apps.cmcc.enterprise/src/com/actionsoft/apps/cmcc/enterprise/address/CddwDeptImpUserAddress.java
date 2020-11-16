package com.actionsoft.apps.cmcc.enterprise.address;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

/**
 * 企标复审流程参与者地址薄过滤事件
 * 承担单位企标管理员节点
 * @author wuxx
 * @date 20190528
 */
public class CddwDeptImpUserAddress implements AddressUIFilterInterface{
	private List<BO> list = null;//记录牵头需求部门、配合需求部门集合
	private String bmjkrId = "";//企标管理员角色ID
	private List<String> listDeptPathId = new ArrayList<String>();//角色下所有人员部门全路径
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String processid = advancedAddressModel.getInstanceId();
		if(list == null || list.size() == 0){
			list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processid).list();
		}
		if(list != null && list.size()>0){
			for(int i = 0;i < list.size();i++){
				String UNDERTAKES = list.get(0).getString("UNDERTAKE");//配合承担单位ID
				
				
				if(!UtilString.isEmpty(UNDERTAKES)){
					if(!UtilString.isEmpty(UNDERTAKES)){
						String[] QTXQBMIDArr = UNDERTAKES.split(",");
						for(int j = 0 ; j < QTXQBMIDArr.length ;j++){
							String QTXQBMID = QTXQBMIDArr[j].trim();
							if(!UtilString.isEmpty(QTXQBMID)){
								String qtxqPathDeptId = DepartmentCache.getModel(QTXQBMID).getPathIdOfCache();
							//String qtxqPathDeptId = DepartmentCache.getModel("5b93a3f7-3ae2-4831-9fcc-d8809ffc462c").getPathIdOfCache();
								listDeptPathId.add(qtxqPathDeptId);
							}
						}
					}
				}
				
			}
			String modelPathId = model.getPathIdOfCache();
			if(listDeptPathId.toString().contains(modelPathId)){
				return true;
			}
		}
		return false;
	}
	
	/*@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {

		String userid = model.getUID();
		//部门文书岗角色ID
		if(UtilString.isEmpty(bmjkrId)){
			Connection conn = null;
			try{
				conn = DBSql.open();
				bmjkrId =DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.deptqbRolename});
				listRowMap = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?", new Object[]{bmjkrId});
			}catch(Exception e){
				e.printStackTrace(System.err);
			}finally{
				DBSql.close(conn);
			}
		}
		if(listRowMap != null && listRowMap.size() > 0){
			for(int i = 0;i<listRowMap.size();i++){
				String USERID = (String) listRowMap.get(i).get("USERID");
				if(userid.equals(USERID)){
					return true;
				}
			}
		}
		return false;
	}*/
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel arg2) {
		String uid = model.getUID();
		if(UtilString.isEmpty(bmjkrId)){
			bmjkrId =DBSql.getString("SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.deptqbRolename});
			
		}
		List<UserMapModel> userMaps = SDK.getORGAPI().getUserMaps(uid);
		/*if(uid.equals("shenjun@cmss.cmcc")){
			System.err.println(userMaps.size());
		}*/
		if(null == userMaps || userMaps.size()<1){
			return false;
		}else{
			for (UserMapModel userMapModel : userMaps) {
				/*if(uid.equals("shenjun@cmss.cmcc")){
					System.err.println("roleId："+roleId);
					System.err.println("userMapModel.getRoleId()："+userMapModel.getRoleId());
				}*/
				String usermapmodel=userMapModel.getDepartmentId();
					if(userMapModel.getRoleId().equals(bmjkrId)){//含有接口人角色显示
						return true;
						
					}
				
				
			}
		}
		return false;
	}
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
