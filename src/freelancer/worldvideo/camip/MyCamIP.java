/**
 * MyCamIP.java
 */
package freelancer.worldvideo.camip;


/**
 *@function: 摄像机IP配置
 *@author jiangbing
 *@data: 2014-3-11
 */
public class MyCamIP 
{
	private static UdpSearchHelper udphelper = null;
	private static UdpIPSetHelper  udphelper1 = null;
	/**
	 * 获取局域网所有摄像机信息
	 *jiangbing
	 *2014-3-11
	 * @return
	 */
	public static LanDeviceInfo getCam(String ip)
	{
		udphelper = new UdpSearchHelper();
		udphelper.setIp(ip);
		Thread tReceived = new Thread(udphelper);
		tReceived.start();
		udphelper.send();
		return udphelper.lan;
	}
	/**
	 * 关闭UDP
	 *jiangbing
	 *2014-3-11
	 */
	public static void getSearchClosed()
	{
		if(udphelper != null)
			udphelper.close();
	}
	
	/**
	 * 关闭UDP
	 *jiangbing
	 *2014-3-11
	 */
	public static void getIPSetClosed()
	{
		if(udphelper1 != null)
			udphelper1.close();
	}
	/**
	 * 更新设备IP配置信息
	 *jiangbing
	 *2014-3-11
	 * @param oldDev
	 * @param newDev
	 */
	public static void updateIp(LanDeviceInfo oldDev, LanDeviceInfo newDev)
	{
		StringBuffer msg = new StringBuffer();
		
		msg.append(HeaderDefine.HEADER);
		
		msg.append(HeaderDefine.PREFIX1);
		msg.append(HeaderDefine.ADMIN);
		msg.append(HeaderDefine.SPLIT);
		msg.append((oldDev.device[HeaderDefine.MAC]).trim());
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX2);
		msg.append(oldDev.device[HeaderDefine.NAME]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.NAME]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX3);
		msg.append(oldDev.device[HeaderDefine.IP]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.IP]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX4);
		msg.append(oldDev.device[HeaderDefine.HTTP_PORT]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.HTTP_PORT]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX5);
		msg.append(oldDev.device[HeaderDefine.RTSP_PORT]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.RTSP_PORT]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX6);
		msg.append(oldDev.device[HeaderDefine.GATE]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.GATE]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX7);
		msg.append(oldDev.device[HeaderDefine.SNCODE]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.SNCODE]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX8);
		msg.append(oldDev.device[HeaderDefine.DNS1]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.DNS1]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX9);
		msg.append(oldDev.device[HeaderDefine.DNS2]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.DNS2]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX10);
		msg.append(oldDev.device[HeaderDefine.MAC]);
		msg.append(HeaderDefine.SPLIT);
		msg.append(newDev.device[HeaderDefine.MAC]);
		msg.append(HeaderDefine.END);
		
		msg.append(HeaderDefine.PREFIX11);
		msg.append(0x30);
		msg.append(HeaderDefine.SPLIT);
		msg.append(0x30);
		msg.append(HeaderDefine.END);
		udphelper1 = new UdpIPSetHelper();
		Thread tReceived = new Thread(udphelper1);
		tReceived.start();
		udphelper1.send(charsToBytes(new String(msg).toCharArray()));
	}
	
	/**
	 * char数组转byte数组
	 * @param chars
	 * @return
	 */
	public static byte[] charsToBytes(char []chars)
	{
		byte[] bytes = new byte[chars.length];
		for(int i = 0; i< bytes.length;++i)
		{
			bytes[i] = (byte)chars[i];
		}
		return bytes;
	}
	
}
