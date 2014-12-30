/**
 * ListFragment.java
 */
package freelancer.worldvideo.fragment;

import neutral.safe.chinese.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import freelancer.worldvideo.fragment.FragmentIndicatorAlert.OnIndicateListener;
import freelancer.worldvideo.view.TitleView;

/**
 *@function: 报警界面
 *@author jiangbing
 *@data: 2014-2-15
 */
public class AlertFragment extends Fragment 
{
	private View mParent = null;
	private TitleView mTitle = null;
	
	public static Fragment[] mFragments = null;
	
	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static AlertFragment newInstance(int index) {
		AlertFragment f = new AlertFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_alert, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mTitle = (TitleView) mParent.findViewById(R.id.title);
		mTitle.setTitle(getString(R.string.alert_fragment_title));
		mTitle.hiddenLeftButton();
		mTitle.hiddenRightButton();
		setFragmentIndicator(0);
	}
	
	
	private void setFragmentIndicator(int whichIsDefault) {
		mFragments = new Fragment[2];
		mFragments[0] = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_alertcheck);
		mFragments[1] = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_alertset);
		getActivity().getSupportFragmentManager().beginTransaction().hide(mFragments[0])
				.hide(mFragments[1]).show(mFragments[whichIsDefault]).commit();

		FragmentIndicatorAlert mIndicator = (FragmentIndicatorAlert) mParent.findViewById(R.id.indicator_alert);
		FragmentIndicatorAlert.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				getActivity().getSupportFragmentManager().beginTransaction()
						.hide(mFragments[0]).hide(mFragments[1]).show(mFragments[which]).commit();
			}
		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
