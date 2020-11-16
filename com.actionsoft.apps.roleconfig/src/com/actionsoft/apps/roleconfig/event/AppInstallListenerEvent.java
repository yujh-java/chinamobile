package com.actionsoft.apps.roleconfig.event;

import com.actionsoft.apps.lifecycle.dist.DistContext;
import com.actionsoft.apps.lifecycle.event.AppCustomActionInterface;
import com.actionsoft.apps.resource.AppContext;

public class AppInstallListenerEvent implements AppCustomActionInterface {

	@Override
	public void dist(AppContext arg0, DistContext arg1) {
		// TODO Auto-generated method stub
	}

	/**
     * 应用安装的扩展自定义动作事件，该事件执行的时间点是安装后的第一次启动
     * 安装时创建相关视图
     *
     * @param newApp
     */
	public void install(AppContext arg0) {
		//创建view_act_usermap视图sql语句
		/*String usermap_sql="CREATE VIEW VIEW_ACT_USERMAP AS SELECT `M`.`ROLEID` AS `ROLEID`, `M`.`DEPARTMENTID` AS `DEPARTMENTID`, GROUP_CONCAT(`M`.`USERID` SEPARATOR ' ') AS `USERIDS`, GROUP_CONCAT(`U`.`USERNAME` SEPARATOR ' ') AS `USERNAMES` FROM ( `ORGUSERMAP` `M` LEFT JOIN `ORGUSER` `U` ON ((`M`.`USERID` = `U`.`USERID`))) GROUP BY `M`.`ROLEID`, `M`.`DEPARTMENTID`";
		//创建view_act_usermap_username视图sql语句
		String username_sql="CREATE VIEW VIEW_ACT_USERMAP_USERNAME AS SELECT `M`.`ROLEID` AS `ROLEID`, `M`.`USERIDS` AS `USERIDS`, `M`.`USERNAMES` AS `USERNAMES`, `R`.`ROLENAME` AS `ROLENAME`, `M`.`DEPARTMENTID` AS `DEPARTMENTID`, `D`.`DEPARTMENTNAME` AS `DEPARTMENTNAME`, `R`.`CATEGORYNAME` AS `CATEGORYNAME` FROM (( `VIEW_ACT_USERMAP` `M` LEFT JOIN `ORGROLE` `R` ON ((`M`.`ROLEID` = `R`.`ID`))) LEFT JOIN `ORGDEPARTMENT` `D` ON (( `M`.`DEPARTMENTID` = `D`.`ID` )))";
		Connection conn = DBSql.open();
		DBSql.update(conn,usermap_sql);
		DBSql.update(conn,username_sql);*/
		
	}

	@Override
	public void uninstall(AppContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void upgrade(AppContext arg0) {
		// TODO Auto-generated method stub
		
	}


}
