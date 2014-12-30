/**
 * RecordReplay.java
 */
package freelancer.worldvideo.videorecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import neutral.safe.chinese.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import freelancer.worldvideo.BaseActivity;
import freelancer.worldvideo.videorecord.popuwindow.GroupAdapter;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;
import freelancer.worldvideo.view.TitleView.OnRightButtonClickListener;

/**
 * @function: 本地录像回放界面
 * @author jiangbing
 * @data: 2014-2-28
 */
public class RecordReplayActivity extends BaseActivity {
	private ProgressDialog loadingDialog = null;
	private TitleView mTitle = null;
	private Button btn_play = null;
	private Button btn_preview = null;
	private Button btn_next = null;

	private SeekBar progressBar = null;

	private LocalMonitor monitor = null;
	public static List<File> list = new ArrayList<File>();
	private int position = -1;
	private File file = null;

	private int currentPosition = 0;

	private PopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	private ImageView recordDelete = null;

	private Handler mainHandler = null;
	private String camName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recordreplay);
		mainHandler = new Handler();
		position = getIntent().getIntExtra("position", -1);
		camName = getIntent().getStringExtra("camName");
		if (position != -1) {
			currentPosition = position;
			file = (File) LocalRecordActivity.list.get(currentPosition);
		}
		monitor = (LocalMonitor) findViewById(R.id.replay_monitor);
		initThread();
		initProgress();
		btn_play = (Button) findViewById(R.id.play);
		btn_preview = (Button) findViewById(R.id.btn_preview);
		btn_next = (Button) findViewById(R.id.btn_next);
		/**
		 * 播放
		 */
		btn_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (monitor.is_play == false) {
					monitor.is_play = true;
					progressBar.setProgress(monitor.frame);
					monitor.attachCamera();
					btn_play.setBackgroundResource(R.drawable.icon_pause);
					Toast.makeText(RecordReplayActivity.this, "Play",
							Toast.LENGTH_SHORT).show();
				} else {
					if (monitor != null) {
						monitor.is_play = false;
						btn_play.setBackgroundResource(R.drawable.btn_play);
						Toast.makeText(RecordReplayActivity.this, "Pause",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		/**
		 * 上一个
		 */
		btn_preview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (monitor != null) {
					monitor.frame = 0;
					monitor.deattachCamera();
					if (--currentPosition >= 0
							&& currentPosition < LocalRecordActivity.list
									.size()) {
						file = (File) LocalRecordActivity.list
								.get(currentPosition);
						initThread();
						initProgress();
					} else {
						currentPosition = 0;
						Toast.makeText(RecordReplayActivity.this, "First",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		/**
		 * 下一个
		 */
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (monitor != null) {
					monitor.frame = 0;
					monitor.deattachCamera();
					if (++currentPosition < LocalRecordActivity.list.size()
							&& currentPosition >= 0) {
						file = (File) LocalRecordActivity.list
								.get(currentPosition);
						initThread();
						initProgress();
					} else {
						currentPosition = LocalRecordActivity.list.size();
						Toast.makeText(RecordReplayActivity.this, "Lasted",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		mTitle = (TitleView) findViewById(R.id.recordreplay_title);
		mTitle.setTitle(camName);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {

				if (monitor != null) {
					monitor.frame = 0;
					monitor.deattachCamera();
				}
				RecordReplayActivity.this.finish();
			}

		});
		mTitle.setRightButtonBg(R.drawable.icon_localplayback,
				new OnRightButtonClickListener() {

					@Override
					public void onClick(View button) {
						showWindow(button);
					}
				});
	}

	private void initProgress() {
		progressBar = (SeekBar) findViewById(R.id.progress);

		progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				monitor.frame = progress;
			}
		});
	}

	public void loading(Activity act) {

		if (loadingDialog == null)
			loadingDialog = new ProgressDialog(act);
		try {
			loadingDialog.setMessage(getString(R.string.dialog_wait));
			loadingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}

	private Handler initHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				stopLoading();
				if (monitor != null) {
					monitor.attachCamera();
				}
			}
			if (msg.what == 2) {
				progressBar.setProgress(monitor.frame);
				if (monitor.frame == progressBar.getMax() - 1) {
					progressBar.setProgress(progressBar.getMax());
					if (monitor != null) {
						monitor.frame = 0;
						monitor.deattachCamera();
					}
					Toast.makeText(RecordReplayActivity.this, "Stop",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private void initThread() {
		loading(this);
		monitor.setHandler(initHandle);
		Thread init = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					init();
					Message msg = new Message();
					msg.what = 1;
					initHandle.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		init.start();
	}

	private void init()
	{
		try {
			list.clear();
			File[] files = file.listFiles();
			if (files != null) {
				for (int j = 0; j < files.length; j++) {
					File f = new File(files[j].getAbsolutePath());
					list.add(f);
				}
				progressBar.setMax(list.size());
			} else {
				Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (monitor != null) {
			monitor.frame = 0;
			monitor.deattachCamera();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showWindow(View parent) {

		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.popuwindow_list, null);

			lv_group = (ListView) view.findViewById(R.id.lvGroup);
			recordDelete = (ImageView) view
					.findViewById(R.id.local_record_delete);
			GroupAdapter groupAdapter = new GroupAdapter(this,
					LocalRecordActivity.list);
			lv_group.setAdapter(groupAdapter);
			// 创建一个PopuWidow对象
			popupWindow = new PopupWindow(view, 300, 500);
		}

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(parent, -10, -20);

		lv_group.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				monitor.frame = 0;
				monitor.deattachCamera();
				currentPosition = position;
				if (currentPosition >= 0
						&& currentPosition < LocalRecordActivity.list.size()) {
					file = (File) LocalRecordActivity.list.get(currentPosition);
					initThread();
					initProgress();
				}
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
			}
		});
		recordDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
				new AlertDialog.Builder(RecordReplayActivity.this)
						.setTitle(getString(R.string.dialog_delete_alert))
						.setMessage("Determine clear "+LocalRecordActivity.list
								.get(currentPosition).getName().substring(1)+" files")
						.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										deleteThread(LocalRecordActivity.list
												.get(currentPosition));
									}
								}).setNegativeButton("NO", null).show();

			}
		});
	}

	/**
	 * 删除 jiangbing 2014-3-2
	 */
	private void deleteThread(final File deletefile) {
		loading(this);
		if (monitor != null) {
			monitor.frame = 0;
			monitor.deattachCamera();
		}
		Thread delete = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					deleteFolder(deletefile);
					mainHandler.post(new Runnable() {

						@Override
						public void run() {
							stopLoading();
							LocalRecordActivity.list.remove(currentPosition);

							if (monitor != null) {
								if (++currentPosition < LocalRecordActivity.list
										.size() && currentPosition >= 0) {
									file = (File) LocalRecordActivity.list
											.get(currentPosition);
									initThread();
									initProgress();
								} else {
									currentPosition = LocalRecordActivity.list
											.size() - 1;
									Toast.makeText(RecordReplayActivity.this,
											"Lasted", Toast.LENGTH_SHORT)
											.show();
								}
							}

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		delete.start();
	}

	/**
	 * 删除文件夹及文件夹下的所有 jiangbing 2014-3-4
	 * 
	 * @param file
	 */

	private void deleteFolder(File file) {
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
				deleteFolder(childFiles[i]);
			}
			file.delete();
		}
	}
}
