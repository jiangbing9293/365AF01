package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetFtpReq;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 	 * FTP报警设置
 * @author jiangbing
 *
 */
public class FTPAlarmActivity extends BaseActivity implements IRegisterIOTCListener
{
	
	private TitleView mTitle = null;
	
	private EditText edt_ftp_server = null;
	private EditText edt_ftp_username = null;
	private EditText edt_ftp_password = null;
	private EditText edt_ftp_port = null;
	private EditText edt_ftp_src = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set_ftp);
		if(AlarmSetActivity.mCamera != null)
		{
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_FTP_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetFtpReq.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
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
		mTitle = (TitleView) findViewById(R.id.alarm_ftp_title);
		mTitle.setTitle(getString(R.string.alertset_ftp));
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
					
					String server_ftp1 = edt_ftp_server.getText() == null?"":edt_ftp_server.getText().toString().trim();
					String user_ftp1 = edt_ftp_username.getText() == null?"":edt_ftp_username.getText().toString().trim();
					String pass_ftp1 = edt_ftp_password.getText() == null?"":edt_ftp_password.getText().toString().trim();
					String path1 = edt_ftp_src.getText() == null?"":edt_ftp_src.getText().toString().trim();
					String port = edt_ftp_port.getText() == null?"0":edt_ftp_port.getText().toString().trim();
					
					byte server_ftp[] = new byte[128];
					byte user_ftp[] = new byte[32];
					byte pass_ftp[] = new byte[32];
					byte path[] = new byte[128];
					byte[] temp5 = server_ftp1.getBytes();
					byte[] temp6 = user_ftp1.getBytes();
					byte[] temp7 = pass_ftp1.getBytes();
					byte[] temp8 = path1.getBytes();
					System.arraycopy(temp5, 0, server_ftp, 0, temp5.length);
					System.arraycopy(temp6, 0, user_ftp, 0, temp6.length);
					System.arraycopy(temp7, 0, pass_ftp, 0, temp7.length);
					System.arraycopy(temp8, 0, path, 0, temp8.length);
					
					AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_FTP_REQ, sMsgNetviomSetFtpReq.parseContent(port == null || port.equals("")?0:Integer.valueOf(port), server_ftp, user_ftp, pass_ftp, path));
				}
				finish();
			}
		});
		edt_ftp_server = (EditText)findViewById(R.id.edt_ftp_server);
		edt_ftp_username = (EditText)findViewById(R.id.edt_ftp_username);
		edt_ftp_password = (EditText)findViewById(R.id.edt_ftp_password);
		edt_ftp_port = (EditText)findViewById(R.id.edt_ftp_port);
		edt_ftp_src = (EditText)findViewById(R.id.edt_ftp_src);
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
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_FTP_RESP:
				int port;
				byte server_ftp[] = new byte[128];
				byte user_ftp[] = new byte[32];
				byte pass_ftp[] = new byte[32];
				byte path_ftp[] = new byte[128];
				byte[] pt = new byte[4];
				System.arraycopy(data, 0, pt, 0, 4);
				System.arraycopy(data, 4, server_ftp, 0, 128);
				System.arraycopy(data, 132, user_ftp, 0, 32);
				System.arraycopy(data, 164, pass_ftp, 0, 32);
				System.arraycopy(data, 196, path_ftp, 0, 128);
				port = Packet.byteArrayToInt_Little(pt);
				String ftp_port = port+"";
				String ftp_server = new String(server_ftp).trim();
				String ftp_user = new String(user_ftp).trim();
				String ftp_pass = new String(pass_ftp).trim();
				String ftp_path = new String(path_ftp).trim();
				
				edt_ftp_port.setText(ftp_port);
				edt_ftp_server.setText(ftp_server);
				edt_ftp_username.setText(ftp_user);
				edt_ftp_password.setText(ftp_pass);
				edt_ftp_src.setText(ftp_path);
				break;
			}
		}
	};
}
