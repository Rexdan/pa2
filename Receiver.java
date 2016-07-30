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
		DatagramSocket nSocket = new DatagramSocket();
		nSocket.connect(InetAddress.getLocalHost(), 3001);
		//double check if my socket syntax is doing what I think its doing
		nSocket.setReuseAddress(true);
		socket.setReuseAddress( true );
		
		
		
		byte []		payload = new byte[2048 + 15];
		DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
		DatagramPacket nackPack;
		String fileName;
		File file;
		boolean needName = true;
		int stillNeed = 0;
		PacketInfo[] window = new PacketInfo[10];
		RandomAccessFile raf;
		
		while( true ) //what would be a good terminating condition for this?
		{
			socket.receive( receivePacket );
			byte [] data = receivePacket.getData();
			int seq = PacketHelp.getSequenceNumber(data);
			byte[] temp;
			if(seq==0 && needName){ //case of first packet
				temp = PacketHelp.getPayLoad(data, data.length);
				if(!PacketHelp.checkTheSum(data)){
					//nack the packet here
					temp = PacketHelp.makePacket(seq, InetAddress.getLocalHost().toString(), 'r', (byte) 0, (byte) 1, PacketHelp.getPort(data), data);
					nackPack = new DatagramPacket( temp, temp.length, InetAddress.getByName(PacketHelp.getIP(data)), PacketHelp.getPort(data)  );
					nSocket.send(nackPack);
				}
				else{
					/*
					 * fileName = new String(temp);
					 * file = new File(System.getProperty("user.dir")+ "\\"+ fileName);
					 * raf = new RandomAccessFile(file, "rw");
					*/
					if(window[PacketHelp.getSequenceNumber(data)%10]==null){
						window[PacketHelp.getSequenceNumber(data)%10] = new PacketInfo(data);
					}
					//ignore duplicate packet
				}
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
