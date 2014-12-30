/**
 * LocalPictureActivity.java
 */
package freelancer.worldvideo;

import java.io.File;
import java.util.ArrayList;

import neutral.safe.chinese.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import freelancer.worldvideo.event.ReplayFragment;
import freelancer.worldvideo.util.ImageUtils;
import freelancer.worldvideo.util.TipHelper;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

/**
 * @function: 本地图片
 * @author jiangbing
 * @data: 2014-2-28
 */
public class LocalPictureActivity extends BaseActivity {
	private ProgressDialog loadingDialog = null;
	private TitleView mTitle = null;
	private GridView picturelist = null;
	public MyAdapter adapter = null;
	private ArrayList<String> files = new ArrayList<String>();
	private ArrayList<String> deleteFiles = new ArrayList<String>();
	private boolean DELETE = false;
	private boolean LONG_PRESS = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_localpicture);
		
		mTitle = (TitleView) findViewById(R.id.localpicture_title);
		if (ReplayFragment.name != null) {
			mTitle.setTitle(ReplayFragment.name);
		} else {
			mTitle.setTitle(getString(R.string.replay_localpicture));
		}
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}

		});

		mTitle.hiddenRightButton();
		picturelist = (GridView) findViewById(R.id.localpicture_list);
		adapter = new MyAdapter(this);
		picturelist.setAdapter(adapter);
		picturelist
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						TipHelper.VibrateOne(LocalPictureActivity.this);
						LONG_PRESS = true;
						if(DELETE)
						{
							DELETE = false;
							mTitle.hiddenRightButton();
						}
						else
						{
							DELETE = true;
							mTitle.setRightButtonBg(R.drawable.icon_delete_withe, new OnRightButtonClickListener() {
							
								@Override
								public void onClick(View button) {
									DELETE = false;
									deleteThread();
								}
							});
						}
						return false;
					}
				});
		picturelist
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						if(LONG_PRESS)
						{
							LONG_PRESS = false;
							return;
						}
						
						if(DELETE)
						{
							if (deleteFiles.contains(files.get(position))) {
								arg1.setBackgroundColor(Color.TRANSPARENT);
								deleteFiles.remove(files.get(position));
							} else {
								arg1.setBackgroundColor(Color.BLUE);
								deleteFiles.add(files.get(position));
							}
							return;
						}
						String f[] = new String[files.size()];
						for (int i = 0; i < files.size(); i++) {
							f[i] = files.get(i);
						}
						Intent imageView = new Intent();
						imageView.setClass(LocalPictureActivity.this,
								ImageViewActivity.class);
						imageView.putExtra("files", f);
						imageView.putExtra("pos", position);
						LocalPictureActivity.this.startActivity(imageView);
						finish();
					}
				});
		getLocalPicture();
	}

	public void onResume() {
		super.onResume();
		getLocalPicture();
	}

	/**
	 * 获取本地图片 jiangbing 2014-3-4
	 */
	private void getLocalPicture() {

		String f[] = getIntent().getStringArrayExtra("files");
		files.clear();
		for (int i = 0; i < f.length; i++) {
			if(f != null)
				files.add(f[i]);
		}
		picturelist.invalidate();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 删除文件夹及文件夹下的所有 jiangbing 2014-3-4
	 * 
	 * @param file
	 */
	private void deleteFolder(String path) {
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFolder(childFiles[i].getAbsolutePath());
			}
			file.delete();
		}
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (files == null || files.size() == 0)
				return 0;
			return files.size();
		}

		@Override
		public Object getItem(int position) {
			if (files == null || files.size() == 0)
				return null;
			return files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(
						R.layout.adapter_localpicturegrid, null);
				holder.img = (ImageView) convertView
						.findViewById(R.id.grid_img);
				// holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
				holder.title = (TextView) convertView
						.findViewById(R.id.grid_txt);
				holder.title.setTextSize(12);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			try {
				if (files != null && files.get(position) != null) {
					holder.img.setImageDrawable(ImageUtils.getImageDrawable(files
							.get(position)));
					holder.title.setText(files.get(position).substring(42));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return convertView;

		}
	}

	public void loadingDelete(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		loadingDialog.setTitle("delete...");
		loadingDialog.setMessage("wait...");
		loadingDialog.show();
	}

	public void stopLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}

	private Handler deleteHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				stopLoading();
				if (files == null || files.size() == 0) {
					Intent main = new Intent();
					main.setClass(LocalPictureActivity.this, MainActivity.class);
					main.putExtra("which", 1);
					startActivity(main);
				}
				deleteFiles.clear();
				picturelist.invalidate();
				adapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 删除摄像机线程 jiangbing 2014-3-2
	 */
	private void deleteThread() {
		loadingDelete(LocalPictureActivity.this);
		Thread delete = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < deleteFiles.size(); i++) {
						deleteFolder(deleteFiles.get(i));
						files.remove(deleteFiles.get(i));
					}
					Message msg = new Message();
					msg.what = 1;
					deleteHandle.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		delete.start();
	}

}
