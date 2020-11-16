package com.actionsoft.apps.cmcc.integration.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.actionsoft.apps.cmcc.integration.constant.WorkFlowAPIConstant;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 用户工具类
 * @author yujh
 *
 */
public class UserUtil {
	/**
	 * 校验用户是否为合法用户
	 * @param userId
	 * @return 返回合法用户
	 */
	public boolean validateUser(String userId){
		if(!UtilString.isEmpty(userId) && UserCache.getModel(userId)!=null
				&& UtilString.isEmpty(SDK.getORGAPI().validateUsers(userId))){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 同步外协账号
	 * @return
	 */
	public static String SyncVPNUser(){
		String url = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.SYNC_VPN_USER);
		System.err.println(">>>>url:"+url);
		String data = UtilURL.get(url);
		if(UtilString.isNotEmpty(data)){
			System.err.println(">>>jinlaile");
			JSONObject parseObject = JSONObject.parseObject(data);
			JSONArray jsonArray = parseObject.getJSONArray("externalPerson");
			if(jsonArray.size()>0){
				ORGAPI orgAPI = SDK.getORGAPI();
				/*{
			    "userName" : "w18101089057",
			    "fullName" : "于俊豪 于",
			    "zhcnName" : "于俊豪",
			    "email" : "1303893146@qq.com",
			    "gender" : 1,
			    "birthday" : 0,
			    "mobile" : "18101089057",
			    "starTime" : 1571108280000,
			    "endTime" : 1602644280000,
			    "password" : "HOgNP/XTjSX2IwtytugsoRmIZ+0=",
			    "status" : 0,
			    "employeeType" : 2,
			    "avatar" : "/w18101089057/avatar",
			    "changeFlag" : 1,
			    "supporterCorpName" : "北京炎黄盈动科技发展有限责任公司",
			    "superviseDept" : "00030087000800000000",
			    "supervisor" : "zuojing",
			    "accountType" : "项目外协",
			    "passwordEncrypted" : true
			  }*/
				System.err.println(">>>>同步开始，总计用户数据："+jsonArray.size());
				for(int i=0; i<jsonArray.size(); i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String zhcnName = jsonObject.getString("zhcnName");
					System.err.println(">>>>userName:"+zhcnName);
					String superviseDept = jsonObject.getString("superviseDept");
					String userName = jsonObject.getString("userName");
					String email = jsonObject.getString("email");
					String gender = jsonObject.getString("gender");
					String birthday = jsonObject.getString("birthday");
					String mobile = jsonObject.getString("mobile");
					String accountType = jsonObject.getString("accountType");
					String employeeType = jsonObject.getString("employeeType");
					String supporterCorpName = jsonObject.getString("supporterCorpName");
					//新增或更新部门
					String departmentId =insertOrUpdateDept(superviseDept);
					//新增或更新人员
					insertOrUpdateUser(email,departmentId,superviseDept,zhcnName,mobile,accountType,employeeType,supporterCorpName);
				}
				System.err.println(">>>>外协组织人员同步结束。");
			}
		}else{
			System.err.println(">>>>没有数据");
		}
		return "";
	}
	

	/**
	 * 新增或更新部门
	 * @param departmentId
	 * @return
	 */
	public static String insertOrUpdateDept(String departmentId){
		String url = SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.SYNC_VPN_USER);
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
	public static String insertOrUpdateUser(String email,String departmentId,String superviseDept,String zhcnName,String mobile,String accountType,String employeeType,String supporterCorpName){
		ORGAPI orgAPI = SDK.getORGAPI();
		String roleId =SDK.getAppAPI().getProperty(WorkFlowAPIConstant.APPID, WorkFlowAPIConstant.KFWHTD_ROLEID);
		JSONObject json =new JSONObject();
		json.put("accountType", accountType);
		json.put("employeeType", employeeType);
		json.put("supporterCorpName", supporterCorpName);
		System.err.println(">>>supporterCorpName:"+supporterCorpName);
		//同步账号
		UserModel model = UserCache.getModel(email);
		if(null == model){//新增
			int createUser = orgAPI.createUser(departmentId,email,zhcnName,"a2466571-b615-42bb-86b4-b9c9c15d6730","","",false,email,mobile,"",superviseDept,"","",json.toString());
			orgAPI.createUserMap(email, departmentId, roleId, false, true);
		}else{ //修改
			Map<String,Object > map =new HashMap<String,Object>();
			map.put("departmentId", departmentId);
			map.put("ext2", superviseDept);
			map.put("ext5", json.toString());
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
			//orgAPI.updateUser(email,map);
		}
		return "";
	}
	
	public static String getURL(){
		String fileName = "../webserver/webapps/workflow/formSnapShot/wxData.txt";
		String jsonStr = "";
		try {
			File jsonFile = new File(fileName);
			FileReader fileReader = new FileReader(jsonFile);
			Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
			int ch = 0;
			StringBuffer sb = new StringBuffer();
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}
			fileReader.close();
			reader.close();
			jsonStr = sb.toString();
			System.err.println(">>>>jsonStr:"+jsonStr);
			return jsonStr;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
		
}
