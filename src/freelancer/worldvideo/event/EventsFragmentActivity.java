package freelancer.worldvideo.event;

import neutral.safe.chinese.R;
import freelancer.worldvideo.VideoPlaybackActivity;
import freelancer.worldvideo.fragment.FragmentIndicatorAlert;
import freelancer.worldvideo.fragment.FragmentIndicatorAlert.OnIndicateListener;
import freelancer.worldvideo.view.TitleView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;


public class EventsFragmentActivity extends Fragment
{
	public static Fragment[] mFragments = null;
	private View mParent = null;
	
	private TitleView mTitle = null;

	private Button btn_localpicture = null;
	private Button btn_notification = null;
	private Button btn_videofile = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.fragment_avtivity_events, container,false);
		initView();
		setFragmentIndicator(0);
		return mParent;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setFragmentIndicator(int whichIsDefault) {
		mFragments = new Fragment[2];
		mFragments[0] = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_local_picture);
		mFragments[1] = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_notification);
		getActivity().getSupportFragmentManager().beginTransaction().hide(mFragments[0])
				.hide(mFragments[1]).show(mFragments[whichIsDefault]).commit();
	}
	
	private void initView()
	{
		mTitle = (TitleView) mParent.findViewById(R.id.title);
		mTitle.setTitle(getText(R.string.replayfragment_title).toString());
		mTitle.hiddenLeftButton();
		mTitle.hiddenRightButton();
		
		btn_localpicture = (Button)mParent.findViewById(R.id.btn_localpicture);
		btn_notification = (Button)mParent.findViewById(R.id.btn_notification);
		btn_videofile = (Button)mParent.findViewById(R.id.btn_videofile);
		btn_localpicture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setFragmentIndicator(0);
				v.setBackgroundResource(R.drawable.tab_focus);
				btn_notification.setBackgroundResource(R.drawable.tab);
			}
		});
		btn_notification.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setFragmentIndicator(1);
				v.setBackgroundResource(R.drawable.tab_focus);
				btn_localpicture.setBackgroundResource(R.drawable.tab);
			}
		});
		btn_videofile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent palyback = new Intent();
				palyback.setClass(getActivity(),VideoPlaybackActivity.class);
				startActivity(palyback);
			}
		});
	}
}
