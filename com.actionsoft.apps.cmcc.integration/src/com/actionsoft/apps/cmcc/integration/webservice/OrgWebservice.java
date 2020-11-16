package com.actionsoft.apps.cmcc.integration.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import com.actionsoft.apps.cmcc.integration.util.OrgUtil;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.HandlerType;
import com.actionsoft.bpms.server.bind.annotation.Param;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import net.sf.json.JSONObject;

/** 
* @author yujh 
* @version 创建时间：2019年6月19日 上午9:36:01 
* 组织同步扩展接口
*/
@Controller(type = HandlerType.NORMAL, apiName = "OrgWebservice Api", desc = "组织同步扩展接口")
@WebService(serviceName = "OrgWebserviceApi")
public class OrgWebservice {
	/**
	 * 新增角色记录
	 * @param appID  应用名称
	 * @param updateUser 操作人
	 * @param roleUser 角色用户
	 * @param roleID 角色ID
	 * @param roleName 角色名称
	 * @param departmentID 部门ID
	 * @return
	 */
	public String createRoleUsers(@Param(value = "appId", desc = "应用名称", required = true) @WebParam(name = "appId") String appId,
			@Param(value = "updateUser", desc = "操作人", required = true) @WebParam(name = "updateUser") String updateUser,
			@Param(value = "roleUser", desc = "角色用户", required = true) @WebParam(name = "roleUser") String roleUser,
			@Param(value = "roleId", desc = "角色ID", required = true) @WebParam(name = "roleId") String roleId,
	        @Param(value = "roleName", desc = "角色名称", required = false) @WebParam(name = "roleName") String roleName,
	        @Param(value = "departmentId", desc = "部门ID", required = false) @WebParam(name = "departmentId") String departmentId){
		JSONObject json = new JSONObject();
		//校验开始
		if(UtilString.isEmpty(updateUser)){
			json.put("msg", "操作人不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json.toString();
		}
		
		if(UtilString.isEmpty(roleUser)){
			json.put("msg", "角色用户不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json.toString();
		}
		
		if(UtilString.isEmpty(roleId)){
			json.put("msg", "角色ID不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json.toString();
		}
		
		if(!UtilString.isEmpty(roleUser) && roleUser.indexOf("@")==-1){
			roleUser=roleUser+"@hq.cmcc";
		}
		
		if(UserCache.getModel(roleUser)==null || !UtilString.isEmpty(SDK.getORGAPI().validateUsers(roleUser))){
			json.put("msg", "角色用户不存在或者已注销");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json.toString();
		}
		
		OrgUtil orgUtil =new OrgUtil();
		String result = orgUtil.createRole(appId, updateUser, roleUser, roleId, roleName, departmentId);
		return result;
	}
	
	/**
	 * 删除角色记录
	 * @param appID  应用名称
	 * @param updateUser 操作人
	 * @param roleUser 角色用户
	 * @param roleID 角色ID
	 * @param roleNmae 角色名称
	 * @param departmentID 部门ID
	 * @return
	 */
	public String removeRoleUsers(@Param(value = "appID", desc = "应用名称", required = true) @WebParam(name = "appID") String appID,
			@Param(value = "updateUser", desc = "操作人", required = true) @WebParam(name = "updateUser") String updateUser,
			@Param(value = "roleUser", desc = "角色用户", required = true) @WebParam(name = "roleUser") String roleUser,
			@Param(value = "roleID", desc = "角色ID", required = false) @WebParam(name = "roleID") String roleID,
	        @Param(value = "roleNmae", desc = "角色名称", required = false) @WebParam(name = "roleNmae") String roleNmae,
	        @Param(value = "departmentID", desc = "部门ID", required = false) @WebParam(name = "departmentID") String departmentID){
		return "";
	}
	
	/**
	 * 更新角色记录
	 * @param appId  应用名称
	 * @param updateUser 操作人
	 * @param roleUser 角色用户
	 * @param roleId 角色ID
	 * @param roleName 角色名称
	 * @param departmentId 部门ID
	 * @return
	 */
	public String updateRoleUsers(@Param(value = "appId", desc = "应用名称", required = true) @WebParam(name = "appId") String appId,
			@Param(value = "updateUser", desc = "操作人", required = true) @WebParam(name = "updateUser") String updateUser,
			@Param(value = "data", desc = "更新前角色用户", required = true) @WebParam(name = "data") String data){
		System.err.println(">>>updateRoleUsers进来了");
		OrgUtil orgUtil =new OrgUtil();
		//执行修改
		String result= orgUtil.updateRole(appId, updateUser,data);
		return result;
	}
}
