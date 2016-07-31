import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask{
	int i;
	public MyTimerTask(int i){
		this.i = i;
	}
	public void run() {
		// TODO Auto-generated method stub
		synchronized(Sender.timers){
			absCancel(Sender.timers[i]);
			Sender.timers[i].purge();
			Sender.timers[i] = new Timer();
			synchronized(Sender.hashArray){
				/*byte [] resend = Sender.hashArray[i].toSend;
				System.out.println("SEQ for RESEND IN MYTIMERTASK: " + PacketHelp.getSequenceNumber(resend));
				DatagramPacket dp = new DatagramPacket(resend, resend.length, Sender.destination, Sender.port);*/
				try {
					byte [] resend = Sender.hashArray[i].toSend;
					System.out.println("SEQ for RESEND IN MYTIMERTASK: " + PacketHelp.getSequenceNumber(resend));
					DatagramPacket dp = new DatagramPacket(resend, resend.length, Sender.destination, Sender.port);
					Sender.socket.send( dp);
				} catch (Exception e) {
					//e.printStackTrace();
					return;
				}
			}
			
			Sender.timers[i].schedule(new MyTimerTask(i), 3000);
		}
	}
	public boolean absCancel(Timer t){
		try{
			t.cancel();
			return true;
		}
		catch(IllegalStateException e){
			return false;
		}
	}
		
}
