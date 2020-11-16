package com.actionsoft.apps.cmcc.budget.util;


import com.actionsoft.apps.cmcc.budget.at.getBudgetUserIdByRoleData;
import com.actionsoft.apps.cmcc.budget.at.getCurHisStepUserExpression;
import com.actionsoft.apps.cmcc.budget.at.getDeptImperByDeptIdExpress;
import com.actionsoft.apps.cmcc.budget.at.getUserByRoleId;
import com.actionsoft.apps.cmcc.budget.event.ProcessPubicEvent;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.ProcessPublicEventPluginProfile;

import java.util.ArrayList;
import java.util.List;

public class MyPlugins
  implements PluginListener
{
  public List<AWSPluginProfile> register(AppContext arg0)
  {
    List<AWSPluginProfile> list = new ArrayList();
    
    list.add(new AtFormulaPluginProfile("获取指定角色、部门下的人员", "@getUserByRoleId(roleId,deptId)", getUserByRoleId.class.getName(), "角色ID,部门ID", "参与者账号"));
    list.add(new AtFormulaPluginProfile("在角色维护表中获取账号", "@getBudgetUserIdByRoleData(roleName,deptId)", getBudgetUserIdByRoleData.class.getName(), "角色名,部门ID", "参与者账号"));
    //list.add(new AtFormulaPluginProfile("获取指定节点、部门、角色参与者", "@getDeptImperByDeptId(processId,activityId,roleId,deptID)", getDeptImperByDeptIdExpress.class.getName(), "流程实例ID,节点ID,角色ID,部门ID", "参与者账号"));
    list.add(new AtFormulaPluginProfile("获得指定节点历史参与者", "@getCurrentHisStepUser(processid,activityId,flag)", getCurHisStepUserExpression.class.getName(), "流程实例ID,指定节点ID,是否为第一节点", "参与者账号"));
    list.add(new AtFormulaPluginProfile("获取指定节点、部门、角色参与者", "@getBudgetDeptImperByDeptId(processId,activityId,roleId,taskid,deptID)", getDeptImperByDeptIdExpress.class.getName(), "流程实例ID,节点ID,角色ID,任务实例ID,部门ID", "参与者账号"));
    // 注册流程/预算第一节点加签任务办理后 回调接口 ----
    list.add(new ProcessPublicEventPluginProfile(ProcessPubicEvent.class.getName(), "注册流程/预算第一节点加签任务办理后 回调接口"));
    return list;
  }
}
