package com.actionsoft.apps.addons.ldapsync.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

import com.actionsoft.bpms.cc.Connector;

public class SearchUtil {

	public static String getParentDN(String DN) throws InvalidNameException {
		LdapName ldapName = new LdapName(DN);
		ldapName.remove(ldapName.size() - 1);
		return ldapName.toString().toUpperCase();
	}

	// 得到执行目录操作的初始上下文。
	public static LdapContext getDirContext(String cc) throws NamingException {
		Connector.JNDI connector = Connector.JNDI.binding(cc);
		LdapContext ldapContext = (LdapContext) connector.getContext();
		ldapContext.addToEnvironment("java.naming.ldap.attributes.binary", "objectGUID objectSid");
		return ldapContext;
	}

	public static void close(LdapContext ctx) {
		try {
			ctx.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public static NamingEnumeration<SearchResult> getResult(LdapContext ldapContext, String searchBase, String searchFilter, SearchControls searchControls) throws NamingException {
		NamingEnumeration<SearchResult> resultEnumeration = ldapContext.search(searchBase, searchFilter, searchControls);
		close(ldapContext);
		return resultEnumeration;
	}

	public static NamingEnumeration<SearchResult> getResult(String cc, String searchBase, String searchFilter, SearchControls searchControls) throws NamingException {
		LdapContext ldapContext = getDirContext(cc);
		return getResult(ldapContext, searchBase, searchFilter, searchControls);
	}

	public static SearchControls createSearchControls(int scope) {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(scope);
		return searchControls;
	}

	/**
	 * 查询ldap 组织单元属性
	 * 
	 * @return
	 */
	public static Collection getLdapDepartmentAttList(NamingEnumeration<SearchResult> result) {
		Set set = new HashSet();
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes atts = searchResult.getAttributes();
			NamingEnumeration ne = atts.getIDs();
			while (ne.hasMoreElements()) {
				set.add(ne.nextElement().toString());
			}
		}

		List<String> s = new ArrayList<String>(set);
		Collections.sort(s);
		return s;
	}

	/**
	 * 查询ldap person 属性
	 * 
	 * @return
	 */
	public static Collection getLdapUserAttList(NamingEnumeration<SearchResult> result) {
		LinkedHashSet set = new LinkedHashSet();
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes atts = searchResult.getAttributes();
			NamingEnumeration ne = atts.getIDs();
			while (ne.hasMoreElements()) {
				set.add(ne.nextElement().toString());
			}
		}

		List<String> s = new ArrayList<String>(set);
		Collections.sort(s);
		return s;
	}

}
