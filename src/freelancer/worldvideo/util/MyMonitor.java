/**
 * MyMonitor.java
 */
package freelancer.worldvideo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Monitor;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd;

/**
 *@function: 屏幕相关
 *@author jiangbing
 *@data: 2014-3-8
 */
public class MyMonitor extends Monitor
{
	private String name = null;
	private boolean is_pt = false;
	private boolean is_record = false;
	private String mDevUID = null;
	
	private Bitmap lastFrame = null;
	private ThreadRecord mThreadRecord = null;
	
	private Camera mCamera = null;
	/**
	 * @param context
	 * @param attrs
	 */
	public MyMonitor(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		super.surfaceChanged(holder, format, width, height);
		MyTool.println("surfaceChanged: width:"+width+",height:"+height);
	}

	public void attachCamera(Camera camera, int avChannel) 
	{
		mCamera = camera;
		super.attachCamera(camera, avChannel);
		if (mThreadRecord == null) {
			mThreadRecord = new ThreadRecord();
			mThreadRecord.start();
		}
	}

	public void deattachCamera()
	{
		super.deattachCamera();
		if (mThreadRecord != null) {
			mThreadRecord.stopThread();
			try {
				mThreadRecord.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mThreadRecord = null;
		}
	}
	
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp)
	{
		super.receiveFrameData(camera, avChannel, bmp);
		if(bmp != null)
			lastFrame = bmp;
	}
	
	private class ThreadRecord extends Thread {

		private boolean mIsRunningThread = false;
		private Object mWaitObjectForStopThread = new Object();

		public void stopThread() {
			mIsRunningThread = false;
			try {
				mWaitObjectForStopThread.notify();
			} catch (Exception e) {
			}
		}

		@Override
		public void run() {

			mIsRunningThread = true;
			while (mIsRunningThread) {
				if(lastFrame != null && !lastFrame.isRecycled())
				{
					try {
						if(!getRecord())
					    {
					    	name = getFileNameWithTime();
					    }
					    else
						{
					    	if(mDevUID != null)
					    		record(mDevUID, lastFrame);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}

				try {
					synchronized (mWaitObjectForStopThread) {
						mWaitObjectForStopThread.wait(33);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("===ThreadRender exit===");
		}
	}
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		if(getPT())
			return super.onFling(e1, e2, velocityX, velocityY);
		return false;
	}
	public void onLongPress(MotionEvent e) 
	{
		if (mCamera != null)
		{
			mCamera.sendIOCtrl(mCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND, SMsgAVIoctrlPtzCmd.parseContent((byte) AVIOCTRLDEFs.AVIOCTRL_LENS_ZOOM_OUT, (byte) 4, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
		}
	}
	public boolean onSingleTapUp(MotionEvent e) 
	{
		if (mCamera != null)
		{
			mCamera.sendIOCtrl(mCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND, SMsgAVIoctrlPtzCmd.parseContent((byte) AVIOCTRLDEFs.AVIOCTRL_LENS_ZOOM_IN, (byte) 4, (byte) 0, (byte) 0, (byte) 0, (byte) 0));
		}
		return false;
	}
	private String getFileNameWithTime() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);
		int mMilliSec = c.get(Calendar.MILLISECOND);

		StringBuffer sb = new StringBuffer();
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		return sb.toString();
	}
	
	public void setUID(String uid)
	{
		mDevUID = uid;
	}
	
	private boolean saveFile(String fileName, Bitmap frame) {

		if (fileName == null || fileName.length() <= 0)
			return false;

		boolean bErr = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName, false);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			 frame.compress(Bitmap.CompressFormat.PNG, 100, baos);  
		     InputStream is = new ByteArrayInputStream(baos.toByteArray());  
		     byte[] buffer=new byte[8192];  
             int count=0;  
             while ((count=is.read(buffer))>=0) {  
                 fos.write(buffer,0,count);  
             }  
			fos.flush();
			fos.close();

		} catch (Exception e) {
			bErr = true;
		} finally {

			if (bErr) {

				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		}
		return true;
	}
	/**
	 * 录像
	 *jiangbing
	 *2014-3-6
	 * @param uid
	 */
	private void record(String uid,Bitmap frame)
	{
		try {
			File rootFolder = new File(Environment
					.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/365afRecord/");
			File targetFolder1 = null;
			targetFolder1 =new File(rootFolder.getAbsolutePath() + "/" + uid);
			
			File targetFolder2 = null;
			targetFolder2 =new File(targetFolder1.getAbsolutePath() + "/." + name);
			
			if (!rootFolder.exists()) {
				try {
					rootFolder.mkdir();
				} catch (SecurityException se) {
					
				}
			}

			if (!targetFolder1.exists()) {

				try {
					targetFolder1.mkdir();
				} catch (SecurityException se) {
					
				}
			}
			
			if (!targetFolder2.exists()) {
				
				try {
					targetFolder2.mkdir();
				} catch (SecurityException se) {
					
				}
			}
			
			final String file = targetFolder2.getAbsoluteFile()
					+ "/" + getFileNameWithTime();

			saveFile(file, frame);
		} catch (Exception e) {
			MyTool.println("---MyMonitor录像出错----");
			e.printStackTrace();
		}

	}
	
	public void stopRecord()
	{
		is_record = false;
	}
	
	public void startRecord()
	{
		is_record = true;
	}
	
	public boolean getRecord()
	{
		return is_record;
	}
	
	public void closedPT()
	{
		is_pt = false;
	}
	
	public void openedPT()
	{
		is_pt = true;
	}
	
	public boolean getPT()
	{
		return is_pt;
	}

}
