package com.actionsoft.apps.cmcc;
/**
 * 注册类
 * @author nch
 * @date 20170622
 */
import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.cmcc.at.getCurHisStepUserExpression;
import com.actionsoft.apps.cmcc.at.getDeptImperByDeptIdExpress;
import com.actionsoft.apps.cmcc.at.getDeptProcessCreate;
import com.actionsoft.apps.cmcc.at.getHisStepUserExpression;
import com.actionsoft.apps.cmcc.at.getJkrDeptImperByDeptIdExpress;
import com.actionsoft.apps.cmcc.at.getNoteHisUserExpression;
import com.actionsoft.apps.cmcc.at.getParentProcessExpress;
import com.actionsoft.apps.cmcc.at.getPhyfjgDeptImperExpress;
import com.actionsoft.apps.cmcc.at.getProductManagerPeople;
import com.actionsoft.apps.cmcc.at.getRoleIdByRoleNameExpressipon;
import com.actionsoft.apps.cmcc.at.getStepUserExpression;
import com.actionsoft.apps.cmcc.at.getUserExt5;
import com.actionsoft.apps.cmcc.at.getZbyfjkrHisUserExpression;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;

public class Plugins implements PluginListener {

	public Plugins() {
    }
	public List<AWSPluginProfile> register(AppContext context) {
	    List<AWSPluginProfile> list = new ArrayList<AWSPluginProfile>();
	    
		list.add(new AtFormulaPluginProfile("获取指定节点、部门、角色参与者", "@getJkrDeptImperByDeptId(processId,activityId,roleId,deptID,preNoteid,noteid)", getJkrDeptImperByDeptIdExpress.class.getName(), "流程实例ID,节点ID,角色ID,部门ID,固定节点ID,当前节点ID", "参与者账号"));

		list.add(new AtFormulaPluginProfile("获取角色ID", "@getRoleIdByRoleName(roleName)", getRoleIdByRoleNameExpressipon.class.getName(), "角色名称", "角色ID"));
		list.add(new AtFormulaPluginProfile("获取项目管理人员", "@getProductManagerPeople(productType)", getProductManagerPeople.class.getName(), "项目类型","项目管理人员"));
		
		list.add(new AtFormulaPluginProfile("获得配合研发机构接口人", "@getPhyfjgDeptImper(processid)", getPhyfjgDeptImperExpress.class.getName(), "流程实例ID", "参与者账号"));
		list.add(new AtFormulaPluginProfile("获得指定节点创建子流程能参与者(立项)", "@getStepUser(processid,activityId)", getStepUserExpression.class.getName(), "流程实例ID,指定节点ID", "参与者账号"));
		list.add(new AtFormulaPluginProfile("获得父流程指定节点历史参与者", "@getHisStepUser(processid,activityId)", getHisStepUserExpression.class.getName(), "流程实例ID,指定节点ID", "参与者账号"));
		//chenxf add 2018-5-8
		list.add(new AtFormulaPluginProfile("获得指定节点历史参与者", "@getCurrentHisStepUser(processid,activityId,flag)", getCurHisStepUserExpression.class.getName(), "流程实例ID,指定节点ID,是否为第一节点", "参与者账号"));
		
		list.add(new AtFormulaPluginProfile("获取指定节点、部门、角色参与者、历史参与者", "@getNoteHisUser(processId,noteid,rolename,taskid,note,deptId)", getNoteHisUserExpression.class.getName(), "流程实例ID,节点ID,角色名称,任务实例ID,指定节点ID,部门ID","参与者账号"));
		list.add(new AtFormulaPluginProfile("获取流程的父流程实例ID", "@getParentProcess(processId)", getParentProcessExpress.class.getName(), "流程实例ID","父流程实例ID"));
		list.add(new AtFormulaPluginProfile("获取指定节点、角色参与者、历史参与者", "@getZbyfjkrHisUser(processId,noteid,rolename,taskid,note,noid)", getZbyfjkrHisUserExpression.class.getName(), "流程实例ID,节点ID,角色名称,任务实例ID,指定节点ID,指定节点ID","参与者账号"));
		list.add(new AtFormulaPluginProfile("获得流程实例创建者的部门ID", "@getDeptProcessCreate(processid)", getDeptProcessCreate.class.getName(), "流程实例ID", "流程创建者部门ID"));
		list.add(new AtFormulaPluginProfile("获取用户扩展字段中的某个属性值", "@getUserExt5(uid,field)", getUserExt5.class.getName(), "用户登陆帐户,扩展字段属性值", "获取扩展字段属性"));
		list.add(new AtFormulaPluginProfile("获取指定节点、部门、角色参与者", "@getDeptImperByDeptId(processId,activityId,roleId,deptID)", getDeptImperByDeptIdExpress.class.getName(), "流程实例ID,节点ID,角色ID,部门ID", "参与者账号"));

		return list;
	}
}
