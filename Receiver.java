import	java.util.*;
import	java.io.*;
import	java.net.*;

public class Receiver implements Runnable
{
	private static String perc = "";
	private static PacketInfo[] window = new PacketInfo[10];
	
	private void NACK() /*OPTION 2 For Protocol*/
	{
		
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
		if (chance > percentage)
		{
			return true;
		}
		else return false;
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
		
		//perc = args[1];
		DatagramSocket	socket = new DatagramSocket( 3001 );
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
		
		byte []	payload = new byte[2048 + 19];
		byte [] fileBytes;
		
		/*if(packetLossEmulator())
		{
			
		}*/
		DatagramPacket	receivePacket = new DatagramPacket(payload, payload.length);
		DatagramPacket nackPack;
		String fileName = "";
		File file = null;
		boolean needName = true;
		int stillNeed = 0;
		
		FileOutputStream fileOutputStream = null;
		
		RandomAccessFile raf;
		boolean terminate = false;
		
		while( !terminate ) /*what would be a good terminating condition for this? Did you forget about the EOF packet?*/
		{
			socket.receive( receivePacket );
			System.out.println("After receiving packet.");
			byte [] data = receivePacket.getData();
						
			int seq = PacketHelp.getSequenceNumber(data);
			System.out.println("seq: " + seq);
			
			int dataLength = PacketHelp.getLength(data);
			System.out.println("Size of Data Byte array: " + dataLength);
			
			byte[] temp = PacketHelp.getPayLoad(data, dataLength);
			
			if(seq == 0 && needName){ //case of first packet
				//temp = PacketHelp.getPayLoad(data, data.length);
				fileName = new String(temp);
				System.out.println("temp.length: "+ temp.length);
				System.out.println("fileName: "+ fileName + "length: "+ fileName.length());
				String tempName =fileName;
				if(!PacketHelp.checkTheSum(data)){
					/* nack the packet here
					 * I think we should call a method each time we have to NACK.
					 * It's cleaner and avoids having to constantly copy the same three lines of code.
					 * 
					 * */
					temp = PacketHelp.makePacket(seq, InetAddress.getLocalHost().toString(), 'r', (byte) 0, (byte) 1, PacketHelp.getPort(data), data);
					nackPack = new DatagramPacket( temp, temp.length, InetAddress.getByName(PacketHelp.getIP(data)), PacketHelp.getPort(data)  );
					socket.send(nackPack);
				}
				else{
					/*
					 * fileName = new String(temp);
					 * file = new File(System.getProperty("user.dir")+ "\\"+ fileName);
					 * raf = new RandomAccessFile(file, "rw");
					*/
					/*if(window[PacketHelp.getSequenceNumber(data)%10] == null){
						window[PacketHelp.getSequenceNumber(data)%10] = new PacketInfo(data);
					}*/
					/*ignore duplicate packet*/
					
					try
					{
						String temp1 = "new-"+fileName; /*Get Rid of new- *************************************************************************Get Rid of new- ******/
						byte [] tempy = temp1.getBytes();
						String blah = new String(tempy);
						System.out.println(blah);
						file = new File(blah);
						file.createNewFile();
						System.out.println("File created...");
					}catch(Exception e)
					{
						System.err.println(e);
					}
					
					/*Creates the file with the given name. I tested it on our Test class and it creates the file.*/
				}
			}
			else
			{
				
				fileBytes = Arrays.copyOfRange(temp, 0, temp.length);
				System.out.println("fileBytes.length after Copying: " + fileBytes.length);
				System.out.println("Temp.length after Copying: " + temp.length);

				try
				{ 
				    fileOutputStream = new FileOutputStream(file); 
				    fileOutputStream.write(fileBytes);
				    System.out.println("Wrote into file. File size is: " + file.length());
				    
				}catch(Exception e)
				{
					System.err.println("Something went wrong when we tried to get the fileOutPutStream...");
				}
				finally 
				{
					fileOutputStream.close();
				}
			}
		}
	}
	public static int checkStatus(PacketInfo [] window){
		int i;
		int a=0;
		for(i=0; i<window.length; i++){
			if(window[i]==null){
				return (i*-1)-10;
			}
			else if (PacketHelp.getFlag(window[i].toSend)==1){
				a+=100;
			}
			else if(PacketHelp.getFlag(window[i].toSend)==2){
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
	 * adds an extra 100 to return value if there is a beginning of file contained in the window
	 * unless the window is incomplete
	 */
	@Override
	public void run()
	{
		/*
		 * 
		 */
	}
}