package freelancer.worldvideo.camip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class UdpSearchHelper implements Runnable {
	
	private String ip = null;
	public Boolean IsThreadDisable = false;
	public LanDeviceInfo lan = new LanDeviceInfo();
	DatagramSocket datagramSocket = null;
	public void StartListen() {
		Integer port = 43440;
		byte[] message = new byte[1024];
		try {
			if(datagramSocket == null)
				datagramSocket = new DatagramSocket(port);
			datagramSocket.setBroadcast(true);
			datagramSocket.setReuseAddress(true);
			DatagramPacket datagramPacket = new DatagramPacket(message,
					message.length);
			try {
				while (!IsThreadDisable) {
					datagramSocket.receive(datagramPacket);
					String strMsg = new String(datagramPacket.getData()).trim();
					String str[] = strMsg.split("\n");
					LanDeviceInfo info = new LanDeviceInfo();
					if(strMsg.length() < 300)
					{
						continue;
					}
					for(int i = 2; i < str.length ;++i)
					{
						if(str[i].length() > 15 && str[i].length() < 40)
						{
							if(i >= 2 && i < 11)
							{
								int t = i -2;
								info.device[t] = str[i].substring(15);
							}
						}
					}
					if(info.device[2].contains(this.ip))
					{
						System.out.println("=========="+info.device[2]);
						lan = info;
						IsThreadDisable = true;
						return;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void send() {
		int server_port = 43440;
		DatagramSocket s = null;
		try {
			s = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress local = null;
		try {
			local = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		DatagramPacket p = null;
		p = new DatagramPacket(HeaderDefine.SCRH, HeaderDefine.SCRH.length, local,
				server_port);
		try {
			while (!IsThreadDisable) {
			s.send(p);
			Thread.sleep(3000);
			}
			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 public void close(){
         if(datagramSocket!=null){
        	 datagramSocket.disconnect();
        	 datagramSocket.close();
        	 datagramSocket =null ;
         }
         System.gc();
 }

	@Override
	public void run() {
		StartListen();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
