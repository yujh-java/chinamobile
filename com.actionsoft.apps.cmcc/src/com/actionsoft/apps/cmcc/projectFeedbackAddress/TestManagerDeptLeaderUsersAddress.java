package com.actionsoft.apps.cmcc.projectFeedbackAddress;
/**
 * 一般委托流程
 * 参与者地址薄过滤事件,测试管理人员阅知
 * @author zhaoxs
 * @date 2017-06-21
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

public class TestManagerDeptLeaderUsersAddress implements AddressUIFilterInterface {
	private List<RowMap> listRowMap = new ArrayList<RowMap>();// 角色下所有人员
	private List<String> listDeptPathId = new ArrayList<String>();// 角色下所有人员部门全路径
	private String roleid = "";
	private String yjyDeptId = "";
	

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {
		Connection conn = DBSql.open();
		try {
			String deptpathIdofCache = model.getPathIdOfCache();// 过滤部门ID全路径
			if (UtilString.isEmpty(roleid)) {
				roleid = DBSql.getString(conn, "SELECT ID FROM ORGROLE where ROLENAME=?",
						new Object[] { CmccConst.deptCsglRolename });// 获取角色id
			}
			if (UtilString.isEmpty(listRowMap)) {
				listRowMap = DBSql.getMaps(conn, "SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?",
						new Object[] { roleid });
			}
			if (listRowMap != null && listRowMap.size() > 0) {
				for (int i = 0; i < listRowMap.size(); i++) {
					String userid = (String) listRowMap.get(i).get("USERID");
					String deptId = SDK.getORGAPI().getUser(userid).getDepartmentId();
					String deptPathId = DepartmentCache.getModel(deptId).getPathIdOfCache();
					listDeptPathId.add(deptPathId);
				}
			}
			// 00030087000000000000
			if (UtilString.isEmpty(yjyDeptId)) {
				yjyDeptId = DBSql.getString("SELECT ID FROM ORGDEPARTMENT WHERE OUTERID = ?",
						new Object[] { "00030087000000000000" });
			}
			String yjyDeptIdCache = DepartmentCache.getModel(yjyDeptId).getPathIdOfCache();
			if (yjyDeptIdCache.contains(deptpathIdofCache)) {
				return true;
			} else if (deptpathIdofCache.contains(yjyDeptIdCache)) {
				if (listDeptPathId != null && listDeptPathId.size() > 0) {
					for (int j = 0; j < listDeptPathId.size(); j++) {
						String userDeptpathId = listDeptPathId.get(j);
						if (userDeptpathId.contains(deptpathIdofCache)) {
							return true;
						}
					}
				}
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return false;

	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model, AdvancedAddressModel advancedAddressModel) {
		Connection conn = DBSql.open();
		try {
			String uid = model.getUID();// 获取用户id
			if (UtilString.isEmpty(roleid)) {
				roleid = DBSql.getString(conn, "SELECT ID FROM ORGROLE where ROLENAME=?",
						new Object[] { CmccConst.deptCsglRolename });// 获取角色id
			}
			if (UtilString.isEmpty(listRowMap)) {
				listRowMap = DBSql.getMaps(conn, "SELECT USERID FROM ORGUSERMAP WHERE ROLEID=?",
						new Object[] { roleid });

			}
			if (listRowMap != null && listRowMap.size() > 0) {
				for (int i = 0; i < listRowMap.size(); i++) {
					String USERID = (String) listRowMap.get(i).get("USERID");
					if (uid.equals(USERID)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			DBSql.close(conn);
		}
		return false;
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}
}