package com.actionsoft.apps.cmcc.enterprise.util;

import com.actionsoft.apps.cmcc.enterprise.at.getUserByRoleId;
import com.actionsoft.apps.cmcc.enterprise.at.getUserIdByRoleData;
import com.actionsoft.apps.listener.PluginListener;
import com.actionsoft.apps.resource.AppContext;
import com.actionsoft.apps.resource.plugin.profile.AWSPluginProfile;
import com.actionsoft.apps.resource.plugin.profile.AtFormulaPluginProfile;
import java.util.ArrayList;
import java.util.List;

public class MyPlugins
  implements PluginListener
{
  public List<AWSPluginProfile> register(AppContext arg0)
  {
    List<AWSPluginProfile> list = new ArrayList();
    
    list.add(new AtFormulaPluginProfile("获取指定角色、部门下的人员", "@getUserByRoleId(roleId,deptId)", getUserByRoleId.class.getName(), "角色ID,部门ID", "参与者账号"));
    list.add(new AtFormulaPluginProfile("在角色维护表中获取账号", "@getUserIdByRoleData(processId,taskId,activityId,parentActivityId,roleName,deptId)", getUserIdByRoleData.class.getName(), "流程实例ID,任务实例ID,当前节点ID,父节点ID,角色名,部门ID", "参与者账号"));
    return list;
  }
}
