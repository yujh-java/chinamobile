package com.actionsoft.apps.addons.ldapsync;

import javax.naming.NamingException;

import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;

/**
 * ldap sync interface
 * 
 * @author chengy
 *
 */
public abstract class LdapAdapter {

	public abstract UserModelImpl getUser(LdapUser user, UserModelImpl u, LdapConf conf) throws NamingException;

	public abstract DepartmentImpl getDepartment(LdapDept user, DepartmentImpl u, LdapConf conf) throws NamingException;

}
