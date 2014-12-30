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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.alarmset.AlarmSetActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetOutPutReq;

public class OutputAlarmFragment extends Fragment implements IRegisterIOTCListener
{
	private View mParent = null;
	
	private LinearLayout layout_output_set_open = null;
	private LinearLayout layout_output_set_close = null;
	private ImageView output_gpio_level_open = null;
	private ImageView output_gpio_level_close = null;
	private EditText relay_time = null;
	private int is_output_model = 0;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_OUTPUT_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetOutputReq.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
		initView();
		initListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.fragment_alarm_set_output, container, false);
		return mParent;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(AlarmSetActivity.mCamera != null)
		{
			int d = 5;
			if(relay_time.getText().toString() != null)
			{
				try {
					d = Integer.valueOf(relay_time.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_OUTPUT_REQ, sMsgNetviomSetOutPutReq.parseContent(is_output_model,d));
			AlarmSetActivity.mCamera.unregisterIOTCListener(this);
		}
		handler.removeCallbacksAndMessages(null);
	}
	private void initView()
	{
		layout_output_set_open = (LinearLayout) mParent.findViewById(R.id.layout_output_set_open);
		layout_output_set_close = (LinearLayout) mParent.findViewById(R.id.layout_output_set_close);
		output_gpio_level_open = (ImageView) mParent.findViewById(R.id.output_gpio_level_open);
		output_gpio_level_close = (ImageView) mParent.findViewById(R.id.output_gpio_level_close);
		relay_time = (EditText) mParent.findViewById(R.id.relay_time);
		
		output_gpio_level_open.setVisibility(View.INVISIBLE);
		output_gpio_level_close.setVisibility(View.INVISIBLE);
	}
	
	private void initListener()
	{
		layout_output_set_open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				is_output_model = 0;
				changeStatus();
			}
		});
		layout_output_set_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				is_output_model = 1;
				changeStatus();
			}
		});
	}
	
	private void changeStatus()
	{
		if (is_output_model == 0)
		{
			output_gpio_level_open.setVisibility(View.VISIBLE);
			output_gpio_level_close.setVisibility(View.INVISIBLE);
		}
		else
		{
			output_gpio_level_open.setVisibility(View.INVISIBLE);
			output_gpio_level_close.setVisibility(View.VISIBLE);
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
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_OUTPUT_RESP:
				byte[] delay = new byte[4];
				byte[] state = new byte[4];
				System.arraycopy(data, 0, state, 0, 4);
				System.arraycopy(data, 4, delay, 0, 4);
				relay_time.setText(String.valueOf(Packet.byteArrayToInt_Little(delay)));
				is_output_model = Packet.byteArrayToInt_Little(state);
				changeStatus();
				break;
			}
		}
	};
}
