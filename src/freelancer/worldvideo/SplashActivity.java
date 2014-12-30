/**
 * SplashActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @function: 启动界面
 * @author jiangbing
 * @data: 2014-1-24
 */
public class SplashActivity extends BaseActivity {

	Handler x = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		x = new Handler()
		{
			public void handleMessage(Message msg) 
	        {
				if(UIApplication.LOGIN)
				{
					startActivity(new Intent(getApplication(), MainActivity.class));
				}
				else
				{
					startActivity(new Intent(getApplication(), LoginActivity.class));
				}
				SplashActivity.this.finish();
	        	super.handleMessage(msg);  
	        }
		};
		new Thread(new DataThread()).start();
	}
	
	class DataThread implements Runnable {
		@Override
		public void run() 
		{
			Message msg = new Message();
			long temp = System.currentTimeMillis();
			try {
				if(System.currentTimeMillis() - temp < 500)
				{
					Thread.sleep(500);
				}
				msg.what = 1;
				x.sendMessage(msg);
				return;
				
			} catch (Exception e)
			{
				msg.what = 3;
				x.sendMessage(msg);
				e.printStackTrace();
			}
		}

	}
}
