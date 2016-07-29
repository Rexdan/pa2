public tempForPacketLoss
{
	public static void main(String [] args)
	{
		BufferedReader stdIn = new BufferedReader( new InputStreamReader( System.in ) );
		String s;
		double percentage = 0;
		
		Random r = new Random();
		
		System.out.print("Enter your packet loss threshold as a percentage (regular integer without the percentage sign): ");
		
		try
		{
			if((s = stdIn.readLine()) != null)
			{
				percentage = (double)Integer.parseToInt(s);
				percentage /= 100;
			}
		}catch(Exception e)
		{
			System.err.println("You dun goofed.");
		}
		
		float chance = r.nextFloat();
		if (chance > percentage)
		{
			/*Do something to ignore the packet.*/
		}
	}
}