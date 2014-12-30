package freelancer.worldvideo.net;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
/**
 * HttpSever:用于从服务器获取数据及发送数据到服务器
 * @author jiangbing
 *
 */
public class HttpServer 
{
	private HttpClient httpClient;
	private HttpPost httpPost;
	private static HttpResponse httpResponse;
	private String url;

	public HttpServer() 
	{
		init();
	}

	/**
	 * 初始化
	 * 
	 * @param url
	 */
	private void init() {
		try {
			httpClient = new DefaultHttpClient();
			// 设置连接超时
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, AppConfig.TIMEOUT);
			// 设置读取超时
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, AppConfig.TIMEOUT); 
			//设置通信协议版本
			httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		} catch (Exception e) {
			httpClient.getConnectionManager().closeExpiredConnections();
//			System.out.println(" HttpServer == 网络连接失败");
			e.printStackTrace();
		}
	}

	/**
	 * 通过url_address获取地址
	 * @param url_address
	 * @return
	 */
	public String httpGet(String url_address) {
//		System.out.println("get输入："+url_address);
		this.url = url_address;
		 //  第1步：创建HttpGet对象
		HttpGet httpGet = new HttpGet(url);
		try {
		//  第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象  
			httpResponse = httpClient.execute(httpGet);
//			responseHeaders = httpResponse.getAllHeaders();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String str = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
//				System.out.println("---Get 输出："+str);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		httpGet.abort();
		httpClient.getConnectionManager().closeExpiredConnections();
		return null;
	}

	/**
	 * 处理带json的post请求
	 * 
	 * @param header
	 * @param content
	 * @return
	 */
	public String httpPost(String url_Address, List<NameValuePair> nvps) 
	{
//		System.out.println("post 输入："+url_Address+"\n"+params);
		this.url = url_Address;
		try {
			httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			if(nvps != null && nvps.get(3) != null)
				httpPost.setHeader("Cookie", "PHPSESSID="+nvps.get(3).getValue()+";");
			httpResponse = httpClient.execute(httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) 
			{
				String str = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
//				System.out.println("post 输出："+str);
				return str;
			}
		} catch (Exception e) {
			System.out.println("post请求 服务器无应答");
			e.printStackTrace();
		}
		httpPost.abort();
		httpClient.getConnectionManager().closeExpiredConnections();
		return null;
	}
}