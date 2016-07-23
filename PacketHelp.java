public class PacketHelp
{
	/*
	 * Designed packet structure:
	 * First 4 bytes are the sequence number. (Index 0-3 inclusive)
	 * Next 4 Bytes are the sender address. (Index 4-7 inclusive)
	 * Next 4 Bytes are the receiver address. (Index 8-11 inclusive)
	 * Next 4 Byes are the total packets length (Index 12-15 inclusive)
	 * Next 1 Byte is the checksum (Index 16 inclusive)
	 * Next 1 Byte is the Flag(Index 17 inclusive)
	 * Remaining packet size is the payload. (16-)
	 */
	//We may include a checksum byte and a byte for flagging the end of file.
	
	public static byte [] makePacket(int seq, String senderIP, String receiverIP, byte checksum, byte flag, byte [] payLoad)
	{	
		int length = payLoad.length +18;
		
		byte [] bytes = new byte[length];
		Integer sequence = seq;
		int a,b,c,d;
		d = sequence & 0x000000ff;
		c = (sequence >> 8) & 0x000000ff;
		b = (sequence >> 16) & 0x000000ff;
		a = (sequence >> 24) & 0x000000ff;
		bytes[0] = (byte) a;
		bytes[1] = (byte) b;
		bytes[2] = (byte) c;
		bytes[3] = (byte) d;
		/*encodes sequence number*/
		
		String byte0;
		String byte1;
		String byte2;
		String byte3;
		int breakpoint1 = senderIP.indexOf(".");
		int breakpoint2 = senderIP.indexOf(".", breakpoint1+1);
		int breakpoint3 = senderIP.indexOf(".", breakpoint2+1);
		byte0 = senderIP.substring(0, breakpoint1);
		byte1 = senderIP.substring(breakpoint1+1, breakpoint2);
		byte2 = senderIP.substring(breakpoint2+1, breakpoint3);
		byte3 = senderIP.substring(breakpoint3+1);
		bytes[4] = Byte.parseByte(byte0);
		bytes[5] = Byte.parseByte(byte1);
		bytes[6] = Byte.parseByte(byte2);
		bytes[7] = Byte.parseByte(byte3);
		//encodes sender ip address
		
		
		breakpoint1 = receiverIP.indexOf(".");
		breakpoint2 = receiverIP.indexOf(".", breakpoint1+1);
		breakpoint3 = receiverIP.indexOf(".", breakpoint2+1);
		byte0 = receiverIP.substring(0, breakpoint1);
		byte1 = receiverIP.substring(breakpoint1+1, breakpoint2);
		byte2 = receiverIP.substring(breakpoint2+1, breakpoint3);
		byte3 = receiverIP.substring(breakpoint3+1);
		bytes[8] = Byte.parseByte(byte0);
		bytes[9] = Byte.parseByte(byte1);
		bytes[10] = Byte.parseByte(byte2);
		bytes[11] = Byte.parseByte(byte3);
		//encodes receiver ip address
		
		
		d = length & 0x000000ff;
		c = (length >> 8) & 0x000000ff;
		b = (length >> 16) & 0x000000ff;
		a = (length >> 24) & 0x000000ff;
		bytes[12] = (byte) a;
		bytes[13] = (byte) b;
		bytes[14] = (byte) c;
		bytes[15] = (byte) d;
		//encodes the length into the packet
		
		bytes[16] = checksum;
		//copies the checksum into the packet
		
		bytes[17] = flag;
		//copies the flag into the packet
		
		for(int i=16; i<length; i++){
			bytes[i] = payLoad[i-16];
		}
		//copies the payload bytes into the packet
		
		return bytes;
	}
	
	public static int bytesToInt( byte[] b4 )
	{
		int result = 0;
		Byte bt;
		//System.out.println( "b4[0] is " + b4[0] );
		//System.out.println( "(b4[0] << 24) is " + (b4[0] << 24) );
		
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
	
	public static String intToIP( int x )
	{
		int			a,b,c,d;
		d = x & 0x000000ff;
		c = (x >> 8) & 0x000000ff;
		b = (x >> 16) & 0x000000ff;
		a = (x >> 24) & 0x000000ff;
		//System.out.println( "a is " + a );
		return new String( a + "." + b + "." + c + "." + d );
	}
	
	public static int getLength(byte [] bytes)
	{
		byte length[] = new byte[4];
		length[0] = bytes[12];
		length[1] = bytes[13];
		length[2] = bytes[14];
		length[3] = bytes[15];
		return bytesToInt(length);
	}
	
	public static int getSequenceNumber(byte [] bytes)
	{
		byte [] sequenceNum = new byte[4];
		sequenceNum[0] = bytes[0];
		sequenceNum[1] = bytes[1];
		sequenceNum[2] = bytes[2];
		sequenceNum[3] = bytes[3];
		return bytesToInt(sequenceNum);
	}
	
	public static String getSenderIP(byte [] bytes)
	{
		byte [] senderIP = new byte[4];
		senderIP[0] = bytes[4];
		senderIP[1] = bytes[5];
		senderIP[2] = bytes[6];
		senderIP[3] = bytes[7];
		int temp = bytesToInt(senderIP);
		return intToIP(temp);
	}
	
	public static String getReceiverIP(byte [] bytes)
	{
		byte [] receiverIP = new byte[4];
		receiverIP[0] = bytes[8];
		receiverIP[1] = bytes[9];
		receiverIP[2] = bytes[10];
		receiverIP[3] = bytes[11];
		int temp = bytesToInt(receiverIP);
		return intToIP(temp);
	}
	
	public static byte [] getPayLoad(byte [] bytes, int length) /*Length Refers to FULL Packet Length*/
	{
		byte [] payLoad = new byte[length-18];
		
		for(int i = 0; i < payLoad.length; i++)
		{
			payLoad[i] = bytes[i+18];
		}
		return payLoad;
	}
}