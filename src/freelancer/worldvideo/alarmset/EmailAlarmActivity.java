package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetEmailReq;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 	 * 邮件报警设置
 * @author jiangbing
 *
 */
public class EmailAlarmActivity extends BaseActivity implements IRegisterIOTCListener
{
	
	private TitleView mTitle = null;
	
	private ImageView alertset_email_check = null;
	private byte is_alertset_email_check = 1;
	private ImageView alertset_email_ssladd = null;
	private byte is_alertset_email_ssladd = 0;
	
	private EditText edt_server_address = null;
	private EditText edt_sender = null;
	private EditText edt_receiver = null;
	private EditText edt_username = null;
	private EditText edt_password = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set_email);
		if(AlarmSetActivity.mCamera != null){
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_EMAIL_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetEmailReq.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
		initView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.unregisterIOTCListener(this);
		}
		handler.removeCallbacksAndMessages(null);
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.alarm_email_title);
		mTitle.setTitle(getString(R.string.alertset_email));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				if (AlarmSetActivity.mCamera != null)
				{
					if(!AlarmSetActivity.mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL))
					{
						return;
					}
					String server_email= edt_server_address.getText() == null?"":edt_server_address.getText().toString().trim();
					String from_email = edt_sender.getText() == null?"":edt_sender.getText().toString().trim();
					String to_email = edt_receiver.getText() == null?"":edt_receiver.getText().toString().trim();
					String user_email = edt_username.getText() == null?"":edt_username.getText().toString().trim();
					String pass_email = edt_password.getText() == null?"":edt_password.getText().toString().trim();
					
					byte server[] = new byte[128];
					byte from[] = new byte[128];
					byte to[] = new byte[128];
					byte user[] = new byte[32];
					byte pass[] = new byte[32];
					byte[] temp = server_email.getBytes();
					byte[] temp1 = from_email.getBytes();
					byte[] temp2 = to_email.getBytes();
					byte[] temp3 = user_email.getBytes();
					byte[] temp4 = pass_email.getBytes();
					System.arraycopy(temp, 0, server, 0, temp.length);
					System.arraycopy(temp1, 0, from, 0, temp1.length);
					System.arraycopy(temp2, 0, to, 0, temp2.length);
					System.arraycopy(temp3, 0, user, 0, temp3.length);
					System.arraycopy(temp4, 0, pass, 0, temp4.length);
					
					AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_EMAIL_REQ, sMsgNetviomSetEmailReq.parseContent(server, from, to, user, pass, is_alertset_email_check, is_alertset_email_ssladd));
				}
				finish();
			}
		});
		edt_server_address = (EditText)findViewById(R.id.edt_server_address);
		edt_sender = (EditText)findViewById(R.id.edt_sender);
		edt_receiver = (EditText)findViewById(R.id.edt_receiver);
		edt_username = (EditText)findViewById(R.id.edt_username);
		edt_password = (EditText)findViewById(R.id.edt_password);
		
		alertset_email_check = (ImageView)findViewById(R.id.alertset_email_check);
		alertset_email_check.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_alertset_email_check == 1)
				{
					is_alertset_email_check = 0;
					alertset_email_check.setImageResource(R.drawable.switch_no);
				}
				else
				{
					is_alertset_email_check = 1;
					alertset_email_check.setImageResource(R.drawable.switch_yes);
				}
			}
		});
		
		alertset_email_ssladd = (ImageView)findViewById(R.id.alertset_email_ssladd);
		alertset_email_ssladd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(is_alertset_email_ssladd == 1)
				{
					is_alertset_email_ssladd = 0;
					alertset_email_ssladd.setImageResource(R.drawable.switch_off);
				}
				else
				{
					is_alertset_email_ssladd = 1;
					alertset_email_ssladd.setImageResource(R.drawable.switch_on);
				}
			}
		});
		initEmail();
	}
	private void initEmail()
	{
		if(is_alertset_email_check == 0)
		{
			alertset_email_check.setImageResource(R.drawable.switch_no);
		}
		else
		{
			alertset_email_check.setImageResource(R.drawable.switch_yes);
		}
		if(is_alertset_email_ssladd == 0)
		{
			alertset_email_ssladd.setImageResource(R.drawable.switch_off);
		}
		else
		{
			alertset_email_ssladd.setImageResource(R.drawable.switch_on);
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
			
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_EMAIL_RESP:
				byte [] server = new byte[128];
				byte from[] = new byte[128];
				byte to[] = new byte[128];
				byte user[] = new byte[32];
				byte pass[] = new byte[32];
				System.arraycopy(data, 0, server, 0, 128);
				System.arraycopy(data, 128, from, 0, 128);
				System.arraycopy(data, 256, to, 0, 128);
				System.arraycopy(data, 384, user, 0, 32);
				System.arraycopy(data, 416, pass, 0, 32);
				is_alertset_email_check = data[448];
				is_alertset_email_ssladd = data[449];
				String email_server = new String(server).trim();
				String email_from = new String(from).trim();
				String email_to = new String(to).trim();
				String email_user = new String(user).trim();
				String email_pass = new String(pass).trim();
				
				edt_server_address.setText(email_server);
				edt_sender.setText(email_from);
				edt_receiver.setText(email_to);
				edt_username.setText(email_user);
				edt_password.setText(email_pass);
				initEmail();
				break;
			}
		}
	};
}
