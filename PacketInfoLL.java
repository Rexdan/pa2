public class PacketInfoLL 
{
	PacketInfo head;
	
	public PacketInfoLL() 
	{
		this.head = null;
	}
	
	public void addPacketInfo(PacketInfo input)
	{
		if(head == null)
		{
			this.head = input;
		}
		else
		{ 
			PacketInfo temp = head.next;
			head.next = input;
			input.next = temp;
		}
	}
	
	public boolean removePacketInfo(int input)
	{
		PacketInfo tempy = head; 
		PacketInfo prev = null;
		if (tempy == null)
		{
			return false;
		}
		while (tempy != null)
		{
			if (tempy.seq == input)
			{
				if (tempy == head)
				{
					head = head.next;
					return true;
				}
				prev.next = tempy.next;
				return true;
			}
			else
			{
				prev = tempy;
				tempy = tempy.next;
			}
		}
		return false;
	}
	
	public PacketInfo traverse(int input)
	{
		for (PacketInfo tempy = head; tempy != null; tempy = tempy.next)
		{
			if (tempy.seq == input)
			{
				return tempy;
			}
		}
		return null;
	}
}

