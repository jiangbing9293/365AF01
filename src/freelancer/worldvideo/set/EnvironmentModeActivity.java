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
public class EnvironmentModeActivity extends BaseActivity
{
	
	private TitleView mTitle = null;
	private ListView listView = null;
	private MyAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set_environmental_mode);
		initView();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.paramter_set_envirnment_mode_title);
		listView = (ListView) findViewById(R.id.environment_mode_list);
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				ParameterSetActivity.mEnvMode = position;
				adapter.notifyDataSetChanged();
			}
		});
		mTitle.setTitle(getString(R.string.txtEnvironment));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				if (ParameterSetActivity.mCamera != null) {
					if (ParameterSetActivity.mEnvMode != -1) {
						ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex, (byte) ParameterSetActivity.mEnvMode));
					}
					finish();
				}
			}
		});
		mTitle.hiddenRightButton();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (ParameterSetActivity.mCamera != null) {
			if (ParameterSetActivity.mEnvMode != -1) {
				ParameterSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq.parseContent(ParameterSetActivity.mDevice.ChannelIndex, (byte) ParameterSetActivity.mEnvMode));
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			return ParameterSetActivity.environmentModes.length;
		}

		@Override
		public Object getItem(int position) {
			return ParameterSetActivity.environmentModes[position];
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
			holder.title.setText(ParameterSetActivity.environmentModes[position]);
			holder.status.setText("");
			if(ParameterSetActivity.mEnvMode == position)
			{
				holder.img.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

	}
	
}
