/**
 * ImageViewActivity.java
 */
package freelancer.worldvideo;

import neutral.safe.chinese.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;
import freelancer.worldvideo.util.ImageUtils;
import freelancer.worldvideo.view.TitleView;
import freelancer.worldvideo.view.TitleView.OnLeftButtonClickListener;

/**
 * @function: 图片浏览
 * @author jiangbing
 * @data: 2014-3-4
 */
public class ImageViewActivity extends BaseActivity implements ViewFactory,
		OnTouchListener {
	private ImageSwitcher mImageSwitcher;
	private int currentPosition;
	private float downX;
	private String files[];
	private int position = 0;

	private TitleView mTitle = null;
	private int index = 50;
	@SuppressWarnings("deprecation")
	private GestureDetector gestureDetector = new GestureDetector(
			new OnGestureListener() {

				public boolean onDown(MotionEvent event) {
					downX = event.getX();
					return false;
				}

				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					return false;
				}

				public void onLongPress(MotionEvent event) {
					
				}

				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					return false;
				}

				public void onShowPress(MotionEvent event) {

				}

				public boolean onSingleTapUp(MotionEvent event) {
					float lastX = event.getX();
					if (lastX > downX) {
						if (currentPosition > 0) {
							mImageSwitcher.setInAnimation(AnimationUtils
									.loadAnimation(getApplication(),
											R.anim.left_in));
							mImageSwitcher.setOutAnimation(AnimationUtils
									.loadAnimation(getApplication(),
											R.anim.right_out));
							currentPosition--;
							if (files[currentPosition] != null) {
								try {
									mImageSwitcher.setImageDrawable(ImageUtils
											.getImageDrawable(files[currentPosition]));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mTitle.setTitle(files[currentPosition].substring(index));
								return true;
							} else if (currentPosition > 0) {
								try {
									mImageSwitcher.setImageDrawable(ImageUtils
											.getImageDrawable(files[--currentPosition]));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mTitle.setTitle(files[currentPosition].substring(index));

							}
						}
					}

					if (lastX < downX) {
						if (currentPosition < files.length - 1) {
							mImageSwitcher.setInAnimation(AnimationUtils
									.loadAnimation(getApplication(),
											R.anim.right_in));
							mImageSwitcher.setOutAnimation(AnimationUtils
									.loadAnimation(getApplication(),
											R.anim.lift_out));
							currentPosition++;
							if (files[currentPosition] != null) {
								try {
									mImageSwitcher.setImageDrawable(ImageUtils
											.getImageDrawable(files[currentPosition]));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mTitle.setTitle(files[currentPosition].substring(index));
								return true;
							} else if (currentPosition < files.length - 1) {
								try {
									mImageSwitcher.setImageDrawable(ImageUtils
											.getImageDrawable(files[++currentPosition]));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mTitle.setTitle(files[currentPosition].substring(index));
							}
						}
					}
					return false;
				}

			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_imageview);
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.imageview);
		mImageSwitcher.setFactory(this);
		mImageSwitcher.setOnTouchListener(this);
		currentPosition = 0;
		init();
	}

	public void init() {
		position = getIntent().getIntExtra("pos", -1);
		files = getIntent().getStringArrayExtra("files");
		mTitle = (TitleView) findViewById(R.id.imageViewTitle);
		mTitle.setLeftButton("", new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
				Intent localpic = new Intent();
				localpic.setClass(ImageViewActivity.this, LocalPictureActivity.class);
				localpic.putExtra("files", files);
				startActivity(localpic);
			}

		});
		
		currentPosition = position;
		if (position != -1) {

			if (files != null && files.length > 0) {
				try {
					mImageSwitcher.setImageDrawable(ImageUtils
							.getImageDrawable(files[currentPosition]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			index = files[currentPosition].length() - 23;
			mTitle.setTitle(files[currentPosition].substring(index));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		Intent localpic = new Intent();
		localpic.setClass(this, LocalPictureActivity.class);
		localpic.putExtra("files", files);
		startActivity(localpic);
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float lastX = event.getX();
			if (lastX > downX) {
				if (currentPosition > 0) {
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.left_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(), R.anim.right_out));
					currentPosition--;
					if (files[currentPosition] != null) {
						try {
							mImageSwitcher.setImageDrawable(ImageUtils
									.getImageDrawable(files[currentPosition]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mTitle.setTitle(files[currentPosition].substring(index));
						return true;
					} else if (--currentPosition > 0) {
						try {
							mImageSwitcher.setImageDrawable(ImageUtils
									.getImageDrawable(files[currentPosition]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mTitle.setTitle(files[currentPosition].substring(index));

					}
				}
			}

			if (lastX < downX) {
				if (currentPosition < files.length - 1) {
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.right_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(), R.anim.lift_out));
					currentPosition++;
					if (files[currentPosition] != null) {
						try {
							mImageSwitcher.setImageDrawable(ImageUtils
									.getImageDrawable(files[currentPosition]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mTitle.setTitle(files[currentPosition].substring(index));
						return true;
					} else if (currentPosition < files.length - 1) {
						try {
							mImageSwitcher.setImageDrawable(ImageUtils
									.getImageDrawable(files[++currentPosition]));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mTitle.setTitle(files[currentPosition].substring(index));
					}
				}
			}
		}

			break;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ViewSwitcher.ViewFactory#makeView()
	 */
	@Override
	public View makeView() {
		final ImageView i = new ImageView(this);
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ScaleType.FIT_XY);
		i.setOnTouchListener(ImageViewActivity.this);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT,  getWindowManager().getDefaultDisplay().getHeight()/2));
		return i;
	}

}
