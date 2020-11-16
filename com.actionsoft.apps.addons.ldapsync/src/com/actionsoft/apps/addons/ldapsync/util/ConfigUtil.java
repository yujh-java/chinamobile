package com.actionsoft.apps.addons.ldapsync.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.util.UtilString;

public class ConfigUtil {

	public static LdapConf getConf() {
		return new LdapConf();
	}

	public static String getUserFilter(String cc) throws NamingException {
		return new LdapConf(cc).getUserFilter();
	}

	public static String getUserFilter(LdapConf ldapConf) throws NamingException {
		return ldapConf.getUserFilter();
	}

	public static String getDeptFilter(String cc) throws NamingException {
		return new LdapConf(cc).getDeptFilter();
	}

	public static String getDeptFilter(LdapConf ldapConf) throws NamingException {
		return ldapConf.getDeptFilter();
	}

	// 获取从ldap同步过来的部门
	public static List<DepartmentModelImpl> getLdapDeptListFromOrg(String companyId) {
		List<DepartmentModelImpl> deptList = new ArrayList<DepartmentModelImpl>();
		List<DepartmentModel> departmentModels = DepartmentCache
				.getListOfCompany(companyId);
		for (DepartmentModel departmentModel : departmentModels) {
			// 如果outerId不为空,说明是从ldap同步过来的
			if (!UtilString.isEmptyByTrim(departmentModel.getOuterId())) {
				deptList.add((DepartmentModelImpl) departmentModel);
			}
		}
		return deptList;
	}

	// 获取从ldap同步过来的人员列表
	public static List<UserModelImpl> getLdapUserListFromOrg(String departmentId) {
		List<UserModelImpl> userModels = new ArrayList<UserModelImpl>();
		Iterator<UserModel> iterator = UserCache
				.getUserListOfDepartment(departmentId);
		while (iterator.hasNext()) {
			UserModel userModel = (UserModel) iterator.next();
			// 如果outerId不为空,说明是从ldap同步过来的
			if (!UtilString.isEmptyByTrim(userModel.getOuterId())) {
				userModels.add((UserModelImpl) userModel);
			}
		}
		return userModels;
	}

}
