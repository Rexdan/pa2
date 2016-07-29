import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Receiver implements Runnable
{
	
	private void NACK() /*OPTION 2 For Protocol*/
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
		byte []		payload = new byte[2048 + 15];
		DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
		String fileName;
		File file;
		int stillNeed = 0;
		PacketInfo[] window = new PacketInfo[10];
		RandomAccessFile raf;
		socket.setReuseAddress( true );
		while( true ) //what would be a good terminating condition for this?
		{
			socket.receive( receivePacket );
			byte [] data = receivePacket.getData();
			int seq = PacketHelp.getSequenceNumber(data);
			if(seq==0 && stillNeed==0){
				byte[] temp = PacketHelp.getPayLoad(data, data.length);
				fileName = new String(temp);
				file = new File(System.getProperty("user.dir")+ "\\"+ fileName);
				raf = new RandomAccessFile(file, "rw");
			}
			//case where packet is filename
			//creates extrapolates filename from bytes, creates a file in current directory with indicated filename
			//~~~~incomplete~~~~~~~~
			//System.out.println( fileName/*new String( receivePacket.getData() )*/ );
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
