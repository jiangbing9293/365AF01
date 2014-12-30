/**
 * CamRecordActivity.java
 */
package freelancer.worldvideo.videorecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import neutral.safe.chinese.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListEventReq;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;


/**
 *@function: 事件录像信息
 *@author jiangbing
 *@data: 2014-3-5
 */
public class EventRecordActivity extends BaseActivity implements IRegisterIOTCListener
{
	private TitleView mTitle = null;
	
	public static ArrayList<EventInfo> list = new ArrayList<EventInfo>();
	
	private ListView eventlist = null;
	private MyAdapter adapter;
	
	private MyCamera mCamera;
	
	private String mDevUUID;
	private String mDevUID;
	private String mDevNickName = "";
	private String mViewAcc;
	private String mViewPwd;
	private int mCameraChannel;
	
	private int position = -1;

	private Boolean mIsSearchingEvent = false;
	
	public static int from = 60 * 24 *7;
	public static boolean search = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camrecord);
		mTitle = (TitleView) findViewById(R.id.camrecord_title);
		mTitle.setTitle(getString(R.string.eventrecord_activity_title));
		mTitle.setLeftButton("", new OnLeftButtonClickListener(){
			@Override
			public void onClick(View button) {
				quit();
			}
			
		});
		mTitle.setRightButtonBg(R.drawable.icon_search, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				search = true;
				Intent search = new Intent();
				search.setClass(EventRecordActivity.this, RecordTimeActivity.class);
				startActivity(search);
			}
		});
		
		position = getIntent().getIntExtra("position", -1);
		if(position != -1 && UIApplication.DeviceList.size() > 0)
		{
			mDevUUID = UIApplication.DeviceList.get(position).UUID;
			mDevUID =  UIApplication.DeviceList.get(position).UID;
			mDevNickName =  UIApplication.DeviceList.get(position).NickName;
			mCameraChannel =  UIApplication.DeviceList.get(position).ChannelIndex;
			mViewAcc =  UIApplication.DeviceList.get(position).View_Account;
			mViewPwd =  UIApplication.DeviceList.get(position).View_Password;
		}
		
		for (MyCamera camera : UIApplication.CameraList) {

			if (mDevUUID.equalsIgnoreCase(camera.getUUID())) {
				mCamera = camera;
				mCamera.registerIOTCListener(this);
				mCamera.resetEventCount();
				break;
			}
		}
		mTitle.setTitle(mDevNickName);
		eventlist = (ListView)findViewById(R.id.camrecord_list);
		adapter = new MyAdapter(this);
		eventlist.setAdapter(adapter);
		eventlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (list.size() == 0 || list.size() < position || !mCamera.getPlaybackSupported(0))
				{
					Toast.makeText(EventRecordActivity.this, "Record Play Fail", Toast.LENGTH_SHORT).show();
					return;
				}

				EventInfo evt = list.get(position);

				if (evt.EventStatus == EventInfo.EVENT_NORECORD)
					return;

				Bundle extras = new Bundle();
				extras.putString("flag", "Event");
				extras.putString("dev_uuid", mDevUUID);
				extras.putString("dev_nickname", mDevNickName);
				extras.putInt("pos", position);
				extras.putInt("camera_channel", mCameraChannel);
				extras.putInt("event_type", evt.EventType);
				extras.putLong("event_time", evt.Time);
				extras.putString("event_uuid", evt.getUUID());
				extras.putString("view_acc", mViewAcc);
				extras.putString("view_pwd", mViewPwd);
				extras.putByteArray("event_time2", evt.EventTime.toByteArray());
				Intent playback = new Intent();
				playback.putExtras(extras);
				playback.setClass(EventRecordActivity.this, PlaybackActivity.class);
				startActivity(playback);
			}
		});
	}
	
	protected void onResume()
	{
		super.onResume();
		if (mCamera == null || !mCamera.isChannelConnected(0)) {
			Toast.makeText(this, "Camera Disconnect", Toast.LENGTH_SHORT).show();
		} else {
			initEventList();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			quit();
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	private void quit() {

		if (mCamera != null) {
			mCamera.unregisterIOTCListener(this);
			mCamera = null;
		}
		handler.removeCallbacksAndMessages(null);
		EventRecordActivity.this.finish();
	}
	private void initEventList() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, -1*from);
		searchEventList(calendar.getTimeInMillis(), System.currentTimeMillis(), AVIOCTRLDEFs.AVIOCTRL_EVENT_ALL);
	}

	private static String getLocalTime(long utcTime, boolean subMonth) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
		calendar.setTimeInMillis(utcTime);

		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setTimeZone(TimeZone.getDefault());

		if (subMonth)
			calendar.add(Calendar.MONTH, -1);

		return dateFormat.format(calendar.getTime());
	}

	public void searchEventList(long startTime, long stopTime, int eventType) {

		if (mCamera != null) {
			list.clear();
			adapter.notifyDataSetChanged();
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ, SMsgAVIoctrlListEventReq.parseConent(mCameraChannel, startTime, stopTime, (byte) eventType, (byte) 0));
			mIsSearchingEvent = true;

			/* timeout for no search result been found */
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {

					if (mIsSearchingEvent) {
						mIsSearchingEvent = false;
						adapter.notifyDataSetChanged();
					}
				}

			}, 180000);
			
		}
	}
	
	public final class ViewHolder {
		public ImageView img;
		public TextView record_type;
		public TextView record_time;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			final EventInfo evt = (EventInfo) getItem(position);
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.adapter_record, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.record_more);
				holder.record_type = (TextView) convertView
						.findViewById(R.id.record_type);
				holder.record_time = (TextView) convertView
						.findViewById(R.id.record_time);
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
			}
			holder.record_type.setText(UIApplication.getEventType(EventRecordActivity.this, evt.EventType, false));
			
			holder.record_time.setText(evt.EventTime.getLocalTime());
			if(mCamera != null)
				holder.img.setVisibility(mCamera.getPlaybackSupported(0) & evt.EventStatus != EventInfo.EVENT_NORECORD ? View.VISIBLE : View.GONE);

//			if (evt.EventStatus == EventInfo.EVENT_UNREADED) {
//				holder.record_type.setTypeface(null, Typeface.BOLD);
//				holder.record_type.setTextColor(0xFF000000);
//			} else {
//				holder.record_type.setTypeface(null, Typeface.NORMAL);
//				holder.record_type.setTextColor(0xFF999999);
//			}
			
			return convertView;
		}

	}

	/* (non-Javadoc)
	 * @see com.tutk.IOTC.IRegisterIOTCListener#receiveFrameData(com.tutk.IOTC.Camera, int, android.graphics.Bitmap)
	 */
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.tutk.IOTC.IRegisterIOTCListener#receiveFrameInfo(com.tutk.IOTC.Camera, int, long, int, int, int, int)
	 */
	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.tutk.IOTC.IRegisterIOTCListener#receiveSessionInfo(com.tutk.IOTC.Camera, int)
	 */
	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	/* (non-Javadoc)
	 * @see com.tutk.IOTC.IRegisterIOTCListener#receiveChannelInfo(com.tutk.IOTC.Camera, int, int)
	 */
	@Override
	public void receiveChannelInfo(Camera camera, int sessionChannel, int resultCode) {
		
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);

			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}

	}

	/* (non-Javadoc)
	 * @see com.tutk.IOTC.IRegisterIOTCListener#receiveIOCtrlData(com.tutk.IOTC.Camera, int, int, byte[])
	 */
	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);
			bundle.putByteArray("data", data);

			Message msg = new Message();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");
			int sessionChannel = bundle.getInt("sessionChannel");

			switch (msg.what) {

			case Camera.CONNECTION_STATE_CONNECTING:
				break;
			case Camera.CONNECTION_STATE_WRONG_PASSWORD:
			case Camera.CONNECTION_STATE_CONNECT_FAILED:
			case Camera.CONNECTION_STATE_DISCONNECTED:
			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
			case Camera.CONNECTION_STATE_TIMEOUT:

				list.clear();
				adapter.notifyDataSetInvalidated();
				break;

			case Camera.CONNECTION_STATE_CONNECTED:

				if (sessionChannel == 0) {
					adapter.notifyDataSetInvalidated();
				}

				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_RESP:

				if (data.length >= 12 && mIsSearchingEvent) {

					// int idx = data[8];
					int end = data[9];
					int cnt = data[10];

					if (cnt > 0) {

						int pos = 12;
						int size = AVIOCTRLDEFs.SAvEvent.getTotalSize();

						for (int i = 0; i < cnt; i++) {

							byte[] t = new byte[8];
							System.arraycopy(data, i * size + pos, t, 0, 8);
							AVIOCTRLDEFs.STimeDay time = new AVIOCTRLDEFs.STimeDay(t);

							byte event = data[i * size + pos + 8];
							byte status = data[i * size + pos + 9];
							if(event > 0)
							{
								list.add(new EventInfo( event, time,  status));
							}
						}
						adapter.notifyDataSetInvalidated();
					}

					if (end == 1) {

						mIsSearchingEvent = false;

						if (list.size() == 0)
							Toast.makeText(EventRecordActivity.this, EventRecordActivity.this.getText(R.string.tips_search_event_no_result), Toast.LENGTH_SHORT).show();
					}
				}

				break;
			}

			super.handleMessage(msg);
		}
	};
	
}
