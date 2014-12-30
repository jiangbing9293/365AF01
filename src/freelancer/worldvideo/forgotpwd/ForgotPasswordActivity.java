package freelancer.worldvideo.forgotpwd;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
/**
 * 忘记密码
 * @author jiangbing
 *
 */
public class ForgotPasswordActivity extends BaseActivity
{
	private TitleView mTitle = null;
	private EditText email = null;
	private Button submit = null;
	
	String str_email;
	String sessionid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgot_password);
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView()
	{
		mTitle = (TitleView)findViewById(R.id.forgot_password_title);
		email = (EditText)findViewById(R.id.forgot_email);
		submit = (Button)findViewById(R.id.forgot_submit);
		
		mTitle.setTitle("忘记密码");
		mTitle.hiddenRightButton();
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				str_email = email.getText().toString().trim();
				if(str_email == null || str_email.equals(""))
				{
					return;
				}
				if(!MyTool.isPhoneNumberValid(str_email))
				{
					Toast.makeText(ForgotPasswordActivity.this, "手机号不合法",
							Toast.LENGTH_SHORT).show();
					return;
				}
				startProgress();
				getCheckCode();
			}
		});
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) 
		{
			stopProgress();
			if(msg.what == 1)
			{
				String str = (String) msg.obj;
				if (str.substring(0, 1).equals("1")) {
					try {
						Intent main = new Intent();
						main.setClass(ForgotPasswordActivity.this,
								ResetPasswordActivity.class);
						main.putExtra("email", str_email);
						main.putExtra("sessionid", sessionid);
						startActivity(main);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				else
				{
					Toast.makeText(ForgotPasswordActivity.this, str,
							Toast.LENGTH_SHORT).show();
				}
			} 
			else if (msg.what == 2) {
				Toast.makeText(ForgotPasswordActivity.this,
						getText(R.string.toast_net_error),
						Toast.LENGTH_SHORT).show();
			
			}
			super.handleMessage(msg);
		}
		
	};
	private ProgressDialog progress = null;
	private void startProgress()
	{
		if (progress == null) {
			progress = new ProgressDialog(this);
		}
		progress.show();
	}
	
	private void stopProgress()
	{
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}
	private void getCheckCode()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message();
				try {
					GetServerData data = new GetServerData();
					String message = data.getCheckCode(str_email);
					if (message != null) {
						msg.what = 1;
						msg.obj = message;
						sessionid = message.substring(1);
					}
					else
					{
						msg.what = 2;
					}

					handler.sendMessage(msg);
				} catch (Exception e) {
					msg.what = 2;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}).start();
	}
}
