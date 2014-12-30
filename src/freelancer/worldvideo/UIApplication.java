/**
 * UIApplication.java
 */
package freelancer.worldvideo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.nrs.utils.tools.CrashHandler;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;
import com.tutk.IOTC.st_LanSearchInfo;

import freelancer.worldvideo.camip.LanDeviceInfo;
import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.util.MyTool;

/**
 * @function: App主程序
 * @author jiangbing
 * @data: 2014-2-26
 */
public class UIApplication extends Application implements IRegisterIOTCListener {
	public static List<MyCamera> CameraList = new ArrayList<MyCamera>();
	public static List<DeviceInfo> DeviceList = new ArrayList<DeviceInfo>();
	public static st_LanSearchInfo[] arrResp = null;
	public static List<LanDeviceInfo> dev_list = new ArrayList<LanDeviceInfo>();
	public static String mobile = null;
	public static String upswd = null;
	// public static List<MyCamera> dCameraList = new ArrayList<MyCamera>();
	// public static List<DeviceInfo> dDeviceList = Collections
	// .synchronizedList(new ArrayList<DeviceInfo>());

	public static UIApplication app = null;
	// public List<Drawable> bList = null;
	private static Stack<Activity> activityStack = null;

	// public static List<Bitmap> lbitmap = new ArrayList<Bitmap>();

	public static List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

	public static boolean LOGIN = false;
	public static boolean LIVE = false;
	public static MyCamera mCamera = null;
	// public static boolean APP_SLEEP = false;

	@Override
	public void onCreate() {
		if (app == null)
			app = this;
		super.onCreate();
		
		CrashHandler crashHandler = CrashHandler.getInstance() ;
		crashHandler.init(this) ;
		
		// if (bList == null) {
		// bList = new ArrayList<Drawable>();
		// }
		MyCamera.init();
		loadDevice();
		loadLanDevice();
		reconnect();
		// getDataThread();
		LOGIN = getSharedPreferences("365af", MODE_WORLD_READABLE).getBoolean(
				"LOGIN", false);
	}

	// Returns the application instance
	public static UIApplication getInstance() {
		return app;
	}

	/**
	 * add Activity 添加Activity到栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * get current Activity 获取当前Activity（栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			if (!activity.isFinishing())
				activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			DatabaseManager.n_mainActivity_Status = 0;
			finishAllActivity();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDevice() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (CameraList != null) {
					for (MyCamera camera : CameraList) {
						if(camera.isSessionConnected())
							camera.disconnect();
					}
				}
				CameraList.clear();
				DeviceList.clear();
				DatabaseManager manager = new DatabaseManager(
						UIApplication.this);
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE,
						new String[] { "_id", "dev_nickname", "dev_uid",
								"dev_name", "dev_pwd", "view_acc", "view_pwd",
								"event_notification", "camera_channel",
								"snapshot", "ask_format_sdcard" }, null, null,
						null, null, "_id");

				while (cursor.moveToNext()) {

					long db_id = cursor.getLong(0);
					String dev_nickname = cursor.getString(1);
					String dev_uid = cursor.getString(2);
					String view_acc = cursor.getString(5);
					String view_pwd = cursor.getString(6);
					int event_notification = cursor.getInt(7);
					int channel = cursor.getInt(8);
					byte[] bytsSnapshot = cursor.getBlob(9);
					int ask_format_sdcard = cursor.getInt(10);
					
					Bitmap snapshot = (bytsSnapshot != null && bytsSnapshot.length > 0) ? DatabaseManager
							.getBitmapFromByteArray(bytsSnapshot) : null;

					MyCamera camera = new MyCamera(dev_nickname, dev_uid,
							view_acc, view_pwd, 1);
					DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(),
							dev_nickname, dev_uid, view_acc, view_pwd, getResources().getString(R.string.tips_wifi_connecting),
							event_notification, channel, snapshot);
					dev.ShowTipsForFormatSDCard = ask_format_sdcard == 1;
					DeviceList.add(dev);
					
					if (camera != null) {
						camera.registerIOTCListener(UIApplication.this);
						camera.connect(dev_uid);
						camera.start(Camera.DEFAULT_AV_CHANNEL, view_acc,
								view_pwd);
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
								AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
								AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
						camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
								MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_REQ,
								MYAVIOCTRLDEFs.sMsgNetviomGetDoorReq
										.parseContent(dev.ChannelIndex));
					}
					CameraList.add(camera);
				}

				cursor.close();
				db.close();
				try {

					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameData(com.tutk.IOTC.Camera
	 * , int, android.graphics.Bitmap)
	 */
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameInfo(com.tutk.IOTC.Camera
	 * , int, long, int, int, int, int)
	 */
	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveSessionInfo(com.tutk.IOTC.
	 * Camera, int)
	 */
	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveChannelInfo(com.tutk.IOTC.
	 * Camera, int, int)
	 */
	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveIOCtrlData(com.tutk.IOTC.Camera
	 * , int, int, byte[])
	 */
	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		if (avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_RESP) {
			for (DeviceInfo dev : DeviceList) {
				if (((MyCamera) camera).getUID().equals(dev.UID)) {
					if (data != null) {
						dev.flag = data[0];
						break;
					}
				}
			}

		}

		if (data.length < 16)
			return;
		int evtType = Packet.byteArrayToInt_Little(data, 16);
		if (evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS
				&& evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS
				&& evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_ALL) {
			byte[] t = new byte[8];
			System.arraycopy(data, 0, t, 0, 8);
			AVIOCTRLDEFs.STimeDay evtTime = new AVIOCTRLDEFs.STimeDay(t);
			MyTool.println("UIApplication->evtType:" + Integer.toHexString(evtType));
			int camChannel = Packet.byteArrayToInt_Little(data, 12);
			if (DeviceList != null)
				for (int i = 0; i < DeviceList.size(); i++) {
					if (((MyCamera) camera).getUID().equals(
							DeviceList.get(i).UID)) {
						showNotification(DeviceList.get(i), camChannel,
								evtType, evtTime.getTimeInMillis());
						mCamera = (MyCamera) camera;
						return;
					}
				}
		}
	}

	private void showNotification(DeviceInfo dev, int camChannel, int evtType,
			long evtTime) {

		try {
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Bundle extras = new Bundle();
			extras.putString("dev_uid", dev.UID);
			extras.putString("dev_uuid", dev.UUID);
			extras.putString("dev_nickname", dev.NickName);
			extras.putString("conn_status", dev.Status);
			extras.putString("view_acc", dev.View_Account);
			extras.putString("view_pwd", dev.View_Password);
			extras.putInt("camera_channel", dev.ChannelIndex);

			final Intent intent = new Intent(this, VideoLiveActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("cam_info_extra", extras);

			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			cal.setTimeInMillis(evtTime);
			cal.add(Calendar.MONTH, -1);

			Notification notification = new Notification(R.drawable.app_icon,
					String.format(
							getText(R.string.ntfIncomingEvent).toString(),
							dev.NickName), cal.getTimeInMillis());

			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			if (dev.EventNotification == 0) {
				notification.defaults = Notification.DEFAULT_LIGHTS;
			} else if (dev.EventNotification == 1) {
				notification.defaults = Notification.DEFAULT_SOUND;
			} else if (dev.EventNotification == 2) {
				notification.defaults = Notification.DEFAULT_VIBRATE;
			} else {
				notification.defaults = Notification.DEFAULT_ALL;
			}

			if (evtType <= 0x11) {

				String str = "";
				if (dev.flag == 1
						&& evtType == AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARM) {
					str = getString(R.string.evttype_rs232);
				} else {
					str = getEventType(this, evtType, false);
				}

				notification.setLatestEventInfo(this, String.format(
						getText(R.string.ntfIncomingEvent).toString(),
						dev.NickName), String.format(
						getText(R.string.ntfLastEventIs).toString(), str),
						pendingIntent);

				manager.notify(0, notification);

				if (dev.flag == 1
						&& evtType == AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARM && !LIVE) {
					Intent view = new Intent(this,
							AlarmNotificationActivity.class);
					view.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					view.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					view.putExtra("cam_info_extra", extras);
					startActivity(view);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取事件类型 jiangbing 2014-3-5
	 * 
	 * @param context
	 * @param eventType
	 * @param isSearch
	 * @return
	 */
	public final static String getEventType(Context context, int eventType,
			boolean isSearch)
	{
		String result = "";

		switch (eventType) {
		case AVIOCTRLDEFs.AVIOCTRL_EVENT_ALL:
			result = isSearch ? context.getText(R.string.evttype_all)
					.toString() : context.getText(
					R.string.evttype_fulltime_recording).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONDECT:
			result = context.getText(R.string.evttype_motion_detection)
					.toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEOLOST:
			result = context.getText(R.string.evttype_video_lost).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARM:
			result = context.getText(R.string.evttype_io_alarm).toString();
			break;

		case MYAVIOCTRLDEFs.AVIOCTRL_EVENT_PIR:
			result = context.getText(R.string.evttype_io_pir).toString();
			break;
		case MYAVIOCTRLDEFs.AVIOCTRL_EVENT_OD:
			result = context.getText(R.string.evttype_od).toString();
			break;
		case MYAVIOCTRLDEFs.AVIOCTRL_EVENT_RS232:
			result = context.getText(R.string.evttype_rs232_open).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS:
			result = context.getText(R.string.evttype_motion_pass).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_VIDEORESUME:
			result = context.getText(R.string.evttype_video_resume).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS:
			result = context.getText(R.string.evttype_io_alarm_pass).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_EXPT_REBOOT:
			result = context.getText(R.string.evttype_expt_reboot).toString();
			break;

		case AVIOCTRLDEFs.AVIOCTRL_EVENT_SDFAULT:
			result = context.getText(R.string.evttype_sd_fault).toString();
			break;
		}

		return result;
	}

	public final static String getDevNameByUid(String uid) {
		for (int i = 0; i < DeviceList.size(); i++) {
			if (uid.equals(DeviceList.get(i).UID)) {
				return DeviceList.get(i).NickName;
			}
		}
		return "";
	}

	public final static String getDevIPtUid(String uid) {
		if (arrResp == null)
			return "";
		for (st_LanSearchInfo resp : arrResp) {
			if (uid.equals(new String(resp.UID).trim())) {
				return new String(resp.IP).trim();
			}
		}
		return "";
	}

	public static void updateIP(String oldIp, String newIp) {
		for (int i = 0; i < arrResp.length; ++i) {
			if (oldIp.equals(new String(arrResp[i].IP).trim())) {
				arrResp[i].IP = newIp.getBytes();
				return;
			}
		}
	}

	private void loadLanDevice() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				arrResp = Camera.SearchLAN();
				
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void reconnect() {
		new Thread(new Runnable() {
			@Override
			public void run() 
			{
				while (!Thread.currentThread().isInterrupted() ) 
				{
					try {
						if (UIApplication.CameraList != null) {
								for (MyCamera camera : UIApplication.CameraList) {
										if (camera != null && !camera.isBusy) 
										{
											if (camera.isChannelConnected(0)  && camera.mRet >= 0)
											{
												camera.sendIOCtrl(
														Camera.DEFAULT_AV_CHANNEL,
														MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_REQ,
														MYAVIOCTRLDEFs.sMsgNetviomGetDoorReq
														.parseContent(Camera.DEFAULT_AV_CHANNEL));
											}
											else
											{
												
												 if(!camera.isChannelConnected(0))
												{
													camera.disconnect();
													MyTool.println(" UIApplication disconnect ");
													camera.connect(camera.getUID());
													camera.start(Camera.DEFAULT_AV_CHANNEL,
															"admin", camera.getPassword());
													camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
															AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
															AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
													camera.sendIOCtrl(
															Camera.DEFAULT_AV_CHANNEL,
															MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_REQ,
															MYAVIOCTRLDEFs.sMsgNetviomGetDoorReq
																	.parseContent(Camera.DEFAULT_AV_CHANNEL));
												}
											}
										}
									
								}
						}
						Thread.sleep(10 * 1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
