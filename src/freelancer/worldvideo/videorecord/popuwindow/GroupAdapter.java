package freelancer.worldvideo.videorecord.popuwindow;

import java.io.File;
import java.util.List;

import neutral.safe.chinese.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupAdapter extends BaseAdapter {

	private Context context;

	private List<File> list;

	public GroupAdapter(Context context, List<File> list) {

		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.popuwindow_adpeter, null);
			holder = new ViewHolder();

			convertView.setTag(holder);

			holder.groupItem = (TextView) convertView
					.findViewById(R.id.groupItem);
			holder.groupItemId = (TextView) convertView
					.findViewById(R.id.groupItemId);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.groupItem.setTextColor(Color.WHITE);
		holder.groupItem.setText(list.get(position).getName().substring(1));
		int tem = position +1;
		if(tem < 10)
		{
			holder.groupItemId.setText("0"+tem);
		}
		else
		{
			holder.groupItemId.setText(""+tem);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView groupItem;
		TextView groupItemId;
	}

}
