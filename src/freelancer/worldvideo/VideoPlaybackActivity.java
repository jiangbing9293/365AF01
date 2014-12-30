/**
 * VideoPlaybackActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import freelancer.worldvideo.fragment.FragmentIndicatorVideoPalyback;
import freelancer.worldvideo.fragment.FragmentIndicatorVideoPalyback.OnIndicateListener;
import freelancer.worldvideo.view.TitleView;

/**
 *@function: 录像回放
 *@author jiangbing
 *@data: 2014-3-4
 */
public class VideoPlaybackActivity extends FragmentActivity
{
	private TitleView mTitle;
	public static Fragment[] mFragments;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_palyback);
		UIApplication.getInstance().addActivity(this);  
		mTitle = (TitleView) findViewById(R.id.video_palyback_title);
		mTitle.setTitle(getText(R.string.playback_activty_title).toString());
		mTitle.setLeftButton("", new TitleView.OnLeftButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.hiddenRightButton();
		setFragmentIndicator(0);
	}
	
	private void setFragmentIndicator(int whichIsDefault) {
		mFragments = new Fragment[2];
		mFragments[0] = this.getSupportFragmentManager().findFragmentById(R.id.fragment_playback_local);
		mFragments[1] = this.getSupportFragmentManager().findFragmentById(R.id.fragment_playback_cam);
		this.getSupportFragmentManager().beginTransaction().hide(mFragments[0])
		.hide(mFragments[1]).show(mFragments[whichIsDefault]).commit();

		FragmentIndicatorVideoPalyback mIndicator = (FragmentIndicatorVideoPalyback) findViewById(R.id.indicator_palyback);
		FragmentIndicatorVideoPalyback.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			public void onIndicate(View v, int which) {
				getSupportFragmentManager().beginTransaction()
				.hide(mFragments[0]).hide(mFragments[1]).show(mFragments[which]).commit();
			}
		});
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		UIApplication.getInstance().finishActivity(this);
	}

}
