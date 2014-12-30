package freelancer.worldvideo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
/**
 * Alarm Type Parse
 * @author jiangbing
 *
 */
public class MyAlarmTool 
{
	public final static String EVENTS[] = { "IO", "MOTION", "OD","RS232" };
	public final static String EVENTS_[][] = { { "Time", "Port", "Value" },
			{ "Time" }, { "Time", "Index" } };
	public static Map<String, String> getALarmMsgByJson(String msg,int flag)
	{
		String msgType = "";
		Map<String, String> alarmMsg = new HashMap<String, String>();
		try {
	    	   JSONObject jsonP = new JSONObject(msg);
	    	   for (int i = 0; i < EVENTS.length; i++) {
				if(jsonP.names().get(0).equals(EVENTS[i]))
				{
					JSONObject json = jsonP.getJSONObject(EVENTS[i]);
					String t = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(json.getLong("Time")*1000));
					alarmMsg.put("Time", t);
					switch (i) {
					case 0:
					{
						//IO
						int port = json.getInt("Port");
						if(port == 0)	
						{
							if(flag == 1)
							{
								msgType = "门铃响了";
							}else
							{
								msgType = "IO 报警 ";
							}
						}
						else if(port == 1)	
						{
							msgType = "感应 报警";
						}
					}
						break;
					case 1:
					{
						//MOTION
						msgType = "移动侦测报警 ";
					}
						break;
					case 2:
					{
						//OD
						msgType = "遮挡报警";
					}
						break;
					case 3:
					{
						//rs232
						msgType = "开门成功";
					}
					break;
					}
					break;
				}
			}
	    	   alarmMsg.put("Msg", msgType);
	       } catch (Exception e) {
	    	   e.printStackTrace();
	       }
		return alarmMsg;
	}
}
