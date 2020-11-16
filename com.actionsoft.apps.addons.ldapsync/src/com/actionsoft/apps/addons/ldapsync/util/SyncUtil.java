package com.actionsoft.apps.addons.ldapsync.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.apps.addons.ldapsync.model.MapModel;
import com.actionsoft.apps.addons.ldapsync.model.StaticModel;
import com.actionsoft.apps.addons.ldapsync.search.Loggable;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.dao.OrgDaoFactory;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilSerialize;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class SyncUtil {

	/**
	 * 新增或更新部门
	 * 
	 * @param departList
	 */
	public static String syncDeptModel(LdapDept currentDept, DepartmentImpl departmentModelImpl, Set<String> deptOuterIds, StaticModel staticUtil, LdapConf ldapConf, Loggable logger, Set<String> deleteDeptsByClose) {
		// 筛选出多余的部门，为同步删除做准备
		if (deptOuterIds != null && deptOuterIds.size() > 0) {
			deptOuterIds.remove(departmentModelImpl.getOuterId());
		}

		// 判断AWS是否存在该部门,不存在则新建,存在则更新
		DepartmentModel oldDeptModel = DepartmentCache.getModelOfOuterId(departmentModelImpl.getOuterId());
		if (oldDeptModel == null) { // 新增
			if (departmentModelImpl.isClosed()) {
				staticUtil.addIgnoreDept();
				logger.log("忽略注销部门[" + departmentModelImpl.getOuterId() + "/" + departmentModelImpl.getName() + "]");
			} else {
				try {
					OrgDaoFactory.createDepartment().insert(departmentModelImpl);
					logger.log("新增部门[" + departmentModelImpl.getOuterId() + "]");
					syncDepartmentExt(true, departmentModelImpl.getId(), currentDept, ldapConf);
					staticUtil.addAddDeptcount();
				} catch (Exception e) {
					e.printStackTrace();
					logger.log("新增部门[" + departmentModelImpl.getOuterId() + "]失败");
					staticUtil.addFailAddDeptcount();
				}
			}
		} else { // 更新
			try {
				departmentModelImpl.setId(oldDeptModel.getId());
				boolean update = false;
				if (isClosed(departmentModelImpl)) {
					deleteDeptsByClose.add(oldDeptModel.getId());
					update = true;
				}

				if (isDeptChange(oldDeptModel, departmentModelImpl, ldapConf)) {
					OrgDaoFactory.createDepartment().update(departmentModelImpl);
					syncDepartmentExt(false, departmentModelImpl.getId(), currentDept, ldapConf);
					logger.log("更新部门[" + departmentModelImpl.getOuterId() + "]");
					update = true;
				} else {
					logger.log("部门[" + departmentModelImpl.getOuterId() + "]已存在");
				}
				if (update) {
					staticUtil.addUpdateDeptcount();
				} else {
					staticUtil.addKeepDept();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.log("更新部门[" + departmentModelImpl.getOuterId() + "]失败");
				staticUtil.addFailUpdateDeptcount();
			}

		}
		return departmentModelImpl.getId();
	}

	/**
	 * 新增或更新人员
	 * 
	 * @param userModels
	 */
	public static void syncUserModel(LdapUser ldapUser, UserModelImpl model, Set<String> userOuterIds, StaticModel staticUtil, LdapConf ldapConf, Loggable logger) {
		// 筛选出多余的人员，为同步删除做准备
		if (userOuterIds != null && userOuterIds.size() > 0) {
			userOuterIds.remove(model.getOuterId());
		}
		UserModel oldModel = UserCache.getModel(model.getUID());
		if (oldModel != null) { // 更新
			try {
				model.setUniqueId(oldModel.getUniqueId());
				boolean update = false;
				if (isClosedChanged(oldModel, model)) {
					if (model.isClosed()) {
						SDK.getORGAPI().disabledUser(model.getUID());
						logger.log("注销账户[" + model.getUID() + "]");
						update = true;
					} else {
						SDK.getORGAPI().activateUser(model.getUID());
						logger.log("激活账户[" + model.getUID() + "]");
						update = true;
					}
				}

				if (isUserChange(oldModel, model, ldapConf)) {
					OrgDaoFactory.createUser().update(model);
					syncUserExt(false, model.getUniqueId(), ldapUser, ldapConf);
					logger.log("更新人员[" + model.getOuterId() + "]");
					update = true;
				} else {
					logger.log("人员[" + model.getOuterId() + "]已存在");
				}
				if (update) {
					staticUtil.addUpdateUsercount();
				} else {
					staticUtil.addKeepUser();
				}
			} catch (Exception e) {
				e.printStackTrace();
				staticUtil.addFailUpdateUsercount();
				logger.log("更新人员[" + model.getOuterId() + "]失败");
			}
		} else { // 新增
			if (model.isClosed()) {
				staticUtil.addIgnoreUser();
				logger.log("忽略注销人员[" + model.getUID() + "]");
			} else {
				try {
					OrgDaoFactory.createUser().insert(model);
					syncUserExt(true, model.getUniqueId(), ldapUser, ldapConf);
					logger.log("新增人员[" + model.getOuterId() + "]");
					staticUtil.addAddUsercount();
				} catch (Exception e) {
					e.printStackTrace();
					staticUtil.addFailAddUsercount();
					logger.log("新增人员[" + model.getOuterId() + "]失败");
				}
			}
		}
	}

	private static void syncUserExt(boolean create, String uid, LdapUser ldap, LdapConf ldapConf) throws NamingException {
		Map<String, Object> ext = getUserExt(uid, ldap, ldapConf);
		if (UtilString.isEmpty(ext)) {
			return;
		}

		if (create) {
			ext.put(LdapConf.R_UID, uid);
			BO bo = new BO();
			bo.setAll(ext);
			SDK.getBOAPI().createDataBO(LdapConf.TN_EXT_USER, bo, null);
		} else {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + LdapConf.TN_EXT_USER + " SET ");
			List<String> sb = new ArrayList<String>();
			for (Entry<String, Object> e : ext.entrySet()) {
				sb.add(e.getKey() + "=:" + e.getKey());
			}

			sql.append(UtilString.join(sb, ","));
			sql.append(" WHERE " + LdapConf.R_UID + "=:" + LdapConf.R_UID);
			ext.put(LdapConf.R_UID, uid);
			DBSql.update(sql.toString(), ext);
		}
	}

	private static void syncDepartmentExt(boolean create, String deptId, LdapDept ldap, LdapConf ldapConf) throws NamingException {
		Map<String, Object> ext = getDepartmentExt(deptId, ldap, ldapConf);
		if (UtilString.isEmpty(ext)) {
			return;
		}

		if (create) {
			ext.put(LdapConf.R_DID, deptId);
			BO bo = new BO();
			bo.setAll(ext);
			SDK.getBOAPI().createDataBO(LdapConf.TN_EXT_DEPT, bo, null);
		} else {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + LdapConf.TN_EXT_DEPT + " SET ");
			List<String> sb = new ArrayList<String>();
			for (Entry<String, Object> e : ext.entrySet()) {
				sb.add(e.getKey() + "=:" + e.getKey());
			}

			sql.append(UtilString.join(sb, ","));
			sql.append(" WHERE " + LdapConf.R_DID + "=:" + LdapConf.R_DID);
			ext.put(LdapConf.R_DID, deptId);
			DBSql.update(sql.toString(), ext);
		}
	}

	private static Map<String, Object> getDepartmentExt(String deptId, LdapDept ldap, LdapConf ldapConf) throws NamingException {
		Map<String, String> extConf = ldapConf.getDeptExtMappingConfig();
		if (UtilString.isEmpty(extConf)) {
			return null;
		}

		Map<String, Object> bo = new HashMap<String, Object>();
		Attributes attributes = ldap.getAttributes();
		for (String ldapAttr : extConf.keySet()) {
			String awsAttr = extConf.get(ldapAttr);
			Attribute attribute = attributes.get(ldapAttr);
			if (attribute == null) {
				continue;
			}

			Object obj = attribute.get();
			if (obj != null) {
				obj = obj.toString();
			} else {
				obj = "";
			}
			bo.put(awsAttr, obj);
		}

		return bo;
	}

	private static Map<String, Object> getUserExt(String uid, LdapUser user, LdapConf ldapConf) throws NamingException {
		Map<String, String> extConf = ldapConf.getUserExtMappingConfig();
		if (UtilString.isEmpty(extConf)) {
			return null;
		}

		Map<String, Object> bo = new HashMap<String, Object>();
		Attributes attributes = user.getAttributes();
		for (String ldapAttr : extConf.keySet()) {
			String awsAttr = extConf.get(ldapAttr);
			Attribute attribute = attributes.get(ldapAttr);
			if (attribute == null) {
				continue;
			}

			Object obj = attribute.get();
			if (obj != null) {
				obj = obj.toString();
			} else {
				obj = "";
			}

			bo.put(awsAttr, obj);
		}
		return bo;
	}

	public static String statisticCount(StaticModel staticUtil) {
		return UtilSerialize.toJSONString(staticUtil);
	}

	private static boolean isDeptChange(DepartmentModel oldDept, DepartmentModel newDept, LdapConf ldapConf) {
		if (!eq(newDept.getCompanyId(), oldDept.getCompanyId())) {
			return true;
		}
		if (!eq(newDept.getParentDepartmentId(), oldDept.getParentDepartmentId())) {
			return true;
		}
		List<MapModel> mappers = ldapConf.getDepartmentMaps();
		for (MapModel mapper : mappers) {
			String awsAttr = mapper.getTo();
			if (DepartmentModelImpl.FIELD_DEPARTMENT_NAME.equals(awsAttr)) {
				if (!eq(oldDept.getName(), newDept.getName())) {
					return true;
				}
			} else if (DepartmentModelImpl.FIELD_EXT1.equals(awsAttr)) {
				if (!eq(oldDept.getExt1(), newDept.getExt1())) {
					return true;
				}
			} else if (DepartmentModelImpl.FIELD_EXT2.equals(awsAttr)) {
				if (!eq(oldDept.getExt2(), newDept.getExt2())) {
					return true;
				}
			} else if (DepartmentModelImpl.FIELD_ORDERINDEX.equals(awsAttr)) {
				if (oldDept.getOrderIndex() != newDept.getOrderIndex()) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isUserChange(UserModel oldUser, UserModel newUser, LdapConf ldapConf) {
		if (!eq(newUser.getDepartmentId(), oldUser.getDepartmentId())) {
			return true;
		}

		List<MapModel> mappers = ldapConf.getUserMaps();
		for (MapModel mapper : mappers) {
			String awsAttr = mapper.getTo();
			if (awsAttr.equals(UserModelImpl.FIELD_USER_ID)) {
				if (!eq(oldUser.getUID(), newUser.getUID())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_USER_NAME)) {
				if (!eq(oldUser.getUserName(), newUser.getUserName())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_USER_NO)) {
				if (!eq(oldUser.getUserNo(), newUser.getUserNo())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EMAIL)) {
				if (!eq(oldUser.getEmail(), newUser.getEmail())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_MOBILE)) {
				if (!eq(oldUser.getMobile(), newUser.getMobile())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_OFFICE_TEL)) {
				if (!eq(oldUser.getOfficeTel(), newUser.getOfficeTel())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_OFFICE_FAX)) {
				if (!eq(oldUser.getOfficeFax(), newUser.getOfficeFax())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_1)) {
				if (!eq(oldUser.getExt1(), newUser.getExt1())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_2)) {
				if (!eq(oldUser.getExt2(), newUser.getExt2())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_3)) {
				if (!eq(oldUser.getExt3(), newUser.getExt3())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_4)) {
				if (!eq(oldUser.getExt4(), newUser.getExt4())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_5)) {
				if (!eq(oldUser.getExt5(), newUser.getExt5())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_LAYER)) {
				if (!eq(oldUser.getPositionLayer(), newUser.getPositionLayer())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_NAME)) {
				if (!eq(oldUser.getPositionName(), newUser.getPositionName())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_NO)) {
				if (!eq(oldUser.getPositionNo(), newUser.getPositionNo())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_PASSWORD)) {
				if (!eq(oldUser.getPassword(), newUser.getPassword())) {
					return true;
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_WORK_STATUS)) {
				if (!eq(oldUser.getWorkStatus(), newUser.getWorkStatus())) {
					return true;
				}
			} else if (UserModelImpl.FIELD_ORDER_INDEX.equals(awsAttr)) {
				if (oldUser.getOrderIndex() != newUser.getOrderIndex()) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean isClosed(DepartmentImpl dept) {
		return dept.isClosed();
	}

	private static boolean isClosedChanged(UserModel oldUser, UserModel newUser) {
		return oldUser.isClosed() != newUser.isClosed();
	}

	private static boolean eq(String s1, String s2) {
		return s1 == s2 || s1.equals(s2);
	}

}
