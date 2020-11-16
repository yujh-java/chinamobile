package com.actionsoft.apps.cmcc.enterprise.util;

import java.util.List;

import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.util.DBSql;

/**
 * 公共实体类
 * @author wuxx
 *
 */
public class EnterpriseUtilEntryEventClass {
	/**
	 * 通过用户账号获取其兼职的部门ID
	 * @author wuxx
	 * @date   2018年10月23日
	 * @param userid
	 * @return
	 */
	public static boolean getMapperDeptIdByUserid(String[] peoples,int length,String params){
		//通过人员账号获取其兼职的部门ID集合（去重以后的）
		String sql = "";
		//通过人员账号获取其兼职的部门ID集合（去重以后的）
		sql = "select DEPARTMENTID "
					+ "from ORGUSER "
					+ "where userid in (";
		
		//拼接过滤人员
		String where_people = "";
		//遍历人员
		for(int i = 0; i < length; i++){
			String userid = peoples[i];
			if("".equals(userid)){
				continue;
			}
			//截取，只留下userid
			userid = userid.substring(0,userid.indexOf("<"));
			System.err.println("====userid:"+userid+"======");
			if(i == length - 1){
				where_people += "'"+userid+"'";
			}else{
				where_people += "'"+userid+"',";
			}
		}
			sql += where_people + ") group by DEPARTMENTID";

		//获取人员兼职部门ID集合
		List<RowMap> list = DBSql.getMaps(sql);
		/*
		 * 获取所选人员兼职部门数量
		 */
		String sql_count = "";

			sql_count = "select count(*) c from ORGUSER where userid in ("+where_people+")";

		//部门数量
		String count = DBSql.getString(sql_count);
		int size = list.size();
		int sum = Integer.parseInt(count);
		//如果获取到的部门ID的集合数量等于人员的数量，则没有同个部门的接口人多选的情况，则返回true
		if(size <= 0 || size < sum){
			return false;
		}
		return true;
	}

}
