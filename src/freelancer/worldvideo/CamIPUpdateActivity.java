/**
 * CamIPSetActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import freelancer.worldvideo.camip.HeaderDefine;
import freelancer.worldvideo.camip.LanDeviceInfo;
import freelancer.worldvideo.camip.MyCamIP;
import freelancer.worldvideo.util.CodeUtils;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 *@function: 摄像机IP配置
 *@author jiangbing
 *@data: 2014-3-7
 */
public class CamIPUpdateActivity extends BaseActivity implements View.OnFocusChangeListener
{
	private ProgressDialog loadingDialog;
	
	private TitleView mTitle;
	
	private EditText edt_cam_name = null;
	private EditText edt_cam_ip = null;
	private EditText edt_cam_port = null;
	private EditText edt_cam_mac = null;
	private EditText t_camgate = null;
	private EditText t_camgcode = null;
	private EditText t_camgdns1 = null;
	private EditText t_camgdns2 = null;
	private EditText t_camrtsp = null;
	
	private LinearLayout cam_name = null;
	private LinearLayout cam_ip = null;
	private LinearLayout cam_code = null;
	private LinearLayout cam_gate = null;
	private LinearLayout cam_dns1 = null;
	private LinearLayout cam_dns2 = null;
	private LinearLayout cam_httpport = null;
	private LinearLayout cam_rtspport = null;
	private LinearLayout cam_mac = null;
	
	private LanDeviceInfo mOldDev = new LanDeviceInfo();
	private LanDeviceInfo mnewDev = new LanDeviceInfo();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camipupdate);
		init();
		initTitle();
		getDeviceInfo(UIApplication.getDevIPtUid(ParameterSetActivity.mDevice.UID));
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		MyCamIP.getSearchClosed();
	}
	
	private void init()
	{
		cam_name = (LinearLayout)findViewById(R.id.cam_name);
		cam_ip = (LinearLayout)findViewById(R.id.cam_ip);
		cam_code = (LinearLayout)findViewById(R.id.cam_code);
		cam_gate = (LinearLayout)findViewById(R.id.cam_gate);
		cam_dns1 = (LinearLayout)findViewById(R.id.cam_dns1);
		cam_dns2 = (LinearLayout)findViewById(R.id.cam_dns2);
		cam_httpport = (LinearLayout)findViewById(R.id.cam_httpport);
		cam_rtspport = (LinearLayout)findViewById(R.id.cam_rtspport);
		cam_mac = (LinearLayout)findViewById(R.id.cam_mac);
		
		edt_cam_name = (EditText)findViewById(R.id.edt_cam_name);
		edt_cam_ip = (EditText)findViewById(R.id.edt_cam_ip);
		edt_cam_port = (EditText)findViewById(R.id.edt_cam_port);
		edt_cam_mac = (EditText)findViewById(R.id.edt_cam_mac);
		t_camgate = (EditText)findViewById(R.id.t_camgate);
		t_camgcode = (EditText)findViewById(R.id.t_camgcode);
		t_camgdns1 = (EditText)findViewById(R.id.t_camgdns1);
		t_camgdns2 = (EditText)findViewById(R.id.t_camgdns2);
		t_camrtsp = (EditText)findViewById(R.id.t_camrtsp);
		
		edt_cam_name.setOnFocusChangeListener(this);
		edt_cam_ip.setOnFocusChangeListener(this);
		edt_cam_port.setOnFocusChangeListener(this);
		edt_cam_mac.setOnFocusChangeListener(this);
		t_camgate.setOnFocusChangeListener(this);
		t_camgcode.setOnFocusChangeListener(this);
		t_camgdns1.setOnFocusChangeListener(this);
		t_camgdns2.setOnFocusChangeListener(this);
		t_camrtsp.setOnFocusChangeListener(this);
	}
	
	private void initData()
	{
		if(mOldDev != null && mOldDev.device != null)
		{
			if(mOldDev.device[HeaderDefine.NAME].length() > 0)
				edt_cam_name.setText(CodeUtils.decode(mOldDev.device[HeaderDefine.NAME]));
			edt_cam_ip.setText(mOldDev.device[HeaderDefine.IP]);
			edt_cam_port.setText(mOldDev.device[HeaderDefine.HTTP_PORT]);
			edt_cam_mac.setText(mOldDev.device[HeaderDefine.MAC]);
			t_camgate.setText(mOldDev.device[HeaderDefine.GATE]);
			t_camgcode.setText(mOldDev.device[HeaderDefine.SNCODE]);
			t_camgdns1.setText(mOldDev.device[HeaderDefine.DNS1]);
			t_camgdns2.setText(mOldDev.device[HeaderDefine.DNS2]);
			t_camrtsp.setText(mOldDev.device[HeaderDefine.RTSP_PORT]);
		}
	}
	
	/**
	 * 初始化顶部工具栏
	 *jiangbing
	 *2014-3-7
	 */
	private void initTitle()
	{
		mTitle = (TitleView)findViewById(R.id.camipupdate_title);
		mTitle.setTitle(getText(R.string.camipset_activity_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener(){

			@Override
			public void onClick(View button) {
				CamIPUpdateActivity.this.finish();
			}
		});
		mTitle.setRightButtonBg(R.drawable.icon_save, new TitleView.OnRightButtonClickListener() {
			
			@Override
			public void onClick(View button) {
				if(mOldDev == null)
					return;
				if(edt_cam_name.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(edt_cam_ip.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(t_camgcode.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(t_camgate.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(t_camgdns1.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(t_camgdns2.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(edt_cam_port.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(t_camrtsp.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(edt_cam_mac.getText().toString().trim().equals(""))
				{
					Toast.makeText(CamIPUpdateActivity.this, getText(R.string.camipset_activity_notnull), Toast.LENGTH_SHORT).show();
					return;
				}
				mnewDev.device[HeaderDefine.MAC] = edt_cam_mac.getText().toString().trim();
				mnewDev.device[HeaderDefine.NAME] = CodeUtils.encode(edt_cam_name.getText().toString().trim());
				mnewDev.device[HeaderDefine.IP] = edt_cam_ip.getText().toString().trim();
				mnewDev.device[HeaderDefine.SNCODE] = t_camgcode.getText().toString().trim();
				mnewDev.device[HeaderDefine.GATE] = t_camgate.getText().toString().trim();
				mnewDev.device[HeaderDefine.DNS1] = t_camgdns1.getText().toString().trim();
				mnewDev.device[HeaderDefine.DNS2] = t_camgdns2.getText().toString().trim();
				mnewDev.device[HeaderDefine.HTTP_PORT] = edt_cam_port.getText().toString().trim();
				mnewDev.device[HeaderDefine.RTSP_PORT] = t_camrtsp.getText().toString().trim();
				UIApplication.updateIP(mOldDev.device[HeaderDefine.IP].trim() ,mnewDev.device[HeaderDefine.IP].trim());
				updateIP(mOldDev,mnewDev);
			}
		});
	}
	public void loading(Activity act){
		
		if(loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setMessage(getText(R.string.dialog_wait));
		loadingDialog.show();
	}
	
	public void stopLoading(){
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
	}
	private Handler handle = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){
				stopLoading();
				MyCamIP.getIPSetClosed();
				Intent view = new Intent();
				view.setClass(CamIPUpdateActivity.this, MainActivity.class);
				startActivity(view);
			}
			if(msg.what == 2)
			{
				initData();
				MyCamIP.getSearchClosed();
				stopLoading();
			}
			if(msg.what == 3)
			{
				MyCamIP.getSearchClosed();
				stopLoading();
			}
		}
	};
	
	private void getDeviceInfo(final String ip)
	{
		loading(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(ip != null)
					mOldDev = MyCamIP.getCam(ip);
				if(mOldDev != null)
				{
					Message msg = new Message();
					msg.what = 2;
					handle.sendMessage(msg);
				}
				else
				{
					Message msg = new Message();
					msg.what = 3;
					handle.sendMessage(msg);
				}
				
				try {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void updateIP(final LanDeviceInfo oldDev, final LanDeviceInfo newDev)
	{
		loading(this);
		Thread update = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					MyCamIP.updateIp(oldDev, newDev);
					Message msg = new Message();
					msg.what = 1;
					handle.sendMessage(msg);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		update.start();
	}

	private void setBg(int index)
	{
		cam_name.setBackgroundResource(R.drawable.bg_disable);
		cam_ip.setBackgroundResource(R.drawable.bg_disable);
		cam_code.setBackgroundResource(R.drawable.bg_disable);
		cam_gate.setBackgroundResource(R.drawable.bg_disable);
		cam_dns1.setBackgroundResource(R.drawable.bg_disable);
		cam_dns2.setBackgroundResource(R.drawable.bg_disable);
		cam_httpport.setBackgroundResource(R.drawable.bg_disable);
		cam_rtspport.setBackgroundResource(R.drawable.bg_disable);
		cam_mac.setBackgroundResource(R.drawable.bg_disable);
		switch (index) {
		case 1:
			cam_name.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 2:
			cam_ip.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 3:
			cam_code.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 4:
			cam_gate.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 5:
			cam_dns1.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 6:
			cam_dns2.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 7:
			cam_httpport.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 8:
			cam_rtspport.setBackgroundResource(R.drawable.bg_enable);
			break;
		case 9:
			cam_mac.setBackgroundResource(R.drawable.bg_enable);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch(v.getId())
		{
		case R.id.edt_cam_name:
			setBg(1);
			edt_cam_name.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.edt_cam_ip:
			setBg(2);
			edt_cam_ip.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.edt_cam_port:
			setBg(7);
			edt_cam_port.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.edt_cam_mac:
			setBg(9);
			edt_cam_mac.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.t_camgate:
			setBg(4);
			t_camgate.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.t_camgcode:
			setBg(3);
			t_camgcode.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.t_camgdns1:
			setBg(5);
			t_camgdns1.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.t_camgdns2:
			setBg(6);
			t_camgdns2.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		case R.id.t_camrtsp:
			setBg(8);
			t_camrtsp.setTextColor(Color.rgb(0x63, 0x8c, 0xaa));
			break;
		}
	}
}
