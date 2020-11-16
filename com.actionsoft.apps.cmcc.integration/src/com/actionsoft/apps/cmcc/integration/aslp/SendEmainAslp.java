package com.actionsoft.apps.cmcc.integration.aslp;
/**
 * 发送邮件、短信ASLP
 * @author niech
 * @date 20170926
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import net.sf.json.JSONObject;

import com.actionsoft.apps.resource.interop.aslp.ASLP;
import com.actionsoft.apps.resource.interop.aslp.Meta;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.sdk.local.SDK;

public class SendEmainAslp implements ASLP{
	@Override
	@Meta(parameter = { "name:'mapMsg',desc:'邮件、短信相关信息'"}) 
	public ResponseObject call(Map<String, Object> params) {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>)params.get("mapMsg");
		String ISEMAIL = map.get("ISEMAIL");//是否发送邮件
		String ISMESSAGE = map.get("ISMESSAGE");//是否发送短信
		String app = SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "app");
		String urlPath =SDK.getAppAPI().getProperty("com.actionsoft.apps.cmcc.integration", "urlPath");

		if("是".equals(ISEMAIL)){
			String email =  map.get("TOEMAIL");//邮件收件人邮箱
			String emailContext = map.get("EMAIL_CONTENT");//邮件内容
			String title = map.get("TITLE");//邮件标题

			JSONObject obj = new JSONObject();
			obj.element("app", app);
			obj.element("channel", "mail");
			obj.element("target", email);
			obj.element("message",emailContext);
			JSONObject extras = new JSONObject();
			extras.element("title", title);
			obj.element("extras", extras.toString());
			sendMag(urlPath,obj.toString());
		}
		if("是".equals(ISMESSAGE)){
			String mobile = map.get("MOBILE");//短信收件人电话号码
			String mobileContext = map.get("SHORTMESSAGE");//短信内容
			JSONObject obj = new JSONObject();
			obj.element("app", app);
			obj.element("channel", "sms");
			obj.element("target", mobile);
			obj.element("message", mobileContext);
			sendMag(urlPath,obj.toString());
		}
		return null;
	}
	/**
	 * 发送短信、邮件方法
	 * @author nch
	 * @date 2017-9-26
	 * @param urlPath
	 * @param json
	 */
	@SuppressWarnings("static-access")
	public static void sendMag(String urlPath, String json){
		try{  
			URL url = new URL(urlPath);  
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();  
			// 设置doOutput属性为true表示将使用此urlConnection写入数据  
			urlConnection.setDoOutput(true); 
			urlConnection.setDoInput(true);
			// 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型  charset=UTF-8; 
//			urlConnection.setRequestProperty("content-type", "application/json;charset=GBK; ");  
			urlConnection.setRequestProperty("content-type", "application/json;charset=UTF-8; ");
			// 得到请求的输出流对象  
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());  
			// 把数据写入请求的Body  
			out.write(json);  
			out.flush();  
			out.close(); 

			// 从服务器读取响应  
			InputStream inputStream = urlConnection.getInputStream();  
			String result = convertStreamToString(inputStream);
			if(urlConnection.getResponseCode()!=200){
				//调用接口失败,记录失败信息
				SDK.getLogAPI().getLogger(SendEmainAslp.class).error("调用短信邮件接口失败，失败信息："+result);
			}
		}catch(IOException e){
			e.printStackTrace(System.err);
		}
	}
	/**
	 * 处理邮件、短信接口返回结果
	 * @author nch
	 * @date 2017-9-26
	 * @param is
	 * @return
	 */
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
