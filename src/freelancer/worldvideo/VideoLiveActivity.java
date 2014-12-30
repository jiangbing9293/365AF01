/**
 * VideoLiveActivity.java
 */
package freelancer.worldvideo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import neutral.safe.chinese.R;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.St_SInfo;

import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.util.MyMonitor;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

/**
 * @function: 直播界面
 * @author jiangbing
 * @data: 2014-2-21
 */
public class VideoLiveActivity extends BaseActivity implements
		ViewSwitcher.ViewFactory, IRegisterIOTCListener {
	private final String TAG = "VideoLiveActivity->";
	private ProgressDialog dialog = null;
	private TitleView mTitle;

	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	private static final int STS_SNAPSHOT_SCANED = 98;
	private MyMonitor monitor = null;
	private MyCamera mCamera = null;
	private DeviceInfo mDevice = null;

	private ProgressDialog loadingDialog;
	private String mDevUID;
	private String mDevUUID;
	private String mConnStatus = "";
	private int mOnlineNm;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSelectedChannel;

	private TextView videolive_quality = null;
	private TextView videolive_people = null;
	private TextView videolive_clearly = null;

	private boolean mIsListening = true;
	private boolean mIsSpeaking = false;

	private ImageView videolive_spalsh = null;
	private ImageView videolive_record = null;
	private ImageView videolive_speaking = null;
	private ImageView videolive_listening = null;
	private ImageView videolive_door = null;

	private ImageView videolive_img_record = null;

	private LinearLayout l1 = null;

	String demo = null;

	LinearLayout llayout_tool = null;
	private Button lvideolive_spalsh = null;
	private Button lvideolive_record = null;
	private Button lvideolive_speaking = null;
	private Button lvideolive_listening = null;

	private ImageView lvideo_live_img_record = null;

	private Button lvideolive_lock = null;

	private Button btn_left = null;
	private Button btn_right = null;

	private Bundle bundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		UIApplication.LIVE = true;
		bundle = this.getIntent().getBundleExtra("cam_info_extra");
		if (bundle != null) {
			mDevUID = bundle.getString("dev_uid");
			mDevUUID = bundle.getString("dev_uuid");
			mConnStatus = bundle.getString("conn_status");
			mSelectedChannel = bundle.getInt("camera_channel");
			demo = bundle.getString("flag");
		}
		for (MyCamera camera : UIApplication.CameraList) {
			if (mDevUID.equalsIgnoreCase(camera.getUID())
					&& mDevUUID.equalsIgnoreCase(camera.getUUID())) {
				mCamera = camera;
				break;
			}
		}

		for (DeviceInfo dev : UIApplication.DeviceList) {

			if (mDevUID.equalsIgnoreCase(dev.UID)
					&& mDevUUID.equalsIgnoreCase(dev.UUID)) {
				mDevice = dev;
				break;
			}
		}
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int rotation = display.getOrientation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			setupViewInPortraitLayout();
			if (monitor != null)
				monitor.setFixXY(false);
		} else {
			setupViewInLandscapeLayout();
			if (monitor != null)
				monitor.setFixXY(true);
		}
	}

	public void onStart() {
		super.onStart();
		MyTool.println(TAG + mCamera.isBusy);
		if (mCamera != null && !mCamera.isBusy) {
			mCamera.isBusy = true;
			mCamera.registerIOTCListener(this);

			if (mCamera.isSessionConnected()) {

				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
						SMsgAVIoctrlGetSupportStreamReq.parseContent());
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
								.parseContent());

			}
		}
	}

	public void onResume() {
		super.onResume();
		KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();

		if (flag) {
			MyTool.println("======= 休眠中 ====== ");
		} else {
			MyTool.println("======= 未休眠 ====== ");
			if (monitor != null) {
				monitor.setMaxZoom(3.0f);
				monitor.attachCamera(mCamera, mSelectedChannel);
			}

			if (mCamera != null) {
				mCamera.startShow(mSelectedChannel);
				if (mIsListening) {
					mCamera.startListening(mSelectedChannel);
				}
				if (mIsSpeaking) {
					mCamera.startSpeaking(mSelectedChannel);
				}
			}
		}

	}

	protected void onPause() {
		super.onPause();
		MyTool.println(TAG + "onPause ");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (monitor != null)
						monitor.deattachCamera();
					if (mCamera != null) {

						if (mIsSpeaking)
							mCamera.stopSpeaking(mSelectedChannel);
						if (mIsListening)
							mCamera.stopListening(mSelectedChannel);
						mCamera.stopShow(mSelectedChannel);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Thread.currentThread().interrupt();
				}
			}
		}).start();

	}

	protected void onDestroy() {
		super.onDestroy();
		UIApplication.LIVE = false;
		if (handler != null)
			handler.removeCallbacksAndMessages(null);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			exitThread();
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void setupViewInPortraitLayout() {

		setContentView(R.layout.activity_live_video_portrait);
		mTitle = (TitleView) findViewById(R.id.livevideo_title);

		videolive_quality = (TextView) findViewById(R.id.videolive_quality);
		videolive_people = (TextView) findViewById(R.id.videolive_people);
		videolive_clearly = (TextView) findViewById(R.id.videolive_clearly);
		videolive_spalsh = (ImageView) findViewById(R.id.videolive_spalsh);
		videolive_record = (ImageView) findViewById(R.id.videolive_record);
		videolive_speaking = (ImageView) findViewById(R.id.videolive_speaking);
		videolive_listening = (ImageView) findViewById(R.id.videolive_listenting);
		videolive_door = (ImageView) findViewById(R.id.videolive_door);
		videolive_img_record = (ImageView) findViewById(R.id.video_live_img_record);
		videolive_img_record.setVisibility(View.GONE);

		l1 = (LinearLayout) findViewById(R.id.video_live_l1);
		if (mDevice != null && mDevice.flag == 0) {
			l1.setBackgroundResource(R.drawable.l1_1);
			videolive_door.setEnabled(false);
			videolive_door.setImageResource(R.drawable.video_live_lock_unused);
		} else {
			l1.setBackgroundResource(R.drawable.l1);
			videolive_door.setEnabled(true);
			videolive_door.setImageResource(R.drawable.btn_lock_seletor);
		}

		videolive_door.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				byte pw[] = new byte[32];
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DOOR_REQ,
						MYAVIOCTRLDEFs.sMsgNetviomSetDoorReq
								.parseContent(1, pw));
			}
		});

		/**
		 * 录像
		 */
		videolive_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera == null || !mCamera.isSessionConnected()) {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.toast_connect_fail),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (monitor.getRecord()) {
					monitor.stopRecord();
					videolive_record
							.setImageResource(R.drawable.menu_videotape);
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.live_activity_record_stop),
							Toast.LENGTH_SHORT).show();
					videolive_img_record.setVisibility(View.GONE);
				} else {
					monitor.setUID(mDevUID);
					monitor.startRecord();
					videolive_record
							.setImageResource(R.drawable.menu_videotape_on);
					videolive_img_record.setVisibility(View.VISIBLE);
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.live_activity_recording),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		/**
		 * 抓拍操作
		 */
		videolive_spalsh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera == null || !mCamera.isSessionConnected()) {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.toast_connect_fail),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (mCamera != null
						&& mCamera.isChannelConnected(mSelectedChannel)) {

					if (isSDCardValid()) {

						File rootFolder = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Snapshot/");
						File targetFolder = new File(rootFolder
								.getAbsolutePath() + "/" + mDevUID);

						if (!rootFolder.exists()) {
							try {
								rootFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						if (!targetFolder.exists()) {

							try {
								targetFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						final String file = targetFolder.getAbsoluteFile()
								+ "/" + getFileNameWithTime();
						Bitmap frame = mCamera != null ? mCamera
								.Snapshot(mSelectedChannel) : null;

						if (frame != null && saveImage(file, frame)) {
							MediaScannerConnection.scanFile(
									VideoLiveActivity.this,
									new String[] { file.toString() },
									new String[] { "image/*" },
									new MediaScannerConnection.OnScanCompletedListener() {
										public void onScanCompleted(
												String path, Uri uri) {
											Log.i("ExternalStorage", "Scanned "
													+ path + ":");
											Log.i("ExternalStorage", "-> uri="
													+ uri);
											Message msg = handler
													.obtainMessage();
											msg.what = STS_SNAPSHOT_SCANED;
											handler.sendMessage(msg);
										}
									});

						} else {

							Toast.makeText(VideoLiveActivity.this,
									getText(R.string.live_activity_sp_fail),
									Toast.LENGTH_SHORT).show();
						}

					} else {

						Toast.makeText(VideoLiveActivity.this,
								getText(R.string.live_activity_sp_sd),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		/**
		 * 对讲
		 */

		videolive_speaking.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					speakBtnset();
					mIsListening = false;
					listenBtnset();
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					speakBtnset();
					mIsListening = true;
					listenBtnset();
				}
				return true;
			}
		});

		/*
		 * videolive_speaking.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { speakBtnset(); } });
		 */
		videolive_listening.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listenBtnset();
			}
		});

		mIsListening = !mIsListening;
		mIsSpeaking = !mIsSpeaking;
		speakBtnset();
		listenBtnset();
		mTitle.setTitle(getText(R.string.live_activity_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				exitThread();
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.video_live_icon_setup,
				new OnRightButtonClickListener() {

					@Override
					public void onClick(View button) {
						/*
						 * if (mCamera == null || !mCamera.isSessionConnected())
						 * { Toast.makeText(VideoLiveActivity.this,
						 * getText(R.string.toast_connect_fail),
						 * Toast.LENGTH_SHORT).show(); return; } if
						 * (monitor.getPT()) { monitor.closedPT();
						 * mTitle.setRightButtonBg(R.drawable.cloud_off);
						 * Toast.makeText(VideoLiveActivity.this,
						 * getText(R.string.live_activity_control_closed),
						 * Toast.LENGTH_SHORT).show(); } else {
						 * monitor.openedPT();
						 * mTitle.setRightButtonBg(R.drawable.cloud_on);
						 * Toast.makeText(VideoLiveActivity.this,
						 * getText(R.string.live_activity_control_open),
						 * Toast.LENGTH_SHORT).show(); }
						 */
						Intent view = new Intent();
						view.putExtra("cam_info_extra", bundle);
						view.setClass(VideoLiveActivity.this,
								ParameterSetActivity.class);
						startActivity(view);
					}
				});

		if (mConnStatus != null && mConnStatus.length() > 0) {
			Toast.makeText(this, mConnStatus, Toast.LENGTH_SHORT).show();
		}
		/*
		 * if (videolive_quality != null && mCamera != null) videolive_quality
		 * .setText(getPerformance((int) (((float) mCamera .getDispFrmPreSec() /
		 * (float) mCamera .getRecvFrmPreSec()) * 100)));
		 */
		videolive_clearly.setText("");
		if (dialog == null)
			dialog = new ProgressDialog(this);
		dialog.setMessage(getText(R.string.dialog_wait));
		dialog.show();

		if (monitor != null) {
			monitor.deattachCamera();
			monitor = null;
		}

		monitor = (MyMonitor) findViewById(R.id.monitor);
		// monitor.attachCamera(mCamera, mSelectedChannel);
		monitor.mEnableDither = true;
		monitor.openedPT();
		monitor.attachCamera(mCamera, mSelectedChannel);
		/*
		 * if (monitor.getPT()) { mTitle.setRightButtonBg(R.drawable.cloud_on);
		 * } else { mTitle.setRightButtonBg(R.drawable.cloud_off); }
		 */

		if (monitor.getRecord()) {
			videolive_record.setImageResource(R.drawable.menu_videotape_on);
		} else {
			videolive_record.setImageResource(R.drawable.menu_videotape);
		}

	}

	/**
	 * 横屏 jiangbing 2014-5-6
	 */
	private void setupViewInLandscapeLayout() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_live_video_land);
		llayout_tool = (LinearLayout) findViewById(R.id.llayout_tool);
		lvideolive_spalsh = (Button) findViewById(R.id.videolive_spalsh1);
		lvideolive_record = (Button) findViewById(R.id.videolive_record1);
		lvideolive_speaking = (Button) findViewById(R.id.videolive_speaking1);
		lvideolive_listening = (Button) findViewById(R.id.videolive_listening1);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		lvideolive_lock = (Button) findViewById(R.id.videolive_lock);

		lvideo_live_img_record = (ImageView) findViewById(R.id.lvideo_live_img_record);
		lvideo_live_img_record.setVisibility(View.GONE);
		if (mDevice != null && mDevice.flag == 0) {
			lvideolive_lock.setEnabled(false);
			lvideolive_lock
					.setBackgroundResource(R.drawable.video_live_lock_unused);
		} else {
			lvideolive_lock.setEnabled(true);
			lvideolive_lock.setBackgroundResource(R.drawable.btn_lock_seletor);
		}

		lvideolive_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				byte pw[] = new byte[32];
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DOOR_REQ,
						MYAVIOCTRLDEFs.sMsgNetviomSetDoorReq
								.parseContent(1, pw));
			}
		});
		btn_left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				llayout_tool.setVisibility(View.GONE);
				btn_right.setVisibility(View.VISIBLE);
			}
		});
		btn_right.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				llayout_tool.setVisibility(View.VISIBLE);
				btn_right.setVisibility(View.GONE);
			}
		});
		/**
		 * 录像
		 */
		lvideolive_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera == null || !mCamera.isSessionConnected()) {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.toast_connect_fail),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (monitor.getRecord()) {
					lvideo_live_img_record.setVisibility(View.GONE);
					monitor.stopRecord();
					lvideolive_record
							.setBackgroundResource(R.drawable.menu_videotape);
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.live_activity_record_stop),
							Toast.LENGTH_SHORT).show();
				} else {
					lvideo_live_img_record.setVisibility(View.VISIBLE);
					monitor.setUID(mDevUID);
					monitor.startRecord();
					lvideolive_record
							.setBackgroundResource(R.drawable.menu_videotape_on);
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.live_activity_recording),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		/**
		 * 抓拍操作
		 */
		lvideolive_spalsh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCamera == null || !mCamera.isSessionConnected()) {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.toast_connect_fail),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (mCamera != null
						&& mCamera.isChannelConnected(mSelectedChannel)) {

					if (isSDCardValid()) {

						File rootFolder = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Snapshot/");
						File targetFolder = new File(rootFolder
								.getAbsolutePath() + "/" + mDevUID);

						if (!rootFolder.exists()) {
							try {
								rootFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						if (!targetFolder.exists()) {

							try {
								targetFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						final String file = targetFolder.getAbsoluteFile()
								+ "/" + getFileNameWithTime();
						Bitmap frame = mCamera != null ? mCamera
								.Snapshot(mSelectedChannel) : null;

						if (frame != null && saveImage(file, frame)) {
							MediaScannerConnection.scanFile(
									VideoLiveActivity.this,
									new String[] { file.toString() },
									new String[] { "image/*" },
									new MediaScannerConnection.OnScanCompletedListener() {
										public void onScanCompleted(
												String path, Uri uri) {
											Log.i("ExternalStorage", "Scanned "
													+ path + ":");
											Log.i("ExternalStorage", "-> uri="
													+ uri);
											Message msg = handler
													.obtainMessage();
											msg.what = STS_SNAPSHOT_SCANED;
											handler.sendMessage(msg);
										}
									});

						} else {

							Toast.makeText(VideoLiveActivity.this,
									getText(R.string.live_activity_sp_fail),
									Toast.LENGTH_SHORT).show();
						}

					} else {

						Toast.makeText(VideoLiveActivity.this,
								getText(R.string.live_activity_sp_sd),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		/**
		 * 对讲
		 */

		lvideolive_speaking.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					lspeakBtnset();
					mIsListening = false;
					llistenBtnset();
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					lspeakBtnset();
					mIsListening = true;
					llistenBtnset();
				}
				return true;
			}
		});

		/*
		 * lvideolive_speaking.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { lspeakBtnset(); } });
		 */

		if (monitor != null) {
			monitor.deattachCamera();
			monitor = null;
		}

		mIsListening = !mIsListening;
		mIsSpeaking = !mIsSpeaking;
		lspeakBtnset();
		llistenBtnset();
		monitor = (MyMonitor) findViewById(R.id.monitor1);
		monitor.attachCamera(mCamera, mSelectedChannel);
		/**
		 * 监听操作
		 */
		lvideolive_listening.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				llistenBtnset();
			}
		});
	}

	/**
	 * 横屏对讲按钮设置 jiangbing 2014-2-28
	 */
	private void lspeakBtnset() {
		if (mCamera == null || !mCamera.isSessionConnected()) {
			Toast.makeText(this, getText(R.string.toast_connect_fail),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCamera == null)
			return;
		if (!mIsSpeaking) {
			lvideolive_speaking
					.setBackgroundResource(R.drawable.menu_intercom_on);
			mIsSpeaking = true;
			mCamera.startSpeaking(mSelectedChannel);
			return;
		} else {
			lvideolive_speaking.setBackgroundResource(R.drawable.menu_intercom);
			mCamera.stopSpeaking(mSelectedChannel);
			mIsSpeaking = false;
			return;
		}
	}

	/**
	 * 横屏监听按钮设置 jiangbing 2014-2-28
	 */
	private void llistenBtnset() {

		if (mCamera == null || !mCamera.isSessionConnected()) {
			Toast.makeText(this, getText(R.string.toast_connect_fail),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (mCamera == null)
			return;
		if (!mIsListening) {
			mIsListening = true;
			lvideolive_listening
					.setBackgroundResource(R.drawable.menu_monitor_on);
			mCamera.startListening(mSelectedChannel);
			return;
		} else {
			lvideolive_listening.setBackgroundResource(R.drawable.menu_monitor);
			mCamera.stopListening(mSelectedChannel);
			mIsListening = false;
			return;
		}
	}

	/**
	 * 对讲/按钮设置 jiangbing 2014-2-28
	 */
	private void speakBtnset() {
		if (mCamera == null || !mCamera.isSessionConnected()) {
			Toast.makeText(this, getText(R.string.toast_connect_fail),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCamera == null)
			return;
		if (!mIsSpeaking) {
			videolive_speaking.setImageResource(R.drawable.menu_intercom_on);
			mIsSpeaking = true;
			mCamera.startSpeaking(mSelectedChannel);
			return;
		} else {
			videolive_speaking.setImageResource(R.drawable.menu_intercom);
			mCamera.stopSpeaking(mSelectedChannel);
			mIsSpeaking = false;
			return;
		}
	}

	/**
	 * 监听/按钮设置 jiangbing 2014-2-28
	 */
	private void listenBtnset() {
		if (mCamera == null || !mCamera.isSessionConnected()) {
			Toast.makeText(this, getText(R.string.toast_connect_fail),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCamera == null)
			return;
		if (!mIsListening) {
			videolive_listening.setImageResource(R.drawable.menu_monitor_on);
			mIsListening = true;
			mCamera.startListening(mSelectedChannel);
			return;
		} else {
			videolive_listening.setImageResource(R.drawable.menu_monitor);
			mCamera.stopListening(mSelectedChannel);
			mIsListening = false;
			return;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setupViewInLandscapeLayout();

		} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setupViewInPortraitLayout();
		}
	}

	private void quit() {
		byte[] snapshot = DatabaseManager.getByteArrayFromBitmap(mCamera
				.Snapshot(mSelectedChannel));
		// if (monitor != null) {
		// monitor.deattachCamera();
		// }

		if (mCamera != null) {

			// MyTool.println(TAG+"quit 1");
			// mCamera.stop(mSelectedChannel);
			// MyTool.println(TAG+"quit 2");
			mCamera.unregisterIOTCListener(VideoLiveActivity.this);
			mCamera.isBusy = false;

		}
		for (DeviceInfo dev : UIApplication.DeviceList) {

			if (mDevUID.equalsIgnoreCase(dev.UID)
					&& mDevUUID.equalsIgnoreCase(dev.UUID)) {
				dev.Snapshot = DatabaseManager.getBitmapFromByteArray(snapshot);
				break;
			}
		}
		DatabaseManager manager = new DatabaseManager(this);

		manager.updateDeviceChannelByUID(mDevUID, mSelectedChannel);

		if (snapshot != null) {
			manager.updateDeviceSnapshotByUID(mDevUID, snapshot);

		}
	}

	/**
	 * 网络状况 jiangbing 2014-2-28
	 * 
	 * @param mode
	 * @return
	 */
	private String getPerformance(int mode) {

		String result = "";
		if (mode < 30)
			result = "Bad";
		else if (mode < 60)
			result = "Normal";
		else
			result = "Good";

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameData(com.tutk.IOTC.Camera
	 * , int, android.graphics.Bitmap)
	 */
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		if (mCamera == camera && avChannel == mSelectedChannel) {
			if (bmp.getWidth() != mVideoWidth
					|| bmp.getHeight() != mVideoHeight) {
				mVideoWidth = bmp.getWidth();
				mVideoHeight = bmp.getHeight();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameInfo(com.tutk.IOTC.Camera
	 * , int, long, int, int, int, int)
	 */
	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		if (mCamera == camera && avChannel == mSelectedChannel) {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			// mVideoFPS = frameRate;
			// mVideoBPS = bitRate;
			mOnlineNm = onlineNm;
			// mFrameCount = frameCount;
			// mIncompleteFrameCount = incompleteFrameCount;

			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);

			Message msg = handler.obtainMessage();
			msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveSessionInfo(com.tutk.IOTC.
	 * Camera, int)
	 */
	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveChannelInfo(com.tutk.IOTC.
	 * Camera, int, int)
	 */
	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub
		if (mCamera == camera && avChannel == mSelectedChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);

			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveIOCtrlData(com.tutk.IOTC.Camera
	 * , int, int, byte[])
	 */
	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			bundle.putByteArray("data", data);
			Message msg = handler.obtainMessage();
			msg.setData(bundle);
			msg.what = avIOCtrlMsgType;
			handler.sendMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ViewSwitcher.ViewFactory#makeView()
	 */
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();
			int avChannel = bundle.getInt("avChannel");
			byte[] data = bundle.getByteArray("data");

			St_SInfo stSInfo = new St_SInfo();
			IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);

			switch (msg.what) {

			case STS_CHANGE_CHANNEL_STREAMINFO:
				if (videolive_clearly != null)
					videolive_clearly.setText(String.valueOf(mVideoWidth) + "x"
							+ String.valueOf(mVideoHeight));
				if (videolive_quality != null)
					videolive_quality
							.setText(getPerformance((int) (((float) mCamera
									.getDispFrmPreSec() / (float) mCamera
									.getRecvFrmPreSec()) * 100)));
				if (videolive_people != null)
					videolive_people.setText(String.valueOf(mOnlineNm));
				break;

			case STS_SNAPSHOT_SCANED:
				Toast.makeText(VideoLiveActivity.this,
						getText(R.string.live_activity_sp_success),
						Toast.LENGTH_SHORT).show();
				break;

			case Camera.CONNECTION_STATE_CONNECTING:

				if (!mCamera.isSessionConnected()
						|| !mCamera.isChannelConnected(mSelectedChannel)) {

					mConnStatus = getText(R.string.connstus_connecting)
							.toString();
				}

				break;

			case Camera.CONNECTION_STATE_CONNECTED:

				if (mCamera.isSessionConnected()
						&& avChannel == mSelectedChannel
						&& mCamera.isChannelConnected(mSelectedChannel)) {

					mConnStatus = getText(R.string.connstus_connected)
							.toString();
				}

				break;

			case Camera.CONNECTION_STATE_DISCONNECTED:

				mConnStatus = getText(R.string.connstus_disconnect).toString();
				break;

			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
				mConnStatus = getText(R.string.connstus_unknown_device)
						.toString();

				break;

			case Camera.CONNECTION_STATE_TIMEOUT:
				if (mCamera != null) {
					mCamera.stopSpeaking(mSelectedChannel);
					mCamera.stopListening(mSelectedChannel);
					mCamera.stopShow(mSelectedChannel);
					mCamera.stop(mSelectedChannel);
					mCamera.disconnect();

					mCamera.connect(mDevUID);
					mCamera.start(Camera.DEFAULT_AV_CHANNEL,
							mDevice.View_Account, mDevice.View_Password);
					mCamera.startShow(mSelectedChannel);

					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
									.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
									.parseContent());
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
									.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());

					if (mIsListening)
						mCamera.startListening(mSelectedChannel);
				}

				break;

			case Camera.CONNECTION_STATE_CONNECT_FAILED:

				mConnStatus = getText(R.string.connstus_connection_failed)
						.toString();

				break;

			case Camera.CONNECTION_STATE_WRONG_PASSWORD:

				mConnStatus = getText(R.string.connstus_wrong_password)
						.toString();

				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:

				break;

			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_RESP:
				if (data != null && data[0] == 1) {
					videolive_door.setVisibility(View.VISIBLE);
				}

				break;

			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_DOOR_RESP:
				if (data != null && data[0] == 0) {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.video_live_activity_open_success),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(VideoLiveActivity.this,
							getText(R.string.video_live_activity_open_fail),
							Toast.LENGTH_SHORT).show();
				}

				break;

			}
			super.handleMessage(msg);
		}
	};

	private static String getFileNameWithTime() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);
		int mMilliSec = c.get(Calendar.MILLISECOND);

		StringBuffer sb = new StringBuffer();
		sb.append("IMG_");
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		sb.append(".jpg");

		return sb.toString();
	}

	/**
	 * SD卡 jiangbing 2014-2-28
	 * 
	 * @return
	 */
	private static boolean isSDCardValid() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private void addImageGallery(File file) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	private boolean saveImage(String fileName, Bitmap frame) {

		if (fileName == null || fileName.length() <= 0)
			return false;

		boolean bErr = false;
		FileOutputStream fos = null;

		try {

			fos = new FileOutputStream(fileName, false);
			frame.compress(Bitmap.CompressFormat.JPEG, 90, fos);
			fos.flush();
			fos.close();

		} catch (Exception e) {

			bErr = true;
			MyTool.println("saveImage(.): " + e.getMessage());

		} finally {
			if (bErr) {

				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		}

		addImageGallery(new File(fileName));
		return true;
	}

	public void loadingPlay(Context act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		if (loadingDialog != null && !loadingDialog.isShowing())
			loadingDialog.show();
	}

	public void stopLoading() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	/**
	 * 退出摄像机线程 jiangbing 2014-3-2
	 */
	private void exitThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					quit();

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Thread.currentThread().interrupt();
				}
			}
		}).start();
	}
}
