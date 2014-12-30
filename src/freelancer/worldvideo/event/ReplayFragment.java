/**
 * ListFragment.java
 */
package freelancer.worldvideo.event;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutral.safe.chinese.R;
import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import freelancer.worldvideo.LocalPictureActivity;
import freelancer.worldvideo.UIApplication;
import freelancer.worldvideo.VideoPlaybackActivity;
import freelancer.worldvideo.util.ImageUtils;
import freelancer.worldvideo.view.TitleView;

/**
 *@function: 回放界面
 *@author jiangbing
 *@data: 2014-2-15
 */
public class ReplayFragment extends Fragment 
{
	private ProgressDialog loadingDialog = null;
	
	private View mParent = null;
	
	private ListView picturelist = null;
	public MyAdapter adapter;
	public List<Map<String, Object>> locpic = new ArrayList<Map<String,Object>>();
	
	public static String name ="";
	/*
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static ReplayFragment newInstance(int index) {
		ReplayFragment f = new ReplayFragment();

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
		mParent = inflater.inflate(R.layout.fragment_replay, container, false);
		return mParent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		picturelist = (ListView)mParent.findViewById(R.id.localpicture_list);
		adapter = new MyAdapter(getActivity());
		picturelist.setAdapter(adapter);
		picturelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long arg3) {
				if(locpic.get(position).get("files") == null || ((File[])locpic.get(position).get("files")).length == 0)
					return false;
				new AlertDialog.Builder(ReplayFragment.this.getActivity()).setTitle(getText(R.string.dialog_delete_alert)).setMessage(getText(R.string.dialog_delete_local_picture))
				.setPositiveButton(getText(R.string.dialog_submit), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					deleteThread((File)locpic.get(position).get("file"));
					locpic.get(position).remove("file");
					locpic.get(position).remove("files");
					adapter.notifyDataSetInvalidated();
				}}).setNegativeButton(getText(R.string.dialog_cancel),null).show();
				return false;
			}
		});
		picturelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(locpic.get(position).get("files") == null || ((File[])locpic.get(position).get("files")).length == 0)
					return;
				File f[] = (File[])locpic.get(position).get("files");
				String files[] = new String[f.length];
				for (int i = 0; i < f.length; i++) {
					if(f[i] != null)
						files[i] = f[i].getAbsolutePath();
				}
				Intent imageView = new Intent();
				imageView.setClass(getActivity(), LocalPictureActivity.class);
				imageView.putExtra("files",files);
				name = locpic.get(position).get("name").toString();
				startActivity(imageView);
			}
		});
		getLocalPicture();
	}

	public void onResume()
	{
		super.onResume();
		getLocalPicture();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 获取本地图片
	 *jiangbing
	 *2014-3-4
	 */
	private void getLocalPicture()
	{
		locpic.clear();
		if(UIApplication.DeviceList != null)
		{
			for(int i = 0; i < UIApplication.DeviceList.size();++i)
			{
				 File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/" + UIApplication.DeviceList.get(i).UID);
				 File[] files = folder.listFiles();
				 if(files != null && files.length > 0)
				 {
					 Map<String, Object> m = new HashMap<String, Object>();
					 m.put("file", folder);
					 m.put("files", files);
					 m.put("name", UIApplication.DeviceList.get(i).NickName);
					 m.put("size", files.length+"");
					 locpic.add(m);
				 }
				 else
				 {
					 Map<String, Object> m = new HashMap<String, Object>();
					 m.put("file", folder);
					 m.put("files", files);
					 m.put("name", UIApplication.DeviceList.get(i).NickName);
					 m.put("size", "0");
					 locpic.add(m);
				 }
			}
		}
		adapter.notifyDataSetInvalidated();
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
		public ImageView img;
		public TextView title;
		public TextView info;
		public ImageView viewBtn;
	}
	
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return locpic.size();
		}

		@Override
		public Object getItem(int position) {
			return locpic.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.adapter_localpicturelist, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.localpicture_img_splash);
//				holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
				holder.title = (TextView) convertView
						.findViewById(R.id.localpicture_txt_name1);
				holder.info = (TextView) convertView
						.findViewById(R.id.localpicture_txt_name2);
				holder.viewBtn = (ImageView) convertView
						.findViewById(R.id.localpicture_more);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText((String)locpic.get(position).get("name"));
			holder.info.setText((String)locpic.get(position).get("size"));
			if(locpic.get(position).get("files") != null && ((File[])locpic.get(position).get("files")).length > 0)
			{
				try {
					holder.img.setImageDrawable(ImageUtils.getImageDrawable(((File[])locpic.get(position).get("files"))[0].getAbsolutePath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				holder.img.setImageResource(R.drawable.pic_video_default);
				holder.info.setText("0");
			}
			return convertView;
		}
	}
	public void loadingDelete(Activity act){
		
		if(loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		
		loadingDialog.setTitle(R.string.dialog_deleting);
		loadingDialog.setMessage(getString(R.string.dialog_wait));
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
	 * 删除摄像机线程
	 *jiangbing
	 *2014-3-2
	 */
	private void deleteThread(final File deletefile){
		loadingDelete(getActivity());
		Thread delete = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					deleteFolder(deletefile);
					Message msg = new Message();
					msg.what = 1;
					deleteHandle.sendMessage(msg);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		delete.start();
	}
}
