package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.alarmset.task.AlarmTaskListActivity;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

public class AlarmSetActivity extends BaseActivity implements IRegisterIOTCListener
{
	public static MyCamera mCamera = null;
	public static DeviceInfo mDevice = null;
	public static boolean pir = false;
	
	
	private TitleView mTitle = null;
	private LinearLayout layout_motion = null;
	private LinearLayout layout_gpio = null;
	private LinearLayout layout_plan = null;
	private LinearLayout layout_email = null;
	private LinearLayout layout_ftp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set);
		init();
		initView();
	}
	
	private void init()
	{
		Bundle bundle = this.getIntent().getExtras();
		int position = bundle.getInt("position",-1);
		/* register recvIOCtrl listener */
		if (position > -1 && position < UIApplication.CameraList.size())
		{
			mCamera = UIApplication.CameraList.get(position);
			mDevice = UIApplication.DeviceList.get(position);
			mCamera.registerIOTCListener(this);
		}
		if (mCamera != null)
		{
			if(!mCamera.isSessionConnected())
			{
				Toast.makeText(AlarmSetActivity.this, getText(R.string.alarmset_activity_connect_fail), Toast.LENGTH_SHORT).show();
				return;
			}
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_PIR_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetPIRRep.parseContent(mDevice.ChannelIndex));
		}
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.alarm_set_title);
		layout_motion = (LinearLayout) findViewById(R.id.layout_motion);
		layout_gpio = (LinearLayout) findViewById(R.id.layout_gpio);
		layout_plan = (LinearLayout) findViewById(R.id.layout_plan);
		layout_email = (LinearLayout) findViewById(R.id.layout_email);
		layout_ftp = (LinearLayout) findViewById(R.id.layout_ftp);
		mTitle.setTitle(R.string.alarmset_activity_title);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				if(mCamera != null)
					mCamera.unregisterIOTCListener(AlarmSetActivity.this);
				mCamera = null;
				mDevice = null;
				finish();
			}
		});
		layout_motion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(AlarmSetActivity.this, MotionDetionActivity.class);
				startActivity(view);
			}
		});
		layout_gpio.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(AlarmSetActivity.this, GPIOAlarmActivity.class);
				startActivity(view);
			}
		});
		layout_plan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				//view.setClass(AlarmSetActivity.this, AlarmPlanActivity.class);
				view.setClass(AlarmSetActivity.this, AlarmTaskListActivity.class);
				startActivity(view);
			}
		});
		layout_email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(AlarmSetActivity.this, EmailAlarmActivity.class);
				startActivity(view);
			}
		});
		layout_ftp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(AlarmSetActivity.this, FTPAlarmActivity.class);
				startActivity(view);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCamera != null)
		{
			mCamera.unregisterIOTCListener(this);
		}
		pir = false;
		handler.removeCallbacksAndMessages(null);
	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
		if (mCamera == camera) 
		{
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);
			bundle.putByteArray("data", data);

			Message msg = new Message();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");
			switch (msg.what) {
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_PIR_RESP:
				if(data[10] == 1)
				{
					pir = true;
				}
				break;
			}
		}
	};
}
