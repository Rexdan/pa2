public class PacketInfoLL 
{
	PacketInfo head;
	public int size = 0;
	
	public PacketInfoLL() 
	{
		this.head = null;
	}
	
	public void addPacketInfo(PacketInfo input)
	{
		if(head == null)
		{
			head = input;
		}
		else
		{ 
			PacketInfo temp = input;
			temp.next = head;
			head = temp;
		}
		size++;
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
					size--;
					return true;
				}
				prev.next = tempy.next;
				size--;
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
	
	public PacketInfo search(int input)
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

