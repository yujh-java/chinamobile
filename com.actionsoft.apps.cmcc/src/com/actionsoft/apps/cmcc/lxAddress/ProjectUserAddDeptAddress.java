package com.actionsoft.apps.cmcc.lxAddress;
/**
 * 立项流程
 * 立项、结项项目管理人员加签地址薄事件
 * @author nch
 * @date 20170622
 */
import java.sql.Connection;
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
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class ProjectUserAddDeptAddress implements AddressUIFilterInterface {
	private List<String> listDept = new ArrayList<String>();
	private List<String> listUser = new ArrayList<String>();
	
	public String sql = "select ROLEID from ORGUSERMAP where userid = ?";
	public String sql2 = "select ROLENAME from ORGROLE where id = ?";
	
	public String activeType = "";
	
	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel arg1, AdvancedAddressModel arg2) {
		return true;
	}

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advanceModel) {
		String choiceType = advanceModel.getChoiceType();
		if("single".equals(choiceType)){//委托办理
			//过滤部门，显示本部门及子部门
			String userDeptPathId = uc.getDepartmentModel().getPathIdOfCache(); // 当前登陆账户部门全路径ID
			String deptPathId = model.getPathIdOfCache();
			if(userDeptPathId.contains(deptPathId) || deptPathId.contains(userDeptPathId)){
				return true;
			}
		}else{
			/*
			 * 判断是否加签
			 */
			String taskInstId = advanceModel.getTaskId();
			boolean flag = SDK.getTaskAPI().isChoiceActionMenu(taskInstId, "送相关管理部门会签");
			if(flag){
				//获取参数部门编号
				if(listDept == null || listDept.size() == 0){
					String deptnos = SDK.getAppAPI().getProperty(CmccConst.propertyApp, CmccConst.propertyName);
					if(!UtilString.isEmpty(deptnos)){
						Connection conn = null;
						try{
							conn = DBSql.open();
							String[] deptnoArr = deptnos.split(",");
							for(int i = 0;i<deptnoArr.length;i++){
								String deptno = deptnoArr[i];
								String deptId = DBSql.getString(conn,"SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?", new Object[]{deptno});
								if(!UtilString.isEmpty(deptId)){
									String deptpathId = DepartmentCache.getModel(deptId).getPathIdOfCache();
									listDept.add(deptpathId);
								}
							}
						}catch(Exception e){
							e.printStackTrace(System.err);
						}finally{
							DBSql.close(conn);
						}
					}
				}
				String modelDeptPathId = model.getPathIdOfCache();
				if(listDept != null && listDept.size() > 0 && listDept.toString().contains(modelDeptPathId)){
					return true;
				}
			}else{
				//传阅--研究院下所有部所
				//当前人父部门ID
				String userParentDepId = uc.getDepartmentModel().getParentDepartmentId();
				 // 当前登陆账户部门全路径ID
				String userPathDeptId = DepartmentCache.getModel(userParentDepId).getPathIdOfCache();
				String deptPathId = model.getPathIdOfCache();
				if(userPathDeptId.contains(deptPathId) || deptPathId.contains(userPathDeptId)){
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advanceModel) {
		
		String choiceType = advanceModel.getChoiceType();
		if("single".equals(choiceType)){//委托办理
			String uc_departMentID = uc.getDepartmentModel().getPathIdOfCache();
			String model_deptMentId = SDK.getORGAPI().getDepartmentById(model.getDepartmentId()).getPathIdOfCache();
			if(!model_deptMentId.contains(uc_departMentID)){
				return false;
			}
			String userid = model.getUID();
			List<RowMap> list = DBSql.getMaps(sql, new Object[]{ userid });
			if(list != null && list.size() > 0){
				if(list != null && list.size() > 0){
					//判断是不是兼职项目管理人员角色
					boolean flag = false;
					for(int k = 0;k < list.size();k++){
						//兼职角色ID
						String roleId = list.get(k).getString("ROLEID");
						//兼职角色名
						String rolename = DBSql.getString(sql2 , new Object[]{ roleId });
						if(CmccConst.projectManagerRolename.equals(rolename)){
							flag = true;
							break;
						}
					}
					if(flag){
						return true;
					}
				}
			}
			return false;
		}else{
			/*
			 * 判断是否加签
			 */
			String taskInstId = advanceModel.getTaskId();
			boolean flag = SDK.getTaskAPI().isChoiceActionMenu(taskInstId, "送相关管理部门会签");
			
			if(flag){
				String modelUser = model.getUID();
				if(listUser == null || listUser.size() == 0){
					List<String> listRole = new ArrayList<String>();
					String roleNames = SDK.getAppAPI().getProperty(CmccConst.propertyApp, CmccConst.propertyRoleName);
					Connection conn = null;
					try{
						conn = DBSql.open();
						if(!UtilString.isEmpty(roleNames)){
							String[] roleNameArr = roleNames.split(",");
							for(int i = 0;i<roleNameArr.length;i++){
								String roleName = roleNameArr[i];
								String roleId = DBSql.getString(conn,"SELECT ID FROM ORGROLE WHERE ROLENAME = ?", new Object[]{roleName});
								listRole.add(roleId);
							}
						}
						for(int j = 0;j<listRole.size();j++){
							String roleId = listRole.get(j);
							List<RowMap> list = DBSql.getMaps(conn,"SELECT USERID FROM ORGUSERMAP WHERE ROLEID= ?", new Object[]{roleId});
							if(list != null && list.size() > 0){
								for(int k = 0;k<list.size();k++){
									String userid = list.get(k).getString("USERID");
									listUser.add(userid);
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace(System.err);
					}finally{
						DBSql.close(conn);
					}
				}
				if(listUser != null && listUser.size() > 0 && listUser.toString().contains(modelUser)){
					return true;
				}
			}else{
				//只留部所领导、院领导
				if(CmccConst.user_leave3.equals(model.getExt1())){
					return true;
				}else if(CmccConst.user_leave2.equals(model.getExt1())){
					return true;
				}
			}
		}
		return false;
	}
}
