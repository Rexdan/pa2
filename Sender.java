import	java.io.*;
import	java.net.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;

public class Sender implements Runnable{
	
	//HashMap<Integer, Packet> packets = new HashMap<>();
	
	int size;
	
	static File file;
	
	final private static int MAX = 100;
	
	private void ping()
	{
		/*
		 * Somehow ask Receiver if they received the packet.
		 */
	}

	public static void main(String[] args) throws Exception
	{
		//Broadcasting
		InetAddress		destination = InetAddress.getByName( "255.255.255.255" );
		DatagramPacket		sendPacket;
		DatagramSocket		socket = new DatagramSocket();
		BufferedReader		stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		int			port = 3000;
		String s /*= stdIn.readLine()*/;
		File file = null;
		FileInputStream fin;
		FileOutputStream fos;
		InputStream stream;
		byte [] fileBytes;
		
		HashMap<Integer,PacketInfo> window = new HashMap();

		System.out.println( "datagram target is " + destination + " port " + port );
		System.out.print( "Enter File Name: " );
		//Packet fileName = new Packet(s.getBytes());
		socket.setBroadcast( true );
		
		while ( (s = stdIn.readLine()) != null )
		{
			try
			{
				file = new File(s);
			}catch(Exception e)
			{
				System.err.println("File not found." + "\n" + "Please enter a proper file name.");
				continue;
				//e.printStackTrace();
			}
			
			/*
			 * For reading the contents of the file.
			 */
			fileBytes = Files.readAllBytes(file.toPath());
			
			/*
			 * How do we truncate this into different
			 * datagram packets?
			 */
			int seq = 1;
			int from = 0;
			int to = 2047;
			byte [] toSend;
			PacketInfo packet;
		
			while(fileBytes.length > 0)
			{
				/*
				 * The packet we are sending.
				 * 
				 * The checksum and the flag we need to set default values.
				 * 
				 * Checksum all is good = 0
				 * Checksum everything else = bad
				 * 
				 * flag 0 = nothing special about packet
				 * flag 1 = name of file/first packet
				 * flag 2 = EOF/last packet
				 * 
				 * Size of payload should be 2048.
				 * 
				 */
				if(seq == 1)
				{
					/*Easily send name of file to Sender first.*/
					toSend = PacketHelp.makePacket(seq, destination.toString(), InetAddress.getLocalHost().toString(), (byte) 0 , (byte) 1, file.getPath().getBytes());
				}
				else
				{
					from += 2048;
					if(to + 2048 >= fileBytes.length)
					{
						to = fileBytes.length - 1;
						toSend = PacketHelp.makePacket(seq, destination.toString(), InetAddress.getLocalHost().toString(), (byte) 0 , (byte) 2, Arrays.copyOfRange(fileBytes, from, to));
						synchronized(window)
						{
							if(window.size() != MAX)
							{
								packet = new PacketInfo(toSend);
								window.put(seq % 100, packet);
							}
						}
						/*Pass last packet to sender thread.*/
						break;
					}
					to += 2048;
					toSend = PacketHelp.makePacket(seq, destination.toString(), InetAddress.getLocalHost().toString(), (byte) 0 , (byte) 2, Arrays.copyOfRange(fileBytes, from, to));
				}
				synchronized(window)
				{
					if(window.size() != MAX)
					{
						packet = new PacketInfo(toSend);
						window.put(seq % 100, packet);
					}
				}
				seq++;
				sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port ); //Congestion may be a problem if the buffer is not large enough.
				/*needs to be handled in different thread*/
				socket.send( sendPacket );
			}
			
			//To check size...
			System.out.println("This is the size of the byte array: " + fileBytes.length);
			
			sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port );
			//byte [] bytes = sendPacket.getData();
			socket.send( sendPacket );
			System.out.println( "Enter a File Name: " );
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
		
	}
}
