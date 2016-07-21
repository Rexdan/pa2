public class PacketHelp
{
	public PacketHelp()
	{
		
	}
	
	public PacketHelp(byte [] bytes)
	{
		
	}
	
	public static byte [] makePacket(int seq/*, String sender, String receiver, byte [] payLoad*/)
	{
		byte [] bytes = new byte[4 /*16 + payLoad.length*/];
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
		return bytes;
	}
	
	static int get_int( byte[] b4 )
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
	
	static String get_istring( int x )
	{
		int			a,b,c,d;
		d = x & 0x000000ff;
		c = (x >> 8) & 0x000000ff;
		b = (x >> 16) & 0x000000ff;
		a = (x >> 24) & 0x000000ff;
		//System.out.println( "a is " + a );
		return new String( a + "." + b + "." + c + "." + d );
	}
	
	public int getLength(byte [] bytes)
	{
		byte length[] = new byte[4];
		length[0] = bytes[12];
		length[1] = bytes[13];
		length[2] = bytes[14];
		length[3] = bytes[15];
		return get_int(length);
	}
	
	public int getSequenceNumber(byte [] bytes)
	{
		byte [] sequenceNum = new byte[4];
		sequenceNum[0] = bytes[0];
		sequenceNum[1] = bytes[1];
		sequenceNum[2] = bytes[2];
		sequenceNum[3] = bytes[3];
		return get_int(sequenceNum);
	}
	
	public String getSenderIP(byte [] bytes)
	{
		byte [] senderIP = new byte[4];
		senderIP[0] = bytes[4];
		senderIP[1] = bytes[5];
		senderIP[2] = bytes[6];
		senderIP[3] = bytes[7];
		int temp = get_int(senderIP);
		return get_istring(temp);
	}
	
	public String getReceiverIP(byte [] bytes)
	{
		byte [] receiverIP = new byte[4];
		receiverIP[0] = bytes[8];
		receiverIP[1] = bytes[9];
		receiverIP[2] = bytes[10];
		receiverIP[3] = bytes[11];
		int temp = get_int(receiverIP);
		return get_istring(temp);
	}
	
	public byte [] getPayLoad(byte [] bytes, int length) /*Length Refers to FULL Packet Length*/
	{
		byte [] payLoad = new byte[length-16];
		
		for(int i = 0; i < payLoad.length; i++)
		{
			payLoad[i] = bytes[i+16];
		}
		return payLoad;
	}
}