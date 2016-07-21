import	java.io.*;
import	java.net.*;
import java.util.HashMap;

public class Sender implements Runnable{
	
	//HashMap<Integer, Packet> packets = new HashMap<>();
	
	int size;
	
	static File file;
	
	private void ping()
	{
		/*
		 * Somehow ask Receiver if they received the packet.
		 */
	}

	public static void main(String[] args) throws Exception
	{
		InetAddress		destination = InetAddress.getByName( "255.255.255.255" );
		DatagramPacket		sendPacket;
		DatagramSocket		socket = new DatagramSocket();
		BufferedReader		stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		int			port = 3000;
		String s = stdIn.readLine();

		System.out.println( "datagram target is " + destination + " port " + port );
		System.out.print( "Enter file name: " );
		//Packet fileName = new Packet(s.getBytes());
		socket.setBroadcast( true );
		
		while ( (s = stdIn.readLine()) != null )
		{
			sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port );
			//byte [] bytes = sendPacket.getData();
			
			socket.send( sendPacket );
			System.out.print( "Enter a string>>" );
		}
		
		/*while ( s != null )
		{
			file = new File(s);
			sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port );
			socket.send( sendPacket );
			
			System.out.println("AFTER CONVERTING TO INT: " + get_int(s.getBytes()));
			System.out.println("AFTER CONVERTING TO STRING: " + get_istring(get_int(s.getBytes())));
			System.out.print( "Enter file name: " );
		}*/
		System.out.println( "Normal end of sender2." );
	}

	@Override
	public void run()
	{
		/*
		 * For TIMEOUT.
		 * If you send a packet, not going to wait for an indefinite amount of time for an acknowledgement.
		 * Just sleep and then restart.
		 */
	}
}
