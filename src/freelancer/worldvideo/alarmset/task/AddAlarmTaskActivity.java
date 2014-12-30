package freelancer.worldvideo.alarmset.task;

import neutral.safe.chinese.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.alarmset.AlarmSetActivity;
import freelancer.worldvideo.database.DatabaseManager;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;
/**
 * 
 * 添加任务
 * @author jiangbing
 *
 */
public class AddAlarmTaskActivity extends BaseActivity {

	private TitleView mTitle = null;
	
	private Button btnStartSub = null;
	private Button btnStartAdd = null;
	private EditText txtStart = null;
	
	private Button btnEndSub = null;
	private Button btnEndAdd = null;
	private EditText txtEnd = null;
	
	private Button btnMon = null;
	private Button btnTues = null;
	private Button btnWed = null;
	private Button btnThur = null;
	private Button btnFri = null;
	private Button btnSat = null;
	private Button btnSun = null;
	
	private int start = 0;
	private int end = 23;
	private int week[] = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmtask_add);
		week = new int[7];
		for (int i = 0; i < week.length; i++) {
			week[i] = 1;
		}
		
		initView();
		initWeek();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initView()
	{
		mTitle = (TitleView)findViewById(R.id.alarmtask_add_title);
		
		btnStartSub = (Button)findViewById(R.id.alarmtask_start_sub);
		btnStartAdd = (Button)findViewById(R.id.alarmtask_start_add);
		txtStart = (EditText)findViewById(R.id.alarmtask_start_value);
		
		btnEndSub = (Button)findViewById(R.id.alarmtask_end_sub);
		btnEndAdd = (Button)findViewById(R.id.alarmtask_end_add);
		txtEnd = (EditText)findViewById(R.id.alarmtask_end_value);
		
		btnMon = (Button)findViewById(R.id.btn_mon);
		btnTues = (Button)findViewById(R.id.btn_tues);
		btnWed = (Button)findViewById(R.id.btn_wed);
		btnThur = (Button)findViewById(R.id.btn_thur);
		btnFri = (Button)findViewById(R.id.btn_fri);
		btnSat = (Button)findViewById(R.id.btn_sat);
		btnSun = (Button)findViewById(R.id.btn_sun);
		
		initListener();
	}
	private void initListener()
	{
		mTitle.setTitle("报警计划");
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				
				DatabaseManager manager = new DatabaseManager(AddAlarmTaskActivity.this);
				long db_id = manager.addAlarmTask(AlarmSetActivity.mCamera.getUID(), getTime_(), getWeek(), 1);
				System.out.println(" Add AlarmTask to database id "+db_id);
				AlarmTask mTask = new AlarmTask(db_id, AlarmSetActivity.mCamera.getUID(), getTime_(), getWeek(), 1);
				AlarmTaskListActivity.mTaskList.add(mTask);
				finish();
			}
		});
		
		btnStartSub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				start = Integer.valueOf(txtStart.getText().toString());
				if(start > 0)
				{
					--start;
				}
				else
				{
					start = 0;
				}
				txtStart.setText(""+start);
			}
		});
		btnStartAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				start = Integer.valueOf(txtStart.getText().toString());
				if(start < end)
				{
					++start;
				}
				else
				{
					start = 23;
				}
				txtStart.setText(""+start);
			}
		});
		
		btnEndSub.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				end = Integer.valueOf(txtEnd.getText().toString());
				if(end > start)
				{
					--end;
				}
				else
				{
					end = 0;
				}
				txtEnd.setText(""+end);
			}
		});
		btnEndAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				end = Integer.valueOf(txtEnd.getText().toString());
				if(end < 23)
				{
					++end;
				}
				else
				{
					end = 23;
				}
				txtEnd.setText(""+end);
			}
		});
		
		btnMon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[1] == 1)
				{
					week[1] = 0;
				}
				else
				{
					week[1] = 1;
				}
				initWeek();
			}
		});
		
		btnTues.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[2] == 1)
				{
					week[2] = 0;
				}
				else
				{
					week[2] = 1;
				}
				initWeek();
			}
		});
		btnWed.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[3] == 1)
				{
					week[3] = 0;
				}
				else
				{
					week[3] = 1;
				}
				initWeek();
			}
		});
		btnThur.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[4] == 1)
				{
					week[4] = 0;
				}
				else
				{
					week[4] = 1;
				}
				initWeek();
			}
		});
		btnFri.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[5] == 1)
				{
					week[5] = 0;
				}
				else
				{
					week[5] = 1;
				}
				initWeek();
			}
		});
		btnSat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[6] == 1)
				{
					week[6] = 0;
				}
				else
				{
					week[6] = 1;
				}
				initWeek();
			}
		});
		btnSun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(week[0] == 1)
				{
					week[0] = 0;
				}
				else
				{
					week[0] = 1;
				}
				initWeek();
			}
		});
	}
	
	private void initWeek()
	{
		if(week[0] == 1)
		{
			btnSun.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnSun.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[1] == 1)
		{
			btnMon.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnMon.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[6] == 1)
		{
			btnSat.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnSat.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[2] == 1)
		{
			btnTues.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnTues.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[3] == 1)
		{
			btnWed.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnWed.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[4] == 1)
		{
			btnThur.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnThur.setBackgroundResource(R.drawable.icon_unselected);
		}
		
		if(week[5] == 1)
		{
			btnFri.setBackgroundResource(R.drawable.icon_selected);
		}
		else
		{
			btnFri.setBackgroundResource(R.drawable.icon_unselected);
		}
	}
	
	private String getWeek()
	{
		StringBuffer sBuf = new StringBuffer();
		for (int i = 0; i < week.length; i++) {
			sBuf.append(week[i]);
		}
		return ""+sBuf.toString();
	}
	
	private String getTime_()
	{
		StringBuffer s = new StringBuffer();
		s.append(start);
		s.append("-");
		s.append(end);
		return s.toString();
	}
	
}
