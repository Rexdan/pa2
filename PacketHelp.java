import com.sun.org.apache.bcel.internal.util.ByteSequence;

public class PacketHelp
{
	/*
	 * Designed packet structure:
	 * First 4 bytes are the sequence number. (Index 0-3 inclusive)
	 * Next 4 Bytes are the sender address. (Index 4-7 inclusive)
	 * Next 1 Byte. (Index 8 inclusive)
	 * Next 4 Byes are the total payload length with header (Index 9-12 inclusive)
	 * Next 1 Byte is the checksum (Index 13 inclusive)
	 * Next 1 Byte is the Flag(Index 14 inclusive)
	 * Remaining packet size is the headerless payoad. (15-)
	 */
	//We may include a checksum byte and a byte for flagging the end of file.
	
	public static byte [] makePacket(int seq, String ip, char color, byte checksum, byte flag, byte [] payLoad)
	{	
		int length = payLoad.length +15;
		
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
		int breakpoint1 = ip.indexOf(".");
		int breakpoint2 = ip.indexOf(".", breakpoint1+1);
		int breakpoint3 = ip.indexOf(".", breakpoint2+1);
		byte0 = ip.substring(0, breakpoint1);
		byte1 = ip.substring(breakpoint1+1, breakpoint2);
		byte2 = ip.substring(breakpoint2+1, breakpoint3);
		byte3 = ip.substring(breakpoint3+1);
		bytes[4] = Byte.parseByte(byte0);
		bytes[5] = Byte.parseByte(byte1);
		bytes[6] = Byte.parseByte(byte2);
		bytes[7] = Byte.parseByte(byte3);
		//encodes ip address (can be sender or receiver depending on context)
		
		
		bytes[8] = Byte.parseByte(Character.getNumericValue(color)+"");
		//'r' for red; 'b' for blue
		//encodes color char
		
		
		d = length & 0x000000ff;
		c = (length >> 8) & 0x000000ff;
		b = (length >> 16) & 0x000000ff;
		a = (length >> 24) & 0x000000ff;
		bytes[9] = (byte) a;
		bytes[10] = (byte) b;
		bytes[11] = (byte) c;
		bytes[12] = (byte) d;
		//encodes the length into the packet
		
		bytes[13] = checksum;
		//copies the checksum into the packet
		
		bytes[14] = flag;
		//copies the flag into the packet
		
		for(int i=15; i<length; i++){
			bytes[i] = payLoad[i-15];
		}
		//bundles the payload with the header
		
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
	
	public static String getIP(byte [] bytes)
	{
		byte [] ip = new byte[4];
		ip[0] = bytes[4];
		ip[1] = bytes[5];
		ip[2] = bytes[6];
		ip[3] = bytes[7];
		int temp = bytesToInt(ip);
		return intToIP(temp);
	}
	
	public static char getColor(byte [] bytes)
	{
		byte [] byteChar = new byte[1];
		byteChar[0] = bytes[8];
		String s = new String(byteChar); // possibly with a charset
		return s.charAt(0);
	}
	
	public static byte [] getPayLoad(byte [] bytes, int length) /*Length Refers to FULL Payload Length*/
	{
		byte [] payLoad = new byte[length-15];
		
		for(int i = 0; i < payLoad.length; i++)
		{
			payLoad[i] = bytes[i+15];
		}
		return payLoad;
	}
}