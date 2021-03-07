package door;

import gui.MonitorFrame;
import gui.Server;


public class MainClass {
	
	public static void main(String[] args) {
		// create server monitor frame:
		new MonitorFrame();
		
		// launch a thread which works with a connections:
		new Server();
	}
}