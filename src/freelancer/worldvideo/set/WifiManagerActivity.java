package freelancer.worldvideo.set;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.ParameterSetActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
/**
 * 
 * @author jiangbing
 *
 */
public class WifiManagerActivity extends BaseActivity implements IRegisterIOTCListener
{
	private TitleView mTitle = null;
	private ListView listView = null;
	private MyAdapter adapter = null;
	private TextView lable_search = null;
	public static List<AVIOCTRLDEFs.SWifiAp> m_wifiList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wifi_manager);
		initView();
		if(m_wifiList == null)
		{
			m_wifiList = new ArrayList<AVIOCTRLDEFs.SWifiAp>();
		}
		boolean save = getIntent().getBooleanExtra("save", false);
		if(save)
		{
			waitThread(59);
		}else
		{

			loadWifiList();
		}
	}

	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.wifi_manager_title);
		listView = (ListView) findViewById(R.id.wifi_list);
		lable_search = (TextView) findViewById(R.id.lable_wifi_search);
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		adapter.notifyDataSetInvalidated();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent pwd = new Intent();
				pwd.setClass(WifiManagerActivity.this, WifiPasswordSetActivity.class);
				pwd.putExtra("pos", position);
				startActivity(pwd);
			}
		});
		
		mTitle.setTitle(getString(R.string.wifi_manager_title1));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
				m_wifiList = null;
			}
		});
		mTitle.hiddenRightButton();
	}
	private void loadWifiList()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(m_wifiList != null)
					m_wifiList.clear();
				if (ParameterSetActivity.mCamera != null) {
					ParameterSetActivity.mCamera.registerIOTCListener(WifiManagerActivity.this);
					ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ, AVIOCTRLDEFs.SMsgAVIoctrlListWifiApReq.parseContent());
				}
			}
		}).start();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(ParameterSetActivity.mCamera != null)
		{
			ParameterSetActivity.mCamera.unregisterIOTCListener(this);
			handler.removeCallbacksAndMessages(null);
		}
	
	}
	private void waitThread(final int time)
	{
		lable_search.setVisibility(View.VISIBLE);
		listView.setVisibility(View.INVISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int start = time;
				while(!Thread.currentThread().isInterrupted())
				{
					if(handler == null)
						Thread.currentThread().interrupt();
					Message msg = new Message();
					if(start > 0)
					{
						msg.what = 1;
						msg.arg1 = start;
						handler.sendMessage(msg);
						start--;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else
					{
						msg.what = 2;
						handler.sendMessage(msg);
						Thread.currentThread().interrupt();
					}
				}
			}
		}).start();
	}
	public final class ViewHolder {
		public ImageView img = null;
		public TextView title = null;
		public TextView status = null;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if(m_wifiList == null)
				return 0;
			return m_wifiList.size();
		}

		@Override
		public Object getItem(int position) {
			return m_wifiList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.adapter_paramter_seleter, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.image_check);
				holder.title = (TextView) convertView
						.findViewById(R.id.lable_name);
				holder.status = (TextView)convertView.findViewById(R.id.lable_status);
				holder.title.setTextSize(14);
				holder.status.setTextSize(12);
				convertView.setTag(holder);

			}
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}
			if(m_wifiList.size() > position)
				
			{
				holder.img.setVisibility(View.INVISIBLE);
				holder.title.setText(ParameterSetActivity.getString(m_wifiList.get(position).ssid));
				holder.status.setText("");
				switch (m_wifiList.get(position).status) {
				case 0:
					holder.status.setText(getText(R.string.tips_wifi_disconnect));
					break;
				case 1:
					holder.status.setText(getText(R.string.tips_wifi_connected));
					holder.img.setVisibility(View.VISIBLE);
					break;
				case 2:
					holder.status.setText(getText(R.string.tips_wifi_wrongpassword));
					break;
				case 3:
					holder.status.setText(getText(R.string.tips_wifi_weak_signal));
					break;
				case 4:
					holder.status.setText(getText(R.string.tips_wifi_ready));
					break;

				}
				
			}
			return convertView;
		}

	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
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
			
			case 1:
				lable_search.setText(getString(R.string.wifi_manager_lable_reboot)+msg.arg1+"s");
				break;
				
			case 2:
				lable_search.setText(R.string.wifi_manager_lable_search);
				if (ParameterSetActivity.mCamera != null) {
					ParameterSetActivity.mCamera.unregisterIOTCListener(WifiManagerActivity.this);
					ParameterSetActivity.mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
					ParameterSetActivity.mCamera.disconnect();
					ParameterSetActivity.mCamera.connect(ParameterSetActivity.mDevice.UID);
					ParameterSetActivity.mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin",ParameterSetActivity.mDevice.View_Password);
				}
				loadWifiList();
				break;
			
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_RESP:

				int cnt = Packet.byteArrayToInt_Little(data, 0);
				int size = AVIOCTRLDEFs.SWifiAp.getTotalSize();

				m_wifiList.clear();
				listView.setVisibility(View.VISIBLE);
				lable_search.setVisibility(View.GONE);
				if (cnt > 0 && data.length >= 40) {

					int pos = 4;

					for (int i = 0; i < cnt; i++) {

						byte[] ssid = new byte[32];
						System.arraycopy(data, i * size + pos, ssid, 0, 32);

						byte mode = data[i * size + pos + 32];
						byte enctype = data[i * size + pos + 33];
						byte signal = data[i * size + pos + 34];
						byte status = data[i * size + pos + 35];
						m_wifiList.add(new AVIOCTRLDEFs.SWifiAp(ssid, mode, enctype, signal, status));

						adapter.notifyDataSetChanged();
					}
				}
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
				
				if(handler != null)
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(ParameterSetActivity.mCamera != null)
								ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ, AVIOCTRLDEFs.SMsgAVIoctrlListWifiApReq.parseContent());
						}
					}, 30000);

				break;
		}
			super.handleMessage(msg);
		}
	};
	
}
