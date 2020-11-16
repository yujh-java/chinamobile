package com.actionsoft.apps.addons.ldapsync.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.apps.addons.ldapsync.util.SearchUtil;
import com.actionsoft.apps.addons.ldapsync.util.SyncUtil;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;

public class IncSync extends FullSync {

	private Date startTime;
	private String delCtls;

	public IncSync(LdapConf conf, Date startTime) {
		super(conf);
		this.startTime = startTime;
	}

	private List<String> getDelDepts() throws NamingException {
		List<String> list = new ArrayList<String>();
		String f = qfilter(ldapConf.getDeptFilterDel());
		LdapContext ctx = SearchUtil.getDirContext(ldapConf.getCC());
		befSearch(ctx);
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ctx, ldapConf.getRDNDel(), f, SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapDept ldapDept = new LdapDept(attributes, searchResult.getNameInNamespace());
				String key = ldapDept.getLdapVal(ldapConf.getDepartmentLdapAttr());
				list.add(key);
				staticUtil.addDelDeptNum();
			}
		}
		return list;
	}

	private void befSearch(LdapContext ctx) throws NamingException {
		if (!UtilString.isEmpty(getDelCtls())) {
			String[] ss = getDelCtls().split(",");

			List<Control> cs = new ArrayList<Control>();
			for (String s : ss) {
				cs.add(new BasicControl(s));
			}
			ctx.setRequestControls(cs.toArray(new Control[0]));
		} else {
			if (ldapConf.isAd()) {
				// delete object
				ctx.setRequestControls(new Control[] { new BasicControl("1.2.840.113556.1.4.417") });
			}
		}
	}

	private List<String> getDelUsers() throws NamingException {
		List<String> list = new ArrayList<String>();
		String f = qfilter(ldapConf.getUserFilterDel());
		LdapContext ctx = SearchUtil.getDirContext(ldapConf.getCC());
		befSearch(ctx);
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ctx, ldapConf.getRDNDel(), f, SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapDept ldapDept = new LdapDept(attributes, searchResult.getNameInNamespace());
				String key = ldapDept.getLdapVal(ldapConf.getUserLdapAttr());
				list.add(key);
				staticUtil.addDelUserNum();
			}
		}
		return list;
	}

	public void sync() throws NamingException {
		tmp = new HashMap<String, Integer>();
		log("\n\n\nLDAP增量同步开始......");
		String filter = ldapConf.getRootFilter();
		if (UtilString.isEmpty(filter)) {
			filter = ldapConf.getDeptFilter();
		}
		log("搜索根部门[context = " + ldapConf.getDepartmentRDN() + "][filter = " + filter + "]开始...");
		List<LdapDept> rootLdap = searchRootDept(filter);
		log("搜索根部门[" + staticUtil.getSearchRootCount() + "]结束");

		log("搜索部门[增][context = " + ldapConf.getDepartmentRDN() + "][filter = " + getIncDeptFileter() + "]开始...");
		final Map<String, List<LdapDept>> deptMap = getDeptsInc(); // 加载从ldap查询出来的所有部门列表
		log("搜索部门[" + staticUtil.getTotalDeptCount() + "]结束");

		log("搜索人员[增][context = " + ldapConf.getUserRDN() + "][filter = " + getIncUserFileter() + "]开始...");
		final Map<String, List<LdapUser>> userMap = getUsersInc(); // 加载从ldap查询出来的所有人员列表
		log("搜索人员[" + staticUtil.getTotalUserCount() + "]结束");

		List<String> delDepts = null;
		if (!UtilString.isEmpty(ldapConf.getDeptFilterDel())) {
			log("搜索部门[被删][context = " + ldapConf.getRDNDel() + "][filter = " + qfilter(ldapConf.getDeptFilterDel()) + "]开始...");
			delDepts = getDelDepts();
			log("搜索被删部门[" + staticUtil.getDelDeptNum() + "]结束");
		}

		List<String> delUsers = null;
		if (!UtilString.isEmpty(ldapConf.getUserFilterDel())) {
			log("搜索人员[被删][context = " + ldapConf.getRDNDel() + "][filter = " + qfilter(ldapConf.getUserFilterDel()) + "]开始...");
			delUsers = getDelUsers();
			log("搜索人员[" + staticUtil.getDelUserNum() + "]结束");
		}

		final Set<String> ldapDeptOuterIds = new HashSet<String>();
		final Set<String> ldapUserOuterIds = new HashSet<String>();
		String deptId = deptConfig.get("targetDepartment");

		try {
			// 有可能新增了根部门
			startThreadPool(ldapConf.getMaxConc());
			Map<String, List<LdapDept>> x = new HashMap<>(deptMap);
			Map<String, List<LdapUser>> y = new HashMap<>(userMap);
			for (LdapDept currentDept : rootLdap) {
				syncDept(currentDept, deptMap, userMap, deptId, ldapDeptOuterIds, ldapUserOuterIds);
			}

			Map<String, String> seachMapping = null;
			if (LdapSyncConstant.PARENT_DN.equals(ldapConf.getDepartmentParentLdapAttr())) {
				seachMapping = searchDeptForInc();
			}

			boolean loop = false;
			do {
				loop = false;
				for (Iterator<Entry<String, List<LdapDept>>> iterator = x.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, List<LdapDept>> t = iterator.next();
					String outerId = t.getKey();
					if (!deptMap.containsKey(outerId)) {
						continue;
					}

					List<LdapDept> vs = t.getValue();
					DepartmentModel dm = DepartmentCache.getModelOfOuterId(seachMapping != null ? seachMapping.get(outerId) : outerId);
					if (dm != null) {
						final String pid = dm.getId();
						for (final LdapDept tDept : vs) {
							submitTask(new Runnable() {
								public void run() {
									try {
										syncDept(tDept, deptMap, userMap, pid, ldapDeptOuterIds, ldapUserOuterIds);
									} catch (Exception e) {
										e.printStackTrace();
										IncSync.this.log(e);
									}
								}
							});
						}

						// 可能有新部门生成，因此多循环一轮，直到没有部门可以新建，认为是孤岛部门，无法同步
						iterator.remove();
						deptMap.remove(outerId);
						loop = true;
					}
				}
			} while (loop);

			/**
			 * 增量同步，人员可以处理，不提示同步特征信息
			 */
			for (Iterator<Entry<String, List<LdapUser>>> iterator = y.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, List<LdapUser>> e = iterator.next();
				String pid = e.getKey();
				if (!userMap.containsKey(pid)) {
					continue;
				}

				DepartmentModel dm = DepartmentCache.getModelOfOuterId(pid);
				if (dm != null) {
					for (final LdapUser ldapUser : e.getValue()) {
						final UserModelImpl model = ldapToModel.getUser(ldapUser, userConfig, dm.getId());
						if (model == null) {
							log("人员[" + ldapUser.getName() + "] ID属性不正确");
							continue;
						}

						submitTask(new Runnable() {
							public void run() {
								try {
									SyncUtil.syncUserModel(ldapUser, model, ldapUserOuterIds, staticUtil, ldapConf, IncSync.this);
								} catch (Exception e) {
									e.printStackTrace();
									IncSync.this.log(e);
								}
							}
						});
					}

					iterator.remove();
					userMap.remove(pid);
				}
			}

			if (!UtilString.isEmpty(delUsers)) {
				for (String outerId : delUsers) {
					final UserModel dm = UserCache.getModelByOuterId(outerId);
					if (dm == null) {
						staticUtil.addDelUsercount();
					} else {
						submitTask(new Runnable() {
							@Override
							public void run() {
								try {
									SDK.getORGAPI().removeUser(dm.getUID());
									log("删除人员[" + dm.getUID() + "]");
								} catch (Exception e) {
									e.printStackTrace();
									IncSync.this.log(e);
								}
								staticUtil.addDelUsercount();
							}
						});
					}
				}
			}

			if (!UtilString.isEmpty(delDepts)) {
				safeDeleteDept(delDepts, true);
			}
		} finally {
			stopThreadPool();
		}

		safeDeleteDept(deleteDeptsByClose, false);
		logAlone(deptMap, userMap);
		log("LDAP增量同步结束\n\n\n");
	}

	private Map<String, List<LdapDept>> getDeptsInc() throws NamingException {
		Map<String, List<LdapDept>> depts = new HashMap<String, List<LdapDept>>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getDepartmentRDN(), getIncDeptFileter(), SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapDept ldapDept = new LdapDept(attributes, searchResult.getNameInNamespace());
				String key = ldapDept.getLdapVal(ldapConf.getDepartmentParentLdapAttr()).toUpperCase();
				if (UtilString.isEmpty(key)) {
					log("[部门][" + ldapDept.getName() + "]属性[" + ldapConf.getDepartmentParentLdapAttr() + "]为空");
					continue;
				}

				List<LdapDept> newDepts = depts.get(key);
				if (newDepts == null) {
					newDepts = new ArrayList<LdapDept>();
					depts.put(key, newDepts);
				}

				newDepts.add(ldapDept);
				staticUtil.addTotalDeptCount();
			}
		}

		return depts;
	}

	private Map<String, List<LdapUser>> getUsersInc() throws NamingException {
		Map<String, List<LdapUser>> depts = new HashMap<String, List<LdapUser>>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getUserRDN(), getIncUserFileter(), SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapUser ldapUser = new LdapUser(attributes, searchResult.getNameInNamespace());
				String key = ldapUser.getLdapVal(ldapConf.getUserLdapDepartmentAttr()).toUpperCase();
				if (UtilString.isEmpty(key)) {
					log("[人员][" + ldapUser.getName() + "]属性[" + ldapConf.getUserLdapDepartmentAttr() + "]为空");
					continue;
				}

				List<LdapUser> newUsers = depts.get(key);
				if (newUsers == null) {
					newUsers = new ArrayList<LdapUser>();
					depts.put(key, newUsers);
				}
				newUsers.add(ldapUser);
				staticUtil.addTotalUserCount();
			}
		}

		return depts;
	}

	private Map<String, String> searchDeptForInc() throws NamingException {
		Map<String, String> depts = new HashMap<String, String>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getDepartmentRDN(), ldapConf.getDeptFilter(), SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapUser ldapUser = new LdapUser(attributes, searchResult.getNameInNamespace());
				String id = ldapUser.getLdapVal(ldapConf.getDepartmentLdapAttr());
				String v = ldapUser.getLdapVal(LdapSyncConstant.DN).toUpperCase();
				depts.put(v, id);
			}
		}

		return depts;
	}

	private String getIncDeptFileter() {
		String s = ldapConf.getDeptFilterInc();
		if (startTime != null) {
			s = qfilter(s);
		}

		return s;
	}

	private String getIncUserFileter() {
		String s = ldapConf.getUserFilterInc();
		if (startTime != null) {
			s = qfilter(s);
		}

		return s;
	}

	private String qfilter(String s) {
		if (startTime == null) {
			return s;
		}

		String pstr = Pattern.quote("$st(") + "(.+?)" + Pattern.quote(")");
		Pattern p = Pattern.compile(pstr);
		Matcher m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String g1 = m.group(1);
			m.appendReplacement(sb, UtilDate.datetimeFormat(startTime, g1));
		}

		m.appendTail(sb);
		return sb.toString();
	}

	public String getDelCtls() {
		return delCtls;
	}

	public void setDelCtls(String delCtls) {
		this.delCtls = delCtls;
	}

}
