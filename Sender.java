import	java.io.*;
import	java.net.*;

/*
 * ASK RUSSELL: What EXACTLY are we sending? Just strings? Generics? What?
 * 
 */

public class Sender {
	
	private void ping()
	{
		/*
		 * Somehow ask Receiver if they received the packet.
		 */
	}
	
	static String get_istring( int x )
	{
		int			a,b,c,d;
		d = x & 0x000000ff;
		c = (x >> 8) & 0x000000ff;
		b = (x >> 16) & 0x000000ff;
		a = (x >> 24) & 0x000000ff;
		System.out.println( "a is " + a );
		return new String( a + "." + b + "." + c + "." + d );
	}
	
	static int get_int( byte[] b4 )
	{
		int result = 0;
		Byte bt;
		System.out.println( "b4[0] is " + b4[0] );
		System.out.println( "(b4[0] << 24) is " + (b4[0] << 24) );
		
		try
		{
			bt = b4[0];
			if(!bt.toString().equals(null)) result += b4[0] << 24;
			bt = b4[1];
			if(!bt.toString().equals(null)) result += b4[1] << 16;
			bt = b4[2];
			if(!bt.toString().equals(null)) result += b4[2] << 8;
			bt = b4[3];
			if(!bt.toString().equals(null))
			{
				//Statement is to see if it is signed or unsigned.
				if(b4[0] < 0)result += (b4[3] + 0x01000000);
				else result += b4[3];
			}
		}catch(Exception e)
		{
			return result;
		}
		return result;
	}

	public static void main(String[] args) throws Exception
	{
		InetAddress		destination = InetAddress.getByName( "127.0.0.1" );
		DatagramPacket		sendPacket;
		DatagramSocket		socket = new DatagramSocket();
		BufferedReader		stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		int			port = 3000;
		String s;

		System.out.println( "datagram target is " + destination + " port " + port );
		System.out.print( "Enter a string>>" );
		socket.setBroadcast( true );
		while ( (s = stdIn.readLine()) != null )
		{
			sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port );
			socket.send( sendPacket );
			
			System.out.println("AFTER CONVERTING TO INT: " + get_int(s.getBytes()));
			System.out.println("AFTER CONVERTING TO STRING: " + get_istring(get_int(s.getBytes())));
			System.out.print( "Enter a string>>" );
		}
		System.out.println( "Normal end of sender2." );
	}
}
