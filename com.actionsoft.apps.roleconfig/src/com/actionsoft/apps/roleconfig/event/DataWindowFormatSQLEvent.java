package com.actionsoft.apps.roleconfig.event;

import java.util.List;
import com.actionsoft.apps.roleconfig.constant.RoleConfigConstant;
import com.actionsoft.apps.roleconfig.util.PowerUtil;
import com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface;
import com.actionsoft.bpms.dw.exec.component.DataView;
import com.actionsoft.bpms.org.model.RoleModel;
import com.actionsoft.bpms.org.model.UserMapModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.sdk.local.SDK;
import com.actionsoft.sdk.local.api.ORGAPI;

/**
 * 格式化sql语句的触发器
 * 
 * @param me
 *            用户上下文
 * @param view
 *            dw视图对象
 * @param sql
 *            sql语句
 * @return 格式化好的sql语句 说明： 1.必须实现类
 *         com.actionsoft.bpms.dw.design.event.DataWindowFormatSQLEventInterface
 *         2.此示例实现的是 : 只有admin用户能够查询所有数据,其他用户只能查看自己创建的信息
 */
public class DataWindowFormatSQLEvent implements DataWindowFormatSQLEventInterface {

	public String formatSQL(UserContext me, DataView view, String sql) {
		StringBuffer sb=new StringBuffer(sql);
		String powerType = SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "ROLE_POWER_TYPE");
		ORGAPI orgapi = SDK.getORGAPI();
		if(powerType.equals(RoleConfigConstant.ROLE_TYPE)){//按角色组分类
			String roleKeyMatch = SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "ROLE_KEY_MATCH");
			List<UserMapModel> userMaps = orgapi.getUserMaps(me.getUID());
			if(!userMaps.isEmpty()){
				for (UserMapModel model : userMaps) {
					RoleModel roleModel = orgapi.getRoleById(model.getRoleId());
					String roleName = roleModel.getName();
					if(roleName.indexOf(roleKeyMatch)>0){//如果有关键字则拼接角色集合
						String categoryName = roleModel.getCategoryName();
						sb.append(" AND CATEGORYNAME ='").append(categoryName).append("'");
					}
				}
			}
		}else if(powerType.equals(RoleConfigConstant.EXT_TYPE)){//按人员扩展字段分组
			String matchKey=SDK.getAppAPI().getProperty(RoleConfigConstant.APPID, "EXT_MATCH");
			String userExt =matchKey.split(":")[0];//人员的扩展字段
			String roleExt =matchKey.split(":")[1];//角色的扩展字段
			PowerUtil power=new PowerUtil();
			String userKey = power.getEXTField(me, userExt);
			List<RoleModel> roles = orgapi.getRoles();
			for (RoleModel roleModel : roles) {
				String roleKey = power.getEXTField(roleModel, roleExt);
				if(userKey.equals(roleKey)){
					String roleId = roleModel.getId();
					sb.append(" AND ROLEID ='").append(roleId).append("'");
				}
			}
		}
		return sb.toString();

	}

}