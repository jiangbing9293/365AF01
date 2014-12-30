package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetAlarmTimeReq;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 	报警计划
 * @author jiangbing
 *
 */
public class AlarmPlanActivity extends BaseActivity implements IRegisterIOTCListener
{
	
	private TitleView mTitle = null;
	
	private byte Week[][] = new byte[7][24];
	private String[] hours = new String[]{"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
	private boolean open = true;
	public boolean close = true;
	private ListView time_list = null;
	private MyAdapter adapter = null;
	private ViewHolder holder = null;
	private TextView day_time_s = null;
	private TextView week_0 = null;
	private TextView week_1 = null;
	private TextView week_2 = null;
	private TextView week_3 = null;
	private TextView week_4 = null;
	private TextView week_5 = null;
	private TextView week_6 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set_plan);
		if(AlarmSetActivity.mCamera != null){
			AlarmSetActivity.mCamera.registerIOTCListener(this);
			AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_ALARMTIME_REQ, MYAVIOCTRLDEFs.sMsgNetviomGetAlarmTimeRep.parseContent(AlarmSetActivity.mDevice.ChannelIndex));
		}
		initView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.alarm_plan_title);
		mTitle.setTitle(getString(R.string.alertset_time));
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				if (AlarmSetActivity.mCamera != null)
				{
					if(!AlarmSetActivity.mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL))
					{
						return;
					}
					AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_ALARMTIME_REQ, sMsgNetviomSetAlarmTimeReq.parseContent(Camera.DEFAULT_AV_CHANNEL,1,Week));
				}
				finish();
			}
		});
		time_list = (ListView)findViewById(R.id.week_day);
		adapter = new MyAdapter(this);
		time_list.setAdapter(adapter);
		adapter.notifyDataSetInvalidated();
		day_time_s = (TextView)findViewById(R.id.day_time_s);
		week_0 = (TextView)findViewById(R.id.week_0);
		week_1 = (TextView)findViewById(R.id.week_1);
		week_2 = (TextView)findViewById(R.id.week_2);
		week_3 = (TextView)findViewById(R.id.week_3);
		week_4 = (TextView)findViewById(R.id.week_4);
		week_5 = (TextView)findViewById(R.id.week_5);
		week_6 = (TextView)findViewById(R.id.week_6);
		day_time_s.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(open)
				{
					Week = null;
					Week = new byte[7][24];
					adapter.notifyDataSetChanged();
					open = false;
					day_time_s.setText(getString(R.string.alertset_time_open));
				}
				else
				{
					open = true;
					day_time_s.setText(getString(R.string.alertset_time_close));
					for (int i = 0; i < Week.length; i++) {
						for (int j = 0; j < Week[0].length; j++) {
							Week[i][j] = 1;
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
		week_0.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[0].length; i++) {
						Week[0][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[0].length; i++) {
						Week[0][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[1].length; i++) {
						Week[1][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[1].length; i++) {
						Week[1][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[2].length; i++) {
						Week[2][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[2].length; i++) {
						Week[2][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[3].length; i++) {
						Week[3][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[3].length; i++) {
						Week[3][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[4].length; i++) {
						Week[4][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[4].length; i++) {
						Week[4][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[5].length; i++) {
						Week[5][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[5].length; i++) {
						Week[5][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
		week_6.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				if(close)
				{
					for (int i = 0; i < Week[6].length; i++) {
						Week[6][i] = 1;
					}
					adapter.notifyDataSetChanged();
					close = false;
				}
				else
				{
					for (int i = 0; i < Week[6].length; i++) {
						Week[6][i] = 0;
					}
					adapter.notifyDataSetChanged();
					close = true;
				}
			}
		});
	}
	public final class ViewHolder {
		public TextView img_0;
		public TextView img_1;
		public TextView img_2;
		public TextView img_3;
		public TextView img_4;
		public TextView img_5;
		public TextView img_6;
		public TextView hour;
	}
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return hours.length;
		}

		@Override
		public Object getItem(int position) {
			return hours[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.adapter_alart_time, null);
				holder.img_0 = (TextView) convertView
						.findViewById(R.id.week_0);
				holder.img_1 = (TextView) convertView
						.findViewById(R.id.week_1);
				holder.img_2 = (TextView) convertView
						.findViewById(R.id.week_2);
				holder.img_3 = (TextView) convertView
						.findViewById(R.id.week_3);
				holder.img_4 = (TextView) convertView
						.findViewById(R.id.week_4);
				holder.img_5 = (TextView) convertView
						.findViewById(R.id.week_5);
				holder.img_6 = (TextView) convertView
						.findViewById(R.id.week_6);
				holder.hour = (TextView) convertView
						.findViewById(R.id.day_hour);
				holder.hour.setTextSize(14);
				convertView.setTag(holder);

			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.hour.setText(hours[position]);
			//
			holder.img_0.setText(hours[position]);
			holder.img_1.setText(hours[position]);
			holder.img_2.setText(hours[position]);
			holder.img_3.setText(hours[position]);
			holder.img_4.setText(hours[position]);
			holder.img_5.setText(hours[position]);
			holder.img_6.setText(hours[position]);
			if(Week[0][position] == 0)
			{
				holder.img_0.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_0.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[1][position] == 0)
			{
				holder.img_1.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_1.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[2][position] == 0)
			{
				holder.img_2.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_2.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[3][position] == 0)
			{
				holder.img_3.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_3.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[4][position] == 0)
			{
				holder.img_4.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_4.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[5][position] == 0)
			{
				holder.img_5.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_5.setBackgroundResource(R.drawable.bg_menu);
			}
			//
			if(Week[6][position] == 0)
			{
				holder.img_6.setBackgroundResource(R.drawable.bg_control);
			}
			else
			{
				holder.img_6.setBackgroundResource(R.drawable.bg_menu);
			}
			holder.hour.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(close)
					{
						for (int i = 0; i < Week.length; i++) {
							Week[i][position] = 1;
						}
						adapter.notifyDataSetChanged();
						close = false;
					}
					else
					{
						for (int i = 0; i < Week.length; i++) {
							Week[i][position] = 0;
						}
						adapter.notifyDataSetChanged();
						close = true;
					}
				}
			});
			
			holder.img_0.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v)
				{
					if(Week[0][position] == 0)
					{
						Week[0][position] = 1;
					}
					else
					{
						Week[0][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[1][position] == 0)
					{
						Week[1][position] = 1;
					}
					else
					{
						Week[1][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[2][position] == 0)
					{
						Week[2][position] = 1;
					}
					else
					{
						Week[2][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[3][position] == 0)
					{
						Week[3][position] = 1;
					}
					else
					{
						Week[3][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_4.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[4][position] == 0)
					{
						Week[4][position] = 1;
					}
					else
					{
						Week[4][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_5.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[5][position] == 0)
					{
						Week[5][position] = 1;
					}
					else
					{
						Week[5][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			holder.img_6.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					if(Week[6][position] == 0)
					{
						Week[6][position] = 1;
					}
					else
					{
						Week[6][position] = 0;
					}
					adapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}

	}
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		
	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		
	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		
	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		
	}

	@Override
	public void receiveIOCtrlData(Camera camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
		if (AlarmSetActivity.mCamera == camera) 
		{
			Bundle bundle = new Bundle();
			bundle.putInt("sessionChannel", sessionChannel);
			bundle.putByteArray("data", data);

			Message msg = new Message();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	private Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle = msg.getData();
			byte[] data = bundle.getByteArray("data");
			switch (msg.what) {
			case MYAVIOCTRLDEFs.IOTYPE_NETVIOM_GET_ALARMTIME_RESP:
				Week = null;
				Week = new byte[7][24];
				for (int i = 0; i < 7; i++) 
				{
					System.arraycopy(data, (i*24)+8, Week[i], 0, 24);
				}
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};
}
