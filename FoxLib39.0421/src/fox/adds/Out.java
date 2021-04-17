package adds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;


public class Out {
	public static enum LEVEL {DEBUG, INFO, ACCENT, WARN, ERROR, CRITICAL}
	static LEVEL errLevel = LEVEL.DEBUG;
	
	private final Charset charset = StandardCharsets.UTF_8;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"), fnc = new SimpleDateFormat("HH.mm.ss");
	static Thread LogThread;
	static Stack<String> messageStack = new Stack<String>();
	static Deque<LEVEL> typeDeque = new ArrayDeque<LEVEL>();
	private LEVEL type;
	private static File HTMLdir = new File("./log/"), HTMLlog;
	private int logCount = 0;
	int sleepTime = 333;
	Boolean free = true;
	private String currentTime, currentDate, address;
	
	private static int logsCountAllow = 10;	
	static Boolean enabled = true;
	private static Boolean thanNextLine = false;

	
	public Out() {
		if (LogThread == null) {
			LogThread = new Thread(new Runnable() {
				@Override
				public void run() {
					Print("INFO: Out", LEVEL.INFO, Thread.currentThread().getName() + " is started with write-level " + errLevel.name() + " (#" + errLevel.ordinal() + ")");
					if (!checkFiles()) {throw new RuntimeException("ERR: Out: Files creating is fail!");}
					
					while (enabled || !Thread.currentThread().isInterrupted()) {
						if (messageStack.isEmpty()) {
							try {Thread.sleep(sleepTime);
							} catch (InterruptedException ie) {Thread.currentThread().interrupt();}
						} else {
							if (free) {
								free = false;
								
								if (messageStack.size() > 30) {LogThread.setPriority(Thread.MAX_PRIORITY);
								} else if (messageStack.size() > 15) {LogThread.setPriority(Thread.NORM_PRIORITY);
								} else {LogThread.setPriority(Thread.MIN_PRIORITY);}
								
//								if (Thread.currentThread().getPriority() == 10) {System.err.println("Out trhead in Hight priority by stacks size: " + messageStack.size());}
								if (typeDeque.size() != messageStack.size()) {System.err.println("WARN: Out messageArray has size: " + messageStack.size() + ", but typeDeque`s size: " + typeDeque.size());}
								
								logHTML();
							}

							try {Thread.sleep(sleepTime);} catch (InterruptedException ie) {Thread.currentThread().interrupt();
							} catch (Exception e) {e.printStackTrace();}
						}
					}
					
					Print("INFO: Out", LEVEL.ACCENT, Thread.currentThread().getName() + " was stopped.");
					close();
				}
			})

			{
				{
					setName("FoxLib39: OutLogThread");
					setPriority(Thread.MIN_PRIORITY);
					setDaemon(true);
					start();
				}
			};
		}
	}
	
	boolean checkFiles() {
		int tryes = 0;
		do {
			tryes++;
			HTMLdir.mkdirs();
		} while (!HTMLdir.exists() && tryes < 10);
		if (!HTMLdir.exists()) {throw new RuntimeException("The HTMLdir '" + HTMLdir + "' can`t created!");}
		
		// удаляем лишние файлы, если их больше, чем разрешено хранить:
		File[] logsCount = HTMLdir.listFiles();
		if (logsCount.length >= logsCountAllow) {
			for (int i = 0; i < logsCount.length - logsCountAllow; i++) {logsCount[i].delete();}
		}
		logsCount = null;

		String timeData = fnc.format(System.currentTimeMillis());		
		currentDate = sdf.format(System.currentTimeMillis());
		HTMLlog = new File(HTMLdir.getPath() + "/" + timeData + " log.html");
		
		tryes = 0;
		while (!HTMLlog.exists() && tryes < 10) {
			tryes++;
			
			try {
				HTMLlog.createNewFile();
				logFileOpen();
			} catch (IOException e) {
				System.err.println("ERR: Out: checkFiles: Creating \"" + HTMLlog + "\" is FAILED!");
				e.printStackTrace();
				return false;
			}
		}
		if (!HTMLlog.exists()) {throw new RuntimeException("The HTMLlog '" + HTMLlog + "' can`t created!");}
		
		return true;
	}
		
	private void logFileOpen() {
		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(HTMLlog, true), charset)) {
			osw.write("<!DOCTYPE html><HTML lang=\"ru\"><HEAD><meta charset=\"UTF-8\"><title>" + currentDate + "</title></HEAD><BODY>");
		} catch (Exception e) {e.printStackTrace();}
	}
		
	public void close() {
		Print(Out.class, LEVEL.ACCENT, "Out log process will be stoped by close() methode init.");
		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(HTMLlog, true), charset)) {osw.write("</BODY></HTML>");
		} catch (IOException e) {e.printStackTrace();
		} finally {
			enabled = false;
			LogThread.interrupt();
		}
	}
	
	synchronized void logHTML() {
		try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(HTMLlog, true), charset)) {
			currentTime = fnc.format(System.currentTimeMillis());
			type = typeDeque.pollLast();
			address = messageStack.pop();

			osw.write(
					"<p style='font-family:arial,fixedsys,consolas;font-size:10px;font color=#000'>" + currentTime + "<br>" + 
					"<font style='font-family:consolas,arial,garamond;font-size:12px;'>");
			
			switch (type) {
				case DEBUG: osw.write("<font color='#666'>" + logCount + ") " + address);
					break;	
				case INFO: osw.write("<font color='#000'>" + logCount + ") " + address);
					break;	
				case ACCENT: osw.write("<font color='#0094de'>" + logCount + ") " + address);
					break;						
				case WARN: osw.write("<font color='#e0a800'>" + logCount + ") " + address);
					break;						
				case ERROR: osw.write("<font color='#bf4c28'><h3>" + logCount + ") " + address + "</h>");
					break;						
				default:	osw.write("<font color='#ff0000'><h2>" + logCount + ") " + address + "</h>"); // CRITICAL
			}
			
			osw.write("</p></font>\n");
		} catch (Exception de) {System.err.println(de.getMessage());
		} finally {
			logCount++;
			free = true;
		}
	}
	
	public synchronized static void Print(String message) {Print(Out.class, LEVEL.INFO, message, Thread.currentThread());}	
	public synchronized static void Print(Class<?> clazz, Exception e) {Print(clazz, LEVEL.ERROR, e.getStackTrace());}
	public synchronized static void Print(Class<?> clazz, LEVEL level, Exception e) {Print(clazz, level, e.getStackTrace());}	
	public synchronized static void Print(Class<?> clazz, LEVEL level, Object[] messages) {Print(clazz.getName(), level, messages);}	
	public synchronized static void Print(Class<?> clazz, LEVEL level, String message) {Print(clazz, level, message, Thread.currentThread());}
	public synchronized static void Print(Class<?> clazz, LEVEL level, String message, Thread srcThread) {Print(clazz.getName() + " -> " + srcThread.getStackTrace()[1].getMethodName(), level, message);}
	public synchronized static void Print(String address, LEVEL level, Object[] messages) {
		for (int i = 0; i < messages.length; i++) {Print(address, level, messages[i].toString());}
	}
	
	synchronized static void Print(String address, LEVEL level, String message) {
		if (isEnabled()) {
			if (message.endsWith("\n")) {thanNextLine = true;}
			if (message.startsWith("\n")) {System.out.println(); message = message.substring(2, message.length());}
			
			switch (level) {
				case CRITICAL: throw new RuntimeException("!!! CRITICAL ERROR !!!\n" + address + ": " + message);
				
				case ERROR:		System.err.println("[ERROR]\t" + address + ": " + message);
					break;
					
				case WARN: 		System.err.println("[WARN]\t" + address + ": " + message);
					break;
					
				case ACCENT:	System.out.println("[ATTENTION]\t" + address + ": " + message);
					break;
				
				case INFO:			System.out.println("[INFO]\t" + address + ": " + message);
					break;
					
				case DEBUG: 
				default:				System.out.println("[DEBUG]\t" + address + ": " + message);
			}
			
			if (thanNextLine) {System.out.println(); thanNextLine = false;}
			
			if (LogThread == null) {new Out();}
			if (level.ordinal() >= errLevel.ordinal()) {
				typeDeque.addFirst(level);
				messageStack.insertElementAt(address + ": " + message, 0);
			}
		}
	}

	
	public static String getLogPath() {return HTMLdir.getPath();}
	public static File getLogFile() {return HTMLlog;}

	public static int getLogsCoutAllow() {return logsCountAllow;}
	public static void setLogsCoutAllow(int _logsCountAllow) {logsCountAllow = _logsCountAllow;}
	
	public synchronized static void setErrorLevel(LEVEL lvl) {errLevel = lvl;}
	public static LEVEL getErrorLevel() {return errLevel;}

	public static boolean isEnabled() {return enabled;}
	@SuppressWarnings("resource")
	public static void setEnabled(Boolean d) {
		if (enabled.equals(d)) {return;}
		enabled = d;
		
		if (LogThread == null || !LogThread.isAlive()) {new Out();}
	}
}