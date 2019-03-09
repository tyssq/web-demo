package com.itapgo.web.demo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/**
 *
 * @作者   入梦炼心
 * @日期 2017年8月2日
 */
public class HttpUtil {
	
	/**
	 * 模拟form表单提交
	 * @作者 入梦炼心
	 * @创建时间 2017年9月30日
	 * @param paramMap
	 * @param files
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String doPost(Map<String, String> paramMap, File[] files, String url) throws HttpException, IOException{
		PostMethod filePost = new PostMethod(url); 
		if (files == null) {
			files = new File[]{};
		}
		int len = paramMap.size() + files.length;
		Part[] parts = new Part[len];
		int i = 0;
		for (File file : files) {
			parts[i++] = new FilePart("files", file);
		}
		for (String key : paramMap.keySet()) {
			parts[i++] = new StringPart(key, paramMap.get(key), "utf-8");
		}
		filePost.addRequestHeader("Accept-Charset", "utf-8");
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams())); 
		HttpClient clients = new HttpClient();

		int status = clients.executeMethod(filePost); 
		try { 
		    BufferedReader rd = new BufferedReader(new InputStreamReader( 
		            filePost.getResponseBodyAsStream(), "UTF-8")); 
		    StringBuffer stringBuffer = new StringBuffer(); 
		    String line; 
		    while ((line = rd.readLine()) != null) { 
		        stringBuffer .append(line); 
		    } 
		    rd.close(); 
		    System.out.println("接受到的流是：" + stringBuffer + "—-" + status); 
		    return stringBuffer.toString();
		} catch (Exception e) { 
		    throw new RuntimeException("error",e);
		}
	}
	
	/**
	 * 发起请求
	 * @作者 入梦炼心
	 * @创建时间 2017年8月2日
	 * @param reqUrl
	 * @param data
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String reqUrl, String data, String method) throws Exception{
		
		if (method != null) {
			method = method.toUpperCase();
			if (data == null) {
				method = null;
			}
		}
		
		URL localURL = new URL(reqUrl);
		URLConnection connection = localURL.openConnection();
		//此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection,
		//故此处最好将其转化为HttpURLConnection类型的对象,以便用到HttpURLConnection更多的API.如下:
		HttpURLConnection httpUrlConnection  = (HttpURLConnection)connection;
		
		//设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
		httpUrlConnection.setDoOutput(true);
		
		//设置是否从httpUrlConnection读入，默认情况下是true;
		httpUrlConnection.setDoInput(true);
		
		//Post请求不能使用缓存
		httpUrlConnection.setUseCaches(false);
		
		//设定传送的内容类型是可序列化的java对象(如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) 
		httpUrlConnection.setRequestProperty("content-type", "application/json");
		
		//设定请求的方法，默认是GET
		if ("POST".equals(method)) {
			httpUrlConnection.setRequestMethod(method);
		}
		
		//连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成，
		httpUrlConnection.connect();
		
		if ("POST".equals(method)) {
			OutputStreamWriter outStrm = null;
			try {
				//此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，所以在开发中不调用上述的connect()也可以)。
				outStrm = new OutputStreamWriter(httpUrlConnection.getOutputStream());
				outStrm.write(data);
				outStrm.flush();
			} finally {
				if (outStrm != null) {
					// 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器 
					outStrm.close();
				}
			}
		}
		
		StringBuffer resultBuffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			if (httpUrlConnection.getResponseCode() >= 300) {
				 throw new Exception("请求code为 " + httpUrlConnection.getResponseCode());
			}
			//调用HttpURLConnection连接对象的getInputStream()函数,将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
			reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));
			
			String tempLine = null;
			while ((tempLine = reader.readLine()) != null) {
				 resultBuffer.append(tempLine);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return resultBuffer.toString();
	}
}
