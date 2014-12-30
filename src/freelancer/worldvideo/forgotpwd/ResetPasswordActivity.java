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
import freelancer.worldvideo.LoginActivity;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

public class ResetPasswordActivity extends BaseActivity
{
	
	private TitleView mTitle = null;
	private EditText mCode = null;
	private EditText mNewPassword = null;
	private EditText mSubmitPassword = null;
	private Button mSubmit = null;
	
	String code = null;
	String email = null;
	String newPassword = null;
	
	String sessionid = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgot_password_reset);
		initView();
		email = getIntent().getStringExtra("email");
		sessionid = getIntent().getStringExtra("sessionid");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	private void initView()
	{
		mTitle = (TitleView)findViewById(R.id.reset_password_title);
		mCode = (EditText)findViewById(R.id.reset_code);
		mNewPassword = (EditText)findViewById(R.id.reset_newpwd);
		mSubmitPassword = (EditText)findViewById(R.id.reset_submitpwd);
		mSubmit = (Button)findViewById(R.id.reset_submit);
		mTitle.setTitle("重置密码");
		mTitle.hiddenRightButton();
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		
		mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				code = mCode.getText().toString().trim();
				String n = mNewPassword.getText().toString().trim();
				newPassword = mSubmitPassword.getText().toString().trim();
				if(n == null || !n.equals(newPassword))
				{
					Toast.makeText(ResetPasswordActivity.this, "Two passwords do not match",
							Toast.LENGTH_SHORT).show();
					return;
				}
				startProgress();
				resetPassword();
			}
		});
		
	}
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
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			stopProgress();
			if(msg.what == 1)
			{
				String str = (String) msg.obj;
				if (str.substring(0, 1).equals("1")) {
					try {
						Toast.makeText(ResetPasswordActivity.this, str,
								Toast.LENGTH_SHORT).show();
						Intent main = new Intent();
						main.setClass(ResetPasswordActivity.this,
								LoginActivity.class);
						startActivity(main);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(ResetPasswordActivity.this, str,
							Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 2) {
				Toast.makeText(ResetPasswordActivity.this,
						getText(R.string.toast_net_error),
						Toast.LENGTH_SHORT).show();
			
			}
			super.handleMessage(msg);
		}
		
	};
	private void resetPassword()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				Message msg = new Message();
				try {
					GetServerData data = new GetServerData();
					String message = data.resetPassword(code, email, newPassword,sessionid);
					if (message != null) {
						msg.what = 1;
						msg.obj = message;
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
