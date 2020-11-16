package com.actionsoft.apps.cmcc.enterprise.fs;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.bpms.bo.engine.BO;
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
 * 企标复审流程
 * 获取需求部门企标管理员
 * @author wuxx
 *
 */
public class FS_Sub_GetManagerAddress implements AddressUIFilterInterface{
	private List<BO> list = null;//记录牵头需求部门、配合需求部门集合
	private String bmjkrId = "";//企标管理员角色ID
	private List<String> listDeptPathId = new ArrayList<String>();//角色下所有人员部门全路径
	//获取复审需求部门ID
		public String sql_remandDeptId = "select DEMAND from BO_ACT_CMCC_PROCESSDATA where PROCESSID = ?";
		
		//需求部门ID
		public String remandDeptId = "";
		
		
	
	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
	
		String processid = advancedAddressModel.getInstanceId();
			remandDeptId = DBSql.getString(sql_remandDeptId, new Object[]{ processid });
			System.err.println("=====需求部门ID："+remandDeptId+"=======");
			
		
		
		if(list == null || list.size() == 0){
			list = SDK.getBOAPI().query("BO_ACT_CMCC_PROCESSDATA").addQuery("PROCESSID = ", processid).list();
		}
		if(list != null && list.size()>0){
			for(int i = 0;i < list.size();i++){
				String DEMANDS = list.get(i).getString("DEMAND");//需求部门ID
				
				if(!UtilString.isEmpty(DEMANDS)){
					if(!UtilString.isEmpty(DEMANDS)){
						//String[] QTXQBMIDArr = DEMANDS.split(",");
						String[] QTXQBMIDArr = DEMANDS.split(" ");
						//System.err.println("QTXQBMIDArr444"+QTXQBMIDArr.toString());
						for(int j = 0 ; j < QTXQBMIDArr.length ;j++){
							String QTXQBMID = QTXQBMIDArr[j].trim();
							System.err.println("000000099"+QTXQBMID);
							if(!UtilString.isEmpty(QTXQBMID)){
								String qtxqPathDeptId = DepartmentCache.getModel(QTXQBMID).getPathIdOfCache();
								//System.err.println("qtxqPathDeptId------"+qtxqPathDeptId);
							//String qtxqPathDeptId = DepartmentCache.getModel("5b93a3f7-3ae2-4831-9fcc-d8809ffc462c").getPathIdOfCache();
								listDeptPathId.add(qtxqPathDeptId);
							}
						}
					}
				}
				
			}
			String modelPathId = model.getPathIdOfCache();
			System.err.println("modelPathId+++++++"+modelPathId);
			if(listDeptPathId.toString().contains(modelPathId)){
				return true;
			}
		}
		return false;
	}
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
