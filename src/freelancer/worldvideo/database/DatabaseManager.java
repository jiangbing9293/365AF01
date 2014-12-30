package freelancer.worldvideo.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class DatabaseManager {

	public static final String TABLE_DEVICE = "device";
	public static final String TABLE_SEARCH_HISTORY = "search_history";
	public static final String TABLE_SNAPSHOT = "snapshot";
	public static final String TABLE_ALARM_TASK = "alarm_task";
	public static final String s_GCM_PHP_URL = "http://push.iotcplatform.com/apns/apns.php";
	public static final String s_Package_name = "com.tutk.p2pcamlive.2(Android)";
	//public static final String s_GCM_sender = "935793047540";
	
	public static final String GCM_Server_URL = "http://www.boxkam.com:8080/365PushServer/apns";
	public static final String Package_name = "cn.365af.01";
	public static final String GCM_sender = "159970081077";
	public static String GCM_token = "";
	
	public static String s_GCM_token = "";
	public static int n_mainActivity_Status = 0;
	
	private DatabaseHelper mDbHelper;

	public DatabaseManager(Context context) {
		mDbHelper = new DatabaseHelper(context);
	}

	public SQLiteDatabase getReadableDatabase() {
		return mDbHelper.getReadableDatabase();
	}

	public long addDevice(String dev_nickname, String dev_uid, String dev_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_nickname", dev_nickname);
		values.put("dev_uid", dev_uid);
		values.put("dev_name", dev_name);
		values.put("dev_pwd", dev_pwd);
		values.put("view_acc", view_acc);
		values.put("view_pwd", view_pwd);
		values.put("event_notification", event_notification);
		values.put("camera_channel", channel);

		long ret = db.insertOrThrow(TABLE_DEVICE, null, values);
		db.close();

		return ret;
	}
	
	public long addAlarmTask(String dev_uid, String time_, String weeks, int flag) {
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("time_", time_);
		values.put("weeks", weeks);
		values.put("flag", flag);
		
		long ret = db.insertOrThrow(TABLE_ALARM_TASK, null, values);
		db.close();
		
		return ret;
	}

	public long addSnapshot(String dev_uid, String file_path, long time) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("file_path", file_path);
		values.put("time", time);

		long ret = db.insertOrThrow(TABLE_SNAPSHOT, null, values);
		db.close();

		return ret;
	}

	public void updateDeviceInfoByDBID(long db_id, String dev_uid, String dev_nickname, String dev_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("dev_nickname", dev_nickname);
		values.put("dev_name", dev_name);
		values.put("dev_pwd", dev_pwd);
		values.put("view_acc", view_acc);
		values.put("view_pwd", view_pwd);
		values.put("event_notification", event_notification);
		values.put("camera_channel", channel);
		db.update(TABLE_DEVICE, values, "_id = '" + db_id + "'", null);
		db.close();
	}

	public void updateDeviceAskFormatSDCardByUID(String dev_uid, boolean askOrNot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ask_format_sdcard", askOrNot ? 1 : 0);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void updateDeviceChannelByUID(String dev_uid, int channelIndex) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("camera_channel", channelIndex);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid, Bitmap snapshot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snapshot", getByteArrayFromBitmap(snapshot));
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public void updateDeviceSnapshotByUID(String dev_uid, byte[] snapshot) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("snapshot", snapshot);
		db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}
	
	public void updateAlarmTaskById(long _id,String time_,String weeks,int flag)
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time_", time_);
		values.put("weeks", weeks);
		values.put("flag", flag);
		db.update(TABLE_ALARM_TASK, values, "_id = " + _id , null);
		db.close();
	}

	public void removeDeviceByUID(String dev_uid) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_DEVICE, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}
	
	public void removeAlarmTaskById(long _id) {
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_ALARM_TASK, "_id = " + _id , null);
		db.close();
	}

	public void removeSnapshotByUID(String dev_uid) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_SNAPSHOT, "dev_uid = '" + dev_uid + "'", null);
		db.close();
	}

	public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {

		if (bitmap != null && !bitmap.isRecycled()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0, bos);
			return bos.toByteArray();
		} else {
			return null;
		}
	}

	public static BitmapFactory.Options getBitmapOptions(int scale) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = scale;

		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return options;
	}

	public static Bitmap getBitmapFromByteArray(byte[] byts) {

		InputStream is = new ByteArrayInputStream(byts);
		return BitmapFactory.decodeStream(is, null, getBitmapOptions(2));
	}

	public long addSearchHistory(String dev_uid, int eventType, long start_time, long stop_time) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dev_uid", dev_uid);
		values.put("search_event_type", eventType);
		values.put("search_start_time", start_time);
		values.put("search_stop_time", stop_time);

		long ret = db.insertOrThrow(TABLE_SEARCH_HISTORY, null, values);
		db.close();

		return ret;
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DB_FILE = "IOTCamViewer.db";
		private static final int DB_VERSION = 7;

		private static final String SQLCMD_CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_nickname			NVARCHAR(30) NULL, " + "dev_uid				VARCHAR(20) NULL, " + "dev_name				VARCHAR(30) NULL, " + "dev_pwd				VARCHAR(30) NULL, "
				+ "view_acc				VARCHAR(30) NULL, " + "view_pwd				VARCHAR(30) NULL, " + "event_notification 	INTEGER, " + "ask_format_sdcard		INTEGER," + "camera_channel			INTEGER, " + "snapshot				BLOB" + ");";

		private static final String SQLCMD_CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_SEARCH_HISTORY + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "search_event_type	INTEGER, " + "search_start_time	INTEGER, " + "search_stop_time	INTEGER" + ");";

		private static final String SQLCMD_CREATE_TABLE_SNAPSHOT = "CREATE TABLE IF NOT EXISTS " + TABLE_SNAPSHOT + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "file_path			VARCHAR(80), " + "time				INTEGER" + ");";
		
		private static final String SQLCMD_CREATE_TABLE_ALARM_TASK = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARM_TASK + "(" + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + "dev_uid			VARCHAR(20) NULL, " + "time_			VARCHAR(20) NULL, " + "weeks	VARCHAR(20) NULL," + " flag  INTEGER"+ ");";

		private static final String SQLCMD_DROP_TABLE_DEVICE = "drop table if exists " + TABLE_DEVICE + ";";

		private static final String SQLCMD_DROP_TABLE_SEARCH_HISTORY = "drop table if exists " + TABLE_SEARCH_HISTORY + ";";

		private static final String SQLCMD_DROP_TABLE_SNAPSHOT = "drop table if exists " + TABLE_SNAPSHOT + ";";
		
		private static final String SQLCMD_DROP_TABLE_ALARM_TASK = "drop table if exists " + TABLE_ALARM_TASK + ";";

		public DatabaseHelper(Context context) {
			super(context, DB_FILE, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(SQLCMD_CREATE_TABLE_DEVICE);
			db.execSQL(SQLCMD_CREATE_TABLE_SEARCH_HISTORY);
			db.execSQL(SQLCMD_CREATE_TABLE_SNAPSHOT);
			db.execSQL(SQLCMD_CREATE_TABLE_ALARM_TASK);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
//				db.execSQL(SQLCMD_DROP_TABLE_DEVICE);
//				db.execSQL(SQLCMD_DROP_TABLE_SEARCH_HISTORY);
//				db.execSQL(SQLCMD_DROP_TABLE_SNAPSHOT);
//				db.execSQL(SQLCMD_DROP_TABLE_ALARM_TASK);
				onCreate(db);
		}

	}
}
