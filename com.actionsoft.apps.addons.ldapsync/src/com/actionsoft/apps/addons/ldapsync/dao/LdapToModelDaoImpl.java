package com.actionsoft.apps.addons.ldapsync.dao;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.actionsoft.apps.addons.ldapsync.LdapAdapter;
import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.apps.lifecycle.api.AppsAPIManager;
import com.actionsoft.bpms.commons.xmetadata.XDaoUtil;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.util.ClassReflect;
import com.actionsoft.bpms.util.UtilString;

public class LdapToModelDaoImpl implements LdapToModelDao {
	private LdapConf conf;
	private LdapAdapter ldapAdapter;

	public LdapToModelDaoImpl(LdapConf conf) {
		this.conf = conf;
		String f = AppsAPIManager.getInstance().getProperty(LdapSyncConstant.ADDONS_IOX_APPID, "ldap.adapter");
		if (UtilString.isEmpty(f)) {
			f = conf.getSyncAdapter();
		}
		if (!UtilString.isEmpty(f)) {
			try {
				ldapAdapter = (LdapAdapter) ClassReflect.getInstance(f, null, null, LdapSyncConstant.ADDONS_IOX_APPID);
			} catch (Exception e) {
				System.err.println("App[" + LdapSyncConstant.ADDONS_IOX_APPID + "] property [ldap.adapter] not " + LdapAdapter.class.getName() + ", err:" + e);
			}
		}
	}

	@Override
	public UserModelImpl getUser(LdapUser user, Map<String, String> userConf, String parentDepId) throws NamingException {
		Attributes attributes = user.getAttributes();
		String id = user.getLdapVal(conf.getUserLdapAttr());
		if (UtilString.isEmpty(id)) {
			return null;
		}
		UserModelImpl userModelImpl = new UserModelImpl();
		UserModelImpl u = (UserModelImpl) UserCache.getModelByOuterId(id);
		if (u != null) {
			userModelImpl.setModel(u);
		}
		for (String ldapAttr : userConf.keySet()) {
			String awsAttr = userConf.get(ldapAttr);
			Attribute attribute = attributes.get(ldapAttr);
			if (attribute == null) {
				continue;
			}
			String value = attribute.get().toString();
			if (awsAttr.equals(UserModelImpl.FIELD_USER_ID)) {
				userModelImpl.setUID(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_ORDER_INDEX)) {
				System.err.println("排序值为[" + value+ "]");
				try {
					userModelImpl.setOrderIndex(Integer.parseInt(value));
				} catch (NumberFormatException e) {
				}
			} else if (awsAttr.equals(UserModelImpl.FIELD_USER_NAME)) {
				userModelImpl.setUserName(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_USER_NO)) {
				userModelImpl.setUserNo(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EMAIL)) {
				userModelImpl.setEmail(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_MOBILE)) {
				userModelImpl.setMobile(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_OFFICE_TEL)) {
				userModelImpl.setOfficeTel(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_OFFICE_FAX)) {
				userModelImpl.setOfficeFax(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_1)) {
				userModelImpl.setExt1(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_2)) {
				userModelImpl.setExt2(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_3)) {
				userModelImpl.setExt3(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_4)) {
				userModelImpl.setExt4(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_EXTEND_5)) {
				userModelImpl.setExt5(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_LAYER)) {
				userModelImpl.setPositionLayer(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_NAME)) {
				userModelImpl.setPositionName(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_POSITION_NO)) {
				userModelImpl.setPositionNo(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_PASSWORD)) {
				userModelImpl.setPassword(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_WORK_STATUS)) {
				userModelImpl.setWorkStatus(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_CLOSED)) {
				if (conf.isAd()) {
					String addisable = ",514,546,66050,66082,262658,262690,328194,328226,";
					userModelImpl.setClosed(addisable.indexOf("," + value + ",") != -1);
				} else if (value.matches("\\d+")) {
					userModelImpl.setClosed(Integer.parseInt(value) == 0);
				}
			}
		}
		userModelImpl.setDepartmentId(parentDepId);
		userModelImpl.setOuterId(id);
		userModelImpl.setManager(false);
		userModelImpl.setRoleId(userConf.get("roleId"));
		if (ldapAdapter != null) {
			UserModelImpl t = ldapAdapter.getUser(user, userModelImpl, conf);
			if (t != null) {
				userModelImpl = t;
			}else{
				return null;
			}
		}
		return userModelImpl;
	}

	@Override
	public String getUserProperty(String prop, Attributes attribute) throws NamingException {
		Attribute att = attribute.get(prop);
		if (att != null) {
			return (String) att.get();
		}
		return "";
	}

	@Override
	public DepartmentImpl getDepartment(LdapDept dept, Map<String, String> deptConf, String parentId) throws NamingException {
		Attributes attributes = dept.getAttributes();
		String id = dept.getLdapVal(conf.getDepartmentLdapAttr());
		if (UtilString.isEmpty(id)) {
			return null;
		}
		DepartmentImpl departmentModelImpl = new DepartmentImpl();
		DepartmentModelImpl d = (DepartmentModelImpl) DepartmentCache.getModelOfOuterId(id);
		if (d != null) {
			departmentModelImpl.setModel(d);
		}

		for (String ldapAttr : deptConf.keySet()) {
			String awsAttr = deptConf.get(ldapAttr);
			Attribute attribute = attributes.get(ldapAttr);
			if (attribute == null) {
				continue;
			}
			String value = attribute.get().toString();
			if (awsAttr.equals(DepartmentModelImpl.FIELD_DEPARTMENT_NAME)) {
				departmentModelImpl.setName(value);
			} else if (awsAttr.equals(DepartmentModelImpl.FIELD_ORDERINDEX)) {
				try {
					departmentModelImpl.setOrderIndex(Integer.parseInt(value));
				} catch (NumberFormatException e) {
				}
			} else if (awsAttr.equals(DepartmentModelImpl.FIELD_EXT1)) {
				departmentModelImpl.setExt1(value);
			} else if (awsAttr.equals(DepartmentModelImpl.FIELD_EXT2)) {
				departmentModelImpl.setExt2(value);
			} else if (awsAttr.equals(UserModelImpl.FIELD_CLOSED)) {
				if (value.matches("\\d+")) {
					departmentModelImpl.setClosed(Integer.parseInt(value) == 0);
				}
			}
		}
		departmentModelImpl.setId(XDaoUtil.uuid());
		departmentModelImpl.setParentDepartmentId(parentId);
		departmentModelImpl.setCompanyId(deptConf.get("targetCompany"));
		departmentModelImpl.setOuterId(id);
		if (ldapAdapter != null) {
			DepartmentImpl t = ldapAdapter.getDepartment(dept, departmentModelImpl, conf);
			if (t != null) {
				departmentModelImpl = t;
			}
		}
		return departmentModelImpl;
	}

	@Override
	public String getDepartmentProperty(String prop, Attributes attribute) throws NamingException {
		Attribute att = attribute.get(prop);
		if (att != null) {
			return att.get().toString();
		}
		return "";
	}

}
