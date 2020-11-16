package com.actionsoft.apps.cmcc.email.util;

import com.actionsoft.apps.cmcc.email.EmailConst;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.util.UtilString;

/**
 * @author nch
 * @date 2017-10-20
 * 公共处理方法类
 */
public class CommonUtil {
	/**
	 * 根据用户账号，获取单位名称
	 * 集团下去二级部门，非集团取一级部门
	 * @author nch
	 * @date 2017-10-20
	 * @param userid
	 * @return
	 */
	public static String getCompany(String userid){
		String company = "";
		UserModel userModel = UserCache.getModel(userid);
		String deptid = userModel.getDepartmentId();
		String deptPathid = DepartmentCache.getModel(deptid).getPathIdOfCache();
		if(!UtilString.isEmpty(deptPathid)){
			String[] deptidPathArr = deptPathid.split("/");
			String deptno = DepartmentCache.getModel(deptidPathArr[0]).getOuterId();
			if(EmailConst.deptNo_jt.equals(deptno)){//属于集团
				if(deptidPathArr.length > 0){
					String companyid = deptidPathArr[1];
					if(!UtilString.isEmpty(companyid)){
						company = DepartmentCache.getModel(companyid).getName();
					}
				}
			}else{
				String companyid = deptidPathArr[0];
				if(!UtilString.isEmpty(companyid)){
					company = DepartmentCache.getModel(companyid).getName();
				}
			}
		}
		return company;
	}
}
