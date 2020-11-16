package com.actionsoft.apps.addons.ldapsync.dao;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;


public interface LdapToModelDao {
	
	
	/**
	 * 从ldap实体解析出账户
	 * 
	 * @param attribute
	 * @return
	 */
	UserModelImpl getUser(LdapUser user, Map<String, String> userConf, String parentId) throws NamingException;
	
	/**
	 * 解析ldap值设置到UserModel的prop属性
	 * 
	 * @param prop :userModel 屬性,
	 * @param attribute: ldap 屬性
	 */
	String getUserProperty(String prop, Attributes attribute) throws NamingException;
	
	/**
	 * 从ldap实体解析出部门
	 */
	DepartmentImpl getDepartment(LdapDept dept, Map<String, String> deptConf, String parentId) throws NamingException;
	
	/**
	 * 解析ldap值设置到DepartmentModel的prop属性
	 * 
	 * @param prop:DepartmentModel属性
	 */
	String getDepartmentProperty(String prop, Attributes attribute) throws NamingException;
}
