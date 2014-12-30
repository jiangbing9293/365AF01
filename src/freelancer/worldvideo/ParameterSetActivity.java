/**
 * ParameterSet.java
 */
package freelancer.worldvideo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.set.CameraInfoActivity;
import freelancer.worldvideo.set.EnvironmentModeActivity;
import freelancer.worldvideo.set.RecordModeActivity;
import freelancer.worldvideo.set.UpdatePasswordActivity;
import freelancer.worldvideo.set.VideoFlipActivity;
import freelancer.worldvideo.set.VideoQualityActivity;
import freelancer.worldvideo.set.WifiManagerActivity;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 * @function: 参数设置
 * @author jiangbing
 * @data: 2014-2-20
 */
public class ParameterSetActivity extends BaseActivity implements
		IRegisterIOTCListener {
	public static String newPassword;
	public static boolean isModifyPassword = false;
	public static boolean isModifyNicName = false;
	public static boolean isModifyWiFi = false;

	private RelativeLayout record_relative = null;

	private Button btnModifySecurityCode;
	private Button parameterset_scree;
	private Button parameterset_videolevel;
	private Button parameterset_environment;
	private Button parameterset_wifi;
	private Button parameterset_ipset;
	private Button parameterset_record;
	private Button parameterset_info;

	private Button parameterset_ring = null;
	private Button parameterset_alarm_ring = null;
	private Button parameterset_voice_language = null;
	private Button parameterset_apmode = null;
	
	private TextView txt_parameterset_voice_language = null;
	private TextView txt_parameterset_apmode = null;
	private int apmode = 0;
	private int voice_language = 0;
	
	public static MyCamera mCamera = null;
	public static DeviceInfo mDevice = null;

	private TextView txt_parameterset_videolevel = null;
	private TextView txt_parameterset_scree = null;
	private TextView txt_parameterset_environment = null;
	private TextView txt_parameterset_record = null;

	public static String model = null;
	public static String vender = null;
	public static int version = 0;
	public static int free = 0;
	public static int mTotalSize = -1;

	public static int mVideoQuality = -1;
	public static int mVideoFlip = -1;
	public static int mEnvMode = -1;
	public static int mRecordType = -1;

	public static List<AVIOCTRLDEFs.SWifiAp> m_wifiList = new ArrayList<AVIOCTRLDEFs.SWifiAp>();

	public static String videoQualitys[] = new String[5];
	public static String videoFlips[] = new String[4];
	public static String environmentModes[] = new String[4];
	public static String recordModes[] = new String[3];

	private TitleView mTitle;

	private Bundle bundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_parameter_set);
		videoQualitys = getResources().getStringArray(R.array.video_quality);
		videoFlips = getResources().getStringArray(R.array.video_flip);
		environmentModes = getResources().getStringArray(
				R.array.environment_mode);
		recordModes = getResources().getStringArray(R.array.recording_mode);

		bundle = this.getIntent().getBundleExtra("cam_info_extra");
		String devUUID = bundle.getString("dev_uuid");
		String devUID = bundle.getString("dev_uid");
		/* register recvIOCtrl listener */
		for (MyCamera camera : UIApplication.CameraList) {

			if (devUUID.equalsIgnoreCase(camera.getUUID())
					&& devUID.equalsIgnoreCase(camera.getUID())) {
				mCamera = camera;
				mCamera.registerIOTCListener(this);
				break;
			}
		}
		for (DeviceInfo dev : UIApplication.DeviceList) {

			if (devUUID.equalsIgnoreCase(dev.UUID)
					&& devUID.equalsIgnoreCase(dev.UID)) {
				mDevice = dev;
				break;
			}
		}
		if (mDevice != null)
			ParameterSetActivity.newPassword = mDevice.View_Password;
		initCamInfo();
		initVideoSetting();
		initView();
		record_relative = (RelativeLayout) findViewById(R.id.record_relative);
		btnModifySecurityCode = (Button) findViewById(R.id.parameterset_passsword);
		parameterset_videolevel = (Button) findViewById(R.id.parameterset_videolevel);
		parameterset_scree = (Button) findViewById(R.id.parameterset_scree);
		parameterset_environment = (Button) findViewById(R.id.parameterset_environment);
		parameterset_wifi = (Button) findViewById(R.id.parameterset_wifi);
		parameterset_ipset = (Button) findViewById(R.id.parameterset_ipset);
		parameterset_record = (Button) findViewById(R.id.parameterset_record);
		parameterset_info = (Button) findViewById(R.id.parameterset_info);
		parameterset_ring = (Button) findViewById(R.id.parameterset_ring);
		parameterset_alarm_ring = (Button) findViewById(R.id.parameterset_alarm_ring);
		
		
		btnModifySecurityCode
				.setOnClickListener(btnModifySecurityCodeOnClickListener);
		parameterset_videolevel
				.setOnClickListener(parameterset_videolevelOnClickListener);
		parameterset_scree
				.setOnClickListener(parameterset_screeOnClickListener);
		parameterset_environment
				.setOnClickListener(parameterset_environmentOnClickListener);
		//报警铃声
		parameterset_alarm_ring.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 打开系统铃声设置
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				// 类型为来电RINGTONE
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_RINGTONE);
				// 设置显示的title
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.income_ring));
				// 当设置完成之后返回到当前的Activity
				startActivityForResult(intent, 1);
			}
		});
		//来电铃声
		parameterset_ring.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开系统铃声设置
				Intent intent = new Intent(
						RingtoneManager.ACTION_RINGTONE_PICKER);
				// 类型为来电RINGTONE
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
						RingtoneManager.TYPE_RINGTONE);
				// 设置显示的title
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.income_ring));
				// 当设置完成之后返回到当前的Activity
				startActivityForResult(intent, 0);

			}
		});

		parameterset_wifi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent wifiManager = new Intent();
				wifiManager.setClass(ParameterSetActivity.this,
						WifiManagerActivity.class);
				startActivity(wifiManager);
			}
		});
		parameterset_ipset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(ParameterSetActivity.this,
						CamIPUpdateActivity.class);
				startActivity(view);
			}
		});

		parameterset_record
				.setOnClickListener(parameterset_recordOnClickListener);
		parameterset_info.setOnClickListener(parameterset_infoOnClickListener);

		mTitle = (TitleView) findViewById(R.id.paramter_set_title);
		mTitle.setTitle(getText(R.string.parameterset_activity_title)
				.toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				quit(false);
			}

		});
		mTitle.hiddenRightButton();
		/**
		 * 视频质量
		 */
		if (mCamera != null && mCamera.getVideoQualitySettingSupport(0)) {
			txt_parameterset_videolevel.setVisibility(View.VISIBLE);
			parameterset_videolevel.setVisibility(View.VISIBLE);
		} else {
			txt_parameterset_videolevel.setVisibility(View.GONE);
			parameterset_videolevel.setVisibility(View.GONE);
		}
		/**
		 * 画面翻转
		 */
		if (mCamera != null && mCamera.getVideoFlipSupported(0)) {
			parameterset_scree.setVisibility(View.VISIBLE);
			txt_parameterset_scree.setVisibility(View.VISIBLE);
		} else {
			parameterset_scree.setVisibility(View.GONE);
			txt_parameterset_scree.setVisibility(View.GONE);
		}
		/**
		 * 环境模式
		 */
		if (mCamera != null && mCamera.getEnvironmentModeSupported(0)) {
			parameterset_environment.setVisibility(View.VISIBLE);
			txt_parameterset_environment.setVisibility(View.VISIBLE);
		} else {
			parameterset_environment.setVisibility(View.GONE);
			txt_parameterset_environment.setVisibility(View.GONE);
		}
		/**
		 * wifi
		 */
		if (mCamera != null && mCamera.getWiFiSettingSupported(0)) {
			parameterset_wifi.setVisibility(View.VISIBLE);
		} else {
			parameterset_wifi.setVisibility(View.GONE);
		}
		/**
		 * 录像模式
		 */
		if (mCamera != null && mCamera.getRecordSettingSupported(0)) {
			initRecordingMode();
			record_relative.setVisibility(View.VISIBLE);
		} else {
			record_relative.setVisibility(View.GONE);
		}
		if (mDevice == null
				|| UIApplication.getDevIPtUid(mDevice.UID).equals("")) {
			parameterset_ipset.setVisibility(View.GONE);
		}
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onResume() {
		super.onResume();
		if (mVideoQuality != -1 && txt_parameterset_videolevel != null
				&& videoQualitys != null) {
			txt_parameterset_videolevel.setText(videoQualitys[mVideoQuality]);
		}
		if (mVideoFlip != -1 && txt_parameterset_scree != null
				&& videoFlips != null) {
			txt_parameterset_scree.setText(videoFlips[mVideoFlip]);
		}
		if (mEnvMode != -1 && txt_parameterset_environment != null
				&& environmentModes != null) {
			txt_parameterset_environment.setText(environmentModes[mEnvMode]);
		}
		if (mRecordType != -1 && txt_parameterset_record != null
				&& recordModes != null) {
			txt_parameterset_record.setText(recordModes[mRecordType]);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mCamera != null)
			mCamera.unregisterIOTCListener(this);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 0:
			try {
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					getSharedPreferences("APP_RING_URI", MODE_PRIVATE).edit()
							.putString("APP_RING_URI", pickedUri.toString())
							.commit();
				}
				else
				{
					getSharedPreferences("APP_RING_URI", MODE_PRIVATE).edit()
					.putString("APP_RING_URI", null)
					.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case 1:
			try {
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					getSharedPreferences("APP_ALARM_URI", MODE_PRIVATE).edit()
					.putString("APP_ALARM_URI", pickedUri.toString())
					.commit();
				}
				else
				{
					getSharedPreferences("APP_ALARM_URI", MODE_PRIVATE).edit()
					.putString("APP_ALARM_URI", null)
					.commit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
		}
	}

	/**
	 * 安全密码
	 */
	private View.OnClickListener btnModifySecurityCodeOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this,
					UpdatePasswordActivity.class);
			startActivity(view);
		}
	};
	/**
	 * 视频质量
	 */
	private View.OnClickListener parameterset_videolevelOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this, VideoQualityActivity.class);
			startActivity(view);
		}
	};
	/**
	 * 画面翻转
	 */
	private View.OnClickListener parameterset_screeOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this, VideoFlipActivity.class);
			startActivity(view);
		}
	};
	/**
	 * 环境模式
	 */
	private View.OnClickListener parameterset_environmentOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this,
					EnvironmentModeActivity.class);
			startActivity(view);
		}
	};

	private void initRecordingMode() {
		txt_parameterset_record = (TextView) findViewById(R.id.txt_parameterset_record);
		txt_parameterset_record
				.setText(getString(R.string.tips_wifi_retrieving));

		if (mCamera != null) {
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETRECORD_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetMotionDetectReq
							.parseContent(mDevice.ChannelIndex));
		}
	}

	/**
	 * 录像模式
	 */
	private View.OnClickListener parameterset_recordOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this, RecordModeActivity.class);
			startActivity(view);
		}
	};

	private void initCamInfo() {
		if (mCamera != null) {
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
		}
	}

	/**
	 * 摄像机信息
	 */
	private View.OnClickListener parameterset_infoOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent view = new Intent();
			view.setClass(ParameterSetActivity.this, CameraInfoActivity.class);
			startActivity(view);
		}
	};

	private void initVideoSetting() {
		txt_parameterset_videolevel = (TextView) findViewById(R.id.txt_parameterset_videolevel);
		txt_parameterset_scree = (TextView) findViewById(R.id.txt_parameterset_scree);
		txt_parameterset_environment = (TextView) findViewById(R.id.txt_parameterset_environment);
		txt_parameterset_videolevel
				.setText(getString(R.string.tips_wifi_retrieving));
		txt_parameterset_scree
				.setText(getString(R.string.tips_wifi_retrieving));
		txt_parameterset_environment
				.setText(getString(R.string.tips_wifi_retrieving));

		if (mCamera != null) {
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetStreamCtrlReq
							.parseContent(mDevice.ChannelIndex));
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq
							.parseContent(mDevice.ChannelIndex));
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetEnvironmentReq
							.parseContent(mDevice.ChannelIndex));
		}
	}
	
	private void initView()
	{
		parameterset_voice_language = (Button) findViewById(R.id.parameterset_voice_language);
		parameterset_apmode = (Button) findViewById(R.id.parameterset_apmode);
		txt_parameterset_voice_language = (TextView) findViewById(R.id.txt_parameterset_voice_language);
		txt_parameterset_apmode = (TextView) findViewById(R.id.txt_parameterset_apmode);
		
		//语音语言
		parameterset_voice_language.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(voice_language == 1)
				{
					voice_language = 0;
				}
				else
				{
					voice_language = 1;
				}
				initVoiceLanguage();
				if(mCamera != null)
				{
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							MYAVIOCTRLDEFs.IOTYPE_ISMART_SET_VOICE_LNG_REQ,
							MYAVIOCTRLDEFs.sMsgIsmartSetVoiceLngReq
									.parseContent(voice_language));
				}
			}
		});
		//AP模式
		parameterset_apmode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(apmode == 1)
				{
					apmode = 0;
				}
				else
				{
					apmode = 1;
				}
				initApmode();
				if(mCamera != null)
				{
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							MYAVIOCTRLDEFs.IOTYPE_ISMART_SET_APMODE_REQ,
							MYAVIOCTRLDEFs.sMsgIsmartSetAPModeReq
									.parseContent(apmode));
				}
			}
		});
		if(mCamera != null)
		{
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_APMODE_REQ,
					MYAVIOCTRLDEFs.sMsgIsmartGetAPModeReq
							.parseContent(mDevice.ChannelIndex));
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_VOICE_LNG_REQ,
					MYAVIOCTRLDEFs.sMsgIsmartGetVoiceLngReq
					.parseContent(mDevice.ChannelIndex));
		}
	}
	private void initApmode()
	{
		if(txt_parameterset_apmode != null)
		{
			if(apmode ==1)
			{
				txt_parameterset_apmode.setText(getResources().getString(R.string.parameterset_apmode_open));
			}
			else 
			{
				txt_parameterset_apmode.setText(getResources().getString(R.string.parameterset_apmode_closed));
			}
		}
		
	}
	private void initVoiceLanguage()
	{
		if(txt_parameterset_voice_language != null)
		{
			if(voice_language == 1)
			{
				txt_parameterset_voice_language.setText(getResources().getString(R.string.parameterset_voice_en));
			}
			else
			{
				txt_parameterset_voice_language.setText(getResources().getString(R.string.parameterset_voice_zh));
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			quit(false);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void quit(boolean isPressOK) {

		if (ParameterSetActivity.isModifyPassword) {
			mDevice.View_Password = ParameterSetActivity.isModifyPassword ? ParameterSetActivity.newPassword
					: mDevice.View_Password;
			DatabaseManager manager = new DatabaseManager(this);
			manager.updateDeviceInfoByDBID(mDevice.DBID, mDevice.UID,
					mDevice.NickName, "", "", "admin", mDevice.View_Password,
					mDevice.EventNotification, mDevice.ChannelIndex);
		}
		if (ParameterSetActivity.isModifyNicName) {
			DatabaseManager manager = new DatabaseManager(this);
			manager.updateDeviceInfoByDBID(mDevice.DBID, mDevice.UID,
					mDevice.NickName, "", "", "admin", mDevice.View_Password,
					mDevice.EventNotification, mDevice.ChannelIndex);
		}

		if (mCamera != null)
			mCamera.unregisterIOTCListener(this);
		m_wifiList = null;
		videoQualitys = null;
		videoFlips = null;
		environmentModes = null;
		recordModes = null;
		mDevice = null;
		mCamera = null;
		/*
		 * if(bundle.getInt("live") == 1) { Intent view = new Intent();
		 * view.putExtra("cam_info_extra", bundle); view.setClass(this,
		 * VideoLiveActivity.class); startActivity(view); } else { Intent view =
		 * new Intent(); view.setClass(this, MainActivity.class);
		 * startActivity(view); }
		 */
		finish();
	}

	public static String getString(byte[] data) {

		StringBuilder sBuilder = new StringBuilder();

		for (int i = 0; i < data.length; i++) {

			if (data[i] == 0x0)
				break;

			sBuilder.append((char) data[i]);
		}

		return sBuilder.toString();
	}

	private String getVersion(int version) {

		byte[] bytVer = new byte[4];

		StringBuffer sb = new StringBuffer();
		bytVer[3] = (byte) (version);
		bytVer[2] = (byte) (version >>> 8);
		bytVer[1] = (byte) (version >>> 16);
		bytVer[0] = (byte) (version >>> 24);
		sb.append((int) (bytVer[0] & 0xff));
		sb.append('.');
		sb.append((int) (bytVer[1] & 0xff));
		sb.append('.');
		sb.append((int) (bytVer[2] & 0xff));
		sb.append('.');
		sb.append((int) (bytVer[3] & 0xff));

		return sb.toString();
	}

	private int getConut(String sFileName, int count) {

		try {
			InputStream is = getResources().getAssets().open(sFileName);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

			try {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] RowData = line.split(",");
					if (!RowData[2].equals("--")) {
						count++;
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				// handle exception
			}

			finally {
				try {
					is.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					// handle exception
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;

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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveIOCtrlData(com.tutk.IOTC.Camera
	 * , int, int, byte[])
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

			switch (msg.what) {

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP:

				if (data[0] == 0x00)
					Toast.makeText(
							ParameterSetActivity.this,
							getText(R.string.tips_modify_security_code_ok)
									.toString(), Toast.LENGTH_SHORT).show();
				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP:

				int videoQuality = data[4];
				if (videoQuality > 0 && videoQuality <= 5) {
					mVideoQuality = videoQuality - 1;
					if (videoQualitys != null) {
						txt_parameterset_videolevel
								.setText(videoQualitys[mVideoQuality]);
					}
				}
				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP:
				mVideoFlip = data[4];
				if (mVideoFlip >= 0 && mVideoFlip <= 3) {
					if (videoFlips != null)
						txt_parameterset_scree.setText(videoFlips[mVideoFlip]);
				}
				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP:
				mEnvMode = data[4];
				if (mEnvMode >= 0 && mEnvMode <= 3) {
					if (environmentModes != null)
						txt_parameterset_environment
								.setText(environmentModes[mEnvMode]);
				}
				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETRECORD_RESP:

				mRecordType = Packet.byteArrayToInt_Little(data, 4);
				if (mRecordType >= 0 && mRecordType <= 2) {
					if (recordModes != null)
						txt_parameterset_record
								.setText(recordModes[mRecordType]);
				}
				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:

				byte[] bytModel = new byte[16];
				byte[] bytVender = new byte[16];
				System.arraycopy(data, 0, bytModel, 0, 16);
				System.arraycopy(data, 16, bytVender, 0, 16);

				model = getString(bytModel);
				vender = getString(bytVender);
				version = Packet.byteArrayToInt_Little(data, 32);
				free = Packet.byteArrayToInt_Little(data, 44);
				mTotalSize = Packet.byteArrayToInt_Little(data, 40);
				break;
			case MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_APMODE_RESP:
				apmode = data[0];
				initApmode();
				break;
			case MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_VOICE_LNG_RESP:
				voice_language = data[0];
				initVoiceLanguage();
				break;
			}
			
			super.handleMessage(msg);
		}
	};

}
