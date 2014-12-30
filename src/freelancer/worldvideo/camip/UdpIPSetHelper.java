package freelancer.worldvideo.camip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpIPSetHelper implements Runnable {
	public Boolean IsThreadDisable = false;
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
					if(strMsg.length() < 300)
					{
						continue;
					}
					if(str[1].length() > 15 && str[1].substring(15).contains("success"))
					{
						IsThreadDisable = true;
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	public void send(byte[] msg) {
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
		p = new DatagramPacket(msg, msg.length, local,
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
}
