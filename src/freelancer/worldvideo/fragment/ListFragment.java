/**
 * ListFragment.java
 */
package freelancer.worldvideo.fragment;

import java.io.File;
import java.net.URLEncoder;
import java.util.Map;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.LoginActivity;
import freelancer.worldvideo.MainActivity;
import freelancer.worldvideo.ParameterSetActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.VideoLiveActivity;
import freelancer.worldvideo.addipc.AddCam2Activity;
import freelancer.worldvideo.addipc.LanAPIPCListActivity;
import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.net.AppConfig;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.net.HttpGetTool;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MyCamera;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
import freelancer.worldvideo.wifi.WifiAdmin;

/**
 * @function: 列表界面
 * @author jiangbing
 * @data: 2014-2-15
 */
public class ListFragment extends Fragment implements IRegisterIOTCListener {
	
	private final String TAG = "ListFragment->";
	private ProgressDialog loadingDialog;
	private View mParent;
	private FragmentActivity mActivity;
	private TitleView mTitle;

	private DeviceInfo selectedDevice = null;
	private MyCamera selectedCamera = null;

	private ListView camlist = null;
	private View addCamView = null;
	private MyAdapter adapter;
	private PopupWindow popupWindow;
	private View view;
	private Button addcam = null;
	private Button btn_popu = null;

	private boolean isExsistIPC = false;

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static ListFragment newInstance(int index) {
		ListFragment f = new ListFragment();

		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		addCamView = inflater.inflate(R.layout.btn_addcam, container, false);
		addcam = (Button) addCamView.findViewById(R.id.btn_add);
		addcam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WifiAdmin wifi = new WifiAdmin(getActivity());
				wifi.startScan();
				isExsistIPC = wifi.isExsitsIPC();
				if (isExsistIPC) {
					Intent intent = new Intent();
					intent.setClass(mActivity, LanAPIPCListActivity.class);
					mActivity.startActivity(intent);
				} else {
					Intent addcam = new Intent();
					addcam.setClass(mActivity, AddCam2Activity.class);
					mActivity.startActivity(addcam);
				}
			}
		});

		camlist = (ListView) view.findViewById(R.id.lv_camlist);
		camlist.addFooterView(addCamView);
		adapter = new MyAdapter(getActivity());
		camlist.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		camlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				if (position < UIApplication.DeviceList.size()) {
					final MyCamera camera = UIApplication.CameraList
							.get(position);
					final DeviceInfo dev = UIApplication.DeviceList
							.get(position);
					if (dev.Status
							.equals(getString(R.string.connstus_wrong_password))) {
						final EditText inputServer = new EditText(getActivity());
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setTitle(
								getString(R.string.listfragment_input_password))
								.setIcon(android.R.drawable.ic_dialog_info)
								.setView(inputServer)
								.setNegativeButton("Cancel", null);
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										DeviceInfo dev = UIApplication.DeviceList
												.get(position);
										MyCamera cam = UIApplication.CameraList
												.get(position);
										String pwd = inputServer.getText()
												.toString();
										if (pwd == null) {
											return;
										}
										dev.View_Password = pwd;
										cam.setPassword(pwd);
										DatabaseManager manager = new DatabaseManager(
												getActivity());
										manager.updateDeviceInfoByDBID(
												dev.DBID, dev.UID,
												dev.NickName, "", "", "admin",
												dev.View_Password,
												dev.EventNotification,
												dev.ChannelIndex);
									}
								});
						builder.show();
					}
					if (!camera.isChannelConnected(0)) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									if(camera.isSessionConnected())
										camera.disconnect();
									else if(!camera.isChannelConnected(0))
									{
										camera.connect(camera.getUID());
										camera.start(dev.ChannelIndex, "admin",
												camera.getPassword());
										camera.sendIOCtrl(
												dev.ChannelIndex,
												MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_REQ,
												MYAVIOCTRLDEFs.sMsgNetviomGetDoorReq
														.parseContent(Camera.DEFAULT_AV_CHANNEL));
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									Thread.currentThread().interrupt();
								}
							}
						}).start();
						return;
					}
					Bundle extras = new Bundle();
					if (UIApplication.DeviceList.size() > position) {
						extras.putString("dev_uid",
								UIApplication.DeviceList.get(position).UID);
						extras.putString("dev_uuid",
								UIApplication.DeviceList.get(position).UUID);
						extras.putString("dev_nickname",
								UIApplication.DeviceList.get(position).NickName);
						extras.putString("conn_status",
								UIApplication.DeviceList.get(position).Status);
						extras.putString(
								"view_acc",
								UIApplication.DeviceList.get(position).View_Account);
						extras.putString(
								"view_pwd",
								UIApplication.DeviceList.get(position).View_Password);
						extras.putInt(
								"camera_channel",
								UIApplication.DeviceList.get(position).ChannelIndex);
					}
					Intent videolive = new Intent();
					videolive.putExtra("cam_info_extra", extras);
					videolive.setClass(mActivity, VideoLiveActivity.class);
					mActivity.startActivity(videolive);
				}
			}
		});
		camlist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selectedCamera = UIApplication.CameraList.get(position);
				selectedDevice = UIApplication.DeviceList.get(position);
				new AlertDialog.Builder(getActivity())
						.setTitle(getText(R.string.listfragment_delete_alert))
						.setMessage(R.string.listfragment_delete_msg)
						.setPositiveButton(
								getText(R.string.listfragment_submit),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										deleteThread();
									}
								})
						.setNegativeButton(
								getText(R.string.listfragment_cancel), null)
						.show();
				return false;
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();
		UIApplication.mobile = getActivity().getSharedPreferences("365af",
				getActivity().MODE_PRIVATE).getString("mobile", null);
		UIApplication.upswd = getActivity().getSharedPreferences("365af",
				getActivity().MODE_PRIVATE).getString("upswd", null);
		for (MyCamera camera : UIApplication.CameraList) {
			if (camera != null)
			{
				camera.registerIOTCListener(this);
				camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			}
		}
		if (UIApplication.DeviceList.size() == 0) {
			showWindow();
			popupHandler.sendEmptyMessageDelayed(0, 500);
			addcam.setVisibility(View.VISIBLE);
		} else {
			addcam.setVisibility(View.GONE);
		}

		mTitle = (TitleView) mParent.findViewById(R.id.title);
		mTitle.setTitle(getText(R.string.listfragment_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				UIApplication.LOGIN = false;
				getActivity()
						.getSharedPreferences("365af",
								getActivity().MODE_WORLD_READABLE).edit()
						.putBoolean("LOGIN", false).commit();
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				getActivity().startActivity(intent);
			}

		});
		mTitle.setRightButton("", new OnRightButtonClickListener() {

			@Override
			public void onClick(View button) {
				if (!isExsistIPC) {
					WifiAdmin wifi = new WifiAdmin(getActivity());
					wifi.startScan();
					isExsistIPC = wifi.isExsitsIPC();
				}

				if (isExsistIPC) {
					Intent intent = new Intent();
					intent.setClass(mActivity, LanAPIPCListActivity.class);
					mActivity.startActivity(intent);
				} else {
					Intent addcam = new Intent();
					addcam.setClass(mActivity, AddCam2Activity.class);
					mActivity.startActivity(addcam);
				}
			}
		});
		Intent registrationIntent = new Intent(
				"com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app",
				PendingIntent.getBroadcast(mActivity, 0, new Intent(), 0));
		// registrationIntent.putExtra("sender",
		// freelancer.worldvideo.database.DatabaseManager.s_GCM_sender);
		registrationIntent.putExtra("sender",
				freelancer.worldvideo.database.DatabaseManager.GCM_sender);
		mActivity.startService(registrationIntent);

		Bundle bundle = mActivity.getIntent().getExtras();
		if (bundle != null) {
			String GCMDevUID = bundle.getString("dev_uid");
			for (int i = 0; i < UIApplication.DeviceList.size(); i++) {
				if (UIApplication.DeviceList.get(i).UID.equals(GCMDevUID) == true) {
					UIApplication.DeviceList.get(i).n_gcm_count++;
					adapter.notifyDataSetChanged();
				}
			}
		}
		freelancer.worldvideo.database.DatabaseManager.n_mainActivity_Status = 1;
		getRemoteIpcThread();
	}

	/**
	 * 删除摄像机 jiangbing 2014-3-1
	 */
	private void deleteCam() {

		// stop & remove camera
		if (selectedCamera.isChannelConnected(0))
			selectedCamera.stop(Camera.DEFAULT_AV_CHANNEL);
		if (selectedCamera.isSessionConnected())
			selectedCamera.disconnect();
		selectedCamera.unregisterIOTCListener(this);

		HttpGetTool HttpGetTool = new HttpGetTool(getActivity(),
				ListFragment.class.getName());

		HttpGetTool HttpGet = new HttpGetTool(getActivity(),
				ListFragment.class.getName());

		HttpGet.execute(AppConfig.SERVER_ADDRESS + "?motion=delCamera&mobile="
				+ UIApplication.mobile + "&upswd=" + UIApplication.upswd
				+ "&uid=" + selectedCamera.getUID());

		HttpGetTool
				.execute(freelancer.worldvideo.database.DatabaseManager.GCM_Server_URL
						+ "/unregister_freelancer?uid="
						+ selectedCamera.getUID()
						+ "&token=&regid="
						+ DatabaseManager.GCM_token
						+ "&clientid="
						+ DatabaseManager.Package_name);

		// remove snapshot from database & storage
		DatabaseManager manager = new DatabaseManager(getActivity());
		SQLiteDatabase db = manager.getReadableDatabase();
		Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT, new String[] {
				"_id", "dev_uid", "file_path", "time" }, "dev_uid = '"
				+ selectedDevice.UID + "'", null, null, null, "_id");

		while (cursor.moveToNext()) {
			String file_path = cursor.getString(2);
			File file = new File(file_path);
			if (file.exists())
				file.delete();
		}
		cursor.close();
		db.close();
		manager.removeSnapshotByUID(selectedDevice.UID);
		manager.removeDeviceByUID(selectedDevice.UID);

		UIApplication.DeviceList.remove(selectedDevice);
		UIApplication.CameraList.remove(selectedCamera);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	public void onPause() {
		super.onPause();
	}

	public void onResume() {
		WifiAdmin wifi = new WifiAdmin(getActivity());
		wifi.startScan();
		isExsistIPC = wifi.isExsitsIPC();

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	public void onStart() {
		super.onStart();
		MyTool.println("ListFragment  onStart");
		reconnect();
		register();
	}

	public void onStop() {
		super.onStop();
		MyTool.println("ListFragment  onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (UIApplication.CameraList != null) {
			for (MyCamera camera : UIApplication.CameraList) {
				camera.unregisterIOTCListener(this);
			}
		}
	}

	private Handler popupHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				popupWindow.showAtLocation(mParent, Gravity.CENTER
						| Gravity.CENTER, 0, 0);
				popupWindow.update();
				break;
			}
		}

	};

	/**
	 * 弹出框
	 * 
	 * @param parent
	 */
	private void showWindow() {

		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.camlist_popuwindow, null);

			btn_popu = (Button) view.findViewById(R.id.btn_popubtn);
			popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}
		btn_popu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);

	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button viewBtn;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (UIApplication.DeviceList != null
					&& UIApplication.DeviceList.size() > 0)
				return UIApplication.DeviceList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (UIApplication.DeviceList != null
					&& UIApplication.DeviceList.size() > 0)
				return UIApplication.DeviceList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (UIApplication.DeviceList == null
					|| position >= UIApplication.DeviceList.size())
				return null;
			final DeviceInfo dev = UIApplication.DeviceList.get(position);
			final MyCamera cam = UIApplication.CameraList.get(position);
			if (dev == null || cam == null)
				return null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.adapter_camlist, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.camlist_img_default);
				holder.title = (TextView) convertView
						.findViewById(R.id.camlist_txt_name1);
				holder.info = (TextView) convertView
						.findViewById(R.id.camlist_txt_name2);
				holder.viewBtn = (Button) convertView
						.findViewById(R.id.camlist_btn_setupvideo);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (dev.Snapshot != null)
				holder.img.setImageBitmap(dev.Snapshot);
			holder.title.setText(dev.NickName);
			holder.info.setText(dev.Status);
			
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dev.Status
							.equals(getString(R.string.connstus_wrong_password))) {
						final EditText inputServer = new EditText(getActivity());
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setTitle(
								getString(R.string.listfragment_input_password))
								.setIcon(android.R.drawable.ic_dialog_info)
								.setView(inputServer)
								.setNegativeButton("Cancel", null);
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										DeviceInfo dev = UIApplication.DeviceList
												.get(position);
										MyCamera cam = UIApplication.CameraList
												.get(position);
										String pwd = inputServer.getText()
												.toString();

										if (pwd == null) {
											return;
										}
										dev.View_Password = pwd;
										cam.setPassword(pwd);
										DatabaseManager manager = new DatabaseManager(
												getActivity());
										manager.updateDeviceInfoByDBID(
												dev.DBID, dev.UID,
												dev.NickName, "", "", "admin",
												dev.View_Password,
												dev.EventNotification,
												dev.ChannelIndex);
									}
								});
						builder.show();
					}
					if (!UIApplication.CameraList.get(position)
							.isSessionConnected()) {
						Toast.makeText(getActivity(),
								getText(R.string.listfragment_connect_fail),
								Toast.LENGTH_SHORT).show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									if(cam.isSessionConnected())
										cam.disconnect();
									else if(!cam.isChannelConnected(0))
									{
										cam.connect(cam.getUID());
										cam.start(dev.ChannelIndex, "admin",
												cam.getPassword());
										cam.sendIOCtrl(
												dev.ChannelIndex,
												MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_REQ,
												MYAVIOCTRLDEFs.sMsgNetviomGetDoorReq
														.parseContent(Camera.DEFAULT_AV_CHANNEL));
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									Thread.currentThread().interrupt();
								}
							}
						}).start();
						return;
					}

					if (UIApplication.DeviceList != null
							&& position < UIApplication.DeviceList.size()) {
						Bundle extras = new Bundle();
						if (UIApplication.DeviceList.size() > position) {
							extras.putString("dev_uid",
									UIApplication.DeviceList.get(position).UID);
							extras.putString("dev_uuid",
									UIApplication.DeviceList.get(position).UUID);
							extras.putString(
									"dev_nickname",
									UIApplication.DeviceList.get(position).NickName);
							extras.putString(
									"conn_status",
									UIApplication.DeviceList.get(position).Status);
							extras.putString(
									"view_acc",
									UIApplication.DeviceList.get(position).View_Account);
							extras.putString(
									"view_pwd",
									UIApplication.DeviceList.get(position).View_Password);
							extras.putInt(
									"camera_channel",
									UIApplication.DeviceList.get(position).ChannelIndex);
						}

						Intent view = new Intent();
						view.putExtra("cam_info_extra", extras);
						view.setClass(mActivity, ParameterSetActivity.class);
						mActivity.startActivity(view);
					}
				}
			});
			return convertView;
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tutk.IOTC.IRegisterIOTCListener#receiveFrameInfo(com.tutk.IOTC.Camera
	 * , int, long, int, int, int, int)
	 */
	public void receiveSessionInfo(final Camera camera, int resultCode) {
		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());

		Message msg = handler.obtainMessage();
		msg.what = resultCode;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void receiveChannelInfo(final Camera camera, int sessionChannel,
			int resultCode) {
		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
		bundle.putInt("sessionChannel", sessionChannel);

		Message msg = handler.obtainMessage();
		msg.what = resultCode;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void receiveIOCtrlData(final Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
		if (avIOCtrlMsgType == MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_DOOR_RESP) {
			for (DeviceInfo dev : UIApplication.DeviceList) {
				if (((MyCamera) camera).getUID().equals(dev.UID)) {
					if (data != null) {
						dev.flag = data[0];
						if (camera != null) {
							if (camera.isSessionConnected()
									&& camera.isChannelConnected(0)) {
								if (dev != null) {
									dev.Status = UIApplication.app.getText(
											R.string.connstus_connected).toString();
									dev.Online = true;
								}
							}
						}
						break;
					}
				}
			}

		}
		

		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
		bundle.putInt("sessionChannel", sessionChannel);
		bundle.putByteArray("data", data);

		Message msg = handler.obtainMessage();
		msg.what = avIOCtrlMsgType;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();
			String requestDevice = bundle.getString("requestDevice");
			byte[] data = bundle.getByteArray("data");
			int i = 0;

			DeviceInfo device = null;
			MyCamera camera = null;
			if (UIApplication.DeviceList != null)
				for (i = 0; i < UIApplication.DeviceList.size(); i++) {

					if (UIApplication.DeviceList.get(i).UUID
							.equalsIgnoreCase(requestDevice)) {
						device = UIApplication.DeviceList.get(i);
						break;
					}
				}
			if (UIApplication.CameraList != null)
				for (i = 0; i < UIApplication.CameraList.size(); i++) {

					if (UIApplication.CameraList.get(i).getUUID()
							.equalsIgnoreCase(requestDevice)) {
						camera = UIApplication.CameraList.get(i);
						break;
					}
				}
			MyTool.println(TAG+"status:"+Integer.toHexString(msg.what));
			
			switch (msg.what) {
			
			case Camera.CONNECTION_STATE_CONNECTING:

				if (camera != null) {
					if (!camera.isSessionConnected()
							|| !camera.isChannelConnected(0)) {
						if (device != null) {
							try {
								device.Status = UIApplication.app.getText(
										R.string.connstus_connecting)
										.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}
							device.Online = false;
						}
					}
				}

				break;

			case Camera.CONNECTION_STATE_CONNECTED:

				if (camera != null) {
					if (camera.isSessionConnected()
							&& camera.isChannelConnected(0)) {
						if (device != null) {
							device.Status = UIApplication.app.getText(
									R.string.connstus_connected).toString();
							device.Online = true;
						}
					}
				}

				break;

			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:

				if (device != null) {
					device.Status = UIApplication.app.getText(
							R.string.connstus_unknown_device).toString();
					device.Online = false;
				}

				break;

			case Camera.CONNECTION_STATE_DISCONNECTED:

				if (device != null) {
					device.Status = UIApplication.app.getText(
							R.string.connstus_disconnect).toString();
					device.Online = false;
				}

				if (camera != null) {
					camera.disconnect();
				}

				break;

			case Camera.CONNECTION_STATE_TIMEOUT:

				if (device != null) {
					device.Status = UIApplication.app.getText(
							R.string.connstus_disconnect).toString();
					device.Online = false;
				}

				break;

			case Camera.CONNECTION_STATE_WRONG_PASSWORD:

				if (device != null) {
					device.Status = UIApplication.app.getText(
							R.string.connstus_wrong_password).toString();
					device.Online = false;
				}

				if (camera != null) {
					camera.disconnect();
				}

				break;

			case Camera.CONNECTION_STATE_CONNECT_FAILED:

				if (device != null) {
					device.Status = UIApplication.app.getText(
							R.string.connstus_connection_failed).toString();
					device.Online = false;
				}

				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT:

				byte[] t = new byte[8];
				System.arraycopy(data, 0, t, 0, 8);
				AVIOCTRLDEFs.STimeDay evtTime = new AVIOCTRLDEFs.STimeDay(t);

				int camChannel = Packet.byteArrayToInt_Little(data, 12);
				int evtType = Packet.byteArrayToInt_Little(data, 16);

				if (evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS
						&& evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS)

					break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:
				if (camera != null) {
					if (camera.isSessionConnected()
							&& camera.isChannelConnected(0)) {
						if (device != null) {
							device.Status = UIApplication.app.getText(
									R.string.connstus_connected).toString();
							device.Online = true;
						}
					}
				}
				int total = Packet.byteArrayToInt_Little(data, 40);
				if (total == -1 && camera != null
						&& camera.getSDCardFormatSupported(0) && device != null
						&& device.ShowTipsForFormatSDCard)
					
					break;
			}

			if (device != null)
				device.Mode = camera.getSessionMode();

			adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	public void loading(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle(getText(R.string.dialog_quit));
		loadingDialog.setMessage(getText(R.string.dialog_wait));
		loadingDialog.show();
	}

	public void loadingDelete(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle(getText(R.string.dialog_deleting));
		loadingDialog.setMessage(getText(R.string.dialog_wait));
		loadingDialog.show();
	}

	public void stopLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}

	private Handler deleteHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				stopLoading();
				adapter.notifyDataSetInvalidated();
			}
		}
	};

	/**
	 * 删除摄像机线程 jiangbing 2014-3-2
	 */
	private void deleteThread() {
		loadingDelete(getActivity());
		Thread delete = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					deleteCam();
					Message msg = new Message();
					msg.what = 1;
					deleteHandle.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Looper.loop();
			}
		});
		delete.start();
	}

	/**
	 * 获取服务器ipcam 信息
	 */
	private void getRemoteIpcThread() {

		if (UIApplication.mobile == null || UIApplication.upswd == null)
			return;

		Thread load = new Thread(new Runnable() {
			@Override
			public void run() 
			{
				try {
					GetServerData data = new GetServerData();
					UIApplication.mData = data.getRemoteIpc(
							UIApplication.mobile, UIApplication.upswd);
					// 服务器IPC 信息同步到本地
					if (UIApplication.mData != null) {
						for (Map<String, Object> map : UIApplication.mData) {
							String uid = (String) map.get("uid");
							boolean exsis = false;
							for (MyCamera cam : UIApplication.CameraList) {
								if (cam.getUID().equals(uid)) {
									exsis = true;
									break;
								}

							}

							if (!exsis) {
								String dev_nickname = (String) map.get("name");
								String dev_uid = (String) map.get("uid");
								String view_pwd = (String) map.get("pswd");
								/* add value to data base */
								DatabaseManager manager = new DatabaseManager(
										getActivity());
								long db_id = manager.addDevice(dev_nickname,
										dev_uid, "", "", "admin", view_pwd, 3,
										0);
								final MyCamera camera = new MyCamera(dev_nickname,
										dev_uid, "admin", view_pwd, 0);

								DeviceInfo dev = new DeviceInfo(db_id, camera
										.getUUID(), dev_nickname, dev_uid,
										"admin", view_pwd, "", 3, 0, null);
								UIApplication.DeviceList.add(dev);
								UIApplication.CameraList.add(camera);
								handler.post(new Runnable() {
									@Override
									public void run() {
										if (adapter != null)
											adapter.notifyDataSetChanged();
										camera.registerIOTCListener(ListFragment.this);
										camera.connect(camera.getUID());
										camera.start(Camera.DEFAULT_AV_CHANNEL,
												"admin", camera.getPassword());
									}
								});
							}

						}
					}

					// 本地IPC 信息同步到服务器
					for (DeviceInfo dev : UIApplication.DeviceList) {
						boolean exsis = false;
						if (UIApplication.mData != null) {
							for (Map<String, Object> cam : UIApplication.mData) {
								String uid = (String) cam.get("uid");
								if (dev.UID.equals(uid)) {
									exsis = true;
									break;
								}

							}
						}

						if (!exsis) {
							HttpGetTool HttpGet = new HttpGetTool(
									getActivity(), ListFragment.class.getName());
							String url = AppConfig.SERVER_ADDRESS
									+ "?motion=addCamera&mobile="
									+ UIApplication.mobile + "&upswd="
									+ UIApplication.upswd + "&uid=" + dev.UID
									+ "&name=" + URLEncoder.encode(dev.NickName, "utf-8") + "&pswd="
									+ URLEncoder.encode(dev.View_Password, "utf-8");
							HttpGet.execute(url.trim());
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		load.start();
	}

	private void register() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				try {
					if (UIApplication.CameraList != null) {
						for (int j = 0; j < UIApplication.CameraList.size(); j++) {
							if (UIApplication.CameraList.get(j).reg == 0) {
								HttpGetTool HttpGetTool = new HttpGetTool(
										getActivity(), MainActivity.class
												.getName());
								String st = freelancer.worldvideo.database.DatabaseManager.GCM_Server_URL
										+ "/register_freelancer?uid="
										+ UIApplication.CameraList.get(j)
												.getUID()
										+ "&token=&regid="
										+ DatabaseManager.GCM_token
										+ "&clientid="
										+ DatabaseManager.Package_name;
								HttpGetTool.execute(st);
								Thread.sleep(1500);
								/**
								 * 注册是否成功
								 */
								if ((HttpGetTool.result != null && HttpGetTool.result
										.contains("success"))
										|| HttpGetTool.result.contains("has")) {
									UIApplication.CameraList.get(j).reg = 1;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void reconnect() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (UIApplication.CameraList != null) {
						for (MyCamera camera : UIApplication.CameraList) {
								if (camera != null && !camera.isBusy) {
									
									 if(!camera.isChannelConnected(0))
									{
										MyTool.println(" ListFragment disconnect  ");
										camera.disconnect();
										camera.connect(camera.getUID());
										camera.start(Camera.DEFAULT_AV_CHANNEL,
												camera.getAcc(), camera.getPassword());
									}
								}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
