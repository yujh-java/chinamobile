package com.actionsoft.apps.cmcc.integration.event.address;

import java.util.Iterator;
import java.util.List;
import com.actionsoft.apps.cmcc.integration.CMCCConst;
import com.actionsoft.apps.cmcc.integration.util.OrgUtil;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.SimulationPath;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.ProcessInstance;
import com.actionsoft.bpms.bpmn.engine.model.run.delegate.TaskInstance;
import com.actionsoft.bpms.commons.database.RowMap;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.cache.UserCache;
import com.actionsoft.bpms.org.model.CompanyModel;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.org.model.UserModel;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.ui.dict.address.base.AddressUIFilterInterface;
import com.actionsoft.bpms.ui.dict.address.constant.AddressUIConst;
import com.actionsoft.bpms.ui.dict.address.model.AdvancedAddressModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.exception.AWSIllegalArgumentException;
import com.actionsoft.sdk.local.SDK;

/**
 * @author yujh
 * @version 创建时间：2019年8月5日 下午3:04:13 公共路由方案，梳理所有路由可能方案
 */
public class CommonRouteScheme implements AddressUIFilterInterface {
	public String taskInstanceId = "";// 当前任务实例ID
	public String activityDefId = "";// 当前任务节点ID
	public String processDefId = "";// 当前流程定义ID
	public String processInstId = "";// 当前流程实例ID
	public String parentProcessInstId = "";// 当前父流程实例ID

	public TaskInstance taskInstance = null;// 当前任务实例
	public ProcessInstance processInstance = null; // 当前流程实例

	public BO signRoute = null; // 加签方案路由
	public BO arriveRoute = null; // 普通到达方案路由
	public BO backRoute = null; // 退回到达方案路由
	

	/** 路由信息 **/
	public Integer processFilter = 0;// 与流程场景相关
	public String processStepId = "";// 流程相关节点值
	public String companyId = "";// 公司ID
	public String companyEXT = "";// 公司扩展查询
	public String departmentId = "";// 部门ID
	public String departmentEXT = "";// 部门扩展查询
	public String personLevel = "";// 人员级别
	public String personLeverEXT = "";// 人员级别扩展
	public String roleId = "";// 角色ID
	public String roleEXT = "";// 角色扩展查询
	public String fixPerson = "";// 固定人
	public String fixPersonEXT = "";// 固定人扩展查询
	public String routeInterfaceUrl = "";// 第三方接口（url）
	public String backOptionName = "";// 退回路径审核菜单
	

	/** sql数据信息 ,存储在公共变量中,检少性能损耗 **/
	public List<RowMap> roleDepartmentMaps = null;// 固定角色-部门集合
	public List<RowMap> roleUserMaps = null;// 固定角色-人员集合
	public String process_companyId = null; // 与流程相关-某节点办理人部所（公司）
	public String process_departmentId = null; // 与流程相关-某节点办理人部门
	public String process_userId = null ; //与流程相关-某节点历史办理人

	public static UserContext me = null;// 当前登录人

	@Override
	public boolean addressUIFlexCompanyFilter(UserContext arg0, CompanyModel arg1, AdvancedAddressModel arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 过滤部门总方法
	 */
	public boolean addressUIFlexDepartmentFilter(UserContext uc, DepartmentModel model,
			AdvancedAddressModel advancedAddressModel) {

		// 如果没值则初始化参数
		if (judgeNewRoute(advancedAddressModel)) {// 判断是否需要初始化
			me = uc;
			processInstId = advancedAddressModel.getInstanceId();// 当前实例Id
			// 执行初始化操作，获取该任务相关流程信息
			init(processInstId);
			
			/** 判断任务类型，选择指定路由 **/
			if (advancedAddressModel.getTransType().equals(AddressUIConst.ADDRESS_TASK_WORKSIGNATURE)) {
				arriveRoute = signRoute;// 到达方案为加签
			}else if(null != backRoute && UtilString.isNotEmpty(backOptionName)){//如果为退回路由
				boolean backOptionChoice = SDK.getTaskAPI().isChoiceActionMenu(taskInstanceId, backOptionName);
				if(backOptionChoice){
					arriveRoute = backRoute;//到达方案为退回节点
				}
			}
			evalRouteInfo(arriveRoute);// 确定具体路由后，给路由相关信息赋值
		}
		

		

		// 判断是否与“流程信息”相关
		if (processFilter != null) {
			if (processFilter == CMCCConst.PROCESSINFO_IRRELEVANT) {// 不相关

			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_COMPANY) {// 与某节点公司相关
				if (null == process_companyId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, processInstId, processStepId, processInstId });
					process_departmentId = SDK.getORGAPI().getUser(process_userId).getDepartmentId(); 
					String pathIdOfCache = SDK.getORGAPI().getDepartmentById(process_departmentId).getPathIdOfCache();
					String[] userDeptPathIdArr = pathIdOfCache.split("/");
					String jt_deptId = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "JT_DEPTID");
					if(pathIdOfCache.contains(jt_deptId)){
						process_companyId = userDeptPathIdArr[1];
					}else{
						process_companyId= userDeptPathIdArr[0];
					}
				}
				if(false == matchCurrentDeptByAll(process_companyId, model)){
					return false;
				}
			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_DEPARTMENT) {// 与某节点部门相关
				if (null == process_departmentId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, processInstId, processStepId, processInstId });
					process_departmentId = SDK.getORGAPI().getUser(process_userId).getDepartmentId();
				}
				//如果是集团公司，且选择了不包含子部门，且选择了公司领导的，过滤掉3级（处室部门）
				if(model.getId().equals("08ddf703-26bb-400e-a140-b6e7b7df7638")){
					System.err.println(">>>>:"+process_userId.indexOf("@hq.cmcc"));
					System.err.println(">>>>:"+personLevel.equals("2"));
					System.err.println(">>>>:"+!departmentEXT.equals("1"));
					System.err.println(">>>>:"+model.getLayer());
				}
				if(process_userId.indexOf("@hq.cmcc")>0 && personLevel.equals("2") && !departmentEXT.equals("1") && model.getLayer()>2){ 
					return false;
				}
				if (false == matchCurrentDept(process_departmentId, model)) {
					return false;
				}
			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_HENDLER) {// 与某节点办理人相关
				if (null == process_userId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, processInstId, processStepId, processInstId });
					process_departmentId = SDK.getORGAPI().getUser(process_userId).getDepartmentId();
				}
				if (false == matchCurrentDept(process_departmentId, model)) {
					return false;
				}
			} else if (processFilter == CMCCConst.PROCESSINFO_PARENTSTEP_HENDLER) {//与父流程某节点办理人相关
				if (null == process_departmentId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, parentProcessInstId, processStepId, parentProcessInstId });
					process_departmentId = SDK.getORGAPI().getUser(process_userId).getDepartmentId();
				}
				if (false == matchCurrentDept(process_departmentId, model)) {
					return false;
				}
			}
		}

		// 判断是否与“固定角色”相关
		if (UtilString.isNotEmpty(roleId)) {// 找出全部相关角色
			if (null == roleDepartmentMaps) {// 为空查询数据，避免重复查询，资源浪费
				String sql = "SELECT DEPARTMENTID FROM ORGUSERMAP WHERE ROLEID = ?";
				roleDepartmentMaps = DBSql.getMaps(sql, new Object[] { roleId });
				advancedAddressModel.setDisplayMap(true);// 如果与角色相关，则显示兼职角色
			}

			if (false == matchCurrentDept(roleDepartmentMaps, model)) {
				return false;
			}
		}
		
		/** 判断并组合参数 -----开始----- **/
		// 判断是否与“固定人”相关
		if (UtilString.isNotEmpty(fixPerson)) {
			if (UtilString.isEmpty(SDK.getORGAPI().validateUsers(fixPerson))) {
				String fixPersonDepartmentId = SDK.getORGAPI().getUser(fixPerson).getDepartmentId();
				if (true == matchCurrentDept(fixPersonDepartmentId, model)) {
					return true;
				}
			}
		}

		// 判断是否与“固定部门”相关
		if (UtilString.isNotEmpty(departmentId)) {
			if (false == matchCurrentDept(departmentId, model)) {
				return false;
			}
		}
		
		/** 判断并组合参数 -----结束----- **/
		return true;
	}

	/**
	 * 过滤人员总方法
	 */
	public boolean addressUIFlexUserFilter(UserContext uc, UserModel userModel,
			AdvancedAddressModel advancedAddressModel) {
		// 如果没值则初始化参数
		if (judgeNewRoute(advancedAddressModel)) {// 判断是否需要初始化
			me = uc;
			processInstId = advancedAddressModel.getInstanceId();// 当前实例Id
			// 执行初始化操作，获取该任务相关流程信息
			init(processInstId);
			/** 判断任务类型，选择指定路由 **/
			if (advancedAddressModel.getTransType().equals(AddressUIConst.ADDRESS_TASK_WORKSIGNATURE)) {
				arriveRoute = signRoute;// 到达方案为加签
			}else if(null != backRoute && UtilString.isNotEmpty(backOptionName)){//如果为退回路由
				boolean backOptionChoice = SDK.getTaskAPI().isChoiceActionMenu(taskInstanceId, backOptionName);
				if(backOptionChoice){
					arriveRoute = backRoute;//到达方案为退回节点
				}
			}
			evalRouteInfo(arriveRoute);// 确定具体路由后，给路由相关信息赋值
		}

		// 判断是否与“人员级别”相关
		if (UtilString.isNotEmpty(personLevel)) {
			userModel.getUID();
			if (!userModel.getExt1().equals(personLevel)) {
				return false;
			}else{
				return true;
			}
		}

		// 判断是否与“流程信息”相关
		if (processFilter != null) {
			if (processFilter == CMCCConst.PROCESSINFO_IRRELEVANT) {// 不相关

			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_COMPANY) {// 与某节点公司相关
				
			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_DEPARTMENT) {// 与某节点部门相关
				//if(UtilString.isEmpty(personLevel) && UtilString.isEmpty(roleId)){// 判断是否只找部门下所有人，则过滤父部门的人
					if (null == process_departmentId) {// 为空查询数据，避免重复查询，资源浪费
						String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
								+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
						String userId = DBSql.getString(sql,
								new Object[] { processStepId, processInstId, processStepId, processInstId });
						process_departmentId = SDK.getORGAPI().getUser(userId).getDepartmentId();
					}
					if (!userModel.getDepartmentId().equals(process_departmentId)) {
						return false;
					}
				//}
			} else if (processFilter == CMCCConst.PROCESSINFO_STEP_HENDLER) {//与某节点办理人相关
				if (null == process_userId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, processInstId, processStepId, processInstId });
				}
				if(!process_userId.equals(userModel.getUID())){
					return false;
				}
			} else if (processFilter == CMCCConst.PROCESSINFO_PARENTSTEP_HENDLER) {//与父流程某节点办理人相关
				if (null == process_departmentId) {// 为空查询数据，避免重复查询，资源浪费
					String sql = "SELECT TARGET FROM WFC_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? "
							+ " UNION SELECT TARGET FROM WFH_TASK WHERE ACTIVITYDEFID = ? AND PROCESSINSTID = ? ";
					process_userId = DBSql.getString(sql,
							new Object[] { processStepId, parentProcessInstId, processStepId, parentProcessInstId });
				}
				if(!process_userId.equals(userModel.getUID())){
					return false;
				}
			}
		}
		
		// 判断是否与“固定角色”相关
		if (UtilString.isNotEmpty(roleId)) {// 找出全部相关角色
			if (null == roleUserMaps) {// 为空查询数据，避免重复查询，资源浪费
				String sql = "SELECT USERID FROM ORGUSERMAP WHERE ROLEID = ?";
				roleUserMaps = DBSql.getMaps(sql, new Object[] { roleId });
				advancedAddressModel.setDisplayMap(true);// 如果与角色相关，则显示兼职角色
			}
			if (roleUserMaps.size() > 0) {
				boolean flag = false;
				for (RowMap rowMap : roleUserMaps) {
					String userId = rowMap.getString("USERID");
					if (userId.equals(userModel.getUID())) {
						flag = true;
						break;
					}
				}
				if (flag == false) {
					return false;
				}
			}
		}


		return true;
	}

	/**
	 * 是否需要重新初始化路由集合
	 * 
	 * @param advancedAddressModel
	 * @return
	 */
	public boolean judgeNewRoute(AdvancedAddressModel advancedAddressModel) {
		// 任务ID为空，或者匹配不上
		if (UtilString.isEmpty(taskInstanceId)) {
			return true;
		}

		// 目标节点ID为空，或者匹配不上
		if (UtilString.isEmpty(activityDefId)) {
			return true;
		}
		return false;

	}

	/**
	 * 初始化方法（根据任务实例获取流程相关信息）
	 * 
	 * @param processInstId
	 */
	public void init(String processInstId) {
		processInstance = SDK.getProcessAPI().getInstanceById(processInstId);// 获取当前流程实例
		taskInstance = SDK.getTaskAPI().query().processInstId(processInstId).target(me.getUID()).orderBy("BEGINTIME")
				.desc().list().get(0);
		taskInstanceId = taskInstance.getId();
		activityDefId = getNextPathActivityId();// 获取任务节点ID
		processDefId = processInstance.getProcessDefId();// 获取当前流程定义ID
		parentProcessInstId = processInstance.getParentProcessInstId();// 获取父流程实例ID

		/** 获取路由集合信息 -----开始----- */
		getTaskRoutesInfo();
		/** 获取路由集合信息 -----结束----- */
	}

	/**
	 * 获取路由集合信息
	 */
	public void getTaskRoutesInfo() {
		String masterBindId = SDK.getBOAPI()
				.getByKeyField(CMCCConst.ROUTE_MASTER_TABLE, "PROCESSDEFIDINFO", processDefId).getBindId();
		List<BO> stepList = SDK.getBOAPI().query(CMCCConst.ROUTE_CHILD_TABLE, false).bindId(masterBindId)
				.addQuery("STEPID=", activityDefId).orderBy("UPDATEDATE").desc().list();
		if (stepList.size() > 0) {
			String id = stepList.get(0).getId();
			// 查询目标节点普通任务到达路由
			List<BO> arriveRouteList = SDK.getBOAPI().query(CMCCConst.ROUTE_FIELD_TABLE, false).bindId(id)
					.addQuery("TASKTYPE=", "1").list();
			if (arriveRouteList.size() > 0) {
				arriveRoute = arriveRouteList.get(0);
			}

			// 查询目标节点加签任务到达路由
			List<BO> signRouteList = SDK.getBOAPI().query(CMCCConst.ROUTE_FIELD_TABLE, false).bindId(id)
					.addQuery("TASKTYPE=", "2").list();
			if (signRouteList.size() > 0) {
				signRoute = signRouteList.get(0);
			}
			
			// 查询目标节点退回任务到达路由
			List<BO> backRouteList = SDK.getBOAPI().query(CMCCConst.ROUTE_FIELD_TABLE, false).bindId(id)
					.addQuery("TASKTYPE=", "3").list();
			if (backRouteList.size() > 0) {
				backRoute = backRouteList.get(0);
				backOptionName = backRoute.getString("BACKOPTIONNAME");
			}
		} else {
			throw new AWSIllegalArgumentException("流程节点信息未维护，请检查");
		}
	}

	/**
	 * 给全局变量(路由信息相关字段)赋值
	 * 
	 * @param bo
	 */
	public void evalRouteInfo(BO bo) {
		processFilter = Integer.valueOf(bo.get("PROCESSFILTER").toString());
		processStepId = bo.getString("PROCESSSTEPID");
		companyId = bo.getString("COMPANYID");
		companyEXT = bo.getString("COMPANYEXT");
		departmentId = bo.getString("DEPARTMENTID");
		departmentEXT = bo.getString("DEPARTMENTEXT");
		personLevel = bo.getString("PERSONLEVEL");
		personLeverEXT = bo.getString("PERSONLEVEREXT");
		roleEXT = bo.getString("ROLEEXT");
		fixPerson = bo.getString("FIXPERSON");
		fixPersonEXT = bo.getString("FIXPERSONEXT");
		routeInterfaceUrl = bo.getString("ROUTEINTERFACEURL");
		backOptionName = bo.getString("BACKOPTIONNAME");
		OrgUtil orgUtil = new OrgUtil();
		roleId = orgUtil.convertRoleId(bo.getString("ROLEID"));// 需要先转化
				
	}

	/**
	 * 根据部门ID筛选树
	 * 
	 * @param departmentId
	 * @param model
	 * @return
	 */
	public boolean matchCurrentDept(String departmentId, DepartmentModel model) {
		String pathIdOfCache = DepartmentCache.getModel(departmentId).getPathIdOfCache();
		String modelPath = DepartmentCache.getModel(model.getId()).getPathIdOfCache();
		if (pathIdOfCache.contains(model.getId())) {
			return true;
		}else if(departmentEXT.equals("1") && modelPath.contains(pathIdOfCache)){ //包含当前子部门
			if(UtilString.isNotEmpty(personLevel)){
				Iterator<UserModel> userListOfDepartment = UserCache.getUserListOfDepartment(model.getId());
				while (userListOfDepartment.hasNext()) {
					if (userListOfDepartment.next().getExt1().equals(personLevel)) {// 如果子部门下有相关级别人员也显示
						return true;
					}
				}
				return false;
			}
			return true;
		}else {
			return false;
		} 
	}
	
	/**
	 * 根据部门ID筛选树(遍历整个部所)
	 * 
	 * @param departmentId
	 * @param model
	 * @return
	 */
	public boolean matchCurrentDeptByAll(String departmentId, DepartmentModel model) {
		String pathIdOfCache = DepartmentCache.getModel(departmentId).getPathIdOfCache();
		String modelPath = DepartmentCache.getModel(model.getId()).getPathIdOfCache();
		if (pathIdOfCache.contains(modelPath) || modelPath.contains(pathIdOfCache)) {
			return true;
		}else{
			return false;
		}
	}
	
	

	/**
	 * 根据部门ID筛选树
	 * 
	 * @param departmentId
	 * @param model
	 * @return
	 */
	public boolean matchCurrentDept(List<RowMap> departmenList, DepartmentModel model) {
		if (departmenList.size() > 0) {
			boolean flag = false;
			for (RowMap rowMap : departmenList) {
				String departmentId = rowMap.getString("DEPARTMENTID");
				String pathIdOfCache = DepartmentCache.getModel(departmentId).getPathIdOfCache();
				if (pathIdOfCache.contains(model.getId())) {
					flag = true;
					break;
				}
			}
			return flag;
		} else {
			return false;
		}
	}

	/**
	 * 获取下一节点的节点ID
	 * @return
	 */
	public String getNextPathActivityId() {
		List<SimulationPath> simulationNextPath = SDK.getTaskAPI().simulationNextPath(me, processInstance,
				taskInstance);
		if (simulationNextPath.size() > 0) {
			return simulationNextPath.get(0).getId();
		}
		return "";
	}

}
