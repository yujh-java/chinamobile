package com.actionsoft.apps.cmcc.standardization.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.util.UtilString;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2019年4月28日 下午2:35:03 
* 类说明 
*/
public class OptionUtil {
	/**
	 * 审批意见拼接html
	 * @param list
	 * @param tableName
	 * @return
	 */
	public static String optionMosaic(List<Map<String,String>> list,String tableName){
		StringBuffer html = new StringBuffer();
		list = filterList(list);//过滤审批意见list集合，审批意见msg为空，不显示
		int size = list.size();
		for(int h1 = 0;h1 < size; h1++){
			String msg = list.get(h1).get("msg");
			String deptname = list.get(h1).get("deptname");
			String userName = list.get(h1).get("userName");
			
			String createData = list.get(h1).get("createData");
			String zwdj = list.get(h1).get("zwdj");
			if(!UtilString.isEmpty(zwdj)){
				int zwdj_numb = Integer.parseInt(zwdj);
				if(zwdj_numb < 3){//font-size:18px;
					msg = "<div style='font-weight:bolder'>"+msg+"</div>";
				}
			}
			if(h1 == 0){
				html.append("<tr><td width=\"20%\" rowspan=\""+size+"\"><div align=\"center\"><div align=\"center\">");
				html.append(tableName+"</div></td><td width=\"80%\"><div align=\"left\">");
				html.append(msg + "</div><div align=\"right\">("+deptname+")"+userName+createData+"</div></td></tr>");
			}else{
				html.append("<tr><td width=\"80%\"><div align=\"left\">");
				html.append(msg + "</div><div align=\"right\">("+deptname+")"+userName+createData+"</div></td></tr>");
			}
		}
		return html.toString();
	}
	
	/**
	 * 过滤审批意见list集合，审批意见msg为空，不显示
	 * @author nch
	 * @date 2017-7-11
	 * @param list
	 * @return
	 */
	public static List<Map<String,String>> filterList(List<Map<String,String>> list){
		List<Map<String,String>> returnlist = new ArrayList<Map<String,String>>();
				
		if(list != null && list.size()>0){
			for(int h1 = 0;h1 < list.size(); h1++){
				String msg = list.get(h1).get("msg");
				//chenxf add 2018-5-8
				//填写意见人员
				String userName = list.get(h1).get("userName");
				/*
				 * 如果为管理员后台移交、跳转、激活等审批意见过滤
				 */
				if("管理员".equals(userName) || msg.contains("red")){
					continue;
				}
				if(!UtilString.isEmpty(msg)){
					returnlist.add(list.get(h1));
				}
			}
		}
		return returnlist;
	}
}
