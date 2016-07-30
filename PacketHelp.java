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
	 * Next 4 Bytes are the Port Number (Index 15-18)
	 * Remaining packet size is the headerless payoad. (19-)
	 */
	//We may include a checksum byte and a byte for flagging the end of file.
	
	public static byte [] makePacket(int seq, String ip, char color, byte checksum, byte flag, int port, byte [] payLoad)
	{	
		int length = payLoad.length +19;
		
		System.out.println("Length: " + length);
		
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
		byte [] ipBytes = ip.getBytes();
		bytes[4] = ipBytes[0];
		bytes[5] = ipBytes[1];
		bytes[6] = ipBytes[2];
		bytes[7] = ipBytes[3];
		//System.out.println("Size of ip address: " + ipBytes.length);
		
		/*int breakpoint1 = ip.indexOf(".");
		int breakpoint2 = ip.indexOf(".", breakpoint1+1);
		int breakpoint3 = ip.indexOf(".", breakpoint2+1);
		byte0 = ip.substring(0, breakpoint1);
		byte1 = ip.substring(breakpoint1+1, breakpoint2);
		byte2 = ip.substring(breakpoint2+1, breakpoint3);
		byte3 = ip.substring(breakpoint3+1);
		a = (int) Integer.parseInt(byte0);
		b = (int) Integer.parseInt(byte1);
		c = (int) Integer.parseInt(byte2);
		d = (int) Integer.parseInt(byte3);
		bytes[4] = (byte) (a & 0xFF);
		bytes[5] = (byte) (b & 0xFF);
		bytes[6] = (byte) (c & 0xFF);
		bytes[7] = (byte) (d & 0xFF);*/
		//encodes ip address (can be sender or receiver depending on context)
		
		
		bytes[8] = (byte) (color & 0xFF);
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
		
		d = port & 0x000000ff;
		c = (port >> 8) & 0x000000ff;
		b = (port >> 16) & 0x000000ff;
		a = (port >> 24) & 0x000000ff;
		bytes[15] = (byte) a;
		bytes[16] = (byte) b;
		bytes[17] = (byte) c;
		bytes[18] = (byte) d;
		
		for(int i=19; i<length; i++){
			bytes[i] = payLoad[i-19];
		}
		//bundles the payload with the header
		
		return bytes;
	}
	
	public static int bytesToInt( byte[] b4 )
	{
		int result = 0;
		/*Byte bt;
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
		}*/
		byte a = b4[0];
		byte b = b4[1];
		byte c = b4[2];
		byte d = b4[3];
		int e = (int) a & 0xFF;
		int f = (int) b & 0xFF;
		int g = (int) c & 0xFF;
		int h = (int) d & 0xFF;
		e = e<<24;
		f = f<<16;
		g = g<<8;
		result = e+f+g+h;
		
		return result;
	}
	
	/*public static String intToIP( int x )
	{
		int			a,b,c,d;
		d = x & 0x000000ff;
		c = (x >> 8) & 0x000000ff;
		b = (x >> 16) & 0x000000ff;
		a = (x >> 24) & 0x000000ff;
		//System.out.println( "a is " + a );
		return new String( a + "." + b + "." + c + "." + d );
	}*/
	
	public static int getLength(byte [] bytes)
	{
		byte length[] = new byte[4];
		length[0] = bytes[9];
		length[1] = bytes[10];
		length[2] = bytes[11];
		length[3] = bytes[12];
		System.out.println("getLength PROBLEM CHECK: " + bytesToInt(length));
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
		int a = (int) ip[0] & 0xFF;
		int b = (int) ip[1] & 0xFF;
		int c = (int) ip[2] & 0xFF;
		int d = (int) ip[3] & 0xFF;
		String temp = a + "." + b + "." + c + "." + d;
		return temp;
	}
	public static int getPort(byte [] bytes)
	{
		byte [] p = new byte[4];
		p[0] = bytes[15];
		p[1] = bytes[16];
		p[2] = bytes[17];
		p[3] = bytes[18];
		return bytesToInt(p);
	}
	
	public static char getColor(byte [] bytes)
	{
		byte [] byteChar = new byte[1];
		byteChar[0] = bytes[8];
		char c = (char) (byteChar[0] & 0xFF);
		return c;
	}
	public static boolean checkTheSum(byte [] bytes){
		if(bytes[13]==0){
			return true;
		}
		else{
			return false;
		}
	}
	public static byte getFlag(byte[] bytes){
		byte b = bytes[14];
		return b;
	}
	public static byte [] getPayLoad(byte [] bytes, int length) /*Length Refers to FULL Payload Length*/
	{
		byte [] payLoad = new byte[length-19];
		
		for(int i = 0; i < payLoad.length; i++)
		{
			payLoad[i] = bytes[i+19];
		}
		return payLoad;
	}	
	public static int compareData(byte[] a, byte[] b){
		int max = 2147483639;
		int smallStart = 0;
		int smallEnd = max/2;
		int bigStart = max/2+1;
		int bigEnd = max;
		char colA = getColor(a);
		char colB = getColor(b);
		int seqA = getSequenceNumber(a);
		int seqB = getSequenceNumber(b);
		boolean isSmallA;
		boolean isSmallB;
		
		if(smallStart<=seqA && seqA<=bigStart){
			isSmallA = true;
		}
		else{
			isSmallA = false;
		}
		if(smallStart<=seqB && seqB<=bigStart){
			isSmallB = true;
		}
		else{
			isSmallB = false;
		}
		
		if(getColor(a)==getColor(b)){
			if(getSequenceNumber(a) == getSequenceNumber(b)){
				return 0;
			}
			else if(getSequenceNumber(a) < getSequenceNumber(b)){
				return 1;
			}
			else{
				return -1;
			}
		}
		else{
			if((!isSmallA && (colA=='r')) && ((isSmallB) && colA=='b')){
				return -1;
			}
			else if((isSmallA && (colA=='r')) && ((!isSmallB) && colA=='b')){
				return 1;
			}
			else if((isSmallA && (colA=='b')) && ((!isSmallB) && colA=='r')){
				return 1;
			}
			else if((!isSmallA && (colA=='b')) && ((isSmallB) && colA=='r')){
				return -1;
			}
			return -2;
		}
	}
	//compares packetData: 
	//returns -1 if the first PacketData comes before
	//returns 1 if the first  PacketData comes After
	//returns 0 if they are the same packetData labeling
	//returns -2 if there is an error
}