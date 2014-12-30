/**
 * BaseActivity.java
 */
package freelancer.worldvideo;

import android.app.Activity;
import android.os.Bundle;

/**
 *@function: 基础Activity 用于程序退出
 *@author jiangbing
 *@data: 2014-3-28
 */
public class BaseActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UIApplication.getInstance().addActivity(this);
    }
 
    @Override
    protected void onDestroy(){
        super.onDestroy();
        UIApplication.getInstance().finishActivity(this);
    }
	 
}
