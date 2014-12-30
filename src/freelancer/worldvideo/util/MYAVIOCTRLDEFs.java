/**
 * MyAVIOCTRLDEFs.java
 */
package freelancer.worldvideo.util;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;

;
/**
 * @function: 报警设置相关参数
 * @author jiangbing
 * @data: 2014-3-28
 */
public class MYAVIOCTRLDEFs extends AVIOCTRLDEFs 
{
	
	public final static byte	AVIOCTRL_EVENT_PIR                                 =0x07;//PIR
	public final static byte	AVIOCTRL_EVENT_OD                                  = 0x08;//OD
	public final static byte	AVIOCTRL_EVENT_RS232                             =0x09;// RS232
	
	// Addtion Setting for Netviom
	public static final int IOTYPE_NETVIOM_GET_MOTION_REQ = 0xFF000001;
	public static final int IOTYPE_NETVIOM_GET_MOTION_RESP = 0xFF000002;
	public static final int IOTYPE_NETVIOM_SET_MOTION_REQ = 0xFF000003;
	public static final int IOTYPE_NETVIOM_SET_MOTION_RESP = 0xFF000004;

	public static final int IOTYPE_NETVIOM_GET_IO_REQ = 0xFF000011;
	public static final int IOTYPE_NETVIOM_GET_IO_RESP = 0xFF000012;
	public static final int IOTYPE_NETVIOM_SET_IO_REQ = 0xFF000013;
	public static final int IOTYPE_NETVIOM_SET_IO_RESP = 0xFF000014;

	public static final int IOTYPE_NETVIOM_GET_ALARMTIME_REQ = 0xFF000021;
	public static final int IOTYPE_NETVIOM_GET_ALARMTIME_RESP = 0xFF000022;
	public static final int IOTYPE_NETVIOM_SET_ALARMTIME_REQ = 0xFF000023;
	public static final int IOTYPE_NETVIOM_SET_ALARMTIME_RESP = 0xFF000024;

	public static final int IOTYPE_NETVIOM_GET_EMAIL_REQ = 0xFF000031;
	public static final int IOTYPE_NETVIOM_GET_EMAIL_RESP = 0xFF000032;
	public static final int IOTYPE_NETVIOM_SET_EMAIL_REQ = 0xFF000033;
	public static final int IOTYPE_NETVIOM_SET_EMAIL_RESP = 0xFF000034;

	public static final int IOTYPE_NETVIOM_GET_FTP_REQ = 0xFF000041;
	public static final int IOTYPE_NETVIOM_GET_FTP_RESP = 0xFF000042;
	public static final int IOTYPE_NETVIOM_SET_FTP_REQ = 0xFF000043;
	public static final int IOTYPE_NETVIOM_SET_FTP_RESP = 0xFF000044;
	
	public static final int IOTYPE_NETVIOM_GET_PIR_REQ = 0xFF000051;
	public static final int IOTYPE_NETVIOM_GET_PIR_RESP	= 0xFF000052;
	public static final int IOTYPE_NETVIOM_SET_PIR_REQ = 0xFF000053;
	public static final int IOTYPE_NETVIOM_SET_PIR_RESP	= 0xFF000054;

	public static final int IOTYPE_NETVIOM_GET_OUTPUT_REQ = 0xFF000061;
	public static final int IOTYPE_NETVIOM_GET_OUTPUT_RESP = 0xFF000062;
	public static final int IOTYPE_NETVIOM_SET_OUTPUT_REQ = 0xFF000063;
	public static final int IOTYPE_NETVIOM_SET_OUTPUT_RESP = 0xFF000064;
	
	public static final int IOTYPE_NETVIOM_SET_REBOOT_REQ = 0xFF000071;
	public static final int IOTYPE_NETVIOM_SET_REBOOT_RESP = 0xFF000072;
	public static final int IOTYPE_NETVIOM_SET_DEFAULT_REQ = 0xFF000073;
	public static final int IOTYPE_NETVIOM_SET_DEFAULT_RESP = 0xFF000074;
	
	public static final int IOTYPE_NETVIOM_GET_DOOR_REQ	= 0xFF000081;
	public static final int IOTYPE_NETVIOM_GET_DOOR_RESP = 0xFF000082;
	public static final int IOTYPE_NETVIOM_SET_DOOR_REQ	= 0xFF000083;
	public static final int IOTYPE_NETVIOM_SET_DOOR_RESP = 0xFF000084;
	
	public static final int IOTYPE_ISMART_GET_VOICE_REQ				=0xFF000091;
	public static final int IOTYPE_ISMART_GET_VOICE_RESP			=0xFF000092;
	public static final int IOTYPE_ISMART_SET_VOICE_REQ				=0xFF000093;
	public static final int IOTYPE_ISMART_SET_VOICE_RESP			=0xFF000094;
	public static final int IOTYPE_ISMART_SET_REJECT_REQ			=0xFF000095;
	public static final int IOTYPE_ISMART_SET_REJECT_RESP			=0xFF000096;
	
	public static final int IOTYPE_ISMART_GET_VOICE_LNG_REQ		=0xFF000097;
	public static final int IOTYPE_ISMART_GET_VOICE_LNG_RESP		=0xFF000098;
	public static final int IOTYPE_ISMART_SET_VOICE_LNG_REQ		=0xFF000099;
	public static final int IOTYPE_ISMART_SET_VOICE_LNG_RESP		=0xFF00009a;
	
	public static final int IOTYPE_ISMART_GET_APMODE_REQ			=0xFF00009b;
	public static final int IOTYPE_ISMART_GET_APMODE_RESP			=0xFF00009c;
	public static final int IOTYPE_ISMART_SET_APMODE_REQ			=0xFF00009d;
	public static final int IOTYPE_ISMART_SET_APMODE_RESP			=0xFF00009e;
	/**
	 * Wi-Fi set for WEP
	 * @author jiangbing
	 *
	 */
	public static class SMsgAVIoctrlSetWifiReq {
		byte[] ssid = new byte[32];
		byte[] password = new byte[32];
		byte mode;
		byte enctype;
		byte[] reserved = new byte[10];

		public static byte[] parseContent(byte[] ssid, byte[] password, byte mode, byte enctype,byte defaultKey,byte wepKeyType) {

			byte[] result = new byte[76];

			System.arraycopy(ssid, 0, result, 0, ssid.length);
			System.arraycopy(password, 0, result, 32, password.length);
			result[64] = mode;
			result[65] = enctype;
			result[66] =defaultKey ;
			result[67] = wepKeyType;
			return result;
		}
	}

	
	// Addtion struct for Netviom
	/*
	 * IOTYPE_NETVIOM_GET_MOTION_RESP =0xFF000002 IOTYPE_NETVIOM_SET_MOTION_REQ
	 * =0xFF000003* @struct sMsgNetviomGetMotionResp* @struct
	 * sMsgNetviomSetMotionReq
	 */
	public static class sMsgNetviomGetMotionResp {
		int onoff; // 0 -off, 1 - on;
		byte record;
		byte io;
		byte emailjpg;
		byte ftpjpg;
		byte ftpmp4;
		byte reserved[] = new byte[3];
	}
	
	public static class sMsgNetviomGetMotionRep {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class sMsgNetviomSetMotionReq {
		int onoff; // 0 -off, 1 - on;
		byte record; //sd卡录像
		byte io;	//联动输出
		byte emailjpg;	//邮件图片
		byte ftpjpg;	//ftp图片
		byte ftpmp4;	//ftp录像
		byte reserved[] = new byte[3];
		
		public static byte[] parseContent(int onoff,byte record,byte io,byte email,byte ftpjpg,byte ftpmp4)
		{
			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(onoff);
			System.arraycopy(ch, 0, result, 0, 4);
			result[4] = record;
			result[5] = io;
			result[6] = email;
			result[7] = ftpjpg;
			result[8] = ftpmp4;
			
			return result;
		}
	}

	/*
	 * IOTYPE_NETVIOM_SET_MOTION_RESP =0xFF000004* @struct
	 * sMsgNetviomSetMotionResp
	 */
	public static class sMsgNetviomSetMotionResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}

	/*
	 * IOTYPE_NETVIOM_GET_IO_RESP =0xFF000012 IOTYPE_NETVIOM_SET_IO_REQ
	 * =0xFF000013* @struct sMsgNetviomGetIOResp* @struct sMsgNetviomSetIOReq
	 */
	public static class sMsgNetviomGetIOResp {
		int trigger; // 0 -open, 1 - close;
		byte record;
		byte io;
		byte emailjpg;
		byte ftpjpg;
		byte ftpmp4;
		byte ptz;
		byte reserved[] = new byte[2];
	}
	
	public static class sMsgNetviomGetIORep {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class sMsgNetviomSetIOReq {
		int trigger; // 0 -open, 1 - close;
		byte record;
		byte io;
		byte emailjpg;
		byte ftpjpg;
		byte ftpmp4;
		byte ptz;
		byte reserved[] = new byte[2];
		public static byte[] parseContent(int trigger,byte record,byte io,byte email,byte ftpjpg,byte ftpmp4,byte ptz)
		{
			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(trigger);
			System.arraycopy(ch, 0, result, 0, 4);
			result[4] = record;
			result[5] = io;
			result[6] = email;
			result[7] = ftpjpg;
			result[8] = ftpmp4;
			result[9] = ptz;
			
			return result;
		}
		
	}

	/*
	 * IOTYPE_NETVIOM_SET_IO_RESP =0xFF000014* @struct sMsgNetviomSetIOResp
	 */
	public static class sMsgNetviomSetIOResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}
	
	/*
	 * IOTYPE_NETVIOM_GET_ALARMTIME_RESP =0xFF000022
	 * IOTYPE_NETVIOM_SET_ALARMTIME_REQ =0xFF000023* @struct
	 * sMsgNetviomGetAlarmTimeResp* @struct sMsgNetviomSetAlarmTimeReq
	 */
	public static class sMsgNetviomGetAlarmTimeResp {
		int channel;
		int Type; // by day -0, by week -1, only 1 is valid!!!
		byte Week[][] = new byte[7][24]; // 0 is sunday!
		byte DayTime[] = new byte[24];
	}
	
	public static class sMsgNetviomGetAlarmTimeRep
	{
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class sMsgNetviomSetAlarmTimeReq {
		int channel;
		int Type; // by day -0, by week -1, only 1 is valid!!!
		byte Week[][] = new byte[7][24]; // 0 is sunday!
		byte DayTime[] = new byte[24];
		public static byte[] parseContent(int channel, int type, byte[][] week)
		{
			byte[] result = new byte[200];
			byte[] ch = Packet.intToByteArray_Little(channel);
			byte[] tp = Packet.intToByteArray_Little(type);
			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(tp, 0, result, 4, 4);
			for (int i = 0; i < 7; i++)
			{
				System.arraycopy(week[i], 0, result, (i * 24) + 8, 24);
			}
			return result;
		}
	}

	/*
	 * IOTYPE_NETVIOM_SET_ALARMTIME_RESP =0xFF000024* @struct
	 * sMsgNetviomSetAlarmTimeResp
	 */
	public static class sMsgNetviomSetAlarmTimeResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}

	/*
	 * IOTYPE_NETVIOM_GET_EMAIL_RESP =0xFF000032 IOTYPE_NETVIOM_SET_EMAIL_REQ
	 * =0xFF000033* @struct sMsgNetviomGetEmailResp* @struct
	 * sMsgNetviomSetEmailReq
	 */
	public static class sMsgNetviomGetEmailResp {
		byte server[] = new byte[128];
		byte from[] = new byte[128];
		byte to[] = new byte[128];
		byte user[] = new byte[32];
		byte pass[] = new byte[32];
		byte auth; // 1 - need;
		byte ssl;
		byte reserved[] = new byte[2];
	}
	
	public static class sMsgNetviomGetEmailReq {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}

	public static class sMsgNetviomSetEmailReq {
		byte server[] = new byte[128];
		byte from[] = new byte[128];
		byte to[] = new byte[128];
		byte user[] = new byte[32];
		byte pass[] = new byte[32];
		byte auth; // 1 - need;
		byte ssl;
		byte reserved[] = new byte[2];
		public static byte[] parseContent(byte[] server,byte[] from,byte[] to,byte[] user,byte[] pass,byte auth,byte ssl)
		{
			byte[] result = new byte[452];
			System.arraycopy(server, 0, result, 0, 128);
			System.arraycopy(from, 0, result, 128, 128);
			System.arraycopy(to, 0, result, 256, 128);
			System.arraycopy(user, 0, result, 384, 32);
			System.arraycopy(pass, 0, result, 416, 32);
			result[448] = auth;
			result[449] = ssl;
			
			return result;
		}
	}

	/*
	 * IOTYPE_NETVIOM_SET_EMAIL_RESP =0xFF000034* @struct
	 * sMsgNetviomSetEmailResp
	 */
	public static class sMsgNetviomSetEmailResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}

	/*
	 * IOTYPE_NETVIOM_GET_FTP_RESP =0xFF000042 IOTYPE_NETVIOM_SET_FTP_REQ
	 * =0xFF000043* @struct sMsgNetviomGetFtpResp* @struct sMsgNetviomSetFtpReq
	 */
	public static class sMsgNetviomGetFtpResp {
		int port;
		byte server[] = new byte[128];
		byte user[] = new byte[32];
		byte pass[] = new byte[32];
		byte path[] = new byte[128];
	}
	public static class sMsgNetviomGetFtpReq {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}
	

	public static class sMsgNetviomSetFtpReq {
		int port;
		byte server[] = new byte[128];
		byte user[] = new byte[32];
		byte pass[] = new byte[32];
		byte path[] = new byte[128];
		public static byte[] parseContent(int port, byte[] server, byte[] user, byte[] pass, byte[] path)
		{
			byte[] result = new byte[324];
			byte[] pt = Packet.intToByteArray_Little(port);
			System.arraycopy(pt, 0, result, 0, 4);
			System.arraycopy(server, 0, result, 4, 128);
			System.arraycopy(user, 0, result, 132, 32);
			System.arraycopy(pass, 0, result, 164, 32);
			System.arraycopy(path, 0, result, 196, 128);
			return result;
		}
	}

	/*
	 * IOTYPE_NETVIOM_SET_FTP_RESP =0xFF000044* @struct sMsgNetviomSetFtpResp
	 */
	public static class sMsgNetviomSetFtpResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	};
	
	/*
	 * IOTYPE_NETVIOM_GET_IO_RESP =0xFF000052 IOTYPE_NETVIOM_SET_IO_REQ
	 * =0xFF000053* @struct sMsgNetviomGetIOResp* @struct sMsgNetviomSetIOReq
	 */
	public static class sMsgNetviomGetPIRResp {
		int trigger; // 0 -open, 1 - close;
		byte record;
		byte io;
		byte emailjpg;
		byte ftpjpg;
		byte ftpmp4;
		byte ptz;
		byte reserved[] = new byte[2];
	}
	
	public static class sMsgNetviomGetPIRRep {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	
	public static class sMsgNetviomSetPIRReq {
		int trigger; // 0 -open, 1 - close;
		byte record;
		byte io;
		byte emailjpg;
		byte ftpjpg;
		byte ftpmp4;
		byte ptz;
		byte reserved[] = new byte[2];
		public static byte[] parseContent(int trigger,byte record,byte io,byte email,byte ftpjpg,byte ftpmp4,byte ptz)
		{
			byte[] result = new byte[12];
			byte[] ch = Packet.intToByteArray_Little(trigger);
			System.arraycopy(ch, 0, result, 0, 4);
			result[4] = record;
			result[5] = io;
			result[6] = email;
			result[7] = ftpjpg;
			result[8] = ftpmp4;
			result[9] = ptz;
			return result;
		}
		
	}
	
	/*
	 * IOTYPE_NETVIOM_SET_IO_RESP =0xFF000054* @struct sMsgNetviomSetIOResp
	 */
	public static class sMsgNetviomSetPIRResp {
		int result; // 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}
	
	/*
	IOTYPE_NETVIOM_GET_OUTPUT_RESP			=0xFF000062
	IOTYPE_NETVIOM_SET_OUTPUT_REQ			=0xFF000063
	** @struct sMsgNetviomGetOutPutResp
	** @struct sMsgNetviomSetOutputReq
	*/
	public static class sMsgNetviomGetOutPutResp {
		 int state;		// 0 -open, 1 - close;
		 int delay;		// seconds
	}
	
	public static class sMsgNetviomSetOutPutReq {
		int state;		// 0 -open, 1 - close;
		int delay;		// seconds
		public static byte[] parseContent(int state, int delay) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(state);
			byte[] d = Packet.intToByteArray_Little(delay);
			System.arraycopy(ch, 0, result, 0, 4);
			System.arraycopy(d, 0, result, 4, 4);
			return result;
		}
	}
	/*
	IOTYPE_NETVIOM_SET_OUTPUT_RESP			=0xFF000064
	** @struct sMsgNetviomSetOutputResp
	*/
	public static class sMsgNetviomGetOutputReq {
		int channel;	
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);

			return result;
		}
	}
	/*
	IOTYPE_NETVIOM_SET_OUTPUT_RESP			=0xFF000064
	** @struct sMsgNetviomSetOutputResp
	*/
	public static class sMsgNetviomSetOutputResp {
		int result;	// 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
	}
	/*
	IOTYPE_NETVIOM_SET_REBOOT_REQ			=0xFF000071
	 ** @struct sMsgNetviomSetOutputResp
	 */
	public static class sMsgNetviomSetRebootReq {
		int channel;	
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	/*
	IOTYPE_NETVIOM_SET_DEFAULT_REQ			=0xFF000073
	 ** @struct sMsgNetviomSetOutputResp
	 */
	public static class sMsgNetviomSetDefaultReq {
		int channel;	
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	public static class sMsgNetviomGetDoorReq {
		int channel; 
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int channel) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(channel);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	/*
	IOTYPE_NETVIOM_GET_DOOR_RESP			=0xFF000082
	** @struct sMsgNetviomGetDoorResp
	*/
	public static class sMsgNetviomGetDoorResp{
		 int supported;		// 0 -no, 1 - yes;
		 byte reserved[] = new byte[4];
		 public static byte[] parseContent(int supported) {
				byte[] result = new byte[8];
				byte[] ch = Packet.intToByteArray_Little(supported);
				System.arraycopy(ch, 0, result, 0, 4);
				return result;
		}
	}

	/*
	IOTYPE_NETVIOM_SET_DOOR_REQ			=0xFF000083
	** @struct sMsgNetviomSetDoorReq
	*/
	public static class  sMsgNetviomSetDoorReq{
		 int state;		// 1 -open, 0 - close;
		 byte pwd[] = new byte[32];		// password for door
		 public static byte[] parseContent(int state, byte pwd[]) {
				byte[] result = new byte[40];
				byte[] ch = Packet.intToByteArray_Little(state);
				System.arraycopy(ch, 0, result, 0, 4);
				System.arraycopy(pwd, 0, result, 4, 32);
				return result;
		}
	}

	/*
	IOTYPE_NETVIOM_SET_DOOR_RESP			=0xFF000084
	** @struct sMsgNetviomSetDoorResp
	*/
	public static class sMsgNetviomSetDoorResp{
		int result;	// 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
	}
	}
	
	
	/*
	IOTYPE_ISMART_GET_VOICE_REQ			=0xFF000091
	IOTYPE_ISMART_SET_VOICE_REQ				=0xFF000093
	** @struct sMsgIsmartGetVoiceResp, sMsgIsmartSetVoiceReq
	*/
	
	public static class sMsgIsmartSetVoiceReq{
		int enabled;	// 1 -enable, 0 - disabled;
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
	}
	}
	
	public static class sMsgIsmartGetVoiceReq{
		int channel;	
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
	}
	}
	
	/*
	IOTYPE_ISMART_GET_VOICE_RESP			=0xFF000092
	IOTYPE_ISMART_SET_VOICE_RESP			=0xFF000094
	IOTYPE_ISMART_SET_REJECT_REQ			=0xFF000095
	IOTYPE_ISMART_SET_REJECT_RESP		    =0xFF000096
	** @struct sMsgIsmartGetVoiceResp, sMsgIsmartSetVoiceResp
	*/
	
	
	public static class sMsgIsmartSetRejectReq{
		int result;	
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
	}
	}
	
	/*
	IOTYPE_ISMART_GET_VOICE_LNG_REQ		=0xFF000097,
	IOTYPE_ISMART_GET_VOICE_LNG_RESP		=0xFF000098,
	IOTYPE_ISMART_SET_VOICE_LNG_REQ		=0xFF000099,
	IOTYPE_ISMART_SET_VOICE_LNG_RESP		=0xFF00009a,
	** @struct sMsgIsmartGetVoiceLngResp, sMsgIsmartSetVoiceLngReq
	*/

	public static class sMsgIsmartSetVoiceLngReq{
		int lng;		// 1 -EN, 0 - ZH;
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
	}
	}
	public static class sMsgIsmartSetVoiceLngResp{
		int result;		// 1 -EN, 0 - ZH;
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	public static class sMsgIsmartGetVoiceLngReq{
		int result;		// 1 -EN, 0 - ZH;
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	
	/*
	IOTYPE_ISMART_GET_APMODE_REQ			=0xFF00009b,
	IOTYPE_ISMART_GET_APMODE_RESP			=0xFF00009c,
	IOTYPE_ISMART_SET_APMODE_REQ			=0xFF00009d,
		IOTYPE_ISMART_SET_APMODE_RESP			=0xFF00009e,
	** @struct sMsgIsmartGetAPModeResp, sMsgIsmartSetAPModeReq
	*/
	

	public static class sMsgIsmartSetAPModeReq{
		int apmode;		// 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	public static class sMsgIsmartSetAPModeResp{
		int result;		// 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
	public static class sMsgIsmartGetAPModeReq{
		int result;		// 0: success; otherwise: failed.
		byte reserved[] = new byte[4];
		public static byte[] parseContent(int re) {
			byte[] result = new byte[8];
			byte[] ch = Packet.intToByteArray_Little(re);
			System.arraycopy(ch, 0, result, 0, 4);
			return result;
		}
	}
}
