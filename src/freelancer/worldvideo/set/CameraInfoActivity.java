package freelancer.worldvideo.set;

import neutral.safe.chinese.R;
import neutral.safe.chinese.R.color;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.MainActivity;
import freelancer.worldvideo.ParameterSetActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

public class CameraInfoActivity extends BaseActivity implements IRegisterIOTCListener
{
	
	private TitleView mTitle = null;
	private EditText cam_name = null;
	private TextView cam_uid = null;
	private TextView cam_ip = null;
	private TextView sd_total = null;
	private TextView sd_free = null;
	private LinearLayout reboot = null;
	private LinearLayout fac_default = null;
	
	private LinearLayout layout_voice = null;
	private TextView voice_txt = null;
	private ImageView voice_img = null;
	
	private int my_voice = 0;
	
	private Handler mHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_camera_info);
		initView();
		mHandler = new Handler();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.paramter_set_camera_info_title);
		cam_name = (EditText) findViewById(R.id.camera_info_name);
		cam_uid = (TextView) findViewById(R.id.camera_info_uid);
		cam_ip = (TextView) findViewById(R.id.camera_info_ip);
		sd_total = (TextView) findViewById(R.id.camera_info_sd_total);
		sd_free = (TextView) findViewById(R.id.camera_info_sd_free);
		reboot = (LinearLayout) findViewById(R.id.reboot);
		fac_default = (LinearLayout) findViewById(R.id.factory_default);
		
		layout_voice = (LinearLayout)findViewById(R.id.layout_voice_switch);
		voice_txt = (TextView)findViewById(R.id.voice_txt);
		voice_img = (ImageView)findViewById(R.id.voice_img);
		layout_voice.setVisibility(View.GONE);
		layout_voice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					if(my_voice == 0)
					{
						my_voice = 1;
					}
					else
					{
						my_voice = 0;
					}
					 initVoice();
					 if(ParameterSetActivity.mCamera != null)
						 ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_ISMART_SET_VOICE_REQ, MYAVIOCTRLDEFs.sMsgIsmartSetVoiceReq.parseContent(my_voice));
						
			}
		});
		
		mTitle.setTitle(getString(R.string.camera_info_title));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				String name =  cam_name.getText().toString();
				if(name != null && !name.equals(""))
				{
					ParameterSetActivity.isModifyNicName = true;
					ParameterSetActivity.mDevice.NickName = name;
				}
				finish();
			}
		});
		mTitle.hiddenRightButton();
		reboot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ParameterSetActivity.mCamera != null)
				{
						startProgress();
						ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_REBOOT_REQ, MYAVIOCTRLDEFs.sMsgNetviomSetRebootReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex));
				}
			}
		});
		fac_default.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ParameterSetActivity.mCamera != null)
				{
						startProgress();
						ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DEFAULT_REQ, MYAVIOCTRLDEFs.sMsgNetviomSetDefaultReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex));
				}
			}
		});
		cam_name.setText(ParameterSetActivity.mDevice.NickName);
		cam_uid.setText(ParameterSetActivity.mDevice.UID);
		if(UIApplication.getDevIPtUid(ParameterSetActivity.mDevice.UID) != null)
		{
			cam_ip.setVisibility(View.VISIBLE);
			cam_ip.setText(UIApplication.getDevIPtUid(ParameterSetActivity.mDevice.UID));
		}
		else
		{
			cam_ip.setVisibility(View.GONE);
		}
		if(ParameterSetActivity.mTotalSize > 0){
			sd_total.setText(ParameterSetActivity.mTotalSize+"M");
		}
		else
		{
			sd_total.setText("无SD卡");
		}
		sd_free.setText(ParameterSetActivity.free+"M");
		
		if(ParameterSetActivity.mCamera != null)
		{
			ParameterSetActivity.mCamera.registerIOTCListener(this);
			ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_VOICE_REQ, MYAVIOCTRLDEFs.sMsgIsmartGetVoiceReq.parseContent(0));
		}
	}
	
	private void initVoice()
	{
		if(my_voice == 1)
		{
			if(voice_txt != null)
				voice_txt.setTextColor(getResources().getColor(R.color.main_color));
			if(voice_img != null)
				voice_img.setImageResource(R.drawable.check);
		}
		else
		{
			if(voice_txt != null)
				voice_txt.setTextColor(Color.GRAY);
			if(voice_img != null)
				voice_img.setImageBitmap(null);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		String name =  cam_name.getText().toString();
		if(name != null && !name.equals(""))
		{
			ParameterSetActivity.isModifyNicName = true;
			ParameterSetActivity.mDevice.NickName = name;
		}
		return super.onKeyDown(keyCode, event);
	}
	private Handler handler = new Handler();
	private void waitting()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				long t = System.currentTimeMillis();
				
				while(!Thread.currentThread().isInterrupted())
				{
					if(System.currentTimeMillis() - t > 3000)
					{
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								stopProgress();
								Intent main = new Intent();
								main.setClass(CameraInfoActivity.this, MainActivity.class);
								startActivity(main);
							}
						});
						break;
					}
				}
				try {
					Thread.interrupted();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	private ProgressDialog progress = null;
	private void startProgress()
	{
		if (progress == null) {
			progress = new ProgressDialog(this);
		}
		progress.setMessage("正在处理中...");
		progress.show();
	}
	
	private void stopProgress()
	{
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType,final byte[] data) 
	{
		if(avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_REBOOT_RESP)
		{
			 waitting();
		}
		else if(avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DEFAULT_RESP || avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DEFAULT_RESP-1)
		{
			 waitting();
		}
		else if(avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_ISMART_GET_VOICE_RESP)
		{
			my_voice = data[0];
			
			if(mHandler != null)
			{
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if(layout_voice != null)
						{
							layout_voice.setVisibility(View.VISIBLE);
						}
						 initVoice();
					}
				});
			}
		}
		else if(avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_ISMART_SET_VOICE_RESP)
		{
			if(mHandler != null)
			{
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if(data[0] == 0)
							Toast.makeText(getApplicationContext(), "set fail", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}
	
	
}
