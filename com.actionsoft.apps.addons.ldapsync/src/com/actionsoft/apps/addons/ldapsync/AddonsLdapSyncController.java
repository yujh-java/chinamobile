package com.actionsoft.apps.addons.ldapsync;

import javax.naming.NamingException;

import com.actionsoft.apps.addons.ldapsync.web.AddonsLdapWeb;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.bind.annotation.Controller;
import com.actionsoft.bpms.server.bind.annotation.Mapping;

/**
 * 实例高级分类管理-控制器
 * 
 * @author 作者： zhuyj
 * @version 创建时间：2014-10-30
 * 
 */
@Controller
public class AddonsLdapSyncController {

	// 实例高级分类管理-入口
	@Mapping("com.actionsoft.apps.addons.ldap_main_page")
	public String showMainPage(UserContext me) {
		AddonsLdapWeb web = new AddonsLdapWeb(me);
		return web.showMainPage();
	}

	@Mapping("com.actionsoft.apps.addons.ldap_params")
	public String getLdapArrList(UserContext me, String ccldapId) throws Exception {
		return AddonsLdapWeb.getLdapArrList(ccldapId);
	}

	@Mapping("com.actionsoft.apps.addons.ldap_saveparams")
	public String saveLdapParam(UserContext me, String ldapParam) {
		AddonsLdapWeb web = new AddonsLdapWeb(me);
		return web.saveLdapParams(ldapParam);
	}

	@Mapping("com.actionsoft.apps.addons.ldap_execsync")
	public String execSync(UserContext me) {
		AddonsLdapWeb web = new AddonsLdapWeb(me);
		return web.execSync();
	}

	@Mapping("com.actionsoft.apps.addons.ldap_testconnect")
	public String testConnect(UserContext me, String ldapId) throws Exception {
		AddonsLdapWeb web = new AddonsLdapWeb(me);
		return web.testConnect(ldapId);
	}

	@Mapping("com.actionsoft.apps.addons.ldap_showlog")
	public String showLog(UserContext me) {
		AddonsLdapWeb web = new AddonsLdapWeb(me);
		return web.showLog();
	}

	@Mapping("com.actionsoft.apps.addons.ldap_showstruct")
	public String getDeptAndUserStruct(UserContext me, String ldapId) throws NamingException {
		return AddonsLdapWeb.getDeptAndUserStruct(ldapId);
	}

}
