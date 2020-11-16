package com.actionsoft.apps.addons.ldapsync.model;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.util.SearchUtil;

public class LdapUser {

	private Attributes attributes;
	private String name;

	public LdapUser(Attributes attributes, String name) {
		this.attributes = attributes;
		this.name = name;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String nameSpace) {
		this.name = nameSpace;
	}

	public String getLdapVal(String property) throws NamingException {
		if (LdapSyncConstant.DN.equals(property)) {
			return this.getName().toUpperCase();
		} else if (LdapSyncConstant.PARENT_DN.equals(property)) {
			return getParentDN();
		}

		Attribute att = attributes.get(property);
		if (att != null) {
			return att.get().toString();
		}
		return "";
	}

	private String getParentDN() throws InvalidNameException {
		return SearchUtil.getParentDN(this.getName());
	}
}
