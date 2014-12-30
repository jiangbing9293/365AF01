package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetMotionReq;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

public class MotionDetionActivity extends BaseActivity implements IRegisterIOTCListener
{
	
	private TitleView mTitle = null;
	
	private int MAIN_COLOR = 0xff00A4C5;
	private int GRY_COLOR = 0xffd9d9d9;
	
	/**
	 * 移动侦听
	 */
	private LinearLayout layout_motion_set = null;
	private LinearLayout layout_motion_sd = null;
	private LinearLayout layout_motion_output = null;
	private LinearLayout layout_motion_email = null;
	private LinearLayout layout_motion_ftp= null;
	private LinearLayout layout_motion_ftpv = null;
	
	private ImageView motion_set = null;
	private ImageView motion_sd = null;
	private ImageView motion_output = null;
	private ImageView motion_email = null;
	private ImageView motion_ftp = null;
	private ImageView motion_ftpv = null;
	
	private TextView motion_sd_txt = null;
	private TextView motion_output_txt = null;
	private TextView motion_email_txt = null;
	private TextView motion_ftp_txt = null;
	private TextView motion_ftpv_txt = null;
	
	private int is_mobile_set = 0;
	private byte is_sdcard_record = 1;
	private byte is_output = 0;
	private byte is_email_picture = 1;
	private byte is_ftp_picture = 1;
	private byte is_ftp_video = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set_motion);
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_MOTION_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetMotionRep.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
		initView();
		initListener();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.alarm_motion_title);
		mTitle.setTitle(getString(R.string.alertset_mobile));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) 
			{
				if(AlarmSetActivity.mCamera != null)
					AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_MOTION_REQ, sMsgNetviomSetMotionReq.parseContent(is_mobile_set, is_sdcard_record, is_output, is_email_picture, is_ftp_picture, is_ftp_video));
				finish();
			}
		});
		mTitle.hiddenRightButton();
		
		layout_motion_set = (LinearLayout) findViewById(R.id.layout_motion_set);
		layout_motion_sd = (LinearLayout) findViewById(R.id.layout_motion_sd);
		layout_motion_output = (LinearLayout) findViewById(R.id.layout_motion_output);
		layout_motion_email = (LinearLayout) findViewById(R.id.layout_motion_email);
		layout_motion_ftp = (LinearLayout) findViewById(R.id.layout_motion_ftp);
		layout_motion_ftpv = (LinearLayout) findViewById(R.id.layout_motion_ftpv);
		
		motion_set = (ImageView) findViewById(R.id.motion_set);
		motion_sd = (ImageView) findViewById(R.id.motion_sd);
		motion_output = (ImageView) findViewById(R.id.motion_output);
		motion_email = (ImageView) findViewById(R.id.motion_email);
		motion_ftp = (ImageView) findViewById(R.id.motion_ftp);
		motion_ftpv = (ImageView) findViewById(R.id.motion_ftpv);
		
		motion_sd_txt = (TextView) findViewById(R.id.motion_sd_txt);
		motion_output_txt = (TextView) findViewById(R.id.motion_output_txt);
		motion_email_txt = (TextView) findViewById(R.id.motion_email_txt);
		motion_ftp_txt = (TextView) findViewById(R.id.motion_ftp_txt);
		motion_ftpv_txt = (TextView) findViewById(R.id.motion_ftpv_txt);
		hide();
	}
	
	private void hide()
	{
		layout_motion_sd.setVisibility(View.INVISIBLE);
		layout_motion_output.setVisibility(View.INVISIBLE);
		layout_motion_email.setVisibility(View.INVISIBLE);
		layout_motion_ftp.setVisibility(View.INVISIBLE);
		layout_motion_ftpv.setVisibility(View.INVISIBLE);
		
		motion_sd_txt.setTextColor(GRY_COLOR);
		motion_output_txt.setTextColor(GRY_COLOR);
		motion_email_txt.setTextColor(GRY_COLOR);
		motion_ftp_txt.setTextColor(GRY_COLOR);
		motion_ftpv_txt.setTextColor(GRY_COLOR);
	}
	private void show()
	{
		layout_motion_sd.setVisibility(View.VISIBLE);
		layout_motion_output.setVisibility(View.VISIBLE);
		layout_motion_email.setVisibility(View.VISIBLE);
		layout_motion_ftp.setVisibility(View.VISIBLE);
		layout_motion_ftpv.setVisibility(View.VISIBLE);
	}
	
	private void initListener()
	{
		layout_motion_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_mobile_set == 0) {
					is_mobile_set = 1;
				}
				else
				{
					is_mobile_set = 0;
				}
				changeStatus();
			}
		});
		layout_motion_sd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_mobile_set == 0)
					return;
				if (is_sdcard_record == 0) {
					is_sdcard_record = 1;
				}
				else
				{
					is_sdcard_record = 0;
				}
				changeStatus();
			}
		});
		layout_motion_output.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_mobile_set == 0)
					return;
				if (is_output == 0) {
					is_output = 1;
				}
				else
				{
					is_output = 0;
				}
				changeStatus();
			}
		});
		layout_motion_email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_mobile_set == 0)
					return;
				if (is_email_picture == 0) {
					is_email_picture = 1;
				}
				else
				{
					is_email_picture = 0;
				}
				changeStatus();
			}
		});
		layout_motion_ftp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_mobile_set == 0)
					return;
				if (is_ftp_picture == 0) {
					is_ftp_picture = 1;
				}
				else
				{
					is_ftp_picture = 0;
				}
				changeStatus();
			}
		});
		layout_motion_ftpv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_mobile_set == 0)
					return;
				if (is_ftp_video == 0) {
					is_ftp_video = 1;
				}
				else
				{
					is_ftp_video = 0;
				}
				changeStatus();
			}
		});
	}
	
	private void changeStatus()
	{
		if (is_mobile_set == 0)
		{
			motion_set.setImageResource(R.drawable.switch_off);
			hide();
			return;
		}
		else
		{
			motion_set.setImageResource(R.drawable.switch_on);
			show();
		}
		
		if (is_sdcard_record == 0)
		{
			motion_sd.setVisibility(View.INVISIBLE);
			motion_sd_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			motion_sd.setVisibility(View.VISIBLE);
			motion_sd_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_output == 0)
		{
			motion_output.setVisibility(View.INVISIBLE);
			motion_output_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			motion_output.setVisibility(View.VISIBLE);
			motion_output_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_email_picture == 0)
		{
			motion_email.setVisibility(View.INVISIBLE);
			motion_email_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			motion_email.setVisibility(View.VISIBLE);
			motion_email_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_picture == 0)
		{
			motion_ftp.setVisibility(View.INVISIBLE);
			motion_ftp_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			motion_ftp.setVisibility(View.VISIBLE);
			motion_ftp_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_video == 0)
		{
			motion_ftpv.setVisibility(View.INVISIBLE);
			motion_ftpv_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			motion_ftpv.setVisibility(View.VISIBLE);
			motion_ftpv_txt.setTextColor(MAIN_COLOR);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(AlarmSetActivity.mCamera != null)
			AlarmSetActivity.mCamera.unregisterIOTCListener(this);
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
		if (AlarmSetActivity.mCamera == camera) 
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
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_MOTION_RESP:
				byte[] oo = new byte[4];
				System.arraycopy(data, 0, oo, 0, 4);
				is_mobile_set = Packet.byteArrayToInt_Little(oo);
				is_sdcard_record = data[4];
				is_output = data[5];
				is_email_picture = data[6];
				is_ftp_picture = data[7];
				is_ftp_video = data[8];
				changeStatus();
				break;
			}
		}
	};
}
