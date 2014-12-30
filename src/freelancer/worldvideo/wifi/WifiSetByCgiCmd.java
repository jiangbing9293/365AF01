package freelancer.worldvideo.wifi;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import freelancer.worldvideo.util.Base64Util;
import freelancer.worldvideo.util.MyTool;

/**
 * 通过cgi命令修改设备wifi
 */
public class WifiSetByCgiCmd {
	private static WifiSetByCgiCmd instance = null;

	public static WifiSetByCgiCmd getInstance() {
		if (instance == null) {
			instance = new WifiSetByCgiCmd();
		}
		return instance;
	}

	/**
	 ** 
	 ** Wi-Fi设置
	 */
	public boolean sendSetRequest(final String ssid, final String password,
			final String acc, final String pwd) {
		try {
			
			final Socket socket = getSocket();
			
			String cmdFormat = "WLAN_Enabled=on&WLAN_SSID=%1$s&WLAN_Channel=1&WLAN_NetworkType=0&WLAN_AuthMode=3&WLAN_EncrypType=2&WLAN_DefaultKeyID=1&WLAN_Key1=&WLAN_Key2=&WLAN_Key3=&WLAN_Key4=&WLAN_Key1Type=0&WLAN_Key2Type=0&WLAN_Key3Type=0&WLAN_Key4Type=0&WLAN_WPAPSKKey=%2$s";

			String reqFormat = "POST /cgi-bin/setparam.cgi HTTP/1.1\r\n"
					+ "Host: 192.168.50.1\r\n"
					+ "User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:32.0) Gecko/20100101 Firefox/32.0\r\n"
					+ "Accept: application/json, text/javascript, */*; q=0.01\r\n"
					+ "Accept-Language: zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3\r\n"
					+ "Accept-Encoding: gzip, deflate\r\n"
					+ "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n"
					+ "X-Requested-With: XMLHttpRequest\r\n"
					+ "Referer: http://192.168.50.1\r\n"
					+ "Content-Length: %1$d\r\n" + "Connection: keep-alive\r\n"
					+ "Pragma: no-cache\r\n" + "Cache-Control: no-cache\r\n"
					+ "Authorization: Basic %2$s\r\n\n" + "%3$s";

			String cmd =java.net.URLEncoder.encode(String.format(cmdFormat, ssid, password),"UTF-8");
			
			String request = String.format(reqFormat, cmd.length(),
					Base64Util.encode(new String(acc + ":" + pwd).getBytes()),
					cmd);
			byte[] bytes = request.getBytes();
			MyTool.println(" ===== \n" + request);
			socket.getOutputStream().write(bytes);
			socket.getOutputStream().flush();
			byte[] buffer = new byte[1024];
			socket.getInputStream().read(buffer);

			MyTool.println("\n==== receive ========\n"
					+ new String(buffer).trim() + "\n========\n");

			try {
				socket.close();
				Thread.sleep(3*1000);
				sendDHCPRequest(acc, pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/***
	 * 启用DHCP
	 * 
	 * @param acc
	 * @param password
	 * @return
	 */
	public boolean sendDHCPRequest(final String acc, final String password) {
		/*
		 * new Thread() { public void run() {
		 */
		try {
			final Socket socket = getSocket();

			// String cmdFormat =
			// "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SystemReboot></tds:SystemReboot></soap:Body></soap:Envelope>";

			//String cmdFormat = "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tt=\"http://www.onvif.org/ver10/schema\" xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SetNetworkInterfaces><tds:InterfaceToken>eth0</tds:InterfaceToken><tds:NetworkInterface token=\"eth0\"><tt:IPv4><tt:Enabled>true</tt:Enabled><tt:Manual><tt:Address>192.168.1.126</tt:Address><tt:PrefixLength>24</tt:PrefixLength></tt:Manual><tt:DHCP>true</tt:DHCP></tt:IPv4></tds:NetworkInterface></tds:SetNetworkInterfaces></soap:Body></soap:Envelope>";
			
			String cmdFormat = "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tt=\"http://www.onvif.org/ver10/schema\" xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SetNetworkInterfaces><tds:InterfaceToken>eth0</tds:InterfaceToken><tds:NetworkInterface token=\"eth0\"><tt:IPv4><tt:Enabled>true</tt:Enabled><tt:DHCP>true</tt:DHCP></tt:IPv4></tds:NetworkInterface></tds:SetNetworkInterfaces></soap:Body></soap:Envelope>";
			
			String reqFormat = "POST /onvif/services HTTP/1.1\r\n"
					+ "method: POST\r\n"
					+ "Accept-Language: zh-CN\r\n"
					+ "Referer: http://192.168.50.1\r\n"
					+ "Accept: text/plain, */*; q=0.01\r\n"
					+ "Content-Type: application/soap+xml; charset=utf-8\r\n"
					+ "x-requested-with: XMLHttpRequest\r\n"
					+ "Accept-Encoding: gzip, deflate\r\n"
					+ "User-Agent: Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0; EIE10;ENUSWOL)\r\n"
					+ "Host: 192.168.50.1\r\n" 
					+ "Content-Length: %1$d\r\n"
					+ "Connection: Keep-Alive\r\n"
					+ "Cache-Control: no-cache\r\n"
					+ "Authorization: Basic %2$s\r\n\n" 
					+ "%3$s";

			String request = String.format(reqFormat, cmdFormat.length(),
					Base64Util.encode(new String(acc + ":" + password)
							.getBytes()), cmdFormat);

			byte[] bytes = request.getBytes();
			MyTool.println(" ===== \n" + request);
			socket.getOutputStream().write(bytes);
			socket.getOutputStream().flush();
			byte[] buffer = new byte[1024*3];
			socket.getInputStream().read(buffer);
			MyTool.println("\n==== receive ========\n"
					+ new String(buffer).trim() + "\n========\n");
			try {
				socket.close();
				sendRebootRequest(acc, password);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * }; }.start();
		 */
		return true;
	}
	/***
	 * 设备重启
	 * 
	 * @param acc
	 * @param password
	 * @return
	 */
	public boolean sendRebootRequest(final String acc, final String password) {
		/*
		 * new Thread() { public void run() {
		 */
		try {
			final Socket socket = getSocket();
			
			 String cmdFormat =
			 "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SystemReboot></tds:SystemReboot></soap:Body></soap:Envelope>";
			
			//String cmdFormat = "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tt=\"http://www.onvif.org/ver10/schema\" xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SetNetworkInterfaces><tds:InterfaceToken>eth0</tds:InterfaceToken><tds:NetworkInterface token=\"eth0\"><tt:IPv4><tt:Enabled>true</tt:Enabled><tt:Manual><tt:Address>192.168.1.126</tt:Address><tt:PrefixLength>24</tt:PrefixLength></tt:Manual><tt:DHCP>true</tt:DHCP></tt:IPv4></tds:NetworkInterface></tds:SetNetworkInterfaces></soap:Body></soap:Envelope>";
			
			//String cmdFormat = "<?xml version='1.0' encoding='utf-8'?><soap:Envelope xmlns:tt=\"http://www.onvif.org/ver10/schema\" xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Header></soap:Header><soap:Body><tds:SetNetworkInterfaces><tds:InterfaceToken>eth0</tds:InterfaceToken><tds:NetworkInterface token=\"eth0\"><tt:IPv4><tt:Enabled>true</tt:Enabled><tt:DHCP>true</tt:DHCP></tt:IPv4></tds:NetworkInterface></tds:SetNetworkInterfaces></soap:Body></soap:Envelope>";
			
			String reqFormat = "POST /onvif/services HTTP/1.1\r\n"
					+ "method: POST\r\n"
					+ "Accept-Language: zh-CN\r\n"
					+ "Referer: http://192.168.50.1\r\n"
					+ "Accept: text/plain, */*; q=0.01\r\n"
					+ "Content-Type: application/soap+xml; charset=utf-8\r\n"
					+ "x-requested-with: XMLHttpRequest\r\n"
					+ "Accept-Encoding: gzip, deflate\r\n"
					+ "User-Agent: Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0; EIE10;ENUSWOL)\r\n"
					+ "Host: 192.168.50.1\r\n" 
					+ "Content-Length: %1$d\r\n"
					+ "Connection: Keep-Alive\r\n"
					+ "Cache-Control: no-cache\r\n"
					+ "Authorization: Basic %2$s\r\n\n" 
					+ "%3$s";
			
			String request = String.format(reqFormat, cmdFormat.length(),
					Base64Util.encode(new String(acc + ":" + password)
					.getBytes()), cmdFormat);
			
			byte[] bytes = request.getBytes();
			MyTool.println(" ===== \n" + request);
			socket.getOutputStream().write(bytes);
			socket.getOutputStream().flush();
			byte[] buffer = new byte[1024*3];
			socket.getInputStream().read(buffer);
			MyTool.println("\n==== receive ========\n"
					+ new String(buffer).trim() + "\n========\n");
			try {
				socket.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * }; }.start();
		 */
		return true;
	}

	/***
	 * 获取wifi列表
	 * 
	 * @return
	 */
	public List<String> getWiFiList() {

		List<String> wifiList = new ArrayList<String>();
		try {
			final Socket socket = getSocket();

			String reqFormat = "GET /cgi/wlan?action=list HTTP/1.1\r\n"
					+ "Host: 192.168.50.1\r\n"
					+ "x-requested-with: XMLHttpRequest\r\n"
					+ "Accept-Language: zh-cn\r\n"
					+ "Accept: application/json, text/javascript, */*; q=0.01\r\n"
					+ "Accept-Encoding: gzip, deflate\r\n"
					+ "User-Agent: Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)\r\n"
					+ "Connection: Keep-Alive\r\n\n";

			byte[] bytes = reqFormat.getBytes();
			MyTool.println(" ===== \n" + reqFormat);
			socket.getOutputStream().write(bytes);
			socket.getOutputStream().flush();
			byte[] buffer = new byte[1024 * 8];
			socket.getInputStream().read(buffer);
			int index = 0;
			for (int i = 0; i < buffer.length - 1; i++) {
				MyTool.println("====== " + i);
				if (buffer[i] == '[') {
					index = i;
					break;
				}
			}

			byte[] tmp = new byte[1024 * 8];
			System.arraycopy(buffer, index, tmp, 0, buffer.length - index);

			String str = new String(tmp).trim();
			MyTool.println("\n==== receive ========\n" + str
					+ "\n========\n");
			if (str == null || str.equals("")) {
				return null;
			}
			JSONArray array = new JSONArray(str);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				if (json.getString("ssid") != null) {
					wifiList.add(json.getString("ssid"));
				}
			}
			try {
				socket.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wifiList;
	}

	private Socket getSocket() throws Exception {
		Socket socket = new Socket();
		SocketAddress remoteAddr = new InetSocketAddress("192.168.50.1", 80);
		socket.connect(remoteAddr, 10 * 1000);
		socket.setSoTimeout(10 * 1000);
		return socket;
	}
}
