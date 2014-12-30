/**
 * PlaybackActivity.java
 */
package freelancer.worldvideo.videorecord;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.STimeDay;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

/**
 * @function: 远程录像回放界面
 * @author jiangbing
 * @data: 2014-3-5
 */
public class PlaybackActivity extends BaseActivity implements
		IRegisterIOTCListener {
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;

	private ProgressDialog dialog = null;
	private TitleView mTitle;

	private Button btn_play = null;
	private Button btn_pause = null;
	private Button btn_stop = null;

	private Monitor monitor = null;
	private MyCamera mCamera = null;

	private String mDevUUID;
	private String mDevNickname;
	private String mViewAcc;
	private String mViewPwd;
	private String mEvtUUID;

	private int mCameraChannel;
	private int mEvtType;
	// private long mEvtTime;
	private AVIOCTRLDEFs.STimeDay mEvtTime2;

	private int mVideoWidth;
	private int mVideoHeight;

	private final int MEDIA_STATE_STOPPED = 0;
	private final int MEDIA_STATE_PLAYING = 1;
	private final int MEDIA_STATE_PAUSED = 2;
	private final int MEDIA_STATE_OPENING = 3;

	private int mPlaybackChannel = -1;
	private int mMediaState = MEDIA_STATE_STOPPED;

	private PopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	private ImageView recordDelete = null;
	private ImageView iv_group_list_bg_divider = null;

	private List<EventInfo> searchData = null;
	private int currentPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_playback);
		searchData = new ArrayList<EventInfo>();
		
		btn_play = (Button) findViewById(R.id.playback_play);
		btn_pause = (Button) findViewById(R.id.playback_pasue);
		btn_stop = (Button) findViewById(R.id.playback_stop);
		/**
		 * 播放
		 */
		btn_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPlaybackChannel >= 0 && mCamera != null) {
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
									mCameraChannel,
									AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE, 0,
									mEvtTime2.toByteArray()));

					if (mMediaState == MEDIA_STATE_PLAYING) {
						btn_play.setBackgroundResource(R.drawable.btn_play);
						Toast.makeText(PlaybackActivity.this, "Pause",
								Toast.LENGTH_SHORT).show();
					} else {
						btn_play.setBackgroundResource(R.drawable.icon_pause);
						Toast.makeText(PlaybackActivity.this, "Play",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		/**
		 * 上一个
		 */
		btn_pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (--currentPosition >= 0
						&& currentPosition < searchData.size()) {
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
									mCameraChannel,
									AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP, 0,
									mEvtTime2.toByteArray()));
					mEvtTime2 = searchData.get(currentPosition).EventTime;
					mCamera.unregisterIOTCListener(PlaybackActivity.this);
					mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
					mCamera.disconnect();
					mCamera.registerIOTCListener(PlaybackActivity.this);
					mCamera.connect(mCamera.getUID());
					mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin",
							mCamera.getPassword());
					mPlaybackChannel = Camera.DEFAULT_AV_CHANNEL;
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord
									.parseContent(
											mCameraChannel,
											AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,
											0, mEvtTime2.toByteArray()));
					mMediaState = MEDIA_STATE_OPENING;
					if (dialog == null)
						dialog = new ProgressDialog(PlaybackActivity.this);
					// dialog.setTitle(getText(R.string.dialog_loading));
					dialog.setMessage(getText(R.string.dialog_wait));
					dialog.show();
					/* if server no response, close playback function */
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (mPlaybackChannel < 0
									&& mMediaState == MEDIA_STATE_OPENING) {
								mMediaState = MEDIA_STATE_STOPPED;
								Toast.makeText(
										PlaybackActivity.this,
										getText(R.string.tips_play_record_timeout),
										Toast.LENGTH_SHORT).show();
							}
						}
					}, 3000);
				} else {
					currentPosition = 0;
					Toast.makeText(PlaybackActivity.this, "第一个",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		/**
		 * 下一个
		 */
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (++currentPosition < searchData.size()
						&& currentPosition >= 0) {
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
									mCameraChannel,
									AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP, 0,
									mEvtTime2.toByteArray()));
					mEvtTime2 = searchData.get(currentPosition).EventTime;
					mCamera.unregisterIOTCListener(PlaybackActivity.this);
					mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
					mCamera.disconnect();
					mCamera.registerIOTCListener(PlaybackActivity.this);
					mCamera.connect(mCamera.getUID());
					mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin",
							mCamera.getPassword());
					mPlaybackChannel = Camera.DEFAULT_AV_CHANNEL;
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord
									.parseContent(
											mCameraChannel,
											AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,
											0, mEvtTime2.toByteArray()));
					mMediaState = MEDIA_STATE_OPENING;
					if (dialog == null)
						dialog = new ProgressDialog(PlaybackActivity.this);
					// dialog.setTitle(getText(R.string.dialog_loading));
					dialog.setMessage(getText(R.string.dialog_wait));
					dialog.show();
					/* if server no response, close playback function */
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (mPlaybackChannel < 0
									&& mMediaState == MEDIA_STATE_OPENING) {
								mMediaState = MEDIA_STATE_STOPPED;
								Toast.makeText(
										PlaybackActivity.this,
										getText(R.string.tips_play_record_timeout),
										Toast.LENGTH_SHORT).show();
							}
						}
					}, 3000);
				} else {
					currentPosition = searchData.size()-1;
					Toast.makeText(PlaybackActivity.this, "最后一个",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mTitle = (TitleView) findViewById(R.id.playback_title);
		mTitle.setTitle(getString(R.string.playback_activty_title));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				quit();
				PlaybackActivity.this.finish();
			}

		});
		mTitle.setRightButtonBg(R.drawable.icon_localplayback,
				new OnRightButtonClickListener() {

					@Override
					public void onClick(View button) {
						showWindow(button);
					}
				});
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null && bundle.getString("flag").equals("Event"))
		{
			searchData.addAll(EventRecordActivity.list);
		}
		else
		{
			searchData.addAll(CamRecordActivity.list);
		}
		currentPosition = bundle != null ? bundle.getInt("pos") : 0;
		mDevUUID = bundle != null ? bundle.getString("dev_uuid") : "";
		mDevNickname = bundle != null ? bundle.getString("dev_nickname") : "";
		mCameraChannel = bundle != null ? bundle.getInt("camera_channel") : -1;
		mViewAcc = bundle != null ? bundle.getString("view_acc") : "";
		mViewPwd = bundle != null ? bundle.getString("view_pwd") : "";
		mEvtType = bundle != null ? bundle.getInt("event_type") : -1;
		// mEvtTime = bundle != null ? bundle.getLong("event_time") : -1;
		mEvtUUID = bundle != null ? bundle.getString("event_uuid") : null;
		mEvtTime2 = bundle != null ? new STimeDay(bundle.getByteArray("event_time2")) : null;

		for (MyCamera camera : UIApplication.CameraList) {

			if (mDevUUID.equalsIgnoreCase(camera.getUUID())) {
				mCamera = camera;
				mCamera.registerIOTCListener(this);
				mCamera.resetEventCount();
				break;
			}
		}
		mPlaybackChannel = mCamera.DEFAULT_AV_CHANNEL;
		monitor = (Monitor) findViewById(R.id.playback_monitor);
		monitor.setMaxZoom(3.0f);

		if (mPlaybackChannel >= 0) {
			monitor.attachCamera(mCamera, mPlaybackChannel);
		}

		if (mCamera != null) {
			mTitle.setTitle(mCamera.getName());
			mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
					AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
							mCameraChannel,
							AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START, 0,
							mEvtTime2.toByteArray()));
			mMediaState = MEDIA_STATE_OPENING;
			if (dialog == null)
				dialog = new ProgressDialog(this);
			// dialog.setTitle(getText(R.string.dialog_loading));
			dialog.setMessage(getText(R.string.dialog_wait));
			dialog.show();
			/* if server no response, close playback function */
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mPlaybackChannel < 0
							&& mMediaState == MEDIA_STATE_OPENING) {
						mMediaState = MEDIA_STATE_STOPPED;
						Toast.makeText(PlaybackActivity.this,
								getText(R.string.tips_play_record_timeout),
								Toast.LENGTH_SHORT).show();
					}
				}
			}, 3000);
		}
		/*
		 * mMediaState = MEDIA_STATE_PLAYING; if (mPlaybackChannel >= 0 &&
		 * mCamera != null) { mCamera.start(mPlaybackChannel, mViewAcc,
		 * mViewPwd); mCamera.startShow(mPlaybackChannel);
		 * mCamera.startListening(mPlaybackChannel);
		 * monitor.attachCamera(mCamera, mPlaybackChannel); }
		 */
	}

	protected void onPause() {
		super.onPause();

		if (mCamera != null)
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						if(mCamera != null)
						{
							mCamera.stopListening(mPlaybackChannel);
							mCamera.stopShow(mPlaybackChannel);
							mCamera.stop(mPlaybackChannel);
						}
						if(monitor != null)
							monitor.deattachCamera();
					} catch (Exception e) {
						e.printStackTrace();
					}
					finally
					{
						Thread.currentThread().interrupt();
					}
				}
			}).start();
			
			
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			quit();
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void quit() {

		if (mCamera != null) {

			if (mPlaybackChannel >= 0) {
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
						AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
								mCameraChannel,
								AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP, 0,
								mEvtTime2.toByteArray()));
			}
			mMediaState = MEDIA_STATE_STOPPED;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameData(com.tutk.IOTC.Camera
	 * , int, android.graphics.Bitmap)
	 */
	@Override
	public void receiveFrameData(Camera camera, int sessionChannel, Bitmap bmp) {
		// TODO Auto-generated method stub
		if (mCamera == camera && sessionChannel == mPlaybackChannel
				&& bmp != null) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			mVideoWidth = bmp.getWidth();
			mVideoHeight = bmp.getHeight();
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
	public void receiveFrameInfo(Camera camera, int sessionChannel,
			long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub
		if (mCamera == camera && sessionChannel == mPlaybackChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);
			bundle.putInt("videoFPS", frameRate);
			bundle.putLong("videoBPS", bitRate);
			bundle.putInt("frameCount", frameCount);
			bundle.putInt("inCompleteFrameCount", incompleteFrameCount);

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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveIOCtrlData(com.tutk.IOTC.Camera
	 * , int, int, byte[])
	 */
	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub
		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);
			bundle.putByteArray("data", data);

			Message msg = new Message();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");

			if (msg.what == STS_CHANGE_CHANNEL_STREAMINFO) {

				int videoFPS = bundle.getInt("videoFPS");
				long videoBPS = bundle.getLong("videoBPS");
				int frameCount = bundle.getInt("frameCount");
				int inCompleteFrameCount = bundle
						.getInt("inCompleteFrameCount");

			} else if (msg.what == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP) {

				int command = Packet.byteArrayToInt_Little(data, 0);
				int result = Packet.byteArrayToInt_Little(data, 4);

				switch (command) {

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START:

					System.out.println("AVIOCTRL_RECORD_PLAY_START");

					if (mMediaState == MEDIA_STATE_OPENING) {
						if (0 <= result && result <= 31) {

							mPlaybackChannel = result;
							mMediaState = MEDIA_STATE_PLAYING;

							if (mCamera != null) {
								mCamera.start(mPlaybackChannel, mViewAcc,
										mViewPwd);
								mCamera.startShow(mPlaybackChannel);
								mCamera.startListening(mPlaybackChannel);
								monitor.attachCamera(mCamera, mPlaybackChannel);
							}

						} else {
							Toast.makeText(
									PlaybackActivity.this,
									PlaybackActivity.this
											.getText(R.string.tips_play_record_failed),
									Toast.LENGTH_SHORT).show();
						}
					}

					break;

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE:

					System.out.println("AVIOCTRL_RECORD_PLAY_PAUSE");

					if (mPlaybackChannel >= 0 && mCamera != null) {

						if (mMediaState == MEDIA_STATE_PAUSED)
							mMediaState = MEDIA_STATE_PLAYING;
						else if (mMediaState == MEDIA_STATE_PLAYING)
							mMediaState = MEDIA_STATE_PAUSED;

						if (mMediaState == MEDIA_STATE_PAUSED) {
							monitor.deattachCamera();
						} else {
							monitor.attachCamera(mCamera, mPlaybackChannel);
						}

					}

					break;

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP:

					System.out.println("AVIOCTRL_RECORD_PLAY_STOP");

					if (mPlaybackChannel >= 0 && mCamera != null) {
						mCamera.stopListening(mPlaybackChannel);
						mCamera.stopShow(mPlaybackChannel);
						mCamera.stop(mPlaybackChannel);
						monitor.deattachCamera();
					}

					mPlaybackChannel = -1;
					mMediaState = MEDIA_STATE_STOPPED;

					break;

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_END:

					System.out.println("AVIOCTRL_RECORD_PLAY_END");

					if (mPlaybackChannel >= 0 && mCamera != null) {
						mCamera.stopListening(mPlaybackChannel);
						mCamera.stopShow(mPlaybackChannel);
						mCamera.stop(mPlaybackChannel);
						monitor.deattachCamera();

						mCamera.sendIOCtrl(
								Camera.DEFAULT_AV_CHANNEL,
								AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
								AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord
										.parseContent(
												mCameraChannel,
												AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP,
												0, mEvtTime2.toByteArray()));
					}

					Toast.makeText(PlaybackActivity.this,
							getText(R.string.tips_play_record_end),
							Toast.LENGTH_LONG).show();

					mPlaybackChannel = -1;
					mMediaState = MEDIA_STATE_STOPPED;
					break;

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_BACKWARD:

					break;

				case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_FORWARD:

					break;
				}
			}

			super.handleMessage(msg);
		}
	};

	private void showWindow(View parent) {

		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.popuwindow_list, null);

			lv_group = (ListView) view.findViewById(R.id.lvGroup);

			recordDelete = (ImageView) view
					.findViewById(R.id.local_record_delete);
			iv_group_list_bg_divider = (ImageView) view
					.findViewById(R.id.iv_group_list_bg_divider);
			recordDelete.setVisibility(View.INVISIBLE);
			iv_group_list_bg_divider.setVisibility(View.INVISIBLE);

			MyGroupAdapter groupAdapter = new MyGroupAdapter(this);
			lv_group.setAdapter(groupAdapter);
			// 创建一个PopuWidow对象
			popupWindow = new PopupWindow(view, 400, 500);
		}

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(parent, -10, -20);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if (mCamera != null) {
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(
									mCameraChannel,
									AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP, 0,
									mEvtTime2.toByteArray()));
					currentPosition = position;
					mEvtTime2 = searchData.get(currentPosition).EventTime;
					mCamera.unregisterIOTCListener(PlaybackActivity.this);
					mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
					mCamera.disconnect();
					mCamera.registerIOTCListener(PlaybackActivity.this);
					mCamera.connect(mCamera.getUID());
					mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin",
							mCamera.getPassword());
					mPlaybackChannel = Camera.DEFAULT_AV_CHANNEL;
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
							AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord
									.parseContent(
											mCameraChannel,
											AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,
											0, mEvtTime2.toByteArray()));
					mMediaState = MEDIA_STATE_OPENING;
					if (dialog == null)
						dialog = new ProgressDialog(PlaybackActivity.this);
					// dialog.setTitle(getText(R.string.dialog_loading));
					dialog.setMessage(getText(R.string.dialog_wait));
					dialog.show();
					/* if server no response, close playback function */
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (mPlaybackChannel < 0
									&& mMediaState == MEDIA_STATE_OPENING) {
								mMediaState = MEDIA_STATE_STOPPED;
								Toast.makeText(
										PlaybackActivity.this,
										getText(R.string.tips_play_record_timeout),
										Toast.LENGTH_SHORT).show();
							}
						}
					}, 3000);
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});

	}

	private class MyGroupAdapter extends BaseAdapter {

		private Context context;

		public MyGroupAdapter(Context context) {

			this.context = context;
		}

		public int getCount() {
			return searchData.size();
		}

		public Object getItem(int position) {

			return searchData.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup viewGroup) {

			MyViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.popuwindow_adpeter, null);
				holder = new MyViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.groupItem);
				holder.groupItemId = (TextView) convertView
						.findViewById(R.id.groupItemId);

			} else {
				holder = (MyViewHolder) convertView.getTag();
			}
			holder.groupItem.setTextColor(Color.WHITE);

			holder.groupItem.setText(searchData.get(position).EventTime
					.getLocalTime());
			int tem = position + 1;
			if (tem < 10) {
				holder.groupItemId.setText("0" + tem);
			} else {
				holder.groupItemId.setText("" + tem);
			}

			return convertView;
		}
	}

	public class MyViewHolder {
		TextView groupItem;
		TextView groupItemId;
	}

}
