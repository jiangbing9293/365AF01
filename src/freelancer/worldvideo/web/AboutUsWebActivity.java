/**
 * HelpWebActivity.java
 */
package freelancer.worldvideo.web;

import neutral.safe.chinese.R;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 * @function: 关于我们
 * @author jiangbing
 * @data: 2014-3-12
 */
public class AboutUsWebActivity extends BaseActivity {
	private TitleView mTitle;
	private WebView webView;
	private ProgressDialog dialog = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		initTitle();
		initWeb();
	}

	private void initWeb() {
		webView = (WebView) findViewById(R.id.contectweb);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		
		webView.loadUrl(WebConfig.CONTECT_WEB);
		if(dialog == null)
			dialog = new ProgressDialog(this);
		dialog.setTitle(getText(R.string.toast_loading_page));
		dialog.setMessage(getText(R.string.dialog_wait));
		dialog.show();
//		dialog = ProgressDialog.show(AboutUsWebActivity.this,null,getText(R.string.toast_loading_page));  
		webView.setWebViewClient(new WebViewClient() 
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(AboutUsWebActivity.this, getText(R.string.toast_phone_error) + description,
						Toast.LENGTH_SHORT).show();
			}
			public void onPageFinished(WebView view,String url)  
            {  
				if(dialog != null)
				{
	                dialog.dismiss(); 
	                dialog = null;
				} 
            }
		});
	}

	private void initTitle() {
		mTitle = (TitleView) findViewById(R.id.contectweb_title);
		mTitle.setTitle(getText(R.string.aboutus_activty_title).toString());
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}
		});
		mTitle.hiddenRightButton();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			if(dialog != null)
			{
                dialog.dismiss(); 
                dialog = null;
			}
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
