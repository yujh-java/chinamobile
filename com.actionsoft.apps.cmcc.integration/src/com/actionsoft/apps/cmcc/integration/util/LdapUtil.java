package com.actionsoft.apps.cmcc.integration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import com.actionsoft.apps.cmcc.integration.bean.LdapUser;
import com.actionsoft.apps.cmcc.integration.constant.WorkFlowAPIConstant;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.actionsoft.sdk.service.ORGApi;
import com.alibaba.fastjson.JSONObject;

public class LdapUtil {
	public static void main(String[] args) throws NamingException {
		String url = "ldap://10.1.5.179:389/";
		String basedn = "dc=chinamobile,dc=com"; // basedn
		String root = "cn=ITSM,ou=apps,ou=account,dc=chinamobile,dc=com"; // 用户
		String pwd = "175w$0430"; // pwd
		LdapContext ctx = ldapConnect(url, root, pwd);//ldap认证
		List<LdapUser> users = readLdap(ctx, basedn);//获取ldap中用户信息
		
	}
	
	public static String SyncVPNUser(){
		String url = "ldap://10.1.5.179:389/";
		String basedn = "dc=chinamobile,dc=com"; // basedn
		String root = "cn=ITSM,ou=apps,ou=account,dc=chinamobile,dc=com"; // 用户
		String pwd = "175w$0430"; // pwd
		LdapContext ctx = ldapConnect(url, root, pwd);//ldap认证
		List<LdapUser> users = readLdap(ctx, basedn);//获取ldap中用户信息
		for (LdapUser ldapUser : users) {
			String superviseDept = ldapUser.getO();
			String email = ldapUser.getMail();
			String zhcnName = ldapUser.getCn();
			String employeeType = ldapUser.getEmployeeType();
			String supporterCorpName = ldapUser.getOu();
			String mobile = ldapUser.getMobile();
			String gander = ldapUser.getGender();
			String status = ldapUser.getStatus();
			if(employeeType.equals("2")){//外协人员参加同步
				//新增或更新部门
				String departmentId =insertOrUpdateDept(superviseDept);
				JSONObject ext5 =new JSONObject();
				ext5.put("employeeType", employeeType);
				ext5.put("supporterCorpName", supporterCorpName);
				ext5.put("gander", gander);
				ext5.put("status", status);
				if(status.equals("0")){
					//新增或更新人员
					insertOrUpdateUser(email,departmentId,superviseDept,zhcnName,mobile,ext5.toString());
				}else{
					//注销人员
					disabledUser(email);
				}
			}
			
		}
		return "";
	}
	
	public static void disabledUser(String uid){
		UserModel model = UserCache.getModel(uid);
		if(null ==model){
			//空，则不处理
		}else{
			SDK.getORGAPI().disabledUser(uid);
		}
	}

	/**
	 * 获取ldap认证
	 * 
	 * @param url
	 * @param basedn
	 * @param root
	 * @param pwd
	 * @return
	 */
	public static LdapContext ldapConnect(String url, String root, String pwd) {
		String factory = "com.sun.jndi.ldap.LdapCtxFactory";
		String simple = "simple";
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, simple);
		env.put(Context.SECURITY_PRINCIPAL, root);
		env.put(Context.SECURITY_CREDENTIALS, pwd);
		LdapContext ctx = null;
		Control[] connCtls = null;
		try {
			ctx = new InitialLdapContext(env, connCtls);
			System.err.println("认证成功:" + url);
		} catch (javax.naming.AuthenticationException e) {
			System.err.println("认证失败：");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("认证出错：");
			e.printStackTrace();
		}
		return ctx;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param ctx
	 * @param basedn
	 * @return
	 */
	public static List<LdapUser> readLdap(LdapContext ctx, String basedn) {
		List<LdapUser> lm = new ArrayList<LdapUser>();
		try {
			if (ctx != null) {
				// 过滤条件
				String filter = "(&(objectClass=*)(uid=*))";
				String[] attrPersonArray = { "zhcnName", "superviseDept", "userName", "email", "gender", "birthday",
						"mobile", "accountType", "employeeType", "supporterCorpName", "accountType" };
				SearchControls searchControls = new SearchControls();// 搜索控件
				searchControls.setSearchScope(2);// 搜索范围
				// 1.要搜索的上下文或对象的名称；2.过滤条件，可为null，默认搜索所有信息；3.搜索控件，可为null，使用默认的搜索控件
				NamingEnumeration<SearchResult> answer = ctx.search(basedn, filter.toString(), searchControls);
				int num = 0;
				while (answer.hasMore()) {
					SearchResult result = (SearchResult) answer.next();
					NamingEnumeration<? extends Attribute> attrs = result.getAttributes().getAll();
					LdapUser lu = new LdapUser();
					while (attrs.hasMore()) {
						Attribute attr = (Attribute) attrs.next();
						if ("cn".equals(attr.getID())) {
							lu.setCn(attr.get().toString());
						} else if ("mail".equals(attr.getID())) {
							lu.setMail(attr.get().toString());
						} else if ("mobile".equals(attr.getID())) {
							lu.setMobile(attr.get().toString());
						} else if ("status".equals(attr.getID())) {
							lu.setStatus(attr.get().toString());
						} else if ("employeeType".equals(attr.getID())) {
							lu.setEmployeeType(attr.get().toString());
						} else if ("positionLevel".equals(attr.getID())) {
							lu.setPositionLevel(attr.get().toString());
						} else if ("gender".equals(attr.getID())) {
							lu.setGender(attr.get().toString());
						} else if ("superviseDept".equals(attr.getID())) { //外协组织与正式员工key不一致
							lu.setO(attr.get().toString());
						} else if ("supporterCorpName".equals(attr.getID())) { //外协组织与正式员工key不一致
							lu.setOu(attr.get().toString());
						}else if ("Uid".equals(attr.getID())) { //外协组织与正式员工key不一致
							lu.setPositionLevel(attr.get().toString());
						}
					}
					if(lu.getEmployeeType().equals("2") && lu.getMail() != null){
						num++;
						lm.add(lu);
					}
				}
				System.err.println(">>>>>外协员工总数:"+num);
			}
		} catch (Exception e) {
			System.err.println(">>>>>>ldap获取用户信息异常:");
			e.printStackTrace();
		}
		return lm;
	}
	
	/**
	 * 新增或更新部门
	 * @param departmentId
	 * @return
	 */
	public static String insertOrUpdateDept(String departmentId){
		String isUpdate = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.SYNC_VPN_UPDATE);
		ORGAPI orgAPI = SDK.getORGAPI();
		//外协的部门编码增加外协WX标记
		String departmentIdWX = departmentId + "WX"; 
		DepartmentModel oldDeptModel = DepartmentCache.getModelOfOuterId(departmentIdWX);//外协部门是否存在
		DepartmentModel superviseDeptModel = DepartmentCache.getModelOfOuterId(departmentId);//父部门是否存在
		if(null == superviseDeptModel){
			return departmentId;
		}else{
			String name = superviseDeptModel.getName()+"外协组织";
			if (oldDeptModel == null) { // 新增
				DepartmentModel department = DepartmentCache.getModelOfOuterId(departmentIdWX);
				String companyId = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.SYNC_VPN_DEPT);
				departmentId = orgAPI.createDepartment(companyId, name, "", "", "0", "", "", "", "", "", "", departmentIdWX, "", "");
			}else{ //修改
				if(isUpdate.equals("true")){
					departmentId = oldDeptModel.getId();
					orgAPI.updateDepartment(departmentId, name);
				}
			}
			return departmentId;
		}
		
	}
	
	/**
	 * 新增或更新人员
	 * @param email
	 * @return
	 */
	public static String insertOrUpdateUser(String email,String departmentId,String superviseDept,String zhcnName,String mobile,String ext5){
		String isUpdate = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.SYNC_VPN_UPDATE);
		ORGAPI orgAPI = SDK.getORGAPI();
		String roleId =SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.KFWHTD_ROLEID);
		//同步账号
		UserModel model = UserCache.getModel(email);
		if(null == model){//新增
			int createUser = orgAPI.createUser(departmentId,email,zhcnName,"a2466571-b615-42bb-86b4-b9c9c15d6730","","",false,email,mobile,"",superviseDept,"","",ext5);
			orgAPI.createUserMap(email, departmentId, roleId, false, true);
		}else{ //修改
			Map<String,Object > map =new HashMap<String,Object>();
			map.put("departmentId", departmentId);
			map.put("ext2", superviseDept);
			map.put("ext5", ext5);
			map.put("email", email);
			map.put("roleId", "a2466571-b615-42bb-86b4-b9c9c15d6730");
			map.put("userName", zhcnName);
			List<UserMapModel> userMaps = orgAPI.getUserMaps(email);
			boolean flag =true;
			if(null !=userMaps && userMaps.size()>0){
				for (UserMapModel userMapModel : userMaps) {
					if(userMapModel.getRoleId().equals(roleId)){
						flag =false;
						break;
					}
				}
			}
			if(flag){
				orgAPI.createUserMap(email, departmentId, roleId, false, true);
			}
			if(isUpdate.equals("true")){
				orgAPI.updateUser(email,map);
			}
			
		}
		return "";
	}
}