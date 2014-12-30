/**
 * FragmentIndicator.java
 */
package freelancer.worldvideo.fragment;

import neutral.safe.chinese.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *@function: 报警顶部工具栏
 *@author jiangbing
 *@data: 2014-2-6
 */
public class FragmentIndicatorAlert extends LinearLayout implements OnClickListener 
{
	private int mDefaultIndicator = 0;
	private static int mCurIndicator;
	private static View[] mIndicators;
	private OnIndicateListener mOnIndicateListener;
	private static final String TAG_TEXT_0 = "text_tag_0";
	private static final String TAG_TEXT_1 = "text_tag_1";
	private static final int COLOR_UNSELECT = Color.WHITE;
	private static final int COLOR_SELECT = Color.WHITE;

	private FragmentIndicatorAlert(Context context) {
		super(context);
	}

	public FragmentIndicatorAlert(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCurIndicator = mDefaultIndicator;
		setOrientation(LinearLayout.HORIZONTAL);
		init();
	}

	private View createIndicator(int stringResID, int stringColor,String textTag) {
		LinearLayout view = new LinearLayout(getContext());
		view.setOrientation(LinearLayout.VERTICAL);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		view.setGravity(Gravity.CENTER);

		TextView textView = new TextView(getContext());
		textView.setTag(textTag);
		textView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		textView.setTextColor(stringColor);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		textView.setText(stringResID);
		textView.setGravity(Gravity.CENTER);
		view.addView(textView);

		return view;

	}

	private void init() {
		mIndicators = new View[2];
		mIndicators[0] = createIndicator(R.string.alert_check, COLOR_SELECT, TAG_TEXT_0);
		mIndicators[0].setBackgroundResource(R.drawable.tab_focus);
		mIndicators[0].setTag(Integer.valueOf(0));
		mIndicators[0].setOnClickListener(this);
		addView(mIndicators[0]);
		mIndicators[1] = createIndicator(R.string.alert_set, COLOR_SELECT, TAG_TEXT_1);
		mIndicators[1].setBackgroundResource(R.drawable.tab);
		mIndicators[1].setTag(Integer.valueOf(1));
		mIndicators[1].setOnClickListener(this);
		addView(mIndicators[1]);
	}
	
	public static void setIndicator(int which) {
		// clear previous status.
		mIndicators[mCurIndicator].setBackgroundResource(R.drawable.tab);
		TextView prevText;
		switch(mCurIndicator) {
		case 0:
			prevText = (TextView) mIndicators[mCurIndicator].findViewWithTag(TAG_TEXT_0);
			prevText.setTextColor(COLOR_UNSELECT);
			break;
		case 1:
			prevText = (TextView) mIndicators[mCurIndicator].findViewWithTag(TAG_TEXT_1);
			prevText.setTextColor(COLOR_UNSELECT);
			break;
		}
		
		// update current status.
		mIndicators[which].setBackgroundResource(R.drawable.tab_focus);
		TextView currText;
		switch(which) {
		case 0:
			currText = (TextView) mIndicators[which].findViewWithTag(TAG_TEXT_0);
			currText.setTextColor(COLOR_SELECT);
			break;
		case 1:
			currText = (TextView) mIndicators[which].findViewWithTag(TAG_TEXT_1);
			currText.setTextColor(COLOR_SELECT);
			break;
		}
		
		mCurIndicator = which;
	}

	public interface OnIndicateListener {
		public void onIndicate(View v, int which);
	}

	public void setOnIndicateListener(OnIndicateListener listener) {
		mOnIndicateListener = listener;
	}

	@Override
	public void onClick(View v) 
	{
		if (mOnIndicateListener != null)
		{
			int tag = (Integer) v.getTag();
			switch (tag) 
			{
			case 0:
				if (mCurIndicator != 0) {
					mOnIndicateListener.onIndicate(v, 0);
					setIndicator(0);
				}
				break;
			case 1:
				if (mCurIndicator != 1) {
					mOnIndicateListener.onIndicate(v, 1);
					setIndicator(1);
				}
				break;
			}
		}
	}
}
