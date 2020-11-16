package com.actionsoft.apps.cmcc.integration.util;

import java.sql.Connection;
import com.actionsoft.apps.cmcc.integration.CMCCConst;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
* @author yujh 
* @version 创建时间：2019年6月21日 上午10:04:57 
* org角色配置类
*/
public class OrgUtil {
	/**
	 * 创建用户角色
	 * @param appId
	 * @param updateUser
	 * @param roleUser
	 * @param roleId
	 * @param roleNmae
	 * @param departmentId
	 * @return
	 */
	public String createRole(String appId,String updateUser, String roleUser, String roleId, String roleName, String departmentId){
		JSONObject result = new JSONObject();
		String[] uidList = roleUser.split(" ");
		String msg="成功!";
		String success="true";
		Connection conn=DBSql.open();
		for (String uid : uidList) {
			String sql = " SELECT COUNT(*) FROM ORGUSERMAP WHERE USERID = ? AND ROLEID = ? AND DEPARTMENTID = ?";
			int count =DBSql.getInt(conn, sql, new Object[]{uid,roleId,departmentId});
			if(count>0){
				msg+="用户【"+uid+"】已有此部门下此角色,禁止添加 ";
				success="false";
			}else{
				SDK.getORGAPI().createUserMap(uid, departmentId, roleId, false, true);
			}
		}
		DBSql.close(conn);
		result.put("msg", msg);
		result.put("success", success);
		return result.toString();
	}
	
	/**
	 * 更新用户角色入口
	 * @param appId
	 * @param updateUser
	 * @param data
	 * @return
	 */
	public String updateRole(String appId,String updateUser,String data){
		JSONArray retult =new JSONArray();
		JSONObject updateRoleJson =new JSONObject();
		System.err.println(">>>data:"+data);
		if(UtilString.isNotEmpty(data)){
			JSONArray array =JSONArray.fromObject(data);
			for (Object object : array) {
				long bTime=System.currentTimeMillis();
				long eTime=0L;
				long excTime=0L;//耗时时间
				JSONObject roleObject=JSONObject.fromObject(object);
				String roleId = roleObject.get("roleId").toString();
				String roleUsers = roleObject.get("roleUsers").toString();
				String departmentId = roleObject.get("departmentId").toString();
				updateRoleJson = updateRole(appId,updateUser,roleId,roleUsers,departmentId);
				retult.add(updateRoleJson);
				eTime =System.currentTimeMillis();
				excTime =eTime-bTime;
				System.err.println(">>>>>bTime:"+bTime);
				System.err.println(">>>>>eTime:"+eTime);
				System.err.println(">>>>>excTime:"+excTime);
				roleUpateDateInfo(appId, "updateRole", roleConvert(roleId), roleUsers, departmentId, excTime, updateUser, updateRoleJson.getBoolean("isSuccess"), updateRoleJson.getString("msg"));
				
			}
		}else{
			System.err.println("data为空");
		}
		
		return retult.toString();
	}
	
	/**
	 * 更新用户角色
	 * @param appId
	 * @param updateUser
	 * @param roleUserBefore
	 * @param roleUserEnd
	 * @param roleId
	 * @param roleName
	 * @param departmentId
	 * @return
	 */
	public JSONObject updateRole(String appId,String updateUser,String roleId,String roleUserEnd,String departmentId){
		JSONObject result = new JSONObject();
		String awsRoleId = roleConvert(roleId);//转换成平台角色ID
		departmentId = departmentConvert(departmentId);
		String awsBeforeRole = getAWSBeforeRole(awsRoleId, departmentId);
		ORGAPI orgapi = SDK.getORGAPI();
		String[] beforeUsers =null;
		if(UtilString.isNotEmpty(awsBeforeRole)){
			beforeUsers = awsBeforeRole.trim().split(" ");
		}
		String[] endUsers = roleUserEnd.trim().split(" ");
		//校验同步数据信息
		JSONObject checkInfo =checkRoleInfo(updateUser, roleId, awsBeforeRole, roleUserEnd, departmentId);
		if(!checkInfo.getBoolean("isSuccess")){
			return checkInfo;
		}
		Connection conn=null;
		if(beforeUsers!=null && beforeUsers.length>0){//执行删除操作
			conn= DBSql.open();
			for (int i = 0 ;i<beforeUsers.length;i++) {
				String sql = " SELECT ID FROM ORGUSERMAP WHERE USERID = ? AND ROLEID = ? AND DEPARTMENTID = ? ";
				String ID = DBSql.getString(conn, sql ,new Object[]{beforeUsers[i].trim(),awsRoleId,departmentId});
				if(UtilString.isNotEmpty(ID)){
					orgapi.removeUserMap(ID);
				}else{
					DBSql.close(conn);
					result.put("isSuccess", false);
					result.put("msg", beforeUsers[i]+"账号存在异常，请检查此账户。");
					return result;
				}
			}
		}
		if(null!=conn){
			DBSql.close(conn);
		}
		if(endUsers.length>0){//执行新增操作
			for (int i =0 ;i<endUsers.length;i++) {
				try {
					orgapi.createUserMap(endUsers[i].trim(), departmentId, awsRoleId, false, true);
				} catch (Exception e) {
					e.printStackTrace();
					result.put("isSuccess", false);
					result.put("msg", endUsers[i]+"账号存在异常，请检查此账户。");
					return result;
				}
			}
		}
		result.put("isSuccess", true);
		result.put("msg", roleId+"修改成功！");                                                
		result.put("size", 0);
		result.put("data", roleUserEnd);
		return result;
	}
	
	
	/**
	 * 检验角色用户信息
	 * @param updateUser
	 * @param roleUser
	 * @param roleId
	 * @return
	 */
	public JSONObject checkRoleInfo(String updateUser, String roleId, String awsBeforeRole,String roleUserEnd ,String departmentId){
		JSONObject json = new JSONObject();
		if(UtilString.isEmpty(updateUser)){
			json.put("msg", "操作人不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json;
		}
		
		if(UtilString.isEmpty(roleId) ||UtilString.isEmpty(roleConvert(roleId))){
			json.put("msg", "角色ID不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json;
		}
		
		if(UtilString.isEmpty(roleUserEnd)){
			json.put("msg", roleId+"角色用户不允许为空");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json;
		}
		
		if(awsBeforeRole.equals(roleUserEnd)){
			json.put("msg", roleId+"角色用户没有变化，不予更新！");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json;
		}
		
		String validateUsers = SDK.getORGAPI().validateUsers(roleUserEnd);
		if(UtilString.isNotEmpty(validateUsers)){
			json.put("msg", roleId+"更新后的用户"+validateUsers+"异常，更新失败");
			json.put("isSuccess", false);
			json.put("size", 0);
			json.put("data", "");
			return json;
		}
		json.put("isSuccess", true);
		return json;
	}
	
	/**
	 * 补全用户账号
	 * @param userId
	 * @return
	 */
	public String validateUsers(String userId){
		if(userId.indexOf("@")==-1){
			userId=userId+"@hq.cmcc";
		}
		return userId;
	}
	
	/**
	 * 角色配置操作记录留痕
	 * @param appId 应用名称
	 * @param method 方法名称
	 * @param roleId 角色ID 
	 * @param roleName 角色名称 
	 * @param roleUserBefore 更新前角色用户
	 * @param awsBeforeRole 流程平台更新前角色用户
	 * @param roleUserEnd 更新后角色用户
	 * @param departmentId 部门ID
	 * @param excTime 耗时时间
	 * @param updateUser 更新操作者
	 * @param isSuccess 是否成功
	 * @param msg 操作信息
	 * @return
	 */
	public void roleUpateDateInfo(String appId,String method,String roleId,
			 String roleUsers,String departmentId,long excTime ,String updateUser,boolean isSuccess,String msg){
		BO bo =new BO();
		bo.set("APPID", appId);
		bo.set("METHOD", method);
		bo.set("ROLEID", roleId);
		bo.set("ROLENAME", SDK.getORGAPI().getRoleById(roleId).getName());
		bo.set("DEPARTMENTID", departmentId);
		bo.set("UPDATETIME", excTime);
		bo.set("ISSUCCESS", isSuccess);
		bo.set("MSG", msg);
		ProcessInstance createBOProcessInstance = SDK.getProcessAPI().createBOProcessInstance(CMCCConst.ROLECONFIG_PROCESSID, updateUser, "同步记录");
		SDK.getBOAPI().create("BO_ACT_CMCC_ROLEMAP", bo,createBOProcessInstance, UserContext.fromUID(updateUser));
	}
	
	/**
	 * 根据角色配置转换表把第三方角色转换流程平台角色ID
	 * @param roleId
	 * @return
	 */
	public String roleConvert(String roleId){
		String sql = " SELECT ROLEID FROM BO_ACT_CMCC_ROLECONFIG WHERE SYSTEMROLEID =? ";
		roleId= DBSql.getString(sql ,new Object[]{roleId});
		return roleId;
	}
	
	/**
	 * 根据部门表把第三方部门编码转换成流程平台部门ID
	 * @return
	 */
	public String departmentConvert(String departmentId){
		String sql ="SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?";
		departmentId = DBSql.getString(sql ,new Object[]{departmentId});
		return departmentId;
	}
	
	/**
	 * 获取平台当前兼职角色信息
	 * @param roleId
	 * @param departmentId
	 * @return
	 */
	public String getAWSBeforeRole(String roleId,String departmentId){
		String sql ="SELECT GROUP_CONCAT(USERID SEPARATOR ' ') AS USERID FROM ORGUSERMAP WHERE ROLEID =? AND DEPARTMENTID =?";
		String awsBeforeRole =DBSql.getString(sql,new Object[]{roleId,departmentId});
		return awsBeforeRole == null ? "":awsBeforeRole;
	}
	
	/**
	 * 将第三方角色ID转换成平台角色ID
	 * @param systemRoleId
	 */
	public String convertRoleId(String systemRoleId){
		String roleId = DBSql.getString("SELECT ROLEID FROM BO_ACT_CMCC_ROLECONFIG WHERE SYSTEMROLEID =?",new Object[]{systemRoleId});
		return roleId;
	}
	
}
