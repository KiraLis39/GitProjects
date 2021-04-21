package door;

import fox.adds.IOM;

public class ExitClass {
	
	public static void exit(int err) {
		IOM.saveAll();
		System.exit(err);
	}
}
