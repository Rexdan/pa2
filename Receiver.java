
import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Receiver implements Runnable
{
	private static String perc = "";
	private static PacketInfo[] window = new PacketInfo[10];
	public static DatagramSocket socket;
	public static DatagramSocket nackSocket;
	public static 	FileOutputStream fileOutputStream = null;
	public static String senderIP = "";
	public static byte [] ch = new byte[1024 + 19];
	public static int port;
	public static boolean isFirstPacket = true;

	private static void sendNACK(char c ,int seq) throws Exception /*OPTION 2 For Protocol*/
	{
		InetAddress ip = InetAddress.getLocalHost();
		String addr = ip.getHostAddress();
		byte [] temp = new byte[1024];
		ch = PacketHelp.makePacket(seq, addr, c, (byte) 0, (byte) 0, 3003, temp);
		DatagramPacket toSend = new DatagramPacket(ch, 0, ch.length);
		System.out.println("Sending Nack: " + PacketHelp.getSequenceNumber(ch));
		/*System.out.println("Packet's length: " + toSend.getLength());
		System.out.println("Socket's current buffer size: " + nackSocket.getReceiveBufferSize());*/
		nackSocket.send(toSend);
	}
	
	private static boolean packetLossEmulator()
	{
		String s = perc;
		double percentage = 0;
		percentage = (double)Integer.parseInt(s);
		percentage /= 100;
		Random r = new Random();
		
		float chance = r.nextFloat();
		/*Here we return true that it SHOULD lose the packet.*/
		if (chance < percentage)
		{
			return true;
		}
		else return false;
	}

	public static void main(String[] args) throws Exception 
	{
		if(args.length < 2)
		{
			System.err.println("You must enter the arguments in the correct format.");
		}
		else
		{
			try
			{
				port = Integer.parseInt(args[0]);
				perc = args[1];
				if(port == 3003)
				{
					System.out.println("Cannot use port number 3003. Please restart the program with another port number.");
					System.exit(1);
				}
				if(Integer.parseInt(perc) < 0 || Integer.parseInt(perc) > 100)
				{
					System.err.println("Please enter a percentage between 0 and 100");
					System.exit(1);
				}
			}catch(NumberFormatException e)
			{
				System.err.println("Program exited. Please enter an integer as the port number.");
				System.exit(1);
			}
		}
		int max =2147483639;
		/*...............................................................REMEMBER TO CHANGE BACK TO ARGS[0]*/
		
		socket = new DatagramSocket( port );
		nackSocket = new DatagramSocket(3003);
		//nackSocket.connect(InetAddress.getByName(senderIP), 3002);
		//socket.connect(InetAddress.getLocalHost(), 3000);
		/*DatagramSocket nSocket = new DatagramSocket();
		nSocket.connect(InetAddress.getLocalHost(), 3001);*/
		//double check if my socket syntax is doing what I think its doing
		//nSocket.setReuseAddress(true);
		socket.setReuseAddress( true );
		
		/*
		 * Don't we need some of the bottom variables inside of the while loop?
		 * How is it going to keep getting new packets?
		 */
		
		byte []	payload = new byte[1024 + 19];
		byte [] fileBytes;
		
		/*if(packetLossEmulator())
		{
			
		}*/
		DatagramPacket	receivePacket = new DatagramPacket(payload, payload.length);
		DatagramPacket nackPack;
		String fileName = "";
		File file = null;
		boolean needName = true;
		boolean receiver = false;
		int stillNeed = 0;
		char colorNeed  ='r';
		//System.out.println("Marker 1");
		socket.setSoTimeout(15000);
		
		int debug = 0;
		
		boolean terminate = false;
		boolean completeExit = false;
		
		while(completeExit == false)
		{
			stillNeed = 0;
			colorNeed = 'r';
			InetAddress ip = InetAddress.getLocalHost();
			String addr = ip.getHostAddress();
			ch = PacketHelp.makePacket(0, addr, 'r', (byte) 0, (byte) 1, port, ch);
			terminate = false;
			while( !terminate ) /*what would be a good terminating condition for this? Did you forget about the EOF packet?*/
			{
				System.out.println("Marker 2");
				
				int count = 0;
				while(count < 10)
				{
					debug++;
					try{
						
						socket.receive( receivePacket );
						System.out.println("Received packet sequence number: " + PacketHelp.getSequenceNumber(receivePacket.getData()));
					}
					catch(SocketTimeoutException e){
						System.out.println("Sender Unresponsive");
						completeExit = true;
						break;
					}
					/*if(debug == 11)
					{
						System.out.println("This is the debug 11 sequence number: " + PacketHelp.getSequenceNumber(receivePacket.getData()));
					}*/
					//int sequence = PacketHelp.getSequenceNumber(receivePacket.getData());
					
					if(PacketHelp.compareData(receivePacket.getData(), ch) == -1)
					{
						continue;
					}		
				//	System.out.println("Attempting to place: " + PacketHelp.getSequenceNumber(receivePacket.getData()));
	
					placingPacket(receivePacket);
					count++;
				}
				/*if(terminate)
				{
					System.out.println("Socket Timeout...");
					break;
				}*/
			//	System.out.println("Count Check: "+count);
				//System.out.println("Marker 3");
			//	System.out.println("Checking fileName packet: " + PacketHelp.getFlag(window[0].toSend));
				double status1 = checkStatus(window);
			//	System.out.println("Status is: " + status1);
				int status2;
				boolean hasFirst = false;
				
				if(status1 % 1 == 0 )
				{
					status2 = (int)status1;
				}
				else
				{
					status2 = (int)(status1 - .5);
					hasFirst = true;
				}
				//System.out.println("Status1 check: " + status1);
				/*int status1 = checkStatus(window);
				System.out.println("Status1 check: " + status1);
				int status2;*/
				
				if(hasFirst){ //implies that the first packet is within the window
					//status2 = status1 - 100;
					//System.out.println("Marker 4");
	
					if(status2 < 0){ //case where a packet is missing in the window
						status2 = (status2+10)*-1; //converts the negative number to the index of the missing packet in the window
						if(status2!=0){//General case.
							stillNeed = PacketHelp.getSequenceNumber(window[status2-1].toSend) + 1;
							if(stillNeed > max)
							{
								stillNeed = 0;
								if(colorNeed == 'r'){
									 colorNeed= 'b';
								}
								else{
									colorNeed = 'r';
								}
							}
						}
						sendNACK(colorNeed, stillNeed);
						//System.out.println("Marker 4a");
						continue;
					}
					else if((status2 >= 0) && (status2 <= 9)){ //this implies the last packet is contained within the window
						try
						{		//System.out.println("Marker 4b");
	
							byte [] data = window[0].toSend;
							int dataLength = PacketHelp.getLength(data);
							byte[] temp = PacketHelp.getPayLoad(data, dataLength);
							fileName = new String(temp);
							String temp1 = "new-"+fileName;
							byte [] tempy = temp1.getBytes();
							String blah = new String(tempy);
							//System.out.println(blah);
							file = new File(blah);
							file.createNewFile();
							System.out.println("File created");
							fileOutputStream = new FileOutputStream(file, true); 
							//senderIP = PacketHelp.getIP(data);
							//nackSocket.connect(InetAddress.getByName(senderIP), PacketHelp.getPort(data));
							InetAddress cnct = InetAddress.getByName(senderIP);
							//System.out.println("This is the IP address on the Receiver: " + senderIP);
							//socket.connect(cnct, 3001);
							window[0] = null;
							stillNeed++;		
							//System.out.println("Marker 4c");
						}catch(Exception e)
						{
							System.err.println(e);
						}
						writeToFile(1, status2, file);
						stillNeed = window[status2].seq+1;
						if(stillNeed>max){
							stillNeed = 0;
							if(colorNeed == 'r'){
								 colorNeed= 'b';
							}
							else{
								colorNeed = 'r';
							}
								
						}
						for(int i=1; i<status2+1; i++){
							window[i] = null;
						}
						//fileOutputStream.close();
						sendNACK(colorNeed, stillNeed);
						terminate = true;
					}
					else{ //implies window is full, but no last packet within
						try
						{
							//System.out.println("Marker 5");
	
							byte [] data = window[0].toSend;
							int dataLength = PacketHelp.getLength(data);
							byte[] temp = PacketHelp.getPayLoad(data, dataLength);
							fileName = new String(temp);
							String temp1 = "new-"+fileName;
							byte [] tempy = temp1.getBytes();
							String blah = new String(tempy);
							//System.out.println(blah);
							file = new File(blah);
							file.createNewFile();
							System.out.println("File created");
							fileOutputStream = new FileOutputStream(file, true); 
							//senderIP = PacketHelp.getIP(data);
							//nackSocket.connect(InetAddress.getByName(senderIP), PacketHelp.getPort(data));
							InetAddress cnct = InetAddress.getByName(senderIP);
							//System.out.println("This is the IP address on the Receiver: " + senderIP);
							//socket.connect(cnct, 3001);
							window[0] = null;
							stillNeed++;
						}catch(Exception e)
						{
							System.err.println(e);
						}
						writeToFile(1, 9, file);
						stillNeed = window[9].seq+1;
						if(stillNeed>max){
							stillNeed = 0;
							if(colorNeed == 'r'){
								 colorNeed= 'b';
							}
							else{
								colorNeed = 'r';
							}
								
						}
						for(int i=1; i<10; i++){
							window[i] = null;
						}
						sendNACK(colorNeed, stillNeed);
						continue;
					}
					
				}
				else {
					//System.out.println("Marker 6");
	
					if(status2 < 0){ //no first packet within window, and we're missing a packet in the window
						status2 = (status2+10)*(-1);
						if(status2!=0){//General case.
							stillNeed = PacketHelp.getSequenceNumber(window[status2-1].toSend) + 1;
							if(stillNeed > max)
							{
								stillNeed = 0;
								if(colorNeed == 'r'){
									 colorNeed= 'b';
								}
								else{
									colorNeed = 'r';
								}
							}
						}
						sendNACK(colorNeed, stillNeed);
						continue;
					}
					else if((status2 >= 0) && (status2 <= 9) ){//last packet is within window
					//	System.out.println("Marker 6a");
						//status2 = status1; 
						writeToFile(0, status2, file);
						stillNeed = window[status2].seq+1;
						if(stillNeed>max){
							stillNeed = 0;
							if(colorNeed == 'r'){
								 colorNeed= 'b';
							}
							else{
								colorNeed = 'r';
							}
								
						}
						for(int i=0; i<status2+1; i++){
							window[i] = null;
						}
						sendNACK(colorNeed, stillNeed);
						//fileOutputStream.close();
						terminate = true;
					}
					else{ //no beginning packet, no end packet, everything is filled, general case
						status2 = 9;
						//System.out.println("Marker 6b");
	
						writeToFile(0, status2, file);
						stillNeed = window[status2].seq+1;
						if(stillNeed>max){
							stillNeed = 0;
							if(colorNeed == 'r'){
								 colorNeed= 'b';
							}
							else{
								colorNeed = 'r';
							}
								
						}
						for(int i=0; i<status2+1; i++){
							window[i] = null;
						}
						sendNACK(colorNeed, stillNeed);
						continue;
					}
					
				}
			}
			fileOutputStream.close();
		}
	}
	public static void writeToFile(int start, int end, File file) throws IOException{
		byte [] temp;

		for(int i = start; i < end+1; i++){
			
			temp = PacketHelp.getPayLoad(window[i].toSend, window[i].toSend.length);

			try
			{ 
			    
			    fileOutputStream.write(temp);
			   // System.out.println("Wrote into file: " + file.length());
			    
			    
			}catch(Exception e)
			{
				e.printStackTrace();
				System.err.println("Something went wrong when we tried to get the fileOutPutStream...");
			}
		}
		//System.out.println("fileBytes.length after Copying: " + fileBytes.length);
		//System.out.println("Temp.length after Copying: " + temp.length);
		
	}
	public static double checkStatus(PacketInfo [] window){
		int i;
		double a=0;
		for(i=0; i<window.length; i++){
			if(window[i]==null){
				return (i*-1)-10;
			}
			else if (PacketHelp.getFlag(window[i].toSend)==1){
				a+=.5;
			}
			else if(PacketHelp.getFlag(window[i].toSend)==2){
				//System.out.println("The End is Nigh");
				return a+i;
			}
		}
		return a+i;
	}
	/*
	 * The interpretations of the return values are ugly, but I believe it will simplify things for us in the other parts of code:
	 * 
	 * checks window if it is full and needs to be placed into the file
	 * returns (-10 -> -19) if a window slot was empty
	 * returns 10 if it is full (standard case)
	 * returns an index (0-9) if an end of file was found
	 * all ranges given are inclusive
	 * adds an extra 100 to return value if there is a beginning packet contained in the window
	 * unless the window is incomplete
	 */
	public static boolean placingPacket(DatagramPacket packet)
	{
		byte [] data = packet.getData();
		int length = PacketHelp.getLength(data);
		byte [] temp = Arrays.copyOfRange(data, 0, length);
		PacketInfo p = new PacketInfo(Arrays.copyOfRange(data, 0, length));
		boolean checkSum = PacketHelp.checkTheSum(temp);
		
		try
		{
			if(isFirstPacket)
			{
				senderIP = PacketHelp.getIP(data);
				nackSocket.connect(InetAddress.getByName(senderIP), PacketHelp.getPort(data));
				isFirstPacket = false;
			}
		}catch(Exception e)
		{
			System.err.println("Something went wrong when trying to get the Sender's IP address.");
		}
		
		
		if(packetLossEmulator())
		{
			return false;
		}
		
		if(!checkSum)
		{
			return false;
		}
		
		for(int i = 0; i < window.length; i++)
		{
			if(window[i] == null)
			{
				if((p.seq % 10) == i)
				{
					window[i] = p;
					System.out.println("Placement successful.");
					return true;
				}
			}
			else if(window[i].equals(p))
			{
				return false;
			}
		}
		return false;
	}
	
	@Override
	public void run()
	{
		/*
		 * 
		 */
	}
}