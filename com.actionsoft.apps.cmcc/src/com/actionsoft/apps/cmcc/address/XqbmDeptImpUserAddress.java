package com.actionsoft.apps.cmcc.address;
/**
 * 参与者地址薄过滤事件
 * 需求部门文书岗。立项、结项需求部门接口人处理节点
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
import com.actionsoft.bpms.bo.engine.BO;
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

public class XqbmDeptImpUserAddress implements AddressUIFilterInterface {
	private List<BO> list = null;//记录牵头需求部门、配合需求部门集合
	private String bmjkrId = "";//部门文书岗角色ID
	private List<RowMap> listRowMap = new ArrayList<RowMap>();//角色下所有人员
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
				String QTXQBMIDS = list.get(0).getString("QTXQBMID");//牵头需求部门ID
				String PHXQBMIDS = list.get(0).getString("PHXQBMID");//配合需求部门ID
				if(!UtilString.isEmpty(QTXQBMIDS)){
					if(!UtilString.isEmpty(QTXQBMIDS)){
						String[] QTXQBMIDArr = QTXQBMIDS.split(",");
						for(int j = 0 ; j < QTXQBMIDArr.length ;j++){
							String QTXQBMID = QTXQBMIDArr[j];
							if(!UtilString.isEmpty(QTXQBMID)){
								String qtxqPathDeptId = DepartmentCache.getModel(QTXQBMID).getPathIdOfCache();
								listDeptPathId.add(qtxqPathDeptId);
							}
						}
					}
				}
				if(!UtilString.isEmpty(PHXQBMIDS)){
					if(!UtilString.isEmpty(PHXQBMIDS)){
						String[] PHXQBMIDArr = PHXQBMIDS.split(",");
						for(int j = 0 ; j < PHXQBMIDArr.length ;j++){
							String PHXQBMID = PHXQBMIDArr[j];
							if(!UtilString.isEmpty(PHXQBMID)){
								String qtphPathDeptId = DepartmentCache.getModel(PHXQBMID).getPathIdOfCache();
								listDeptPathId.add(qtphPathDeptId);
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

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {

		String userid = model.getUID();
		//部门文书岗角色ID
		if(UtilString.isEmpty(bmjkrId)){
			Connection conn = null;
			try{
				conn = DBSql.open();
				bmjkrId =DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{CmccConst.deptWsgRolename});
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
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

}
