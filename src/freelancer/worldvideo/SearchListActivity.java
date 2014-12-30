/**
 * SearchListActivity.java
 */
package freelancer.worldvideo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.st_LanSearchInfo;

import freelancer.worldvideo.addipc.AddCam2Activity;
import freelancer.worldvideo.util.DeviceInfo;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;


/**
 * @function: 内网搜素
 * @author jiangbing
 * @data: 2014-2-27
 */
public class SearchListActivity extends BaseActivity {
	private ProgressDialog loadingDialog;
	private TitleView mTitle;
	ListView searchlist = null;
	MyAdapter adapter;
	private View footView = null;
	private List<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchlist);
		mTitle = (TitleView) findViewById(R.id.searchlist_title);
		mTitle.setTitle(getText(R.string.searchlist_activity_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) 
			{
				SearchListActivity.this.finish();
			}

		});
		
		mTitle.setRightButtonBg(R.drawable.icon_refresh, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				refreshThread();
			}
		});
		footView = ((LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.btn_search, null, false);
		footView.setVisibility(View.INVISIBLE);
		searchlist = (ListView)findViewById(R.id.searchlist);
		searchlist.addFooterView(footView);
		adapter = new MyAdapter(this);
		searchlist.setAdapter(adapter);
		searchlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent addcam2 = new Intent();
				addcam2.setClass(SearchListActivity.this, AddCam2Activity.class);
				addcam2.putExtra("serach_resule", (String)mData.get(position).get("text1"));
				startActivity(addcam2);
				SearchListActivity.this.finish();
			}
		});
		if(UIApplication.arrResp != null && UIApplication.arrResp.length > 0)
		{
			mData.clear();
			for (st_LanSearchInfo resp : UIApplication.arrResp) {
				Map<String , Object> info = new HashMap<String, Object>();
				info.put("text1", new String(resp.UID).trim());
				info.put("text2", new String(resp.IP).trim());
				mData.add(info);
				adapter.notifyDataSetChanged();
			}
		}
		else
		{
			refreshThread();
		}
	}
	
	public void getData()
	{
		UIApplication.arrResp = Camera.SearchLAN();

		if (UIApplication.arrResp != null && UIApplication.arrResp.length > 0) {
			mData.clear();
			for (st_LanSearchInfo resp : UIApplication.arrResp) {
				Map<String , Object> info = new HashMap<String, Object>();
				info.put("text1", new String(resp.UID).trim());
				info.put("text2", new String(resp.IP).trim());
				mData.add(info);
			}
		}
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
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
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

				convertView = mInflater.inflate(R.layout.adapter_searchlist, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.searchlist_img_default);
				holder.title = (TextView) convertView
						.findViewById(R.id.searchlist_txt_name1);
				holder.info = (TextView) convertView
						.findViewById(R.id.searchlist_txt_name2);
				holder.title.setTextSize(14);
				holder.info.setTextSize(14);
				holder.viewBtn = (Button) convertView
						.findViewById(R.id.searchlist_btn_addvideo);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			
			boolean is_added = false;
			for(int i = 0; i < UIApplication.DeviceList.size();++i)
			{
				DeviceInfo dev = UIApplication.DeviceList.get(i);
				if(dev.UID.equals((String)mData.get(position).get("text1")))
				{
					is_added = true;
					break;
				}
			}
			holder.title.setText((String)mData.get(position).get("text1"));
			if(is_added)
			{
				holder.info.setText((String)mData.get(position).get("text2")+"(Added)");
			}
			else
			{
				holder.info.setText((String)mData.get(position).get("text2")+"(Not Add)");
			}

			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent addcam2 = new Intent();
					addcam2.setClass(SearchListActivity.this, AddCam2Activity.class);
					addcam2.putExtra("serach_resule", (String)mData.get(position).get("text1"));
					startActivity(addcam2);
					SearchListActivity.this.finish();
				}
			});
			return convertView;
		}

	}
	
	private Handler exitHandle = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){ 
				stopLoading();
				adapter.notifyDataSetChanged();
			}
		}
	};
	/**
	 * 退出线程
	 *jiangbing
	 *2014-3-2
	 */
	private void refreshThread(){
		loading(this); //开始加载
 
		Thread refresh = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					getData();
					Message msg = new Message();
					msg.what = 1;
					exitHandle.sendMessage(msg);
					Thread.currentThread().interrupt();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		refresh.start();
	}
 
	

	public void loading(Activity act){

		if(loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle(getText(R.string.searchlist_activity_searching));
		loadingDialog.setMessage(getText(R.string.dialog_wait));
		loadingDialog.show();
	}
	
	public void stopLoading(){
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
	}
	

}
