/**
 * LoginActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import freelancer.worldvideo.net.GetServerData;

/**
 *@function: 注册界面
 *@author jiangbing
 *@data: 2014-2-9
 */
public class RegisterActivity extends BaseActivity
{
	private GetServerData data = null;
	String message = "";
	String mobile = "";
	String upswd = "";
	private Handler x = null;
	
	private EditText register_telephone = null;
	private EditText register_pwd = null;
	private EditText register_repwd = null;
	
	private Button register_register = null;
	private Button register_cancel = null;
	private Button register_login = null;
	private Button register_associated = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		register_telephone = (EditText)findViewById(R.id.register_username);
		register_pwd = (EditText)findViewById(R.id.register_pwd);
		register_repwd = (EditText)findViewById(R.id.register_repwd);
		register_register = (Button)findViewById(R.id.register_register);
		register_cancel = (Button)findViewById(R.id.register_cancel);
		register_login = (Button)findViewById(R.id.menu_login);
		register_associated = (Button)findViewById(R.id.menu_associated);
		
		register_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent login = new Intent();
				login.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(login);
			}
		});
		
		register_associated.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(RegisterActivity.this, "under building", Toast.LENGTH_SHORT).show();
			}
		});
		
		register_register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mobile = register_telephone.getText().toString();
				String str = register_pwd.getText().toString();
				upswd = register_repwd.getText().toString();
				if(mobile == null || mobile.equals(""))
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.login_cativity_input_phone), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(str == null || str.equals(""))
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.login_cativity_input_password), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(upswd == null || upswd.equals(""))
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.register_activity_submit_password), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(mobile.length() > 15)
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.register_activity_vil_phone), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(str.trim().length() < 4)
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.register_activity_long_pwd), Toast.LENGTH_SHORT).show();
					return;
				}
				else if(!str.trim().equals(upswd.trim()))
				{
					Toast.makeText(RegisterActivity.this, getText(R.string.register_activity_same_pwd), Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(new DataThread()).start();
			}
		});
		register_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		x = new Handler()
		{
			public void handleMessage(Message msg) 
	        {
				if(msg.what == 1)
				{
					String str = (String)msg.obj;
					if(str.equals("1"))
					{
						Toast.makeText(RegisterActivity.this, getText(R.string.register_activity_registe_success), Toast.LENGTH_SHORT).show();
						Intent main = new Intent();
						main.setClass(RegisterActivity.this, LoginActivity.class);
						startActivity(main);
					}
					else
					{
						Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
						return;
					}
				}
	        	super.handleMessage(msg);  
	        }
	        	 
		};
	}
	
	class DataThread implements Runnable {
		@Override
		public void run() 
		{
			Message msg = new Message();
			try {
				data = new GetServerData();
				message = data.reg(mobile, upswd);
				if (message != null) 
				{
					msg.what = 1;
					msg.obj = message;
				}
				
				x.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
}
