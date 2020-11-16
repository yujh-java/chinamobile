package com.actionsoft.apps.cmcc.lxAddress;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.CmccConst;
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
 * 计划外流程
 * 参与者地址薄过滤事件
 * 范围：所有部所领导+项目总监角色，单人处理
 * 项目总监或所主管审核节点
 * @author wxx
 * @date 20181121
 */
public class JHW_xmzjLeaderUserAddress implements AddressUIFilterInterface{
	private String leaderDeptId = "";//处长所在部门ID
	private List<String> list_userPathids  = new ArrayList<String>();//部长所在部门全路径 
	private List<String> list_userids  = new ArrayList<String>();//部长角色所有人员账号
	/*
	 * 在企标角色维护表中：BO_ACT_ENTERPRISE_ROLEDATA
	 * 查询项目总监
	 */
	public String sql_demandPeople = "";
	//判断是否是项目总监
	public String count;
	//项目总监所在部门全路径集合
	public List<String> userid_list = new ArrayList<String>();

	@Override
	public boolean addressUIFlexDepartmentFilter(UserContext uc,
			DepartmentModel model, AdvancedAddressModel advancedAddressModel) {
		String userpathIdofCache = uc.getDepartmentModel().getPathIdOfCache();//登录人部门ID全路径
		String deptPathId_model = model.getPathIdOfCache();//部门全路径
        String deptPathId_model1 = DepartmentCache.getModel(CmccConst.deptid_yjy).getPathIdOfCache();
        
		
		if(deptPathId_model1.contains(deptPathId_model) || deptPathId_model.contains(deptPathId_model1)){
			return true;
		}

		return false;
	}

	@Override
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel model,
			AdvancedAddressModel advancedAddressModel) {
		String deptPathId_model1 = DepartmentCache.getModel(CmccConst.deptid_yjy).getPathIdOfCache();
		
		
		//如果流程状态为11，说明是会签的环节发来的，也就是项目总监发来的，所有这里显示项目经理的地址簿
		
		int state=SDK.getTaskAPI().getTaskInstance(advancedAddressModel.getTaskId()).getState();//获得流程状态，先得到任务实例id，advancedAddressModel.getTaskId()得到任务实例id
        if(state == 11 && uc.getUID().contains("@hq.cmcc")){
        	String kzzd=model.getExt1();
    		//显示出所有的人，不分层级
    		if(CmccConst.user_leave3.equals("0") || "0".equals(kzzd)){
    			return false;
    		}
    		return true;
        
		
        }else{//否则，就是从起草环节发来的，到达项目总监会签，这里显示项目总监和部所领导
        	
        	SDK.getProcessAPI().getInstanceById(advancedAddressModel.getInstanceId()).getTitle();//advancedAddressModel.getInstanceId()是得到流程实例id，根据流程实例id可以得到流程标题，创建人等
	        
	        if(UtilString.isEmpty(sql_demandPeople)){
				sql_demandPeople = "SELECT GROUP_CONCAT(userid) userid "
						  		+ "FROM BO_ACT_ENTERPRISE_ROLEDATA "
						  		+ "WHERE ROLENAME = '项目总监' AND locate(deptid, '"+deptPathId_model1+"' ) > 0";
				String userids = DBSql.getString(sql_demandPeople);
				if(!UtilString.isEmpty(userids)){
					//获取多个人
					String[] peoples = userids.split(",");
					for(int i = 0;i<peoples.length;i++){
						//人员账号
						String people = peoples[i];
						//人员集合
						userid_list.add(people);
					}
				}
			}
	        String userid = uc.getUID();//用户账号
	        String kzzd=model.getExt1();
	        if(userid_list.size() > 0 
					&& userid_list.toString().contains(model.getUID()) && CmccConst.user_leave3.equals("3") || "3".equals(kzzd)){
				return true;
			}
			return false;
        }
			
	}

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0,
			CompanyModel model, AdvancedAddressModel arg2) {
		return true;
	}

	

}
