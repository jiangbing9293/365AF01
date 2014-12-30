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
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetPIRReq;

public class PIRAlarmFragment extends Fragment implements IRegisterIOTCListener
{
	private View mParent = null;
	
	private int MAIN_COLOR = 0xff00A4C5;
	private int GRY_COLOR = 0xffd9d9d9;
	
	private LinearLayout layout_pir_set_open = null;
	private LinearLayout layout_pir_sd = null;
	private LinearLayout layout_pir_output = null;
	private LinearLayout layout_pir_email = null;
	private LinearLayout layout_pir_ftp= null;
	private LinearLayout layout_pir_ftpv = null;
	private LinearLayout layout_pir_ptz = null;
	
	private ImageView pir_set_open = null;
	private ImageView pir_sd = null;
	private ImageView pir_output = null;
	private ImageView pir_email = null;
	private ImageView pir_ftp = null;
	private ImageView pir_ftpv = null;
	private ImageView pir_ptz = null;
	
	private TextView pir_sd_txt = null;
	private TextView pir_output_txt = null;
	private TextView pir_email_txt = null;
	private TextView pir_ftp_txt = null;
	private TextView pir_ftpv_txt = null;
	private TextView pir_ptz_txt = null;
	
	private int is_other_set_pir = 0;
	private byte is_sdcard_recordpir = 0;
	private byte is_outputpir = 0;
	private byte is_email_picturepir = 1;
	private byte is_ftp_picturepir = 1;
	private byte is_ftp_videopir = 0;
	private byte is_other_couldpir = 0;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_PIR_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetPIRRep.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
		initView();
		initListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.fragment_alarm_set_pir, container, false);
		return mParent;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_PIR_REQ, sMsgNetviomSetPIRReq.parseContent(is_other_set_pir, is_sdcard_recordpir, is_outputpir, is_email_picturepir, is_ftp_picturepir, is_ftp_videopir,is_other_couldpir));
			AlarmSetActivity.mCamera.unregisterIOTCListener(this);
		}
		handler.removeCallbacksAndMessages(null);
	}
	private void initView()
	{
		
		layout_pir_set_open = (LinearLayout) mParent.findViewById(R.id.layout_pir_set_open);
		layout_pir_sd = (LinearLayout) mParent.findViewById(R.id.layout_pir_sd);
		layout_pir_output = (LinearLayout) mParent.findViewById(R.id.layout_pir_output);
		layout_pir_email = (LinearLayout) mParent.findViewById(R.id.layout_pir_email);
		layout_pir_ftp = (LinearLayout) mParent.findViewById(R.id.layout_pir_ftp);
		layout_pir_ftpv = (LinearLayout) mParent.findViewById(R.id.layout_pir_ftpv);
		layout_pir_ptz = (LinearLayout) mParent.findViewById(R.id.layout_pir_ptz);
		
		pir_set_open = (ImageView) mParent.findViewById(R.id.pir_set_open);
		pir_sd = (ImageView) mParent.findViewById(R.id.pir_sd);
		pir_output = (ImageView) mParent.findViewById(R.id.pir_output);
		pir_email = (ImageView) mParent.findViewById(R.id.pir_email);
		pir_ftp = (ImageView) mParent.findViewById(R.id.pir_ftp);
		pir_ftpv = (ImageView) mParent.findViewById(R.id.pir_ftpv);
		pir_ptz = (ImageView) mParent.findViewById(R.id.pir_ptz);
		
		pir_sd_txt = (TextView) mParent.findViewById(R.id.pir_sd_txt);
		pir_output_txt = (TextView) mParent.findViewById(R.id.pir_output_txt);
		pir_email_txt = (TextView) mParent.findViewById(R.id.pir_email_txt);
		pir_ftp_txt = (TextView) mParent.findViewById(R.id.pir_ftp_txt);
		pir_ftpv_txt = (TextView) mParent.findViewById(R.id.pir_ftpv_txt);
		pir_ptz_txt = (TextView) mParent.findViewById(R.id.pir_ptz_txt);
		hide();
	}
	
	private void hide()
	{
		layout_pir_sd.setVisibility(View.INVISIBLE);
		layout_pir_output.setVisibility(View.INVISIBLE);
		layout_pir_email.setVisibility(View.INVISIBLE);
		layout_pir_ftp.setVisibility(View.INVISIBLE);
		layout_pir_ftpv.setVisibility(View.INVISIBLE);
		layout_pir_ptz.setVisibility(View.INVISIBLE);
		
		pir_sd_txt.setTextColor(GRY_COLOR);
		pir_output_txt.setTextColor(GRY_COLOR);
		pir_email_txt.setTextColor(GRY_COLOR);
		pir_ftp_txt.setTextColor(GRY_COLOR);
		pir_ftpv_txt.setTextColor(GRY_COLOR);
		pir_ptz_txt.setTextColor(GRY_COLOR);
	}
	private void show()
	{
		layout_pir_sd.setVisibility(View.VISIBLE);
		layout_pir_output.setVisibility(View.VISIBLE);
		layout_pir_email.setVisibility(View.VISIBLE);
		layout_pir_ftp.setVisibility(View.VISIBLE);
		layout_pir_ftpv.setVisibility(View.VISIBLE);
		layout_pir_ptz.setVisibility(View.VISIBLE);
	}
	private void initListener()
	{
		layout_pir_set_open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
				{
					is_other_set_pir = 0;
				}
				else
				{
					is_other_set_pir = 1;
				}
				changeStatus();
			}
		});
		layout_pir_sd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_sdcard_recordpir == 0) {
					is_sdcard_recordpir = 1;
				}
				else
				{
					is_sdcard_recordpir = 0;
				}
				changeStatus();
			}
		});
		layout_pir_output.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_outputpir == 0) {
					is_outputpir = 1;
				}
				else
				{
					is_outputpir = 0;
				}
				changeStatus();
			}
		});
		layout_pir_email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_email_picturepir == 0) {
					is_email_picturepir = 1;
				}
				else
				{
					is_email_picturepir = 0;
				}
				changeStatus();
			}
		});
		layout_pir_ftp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_ftp_picturepir == 0) {
					is_ftp_picturepir = 1;
				}
				else
				{
					is_ftp_picturepir = 0;
				}
				changeStatus();
			}
		});
		layout_pir_ftpv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_ftp_videopir == 0) {
					is_ftp_videopir = 1;
				}
				else
				{
					is_ftp_videopir = 0;
				}
				changeStatus();
			}
		});
		layout_pir_ptz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_other_set_pir == 1)
					return;
				if (is_other_couldpir == 0) {
					is_other_couldpir = 1;
				}
				else
				{
					is_other_couldpir = 0;
				}
				changeStatus();
			}
		});
	}
	
	private void changeStatus()
	{
		if (is_other_set_pir == 1)
		{
			pir_set_open.setImageResource(R.drawable.switch_off);
			hide();
			return;
		}
		else
		{
			pir_set_open.setImageResource(R.drawable.switch_on);
			show();
		}
		
		if (is_sdcard_recordpir == 0)
		{
			pir_sd.setVisibility(View.INVISIBLE);
			pir_sd_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_sd.setVisibility(View.VISIBLE);
			pir_sd_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_outputpir == 0)
		{
			pir_output.setVisibility(View.INVISIBLE);
			pir_output_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_output.setVisibility(View.VISIBLE);
			pir_output_txt.setTextColor(MAIN_COLOR);
		}
		
		if (is_email_picturepir == 0)
		{
			pir_email.setVisibility(View.INVISIBLE);
			pir_email_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_email.setVisibility(View.VISIBLE);
			pir_email_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_picturepir == 0)
		{
			pir_ftp.setVisibility(View.INVISIBLE);
			pir_ftp_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_ftp.setVisibility(View.VISIBLE);
			pir_ftp_txt.setTextColor(MAIN_COLOR);
		}
		if (is_ftp_videopir == 0)
		{
			pir_ftpv.setVisibility(View.INVISIBLE);
			pir_ftpv_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_ftpv.setVisibility(View.VISIBLE);
			pir_ftpv_txt.setTextColor(MAIN_COLOR);
		}
		if (is_other_couldpir == 0)
		{
			pir_ptz.setVisibility(View.INVISIBLE);
			pir_ptz_txt.setTextColor(GRY_COLOR);
		}
		else
		{
			pir_ptz.setVisibility(View.VISIBLE);
			pir_ptz_txt.setTextColor(MAIN_COLOR);
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
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		if (AlarmSetActivity.mCamera == camera) 
		{
			Bundle bundle = new Bundle();
			bundle.putInt("sesspirnChannel", avChannel);
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
				byte[] pir = new byte[4];
				System.arraycopy(data, 0, pir, 0, 4);
				is_other_set_pir = Packet.byteArrayToInt_Little(pir);
				is_sdcard_recordpir = data[4];
				is_outputpir = data[5];
				is_email_picturepir = data[6];
				is_ftp_picturepir = data[7];
				is_ftp_videopir = data[8];
				is_other_couldpir = data[9];
				changeStatus();
				break;
			}
		}
	};
}
