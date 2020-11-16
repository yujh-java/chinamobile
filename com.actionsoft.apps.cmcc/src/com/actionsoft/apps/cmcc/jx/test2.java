package com.actionsoft.apps.cmcc.jx;
/**
 * 测试短信接口，未使用
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
public class test2 {
	protected static Logger logger = Logger.getLogger(test2.class);

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * 发送短信
		 */
		JSONObject obj = new JSONObject();
		obj.element("app", "WORK_FLOW");
		obj.element("channel", "sms");
		obj.element("target", "15201638414");
		//obj.element("message", "message");
		obj.element("message", "<p style='white-space: normal;'>【姓名】 &nbsp;你好：</p>" +
				"<p style='white-space: normal;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"您发起的流程【流程标题】在【节点名称】已超过5天，请做好线下沟通加快流程审批速度。</p>" +
				"<p style='white-space: normal;'><br/></p><p style='white-space: normal;'><br/></p>" +
				"<p style='white-space: normal;'><br/></p><p style='white-space: normal;'><br/></p>" +
				"<p style='white-space: normal;'><span style='color: rgb(179, 179, 179); " +
				"font-size: 12px; background-color: rgb(255, 255, 255);'>本邮件属系统邮件，来源于BPM业务流程管理系统，" +
				"</span><span style='font-size: 12px; background-color: rgb(255, 255, 255); " +
				"color: rgb(54, 54, 54);'>请勿回复</span><span style='color: rgb(179, 179, 179);" +
				" font-size: 12px; background-color: rgb(255, 255, 255);'>。</span></p>" +
				"<p style='white-space: normal;'><span style='color: rgb(179, 179, 179); " +
				"font-size: 12px; background-color: rgb(255, 255, 255);'>在系统使用过程中，如果有问题请联系IT服务热线：" +
				"&nbsp;<span style='color: rgb(54, 54, 54);'>XXX XXX XXXX</span></span></p>" +
				"<div><span style='color: rgb(179, 179, 179); font-size: 12px; background-color: rgb(255, 255, 255);'>" +
				"<span style='color: rgb(54, 54, 54);'><br/></span></span></div><p><br/></p>");
		/*JSONObject obj2 = new JSONObject();
		obj2.element("app", "WORK_FLOW");
		obj2.element("channel", "sms");
		obj2.element("target", "15201638414");
		obj2.element("message", "message2");
		JSONArray arr = new JSONArray();
		arr.add(obj);
		arr.add(obj2);*/
		postBody("http://10.1.5.142:8182/api/message/send?access_token=CYAWP7OYm1AE2uUg",obj.toString());
		
		/**
		 * 发送邮件接口
		 */
		/*JSONObject obj = new JSONObject();
		obj.element("app", "WORK_FLOW");
		obj.element("channel", "mail");
		obj.element("target", "1654425379@qq.com");
		obj.element("message", "<p style='white-space: normal;'>【姓名】 &nbsp;你好：</p>" +
				"<p style='white-space: normal;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"您发起的流程【流程标题】在【节点名称】已超过5天，请做好线下沟通加快流程审批速度。</p>" +
				"<p style='white-space: normal;'><br/></p><p style='white-space: normal;'><br/></p>" +
				"<p style='white-space: normal;'><br/></p><p style='white-space: normal;'><br/></p>" +
				"<p style='white-space: normal;'><span style='color: rgb(179, 179, 179); " +
				"font-size: 12px; background-color: rgb(255, 255, 255);'>本邮件属系统邮件，来源于BPM业务流程管理系统，" +
				"</span><span style='font-size: 12px; background-color: rgb(255, 255, 255); " +
				"color: rgb(54, 54, 54);'>请勿回复</span><span style='color: rgb(179, 179, 179);" +
				" font-size: 12px; background-color: rgb(255, 255, 255);'>。</span></p>" +
				"<p style='white-space: normal;'><span style='color: rgb(179, 179, 179); " +
				"font-size: 12px; background-color: rgb(255, 255, 255);'>在系统使用过程中，如果有问题请联系IT服务热线：" +
				"&nbsp;<span style='color: rgb(54, 54, 54);'>XXX XXX XXXX</span></span></p>" +
				"<div><span style='color: rgb(179, 179, 179); font-size: 12px; background-color: rgb(255, 255, 255);'>" +
				"<span style='color: rgb(54, 54, 54);'><br/></span></span></div><p><br/></p>");
		JSONObject extras = new JSONObject();
		extras.element("title", "测试邮件接口");
		obj.element("extras", extras.toString());
		postBody("http://10.1.5.142:8182/api/message/send?access_token=CYAWP7OYm1AE2uUg",obj.toString());
	*/
	}
	public static void postBody(String urlPath, String json) throws Exception {
		try{  
			URL url = new URL(urlPath);  
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();  
			// 设置doOutput属性为true表示将使用此urlConnection写入数据  
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true);
			// 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型  
			urlConnection.setRequestProperty("content-type", "application/json");  
			// 得到请求的输出流对象  
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());  
			// 把数据写入请求的Body  
			out.write(json);  
			out.flush();  
			out.close(); 

			// 从服务器读取响应  
			InputStream inputStream = urlConnection.getInputStream();  
			String result = convertStreamToString(inputStream);
			System.out.println(result);
			if(urlConnection.getResponseCode()==200){
				System.out.println("200000000000000000");
			}else{
				System.out.println("errrrrrrrrrrrrrrrrrr");;
			}
		}catch(IOException e){
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	public static String convertStreamToString(InputStream is) {      
         BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
         StringBuilder sb = new StringBuilder();      
     
         String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {      
                 sb.append(line + "\n");      
             }      
         } catch (IOException e) {      
             e.printStackTrace();      
         } finally {      
            try {      
                 is.close();      
             } catch (IOException e) {      
                 e.printStackTrace();      
             }      
         }      
     
        return sb.toString();      
     }
}
