package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import freelancer.worldvideo.util.MYAVIOCTRLDEFs;

public class AlarmNotificationActivity extends BaseActivity{
	
	private Button btn_cancel = null;
	private Button btn_submit = null;
	private TextView txt_name = null;
	private Bundle bundle = null;
	private AnimationDrawable anim = null;
	private ImageView img = null;
	
	private Ringtone r = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_notification);
		bundle = this.getIntent().getBundleExtra("cam_info_extra");
		initView();
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		r = RingtoneManager.getRingtone(getApplicationContext(), uri);
		r.play();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (r != null && r.isPlaying())
			r.stop();
		anim.stop();
	}
	
	private void initView()
	{
		btn_cancel = (Button)findViewById(R.id.alarm_notification_btn_cancel);
		btn_submit = (Button)findViewById(R.id.alarm_notification_btn_submit);
		img = (ImageView)findViewById(R.id.alarm_notification_img);
		txt_name = (TextView)findViewById(R.id.alarm_notification_txt);
		anim = (AnimationDrawable)img.getBackground();
		txt_name.setText(bundle.getString("dev_nickname"));
		anim.start();
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(UIApplication.mCamera != null)
				{
					UIApplication.mCamera.sendIOCtrl(0, MYAVIOCTRLDEFs.IOTYPE_ISMART_SET_REJECT_REQ, MYAVIOCTRLDEFs.sMsgIsmartSetRejectReq.parseContent(0));
				}
				if (r != null && r.isPlaying())
					r.stop();
				Intent view = new Intent();
				view.setClass(AlarmNotificationActivity.this, MainActivity.class);
				startActivity(view);
				finish();
			}
		});
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (r != null && r.isPlaying())
					r.stop();
				if(UIApplication.LOGIN)
				{
					Intent view = new Intent();
					view.setClass(AlarmNotificationActivity.this, VideoLiveActivity.class);
					view.putExtra("cam_info_extra", bundle);
					startActivity(view);
					finish();
				}
				else
				{
					Intent view = new Intent();
					view.setClass(AlarmNotificationActivity.this, LoginActivity.class);
					startActivity(view);
					finish();
				}
			}
		});
	}

}
