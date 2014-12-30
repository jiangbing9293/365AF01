/**
 * ListFragment.java
 */
package freelancer.worldvideo.fragment;

import neutral.safe.chinese.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *@function: 报警查询界面
 *@author jiangbing
 *@data: 2014-2-15
 */
public class AlertCheckFragment extends Fragment 
{
	private View mParent;
	
	ListView eventlist = null;
	MyAdapter adapter;
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static AlertCheckFragment newInstance(int index) {
		AlertCheckFragment f = new AlertCheckFragment();

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
		View view = inflater.inflate(R.layout.fragment_alertcheck, container, false);
		eventlist = (ListView)view.findViewById(R.id.alertcheck_list);
		adapter = new MyAdapter(getActivity());
		eventlist.setAdapter(adapter);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
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
		public TextView info;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
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

				convertView = mInflater.inflate(R.layout.adapter_alertcheck, null);
				holder.info = (TextView) convertView
						.findViewById(R.id.alert_event);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
//			holder.info.setText(dev.UID);
			return convertView;
		}

	}

}
