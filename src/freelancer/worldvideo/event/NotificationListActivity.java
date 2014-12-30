package freelancer.worldvideo.event;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.net.GetServerData;
import freelancer.worldvideo.util.MyAlarmTool;
import freelancer.worldvideo.util.MyTool;
import freelancer.worldvideo.videorecord.RecordTimeActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

public class NotificationListActivity extends BaseActivity
{
	private ProgressDialog loadingDialog;
	private TitleView mTitle = null;
	private String uid = "";
	private String name = "";
	private int flag = 0;
	
	private List<String> mList = null;
	
	private ListView mListView = null;
	private MyAdapter mAdapter = null;
	
	private Handler mHandler = null;
	
	public static long start;
	public static boolean search = false;
	private long end;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notification_list);
		uid = getIntent().getStringExtra("uid");
		name = getIntent().getStringExtra("name");
		flag = getIntent().getIntExtra("flag",0);
		mList = new ArrayList<String>();
		mHandler = new Handler();
		start = 60 *60 * 24;
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getNotificationList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView()
	{
		mTitle = (TitleView)findViewById(R.id.notification_title);
		mListView = (ListView)findViewById(R.id.notification_list_listview);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mTitle.setTitle(name);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_search, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				search = true;
				Intent view = new Intent();
				view.setClass(NotificationListActivity.this, RecordTimeActivity.class);
				startActivity(view);
			}
		});
	}
	
	private void getNotificationList()
	{
		loading(this);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				GetServerData server = new GetServerData();
				end = System.currentTimeMillis()/1000;
				start = end - start;
				
				List<String> list = server.getNotifications(uid, start, end);
				if(list != null)
				{
					for (int i = 0; i < list.size(); i++) {
						MyTool.println(list.get(i));
					}
					mList.clear();
					mList.addAll(list);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							stopLoading();
							mAdapter.notifyDataSetChanged();
						}
					});
				}
				else
				{
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							stopLoading();
							Toast.makeText(NotificationListActivity.this, "未找到记录", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
	
	public final class ViewHolder {
		public TextView tyep;
		public TextView time;
	}
	
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.adapter_notification_list, null);
				holder.tyep = (TextView) convertView
						.findViewById(R.id.notification_type);
				holder.time = (TextView) convertView
						.findViewById(R.id.notification_time);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			String msg = mList.get(position);
			holder.tyep.setText(MyAlarmTool.getALarmMsgByJson(msg,flag).get("Msg"));
			holder.time.setText(MyAlarmTool.getALarmMsgByJson(msg,flag).get("Time"));
			return convertView;
		}

	}
	
	public void loading(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
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
}
