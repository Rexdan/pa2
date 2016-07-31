import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Nack extends Thread{
	public boolean terminate;
	public void run(){
		try {
			terminate= false;
			DatagramSocket	socket = new DatagramSocket(3002);
			socket.setReuseAddress( true );
			byte []		payload = new byte[1024 + 19];
			DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
			int debug = 0;
			while(terminate==false )
			{
				//System.out.println("Attempting to receive nack: " + debug);
				socket.receive( receivePacket );
				//System.out.println("Nack received.");
				debug++;
				byte[] buffer = receivePacket.getData();
				byte[] b = Arrays.copyOfRange(buffer, 0, PacketHelp.getLength(buffer));
				
				boolean checkSum = PacketHelp.checkTheSum(b);
				
				synchronized(Sender.hashArray){
					
					for(int i=0; i<Sender.hashArray.length; i++){
						
						if(Sender.hashArray[i]!=null ){
							
							byte [] a = Sender.hashArray[i].toSend;
							//System.out.printf("index %d not null.\n", i);
							if( PacketHelp.compareData( a , b )  <  0 && checkSum ){
								//System.out.printf("Packet %d removed.\n", i);
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
