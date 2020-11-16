package com.actionsoft.apps.addons.ldapsync.constant;

import java.util.LinkedHashMap;
import java.util.Map;

import com.actionsoft.apps.lifecycle.api.AppsAPIManager;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;

public class LdapSyncConstant {
	
	public static final String DN = "DN";
	public static final String PARENT_DN = "ParentDN";

	public static final String ADDONS_IOX_APPID = "com.actionsoft.apps.addons.ldapsync";
	public static final String IAE_KEY = "ldap_sync_key";
	
	public static  final String filePath = AppsAPIManager.getInstance().getAppContext(LdapSyncConstant.ADDONS_IOX_APPID).getPath()+"conf.properties";

	public static Map<String, String> aws_deptAttrMap = new LinkedHashMap<String, String>();
	public static Map<String, String> aws_userAttrMap = new LinkedHashMap<String, String>();
	static{
		aws_deptAttrMap.put(DepartmentModelImpl.FIELD_DEPARTMENT_NAME, "部门名称");
		aws_deptAttrMap.put(DepartmentModelImpl.FIELD_EXT1, "扩展标记A");
		aws_deptAttrMap.put(DepartmentModelImpl.FIELD_EXT2, "扩展标记B");
		aws_deptAttrMap.put(UserModelImpl.FIELD_CLOSED, "部门注销");
		aws_deptAttrMap.put(DepartmentModelImpl.FIELD_ORDERINDEX, "排序");

		aws_userAttrMap.put(UserModelImpl.FIELD_USER_NAME, "姓名");
		aws_userAttrMap.put(UserModelImpl.FIELD_USER_NO, "编号");
		aws_userAttrMap.put(UserModelImpl.FIELD_EMAIL, "邮箱");
		aws_userAttrMap.put(UserModelImpl.FIELD_MOBILE, "电话");
		aws_userAttrMap.put(UserModelImpl.FIELD_OFFICE_TEL, "手机");
		aws_userAttrMap.put(UserModelImpl.FIELD_OFFICE_FAX, "传真");
		aws_userAttrMap.put(UserModelImpl.FIELD_EXTEND_1, "扩展标记1");
		aws_userAttrMap.put(UserModelImpl.FIELD_EXTEND_2, "扩展标记2");
		aws_userAttrMap.put(UserModelImpl.FIELD_EXTEND_3, "扩展标记3");
		aws_userAttrMap.put(UserModelImpl.FIELD_EXTEND_4, "扩展标记4");
		aws_userAttrMap.put(UserModelImpl.FIELD_EXTEND_5, "扩展标记5");
		aws_userAttrMap.put(UserModelImpl.FIELD_POSITION_LAYER, "职位编码");
		aws_userAttrMap.put(UserModelImpl.FIELD_POSITION_NAME, "职位名称");
		aws_userAttrMap.put(UserModelImpl.FIELD_POSITION_NO, "职务编号");
		aws_userAttrMap.put(UserModelImpl.FIELD_ORDER_INDEX, "排序");
		aws_userAttrMap.put(UserModelImpl.FIELD_CLOSED, "账户注销");
	}

}
