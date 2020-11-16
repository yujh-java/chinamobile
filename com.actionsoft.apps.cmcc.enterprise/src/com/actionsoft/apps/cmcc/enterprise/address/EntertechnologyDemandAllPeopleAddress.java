package com.actionsoft.apps.cmcc.enterprise.address;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.enterprise.CmccConst;
import com.actionsoft.apps.cmcc.enterprise.util.CmccCommon;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.sdk.local.SDK;
/**
 * 企标管理
 * 非重点企标送审，阅知找技术部企标管理员
 * @author wxx
 *
 */
public class EntertechnologyDemandAllPeopleAddress implements AddressUIFilterInterface{
	//在流程数据维护表中获取需求部门接口人部门ID
		String sql_deptId = "select DEMAND from BO_ACT_CMCC_PROCESSDATA where PROCESSID = ?";
		/*
		 * 在企标角色维护表中：BO_ACT_ENTERPRISE_ROLEDATA
		 * 查询需求部门企标管理员
		 */
		public String sql_demandPeople = "";
		//流程实例ID
		public String processId;
		//任务实例ID
		public String taskid;
		//任务状态
		public int taskstate = 0;
		//技术部门ID
		public String deptId;
		//需求部门企标管理员所在部门全路径集合
		public List<String> list = new ArrayList<String>();
		//需求部门企标管理员人员集合
		public List<String> userid_list = new ArrayList<String>();
		//判断当前人是不是集团企标管理员的sql--在维护表中查找
		public String roleName_sql = "select count(*) c "
									+ "from BO_ACT_ENTERPRISE_ROLEDATA "
									+ "where rolename = ? "
										+ "and userid = ?";
		//用于判断是否企标管理员
		public String count;
		@Override
		public boolean addressUIFlexCompanyFilter(UserContext arg0, 
				CompanyModel arg1, AdvancedAddressModel arg2) {
			return true;
		}
	/*private String leaderDeptId = "";//处长所在部门ID
	private List<String> list_userPathids  = new ArrayList<String>();//部长所在部门全路径 
	private List<String> list_userids  = new ArrayList<String>();//部长角色所有人员账号
*/	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
        if (UtilString.isEmpty(processId)) {
			processId = advancedAddressModel.getInstanceId();
		}
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
		}
		if(taskstate <= 0){
			taskstate = SDK.getTaskAPI().getTaskInstance(taskid).getState();
			//System.err.println("========企标阅知taskstate："+taskstate+"==========");
		}
		/*if("".equals(deptId)){
			return false;
		}*/
		//技术部门ID全路径即为技术部门ID全路径
		//String pathdeptId = DepartmentCache.getModel("ec76faca-0265-4666-bd72-567934551f6f").getPathIdOfCache();//ces
		String pathdeptId = DepartmentCache.getModel("1f5c54ba-1214-478f-b911-8e7018d3b74b").getPathIdOfCache();//zs
		//获取需求部门企标管理员所在部门ID全路径的集合
		if(UtilString.isEmpty(sql_demandPeople)){
			//////////////////
			//if(pathdeptId.equals("ec76faca-0265-4666-bd72-567934551f6f")){
				sql_demandPeople = "SELECT GROUP_CONCAT(userid) userid "
				  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
				  		+ "WHERE ROLENAME = '企标管理员' AND locate(deptid, '"+pathdeptId+"' ) > 0";
			/*}else{
				sql_demandPeople = "SELECT GROUP_CONCAT(userid) userid "
				  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
				  		+ "WHERE ROLENAME = '集团企标管理员' AND locate(deptid, '"+pathdeptIds+"' ) > 0";
			}*/
			
			String userid = DBSql.getString(sql_demandPeople);
			if(!UtilString.isEmpty(userid)){
				//获取多个人
				String[] peoples = userid.split(",");
				for(int i = 0;i<peoples.length;i++){
					//人员账号
					String people = peoples[i];
					//需求部门企标管理员所在部门ID
					String people_deptid = SDK.getORGAPI().getUser(people).getDepartmentId();
					//部门全路径
					String path_people_deptid = DepartmentCache.getModel(people_deptid).getPathIdOfCache();
					//部门集合
					list.add(path_people_deptid);
				}
			}
		}
		//遍历部门ID的全路径
		String deptid_query = DepartmentCache.getModel(model.getId()).getPathIdOfCache();
	    //当前人账号
		String userid = uc.getUID();
		/*
		 * 根据父任务状态查找人员范围
		 * 如果为1，则查找需求部所企标管理员
		 * 如果为企标管理员则查找所有人，
		 * 如果不是企标管理员只查找本部所所有人
		 */
		if(taskstate == 1){
			if(list.size() > 0 
					&& list.toString().contains(deptid_query)){
				return true;
			}
		}else{
			if(UtilString.isEmpty(count)){//wxx
				count = DBSql.getString(roleName_sql, new Object[]{CmccCommon.qbgly_role, userid});
			}
			//判断当前人是否为企标管理员
			if(!UtilString.isEmpty(count) && Integer.parseInt(count) > 0){//wxx
				//判断是否为研究院
				if(userid.contains("@hq.cmcc")){
					//当前人父部门ID
		    		String parentDeptId_current = uc.getDepartmentModel().getParentDepartmentId();
		    		//父部门Id全路径
		    		String pathParentDeptid = DepartmentCache.getModel(parentDeptId_current).getPathIdOfCache();
			    	//查询本单位所有部所
			    	if (deptid_query.contains(parentDeptId_current) 
			    			|| pathParentDeptid.contains(deptid_query)) {
			    		return true;
			    	}
				}else{
					//一级部门ID
					String firstDeptid = pathdeptId.split("/")[0];
					//遍历的一级部门ID
					String firstDeptid_model = deptid_query.split("/")[0];
					if(firstDeptid.equals(firstDeptid_model)){
						return true;
					}
				}
		}else{//wxx
			//判断是否为研究院
			if(userid.contains("@hq.cmcc")){
				//当前人跟部门全路径ID
	    		String pathDeptId_current = uc.getDepartmentModel().getPathIdOfCache();
		    	//查询本部所所有人
		    	if (pathDeptId_current.contains(deptid_query)) {
		    		return true;
		    	}
			}else{
				//一级部门ID
				String firstDeptid = pathdeptId.split("/")[0];
				//遍历的一级部门ID
				String firstDeptid_model = deptid_query.split("/")[0];
				if(firstDeptid.equals(firstDeptid_model)){
					return true;
				}
			}
			}
		}

		return false;
	}
	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, 
		UserModel model, AdvancedAddressModel advancedAddressModel) {
		if (UtilString.isEmpty(processId)) {
			processId = advancedAddressModel.getInstanceId();
		}
		if(UtilString.isEmpty(taskid)){
			taskid = advancedAddressModel.getTaskId();
		}
		if(taskstate <= 0){
			taskstate = SDK.getTaskAPI().getTaskInstance(taskid).getState();
		}
		/*if (UtilString.isEmpty(deptId)) {
			deptId = DBSql.getString(sql_deptId, new Object[] { processId });
		}*/
		/*if("".equals(deptId)){
			return false;
		}*/
		//技术部门ID全路径即为技术部门ID全路径
		//String pathdeptId = DepartmentCache.getModel("ec76faca-0265-4666-bd72-567934551f6f").getPathIdOfCache();
		String pathdeptId = DepartmentCache.getModel("1f5c54ba-1214-478f-b911-8e7018d3b74b").getPathIdOfCache();
		//获取需求部门企标管理员所在部门ID全路径的集合
		if(UtilString.isEmpty(sql_demandPeople)){
			sql_demandPeople = "SELECT GROUP_CONCAT(userid) userid "
					  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
					  		+ "WHERE ROLENAME = '企标管理员' AND locate(deptid, '"+pathdeptId+"' ) > 0";
			String userid = DBSql.getString(sql_demandPeople);
			if(!UtilString.isEmpty(userid)){
				//获取多个人
				String[] peoples = userid.split(",");
				for(int i = 0;i<peoples.length;i++){
					//人员账号
					String people = peoples[i];
					//人员集合
					userid_list.add(people);
				}
			}
		}
		/*
		 * 根据父任务状态查找人员范围
		 * 如果为1，则查找需求部所企标管理员
		 * 否则，查找需求单位所有人
		 */
		String userid = uc.getUID();
		if(taskstate == 1){
			if(userid_list.size() > 0 
					&& userid_list.toString().contains(model.getUID())){
				return true;
			}
			return false;
		}else{
			if(UtilString.isEmpty(count)){
				count = DBSql.getString(roleName_sql, new Object[]{CmccCommon.qbgly_role, userid});
			}
			//判断当前人是否为企标管理员
			if(!UtilString.isEmpty(count) && Integer.parseInt(count) > 0){
			}else{
				if(userid.contains("@hq.cmcc")){
					String ext1 = model.getExt1();
					if(CmccCommon.user_leave2.contains(ext1)){
						return false;
					}
				}
			}
		}
		return true;
	}
}


	


