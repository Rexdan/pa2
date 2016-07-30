import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Nack extends Thread{
	public boolean terminate;
	public void run(){
		try {
			terminate= false;
			DatagramSocket	socket = new DatagramSocket( 3001 );
			socket.setReuseAddress( true );
			byte []		payload = new byte[2048 + 15];
			DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
			while(terminate==false )
			{
				socket.receive( receivePacket );
				byte[] payL = receivePacket.getData();
				int notGet = PacketHelp.getSequenceNumber(payL);
				synchronized(Sender.hashArray){
					for(int i=0; i<Sender.hashArray.length; i++){
						if(Sender.hashArray[i]!=null && Sender.hashArray[i].seq<notGet){
							Sender.hashArray[i] = null;
						}
					}
				}
				Thread.sleep(1000);
			}
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}
}
