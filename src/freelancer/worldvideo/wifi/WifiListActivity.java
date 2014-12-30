package freelancer.worldvideo.wifi;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.MoreActivity;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

/**
 * 回复出厂wifi设置->wifi列表
 * 
 * @author jiangbing
 * 
 */

public class WifiListActivity extends BaseActivity {
	private TitleView mTitle = null;
	private ListView mListView = null;
	private MyAdapter adapter = null;
	private List<String> m_wifiList = null;
	
	private String userName = null;
	private String password = null;
	
	private ProgressDialog progress = null;
	private Handler mHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wifilist);
		initView();
		userName = "root";
		password = "root";
		mHandler = new Handler();
		m_wifiList = new ArrayList<String>();
		getWiFiList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView() {
		mTitle = (TitleView) findViewById(R.id.cgi_wifi_list_title);
		mListView = (ListView) findViewById(R.id.cgi_wifi_list);
		
		mTitle.setTitle(getString(R.string.wifi_manager_title1));
		adapter = new MyAdapter(this);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mTitle.hiddenRightButton();
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		
		mTitle.setRightButtonBg(R.drawable.icon_refresh, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
					 getWiFiList();
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("ssid", m_wifiList.get(position));
				intent.putExtra("username", userName);
				intent.putExtra("password", password);
				intent.setClass(WifiListActivity.this,
						WifiPasswordSetCgiActivity.class);
				startActivity(intent);
			}
		});
	}
	private void getWiFiList()
	{
		if(progress == null)
		{
			progress = new ProgressDialog(this);
		}
		progress.setMessage("请稍后...");
		progress.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				GetServerData server = new GetServerData();
				List<String> list = server.getWiFiList();
				if(list != null)
				{
					m_wifiList.addAll(list);
				}
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(progress != null && progress.isShowing())
						{
							progress.dismiss();
						}
						if(m_wifiList == null || m_wifiList.size() == 0)
						{
							Toast.makeText(WifiListActivity.this, "获取Wi-Fi列表失败，请刷新重新获取...", Toast.LENGTH_SHORT).show();
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	/*
	private void showWaiterAuthorizationDialog() {

		LayoutInflater factory = LayoutInflater.from(WifiListActivity.this);
		final View textEntryView = factory.inflate(R.layout.activity_wifilist,
				null);

		new AlertDialog.Builder(WifiListActivity.this)
				.setView(textEntryView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						final EditText etUserName = (EditText) textEntryView
								.findViewById(R.id.etuserName);
						final EditText etPassword = (EditText) textEntryView
								.findViewById(R.id.etPWD);

					    userName = etUserName.getText().toString()
								.trim();
						password = etPassword.getText().toString()
								.trim();
						System.out.println(userName + password +"====== ");
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						WifiListActivity.this.finish();
					}
				})
				.create().show();
	}
*/
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
