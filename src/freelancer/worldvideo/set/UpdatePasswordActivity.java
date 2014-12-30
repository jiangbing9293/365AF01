/**
 * UpdatePasswordActivity.java
 */
package freelancer.worldvideo.set;

import neutral.safe.chinese.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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
 *@function: 修改密码
 *@author jiangbing
 *@data: 2014-7-16
 */
public class UpdatePasswordActivity extends BaseActivity implements IRegisterIOTCListener
{
	private TitleView mTitle = null;
	private EditText old_password = null;
	private EditText new_password = null;
	private EditText confirm_password = null;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update_password);
		initView();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.update_password);
		old_password = (EditText) findViewById(R.id.old_password);
		new_password = (EditText) findViewById(R.id.new_password);
		confirm_password = (EditText) findViewById(R.id.confirm_password);
		
		mTitle.setTitle(getText(R.string.update_password_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener(){

			@Override
			public void onClick(View button) {
				UpdatePasswordActivity.this.finish();
			}
			
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button)
			{
				String oldPwd = old_password.getText().toString();
				String newPwd = new_password.getText().toString();
				String confirmPwd = confirm_password.getText().toString();

				if (oldPwd.length() == 0 || newPwd.length() == 0 || confirmPwd.length() == 0) {
					Toast.makeText(UpdatePasswordActivity.this, getText(R.string.tips_all_field_can_not_empty).toString(), Toast.LENGTH_SHORT).show();
					return;
				}

				if (!oldPwd.equalsIgnoreCase(ParameterSetActivity.mDevice.View_Password)) {
					Toast.makeText(UpdatePasswordActivity.this, getText(R.string.tips_old_password_is_wrong).toString(), Toast.LENGTH_SHORT).show();
					return;
				}

				if (!newPwd.equalsIgnoreCase(confirmPwd)) {
					Toast.makeText(UpdatePasswordActivity.this, getText(R.string.tips_new_passwords_do_not_match).toString(), Toast.LENGTH_SHORT).show();
					return;
				}

				if (ParameterSetActivity.mCamera != null)
					ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetPasswdReq.parseContent(oldPwd, newPwd));

				ParameterSetActivity.newPassword = newPwd;
				ParameterSetActivity.isModifyPassword = true;
			}
		});
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
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
			int avIOCtrlMsgType, byte[] data) 
	{
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

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP:

				if (data[0] == 0x00)
					Toast.makeText(UpdatePasswordActivity.this, getText(R.string.tips_modify_security_code_ok).toString(), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
