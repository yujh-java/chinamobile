package com.actionsoft.apps.addons.ldapsync;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;

public class LdapAdapterTest extends LdapAdapter {

	@Override
	public UserModelImpl getUser(LdapUser user, UserModelImpl u, LdapConf conf) throws NamingException {
		return null;
	}

	@Override
	public DepartmentImpl getDepartment(LdapDept user, DepartmentImpl u, LdapConf conf) throws NamingException {
		Attribute v = user.getAttributes().get("status");
		if (v != null && v.get() != null) {
			if ("1".equals(v.get().toString())) {
				u.setClosed(true);
			}
		}
		return null;
	}

}
