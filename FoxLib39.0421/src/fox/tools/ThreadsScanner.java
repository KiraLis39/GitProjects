package tools;

import adds.Out;
import adds.Out.LEVEL;

public class ThreadsScanner {	
	private ThreadGroup currentGroup;
	private Thread[] lstThreads;
	
	public ThreadsScanner() {
		currentGroup = Thread.currentThread().getThreadGroup();
		lstThreads = new Thread[currentGroup.activeCount()];

		currentGroup.enumerate(lstThreads);

		for (int i = 0; i < currentGroup.activeCount(); i++) {
			Out.Print(ThreadsScanner.class, LEVEL.ACCENT, "Thread " + i + ": " + lstThreads[i].getName(), Thread.currentThread());
		}
	}
}