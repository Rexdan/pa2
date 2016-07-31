import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class StatusTest {
	
	private static PacketInfo[] window = new PacketInfo[10];
	
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
	
	public static boolean placingPacket(DatagramPacket packet)
	{
		byte [] data = packet.getData();
		int length = PacketHelp.getLength(data);
		PacketInfo p = new PacketInfo(Arrays.copyOfRange(data, 0, length));
		
		for(int i = 0; i < window.length; i++)
		{
			if(window[i] == null)
			{
				if((p.seq % 10) == i)
				{
					window[i] = p;
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

	public static void main(String[] args) throws UnknownHostException 
	{
		byte [] payLoad = new byte[1024];
		byte [] p1  = PacketHelp.makePacket(0, InetAddress.getLocalHost().toString(), 'r', (byte) 0 , (byte) 1, 3001, payLoad);
		byte [] p2  = PacketHelp.makePacket(1, InetAddress.getLocalHost().toString(), 'r', (byte) 0 , (byte) 0, 3001, payLoad);
		byte [] p3  = PacketHelp.makePacket(2, InetAddress.getLocalHost().toString(), 'r', (byte) 0 , (byte) 0, 3001, payLoad);
		byte [] p4  = PacketHelp.makePacket(3, InetAddress.getLocalHost().toString(), 'r', (byte) 0 , (byte) 2, 3001, payLoad);
		DatagramPacket pack1 = new DatagramPacket(p1, p1.length);
		DatagramPacket pack2 = new DatagramPacket(p2, p2.length);
		DatagramPacket pack3 = new DatagramPacket(p3, p3.length);
		DatagramPacket pack4 = new DatagramPacket(p4, p4.length);
		placingPacket(pack1);
		placingPacket(pack2);
		placingPacket(pack3);
		placingPacket(pack4);
		/*window[1] = new PacketInfo(p1);
		window[0] = new PacketInfo(p2);
		window[3] = new PacketInfo(p3);
		window[2] = new PacketInfo(p4);*/
		System.out.println("checkStatus: " + checkStatus(window));
	}

}
































//System.out.println("After receiving packet.");
			
int seq = PacketHelp.getSequenceNumber(data);
//System.out.println("seq: " + seq);

//System.out.println("Size of Data Byte array: " + dataLength);



if(seq == 0 && needName){ //case of first packet
	//temp = PacketHelp.getPayLoad(data, data.length);
	//System.out.println("temp.length: "+ temp.length);
	//System.out.println("fileName: "+ fileName + "length: "+ fileName.length());
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
		/*if(window[PacketHelp.getSequenceNumber(data)%10] == null){
			window[PacketHelp.getSequenceNumber(data)%10] = new PacketInfo(data);
		}*/
		
		try
		{
			String temp1 = "new-"+fileName;
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
	
	
}
}
