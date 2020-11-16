package com.actionsoft.apps.roleconfig.web;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.actionsoft.apps.roleconfig.constant.RoleConfigConstant;
import com.actionsoft.apps.roleconfig.util.PowerUtil;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;

import net.sf.json.JSONObject;

public class RoleConfigWeb extends ActionWeb{
	public RoleConfigWeb(UserContext _context) {
		super(_context);
	}
	public RoleConfigWeb(){}
	
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
	public String openUpdateForm(UserContext me,String uids,String roleId,String roleNames,String userNames,String departmentId,String departmentName,String caregoryName){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("appId", RoleConfigConstant.APPID);
		result.put("sid", me.getSessionId());
		result.put("uids", uids);
		result.put("roleId", roleId);
		result.put("roleNames", roleNames);
		result.put("userNames", userNames);
		result.put("departmentId",departmentId);
		result.put("departmentName", departmentName);
		result.put("caregoryName", caregoryName);
		return HtmlPageTemplate.merge(RoleConfigConstant.APPID, "roleconfig_updateform.html", result);
	}
	
	/**
	 * 修改表单数据
	 * @param me
	 * @param uids
	 * @param newUids
	 * @param roleId
	 * @param departmentId
	 * @return
	 */
	public String updateFormData(UserContext me,String uids ,String newUids,String roleId,String departmentId){
		ORGAPI orgapi = SDK.getORGAPI();
		JSONObject result = new JSONObject();
		if(uids.equals(newUids)){//没变化
			result.put("success", false);
			result.put("msg", "用户没有变化，不做处理");
			return result.toString();
		}
		String[] beforeUsers = uids.split(" ");
		String[] afterUsers = newUids.split(" ");
		Set<String> same = new HashSet<String>(); 
		Set<String> addList = new HashSet<String>();//新增的 
		for (String temp : beforeUsers) {
			if(!orgapi.validateUsers(temp).isEmpty()){
				result.put("success", false);
				result.put("msg", temp+"账号存在异常，请检查此账户。");
				return result.toString();
			}
			same.add(temp);
		}
		for (String temp : afterUsers) {
			if(!orgapi.validateUsers(temp).isEmpty()){
				result.put("success", false);
				result.put("msg", temp+"账号存在异常，请检查此账户。");
				return result.toString();
			}
			if(!same.contains(temp)){//修改后的不在修改前的集合中，则为新增
				addList.add(temp);
			}else{
				same.remove(temp);//修改后的在修改前的集合中全部移除，则剩下删除的
			}
		}
		Connection conn=null;
		if(same.size()>0){//执行删除操作
			conn= DBSql.open();
			for (String temp : same) {
				String sql = " SELECT ID FROM ORGUSERMAP WHERE USERID = ? AND ROLEID = ? ";
				String ID = DBSql.getString(conn, sql ,new Object[]{temp,roleId});
				if(UtilString.isNotEmpty(ID)){
					orgapi.removeUserMap(ID);
				}else{
					DBSql.close(conn);
					result.put("success", false);
					result.put("msg", temp+"账号存在异常，请检查此账户。");
					return result.toString();
				}
			}
		}
		if(null!=conn){
			DBSql.close(conn);
		}
		if(addList.size()>0){//执行新增操作
			for (String temp : addList) {
				try {
					orgapi.createUserMap(temp, departmentId, roleId, false, true);
				} catch (Exception e) {
					e.printStackTrace();
					result.put("success", false);
					result.put("msg", temp+"账号存在异常，请检查此账户。");
					return result.toString();
				}
			}
		}
		result.put("success", true);
		result.put("msg", "修改成功！");                                                
		return result.toString();
	}
	
	/**
	 * 打开新增表单
	 * @param me
	 * @return
	 */
	public String openAddForm(UserContext me){
		JSONObject result = new JSONObject();
		//权限过滤类型
		String powerType = SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "ROLE_POWER_TYPE");
		PowerUtil power = new PowerUtil();
		String roleList = power.getRolesByPowerType(me, powerType);
		result.put("sid", me.getSessionId());
		result.put("roleList", roleList);
		return HtmlPageTemplate.merge(RoleConfigConstant.APPID, "roleconfig_addform.html", result);
	}
	
	/**
	 * 新增用户兼职
	 * @param me
	 * @param roleId
	 * @param departmentId
	 * @param uids
	 * @return
	 */
	public String addRoleMaps(UserContext me ,String roleId ,String departmentId ,String uids){
		JSONObject result = new JSONObject();
		String[] uidList = uids.split(" ");
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
	 * 删除用户兼职
	 * @param me
	 * @param roleId
	 * @param departmentId
	 * @param uids
	 * @return
	 */
	public String deleteRoleMaps(UserContext me ,String roleId ,String departmentId ,String uids){
		JSONObject result = new JSONObject();
		String[] uidList = uids.split(" ");
		String msg="成功!";
		String success="true";
		Connection conn=DBSql.open();
		for (String uid : uidList) {
			String sql = " SELECT ID FROM ORGUSERMAP WHERE USERID = ? AND ROLEID = ? AND DEPARTMENTID = ?";
			String roleMapsId =DBSql.getString(conn, sql, new Object[]{uid,roleId,departmentId});
			if(roleMapsId.isEmpty()){
				msg+="用户【"+uid+"】在部门【"+departmentId+"】下无角色【"+roleId+"】,禁止删除  ";
				success="false";
			}else{
				SDK.getORGAPI().removeUserMap(roleMapsId);
			}
		}
		DBSql.close(conn);
		result.put("msg", msg);
		result.put("success", success);
		return result.toString();
	}
}
