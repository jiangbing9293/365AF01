package freelancer.worldvideo.set;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.ParameterSetActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 
 * @author jiangbing
 *
 */
public class WifiPasswordSetActivity extends BaseActivity implements IRegisterIOTCListener
{
	private TitleView mTitle = null;
	private EditText wifi_passwrod = null;
	private int pos = 0;
	private ImageView pwd_clear = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_wifi_password);
		pos = getIntent().getIntExtra("pos", -1);
		initView();
		ParameterSetActivity.mCamera.registerIOTCListener(this);
	}

	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.wifi_password_set_title);
		wifi_passwrod = (EditText) findViewById(R.id.wifi_password);
		pwd_clear = (ImageView) findViewById(R.id.pwd_clear);
		if(WifiManagerActivity.m_wifiList != null)
		{
			mTitle.setTitle(ParameterSetActivity.getString(WifiManagerActivity.m_wifiList.get(pos).ssid));
		}
		else
		{
			mTitle.setTitle(getString(R.string.wifi_manager_password_title));
		}
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				String pwd = wifi_passwrod.getText().toString();
				AVIOCTRLDEFs.SWifiAp wifi = WifiManagerActivity.m_wifiList.get(pos);

				if (pwd.length() == 0) {
					Toast.makeText(WifiPasswordSetActivity.this, getText(R.string.tips_all_field_can_not_empty).toString(), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (ParameterSetActivity.mCamera != null && wifi != null) {
					ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(wifi.ssid, pwd.getBytes(), wifi.mode, wifi.enctype));
					ParameterSetActivity.isModifyWiFi = true;
				}
				wifi_passwrod.setText("");
			}
		});
		pwd_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifi_passwrod.setText("");
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ParameterSetActivity.mCamera.unregisterIOTCListener(this);
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
		if (ParameterSetActivity.mCamera == camera) {
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
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
				if(data[0] == 0)
				{
					Toast.makeText(WifiPasswordSetActivity.this, getString(R.string.wifi_manager_password_success), Toast.LENGTH_SHORT).show();
					Intent back = new Intent();
					back.setClass(WifiPasswordSetActivity.this, WifiManagerActivity.class);
					back.putExtra("save", true);
					startActivity(back);
				}
				else
				{
					Toast.makeText(WifiPasswordSetActivity.this, getString(R.string.wifi_manager_password_fail), Toast.LENGTH_SHORT).show();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
}
