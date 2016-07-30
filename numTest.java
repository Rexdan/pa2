
public class numTest 
{
	public static int bytesToInt( byte[] b4 )
	{
		int result = 0;
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a,b,c,d;
		
		Integer length = 1191;
		byte [] bytes = new byte[4];
		
		d = length & 0x000000ff;
		c = (length >> 8) & 0x000000ff;
		b = (length >> 16) & 0x000000ff;
		a = (length >> 24) & 0x000000ff;
		bytes[0] = (byte) a;
		bytes[1] = (byte) b;
		bytes[2] = (byte) c;
		bytes[3] = (byte) d;

		System.out.println("After converting: " + bytesToInt(bytes));

	}

}
