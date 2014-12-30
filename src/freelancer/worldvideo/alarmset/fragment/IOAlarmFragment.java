package freelancer.worldvideo.alarmset.fragment;

import neutral.safe.chinese.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.alarmset.AlarmSetActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetIOReq;

public class IOAlarmFragment extends Fragment implements IRegisterIOTCListener
{
	
	private View mParent = null;
	
	private int MAIN_COLOR = 0xff00A4C5;
	private int GRY_COLOR = 0xffd9d9d9;
	
	private LinearLayout layout_io_set_open = null;
	private LinearLayout layout_io_set_close = null;
	private LinearLayout layout_io_sd = null;
	private LinearLayout layout_io_output = null;
	private LinearLayout layout_io_email = null;
	private LinearLayout layout_io_ftp= null;
	private LinearLayout layout_io_ftpv = null;
	private LinearLayout layout_io_ptz = null;
	
	private ImageView io_set_open = null;
	private ImageView io_set_close = null;
	private ImageView io_sd = null;
	private ImageView io_output = null;
	private ImageView io_email = null;
	private ImageView io_ftp = null;
	private ImageView io_ftpv = null;
	private ImageView io_ptz = null;
	
	private TextView io_sd_txt = null;
	private TextView io_output_txt = null;
	private TextView io_email_txt = null;
	private TextView io_ftp_txt = null;
	private TextView io_ftpv_txt = null;
	private TextView io_ptz_txt = null;
	
	private int is_other_set = 0;
	private byte is_sdcard_recordo = 0;
	private byte is_outputo = 0;
	private byte is_email_pictureo = 1;
	private byte is_ftp_pictureo = 1;
	private byte is_ftp_videoo = 0;
	private byte is_other_could = 0;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_IO_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetIORep.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.fragment_alarm_set_io, container, false);
		initView();
		initListener();
		return mParent;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_IO_REQ, sMsgNetviomSetIOReq.parseContent(is_other_set, is_sdcard_recordo, is_outputo, is_email_pictureo, is_ftp_pictureo, is_ftp_videoo,is_other_could));
			AlarmSetActivity.mCamera.unregisterIOTCListener(this);
		}
		handler.removeCallbacksAndMessages(null);
	}
	private void initView()
	{
		layout_io_set_open = (LinearLayout) mParent.findViewById(R.id.layout_io_set_open);
		layout_io_set_close = (LinearLayout) mParent.findViewById(R.id.layout_io_set_close);
		layout_io_sd = (LinearLayout) mParent.findViewById(R.id.layout_io_sd);
		layout_io_output = (LinearLayout) mParent.findViewById(R.id.layout_io_output);
		layout_io_email = (LinearLayout) mParent.findViewById(R.id.layout_io_email);
		layout_io_ftp = (LinearLayout) mParent.findViewById(R.id.layout_io_ftp);
		layout_io_ftpv = (LinearLayout) mParent.findViewById(R.id.layout_io_ftpv);
		layout_io_ptz = (LinearLayout) mParent.findViewById(R.id.layout_io_ptz);
		
		io_set_open = (ImageView) mParent.findViewById(R.id.io_set_open);
		io_set_close = (ImageView) mParent.findViewById(R.id.io_set_close);
		io_sd = (ImageView) mParent.findViewById(R.id.io_sd);
		io_output = (ImageView) mParent.findViewById(R.id.io_output);
		io_email = (ImageView) mParent.findViewById(R.id.io_email);
		io_ftp = (ImageView) mParent.findViewById(R.id.io_ftp);
		io_ftpv = (ImageView) mParent.findViewById(R.id.io_ftpv);
		io_ptz = (ImageView) mParent.findViewById(R.id.io_ptz);
		
		io_sd_txt = (TextView) mParent.findViewById(R.id.io_sd_txt);
		io_output_txt = (TextView) mParent.findViewById(R.id.io_output_txt);
		io_email_txt = (TextView) mParent.findViewById(R.id.io_email_txt);
		io_ftp_txt = (TextView) mParent.findViewById(R.id.io_ftp_txt);
		io_ftpv_txt = (TextView) mParent.findViewById(R.id.io_ftpv_txt);
		io_ptz_txt = (TextView) mParent.findViewById(R.id.io_ptz_txt);
		hide();
	}
	
	private void hide()
	{
		layout_io_sd.setVisibility(View.INVISIBLE);
		layout_io_output.setVisibility(View.INVISIBLE);
		layout_io_email.setVisibility(View.INVISIBLE);
		layout_io_ftp.setVisibility(View.INVISIBLE);
		layout_io_ftpv.setVisibility(View.INVISIBLE);
		layout_io_ptz.setVisibility(View.INVISIBLE);
		
		io_sd_txt.setTextColor(GRY_COLOR);
		io_output_txt.setTextColor(GRY_COLOR);
		io_email_txt.setTextColor(GRY_COLOR);
		io_ftp_txt.setTextColor(GRY_COLOR);
		io_ftpv_txt.setTextColor(GRY_COLOR);
		io_ptz_txt.setTextColor(GRY_COLOR);
	}
	private void show()
	{
		layout_io_sd.setVisibility(View.VISIBLE);
		layout_io_output.setVisibility(View.VISIBLE);
		layout_io_email.setVisibility(View.VISIBLE);
		layout_io_ftp.setVisibility(View.VISIBLE);
		layout_io_ftpv.setVisibility(View.VISIBLE);
		layout_io_ptz.setVisibility(View.VISIBLE);
	}
	
	private void initListener()
	{
		layout_io_set_open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				is_other_set = 1;
				changeStatus();
			}
		});
		layout_io_set_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				is_other_set = 0;
				changeStatus();
			}
		});
		layout_io_sd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_sdcard_recordo == 0) 
				{
					is_sdcard_recordo = 1;
				}
				else
				{
					is_sdcard_recordo = 0;
				}
				changeStatus();
			}
		});
		layout_io_output.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_outputo == 0) {
					is_outputo = 1;
				}
				else
				{
					is_outputo = 0;
				}
				changeStatus();
			}
		});
		layout_io_email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_email_pictureo == 0) {
					is_email_pictureo = 1;
				}
				else
				{
					is_email_pictureo = 0;
				}
				changeStatus();
			}
		});
		layout_io_ftp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_ftp_pictureo == 0) {
					is_ftp_pictureo = 1;
				}
				else
				{
					is_ftp_pictureo = 0;
				}
				changeStatus();
			}
		});
		layout_io_ftpv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_ftp_videoo == 0) {
					is_ftp_videoo = 1;
				}
				else
				{
					is_ftp_videoo = 0;
				}
				changeStatus();
			}
		});
		layout_io_ptz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (is_other_could == 0) {
					is_other_could = 1;
				}
				else
				{
					is_other_could = 0;
				}
				changeStatus();
			}
		});
	}
	
	private void changeStatus()
	{
		if (is_other_set == 1)
		{
			io_set_open.setVisibility(View.VISIBLE);
			io_set_close.setVisibility(View.INVISIBLE);
			show();
		}
		else
		{
			io_set_open.setVisibility(View.INVISIBLE);
			io_set_close.setVisibility(View.VISIBLE);
			hide();
			return;
		}
		
		if (is_sdcard_recordo == 0)
		{
			io_sd.setVisibility(View.INVISIBLE);
			io_sd_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_sd.setVisibility(View.VISIBLE);
			io_sd_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_outputo == 0)
		{
			io_output.setVisibility(View.INVISIBLE);
			io_output_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_output.setVisibility(View.VISIBLE);
			io_output_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_email_pictureo == 0)
		{
			io_email.setVisibility(View.INVISIBLE);
			io_email_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_email.setVisibility(View.VISIBLE);
			io_email_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_pictureo == 0)
		{
			io_ftp.setVisibility(View.INVISIBLE);
			io_ftp_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_ftp.setVisibility(View.VISIBLE);
			io_ftp_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_videoo == 0)
		{
			io_ftpv.setVisibility(View.INVISIBLE);
			io_ftpv_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_ftpv.setVisibility(View.VISIBLE);
			io_ftpv_txt.setTextColor(MAIN_COLOR);
		}
		if (is_other_could == 0)
		{
			io_ptz.setVisibility(View.INVISIBLE);
			io_ptz_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			io_ptz.setVisibility(View.VISIBLE);
			io_ptz_txt.setTextColor(MAIN_COLOR);
		}
	}
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
		
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
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_IO_RESP:
				byte[] ooo = new byte[4];
				System.arraycopy(data, 0, ooo, 0, 4);
				is_other_set = Packet.byteArrayToInt_Little(ooo);
				is_sdcard_recordo = data[4];
				is_outputo = data[5];
				is_email_pictureo = data[6];
				is_ftp_pictureo = data[7];
				is_ftp_videoo = data[8];
				is_other_could = data[9];
				changeStatus();
				break;
			}
		}
	};
}
