/**
 * AddCamActivity.java
 */
package freelancer.worldvideo.addipc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import neutral.safe.chinese.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.MainActivity;
import freelancer.worldvideo.SearchListActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.net.AppConfig;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.net.HttpGetTool;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
import freelancer.worldvideo.wifi.WifiListActivity;
import freelancer.worldvideo.zxing.MipcaActivityCapture;

/**
 * @function: 添加设备
 * @author jiangbing
 * @data: 2014-2-18
 */
public class AddCam2Activity extends BaseActivity {
	private TitleView mTitle;

	private EditText addcam_name = null;
	private EditText addcam_uid = null;
	private EditText addcam_password = null;
	private Button btn_qr = null;
	private Button btn_save = null;
	private Button btn_check_local_device = null;

	private Handler mHandler = null;
	private ProgressDialog dialog = null;

	private String UID = null;

	private boolean isExsitsIPC = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addcam2);
		isExsitsIPC = getIntent().getBooleanExtra("isExsitsIPC", false);
		mHandler = new Handler();
		dialog = new ProgressDialog(this);
		mTitle = (TitleView) findViewById(R.id.addcam2_title);
		addcam_name = (EditText) findViewById(R.id.addcam_name);
		addcam_name.setText("Camera");
		addcam_uid = (EditText) findViewById(R.id.addcam_uid);

		String uid = this.getIntent().getStringExtra("qr_result");
		if (uid != null)
			addcam_uid.setText(uid);
		String uid1 = this.getIntent().getStringExtra("serach_resule");
		if (uid1 != null)
			addcam_uid.setText(uid1);
		addcam_password = (EditText) findViewById(R.id.addcam_password);
		btn_qr = (Button) findViewById(R.id.btn_qr);

		btn_qr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AddCam2Activity.this,
						MipcaActivityCapture.class);
				startActivity(intent);
			}
		});

		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String dev_nickname = addcam_name.getText().toString();
				String dev_uid = addcam_uid.getText().toString().trim();
				String view_pwd = addcam_password.getText().toString().trim();

				if (dev_nickname.length() == 0) {
					Toast.makeText(AddCam2Activity.this,
							getText(R.string.addcam_input_camname),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (dev_uid.length() != 20) {
					Toast.makeText(AddCam2Activity.this,
							getText(R.string.addcam_info_camidhint),
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (view_pwd.length() == 0) {
					Toast.makeText(AddCam2Activity.this,
							getText(R.string.addcam_input_pwd),
							Toast.LENGTH_SHORT).show();
					return;
				}
				int channel = 0;

				boolean duplicated = false;
				for (MyCamera camera : UIApplication.CameraList) {

					if (dev_uid.equalsIgnoreCase(camera.getUID())) {
						duplicated = true;
						break;
					}
				}

				if (duplicated) {
					Toast.makeText(AddCam2Activity.this,
							getText(R.string.addcam_activty_dev_exsist),
							Toast.LENGTH_SHORT).show();
					return;
				}

				HttpGetTool HttpGetTool = new HttpGetTool(AddCam2Activity.this,
						AddCam2Activity.class.getName());
				String st = freelancer.worldvideo.database.DatabaseManager.GCM_Server_URL
						+ "/register_freelancer?uid="
						+ dev_uid
						+ "&token=&regid="
						+ DatabaseManager.GCM_token
						+ "&clientid=" + DatabaseManager.Package_name;

				MyTool.println("add camera " + st);

				HttpGetTool.execute(st);
				
				HttpGetTool HttpGet = new HttpGetTool(AddCam2Activity.this,
						AddCam2Activity.class.getName());
				try {
					HttpGet.execute(AppConfig.SERVER_ADDRESS
							+ "?motion=addCamera&mobile=" + UIApplication.mobile + "&upswd=" + UIApplication.upswd
							+ "&uid=" + dev_uid+"&name="+URLEncoder.encode(dev_nickname, "utf-8")+"&pswd="+URLEncoder.encode(view_pwd, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/**
				 * 注册是否成功
				 */
				int reg = 0;
				if (HttpGetTool.result != null
						&& HttpGetTool.result.contains("success")) {
					reg = 1;
				}
				/* add value to data base */
				DatabaseManager manager = new DatabaseManager(
						AddCam2Activity.this);
				long db_id = manager.addDevice(dev_nickname, dev_uid, "", "",
						"admin", view_pwd, 3, channel);
				MyCamera camera = new MyCamera(dev_nickname, dev_uid, "admin",
						view_pwd, reg);
				DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(),
						dev_nickname, dev_uid, "admin", view_pwd, "", 3,
						channel, null);
				dev.ShowTipsForFormatSDCard = false;
				UIApplication.DeviceList.add(dev);
				UIApplication.CameraList.add(camera);

				Toast.makeText(AddCam2Activity.this,
						getText(R.string.addcam_activty_add_success),
						Toast.LENGTH_SHORT).show();
				
				if (isExsitsIPC) {
					new AlertDialog.Builder(AddCam2Activity.this)
							.setTitle(
									getText(R.string.listfragment_delete_alert))
							.setMessage(R.string.addcam_set_wifi)
							.setPositiveButton(
									getText(R.string.listfragment_submit),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											if (UID != null
													&& UID.length() == 20) {
												Intent intent = new Intent();
												intent.setClass(
														getApplicationContext(),
														WifiListActivity.class);
												startActivity(intent);
												finish();
											}
										}
									})
							.setNegativeButton(
									getText(R.string.listfragment_cancel),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											WifiManager mWifiManager = (WifiManager) getApplicationContext()
													.getSystemService(
															Context.WIFI_SERVICE);
											mWifiManager.setWifiEnabled(false);
											mWifiManager.setWifiEnabled(true);
											Intent intent = new Intent();
											intent.setClass(
													AddCam2Activity.this,
													MainActivity.class);
											startActivity(intent);
											finish();
										}
									}).show();

				} else {
					Intent intent = new Intent();
					intent.setClass(AddCam2Activity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}

			}
		});
		mTitle.setTitle(getText(R.string.addcam_activty_add_dev).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}

		});
		mTitle.setRightButtonBg(R.drawable.icon_search,
				new OnRightButtonClickListener() {

					@Override
					public void onClick(View button) {
						Intent intent = new Intent();
						intent.setClass(AddCam2Activity.this,
								SearchListActivity.class);
						startActivity(intent);
					}
				});

		btn_check_local_device = (Button) findViewById(R.id.btn_check_local_device);
		btn_check_local_device.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UID != null && UID.length() == 20) {
					if (addcam_uid != null)
						addcam_uid.setText(UID);
				} else {
					getIpcamUID();
				}
			}
		});
		if (isExsitsIPC) {
			btn_check_local_device.setVisibility(View.VISIBLE);
			getIpcamUID();
		} else {
			btn_check_local_device.setVisibility(View.GONE);
		}
	}

	private void getIpcamUID() {
		if (dialog != null && !dialog.isShowing()) {
			dialog.setMessage(getText(R.string.addcam_get_ipc_info));
			dialog.show();
		}
		if (btn_check_local_device != null)
			btn_check_local_device
					.setText(getText(R.string.addcam_get_ipc_info));
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					GetServerData server = new GetServerData();
					Thread.sleep(5 * 1000);
					UID = server.getIpcamUID();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (dialog != null && dialog.isShowing()) {
								dialog.dismiss();
							}
							if (UID == null) {
								if (btn_check_local_device != null) {
									btn_check_local_device
											.setText(getText(R.string.addcam_get_ipc_info_fail));
								}
							} else {
								try {

									if (btn_check_local_device != null) {
										btn_check_local_device.setText("UID:"
												+ UID);
										btn_check_local_device
												.setVisibility(View.GONE);
									}
									if (addcam_uid != null) {
										addcam_uid.setText(UID);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							Thread.currentThread().interrupt();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
