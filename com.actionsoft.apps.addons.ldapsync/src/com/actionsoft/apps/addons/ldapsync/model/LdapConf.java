package com.actionsoft.apps.addons.ldapsync.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;

import net.sf.json.JSONObject;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.lifecycle.api.AppsAPIManager;
import com.actionsoft.bpms.cc.Connector;
import com.actionsoft.bpms.commons.eai.ldap.server.LdapType;
import com.actionsoft.bpms.commons.eai.ldap.server.TypeDetector;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.server.conf.ConfigConst;
import com.actionsoft.bpms.util.UtilFile;
import com.actionsoft.bpms.util.UtilSerialize;
import com.actionsoft.bpms.util.UtilString;

public class LdapConf {

	public static final String EXT = "ext.";
	public static final String R_UID = "USERID";
	public static final String R_DID = "DEPARTMENTID";
	public static final String TN_EXT_USER = "BO_ACT_ORG_EXT_USER";
	public static final String TN_EXT_DEPT = "BO_ACT_ORG_EXT_DEPT";

	private JSONObject jo = new JSONObject();
	private LdapType ldapType = null;
	private Map<String, String> deptMappingConfig;
	private Map<String, String> userMappingConfig;
	private Map<String, String> deptExtMappingConfig;
	private Map<String, String> userExtMappingConfig;

	public LdapConf() {
		UtilFile utilFile = new UtilFile(LdapSyncConstant.filePath);
		if (utilFile.exists()) {
			String property = utilFile.readStrUTF8();
			if (!UtilString.isEmpty(property)) {
				jo = JSONObject.fromObject(property);
			}
		}

		initDeptMappingConfig();
		initUserMappingConfig();
	}

	public LdapConf(File f) {
		if (f != null && f.exists()) {
			String property = new UtilFile(f.getPath()).readStrUTF8();
			if (!UtilString.isEmpty(property)) {
				jo = JSONObject.fromObject(property);
			}
		}

		initDeptMappingConfig();
		initUserMappingConfig();
	}

	public LdapConf(String cc) {
		jo.put("cc_ldap", cc);
	}

	public String getId() {
		return jo.optString("id");
	}

	public String getCC() {
		return jo.optString("cc_ldap");
	}

	public LdapType getType() throws NamingException {
		if (ldapType == null) {
			synchronized (jo) {
				if (ldapType == null) {
					Connector.JNDI jndi = null;
					try {
						jndi = Connector.JNDI.binding(getCC());
						ldapType = TypeDetector.detectServerType(jndi.getRootDSE().getAttributes(""));
					} finally {
						if (jndi != null) {
							jndi.close();
						}
					}
				}
			}
		}
		return ldapType;
	}

	public boolean isAd() throws NamingException {
		return isAd(getType());
	}

	public boolean isAd(LdapType t) throws NamingException {
		return t.toString().toUpperCase().indexOf("ACTIVE_DIRECTORY") != -1;
	}

	private boolean isOpenLdap(LdapType t) {
		return t.toString().toUpperCase().indexOf("OPENLDAP") != -1;
	}

	private boolean isOracle(LdapType t) {
		return t.toString().toUpperCase().indexOf("ORACLE") != -1;
	}

	private boolean isOpenDS(LdapType t) {
		return t.toString().toUpperCase().indexOf("OPENDS") != -1;
	}

	private boolean isNOVELL(LdapType t) {
		return isLdap(t, "NOVELL");
	}

	private boolean isLdap(LdapType t, String s) {
		return t.toString().toUpperCase().indexOf(s) != -1;
	}

	public String getUserFilter() throws NamingException {
		String s = jo.optString("filter-user");
		if (!UtilString.isEmpty(s)) {
			return s;
		}

		String f = AppsAPIManager.getInstance().getProperty(LdapSyncConstant.ADDONS_IOX_APPID, "ldap.user.filter");
		if (!UtilString.isEmpty(f)) {
			return f;
		}

		LdapType t = getType();
		if (isAd(t)) {
			return "(&(objectclass=user)(!(objectclass=computer)))";
		} else if (isOpenLdap(t)) {
			return "(objectclass=person)";
		} else if (isOracle(t)) {
			return "(objectclass=person)";
		} else if (isOpenDS(t)) {
			return "(objectclass=person)";
		} else if (isNOVELL(t)) {
			return "(objectclass=person)";
		}

		return "(&(|(objectclass=user)(objectclass=person)(objectclass=inetOrgPerson) (objectclass=organizationalPerson))(!(objectclass=computer)))";
	}

	public String getDeptFilter() throws NamingException {
		String s = jo.optString("filter-dept");
		if (!UtilString.isEmpty(s)) {
			return s;
		}

		String f = AppsAPIManager.getInstance().getProperty(LdapSyncConstant.ADDONS_IOX_APPID, "ldap.dept.filter");
		if (!UtilString.isEmpty(f)) {
			return f;
		}

		LdapType t = getType();
		if (isAd(t)) {
			return DEF_FILTER_DEPT;
		}

		return "(|(objectclass=organization) (objectclass=organizationalUnit))";
	}

	private static final String DEF_FILTER_DEPT = "(objectClass=organizationalUnit)";

	public String getDepartmentRDN() {
		return jo.optString("dept_root_rdn");
	}

	public String getUserRDN() {
		return jo.optString("dept_user_rdn");
	}

	public String getRootFilter() {
		return jo.optString("dept_root_filter");
	}

	public String getSearchScope() {
		return jo.optString("searchScope");
	}

	public boolean isSyncByDN() {
		return jo.optBoolean("type_sync_dept");
	}

	public String getUserLdapAttr() {
		return jo.optString("mappingUserId");
	}

	public String getUserLdapDepartmentAttr() {
		return jo.optString("mappingUserDeptId");
	}

	public String getDepartmentLdapAttr() {
		return jo.optString("mappingDeptId");
	}

	public String getDepartmentParentLdapAttr() {
		return jo.optString("mappingParentDeptId");
	}

	public boolean isSyncToCompany() {
		return jo.optBoolean("targetDeptRdo");
	}

	public String getTargetCompany() {
		return jo.optString("target_company");
	}

	public String getTargetDepartment() {
		return jo.optString("target_department");
	}

	public String getRoleId() {
		return jo.optString("roleId");
	}

	public boolean isSyncDelete() {
		return jo.optBoolean("sync_delete");
	}

	public List<MapModel> getUserMaps() {
		try {
			return UtilSerialize.readJson(jo.optString("user_maps"), List.class, MapModel.class);
		} catch (Exception e) {
			return new ArrayList<MapModel>();
		}
	}

	public List<MapModel> getDepartmentMaps() {
		return UtilSerialize.readJson(jo.optString("dept_maps"), List.class, MapModel.class);
	}

	public String getDeptName() {
		return jo.optString("deptName");
	}

	public String getRoleName() {
		return jo.optString("roleName");
	}

	public String getUserDeptRelation() {
		return jo.optString("userDeptRelation");
	}

	public String getDeptFilterInc() {
		return jo.optString("filter-dept-inc");
	}

	public String getUserFilterInc() {
		return jo.optString("filter-user-inc");
	}

	public String getRDNDel() {
		return jo.optString("rdn_del");
	}

	public String getDeptFilterDel() {
		return jo.optString("filter-dept-del");
	}

	public String getUserFilterDel() {
		return jo.optString("filter-user-del");
	}

	/**
	 * 回调接口适配器
	 * 
	 * @return
	 */
	public String getSyncAdapter() {
		return jo.optString("sync-adapter");
	}

	/**
	 * 解析部门的ldap to aws 的配置属性
	 * 
	 * @return
	 */
	public Map<String, String> getDeptMappingConfig() {
		return deptMappingConfig;
	}

	private void initDeptMappingConfig() {
		Map<String, String> mappingConfig = new HashMap<String, String>();
		String targetDept = getTargetDepartment();
		String targetCompany = getTargetCompany();
		// 同步到部门
		if (!isSyncToCompany()) {
			if (!UtilString.isEmpty(targetDept)) {
				DepartmentModel d = DepartmentCache.getModel(targetDept);
				if (d != null) {
					targetCompany = DepartmentCache.getModel(targetDept).getCompanyId();
				}
			}
		} else {
			targetDept = ConfigConst.DEPARTMENT_PID;
		}
		mappingConfig.put("targetCompany", targetCompany);
		mappingConfig.put("targetDepartment", targetDept);
		mappingConfig.put(DepartmentModelImpl.FIELD_ID, getDepartmentLdapAttr());
		mappingConfig.put(DepartmentModelImpl.FIELD_PARENT_DEPARTMENT_ID, getDepartmentParentLdapAttr());
		List<MapModel> deptArray = getDepartmentMaps();
		for (MapModel jo : deptArray) {
			if (jo.getFrom() == null || jo.getTo() == null) {
				continue;
			}
			mappingConfig.put(jo.getFrom(), jo.getTo());
		}

		Map<String, String> t1 = new HashMap<String, String>();
		Map<String, String> t2 = null;
		for (Entry<String, String> e : mappingConfig.entrySet()) {
			if (e.getValue().startsWith(EXT)) {
				if (t2 == null) {
					t2 = new HashMap<String, String>();
				}
				t2.put(e.getKey(), e.getValue().substring(EXT.length()));
			} else {
				t1.put(e.getKey(), e.getValue());
			}
		}

		this.deptMappingConfig = t1;
		this.deptExtMappingConfig = t2;
	}

	public Map<String, String> getDeptExtMappingConfig() {
		return deptExtMappingConfig;
	}

	public Map<String, String> getUserMappingConfig() {
		return userMappingConfig;
	}

	/**
	 * 解析人员的ldap to aws 的配置属性
	 * 
	 * @return
	 */
	private void initUserMappingConfig() {
		Map<String, String> mappingConfig = new HashMap<String, String>();
		mappingConfig.put("roleId", getRoleId());
		mappingConfig.put(UserModelImpl.FIELD_USER_ID, getUserLdapAttr());
		mappingConfig.put(UserModelImpl.FIELD_DEPARTMENT_ID, getUserLdapDepartmentAttr());
		List<MapModel> deptArray = getUserMaps();
		for (MapModel jo : deptArray) {
			if (jo.getFrom() == null || jo.getTo() == null) {
				continue;
			}
			mappingConfig.put(jo.getFrom(), jo.getTo());
		}

		Map<String, String> t1 = new HashMap<String, String>();
		Map<String, String> t2 = null;
		for (Entry<String, String> e : mappingConfig.entrySet()) {
			if (e.getValue().startsWith(EXT)) {
				if (t2 == null) {
					t2 = new HashMap<String, String>();
				}
				t2.put(e.getKey(), e.getValue().substring(EXT.length()));
			} else {
				t1.put(e.getKey(), e.getValue());
			}
		}

		this.userMappingConfig = t1;
		this.userExtMappingConfig = t2;
	}

	public Map<String, String> getUserExtMappingConfig() {
		return userExtMappingConfig;
	}

	/**
	 * 同步时最大并发线程
	 * 
	 * @return
	 */
	public int getMaxConc() {
		int r = jo.optInt("maxConc");
		if (r <= 0 || r > 200) {
			r = 20;
		}
		return r;
	}

	public String toJson() {
		return jo.toString();
	}

}
