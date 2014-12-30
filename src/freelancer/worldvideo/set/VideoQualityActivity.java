package freelancer.worldvideo.set;

import neutral.safe.chinese.R;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
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
import android.widget.Toast;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.ParameterSetActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
/**
 * 视频质量
 * @author jiangbing
 *
 */
public class VideoQualityActivity extends BaseActivity
{
	
	private final static String qInfo[] = {
	 "分辨率：1280x720\n流量控制：512kb/s\n最大码流：10fps/s",
	 "分辨率：640x480\n流量控制：512kb/s\n最大码流：10fps/s",
	 "分辨率：320x240\n流量控制：256kb/s\n最大码流：10fps/s",
	"分辨率：320x240\n流量控制：128kb/s\n最大码流：10fps/s",
	 "分辨率：320x240\n流量控制：64kb/s\n最大码流：7fps/s"};
	
	private TitleView mTitle = null;
	private ListView listView = null;
	private MyAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_videoquality);
		initView();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.paramter_set_videoquality_title);
		listView = (ListView) findViewById(R.id.videoquality_list);
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				ParameterSetActivity.mVideoQuality = position;
				adapter.notifyDataSetChanged();
				if(qInfo != null && position < qInfo.length)
					Toast.makeText(getApplication(), qInfo[position], Toast.LENGTH_SHORT).show();
			}
			
		});
		mTitle.setTitle(getString(R.string.txtVideoQuality));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				if (ParameterSetActivity.mCamera != null) {
					if (ParameterSetActivity.mVideoQuality != -1) {
						ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex, (byte) (ParameterSetActivity.mVideoQuality+1)));
					}
					finish();
				}
			}
		});
		mTitle.hiddenRightButton();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (ParameterSetActivity.mCamera != null)
		{
			if (ParameterSetActivity.mVideoQuality != -1) {
				ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex, (byte) (ParameterSetActivity.mVideoQuality+1)));
			}
		}
		return super.onKeyDown(keyCode, event);
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
			return ParameterSetActivity.videoQualitys.length;
		}

		@Override
		public Object getItem(int position) {
			return ParameterSetActivity.videoQualitys[position];
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
				holder.status.setVisibility(View.GONE);
				holder.img.setVisibility(View.INVISIBLE);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.img.setVisibility(View.INVISIBLE);
			holder.title.setText(ParameterSetActivity.videoQualitys[position]);
			holder.status.setText("");
			if(ParameterSetActivity.mVideoQuality == position)
			{
				holder.img.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

	}
	
}
