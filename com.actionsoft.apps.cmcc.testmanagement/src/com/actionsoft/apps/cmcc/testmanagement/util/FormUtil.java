package com.actionsoft.apps.cmcc.testmanagement.util;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2019年3月19日 下午4:02:47 
* 类说明 
*/
public class FormUtil {
	/**
	  * 保存第三方表单为快照
	  * @author chenxf
	  * @date   2018年9月29日 下午5:40:57
	  * @param uc
	  * @param bodyHTML
	  * @param headHTML
	  * @return
	  */
	  public String splicingStaticHtml(String bodyHTML,String headHTML){
		  StringBuffer sb = new StringBuffer();
		  sb.append("<!DOCTYPE html>");
		  sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"zh_CN\">");
		  sb.append("<head>");
		  sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">");
		  sb.append(headHTML);
		  sb.append("</head>");
		  sb.append("<body>");
		  sb.append(bodyHTML);
		  sb.append("</body>");
		  sb.append("</html>");
		  
		  return sb.toString();
	  }
}
