package freelancer.worldvideo.videorecord;

import neutral.safe.chinese.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.event.NotificationListActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 * 录像时间段
 * @author jiangbing
 *
 */
public class RecordTimeActivity extends BaseActivity
{
	
	private TitleView mTitle = null;
	private LinearLayout search_hour = null;
	private LinearLayout search_day = null;
	private LinearLayout search_week = null;
	private LinearLayout search_month = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record_search);
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CamRecordActivity.search = false;
		EventRecordActivity.search = false;
	}
	
	private void initView()
	{
		mTitle = (TitleView) findViewById(R.id.record_search_title);
		search_hour = (LinearLayout) findViewById(R.id.layout_search_hour);
		search_day = (LinearLayout) findViewById(R.id.layout_search_day);
		search_week = (LinearLayout) findViewById(R.id.layout_search_week);
		search_month = (LinearLayout) findViewById(R.id.layout_search_month);
		mTitle.setTitle(R.string.record_search_title);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.hiddenRightButton();
		search_hour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CamRecordActivity.search) {
					CamRecordActivity.from = 60;
				}
				if (EventRecordActivity.search) {
					EventRecordActivity.from = 60;
				}
				if (NotificationListActivity.search) {
					NotificationListActivity.start = 60 * 60;
				}
				finish();
			}
		});
		search_day.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CamRecordActivity.search) {
					CamRecordActivity.from = 60 * 24;
				}
				if (EventRecordActivity.search) {
					EventRecordActivity.from = 60 *24;
				}
				if (NotificationListActivity.search) {
					NotificationListActivity.start = 60 * 60 * 24;
				}
				finish();
			}
		});
		search_week.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CamRecordActivity.search) {
					CamRecordActivity.from = 60 * 24 * 7;
				}
				if (EventRecordActivity.search) {
					EventRecordActivity.from = 60 *24 * 7;
				}
				if (NotificationListActivity.search) {
					NotificationListActivity.start = 60 * 60 *24 *7;
				}
				finish();
			}
		});
		search_month.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CamRecordActivity.search) {
					CamRecordActivity.from = 60 * 24 * 30;
				}
				if (EventRecordActivity.search) {
					EventRecordActivity.from = 60 *24 * 30;
				}
				if (NotificationListActivity.search) {
					NotificationListActivity.start = 60 * 60 * 24 *30;
				}
				finish();
			}
		});
	}
}
