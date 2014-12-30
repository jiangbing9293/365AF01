/**
 * LoginActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import freelancer.worldvideo.forgotpwd.ForgotPasswordActivity;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.wifi.WifiAdmin;

/**
 * @function: 登录界面
 * @author jiangbing
 * @data: 2014-2-9
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog loadingDialog;

	private GetServerData data = null;
	private Handler x = null;
	String message = "";

	private boolean remeberpw = true;
	private ImageView rmbpw = null;
	private Button login_login = null;
	private Button menu_forget = null;
	private Button menu_regist = null;
	private Button menu_more = null;
	private EditText login_telephone = null;
	private EditText login_pwd = null;

	private String mobile = null;
	private String upswd = null;

	private SharedPreferences sharedPreferences = null;;
	private SharedPreferences.Editor editor = null;

	private ResultStateReceiver resultStateReceiver;
	private IntentFilter filter;

	private TextView version = null;

	private ImageView imgPwdDelete = null;
	private ImageView imgPhoneDelete = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		version = (TextView) findViewById(R.id.version);
		try {
			version.setText(getVersionName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		imgPwdDelete = (ImageView) findViewById(R.id.pwd_delete);
		imgPwdDelete.setOnClickListener(this);
		imgPhoneDelete = (ImageView) findViewById(R.id.phone_delete);
		imgPhoneDelete.setOnClickListener(this);

		rmbpw = (ImageView) findViewById(R.id.login_remberpwd);
		rmbpw.setOnClickListener(this);

		login_login = (Button) findViewById(R.id.login_login);
		login_login.setOnClickListener(this);
		menu_forget = (Button) findViewById(R.id.menu_forget);
		menu_forget.setOnClickListener(this);
		menu_regist = (Button) findViewById(R.id.menu_regist);
		menu_regist.setOnClickListener(this);
		menu_more = (Button) findViewById(R.id.menu_more);
		menu_more.setOnClickListener(this);

		login_telephone = (EditText) findViewById(R.id.login_telephone);
		login_pwd = (EditText) findViewById(R.id.login_pwd);
		login_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgPwdDelete.setVisibility(View.VISIBLE);
				imgPhoneDelete.setVisibility(View.GONE);
			}
		});
		login_telephone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgPwdDelete.setVisibility(View.GONE);
				imgPhoneDelete.setVisibility(View.VISIBLE);
			}
		});
		init();
		x = new Handler() {
			public void handleMessage(Message msg) {
				stopLoading();
				if (msg.what == 1) {
					String str = (String) msg.obj;
					if (str.equals("1")) {
						if (!remeberpw) {
							login_pwd.setText("");
						}
						try {
							UIApplication.LOGIN = true;
							getSharedPreferences("365af", MODE_WORLD_READABLE).edit().putBoolean("LOGIN", true).commit();
							Intent main = new Intent();
							main.setClass(LoginActivity.this,
									MainActivity.class);
							startActivity(main);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {

						Toast.makeText(LoginActivity.this, str,
								Toast.LENGTH_SHORT).show();
					}
				} else if (msg.what == 2) {
					Toast.makeText(LoginActivity.this,
							getText(R.string.toast_net_error),
							Toast.LENGTH_SHORT).show();
				}
				super.handleMessage(msg);
			}

		};

		filter = new IntentFilter();
		filter.addAction(this.getClass().getName());
		resultStateReceiver = new ResultStateReceiver();

		registerReceiver(resultStateReceiver, filter);
		/*
		String notify = "";

		if (UIApplication.CameraList.size() == 0)
			notify = String.format(getText(R.string.ntfAppRunning).toString(),
					getText(R.string.app_name).toString());
		else
			notify = String.format(getText(R.string.ntfCameraRunning)
					.toString(), UIApplication.CameraList.size());

		startOnGoingNotification(notify);
		*/
	}
	
	private void init() {
		if (sharedPreferences == null) {
			sharedPreferences = this.getSharedPreferences("365af",
					MODE_WORLD_READABLE);
			editor = sharedPreferences.edit();
		}
		mobile = sharedPreferences.getString("mobile", null);
		upswd = sharedPreferences.getString("upswd", null);
		if (mobile != null && upswd != null) {
			login_telephone.setText(mobile);
			login_pwd.setText(upswd);
		}

	}

	private void startOnGoingNotification(String Text) {

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			Intent intent = null;
			if (UIApplication.LOGIN) {
				intent = new Intent(this, MainActivity.class);
			} else {
				intent = new Intent(this, LoginActivity.class);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = new Notification(R.drawable.app_icon,
					String.format(getText(R.string.ntfAppRunning).toString(),
							getText(R.string.app_name).toString()), 0);
			notification.setLatestEventInfo(this, getText(R.string.app_name),
					Text, pendingIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			manager.notify(0, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_remberpwd:
			if (remeberpw) {
				rmbpw.setImageResource(R.drawable.radio_login);
				remeberpw = false;
			} else {
				rmbpw.setImageResource(R.drawable.radio_login_focus);
				remeberpw = true;
			}
			break;

		case R.id.login_login:
			mobile = login_telephone.getText().toString().trim();
			upswd = login_pwd.getText().toString().trim();
			if (mobile != null && upswd != null && !mobile.equals("")
					&& !upswd.equals("")) {
				if (remeberpw) {
					try {
						if (sharedPreferences == null) {
							sharedPreferences = this.getSharedPreferences(
									"365af", MODE_PRIVATE);
							editor = sharedPreferences.edit();
						}
						editor.putString("mobile", mobile);
						editor.putString("upswd", upswd);
						editor.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (sharedPreferences == null) {
						sharedPreferences = this.getSharedPreferences("365af",
								MODE_PRIVATE);
						editor = sharedPreferences.edit();
					}
					editor.remove("upswd").commit();
				}
			} else if (mobile == null || mobile.equals("")) {
				Toast.makeText(LoginActivity.this,
						getText(R.string.login_cativity_input_phone),
						Toast.LENGTH_SHORT).show();
				return;
			} else if (upswd == null || upswd.equals("")) {
				Toast.makeText(LoginActivity.this,
						getText(R.string.login_cativity_input_password),
						Toast.LENGTH_SHORT).show();
				return;
			}
			loading(LoginActivity.this); // 开始加载
			new Thread(new DataThread()).start();
			break;
		case R.id.menu_forget:
			Intent forget = new Intent();
			forget.setClass(this, ForgotPasswordActivity.class);
			startActivity(forget);
			break;
		case R.id.menu_regist:
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_more:
			Intent more = new Intent();
			more.setClass(this, MoreActivity.class);
			startActivity(more);
			break;
		case R.id.pwd_delete:
			login_pwd.setText("");
			break;
		case R.id.phone_delete:
			login_telephone.setText("");
			break;
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);
			builder.setMessage(LoginActivity.this.getText(R.string.dialog_Exit));

			builder.setPositiveButton(
					LoginActivity.this.getText(R.string.btnExit),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							exitThread();
							UIApplication.app.AppExit();
							System.exit(0);
						}

					});
			builder.setNeutralButton(
					LoginActivity.this.getText(R.string.btnRunInBackground),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							LoginActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									moveTaskToBack(true);
								}
							});
							if (UIApplication.CameraList.size() > 0) {
								LoginActivity.this.moveTaskToBack(true);
							}
						}
					});
			builder.setNegativeButton(
					LoginActivity.this.getText(R.string.btnCancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler exitHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIApplication.app.AppExit();
			}
		}
	};

	/**
	 * 退出线程 jiangbing 2014-3-2
	 */
	private void exitThread() {
		// exitloading(this);

		Thread exit = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					quit();
					Message msg = new Message();
					msg.what = 1;
					exitHandle.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		exit.start();
	}

	private void quit() {
		unregisterReceiver(resultStateReceiver);
		//stopOnGoingNotification();
		for (MyCamera camera : UIApplication.CameraList) {
			camera.disconnect();
		}
		// for (MyCamera camera : UIApplication.dCameraList) {
		// camera.disconnect();
		// }
		MyCamera.uninit();
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(resultStateReceiver);
		stopLoading();
	}

	private void stopOnGoingNotification() {

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(0);
		manager.cancel(1);
	}

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = new Message();
			try {
				data = new GetServerData();
				message = data.login(mobile, upswd);
				if (message != null) {
					msg.what = 1;
					msg.obj = message;
				}

				x.sendMessage(msg);
			} catch (Exception e) {
				msg.what = 2;
				x.sendMessage(msg);
				e.printStackTrace();
			}
		}

	}

	/**
	 * 开始启动加载对话框
	 */
	public void loading(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle(getText(R.string.login_cativity_login));
		loadingDialog.setMessage(getText(R.string.dialog_wait));
		loadingDialog.show();
	}

	/**
	 * 结束加载对话框
	 */
	public void stopLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}

	private class ResultStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			IOTC_GCM_IntentService.runIntentInService(context,intent);
			String result = intent.getStringExtra("dev_uid");
			if (result == null) {

			} else {
				for (int i = 0; i < UIApplication.DeviceList.size(); i++) {
					if (UIApplication.DeviceList.get(i).UID.equals(intent
							.getStringExtra("dev_uid")) == true) {
						UIApplication.DeviceList.get(i).n_gcm_count++;
					}
				}
			}
		}
	}

	private String getVersionName() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return "V" + version;
	}
}
