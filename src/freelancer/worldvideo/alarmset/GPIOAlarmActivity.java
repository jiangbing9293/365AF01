package freelancer.worldvideo.alarmset;

import neutral.safe.chinese.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

public class GPIOAlarmActivity extends FragmentActivity
{
	public static Fragment[] mFragments = null;
	
	private TitleView mTitle = null;
	
	private Button gpio_input = null;
	private Button gpio_pir = null;
	private Button gpio_output = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_set_gpio);
		initView();
		setFragmentIndicator(0);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void initView()
	{
		mFragments = new Fragment[3];
		mFragments[0] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_io);
		mFragments[1] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_output);
		mFragments[2] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_pir);
		gpio_input = (Button) findViewById(R.id.gpio_input);
		gpio_pir = (Button) findViewById(R.id.gpio_pir);
		gpio_output = (Button) findViewById(R.id.gpio_output);
		mTitle = (TitleView) findViewById(R.id.alarm_set_gpio_title);
		mTitle.setTitle(R.string.alarmset_activity_title);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.hiddenRightButton();
		
		gpio_input.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setFragmentIndicator(0);
				gpio_input.setBackgroundResource(R.drawable.tab_focus);
				gpio_pir.setBackgroundResource(R.drawable.tab);
				gpio_output.setBackgroundResource(R.drawable.tab);
			}
		});
		gpio_pir.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setFragmentIndicator(2);
				gpio_pir.setBackgroundResource(R.drawable.tab_focus);
				gpio_input.setBackgroundResource(R.drawable.tab);
				gpio_output.setBackgroundResource(R.drawable.tab);
			}
		});
		gpio_output.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setFragmentIndicator(1);
				gpio_output.setBackgroundResource(R.drawable.tab_focus);
				gpio_pir.setBackgroundResource(R.drawable.tab);
				gpio_input.setBackgroundResource(R.drawable.tab);
			}
		});
		if(AlarmSetActivity.pir)
		{
			gpio_pir.setVisibility(View.VISIBLE);
		}
		else
		{
			gpio_pir.setVisibility(View.GONE);
		}
	}
	/**
	 * 初始化fragment
	 */
	private void setFragmentIndicator(int whichIsDefault) {
		getSupportFragmentManager().beginTransaction().hide(mFragments[0])
				.hide(mFragments[1]).hide(mFragments[2])
				.show(mFragments[whichIsDefault]).commit();
	}
}
