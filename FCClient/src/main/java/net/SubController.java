package net;

import fox.adds.IOM;
import registry.IOMs;


public class SubController implements Runnable {

	private static long afkTimeLast, afkTimeLimit, sleepTime = 1000;


	@Override
	public void run() {
		afkTimeLimit = IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.AFK_TIME_SEC) * 1000;
		afkTimeLast = System.currentTimeMillis();
		
		// прочие полезные, фоновые методы. Не критические и не обязательные.
		while (true) {
			checkAfkStatus();			
			
			try {Thread.sleep(sleepTime);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
		}
	}
	
	private static void checkAfkStatus() {
		if (System.currentTimeMillis() - afkTimeLast > afkTimeLimit) {
			NetConnector.setAfk(true);
			resetAfkTime();
		}
	}

	public static void resetAfkTime() {afkTimeLast = System.currentTimeMillis();}
}
