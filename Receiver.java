
import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Receiver {
	
	private void ACK()
	{
		
	}
	
	private void NACK()
	{
		
	}

	public static void main(String[] args) throws Exception 
	{
		DatagramSocket	socket = new DatagramSocket( 3000 );
		byte []		payload = new byte[512];
		DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );

		socket.setReuseAddress( true );
		for( ;; )
		{
			socket.receive( receivePacket );
			System.out.print( new String( receivePacket.getData() ) );
		}
	}
}
