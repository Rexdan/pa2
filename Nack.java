import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

public class Nack extends Thread{
	public boolean terminate;
	public volatile boolean end = false;
	
	public void run(){
		//while(!end)
		{
			try {
			terminate= false;
			DatagramSocket	socket = new DatagramSocket(3002);
			socket.setReuseAddress( true );
			byte []		payload = new byte[1024 + 19];
			DatagramPacket	receivePacket = new DatagramPacket( payload, payload.length );
			int debug = 0;
			socket.setSoTimeout(15000);
			while(terminate==false )
			{
				try{
					socket.receive( receivePacket );
				}
				catch(SocketTimeoutException e){
					System.out.println("Receiver Unresponsive");
					/*
					 * We must shutdown the timers after catching the exception.
					 * Get continuous timers otherwise.
					 */
					for(int i = 0; i < Sender.timers.length; i++)
					{
						Sender.timers[i].cancel();
					}
					terminate = true;
					break;
				}
				debug++;
				int seq;
				byte[] buffer = receivePacket.getData();
				byte[] b = Arrays.copyOfRange(buffer, 0, PacketHelp.getLength(buffer));
				boolean checkSum = PacketHelp.checkTheSum(b);
				//System.out.println("Received Nack Number: " + PacketHelp.getSequenceNumber(b));
					synchronized(this)
					{
						for(int i=0; i<Sender.hashArray.length; i++)
						{
							/*if(Sender.hashArray[i] == null)
							{
								System.out.printf("Entered loop. Index number %d is null.\n", i);
							}*/
							
							if(Sender.hashArray[i] != null )
							{	
								byte [] a = Sender.hashArray[i].toSend;
								//System.out.println("Return from compareData " + PacketHelp.compareData( a , b ));
								
								if( (PacketHelp.compareData( a , b )  <  0) && checkSum )
								{
									
								//synchronized(Sender.timers)
									{	
										seq = PacketHelp.getSequenceNumber(a)%10;
										//Sender.timers[seq].cancel();
										//Sender.timers[seq].purge();
										//shutdown(seq);
										Sender.tasks[seq].cancel();
										Sender.timers[seq].purge();
										MyTimerTask mtt = new MyTimerTask(seq);
										Sender.tasks[seq] = mtt;
										Sender.timers[seq].schedule(mtt, 500);		
										//Sender.timers[seq] = new Timer();
										//Sender.timers[seq].schedule(new MyTimerTask(seq), 500);
									}
									//System.out.printf("Removing index %d.\n", i);
									Sender.hashArray[i] = null;
								}
							}
						}
					}
				}
				return;
			} catch (Exception e) {
				System.err.println("Something went wrong when timing out from Nack class...");
				e.printStackTrace();
			}
		}
		
	}
	public void shutdown(int input)
	{
		//end = true;
		synchronized(Sender.tasks[input])
		{
			Sender.tasks[input].cancel();
			Sender.timers[input].purge();
			MyTimerTask mtt = new MyTimerTask(input);
			Sender.tasks[input] = mtt;
			Sender.timers[input].schedule(mtt, 500);
		}
	}
}
