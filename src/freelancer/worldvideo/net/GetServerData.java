/**
 * GetServerData.java
 */
package freelancer.worldvideo.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import freelancer.worldvideo.util.MyTool;

/**
 * @function: 从服务器获取数据
 * @author jiangbing
 * @data: 2014-2-26
 */
public class GetServerData implements AppConfig {
	private HttpServer server = null;

	public GetServerData() {
		server = new HttpServer();
	}
	
	public List<Map<String, Object>> getRemoteIpc(String mobile,String upswd)
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String temp;
		JSONArray array = null;
		JSONObject object = null;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=getCameras&mobile="+mobile+"&upswd="+upswd);
		if (temp == null) {
			return null;
		}
		try {
			MyTool.println("getCameras:"+temp);
			jsonObjSplit = new JSONObject(temp);
			array = jsonObjSplit.getJSONArray("rows");
			int i = array.length() - 1;
			while (i >= 0) {
				object = array.getJSONObject(i);
				if (object != null) {
					Map<String, Object> info = new HashMap<String, Object>();
					info.put("icon", object.getString("icon"));
					info.put("name", object.getString("name"));
					info.put("uid", object.getString("uid"));
					info.put("pswd", object.getString("pswd"));
					object = null;
					list.add(info);
				}
				--i;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	
	/**
	 * 体验 jiangbing 2014-3-1
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getDemo() {
		List<Map<String, Object>> demoList = new ArrayList<Map<String, Object>>();
		String temp;
		JSONArray array = null;
		JSONObject object = null;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=getDemo");
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			array = jsonObjSplit.getJSONArray("rows");
			int i = array.length() - 1;
			while (i >= 0) {
				object = array.getJSONObject(i);
				if (object != null) {
					Map<String, Object> info = new HashMap<String, Object>();
					info.put("icon", object.getString("icon"));
					info.put("name", object.getString("name"));
					info.put("uid", object.getString("uid"));
					info.put("pswd", object.getString("pswd"));
					object = null;
					demoList.add(info);
				}
				--i;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return demoList;
	}

	/**
	 * 登录 jiangbing 2014-3-1
	 * 
	 * @param mobile
	 * @param upswd
	 * @return
	 */
	public String login(String mobile, String upswd) {
		String temp;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=login&mobile=" + mobile
				+ "&upswd=" + upswd);
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			String str = jsonObjSplit.getString("status");
			String msg = jsonObjSplit.getString("message");
			if (str.equals("1")) {
				return "1";
			} else {
				return msg;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 注册 jiangbing 2014-3-1
	 * 
	 * @param mobile
	 * @param upswd
	 * @return
	 */
	public String reg(String email, String upswd) {
		String temp;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=reg&mobile=" + email
				 + "&upswd=" + upswd);
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			String str = jsonObjSplit.getString("status");
			String msg = jsonObjSplit.getString("message");
			if (str.equals("1")) {
				return "1";
			} else {
				return msg;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取验证码
	 * 
	 * @param email
	 * @return
	 */
	public String getCheckCode(String email) {
		String temp;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=sendCheckCode&mobile="
				+ email);
		System.out.println("getCheckCode====" + temp);
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			String str = jsonObjSplit.getString("status");
			String msg = jsonObjSplit.getString("message");
			if (str.equals("1")) {
				return "1"+msg;
			} else {
				return msg;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 重置密码
	 * 
	 * @param code
	 * @param email
	 * @param newPassword
	 * @return
	 */
	public String resetPassword(String code, String email, String newPassword,
			String sessionid) {
		String temp;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
//		temp = server.httpGet(SERVER_ADDRESS + "?motion=resetPswd&email="
//				+ email+ "&sessionid=" + sessionid + "&code='" + code + "'&pswd=" + newPassword
//				);
		 List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		 nvps.add(new BasicNameValuePair("motion", "resetPswd"));
		 nvps.add(new BasicNameValuePair("mobile", email));
		 nvps.add(new BasicNameValuePair("code", code));
		 nvps.add(new BasicNameValuePair("sessionid",sessionid));
		 nvps.add(new BasicNameValuePair("pswd", newPassword));
		 System.out.println("====== sessionid"+sessionid);
		 temp = server.httpPost(SERVER_ADDRESS, nvps);
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			String str = jsonObjSplit.getString("status");
			String msg = jsonObjSplit.getString("message");
			if (str.equals("1")) {
				return "1"+msg;
			} else {
				return msg;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取ads信息 jiangbing 2014-2-26
	 * 
	 * @return
	 */
	public List<String> getAds() {
		List<String> adsList = new ArrayList<String>();
		String temp;
		JSONArray array = null;
		JSONObject object = null;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(SERVER_ADDRESS + "?motion=getAds");
		if (temp == null) {
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			array = jsonObjSplit.getJSONArray("rows");
			int j = array.length();
			int i = 0;
			while (i < j) {
				object = array.getJSONObject(i);
				if (object != null) {
					adsList.add(object.getString("image"));
					object = null;
				}
				++i;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return adsList;
	}
	/**
	 * 获取通知信息
	 * 
	 * @param uid 设备UID
	 * @param start 起始时间
	 * @param end	截至时间
	 * @return
	 */
	public List<String> getNotifications(String uid,long start,long end) {
		List<String> nList = new ArrayList<String>();
		String temp;
		JSONArray array = null;
		JSONObject object = null;
		JSONObject jsonObjSplit = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet(PUSH_SERVER_ADDRESS + "?uid="+uid+"&start="+start+"&end="+end);
		if (temp == null) 
		{
			return null;
		}
		try {
			jsonObjSplit = new JSONObject(temp);
			array = jsonObjSplit.getJSONArray("msg");
			int j = array.length();
			int i = 0;
			while (i < j) {
				object = array.getJSONObject(i);
				if (object != null) {
					nList.add(object.toString());
					object = null;
				}
				++i;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return nList;
	}
	
	
	/***
	 * 获取wifi列表
	 * @return
	 */
	public List<String> getWiFiList()
	{
		List<String> nList = new ArrayList<String>();
		String temp;
		JSONArray array = null;
		JSONObject object = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet("http://192.168.50.1/cgi/wlan?action=list");
		if (temp == null) {
			return null;
		}
		try {
			array = new JSONArray(temp);
			int j = array.length();
			int i = 0;
			while (i < j) {
				object = array.getJSONObject(i);
				if (object != null) {
					nList.add(object.getString("ssid"));
					object = null;
				}
				++i;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return nList;
	}
	
	/***
	 * 获取UID
	 * @return
	 */
	public String getIpcamUID()
	{
		String uid = null;
		String temp;
		JSONObject array = null;
		JSONObject object = null;
		if (server == null)
			server = new HttpServer();
		temp = server.httpGet("http://192.168.50.1/cgi-bin/getparam.cgi");
		if (temp == null) {
			return null;
		}
		MyTool.println(temp);
		try {
			array = new JSONObject(temp);
			object = array.getJSONObject("TUTKPlatform");
			uid = object.getString("UID");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return uid;
	}
}
