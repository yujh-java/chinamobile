package com.actionsoft.apps.roleconfig;

import com.actionsoft.apps.roleconfig.web.RoleConfigWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;
/**
 * 角色配置APP CMD
 * @author Administrator
 *
 */
@Controller
public class RoleConfigController {
	/**
	 * 打开修改表单
	 * @param me
	 * @param uids
	 * @param roleId
	 * @param roleNames
	 * @param departmentId
	 * @param departmentName
	 * @param caregoryName
	 * @return
	 */
	@Mapping("com.actionsoft.apps.roleconfig.openUpdateForm")
	public String openUpdateForm(UserContext me,String uids,String roleId,String roleNames,String userNames,String departmentId,String departmentName,String caregoryName){
		RoleConfigWeb web=new RoleConfigWeb(me);
		return web.openUpdateForm(me, uids, roleId, roleNames, userNames, departmentId, departmentName, caregoryName);
	}
	
	/**
	 * 修改表单记录
	 * @param me
	 * @param uids
	 * @param newUids
	 * @param roleId
	 * @param departmentId
	 * @return
	 */
	@Mapping("com.actionsoft.apps.roleconfig.updateFormData")
	public String updateFormData(UserContext me,String uids ,String newUids,String roleId,String departmentId){
		RoleConfigWeb web=new RoleConfigWeb(me);
		return web.updateFormData(me, uids, newUids, roleId, departmentId); 
	}
	
	/**
	 * 打开新增表单
	 * @param me
	 * @return
	 */
	@Mapping("com.actionsoft.apps.roleconfig.openAddForm")
	public String openAddForm(UserContext me){
		RoleConfigWeb web=new RoleConfigWeb(me);
		return web.openAddForm(me);
	}
	
	/**
	 * 新增用户兼职
	 * @param me
	 * @param roleId
	 * @param departmentId
	 * @param uids
	 * @return
	 */
	@Mapping("com.actionsoft.apps.roleconfig.addRoleMaps")
	public String addRoleMaps(UserContext me ,String roleId ,String departmentId ,String uids){
		RoleConfigWeb web=new RoleConfigWeb(me);
		return web.addRoleMaps(me, roleId, departmentId, uids);
	}
	/**
	 * 删除用户兼职
	 * @param me
	 * @param roleId
	 * @param departmentId
	 * @param uids
	 * @return
	 */
	@Mapping("com.actionsoft.apps.roleconfig.deleteRoleMaps")
	public String deleteRoleMaps(UserContext me ,String roleId ,String departmentId ,String uids){
		RoleConfigWeb web=new RoleConfigWeb(me);
		return web.deleteRoleMaps(me, roleId, departmentId, uids);
	}
}
