package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * 
 * @function: 列表主界面
 * @author jiangbing
 * @data: 2014-2-12
 */
public class MainActivity extends FragmentActivity {


	public static Fragment[] mFragments;

	private Button menu_camera = null;
	private Button menu_event = null;
	private Button menu_alarm = null;
	
	private int which = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
		UIApplication.getInstance().addActivity(this);
	}
	
	protected void onResumeFragments() {
		// TODO Auto-generated method stub
		super.onResumeFragments();
		changeFragment(which);
	}
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
	}
	private void init()
	{
		menu_camera = (Button)findViewById(R.id.menu_camera);
		menu_event = (Button)findViewById(R.id.menu_event);
		menu_alarm = (Button)findViewById(R.id.menu_alarm);
		menu_camera.setBackgroundColor(Color.rgb(0x0a, 0x81, 0xb7));
		menu_camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				which = 0;
				changeFragment(which);
			}
		});
		menu_event.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				which = 1;
				changeFragment(which);
			}
		});
		menu_alarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				which = 2;
				changeFragment(which);
			}
		});
		
		mFragments = new Fragment[3];
		mFragments[0] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_list);
		mFragments[1] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_replay);
		mFragments[2] = getSupportFragmentManager().findFragmentById(
				R.id.fragment_alert);
	}
	private void changeFragment(int f)
	{
		switch (f) {
		case 0:
			setFragmentIndicator(0);
			menu_camera.setBackgroundColor(Color.rgb(0x0a, 0x81, 0xb7));
			menu_event.setBackgroundColor(Color.alpha(0));
			menu_alarm.setBackgroundColor(Color.alpha(0));
			break;
		case 1:
			setFragmentIndicator(1);
			menu_event.setBackgroundColor(Color.rgb(0x0a, 0x81, 0xb7));
			menu_camera.setBackgroundColor(Color.alpha(0));
			menu_alarm.setBackgroundColor(Color.alpha(0));
			break;
		case 2:
			setFragmentIndicator(2);
			menu_alarm.setBackgroundColor(Color.rgb(0x0a, 0x81, 0xb7));
			menu_event.setBackgroundColor(Color.alpha(0));
			menu_camera.setBackgroundColor(Color.alpha(0));
			break;
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

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	 if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
		 KeyEvent.ACTION_DOWN)
		 {
		 		moveTaskToBack(true);
		 }
		return super.onKeyDown(keyCode, event);
	}

}
