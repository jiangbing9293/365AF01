package freelancer.worldvideo.addipc;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
import freelancer.worldvideo.wifi.WifiAdmin;
/**
 * 所有AP模式下的 IPC 信息
 * @author jiangbing
 *
 */
public class LanAPIPCListActivity extends BaseActivity
{
	private TitleView mTitle = null;
	private ListView mListView = null;
	private MyAdapter adapter = null;
	private List<String> m_wifiList = null;
	
	private ProgressDialog progress = null;
	private Handler mHandler = null;
	
	WifiAdmin wifiAdmin  = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ap_ipclist);
		mHandler = new Handler();
		m_wifiList = new ArrayList<String>();
		progress = new ProgressDialog(this);
		wifiAdmin = new WifiAdmin(getApplicationContext());
		wifiAdmin.openWifi();
		initView();
		getIPCThread();
	}
	
	public void onResume()
	{
		super.onResume();
		if(progress != null && !progress.isShowing())
		{
			progress.setMessage(getString(R.string.ap_ipc_progress_msg));
			progress.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.ap_ipc_list_title);
		mListView = (ListView) findViewById(R.id.ap_ipc_list);
		mTitle.setTitle(getString(R.string.ap_ipc_title));
		adapter = new MyAdapter(this);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mTitle.setRightButtonNoBg(getResources().getString(R.string.dialog_cancel), new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						AddCam2Activity.class);
				startActivity(intent);
				finish();
			}
		});
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				if(wifiAdmin != null && m_wifiList != null && position < m_wifiList.size())
					wifiAdmin.changeWifi(m_wifiList.get(position));
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						AddCam2Activity.class);
				intent.putExtra("isExsitsIPC", true);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private void getIPCThread()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				wifiAdmin.startScan();
				 for (int i = 0; i < wifiAdmin.getWifiList().size(); i++) {
		    		 String w[] = wifiAdmin.getWifiList().get(i).toString().split(",");
		    		 String ssid = w[0].substring(5).trim();
		    		 
		             if(ssid.startsWith("IPCAM_"))
		             {
		            	 	m_wifiList.add(ssid);
		             }
		             mHandler.post(new Runnable() {
						
						@Override
						public void run()
						{
							if(progress != null && progress.isShowing())
							{
								progress.dismiss();
							}
							adapter.notifyDataSetChanged();
						}
					});
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
			if (m_wifiList == null)
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
				convertView = mInflater.inflate(
						R.layout.adapter_paramter_seleter, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.image_check);
				holder.title = (TextView) convertView
						.findViewById(R.id.lable_name);
				holder.status = (TextView) convertView
						.findViewById(R.id.lable_status);
				holder.title.setTextSize(14);
				holder.status.setTextSize(12);
				holder.status.setVisibility(View.GONE);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (m_wifiList != null && m_wifiList.size() > position)

			{
				holder.img.setVisibility(View.INVISIBLE);
				holder.title.setText(m_wifiList.get(position));
				holder.status.setText("");
			}
			return convertView;
		}

	}
}
