package freelancer.worldvideo.camip;

public interface HeaderDefine
{
	public final static int MAC = 0;
	public final static int NAME = 1;
	public final static int IP = 2;
	public final static int SNCODE = 3;
	public final static int GATE = 4;
	public final static int DNS1 = 5;
	public final static int DNS2 = 6;
	public final static int HTTP_PORT = 7;
	public final static int RTSP_PORT = 8;
	
	public final static char END = 0x0a;
	public final static char SPLIT = 0x2c;
	
	public final static char[] HEADER = {0x53,0x43,0x52,0x48,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x31,0x30,0x30,0x30,0x30,0x31,0x34,0x0A,};
	public final static char[] PREFIX1 = {0x53,0x43,0x52,0x48,0x05,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX2 = {0x53,0x43,0x52,0x48,0xf5,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX3 = {0x53,0x43,0x52,0x48,0xf6,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX4 = {0x53,0x43,0x52,0x48,0xf7,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX5 = {0x53,0x43,0x52,0x48,0xf8,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX6 = {0x53,0x43,0x52,0x48,0xf9,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX7 = {0x53,0x43,0x52,0x48,0xfa,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX8 = {0x53,0x43,0x52,0x48,0xfb,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX9 = {0x53,0x43,0x52,0x48,0xfc,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX10 = {0x53,0x43,0x52,0x48,0xfd,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public final static char[] PREFIX11 = {0x53,0x43,0x52,0x48,0xfe,0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	
	public final static byte[] SCRH = {0x53,0x43,0x52,0x48,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x30,0x31,0x30,0x30,0x30,0x30,0x31,0x34,0x0A,0x53,0x43,0x52,0x48,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x0a};
	public final static String ADMIN = "cm9vdDpyb290";
	
}