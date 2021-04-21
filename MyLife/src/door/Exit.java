package door;

import fox.adds.IOM;


public class Exit {

	public static void exit() {exit(0);}
	
	public static void exit(int code) {
		IOM.saveAll();
		System.exit(code);
	}
	

	public static void restart() {
		IOM.saveAll();		
		new MainClass();
	}
}
