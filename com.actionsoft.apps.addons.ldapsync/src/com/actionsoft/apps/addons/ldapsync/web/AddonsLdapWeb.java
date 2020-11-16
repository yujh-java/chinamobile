package com.actionsoft.apps.addons.ldapsync.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import com.actionsoft.apps.addons.ldapsync.FullSyncJob;
import com.actionsoft.apps.addons.ldapsync.LdapAdapter;
import com.actionsoft.apps.addons.ldapsync.constant.LdapSyncConstant;
import com.actionsoft.apps.addons.ldapsync.log.LdapSyncLog;
import com.actionsoft.apps.addons.ldapsync.model.LdapConf;
import com.actionsoft.apps.addons.ldapsync.model.MapModel;
import com.actionsoft.apps.addons.ldapsync.model.StaticModel;
import com.actionsoft.apps.addons.ldapsync.search.FullSync;
import com.actionsoft.apps.addons.ldapsync.util.ConfigUtil;
import com.actionsoft.apps.addons.ldapsync.util.SearchUtil;
import com.actionsoft.apps.addons.ldapsync.util.SyncUtil;
import com.actionsoft.bpms.bo.design.cache.BOCache;
import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.design.model.BOModel;
import com.actionsoft.bpms.cc.CCConst;
import com.actionsoft.bpms.cc.cache.CCCache;
import com.actionsoft.bpms.cc.connector.ldap.LDAPHandler;
import com.actionsoft.bpms.cc.model.CCModel;
import com.actionsoft.bpms.commons.cache.iae.IAECache;
import com.actionsoft.bpms.commons.cache.iae.model.IAEModel;
import com.actionsoft.bpms.commons.htmlframework.HtmlPageTemplate;
import com.actionsoft.bpms.commons.mvc.view.ActionWeb;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.org.cache.CompanyCache;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.impl.DepartmentModelImpl;
import com.actionsoft.bpms.org.model.impl.UserModelImpl;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.conf.server.AWSServerConf;
import com.actionsoft.bpms.util.ThreadMgr;
import com.actionsoft.bpms.util.UUIDGener;
import com.actionsoft.bpms.util.UtilDate;
import com.actionsoft.bpms.util.UtilFile;
import com.actionsoft.bpms.util.UtilSerialize;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSIllegalArgumentException;

public class AddonsLdapWeb extends ActionWeb {

	public AddonsLdapWeb(UserContext me) {
		super(me);
	}

	public String showMainPage() {
		Map<String, Object> macroLibraries = new LinkedHashMap<String, Object>();
		StringBuilder ccldapHTML = new StringBuilder(); // CC LDAP列表
		LdapConf jo = ConfigUtil.getConf();
		String cc_ldap = jo.getCC();
		ccldapHTML.append("<option value=''>请选择</option>");

		// cc ldap列表
		Map<Integer, CCModel> maps = CCCache.getInstance().getListByType(CCConst.TYPE_LDAP);
		for (CCModel ccModel : maps.values()) {
			ccldapHTML.append("<option value='" + ccModel.getId() + "' " + (ccModel.getId().equals(cc_ldap) ? "selected" : "") + ">" + ccModel.getName() + "</option>");
		}

		StringBuilder companyHTML = new StringBuilder(); // AWS组织单元
		// 获取AWS组织单元(公司)
		List<CompanyModel> companys = CompanyCache.getList();
		for (CompanyModel companyModel : companys) {
			companyHTML.append("<option value='" + companyModel.getId() + "' " + (jo.getTargetCompany().equals(companyModel.getId()) ? "selected" : "") + ">" + companyModel.getName() + "</option>");
		}
		StringBuilder awsDepOption = new StringBuilder();
		// aws 部门字段
		for (Entry<String, String> prop : LdapSyncConstant.aws_deptAttrMap.entrySet()) {
			awsDepOption.append("<option value='" + prop.getKey() + "'>" + prop.getKey() + " (" + prop.getValue() + ")</option>");
		}

		BOModel deptExt = BOCache.getInstance().getModelByEntityName(LdapConf.TN_EXT_DEPT);
		if (deptExt != null && !UtilString.isEmpty(deptExt.getBoItems())) {
			awsDepOption.append("<optgroup label=\"" + deptExt.getTitle() + "\">");
			for (BOItemModel field : deptExt.getBoItems()) {
				if (LdapConf.R_DID.equalsIgnoreCase(field.getName())) {
					continue;
				}
				awsDepOption.append("<option value='" + LdapConf.EXT + field.getName() + "'>" + field.getName() + " (" + field.getTitle() + ")</option>");
			}
			awsDepOption.append("</optgroup>");
		}
		StringBuilder awsUserOption = new StringBuilder();
		// aws 人员字段
		for (Entry<String, String> prop : LdapSyncConstant.aws_userAttrMap.entrySet()) {
			awsUserOption.append("<option value='" + prop.getKey() + "'>" + prop.getKey() + " (" + prop.getValue() + ")</option>");
		}

		BOModel userExt = BOCache.getInstance().getModelByEntityName(LdapConf.TN_EXT_USER);
		if (userExt != null && !UtilString.isEmpty(userExt.getBoItems())) {
			awsUserOption.append("<optgroup label=\"" + userExt.getTitle() + "\">");
			for (BOItemModel field : userExt.getBoItems()) {
				if (LdapConf.R_UID.equalsIgnoreCase(field.getName())) {
					continue;
				}
				awsUserOption.append("<option value='" + LdapConf.EXT + field.getName() + "'>" + field.getName() + " (" + field.getTitle() + ")</option>");
			}
			awsUserOption.append("</optgroup>");
		}

		String mappingUserId = "";
		String mappingUserDeptId = "";
		String mappingDeptId = "";
		String mappingParentDeptId = "";
		boolean sync_delete = jo.isSyncDelete();
		List<MapModel> user_maps = jo.getUserMaps();
		List<MapModel> dept_maps = jo.getDepartmentMaps();
		StringBuilder syncUserHTML = new StringBuilder();
		if (user_maps != null) {
			for (MapModel str : user_maps) {
				if (str.getFrom() == null || str.getTo() == null) {
					continue;
				}
				if (str.getTo().equals(UserModelImpl.FIELD_USER_ID)) {
					mappingUserId = str.getFrom();
				} else if (str.getTo().equals(UserModelImpl.FIELD_DEPARTMENT_ID)) {
					mappingUserDeptId = str.getFrom().equals("null") ? "" : str.getFrom();
				} else {
					syncUserHTML.append("<tr><td align='center'><input type='checkbox' class='awsui-checkbox check-all' name='ck_userAttr'  value=''/></td><td>" + str.getFrom() + "</td><td>" + str.getTo() + "</td>");
				}

			}
		}

		StringBuilder syncDeptHTML = new StringBuilder();
		if (dept_maps != null) {
			for (MapModel str : dept_maps) {
				if (str.getFrom() == null || str.getTo() == null) {
					continue;
				}
				if (DepartmentModelImpl.FIELD_ID.equals(str.getTo())) {
					mappingDeptId = str.getFrom();
				} else if (DepartmentModelImpl.FIELD_PARENT_DEPARTMENT_ID.equals(str.getTo())) {
					mappingParentDeptId = str.getFrom().equals("null") ? "" : str.getFrom();
				} else {
					syncDeptHTML.append("<tr  ><td align='center'><input type='checkbox' class='awsui-checkbox check-all' name='ck_deptAttr'  value=''/></td><td>" + str.getFrom() + "</td><td>" + str.getTo() + "</td>");
				}
			}
		}

		macroLibraries.put("sid", this.getSIDFlag());
		macroLibraries.put("dept_root_rdn", jo.getDepartmentRDN());
		macroLibraries.put("dept_user_rdn", jo.getUserRDN());
		macroLibraries.put("dept_root_filter", jo.getRootFilter());
		macroLibraries.put("searchScope", jo.getSearchScope());
		macroLibraries.put("type_sync_dept", jo.isSyncByDN());
		macroLibraries.put("mappingUserId", mappingUserId);
		macroLibraries.put("mappingUserDeptId", mappingUserDeptId);
		macroLibraries.put("mappingDeptId", mappingDeptId);
		macroLibraries.put("mappingParentDeptId", mappingParentDeptId);
		macroLibraries.put("deptName", jo.getDeptName());
		macroLibraries.put("targetDeptRdo", jo.isSyncToCompany());
		macroLibraries.put("target_department", jo.getTargetDepartment());
		macroLibraries.put("roleId", jo.getRoleId());
		macroLibraries.put("roleName", jo.getRoleName());
		macroLibraries.put("sync_delete", sync_delete ? "checked='checked'" : "");
		try {
			macroLibraries.put("filter-dept", jo.getDeptFilter());
		} catch (Exception e) {
			macroLibraries.put("filter-dept", "");
		}
		try {
			macroLibraries.put("filter-user", jo.getUserFilter());
		} catch (Exception e) {
			macroLibraries.put("filter-user", "");
		}

		macroLibraries.put("sync-adapter-holder", LdapAdapter.class.getName());
		macroLibraries.put("filter-dept-inc", jo.getDeptFilterInc());
		macroLibraries.put("filter-user-inc", jo.getUserFilterInc());
		macroLibraries.put("rdn_del", jo.getRDNDel());
		macroLibraries.put("filter-dept-del", jo.getDeptFilterDel());
		macroLibraries.put("filter-user-del", jo.getUserFilterDel());
		macroLibraries.put("sync-adapter", jo.getSyncAdapter());
		macroLibraries.put("maxConc", jo.getMaxConc());
		macroLibraries.put("ccldapHTML", ccldapHTML);
		macroLibraries.put("companyHTML", companyHTML);
		macroLibraries.put("syncDeptHTML", syncDeptHTML);
		macroLibraries.put("syncUserHTML", syncUserHTML);
		macroLibraries.put("awsDepOption", awsDepOption);
		macroLibraries.put("awsUserAttr", awsUserOption);
		macroLibraries.put("isExec", isExecSync());
		macroLibraries.put("props", jo.toJson());
		return HtmlPageTemplate.merge(LdapSyncConstant.ADDONS_IOX_APPID, "com.actionsoft.apps.addons.ldapsync.main.htm", macroLibraries);
	}

	/**
	 * 获取ldap单元(部门)和条目(人员)的属性
	 * 
	 * @param cc_ldapId
	 * @return
	 * @throws Exception
	 */
	public static String getLdapArrList(String cc_ldapId) throws Exception {
		ResponseObject ro = ResponseObject.newOkResponse();
		if (UtilString.isEmptyByTrim(cc_ldapId)) {
			return ro.toString();
		}

		Set<String> t = new HashSet<String>(getAttList(cc_ldapId, true));
		SearchControls searchControls = SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(200);
		NamingEnumeration<SearchResult> d = SearchUtil.getResult(cc_ldapId, "", ConfigUtil.getDeptFilter(cc_ldapId), searchControls);
		t.addAll(SearchUtil.getLdapDepartmentAttList(d));
		List<String> deptSet = new ArrayList<String>(t);
		Collections.sort(deptSet);
		JSONArray deptArray = new JSONArray();
		deptArray.add("<option value=\"\" selected >请选择</option>");
		for (Object object : deptSet) {
			deptArray.add("<option value=\"" + object.toString() + "\">" + object.toString() + "</option>");
		}

		Set<String> s = new HashSet<String>(getAttList(cc_ldapId, false));
		NamingEnumeration<SearchResult> u = SearchUtil.getResult(cc_ldapId, "", ConfigUtil.getUserFilter(cc_ldapId), searchControls);
		s.addAll(SearchUtil.getLdapUserAttList(u));
		List<String> userSet = new ArrayList<String>(s);
		Collections.sort(userSet);
		JSONArray userArray = new JSONArray();
		userArray.add("<option value=\"\"  selected >请选择</option>");
		for (Object object : userSet) {
			userArray.add("<option value=\"" + object.toString() + "\">" + object.toString() + "</option>");
		}
		JSONObject data = new JSONObject();
		data.put("deptArray", deptArray.toString());
		data.put("userArray", userArray.toString());
		ro.setData(data);
		return ro.toString();
	}

	private static Collection<String> getAttList(String cc, boolean dept) throws NamingException {
		String filter = dept ? ConfigUtil.getDeptFilter(cc) : ConfigUtil.getUserFilter(cc);
		Set<String> attrs = new HashSet<String>();
		SearchControls searchControls = SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(2);
		LdapContext ldapContext = null;
		try {
			ldapContext = SearchUtil.getDirContext(cc);
			NamingEnumeration<SearchResult> d = ldapContext.search("", filter, searchControls);
			if (d.hasMore()) {
				Attribute attr = d.nextElement().getAttributes().get("objectClass");
				NamingEnumeration<?> e = attr.getAll();
				DirContext schema = ldapContext.getSchema("");
				while (e.hasMoreElements()) {
					String oc = (String) e.nextElement();
					Attributes ocAttrs = schema.getAttributes("ClassDefinition/" + oc);
					Attribute must = ocAttrs.get("MUST");
					if (must != null) {
						NamingEnumeration<?> vals = must.getAll();
						while (vals.hasMore()) {
							Object val = vals.next();
							attrs.add(String.valueOf(val));
						}
					}
					Attribute may = ocAttrs.get("MAY");
					if (may != null) {
						NamingEnumeration<?> vals = may.getAll();
						while (vals.hasMore()) {
							Object val = vals.next();
							attrs.add(String.valueOf(val));
						}
					}
				}
			}
		} catch (NamingException e) {
			System.err.println(e);
		} finally {
			SearchUtil.close(ldapContext);
		}

		return attrs;
	}

	public static String getDeptAndUserStruct(String CCldapId) throws NamingException {
		ResponseObject ro = ResponseObject.newOkResponse();
		JSONObject jo = new JSONObject();
		SearchControls searchControls = SearchUtil.createSearchControls(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(200);
		String deptObject = getAttObject(SearchUtil.getResult(CCldapId, "", ConfigUtil.getDeptFilter(CCldapId), searchControls), "dept");
		String UserObject = getAttObject(SearchUtil.getResult(CCldapId, "", ConfigUtil.getUserFilter(CCldapId), searchControls), "user");
		jo.put("dept", deptObject);
		jo.put("user", UserObject);
		ro.setData(jo.toString());
		return ro.toString();
	}

	private static String getAttObject(NamingEnumeration result, String type) throws NamingException {
		StringBuffer html = new StringBuffer("<table id='" + type + "' class='awsui-ux'><tr><td><table class='table table-bordered'>");
		SearchResult searchResult = (SearchResult) result.nextElement();
		Attributes attributes = searchResult.getAttributes();
		Collection deptSet = new HashSet();
		if (type.equals("dept")) {
			deptSet = SearchUtil.getLdapDepartmentAttList(result);
		} else {
			deptSet = SearchUtil.getLdapUserAttList(result);
		}
		for (Object object : deptSet) {
			String attVal = "";
			Attribute att = attributes.get(object.toString());
			if (att != null) {
				if (object.equals("objectGUID") || object.equals("objectSid")) {
					attVal = convertToDashedString(att);
				} else {
					attVal = att.get().toString();
				}
			}
			html.append("<tr><td>" + object + "</td><td>" + attVal + "</td></tr>");
		}
		html.append("</table></td></tr></table>");
		return html.toString();
	}

	private static String convertToDashedString(Attribute att) throws NamingException {
		byte[] bytes = (byte[]) att.get();
		return convertToDashedString(bytes);
	}

	public static String convertToDashedString(byte[] objectGUID) {
		StringBuilder displayStr = new StringBuilder();
		displayStr.append(prefixZeros((int) objectGUID[3] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[2] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[1] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[0] & 0xFF));
		displayStr.append("-");
		displayStr.append(prefixZeros((int) objectGUID[5] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[4] & 0xFF));
		displayStr.append("-");
		displayStr.append(prefixZeros((int) objectGUID[7] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[6] & 0xFF));
		displayStr.append("-");
		displayStr.append(prefixZeros((int) objectGUID[8] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[9] & 0xFF));
		displayStr.append("-");
		displayStr.append(prefixZeros((int) objectGUID[10] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[11] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[12] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[13] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[14] & 0xFF));
		displayStr.append(prefixZeros((int) objectGUID[15] & 0xFF));
		return displayStr.toString();
	}

	private static String prefixZeros(int value) {
		if (value <= 0xF) {
			StringBuilder sb = new StringBuilder("0");
			sb.append(Integer.toHexString(value));

			return sb.toString();

		} else {
			return Integer.toHexString(value);
		}
	}

	/**
	 * 保存ldap配置参数
	 * 
	 * @param ldapParam
	 * @return
	 */
	public String saveLdapParams(String ldapParam) {
		com.alibaba.fastjson.JSONObject jo = UtilSerialize.parseObject(ldapParam);
		if (jo.getString("id") == null) {
			jo.put("id", UUIDGener.getUUID());
		}

		UtilFile utilFile = new UtilFile(LdapSyncConstant.filePath);
		boolean flag = utilFile.writeUTF8(jo.toString());
		if (!flag) {
			return ResponseObject.newOkResponse("保存失败").toString();
		}
		return ResponseObject.newOkResponse("保存成功").toString();
	}

	/**
	 * 执行同步方法
	 * 
	 * @return
	 */
	public String execSync() {
		ResponseObject ro = ResponseObject.newOkResponse();
		String serverId = null;
		if (IAECache.getValue(LdapSyncConstant.IAE_KEY) != null) {
			serverId = IAECache.getValue(LdapSyncConstant.IAE_KEY).getObjectId();
		} else {
			checkConf();
			serverId = AWSServerConf.getInstanceName();
			IAEModel iaeModel = new IAEModel(getContext());
			iaeModel.setObjectId(serverId);
			IAECache.putValue(LdapSyncConstant.IAE_KEY, iaeModel);
			FullSyncJob sy = new FullSyncJob();
			sy.setUserContext(getContext());
			ThreadMgr.execute(sy);
		}

		ro.put("serverId", serverId);
		return ro.toString();
	}

	public static boolean isExecSync() {
		return IAECache.getValue(LdapSyncConstant.IAE_KEY) != null;
	}

	private void checkConf() {
		LdapConf conf = ConfigUtil.getConf();
		if (conf.isSyncToCompany()) {
			String id = conf.getTargetCompany();
			if (UtilString.isEmpty(id)) {
				throw new AWSIllegalArgumentException("目标单位", AWSIllegalArgumentException.EMPT);
			}
			CompanyModel c = CompanyCache.getModel(id);
			if (c == null) {
				throw new AWSIllegalArgumentException("目标单位[" + id + "]不存在");
			}
		} else {
			String id = conf.getTargetDepartment();
			if (UtilString.isEmpty(id)) {
				throw new AWSIllegalArgumentException("目标部门", AWSIllegalArgumentException.EMPT);
			}
			DepartmentModel d = DepartmentCache.getModel(id);
			if (d == null) {
				throw new AWSIllegalArgumentException("目标部门[" + id + "]不存在");
			}
		}
	}

	/**
	 * 查看日志
	 * 
	 * @return
	 */
	public String showLog() {
		ResponseObject ro = ResponseObject.newOkResponse();
		IAEModel iaeModel = IAECache.getValue(LdapSyncConstant.ADDONS_IOX_APPID);
		Object infoJson = new JSONObject();
		if (iaeModel == null) {
			String oldInfo = LdapSyncLog.readLogInfoFromFile();
			if (oldInfo != null && oldInfo.length() > 0 && JSONUtils.mayBeJSON(oldInfo)) {
				infoJson = UtilSerialize.parseObject(oldInfo);
			} else {
				iaeModel = new IAEModel(getContext());
				iaeModel.setComplete(true);
				iaeModel.setUrlJson(SyncUtil.statisticCount(new StaticModel()));
				((JSONObject) infoJson).put("iaeModel", iaeModel);
			}
		} else {
			JSONObject jo = new JSONObject();
			jo.put("startTime", UtilDate.datetimeFormat(iaeModel.getStartTime()));
			jo.put("iaeModel", iaeModel);
			jo.put("logs", FullSync.queue.get());
			infoJson = jo;
		}

		ro.setData(infoJson);
		return ro.toString();
	}

	public String testConnect(String ldapId) throws Exception {
		testCCLdap(ldapId);
		return ResponseObject.newOkResponse("测试通过").toString();
	}

	private boolean testCCLdap(String ldapId) throws Exception {
		CCModel ccModel = CCCache.getInstance().getModel(ldapId);
		return LDAPHandler.getInstance().test(ldapId, ccModel.getProperties());
	}
}
