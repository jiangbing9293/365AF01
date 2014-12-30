/**
 * ListFragment.java
 */
package freelancer.worldvideo.fragment;

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
import freelancer.worldvideo.videorecord.LocalRecordActivity;

/**
 *@function: 本地录像界面
 *@author jiangbing
 *@data: 2014-2-15
 */
public class PlaybackLocalFragment extends Fragment 
{
	ListView locallist = null;
	MyAdapter adapter;
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static PlaybackLocalFragment newInstance(int index) {
		PlaybackLocalFragment f = new PlaybackLocalFragment();

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
		View view = inflater.inflate(R.layout.fragment_playback_local, container, false);
		locallist = (ListView)view.findViewById(R.id.local_list);
		adapter = new MyAdapter(getActivity());
		locallist.setAdapter(adapter);
		locallist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent localrecord = new Intent();
				localrecord.setClass(getActivity(), LocalRecordActivity.class);
				localrecord.putExtra("uid", UIApplication.DeviceList.get(position).UID);
				localrecord.putExtra("camName", UIApplication.DeviceList.get(position).NickName);
				getActivity().startActivity(localrecord);
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
