package freelancer.worldvideo.alarmset.task;

import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tutk.IOTC.Camera;

import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.alarmset.AlarmSetActivity;
import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs.sMsgNetviomSetAlarmTimeReq;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 
 * 任务列表
 * @author jiangbing
 *
 */
public class AlarmTaskListActivity extends BaseActivity
{
	private ProgressDialog mDialog = null;
	
	private TitleView mTitle = null;
	private ListView mList = null;
	private MyAdapter mAdapter = null;
	
	private View addTaskView = null;
	private Button addTask = null;
	private LinearLayout layout_delete = null;
	private Button btnCancel = null;
	private Button btnDelete = null;
	
	public static List<AlarmTask> mTaskList = null;
	private List<AlarmTask> mDeleteTask = null;
	
	private Handler mHandler = null;
	private boolean DELETE = false;
	private boolean ALL = false;
	
	private byte alarmData[][] = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmtask_list);
		mTaskList = new ArrayList<AlarmTask>();
		mDeleteTask = new ArrayList<AlarmTask>();
		alarmData = new byte[7][24];
		mHandler = new Handler();
		initView();
		getAlarmTask();
		mDeleteTask.clear();
	}
	
	public void onResume()
	{
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	private void initView()
	{
		mTitle = (TitleView)findViewById(R.id.alarmtask_title);
		mList = (ListView) findViewById(R.id.lv_alarmtask_list);
		addTaskView = ((LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.btn_add_alarmtask, null, false);
		addTask = (Button)addTaskView.findViewById(R.id.btn_add_alarmtask);
		
		layout_delete = (LinearLayout)addTaskView.findViewById(R.id.layout_delete);
		btnCancel = (Button)addTaskView.findViewById(R.id.btn_alarmtask_cancel);
		btnDelete = (Button)addTaskView.findViewById(R.id.btn_alarmtask_delete);
		
		layout_delete.setVisibility(View.GONE);
		
		mList.addFooterView(addTaskView);
		mAdapter = new MyAdapter(this);
		mList.setAdapter(mAdapter);
		
		initListener();
	}
	
	private void initListener()
	{
		mTitle.setTitle("报警计划");
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				saveSetting();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_delete_withe, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				if(DELETE == false)
				{
					addTask.setVisibility(View.GONE);
					layout_delete.setVisibility(View.VISIBLE);
					DELETE = true;
				}
				else
				{
					addTask.setVisibility(View.VISIBLE);
					layout_delete.setVisibility(View.GONE);
					DELETE = false;
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		addTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent view = new Intent();
				view.setClass(AlarmTaskListActivity.this, AddAlarmTaskActivity.class);
				startActivity(view);
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loading();
				DatabaseManager manager = new DatabaseManager(AlarmTaskListActivity.this);
				for (int i = 0; i < mDeleteTask.size(); i++) {
					manager.removeAlarmTaskById(mDeleteTask.get(i)._id);
					mTaskList.remove(mDeleteTask.get(i));
				}
				stopLoading();
				DELETE = false;
				mDeleteTask.clear();
				mAdapter.notifyDataSetChanged();
				addTask.setVisibility(View.VISIBLE);
				layout_delete.setVisibility(View.GONE);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(ALL)
				{
					ALL = false;
					btnCancel.setText("Cancel All");
					mDeleteTask.clear();
					for (int i = 0; i < mTaskList.size(); i++) {
						mTaskList.get(i).delete = 1;
						mDeleteTask.add(mTaskList.get(i));
					}
					mAdapter.notifyDataSetChanged();
				}
				else
				{
					ALL = true;
					mDeleteTask.clear();
					btnCancel.setText("All");
					for (int i = 0; i < mTaskList.size(); i++) {
						mTaskList.get(i).delete = 0;
					}
					mAdapter.notifyDataSetChanged();
				}
				
			}
		});
		
	}

	public final class ViewHolder {
		public TextView txtTime_ = null;
		public TextView txtWeeks = null;
		public Button btnFlag = null;
	}

	private class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			if(mTaskList != null && mTaskList.size() > 0)
				return mTaskList.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(mTaskList != null && mTaskList.size() > 0)
				return mTaskList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			AlarmTask mTask = mTaskList.get(position);
			
			if(mTask == null)
				return null;
				
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.adapter_alarmtask_list, null);
				
				holder.txtTime_ = (TextView) convertView
						.findViewById(R.id.txt_time_);
				holder.txtWeeks = (TextView) convertView
						.findViewById(R.id.txt_weeks);
				holder.btnFlag = (Button) convertView
						.findViewById(R.id.btn_flag);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.txtTime_.setText(getTime_(mTask.time_));
			holder.txtWeeks.setText(getWeeks(mTask.weeks));
			
			if(DELETE)
			{
				LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(30, 30);
				layout.gravity = 21;
				layout.rightMargin = 20;
				holder.btnFlag.setLayoutParams(layout);
			}
			else
			{
				LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(90, 30);
				layout.gravity = 21;
				layout.rightMargin = 20;
				holder.btnFlag.setLayoutParams(layout);
			}
			
			if(DELETE)
			{
				if(mTaskList.get(position).delete == 1)
				{
					holder.btnFlag.setBackgroundResource(R.drawable.icon_checked);
				}
				else
				{
					holder.btnFlag.setBackgroundResource(R.drawable.icon_unckecked);
				}
				
				if(mDeleteTask.size() > 0)
				{
					btnDelete.setBackgroundColor(getResources().getColor(R.color.alarmtask_color_two));
					btnDelete.setTextColor(Color.WHITE);
					btnDelete.setText("Delete("+mDeleteTask.size()+")");
				}
				else
				{
					btnDelete.setBackgroundColor(getResources().getColor(R.color.alarmtask_color_one));
					btnDelete.setTextColor(Color.BLACK);
					btnDelete.setText("Delete");
				}
			}
			else
			{
				if(mTask.flag == 1)
				{
					holder.btnFlag.setBackgroundResource(R.drawable.switch_on);
				}
				else
				{
					holder.btnFlag.setBackgroundResource(R.drawable.switch_off);
				}
			}
			
			holder.btnFlag.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if(DELETE == false)
					{
						if(mTaskList.get(position).flag == 1)
						{
							v.setBackgroundResource(R.drawable.switch_off);
							mTaskList.get(position).setFlag(0);
						}
						else
						{
							v.setBackgroundResource(R.drawable.switch_on);
							mTaskList.get(position).setFlag(1);
						}
						AlarmTask mTask = mTaskList.get(position);
						DatabaseManager manager = new DatabaseManager(AlarmTaskListActivity.this);
						manager.updateAlarmTaskById(mTask._id, mTask.time_, mTask.weeks, mTask.flag);
					}
					else
					{
						if(mTaskList.get(position).delete == 1)
						{
							mDeleteTask.remove(mTaskList.get(position));
							v.setBackgroundResource(R.drawable.icon_unckecked);
							mTaskList.get(position).delete = 0;
						}
						else
						{
							mDeleteTask.add(mTaskList.get(position));
							v.setBackgroundResource(R.drawable.icon_checked);
							mTaskList.get(position).delete = 1;
						}
						
						if(mDeleteTask.size() > 0)
						{
							btnDelete.setBackgroundColor(getResources().getColor(R.color.alarmtask_color_two));
							btnDelete.setTextColor(Color.WHITE);
							btnDelete.setText("Delete("+mDeleteTask.size()+")");
						}
						else
						{
							btnDelete.setBackgroundColor(getResources().getColor(R.color.alarmtask_color_one));
							btnDelete.setTextColor(Color.BLACK);
							btnDelete.setText("Delete");
						}
					}
				}
			});
			
			return convertView;
		}

	}
	
	private void getAlarmTask()
	{
		loading();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				DatabaseManager manager = new DatabaseManager(
						AlarmTaskListActivity.this);
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor cursor = db.query(DatabaseManager.TABLE_ALARM_TASK,
						new String[] { "_id", "dev_uid",
								"time_", "weeks", "flag"}, "dev_uid=?", new String[]{AlarmSetActivity.mCamera!=null?AlarmSetActivity.mCamera.getUID():""},
						null, null, "_id");

				while (cursor.moveToNext()) {

					long db_id = cursor.getLong(0);
					String dev_uid = cursor.getString(1);
					String time_ = cursor.getString(2);
					String weeks = cursor.getString(3);
					int flag = cursor.getInt(4);
					
					AlarmTask mTask = new AlarmTask(db_id, dev_uid, time_, weeks, flag);
					mTaskList.add(mTask);
				}

				cursor.close();
				db.close();
				
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						stopLoading();
						mAdapter.notifyDataSetChanged();
					}
				});
				
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private String getTime_(String time_)
	{
		StringBuffer s = new StringBuffer();
		String str[] = time_.split("-");
		if(str[0].equals("0"))
		{
			s.append("00:00");
		}
		else if(str[0].equals("1"))
		{
			s.append("01:00");
		}
		else if(str[0].equals("1"))
		{
			s.append("01:00");
		}
		else if(str[0].equals("2"))
		{
			s.append("02:00");
		}
		else if(str[0].equals("3"))
		{
			s.append("03:00");
		}
		else if(str[0].equals("4"))
		{
			s.append("04:00");
		}
		else if(str[0].equals("5"))
		{
			s.append("05:00");
		}
		else if(str[0].equals("6"))
		{
			s.append("06:00");
		}
		else if(str[0].equals("7"))
		{
			s.append("07:00");
		}
		else if(str[0].equals("8"))
		{
			s.append("08:00");
		}
		else if(str[0].equals("9"))
		{
			s.append("09:00");
		}
		else if(str[0].equals("10"))
		{
			s.append("10:00");
		}
		else if(str[0].equals("11"))
		{
			s.append("11:00");
		}
		else if(str[0].equals("12"))
		{
			s.append("12:00");
		}
		else if(str[0].equals("13"))
		{
			s.append("13:00");
		}
		else if(str[0].equals("14"))
		{
			s.append("14:00");
		}
		else if(str[0].equals("15"))
		{
			s.append("15:00");
		}
		else if(str[0].equals("16"))
		{
			s.append("16:00");
		}
		else if(str[0].equals("17"))
		{
			s.append("17:00");
		}
		else if(str[0].equals("18"))
		{
			s.append("18:00");
		}
		else if(str[0].equals("19"))
		{
			s.append("19:00");
		}
		else if(str[0].equals("20"))
		{
			s.append("20:00");
		}
		else if(str[0].equals("21"))
		{
			s.append("21:00");
		}
		else if(str[0].equals("22"))
		{
			s.append("22:00");
		}
		else if(str[0].equals("23"))
		{
			s.append("23:00");
		}
		
		s.append(" - ");
		
		if(str[1].equals("0"))
		{
			s.append("00:59");
		}
		else if(str[1].equals("1"))
		{
			s.append("01:59");
		}
		else if(str[1].equals("1"))
		{
			s.append("01:59");
		}
		else if(str[1].equals("2"))
		{
			s.append("02:59");
		}
		else if(str[1].equals("3"))
		{
			s.append("03:59");
		}
		else if(str[1].equals("4"))
		{
			s.append("04:59");
		}
		else if(str[1].equals("5"))
		{
			s.append("05:59");
		}
		else if(str[1].equals("6"))
		{
			s.append("06:59");
		}
		else if(str[1].equals("7"))
		{
			s.append("07:59");
		}
		else if(str[1].equals("8"))
		{
			s.append("08:59");
		}
		else if(str[1].equals("9"))
		{
			s.append("09:59");
		}
		else if(str[1].equals("10"))
		{
			s.append("10:59");
		}
		else if(str[1].equals("11"))
		{
			s.append("11:59");
		}
		else if(str[1].equals("12"))
		{
			s.append("12:59");
		}
		else if(str[1].equals("13"))
		{
			s.append("13:59");
		}
		else if(str[1].equals("14"))
		{
			s.append("14:59");
		}
		else if(str[1].equals("15"))
		{
			s.append("15:59");
		}
		else if(str[1].equals("16"))
		{
			s.append("16:59");
		}
		else if(str[1].equals("17"))
		{
			s.append("17:59");
		}
		else if(str[1].equals("18"))
		{
			s.append("18:59");
		}
		else if(str[1].equals("19"))
		{
			s.append("19:59");
		}
		else if(str[1].equals("20"))
		{
			s.append("20:59");
		}
		else if(str[1].equals("21"))
		{
			s.append("21:59");
		}
		else if(str[1].equals("22"))
		{
			s.append("22:59");
		}
		else if(str[1].equals("23"))
		{
			s.append("23:59");
		}
		
		return s.toString();
	}
	
	private String getWeeks(String week)
	{
		StringBuffer s = new StringBuffer();
		int i = 0;
		if(week.charAt(0) == '1')
		{
			s.append("Sun.\r");
			++i;
		}
		if(week.charAt(1) == '1')
		{
			s.append("Mon.\r");
			++i;
		}
		if(week.charAt(2) == '1')
		{
			s.append("Tues.\r");
			++i;
		}
		if(week.charAt(3) == '1')
		{
			s.append("Wed.\r");
			++i;
		}
		if(week.charAt(4) == '1')
		{
			s.append("Thur.\r");
			++i;
		}
		if(week.charAt(5) == '1')
		{
			s.append("Fri.\r");
			++i;
		}
		if(week.charAt(6) == '1')
		{
			s.append("Sat.\r");
			++i;
		}
		if(i == 7)
			return "Every Day";
		if(i == 0)
			return "Never";
		return s.toString();
	}
	
	public void loading(){

		if(mDialog == null)
			mDialog = new ProgressDialog(AlarmTaskListActivity.this);
		mDialog.setMessage(getText(R.string.dialog_wait));
		mDialog.show();
	}
	
	public void stopLoading(){
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
	}
	
	private void saveSetting()
	{
		loading();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(mTaskList != null)
					for (AlarmTask mTask : mTaskList) {
						if(mTask.flag == 0)
							continue;
						String str = mTask.weeks;
						String s[] = mTask.time_.split("-");
						int start = Integer.valueOf(s[0]);
						int end = Integer.valueOf(s[1]);
						for (int i = 0; i < str.length(); i++) {
							if(str.charAt(i) == '1')
							{
								for (int j = start; j <= end ;++j) {
									alarmData[i][j] = 1;
								}
							}
						}
					}
				if (AlarmSetActivity.mCamera != null)
				{
					if(!AlarmSetActivity.mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL))
					{
						return;
					}
					AlarmSetActivity.mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, MYAVIOCTRLDEFs.IOTYPE_NETVIOM_SET_ALARMTIME_REQ, sMsgNetviomSetAlarmTimeReq.parseContent(Camera.DEFAULT_AV_CHANNEL,1,alarmData));
				}
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						stopLoading();
						finish();
					}
				});
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
