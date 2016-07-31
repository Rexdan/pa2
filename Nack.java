import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class Nack extends Thread{
	public boolean terminate;
	public void run(){
		try {
			terminate= false;
			DatagramSocket	socket = Sender.getSocket();
			socket.setReuseAddress( true );
			byte []		payload = new byte[1024 + 19];
			DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
			while(terminate==false )
			{
				socket.receive( receivePacket );
				byte[] buffer = receivePacket.getData();
				byte[] b = Arrays.copyOfRange(buffer, 0, PacketHelp.getLength(buffer));
				synchronized(Sender.hashArray){
					for(int i=0; i<Sender.hashArray.length; i++){
						
						if(Sender.hashArray[i]!=null ){
							byte [] a = Sender.hashArray[i].toSend;
							if( PacketHelp.compareData( a , b )  <  0 ){
								Sender.hashArray[i] = null;
							}
						}
					}
				}
			}
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
