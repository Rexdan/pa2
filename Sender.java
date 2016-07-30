import	java.io.*;
import	java.net.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;

public class Sender{
	
	//HashMap<Integer, Packet> packets = new HashMap<>();
	
	
	//needs nack and timeout thread implementation to work!!!
	//do we have to deal with multiple dynamically changing receivers?
	
	int size;
	
	static File file;
	
	final private static int MAX = 100;
	
	public static PacketInfo [] hashArray;
	
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
		hashArray = new PacketInfo[10];
		
		Nack nThread = new Nack();
		nThread.start();
		//starts nack thread
		
		System.out.println( "datagram target is " + destination + " port " + port );
		System.out.print( "Enter File Name: " );
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
			int max = 2147483647;
			int seq = 0;
			int from = -2048;
			int to = 0;
			byte [] toSend;
			PacketInfo packet;
			byte flag =0;
			byte checksum=0;
			char color = 'r';
			while(to>=fileBytes.length)
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
				if(seq == 0)
				{
					/*Easily send name of file to Sender first.*/
					flag=1;
					toSend = PacketHelp.makePacket(seq, InetAddress.getLocalHost().toString(), color, checksum , flag, 3001, file.getName().getBytes());
					sendPacket = new DatagramPacket( toSend, toSend.length, destination, port );
					socket.send( sendPacket );
					synchronized(hashArray){
						hashArray[seq%10] = new PacketInfo(toSend);
					}
					seq++;
				}
				else
				{
					
					from+=2048; //first increment make from 0
					if(to+2048>=fileBytes.length-1){
						to = fileBytes.length-1;
						flag =2;
					}
					else{
						flag =0;
						to+=2047;
					}
					//increments boundaries, if/else check is for the upper boundary hitting the boundary of the fileBytes array
					byte[] info =  Arrays.copyOfRange(fileBytes, from, to);
					toSend = PacketHelp.makePacket(seq, InetAddress.getLocalHost().toString(), color, checksum , flag, 3001, info);
					sendPacket = new DatagramPacket( toSend, toSend.length, destination, port );
					socket.send( sendPacket );
					//creates and sends packet
					
					synchronized(hashArray){
						hashArray[seq%10] = new PacketInfo(toSend);
					}
					//puts the packetinfo into hash array
					if(seq==max){
						seq = 0;
						if(color == 'r'){
							color = 'b';
						}
						else{
							color = 'r';
						}
					}
					else{
						seq++;
					}
					//switches color if overflow is about to happen and resets sequence
					if(seq%10==0){
						while(!isEmpty()){
							Thread.sleep(100);
						}
					}
					//checks to see if window(hash array) is empty, sleeps for a second if it's not
					//in this way, it will only move onto the next window of 10 when all objects have been acknowledged
				}
			}
				//To check size...
				//System.out.println("This is the size of the byte array: " + fileBytes.length);
			System.out.println( "Enter a File Name: " );
		}
		//what is good termination condition for file query loop?
		
		nThread.terminate = true;
		
		/*while ( s != null )
		{
			file = new File(s);
			sendPacket = new DatagramPacket( s.getBytes(), s.getBytes().length, destination, port );
			socket.send( sendPacket );
			
			System.out.println("AFTER CONVERTING TO INT: " + get_int(s.getBytes()));
			System.out.println("AFTER CONVERTING TO STRING: " + get_istring(get_int(s.getBytes())));
			System.out.print( "Enter file name: " );
		}*/
	}
	private static boolean isEmpty(){
		synchronized(hashArray){
			for(int i=0; i<hashArray.length; i++){
				if(hashArray[i]!=null){
					return false;
				}
			}
		}
		return true;
	}
	//checks if window is completely empty
}
