/**
 * AppConfig.java
 */
package freelancer.worldvideo.net;

/**
 *@function: app相关配置
 *@author jiangbing
 *@data: 2014-2-26
 */
public interface AppConfig
{
	public static String SERVER_ADDRESS = "http://api.365af.cn/index.php";
	public static String PUSH_SERVER_ADDRESS = "http://www.boxkam.com:8080/365PushServer/apns/alarm_out";
	public final static int TIMEOUT = 15 * 1000;
}
