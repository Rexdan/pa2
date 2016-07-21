import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Receiver implements Runnable
{
	
	private void NACK()
	{
		
	}
	
	private void writeFile( )
	{
		FileOutputStream stream = null;
		try 
		{
		   // stream.write(packet.getBytes());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally 
		{
		    //stream.close();
		}
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
			System.out.println( new String( receivePacket.getData() ) );
		}
	}

	@Override
	public void run()
	{
		/*
		 * 
		 */
	}
}
