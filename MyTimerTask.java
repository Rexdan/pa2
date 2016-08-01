import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Runtime;

public class MyTimerTask extends TimerTask{
	int i;
	public MyTimerTask(int i){
		this.i = i;
	}
	public void run() {
		synchronized(this){
			/*Sender.timers[i].cancel();*/
			/*Sender.timers[i].purge();*/
			Sender.timers[i] = new Timer();

			//synchronized(Sender.hashArray)
			{
					if(Sender.hashArray[i] == null) return;
					
					byte [] resend = Sender.hashArray[i].toSend;
					//System.out.println("SEQ for RESEND IN MYTIMERTASK: " + PacketHelp.getSequenceNumber(resend));
					DatagramPacket dp = new DatagramPacket(resend, resend.length, Sender.destination, Sender.port);
					try {
						Sender.socket.send( dp);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			
			Sender.timers[i].schedule(new MyTimerTask(i), 500);
			//Runtime.getRuntime().gc();
			System.gc();

		}
	}	
}
