/**
 * LocalMonitor.java
 */
package freelancer.worldvideo.videorecord;

import freelancer.worldvideo.util.ImageUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @function: 本地录像播放
 * @author jiangbing
 * @data: 2014-3-6
 */
public class LocalMonitor extends SurfaceView implements SurfaceHolder.Callback 
{

	private PointF mMidPoint = new PointF();
	private PointF mMidPointForCanvas = new PointF();

	private SurfaceHolder mSurHolder = null;

	private int vLeft, vTop, vRight, vBottom;

	private Rect mRectCanvas = new Rect();
	private Rect mRectMonitor = new Rect();

	private Bitmap mLastFrame;

	private int mCurVideoWidth = 0;
	private int mCurVideoHeight = 0;

	public boolean is_play = true;
	public int frame = 0;
	private ThreadRender mThreadRender = null;
	
	private Handler handler = null;
	
	private Paint mPaint = new Paint();
	
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}

	public LocalMonitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurHolder = getHolder();
		mSurHolder.addCallback(this);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		synchronized (this) {

			mRectMonitor.set(0, 0, width, height);
			mRectCanvas.set(0, 0, width, height);

			if (mCurVideoWidth == 0 || mCurVideoHeight == 0) {
				if (height < width) { 
					mRectCanvas.right = 4 * height / 3;
					mRectCanvas.offset((width - mRectCanvas.right) / 2, 0);
				} else { 
					mRectCanvas.bottom = 3 * width / 4;
					mRectCanvas.offset(0, (height - mRectCanvas.bottom) / 2);
				}
			} else {
				if ((mRectMonitor.bottom - mRectMonitor.top) < (mRectMonitor.right - mRectMonitor.left)) { // landscape
																											// layout
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.right = (int) (mRectMonitor.bottom * ratio);
					mRectCanvas.offset(
							(mRectMonitor.right - mRectCanvas.right) / 2, 0);
				} else { // portrait layout
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.bottom = (int) (mRectMonitor.right / ratio);
					mRectCanvas.offset(0,
							(mRectMonitor.bottom - mRectCanvas.bottom) / 2);
				}
			}

			vLeft = mRectCanvas.left;
			vTop = mRectCanvas.top;
			vRight = mRectCanvas.right;
			vBottom = mRectCanvas.bottom;

			parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
			parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
		}

	}

	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void attachCamera() {
		is_play = true;
		if (mThreadRender == null) {
			mThreadRender = new ThreadRender();
			mThreadRender.start();
		}
		
	}

	public void deattachCamera() {

		if (mThreadRender != null) {
			mThreadRender.stopThread();

			try {
				mThreadRender.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			mThreadRender = null;
		}
	}

	private void parseMidPoint(PointF point, float left, float top,
			float right, float bottom) {
		point.set((left + right) / 2, (top + bottom) / 2);
	}

	private class ThreadRender extends Thread {

		private boolean mIsRunningThread = false;

		public void stopThread() {
			is_play = false;
			mIsRunningThread = false;
		}

		@Override
		public void run() {

			mIsRunningThread = true;
			Canvas videoCanvas = null;
			while (mIsRunningThread) {
				if(is_play)
				{
					if(frame == RecordReplayActivity.list.size())
					{
						stopThread();
					}
					if(frame < RecordReplayActivity.list.size())
					{
						mLastFrame =ImageUtils.getInstance().decodeFile(RecordReplayActivity.list.get(frame));
						if(handler != null)
						{
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
						}
						++frame;
					}
					if (mLastFrame != null && !mLastFrame.isRecycled()) {
						try {
							videoCanvas = mSurHolder.lockCanvas();
							if (videoCanvas != null) {
								videoCanvas.drawColor(Color.BLACK);
								videoCanvas.drawBitmap(mLastFrame, null,
										mRectCanvas, mPaint);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (videoCanvas != null)
								mSurHolder.unlockCanvasAndPost(videoCanvas);
							mLastFrame.recycle();
							videoCanvas = null;
						}
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

}
