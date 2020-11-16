package com.actionsoft.apps.cmcc.budget.at;

import com.actionsoft.bpms.commons.at.AbstExpression;
import com.actionsoft.bpms.commons.at.ExpressionContext;
import com.actionsoft.bpms.org.cache.DepartmentCache;
import com.actionsoft.bpms.org.model.DepartmentModel;
import com.actionsoft.bpms.util.DBSql;
import com.actionsoft.exception.AWSExpressionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * 通过兼职角色ID和部门ID获取对应人员账号
 * @公式ID：@getUserByRoleId
 * 例如：@getUserByRoleId(roleId,deptId)
 * @author chenxf
 *
 */
public class getUserByRoleId
  extends AbstExpression
{
  public getUserByRoleId(ExpressionContext atContext, String expressionValue)
  {
    super(atContext, expressionValue);
  }
  
  public String execute(String expression)
    throws AWSExpressionException
  {
    String returnUser = "";
    
    String roleId = getParameter(expression, 1);
    
    String deptId_current = getParameter(expression, 2);
    
    String pathDeptId = DepartmentCache.getModel(deptId_current).getPathIdOfCache();
    

    Connection con = DBSql.open();
    PreparedStatement ps = null;
    ResultSet set = null;
    String sql = "select USERID,DEPARTMENTID from ORGUSERMAP where ROLEID = ?";
    try
    {
      ps = con.prepareStatement(sql);
      ps.setString(1, roleId);
      set = ps.executeQuery();
      while (set.next())
      {
        String userid = set.getString("USERID");
        
        String deptId = set.getString("DEPARTMENTID");
        
        String pathDeptId_while = DepartmentCache.getModel(deptId).getPathIdOfCache();
        if ((pathDeptId.contains(pathDeptId_while)) || 
          (pathDeptId_while.contains(pathDeptId))) {
          returnUser = returnUser + userid + " ";
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace(System.err);
      return "";
    }
    finally
    {
      DBSql.close(con, ps, set);
    }
    return returnUser;
  }
}
