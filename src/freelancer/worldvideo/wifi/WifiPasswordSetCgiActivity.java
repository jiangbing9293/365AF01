package freelancer.worldvideo.wifi;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.MainActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

public class WifiPasswordSetCgiActivity extends BaseActivity {
	
	private TitleView mTitle = null;
	private EditText wifi_passwrod = null;
	private ImageView pwd_clear = null;
	private String ssid = null;
	private String username = null;
	private String pwd = null;
	
	private Handler mHandler = null;
	private ProgressDialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_wifi_password);
		mHandler = new Handler();
		dialog = new ProgressDialog(this);
		ssid = getIntent().getStringExtra("ssid");
		username = getIntent().getStringExtra("username");
		pwd = getIntent().getStringExtra("password");
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		mTitle = (TitleView) findViewById(R.id.wifi_password_set_title);
		wifi_passwrod = (EditText) findViewById(R.id.wifi_password);
		pwd_clear = (ImageView) findViewById(R.id.pwd_clear);

		mTitle.setTitle(getString(R.string.wifi_manager_password_title));

		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save,
				new OnRightButtonClickListener() {
					@Override
					public void onClick(View button) {
						String password = wifi_passwrod.getText().toString()
								.trim();
						if (ssid != null && password != null) {
							 setWiFiThread(password);
						}
						
					}
				});
		pwd_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifi_passwrod.setText("");
			}
		});
	}
	
	private void setWiFiThread(final String password)
	{
		if(dialog != null)
		{
			dialog.setMessage("正在配置中...");
			dialog.show();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final boolean result = WifiSetByCgiCmd.getInstance().sendSetRequest(ssid,
						password, username, pwd);
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(dialog != null)
							dialog.dismiss();
						if(!result)
						{
							Toast.makeText(WifiPasswordSetCgiActivity.this, "配置失败...", Toast.LENGTH_SHORT).show();
							return;
						}
						wifi_passwrod.setText("");
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.setClass(WifiPasswordSetCgiActivity.this,
								MainActivity.class);
						startActivity(intent);
						WifiManager mWifiManager = (WifiManager) getApplicationContext() 
					                .getSystemService(Context.WIFI_SERVICE); 
						mWifiManager.setWifiEnabled(false);
						mWifiManager.setWifiEnabled(true);
					}
				});
			}
		}).start();
	}
}
