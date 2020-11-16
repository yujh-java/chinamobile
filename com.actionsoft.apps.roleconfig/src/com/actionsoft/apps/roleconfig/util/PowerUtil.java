package com.actionsoft.apps.roleconfig.util;

import java.util.HashMap;
import java.util.List;
import com.actionsoft.apps.roleconfig.constant.RoleConfigConstant;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.model.RoleModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 权限过滤工具类
 * @author Administrator
 *
 */
public class PowerUtil {
	/**
	 * 根据权限类型过滤角色
	 * @param powerType
	 * @return
	 */
	public String getRolesByPowerType(UserContext me,String powerType){
		ORGAPI orgapi = SDK.getORGAPI();
		JSONArray arrayData=new JSONArray();
		JSONObject jsonData=new JSONObject();
		if(powerType.equals(RoleConfigConstant.ROLE_TYPE)){//按角色组分类
			String roleKeyMatch = SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "ROLE_KEY_MATCH");
			List<UserMapModel> userMaps = orgapi.getUserMaps(me.getUID());
			if(!userMaps.isEmpty()){
				for (UserMapModel model : userMaps) {
					RoleModel roleModel = orgapi.getRoleById(model.getRoleId());
					String roleName = roleModel.getName();
					if(roleName.indexOf(roleKeyMatch)>0){//如果有关键字则拼接角色集合
						JSONArray array=new JSONArray();
						JSONObject json=new JSONObject();
						String adminRole = roleModel.getId();//管理员ID
						String categoryName = roleModel.getCategoryName();
						List<RoleModel> rolesByCategoryList = orgapi.getRolesByCategory(categoryName);
						for (RoleModel rolemodel_categoty : rolesByCategoryList) {
							if(!rolemodel_categoty.getId().equals(adminRole)){
								json.put("id",rolemodel_categoty.getId());
								json.put("text",rolemodel_categoty.getName());
								String str = json.toString().replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
								array.add(str);
							}
						}
						jsonData.put("children", array);
						jsonData.put("text", categoryName);
					}
				}
			}
		}else if(powerType.equals(RoleConfigConstant.EXT_TYPE)){//按人员扩展字段分组
			String matchKey=SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "EXT_MATCH");
			String userExt =matchKey.split(":")[0];//人员的扩展字段
			String roleExt =matchKey.split(":")[1];//角色的扩展字段
			String userKey = getEXTField(me, userExt);
			String categorySQL="SELECT CATEGORYNAME FROM ORGROLE WHERE 1=1 GROUP BY CATEGORYNAME";
			List<RowMap> maps = DBSql.getMaps(categorySQL, new HashMap<String, Object>());
			for (RowMap rowMap : maps) {
				boolean flag=false;
				JSONObject json=new JSONObject();
				JSONArray array=new JSONArray();
				List<RoleModel> rolesByCategory = orgapi.getRolesByCategory(rowMap.get("CATEGORYNAME").toString());
				for (RoleModel roleModel : rolesByCategory) {
					String roleKey = getEXTField(roleModel, roleExt);
					if(userKey.equals(roleKey)){
						flag=true;
						json.put("id",roleModel.getId());
						json.put("text",roleModel.getName());
						String str = json.toString().replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
						array.add(str);
					}
				}
				if(flag){
					jsonData.put("children", array);
					jsonData.put("text", rowMap.get("CATEGORYNAME"));
				}
			}
		}
		arrayData.add(jsonData);
		return arrayData.toString();
	}
	
	/**
	 * 获取扩展字段key值(人员)
	 * @param me
	 * @param userExt
	 * @return
	 */
	public String getEXTField(UserContext me,String userExt){
		String ext="";
		UserModel userModel = me.getUserModel();
		if(RoleConfigConstant.EXT1.equals(userExt)){
			ext=userModel.getExt1();
		}else if(RoleConfigConstant.EXT2.equals(userExt)){
			ext=userModel.getExt2();
		}else if(RoleConfigConstant.EXT3.equals(userExt)){
			ext=userModel.getExt3();
		}else if(RoleConfigConstant.EXT4.equals(userExt)){
			ext=userModel.getExt4();
		}else if(RoleConfigConstant.EXT5.equals(userExt)){
			ext=userModel.getExt5();
		}
		return ext;
	}
	
	/**
	 * 获取扩展字段key值(角色)
	 * @param model
	 * @param userExt
	 * @return
	 */
	public String getEXTField(RoleModel model,String roleExt){
		String ext="";
		if(RoleConfigConstant.EXT1.equals(roleExt)){
			ext=model.getExt1();
		}else if(RoleConfigConstant.EXT2.equals(roleExt)){
			ext=model.getExt2();
		}else if(RoleConfigConstant.EXT3.equals(roleExt)){
			ext=model.getExt3();
		}else if(RoleConfigConstant.EXT4.equals(roleExt)){
			ext=model.getExt4();
		}else if(RoleConfigConstant.EXT5.equals(roleExt)){
			ext=model.getExt5();
		}
		return ext;
	}
}
