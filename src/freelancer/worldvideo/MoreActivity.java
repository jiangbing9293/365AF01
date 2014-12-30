/**
 * MoreActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.web.HelpWebActivity;
import freelancer.worldvideo.web.NormalQuestionWebActivity;
import freelancer.worldvideo.wifi.WifiAdmin;
import freelancer.worldvideo.wifi.WifiListActivity;

/**
 * @function: 更多
 * @author jiangbing
 * @data: 2014-3-12
 */
public class MoreActivity extends BaseActivity {
	private TitleView mTitle;

	private Button more_help = null;
	private Button more_question = null;
	private Button more_default_wifi = null;

	private ProgressDialog gress = null;
	private Handler mHandler = null;

	public static int netID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		initTitle();
		init();
		mHandler = new Handler();
	}

	private void init() {
		more_help = (Button) findViewById(R.id.more_help);
		more_question = (Button) findViewById(R.id.more_question);
		more_default_wifi = (Button) findViewById(R.id.more_default_wifi);
		more_help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent helpweb = new Intent();
				helpweb.setClass(MoreActivity.this, HelpWebActivity.class);
				startActivity(helpweb);
			}
		});
		more_question.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent web = new Intent();
				web.setClass(MoreActivity.this, NormalQuestionWebActivity.class);
				startActivity(web);
			}
		});
		more_default_wifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkWifi();
			}
		});
	}

	private void initTitle() {
		mTitle = (TitleView) findViewById(R.id.more_title);
		mTitle.setTitle(getText(R.string.morefragment_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.hiddenRightButton();
	}

	private void getIpcamUID() {
		if (gress == null) {
			gress = new ProgressDialog(this);
		}
		gress.setMessage("设备扫描中，请稍后...");
		gress.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					GetServerData server = new GetServerData();
					Thread.sleep(5*1000);
					final String str = server.getIpcamUID();
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (gress != null && gress.isShowing()) {
								gress.dismiss();
							}
							if (str == null) {
								Toast.makeText(MoreActivity.this, "未检测到设备...",
										Toast.LENGTH_SHORT).show();
							} else {
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										try {

											if (gress != null
													|| gress.isShowing())
												gress.dismiss();

											Intent intent = new Intent();
											intent.setClass(MoreActivity.this,
													WifiListActivity.class);
											startActivity(intent);
											finish();

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
								Thread.currentThread().interrupt();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void checkWifi() {
		if (gress == null)
			gress = new ProgressDialog(this);
		gress.setMessage("检测中...");
		gress.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				WifiAdmin wifiAdmin = new WifiAdmin(MoreActivity.this);
				wifiAdmin.openWifi();
				wifiAdmin.startScan();
				netID = wifiAdmin.changeWifi();
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						try {

							if (gress != null || gress.isShowing())
								gress.dismiss();
							if (netID != 0) {
								getIpcamUID();
							} else {
								Toast.makeText(MoreActivity.this, "未检测到可设置设备",
										Toast.LENGTH_SHORT).show();
							}
							Thread.currentThread().interrupt();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			}
		}).start();
	}
}
