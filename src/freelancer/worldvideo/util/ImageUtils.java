/**
 * Tools.java
 */
package freelancer.worldvideo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

/**
 *@function: 图片工具类
 *@author jiangbing
 *@data: 2014-2-26
 */
public class ImageUtils 
{
	private static ImageUtils tools = new ImageUtils();  
	 /** 
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存 
     */  
    private static LruCache<String, Bitmap> mMemoryCache;  
    private static FileUtils fileUtils;  
    public static ImageUtils getInstance() {  
        if (tools == null) {  
            tools = new ImageUtils();  
            return tools;  
        }  
        return tools;  
    } 
    
    public static ImageUtils getInstance(Context context) {  
    	//获取系统分配给每个应用程序的最大内存，每个应用系统分配32M  
    	int maxMemory = (int) Runtime.getRuntime().maxMemory();    
    	int mCacheSize = maxMemory / 8;  
    	//给LruCache分配1/8 4M  
    	mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){  
    		//必须重写此方法，来测量Bitmap的大小  
    		@Override  
    		protected int sizeOf(String key, Bitmap value)
    		{  
    			return value.getRowBytes() * value.getHeight();  
    		}  
    	}; 
    	 fileUtils = new FileUtils(context);  
    	if (tools == null) {  
    		tools = new ImageUtils();  
    		return tools;  
    	}  
    	return tools;  
    } 
    
    public Bitmap decodeFile(File f){
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = false;
            
            fis = new FileInputStream(f);
            b =BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            //Decode with inSampleSize
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            fis = new FileInputStream(f);
//            b = BitmapFactory.decodeStream(fis, null, o2);
//            fis.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return b;
    }
  
    // 将byte[]转换成InputStream  
    public InputStream Byte2InputStream(byte[] b) {  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        return bais;  
    }  
  
    // 将InputStream转换成byte[]  
    public byte[] InputStream2Bytes(InputStream is) {  
        String str = "";  
        byte[] readByte = new byte[1024];  
        int readCount = -1;  
        try {  
            while ((readCount = is.read(readByte, 0, 1024)) != -1) 
            {  
                str += new String(readByte).trim();  
            }  
            return str.getBytes();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    // 将Bitmap转换成InputStream  
    public InputStream Bitmap2InputStream(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将Bitmap转换成InputStream  
    public InputStream Bitmap2InputStream(Bitmap bm, int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
    // 将InputStream转换成Bitmap  
    public Bitmap InputStream2Bitmap(InputStream is) {  
        return BitmapFactory.decodeStream(is);  
    }  
  
    // Drawable转换成InputStream  
    public InputStream Drawable2InputStream(Drawable d) {  
        Bitmap bitmap = this.drawable2Bitmap(d);  
        return this.Bitmap2InputStream(bitmap);  
    }  
  
    // InputStream转换成Drawable  
    public Drawable InputStream2Drawable(InputStream is) {  
        Bitmap bitmap = this.InputStream2Bitmap(is);  
        return this.bitmap2Drawable(bitmap);  
    }  
  
    // Drawable转换成byte[]  
    public byte[] Drawable2Bytes(Drawable d) {  
        Bitmap bitmap = this.drawable2Bitmap(d);  
        return this.Bitmap2Bytes(bitmap);  
    }  
  
    // byte[]转换成Drawable  
    public Drawable Bytes2Drawable(byte[] b) {  
        Bitmap bitmap = this.Bytes2Bitmap(b);  
        return this.bitmap2Drawable(bitmap);  
    }  
  
    // Bitmap转换成byte[]  
    public byte[] Bitmap2Bytes(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }  
  
    // byte[]转换成Bitmap  
    public Bitmap Bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
  
    // Drawable转换成Bitmap  
    public Bitmap drawable2Bitmap(Drawable drawable) {  
        Bitmap bitmap = Bitmap  
                .createBitmap(  
                        drawable.getIntrinsicWidth(),  
                        drawable.getIntrinsicHeight(),  
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
    }  
  
    // Bitmap转换成Drawable  
    public Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    }  
	/**
	 * @param urlpath
	 * @return Bitmap
	 * 根据图片url获取图片对象
	 */
	public Bitmap getBitMBitmap(String urlpath) {
		Bitmap map = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			map = BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	 /** 
     * 添加Bitmap到内存缓存 
     * @param key 
     * @param bitmap 
     */  
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {    
        if (getBitmapFromMemCache(key) == null && bitmap != null) 
        {   
            mMemoryCache.put(key, bitmap);    
        }    
    }    
       
    /** 
     * 从内存缓存中获取一个Bitmap 
     * @param key 
     * @return 
     */  
    public Bitmap getBitmapFromMemCache(String key) 
    {    
        return mMemoryCache.get(key);    
    }
    /** 
     * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步 
     * @param url 
     * @return 
     */  
    public Bitmap showCacheBitmap(String url){  
        if(getBitmapFromMemCache(url) != null)
        {  
            return getBitmapFromMemCache(url);  
        }
        else if(fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0)
        {  
            //从SD卡获取手机里面获取Bitmap  
            Bitmap bitmap = fileUtils.getBitmap(url);  
            //将Bitmap 加入内存缓存  
            addBitmapToMemoryCache(url, bitmap);  
            return bitmap;  
        }  
          
        return null;  
    }  
	/**
	 * @param urlpath
	 * @return Bitmap
	 * 根据url获取布局背景的对象
	 */
	public Drawable getDrawable(final String urlpath){
		final String subpath = urlpath.replaceAll("[^\\w]", ""); 
		Bitmap bitmap = null;
		Drawable d = null;
		try {
			bitmap = getInstance().showCacheBitmap(subpath);
			if(bitmap != null)
			{
				return getInstance().bitmap2Drawable(bitmap);
			}
			else
			{
				URL url = new URL(urlpath);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(3 * 1000);  
				conn.connect();
				InputStream in;
				in = conn.getInputStream();
				d = Drawable.createFromStream(in, subpath);
				bitmap = getInstance().drawable2Bitmap(d);
				 try {  
                     //保存在SD卡或者手机目录  
                     fileUtils.savaBitmap(subpath, bitmap);  
                 } catch (Exception e) {  
                     e.printStackTrace();  
                 }  
				getInstance().addBitmapToMemoryCache(subpath, bitmap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}
	
	/** 
     * 将文件生成位图 
     * @param path 
     * @return 
     * @throws IOException 
     */  
    public static BitmapDrawable getImageDrawable(String path)  throws Exception
    {  
        //打开文件  
        File file = new File(path);  
        if(!file.exists())  
        {  
            return null;  
        }  
          
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        InputStream in = null;
        byte[] bt = new byte[1024];  
        BitmapDrawable bd = null;
         try {
        	//得到文件的输入流  
             in = new FileInputStream(file);  
             //将文件读出到输出流中  
             int readLength = in.read(bt);  
             while (readLength != -1) {  
                 outStream.write(bt, 0, readLength);  
                 readLength = in.read(bt);  
             }  
             //转换成byte 后 再格式化成位图  
             byte[] data = outStream.toByteArray();  
             // Options 只保存图片尺寸大小，不保存图片到内存  
             BitmapFactory.Options opts = new BitmapFactory.Options();  
             opts.inSampleSize =3;  
             
             Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,opts);// 生成位图  
             if(bitmap != null)
            	 bd = new BitmapDrawable(bitmap);  
		} catch (Exception e) {
			System.gc();
			e.printStackTrace();
		}
         finally
         {
        	 outStream = null;
         }
        return bd;  
    }  
}
