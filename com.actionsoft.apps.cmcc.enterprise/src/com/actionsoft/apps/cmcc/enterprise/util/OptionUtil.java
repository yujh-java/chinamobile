package com.actionsoft.apps.cmcc.enterprise.util;

import com.actionsoft.bpms.util.UtilString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OptionUtil
{
  public static String optionMosaic(List<Map<String, String>> list, String tableName)
  {
    StringBuffer html = new StringBuffer();
    list = filterList(list);
    int size = list.size();
    for (int h1 = 0; h1 < size; h1++)
    {
      String msg = (String)((Map)list.get(h1)).get("msg");
      String deptname = (String)((Map)list.get(h1)).get("deptname");
      String userName = (String)((Map)list.get(h1)).get("userName");
      String createData = (String)((Map)list.get(h1)).get("createData");
      String zwdj = (String)((Map)list.get(h1)).get("zwdj");
      if (!UtilString.isEmpty(zwdj))
      {
        int zwdj_numb = Integer.parseInt(zwdj);
        if (zwdj_numb < 3) {
          msg = "<div style='font-weight:bolder'>" + msg + "</div>";
        }
      }
      if (h1 == 0)
      {
        html.append("<tr><td width=\"20%\" rowspan=\"" + size + "\"><div align=\"center\"><div align=\"center\">");
        html.append(tableName + "</div></td><td width=\"80%\"><div align=\"left\">");
        html.append(msg + "</div><div align=\"right\">(" + deptname + ")" + userName + createData + "</div></td></tr>");
      }
      else
      {
        html.append("<tr><td width=\"80%\"><div align=\"left\">");
        html.append(msg + "</div><div align=\"right\">(" + deptname + ")" + userName + createData + "</div></td></tr>");
      }
    }
    return html.toString();
  }
  
  public static List<Map<String, String>> filterList(List<Map<String, String>> list)
  {
    List<Map<String, String>> returnlist = new ArrayList();
    if ((list != null) && (list.size() > 0)) {
      for (int h1 = 0; h1 < list.size(); h1++)
      {
        String msg = (String)((Map)list.get(h1)).get("msg");
        //chenxf add 2018-7-12
		//填写意见人员
		String userName = list.get(h1).get("userName");
		/*
		 * 如果为管理员后台移交、跳转、激活等审批意见过滤
		 */
		if("管理员".equals(userName) || msg.contains("red")){
			continue;
		}
        if (!UtilString.isEmpty(msg)) {
          returnlist.add((Map)list.get(h1));
        }
      }
    }
    return returnlist;
  }
}
