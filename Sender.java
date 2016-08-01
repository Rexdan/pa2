import	java.io.*;
import	java.net.*;
import java.nio.file.*;
import java.util.*;

public class Sender{
	
	//HashMap<Integer, Packet> packets = new HashMap<>();
	
	
	//needs nack and timeout thread implementation to work!!!
	//do we have to deal with multiple dynamically changing receivers?
	
	int size;
	
	static File file;
	
	static byte [] toSend;
	
	public static DatagramSocket socket;
	
	public static Timer [] timers = new Timer[10];
	
	public static InetAddress destination;
	
	public static int port;

	public static DatagramSocket getSocket()
	{
		return socket;
	}
	
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
		/*for(int i=0; i<timers.length; i++){
			timers[i] = new Timer();
		}*/
		destination = InetAddress.getByName( "255.255.255.255" );
		DatagramPacket		sendPacket;
		socket = new DatagramSocket();
		BufferedReader		stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		port = 3001;
		String s /*= stdIn.readLine()*/;
		File file = null;
		FileInputStream fin;
		FileOutputStream fos;
		InputStream stream;
		byte [] fileBytes;
		hashArray = new PacketInfo[10];
		
		
		//starts nack thread
		
		//System.out.println( "datagram target is " + destination + " port " + port );
		System.out.print( "Enter File Name: " );
		socket.setBroadcast( true );
		
		Nack nThread = new Nack();
		nThread.start();
		
		InetAddress ip = InetAddress.getLocalHost();
		String addr = ip.getHostAddress();
		
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
			int max = 2147483639;
			int seq = 0;
			int from = -1024;
			int to = -1;
			PacketInfo packet;
			byte flag = 0;
			byte checksum = 0;
			char color = 'r';
			boolean terminate = false;
			int timIndex;
			while(!terminate)
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
				 * Size of payload should be 1024.
				 * 
				 */
				if(seq == 0)
				{
					/*Easily send name of file to Sender first.*/
					flag=1;
					//System.out.println("fileGetName: " + file.getName().getBytes() + "size: " + file.getName().getBytes().length);
					//System.out.println("This is the IP address on Sender: " + addr);
					toSend = PacketHelp.makePacket(seq, addr, color, checksum , flag, 3001, file.getName().getBytes());
					//System.out.println("After making first packet for name: " + toSend.length);
					sendPacket = new DatagramPacket( toSend, toSend.length, destination, port );
					synchronized(hashArray)
					{
						hashArray[seq%10] = new PacketInfo(toSend);
					}
					
					socket.send( sendPacket );
					timIndex = PacketHelp.getSequenceNumber(toSend)%10;
					synchronized(timers){
						timers[timIndex] = new Timer();
						timers[timIndex].schedule(new MyTimerTask(timIndex), 3000);
					}
					
					seq++;
					//System.out.println("seq: " + seq);
					//from = toSend.length;
					//to = toSend.length + 2047;
				}
				else
				{
					
					
					/*If the to index becomes greater than last index--we are at EOF*/
					int whatever = to + 1024;
					if(whatever >= (fileBytes.length - 1)){
						from += 1024;
						//System.out.println("filebytes.length: " + fileBytes.length);
						to = fileBytes.length - 1;
						//System.out.println("after filebytes.length: " + to);
						flag = 2;
						//System.out.println("HERE: if");
						terminate = true;
					}
					else{
						flag = 0;
						from += 1024;
						to += 1024;
						//System.out.println("HERE: ELSE");
					}
					
					/*increments boundaries, if/else check is for the upper boundary hitting the boundary of the fileBytes array*/
					//System.out.printf("From: %d To: %d \n", from, to);
					byte[] info =  Arrays.copyOfRange(fileBytes, from, to + 1);
					//System.out.println("from: " + from +"\n"+"to: " + to);
					toSend = PacketHelp.makePacket(seq, addr, color, checksum , flag, 3001, info);
					
					//System.out.println("toSend Length: " + toSend.length);
					
					sendPacket = new DatagramPacket( toSend, toSend.length, destination, port );
					socket.send( sendPacket );

					timIndex = PacketHelp.getSequenceNumber(toSend)%10;
					synchronized(timers){
						timers[timIndex] = new Timer();
						timers[timIndex].schedule(new MyTimerTask(timIndex), 3000);
					}

					//creates and sends packet
					
					synchronized(hashArray){
						hashArray[seq%10] = new PacketInfo(toSend);
					}
					//puts the packetinfo into hash array
					
					if(seq == max){
						seq = 0;
						if(color == 'r'){
							color = 'b';
						}
						else{
							color = 'r';
						}
					}
					else{ 
						//System.out.println("seq: " + seq);
						seq++;
					}
					/*switches color if overflow is about to happen and resets sequence.*/
					//System.out.println("seq: " + seq);
					if(seq % 10 == 0){
						//System.out.println("Nap time.");
						while(!isEmpty()){
							//Thread.sleep(100);
						}
						//System.out.println("WAKE UP.");
					}

					//System.out.println("AFTER ISEMPTY CHECK");
					/*
					 * checks to see if window(hash array) is empty, sleeps for a second if it's not
					 * in this way, it will only move onto the next window of 10 when all objects have been acknowledged
					 * 
					 * But all that you're doing here is checking if the current sequence number is going into the first index
					 * and putting the thread to sleep. I get what's going on, but nothing is being done in that block of code other
					 * than sleeping.
					 * 
					 * Need to implement something for resending the packets, i.e. the runnable method here.
					 * 
					 */
				}
			}
			//System.out.println( "Enter a File Name: " );
		}
		/*what is good termination condition for file query loop?*/
		
		nThread.terminate = true;
	}
	private static boolean isEmpty(){
		synchronized(hashArray){
			for(int i = 0; i < hashArray.length; i++){
				if(hashArray[i] != null){
					return false;
				}
			}
		}
		return true;
	}
	/*checks if window is completely empty*/
}
