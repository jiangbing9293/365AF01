/**
 * ListFragment.java
 */
package freelancer.worldvideo.event;

import neutral.safe.chinese.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.alarmset.AlarmSetActivity;

/**
 *@function: 查看通知
 *@author jiangbing
 *@data: 2014-2-15
 */
public class NotificationFragment extends Fragment 
{
	ListView eventlist = null;
	MyAdapter adapter;
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static NotificationFragment newInstance(int index) {
		NotificationFragment f = new NotificationFragment();

		// Supply index input as an argument.
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
		View view = inflater.inflate(R.layout.fragment_playback_event, container, false);
		eventlist = (ListView)view.findViewById(R.id.playback_event_list);
		adapter = new MyAdapter(getActivity());
		eventlist.setAdapter(adapter);
		eventlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent view = new Intent();
				view.setClass(getActivity(), NotificationListActivity.class);
				view.putExtra("uid", UIApplication.DeviceList.get(position).UID);
				view.putExtra("name", UIApplication.DeviceList.get(position).NickName);
				view.putExtra("flag", UIApplication.DeviceList.get(position).flag);
				getActivity().startActivity(view);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public final class ViewHolder {
		public ImageView img;
		public TextView info;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return UIApplication.DeviceList.size();
		}

		@Override
		public Object getItem(int position) {
			return UIApplication.DeviceList.get(position);
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

				convertView = mInflater.inflate(R.layout.adapter_playback, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.playback_img);
				holder.img.setImageResource(R.drawable.notification_icon_alert);
				holder.info = (TextView) convertView
						.findViewById(R.id.cam_uid);
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
			}
			holder.info.setText(UIApplication.DeviceList.get(position).NickName);
			return convertView;
		}

	}
}
