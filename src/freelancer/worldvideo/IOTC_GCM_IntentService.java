package freelancer.worldvideo;
import neutral.safe.chinese.R;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MyAlarmTool;
import freelancer.worldvideo.util.MyTool;

public class IOTC_GCM_IntentService extends IntentService 
{

	    public IOTC_GCM_IntentService() 
	    {
	    	super("MuazzamService");
	    }
	    
	    public static Context mcontext;
		private static PowerManager.WakeLock sWakeLock;
	    private static final Object LOCK = IOTC_GCM_IntentService.class;
	    
	    static void runIntentInService(Context context, Intent intent) 
	    {
	    	synchronized(LOCK) 
	        {
	            if (sWakeLock == null) 
	            {
	                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
	            }
	        }
	        sWakeLock.acquire();
	        intent.setClassName(context, IOTC_GCM_IntentService.class.getName());
	    	mcontext=context;
	        context.startService(intent);
	    }
	    
	    @Override
	    public final void onHandleIntent(Intent intent) 
	    {
	        try 
	        {
	            String action = intent.getAction();
	            if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) 
	            {
	                handleRegistration(intent);
	            }
	            else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) 
	            {
	            	handleMessage(intent);
	            }
	        } finally 
	        {
	            synchronized(LOCK) 
	            {
	                sWakeLock.release();
	            }
	        }
	    }
	    private void handleRegistration(Intent intent) 
	    {
	        String registrationId = intent.getStringExtra("registration_id");
	        String error = intent.getStringExtra("error");
	        String unregistered = intent.getStringExtra("unregistered");       
	        if (registrationId != null) 
	        {
	        	//TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		        //HttpGetTool HttpGetTool=new HttpGetTool(this,IOTC_GCM_IntentService.class.getName());
		        //HttpGetTool.execute(freelancer.worldvideo.database.DatabaseManager.s_GCM_PHP_URL+"?cmd=reg_client&token="+registrationId+"&appid="+freelancer.worldvideo.database.DatabaseManager.s_Package_name+"&dev=0"+"&imei="+tm.getDeviceId());
		        //freelancer.worldvideo.database.DatabaseManager.s_GCM_token=registrationId;
	        	
	        	freelancer.worldvideo.database.DatabaseManager.GCM_token=registrationId;
		        MyTool.println("=== IOTC_GCM_IntentService handleRegistration regid "+registrationId);
	        }
	            
	        if (unregistered != null) 
	        {
	            // get old registration ID from shared preferences
	            // notify 3rd-party server about the unregistered ID
	        } 
	            
	        if (error != null) 
	        {
	    		
	        	if ("SERVICE_NOT_AVAILABLE".equals(error)) 
	            { 
			        Log.i("GCM", "SERVICE_NOT_AVAILABLE");
	            } 
	        	else 
	            {
			        Log.i("GCM","Received error: " + error);
	            }
	        }
	    }

	    private void handleMessage(Intent intent) 
	    {
	    	
			String dev_uid = intent.getStringExtra("uid");
			String dev_alert = intent.getStringExtra("alert");
			String msg = intent.getStringExtra("msg");
			if(msg != null)
			{
				for (DeviceInfo dev : UIApplication.DeviceList) {
					if(dev_uid.equals(dev.UID))
					{
						int flag = 0;
						if(dev.flag == 1)
							flag = 1;
						dev_alert =dev.NickName +" "+ MyAlarmTool.getALarmMsgByJson(msg,flag).get("Msg")+ " "+MyAlarmTool.getALarmMsgByJson(msg,0).get("Time");
						break;
					}
				}
				if(dev_alert == null || dev_alert.equals(""))
				{
					dev_alert = MyAlarmTool.getALarmMsgByJson(msg,0).get("Msg") + " "+MyAlarmTool.getALarmMsgByJson(msg,0).get("Time");
				}
				showNotification(dev_uid,dev_alert);
			}
			/*
			if(freelancer.worldvideo.database.DatabaseManager.n_mainActivity_Status==0)
			{
				showNotification(dev_uid,dev_alert);
			}
			else
			{
				Intent mintent=new Intent(VideoPlaybackActivity.class.getName());
				mintent.putExtra("dev_uid", dev_uid);
				mcontext.sendBroadcast(mintent);
			}
			*/
	    }
	    
		@SuppressWarnings("deprecation")
		private void showNotification(String dev_uid,String dev_alert) 
		{
			try {

				NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				Bundle extras = new Bundle();
				extras.putString("dev_uid", dev_uid);
				Intent intent = null;
				if(UIApplication.LOGIN)
				{
					intent = new Intent(this, MainActivity.class);
				}
				else
				{
					intent = new Intent(this, LoginActivity.class);
				}
				
				intent.putExtras(extras);
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

				Notification notification = new Notification(R.drawable.app_icon,dev_alert,0);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.flags |= Notification.FLAG_NO_CLEAR;
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
				//notification.flags |= Notification.FLAG_INSISTENT;
				
				notification.defaults = Notification.DEFAULT_ALL;
				notification.setLatestEventInfo(this, getText(R.string.app_name),dev_alert, pendingIntent);

				manager.notify(0, notification);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
}