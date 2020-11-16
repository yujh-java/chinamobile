package com.actionsoft.apps.cmcc.integration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.actionsoft.apps.cmcc.integration.CMCCConst;

public class CmccUrlUtil {
	public static void main(String[] args) {
		String processType = "open";
		String processID = "123";
		String url = "http://keyan1.dev.cmri.cn/api/workflow/reqous?type="+processType+"&process_id="+processID;
		System.out.println(url);
		System.out.println(get(url));
	}
	public static String get(String url){
		return post(url, null,null);
	}
	public static String get(String url, String charset){
		return post(url, null, charset);
	}
	public static String post(String url, Map<String, String> param){
		return post(url, param, null);
	}
	public static String post(String url, Map<String, String> param, String outCharset)
	{
		if (outCharset == null) {
			outCharset = "UTF-8";
		}
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection)u.openConnection();
			conn.setConnectTimeout(CMCCConst.ipm_connectTime);
			conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36");
			StringBuilder sb = null;
			if (param != null) {
				sb = new StringBuilder();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);

				OutputStream out = conn.getOutputStream();

				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

				for (Map.Entry s : param.entrySet()) {
					sb.append((String)s.getKey()).append("=").append((String)s.getValue()).append("&");
				}
				writer.write(sb.deleteCharAt(sb.toString().length() - 1).toString());
				writer.close();
				sb = null;
			}
			conn.connect();
			sb = new StringBuilder();

			int recode = conn.getResponseCode();
			BufferedReader reader = null;
			if (recode == 404) {
				System.out.println("404===>" + url);
			}
			if (recode == 200)
			{
				InputStream in = conn.getInputStream();
				String encoding = conn.getContentEncoding();
				if ((encoding != null) && (encoding.equalsIgnoreCase("gzip"))) {
					GZIPInputStream gis = new GZIPInputStream(in);
					reader = new BufferedReader(new InputStreamReader(gis, outCharset));
					for (String str = reader.readLine(); str != null; str = reader.readLine())
						sb.append(str).append(System.getProperty("line.separator"));
				}
				else {
					reader = new BufferedReader(new InputStreamReader(in, outCharset));
					for (String str = reader.readLine(); str != null; str = reader.readLine()) {
						sb.append(str).append(System.getProperty("line.separator"));
					}
				}

				reader.close();
				if (sb.toString().length() == 0) {
					return null;
				}
				String str1 = sb.toString().substring(0, sb.toString().length() - System.getProperty("line.separator").length());
				return str1;
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return null;
	}

}
