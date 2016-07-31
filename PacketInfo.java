import java.net.DatagramPacket;
import java.util.ArrayList;
/*
 * This is to store information on a SINGLE packet.
 */
public class PacketInfo 
{
	/*
	 * This is to store multiple copies of the same
	 * packet to send out to the appropriate receivers.
	 */
	public ArrayList<DatagramPacket> pendingReceivers;
	
	public byte [] toSend;
	
	 public int seq;
	
	 public char c;
	
	public PacketInfo next;
	
	public void addReceiver(DatagramPacket dp)
	{
		pendingReceivers.add(dp);
	}
	
	public void removeReceiver(DatagramPacket dp)
	{
		for (int i = 0; i < pendingReceivers.size(); i++)
		{
			if (pendingReceivers.get(i).equals(dp))
			{
				pendingReceivers.remove(i);
			}
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		PacketInfo p = new PacketInfo();

		if(o == null)
		{
			return false;
		}

		else if(o instanceof PacketInfo)
		{
			p = (PacketInfo) p;
		}

		if(this.seq == p.seq && this.c == p.c) return true;

		else return false;
	}
	
	public PacketInfo()
	{
		
	}
	
	public PacketInfo(byte [] input)
	{
		pendingReceivers = new ArrayList<DatagramPacket>();
		seq = PacketHelp.getSequenceNumber(input);
		c = PacketHelp.getColor(input);
		toSend = input;
	}
}
