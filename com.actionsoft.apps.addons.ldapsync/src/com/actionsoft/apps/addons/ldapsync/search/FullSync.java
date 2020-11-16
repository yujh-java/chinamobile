package com.actionsoft.apps.addons.ldapsync.search;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;

import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.dao.LdapToModelDaoImpl;
import com.actionsoft.apps.addons.ldapsync.log.LdapSyncLog;
import com.actionsoft.apps.addons.ldapsync.model.DepartmentImpl;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.LdapDept;
import com.actionsoft.apps.addons.ldapsync.model.LdapUser;
import com.actionsoft.apps.addons.ldapsync.model.StaticModel;
import com.actionsoft.apps.addons.ldapsync.util.ConfigUtil;
import com.actionsoft.apps.addons.ldapsync.util.SearchUtil;
import com.actionsoft.apps.addons.ldapsync.util.SyncUtil;
import com.actionsoft.apps.lifecycle.log.RoundQueue;
import com.actionsoft.bpms.commons.cache.iae.IAECache;
import com.actionsoft.bpms.commons.cache.iae.model.IAEModel;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.conf.server.AWSServerConf;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSIllegalArgumentException;
import com.actionsoft.exception.ExceptionUtil;
import com.actionsoft.sdk.local.SDK;
import com.alibaba.fastjson.JSONObject;

public class FullSync implements Loggable {
	protected LdapConf ldapConf;
	protected Map<String, String> deptConfig;
	protected Map<String, String> userConfig;
	protected LdapToModelDaoImpl ldapToModel;
	public static StaticModel staticUtil;
	private ThreadPoolExecutor executorService;
	protected static Logger logger = Logger.getLogger(LdapSyncConstant.ADDONS_IOX_APPID);
	private AtomicInteger finishSync = new AtomicInteger();
	private IAEModel iaeModel = null;
	private Integer rootDNPosn = null;
	protected Set<String> deleteDeptsByClose = new HashSet<String>();

	public FullSync(LdapConf conf) {
		this.ldapConf = conf;
		deptConfig = conf.getDeptMappingConfig();
		userConfig = conf.getUserMappingConfig();
		ldapToModel = new LdapToModelDaoImpl(conf);
		staticUtil = new StaticModel();
	}

	public void log(Object msg, boolean q) {
		if (q) {
			queue.add(msg);
		}
		logger.info(msg);
	}

	public void log(Object msg) {
		log(msg, true);
	}

	/**
	 * 查询ldap所有的部门
	 * 
	 * @return key:部门dn ,value:该部门下的子部门
	 * @throws NamingException
	 */
	private Map<String, List<LdapDept>> getDepts() throws NamingException {
		Map<String, List<LdapDept>> depts = new HashMap<String, List<LdapDept>>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getDepartmentRDN(), ldapConf.getDeptFilter(), SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
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
				if (depts.get(key) != null && depts.get(key).size() > 0) {
					depts.get(key).add(ldapDept);
				} else {
					List<LdapDept> newDepts = new ArrayList<LdapDept>();
					newDepts.add(ldapDept);
					depts.put(key, newDepts);
				}

				staticUtil.addTotalDeptCount();
			}
		}

		return depts;
	}

	/**
	 * 查询ldap所有的人员 ,
	 * 
	 * @return key:部门dn ,value:该部门下的人员
	 * @throws NamingException
	 */
	private Map<String, List<LdapUser>> getUsers() throws NamingException {
		Map<String, List<LdapUser>> depts = new HashMap<String, List<LdapUser>>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getUserRDN(), ldapConf.getUserFilter(), SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE));
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
				if (depts.get(key) != null && depts.get(key).size() > 0) {
					depts.get(key).add(ldapUser);
				} else {
					List<LdapUser> newUsers = new ArrayList<LdapUser>();
					newUsers.add(ldapUser);
					depts.put(key, newUsers);
				}

				staticUtil.addTotalUserCount();
			}
		}
		return depts;
	}

	/**
	 * 查询根部门
	 * 
	 * @return
	 * @throws NamingException
	 */
	protected List<LdapDept> searchRootDept(String filter) throws NamingException {
		List<LdapDept> lists = new ArrayList<LdapDept>();
		NamingEnumeration<SearchResult> result = SearchUtil.getResult(ldapConf.getCC(), ldapConf.getDepartmentRDN(), filter, SearchUtil.createSearchControls(Integer.parseInt(ldapConf.getSearchScope())));
		while (result.hasMoreElements()) {
			SearchResult searchResult = (SearchResult) result.nextElement();
			Attributes attributes = searchResult.getAttributes();
			if (attributes != null) {
				LdapDept dpaDept = new LdapDept(attributes, searchResult.getNameInNamespace());
				initRootRDNPosn(dpaDept);
				lists.add(dpaDept);
				staticUtil.addSearchRootCount();
			}
		}
		return lists;
	}

	public static RoundQueue<Object> queue = new RoundQueue<Object>(200);

	protected void startThreadPool(int nThreads) {
		ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
			private int i = 0;

			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "ldap[" + ldapConf.getCC() + "]组织同步线程-" + (i++));
				return t;
			}
		});
		executorService = es;
	}

	protected void submitTask(Runnable t) {
		executorService.submit(t);
	}

	protected void stopThreadPool() {
		if (executorService == null) {
			return;
		}

		while (executorService.getQueue().size() > 0 || executorService.getActiveCount() > 0) {
			try {
				Thread.sleep(300l);
			} catch (InterruptedException e) {
				break;
			}
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 执行同步
	 * 
	 * @throws NamingException
	 */
	Map<String, Integer> tmp = null;

	private static String getFt(String n, Object v) {
		return v + "　->　" + n;
	}

	private void initRootRDNPosn(LdapDept root) throws InvalidNameException {
		if (rootDNPosn == null) {
			LdapName lp = new LdapName(root.getName());
			rootDNPosn = lp.size();
		}
	}

	protected Object getDeptNameShort(String s) throws InvalidNameException {
		LdapName ln = new LdapName(s);
		if (ln.size() > rootDNPosn - 1) {
			return ln.getSuffix(rootDNPosn - 1);
		}
		return s;
	}

	public void sync(UserContext usersContext) {
		if (IAECache.getValue(LdapSyncConstant.ADDONS_IOX_APPID) != null) {
			return;
		}

		tmp = new HashMap<String, Integer>();
		String targetCompany = deptConfig.get("targetCompany");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startInfo = sdf.format(new Date());
		if (usersContext == null) {
			usersContext = UserContext.fromUID("admin");
		}

		initIAEModel(usersContext, startInfo);
		queue.clear();

		try {
			log("\n\n\nLDAP同步[" + ldapConf.getId() + "]开始......");
			String filter = ldapConf.getRootFilter();
			if (UtilString.isEmpty(filter)) {
				filter = ldapConf.getDeptFilter();
			}
			log("搜索根部门[context = " + ldapConf.getDepartmentRDN() + "][filter = " + filter + "]开始...");
			List<LdapDept> rootLdap = searchRootDept(filter);
			log("搜索根部门[" + staticUtil.getSearchRootCount() + "]结束");
			if (UtilString.isEmpty(rootLdap)) {
				log("[" + sdf.format(new Date()) + "] LDAP同步时<font color='red'>没有找到可同步根部门</font>，中止");
				return;
			}

			log("搜索部门[context = " + ldapConf.getDepartmentRDN() + "][filter = " + ldapConf.getDeptFilter() + "]开始...");
			final Map<String, List<LdapDept>> deptMap = getDepts(); // 加载从ldap查询出来的所有部门列表
			log("搜索部门[" + staticUtil.getTotalDeptCount() + "]结束");

			log("搜索人员[context = " + ldapConf.getUserRDN() + "][filter = " + ldapConf.getUserFilter() + "]开始...");
			final Map<String, List<LdapUser>> userMap = getUsers(); // 加载从ldap查询出来的所有人员列表
			log("搜索人员[" + staticUtil.getTotalUserCount() + "]结束");

			final Set<String> ldapDeptOuterIds = loadAllDeptOuterId(targetCompany); // 加载前一次从ldap同步过来的所有部门的outerId
			final Set<String> ldapUserOuterIds = loadAllUserOuterId(targetCompany); // 加载前一次从ldap同步过来的所有人员的outerId
			iaeModel.setTotal(staticUtil.getTotalDeptCount() + staticUtil.getTotalUserCount());

			List<String> sb = new ArrayList<String>();
			List<Entry<String, List<LdapDept>>> s = new ArrayList<Entry<String, List<LdapDept>>>(deptMap.entrySet());
			Collections.sort(s, new Comparator<Entry<String, List<LdapDept>>>() {
				public int compare(Entry<String, List<LdapDept>> o1, Entry<String, List<LdapDept>> o2) {
					return o1.getValue().size() - o2.getValue().size();
				}
			});

			for (Entry<String, List<LdapDept>> dps : s) {
				sb.add(getFt(dps.getKey(), dps.getValue() == null ? 0 : dps.getValue().size()));
			}
			log("\n\n\ndept info：\n" + (sb.size() > 0 ? UtilString.join(sb, "\n") : "无") + "\n\n\n", false);
			sb.clear();

			List<Entry<String, List<LdapUser>>> s1 = new ArrayList<Entry<String, List<LdapUser>>>(userMap.entrySet());
			Collections.sort(s1, new Comparator<Entry<String, List<LdapUser>>>() {
				public int compare(Entry<String, List<LdapUser>> o1, Entry<String, List<LdapUser>> o2) {
					return o1.getValue().size() - o2.getValue().size();
				}
			});
			for (Entry<String, List<LdapUser>> dps : s1) {
				sb.add(getFt(dps.getKey(), dps.getValue() == null ? 0 : dps.getValue().size()));
			}
			log("\n\n\nuser info：\n" + (sb.size() > 0 ? UtilString.join(sb, "\n") : "无") + "\n\n\n", false);
			sb.clear();

			try {
				// 准备同步
				startThreadPool(ldapConf.getMaxConc());
				// 循环根部门
				for (final LdapDept currentDept : rootLdap) {
					final String deptId = deptConfig.get("targetDepartment");
					Runnable r = new Runnable() {
						public void run() {
							try {
								syncDept(currentDept, deptMap, userMap, deptId, ldapDeptOuterIds, ldapUserOuterIds);
							} catch (Exception e) {
								e.printStackTrace();
							}
							finishOne();
						}
					};
					submitTask(r);
				}
			} finally {
				stopThreadPool();
				iaeModel.setState(IAEModel.STATE_SUCCESS);
			}

			int d = count(deptMap, rootLdap);
			int u = countUser(userMap);
			log("部门：维持" + staticUtil.getKeepDept() + "/忽略" + staticUtil.getIgnoreDept() + "/不同步" + d + "，人员：维持" + staticUtil.getKeepUser() + "/忽略" + staticUtil.getIgnoreUser() + "/不同步" + u);
			logAlone(deptMap, userMap);

			// 如果选择同步删除，则删除残余的部门和人员
			if (ldapConf.isSyncDelete()) {
				syncDelete(ldapDeptOuterIds, ldapUserOuterIds);
			}

			safeDeleteDept(deleteDeptsByClose, false);
			log("LDAP同步完成");

			List<Entry<String, Integer>> s2 = new ArrayList<Entry<String, Integer>>(tmp.entrySet());
			Collections.sort(s2, new Comparator<Entry<String, Integer>>() {
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o1.getValue() - o2.getValue();
				}
			});
			for (Entry<String, Integer> dps : s2) {
				sb.add(getFt(dps.getKey(), dps.getValue()));
			}
			log("\n\n\nuser sync info：\n" + (sb.size() > 0 ? UtilString.join(sb, "\n") : "无") + "\n\n\n", false);
			sb.clear();

			// clear
			for (Entry<String, List<LdapDept>> dps : deptMap.entrySet()) {
				if (dps.getValue() != null) {
					dps.getValue().clear();
				}
			}

			List<Entry<String, List<LdapUser>>> s4 = new ArrayList<Entry<String, List<LdapUser>>>(userMap.entrySet());
			Collections.sort(s4, new Comparator<Entry<String, List<LdapUser>>>() {
				public int compare(Entry<String, List<LdapUser>> o1, Entry<String, List<LdapUser>> o2) {
					return o1.getValue().size() - o2.getValue().size();
				}
			});
			for (Entry<String, List<LdapUser>> dps : s4) {
				sb.add(getFt(dps.getKey(), dps.getValue() == null ? 0 : dps.getValue().size()));
			}

			log("\n\n\nuser left info：\n" + (sb.size() > 0 ? UtilString.join(sb, "\n") : "无") + "\n\n\n", false);
			sb.clear();

			for (Entry<String, List<LdapUser>> dps : userMap.entrySet()) {
				if (dps.getValue() != null) {
					dps.getValue().clear();
				}
			}
			deptMap.clear();
			userMap.clear();
		} catch (Exception e) {
			e.printStackTrace();
			log("[" + sdf.format(new Date()) + "] LDAP同步时<font color='red'>" + ExceptionUtil.getMessage(e) + "</font>，中止");
			iaeModel.setState(IAEModel.STATE_FAIL);
		} finally {
			iaeModel.setComplete(true);
			logFile(startInfo, iaeModel);
			IAECache.removeValue(LdapSyncConstant.ADDONS_IOX_APPID);
		}
	}

	protected void safeDeleteDept(Collection<String> set, boolean outerId) {
		if (UtilString.isEmpty(set)) {
			return;
		}

		if (outerId) {
			log("开始删除搜索的部门[" + set.size() + "]...");
		} else {
			log("开始删除注销的部门[" + set.size() + "]...");
		}

		int del = 0;
		if (!UtilString.isEmpty(set)) {
			boolean deleteOne = false;
			do {
				deleteOne = false;
				for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
					String id = (String) iterator.next();
					if (outerId) {
						DepartmentModel dm = DepartmentCache.getModelOfOuterId(id);
						id = dm.getId();
					}
					try {
						SDK.getORGAPI().removeDepartment(id);
						iterator.remove();
						deleteOne = true;
						del++;
					} catch (Exception e) {
					}
				}
			} while (deleteOne);

			List<String> t = new ArrayList<String>();
			for (String id : set) {
				DepartmentModel dm;
				if (outerId) {
					dm = DepartmentCache.getModelOfOuterId(id);
				} else {
					dm = DepartmentCache.getModel(id);
				}
				if (dm != null) {
					t.add(dm.getId() + "/" + dm.getName());
				} else {
					t.add(id);
				}
			}

			log("删除部门结束，成功[" + del + "]，失败[" + set.size() + "][" + (outerId ? "outerId" : "id") + "] => " + UtilString.join(t, ","));
		}
	}

	protected void logAlone(Map<String, List<LdapDept>> deptMap, Map<String, List<LdapUser>> user) {
		if (!UtilString.isEmpty(deptMap)) {
			log("\n\n\n不同步部门父部门属性：" + deptMap.keySet().toString());
			try {
				log("特征部门：" + deptMap.entrySet().iterator().next().getValue().get(0).getLdapVal(ldapConf.getDepartmentLdapAttr()) + "\n\n\n");
			} catch (Exception e) {
				log(e.toString());
			}
		}
		if (!UtilString.isEmpty(user)) {
			log("\n\n\n不同步人员父部门属性：" + user.keySet().toString());
			try {
				log("特征人员：" + user.entrySet().iterator().next().getValue().get(0).getLdapVal(ldapConf.getUserLdapAttr()) + "\n\n\n");
			} catch (Exception e) {
				log(e.toString());
			}
		}
	}

	private static void logFile(String startInfo, IAEModel iaeModel) {
		JSONObject jo = new JSONObject();
		jo.put("startTime", startInfo);
		jo.put("iaeModel", iaeModel);
		jo.put("logs", queue.get());
		jo.put("endTime", UtilDate.datetimeFormat(new Date()));
		LdapSyncLog.writeLogInfoToFile(jo.toString());
	}

	private static int count(Map<String, List<LdapDept>> deptMap, List<LdapDept> rootLdap) {
		int c = 0;
		Set<String> rs = new HashSet<String>();
		for (LdapDept r : rootLdap) {
			rs.add(r.getName());
		}

		if (!UtilString.isEmpty(deptMap)) {
			Set<String> et = new HashSet<String>();
			for (Entry<String, List<LdapDept>> e : deptMap.entrySet()) {
				List<LdapDept> v = e.getValue();
				for (Iterator<LdapDept> iterator = v.iterator(); iterator.hasNext();) {
					LdapDept ldapDept = iterator.next();
					if (rs.contains(ldapDept.getName())) {
						iterator.remove();
					}
				}
				if (UtilString.isEmpty(v)) {
					et.add(e.getKey());
				} else {
					c += v.size();
				}
			}

			for (String s : et) {
				deptMap.remove(s);
			}
		}

		return c;
	}

	private static int countUser(Map<String, List<LdapUser>> list) {
		int c = 0;
		if (!UtilString.isEmpty(list)) {
			for (Collection<LdapUser> cs : list.values()) {
				if (!UtilString.isEmpty(cs)) {
					c += cs.size();
				}
			}
		}

		return c;
	}

	private static void validate(UserModel u) {
		if (UtilString.isEmpty(u.getUID())) {
			throw new AWSIllegalArgumentException("uid", AWSIllegalArgumentException.EMPT);
		}

		String s = " |\"。、，,+/^\\!~<>$";
		check(s, u.getUID(), "uid");
	}

	private static void check(String illegal, String str, String title) {
		for (int i = 0; i < illegal.length(); i++) {
			if (str.indexOf(illegal.charAt(i)) != -1) {
				throw new AWSIllegalArgumentException(title + ":" + str + "含有非法字符：" + illegal.charAt(i));
			}
		}
	}

	/**
	 * 同步当前部门+同步人员+同步子部门
	 * 
	 * @param currentDept
	 *            父部门 ldapdept
	 * @param deptMaps
	 *            部门maps
	 * @param userMaps
	 *            人员maps
	 * @param parentId
	 *            父部门id
	 * @throws NamingException
	 */
	protected void syncDept(LdapDept currentDept, final Map<String, List<LdapDept>> deptMaps, final Map<String, List<LdapUser>> userMaps, String parentId, final Set<String> ldapDeptOuterIds, final Set<String> ldapUserOuterIds) throws NamingException {
		DepartmentImpl deptModel = ldapToModel.getDepartment(currentDept, deptConfig, parentId);
		if (deptModel == null) {
			staticUtil.addIgnoreDept();
			log("部门[" + currentDept.getName() + "] ID属性不正确，忽略");
			return;
		} else if (deptModel.getId().equals(deptModel.getParentDepartmentId())) {
			staticUtil.addIgnoreDept();
			log("部门[" + currentDept.getName() + "] ParentID属性不合法，忽略");
			return;
		}

		// 1.日志部门信息
		/* 如果父部门是ParentDN，那么当前部门的下级部门的PID就是当前部门的DN */
		String pattr = ldapConf.getDepartmentLdapAttr();
		if (LdapSyncConstant.PARENT_DN.equals(ldapConf.getDepartmentParentLdapAttr())) {
			pattr = LdapSyncConstant.DN;
		}

		String parent = currentDept.getLdapVal(pattr);
		List<LdapDept> subDepts = deptMaps.remove(parent);
		List<LdapUser> ldapUsers = userMaps.remove(currentDept.getLdapVal(ldapConf.getUserDeptRelation()));
		log("部门[" + getDeptNameShort(currentDept.getName()) + "]同步开始，子部门[" + (UtilString.isEmpty(subDepts) ? 0 : subDepts.size()) + "]，人员[" + (UtilString.isEmpty(ldapUsers) ? 0 : ldapUsers.size()) + "]");

		// 2.同步当前部门到AWS
		final String deptId = SyncUtil.syncDeptModel(currentDept, deptModel, ldapDeptOuterIds, staticUtil, ldapConf, this, deleteDeptsByClose);

		// 3.同步人员到AWS
		if (!UtilString.isEmpty(ldapUsers)) {
			tmp.put(currentDept.getLdapVal(ldapConf.getUserDeptRelation()), ldapUsers.size());
			for (final LdapUser ldapUser : ldapUsers) {
				final UserModelImpl model = ldapToModel.getUser(ldapUser, userConfig, deptId);
				if (model == null) {
					staticUtil.addIgnoreUser();
					log("人员[" + ldapUser.getName() + "] ID属性不正确");
					continue;
				}

				try {
					validate(model);
				} catch (Exception e) {
					staticUtil.addIgnoreUser();
					log(e.toString());
					continue;
				}

				final Loggable that = this;
				Runnable r = new Runnable() {
					public void run() {
						try {
							SyncUtil.syncUserModel(ldapUser, model, ldapUserOuterIds, staticUtil, ldapConf, that);
						} catch (Exception e) {
							log(e);
							e.printStackTrace();
						}
						finishOne();
					}
				};
				submitTask(r);
			}
		}

		// 4.同步子部门到AWS
		if (!UtilString.isEmpty(subDepts)) {
			// 循环插入部门到AWS中
			for (final LdapDept subDept : subDepts) {
				Runnable r = new Runnable() {
					public void run() {
						try {
							syncDept(subDept, deptMaps, userMaps, deptId, ldapDeptOuterIds, ldapUserOuterIds);
						} catch (Exception e) {
							log(e);
							e.printStackTrace();
						}

						finishOne();
					}
				};
				submitTask(r);
			}
		}

		log("部门[" + getDeptNameShort(currentDept.getName()) + "]同步结束");
	}

	private void initIAEModel(UserContext usersContext, String startInfo) {
		IAEModel iaeModel = new IAEModel(usersContext);
		iaeModel.setStartTime(Timestamp.valueOf(startInfo));
		iaeModel.setNowNum(0);
		iaeModel.setObjectId(AWSServerConf.getInstanceName());
		iaeModel.setUrlJson(SyncUtil.statisticCount(staticUtil));
		IAECache.putValue(LdapSyncConstant.ADDONS_IOX_APPID, iaeModel);
		this.iaeModel = iaeModel;
	}

	private void finishOne() {
		finishSync.incrementAndGet();
		iaeModel.setNowNum(finishSync.get());
		iaeModel.setUrlJson(SyncUtil.statisticCount(staticUtil));
	}

	public IAEModel getIaeModel() {
		return iaeModel;
	}

	// 查询上一次从ldap同步过来的所有部门的outerId
	private Set<String> loadAllDeptOuterId(String companyId) {
		Set<String> outerIds = new HashSet<String>();
		List<DepartmentModelImpl> list = ConfigUtil.getLdapDeptListFromOrg(companyId);
		for (DepartmentModel model : list) {
			outerIds.add(model.getOuterId());
		}
		return outerIds;
	}

	// 查询上一次从ldap同步过来的所有人员的outerId
	private Set<String> loadAllUserOuterId(String companyId) {
		Set<String> outerIds = new HashSet<String>();
		List<DepartmentModelImpl> list = ConfigUtil.getLdapDeptListFromOrg(companyId);
		for (DepartmentModel model : list) {
			List<UserModelImpl> users = ConfigUtil.getLdapUserListFromOrg(model.getId());
			for (UserModelImpl usmodel : users) {
				outerIds.add(usmodel.getOuterId());
			}
		}
		return outerIds;
	}

	/**
	 * 同步删除
	 * 
	 * @param depts
	 * @param users
	 */
	private void syncDelete(Set<String> depts, Set<String> users) {
		for (String outerId : users) {
			UserModel userModel = UserCache.getModelByOuterId(outerId);
			if (userModel != null) {
				try {
					SDK.getORGAPI().removeUser(userModel.getUID());
					staticUtil.addDelUsercount();
					log("删除人员[" + outerId.toUpperCase() + "]");
				} catch (Exception e) {
					e.printStackTrace();
					staticUtil.addFaildelUsercount();
					log("删除人员[" + outerId.toUpperCase() + "]失败");
				}
			}
		}

		deleteDept(depts);
	}

	// 递归删除部门
	private void deleteDept(Set<String> outerIds) {
		if (outerIds != null && outerIds.size() > 0) {
			Set<String> newOuterIds = new HashSet<String>();
			for (String outerId : outerIds) {
				DepartmentModel departmentModel = DepartmentCache.getModelOfOuterId(outerId);
				if (departmentModel == null) {
					continue;
				}
				boolean isExist = DepartmentCache.isExistSubModel(departmentModel.getId());
				if (isExist) {
					newOuterIds.add(outerId);
				} else {
					try {
						SDK.getORGAPI().removeDepartment(departmentModel.getId());
						staticUtil.addDelDeptcount();
						log("删除部门[" + outerId.toUpperCase() + "]");
					} catch (Exception e) {
						e.printStackTrace();
						staticUtil.addFaildelDeptcount();
						log("删除部门[" + outerId.toUpperCase() + "]失败");
					}
				}
			}

			if (outerIds.size() != newOuterIds.size()) {
				deleteDept(newOuterIds);
			} else {
				staticUtil.addFaildelDeptcount(newOuterIds.size());
				log("删除部门[" + newOuterIds + "]失败，请手工删除");
			}
		}
	}

}
