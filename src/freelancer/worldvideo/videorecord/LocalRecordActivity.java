/**
 * LocalRecordActivity.java
 */
package freelancer.worldvideo.videorecord;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 *@function: 本地录像
 *@author jiangbing
 *@data: 2014-3-5
 */

public class LocalRecordActivity extends BaseActivity
{
	private ProgressDialog loadingDialog = null;
	private TitleView mTitle = null;
	
	private ListView locallist = null;
	private MyAdapter adapter = null;
	
	public static List<File> list = Collections.synchronizedList(new ArrayList<File>());
	
	private String uid = null;
	private String camName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_localrecord);
		
		uid = getIntent().getStringExtra("uid");
		camName = getIntent().getStringExtra("camName");
		
		mTitle = (TitleView) findViewById(R.id.localrecord_title);
		mTitle.setTitle(camName);
		mTitle.setLeftButton("", new OnLeftButtonClickListener(){

			@Override
			public void onClick(View button) {
				LocalRecordActivity.this.finish();
			}
			
		});
		mTitle.hiddenRightButton();
		init();
		locallist = (ListView)findViewById(R.id.localrecord_list);
		adapter = new MyAdapter(this);
		locallist.setAdapter(adapter);
		locallist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent replay = new Intent();
				replay.setClass(LocalRecordActivity.this, RecordReplayActivity.class);
				replay.putExtra("position", position);
				replay.putExtra("camName", camName);
				startActivity(replay);
			}
		});
		locallist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long arg3) {
				new AlertDialog.Builder(LocalRecordActivity.this).setTitle(getString(R.string.dialog_delete_alert)).setMessage(getString(R.string.dialog_delete_record))
				.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					deleteThread((File)list.get(position));
					list.remove(position);
					adapter.notifyDataSetInvalidated();
				}}).setNegativeButton("Cancel",null).show();
				return false;
			}
		});
	}
	public void onResume()
	{
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	private void init()
	{
		list.clear();
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/365afRecord/" + uid);
		File[] files = folder.listFiles();
		if(files != null)
		{
			 for (int j = files.length-1; j >=0 ; j--)
			 {
				 list.add(files[j]);
			 }
		}
		else
		{
			 Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void loadingDelete(Activity act){
		
		if(loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle("delete...");
		loadingDialog.setMessage("wait...");
		loadingDialog.show();
	}
 
	
	public void stopLoading(){
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
	}
	
	private Handler deleteHandle = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what == 1){ 
				stopLoading();
			}
		}
	};
	
	/**
	 * 删除
	 *jiangbing
	 *2014-3-2
	 */
	private void deleteThread(final File deletefile){
		loadingDelete(LocalRecordActivity.this);
		Thread delete = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					deleteFolder(deletefile);
					Message msg = new Message();
					msg.what = 1;
					deleteHandle.sendMessage(msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		delete.start();
	}
	
	/**
	 * 删除文件夹及文件夹下的所有
	 *jiangbing
	 *2014-3-4
	 * @param file
	 */
	
	private void deleteFolder(File file)
	{
		if (file.isFile()) {  
            file.delete();  
            return;  
        }  
  
        if(file.isDirectory()){  
            File[] childFiles = file.listFiles();  
            if (childFiles == null || childFiles.length == 0) {  
                file.delete();  
                return;  
            }  
      
            for (int i = 0; i < childFiles.length; i++) {  
            	deleteFolder(childFiles[i]);  
            }  
            file.delete();  
        }  
	}
	
	public final class ViewHolder {
		public ImageView img = null;
		public TextView record_type = null;
		public TextView record_time = null;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.adapter_record, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.record_more);
				holder.record_type = (TextView) convertView
						.findViewById(R.id.record_type);
				holder.record_time = (TextView) convertView
						.findViewById(R.id.record_time);
				holder.record_type.setText(getString(R.string.localrecord_activty_title));
				
				convertView.setTag(holder);

			} else {
				
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.record_time.setText(((File)list.get(position)).getName().substring(1));
			
			return convertView;
		}

	}
}
