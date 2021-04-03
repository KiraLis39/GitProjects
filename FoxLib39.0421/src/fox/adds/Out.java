package fox.adds;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Stack;


public class Out implements Closeable {
	public static enum levels {FULL, INFO, WARNS, ERRORS, CRITICAL}
	static levels errLevel = levels.FULL;
	private static final int SIMPLE = 0, ACCENT = 1, WARN = 2, ERROR = 3, CRITICAL = 4;	
	
	private final Charset charset = StandardCharsets.UTF_8;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"), fnc = new SimpleDateFormat("HH.mm.ss");
	static Thread LogThread;
	static Stack<String> messageStack = new Stack<String>();
	static Stack<Integer> typeStack = new Stack<Integer>();
	private static File HTMLdir = new File("./log/"), HTMLlog;
	
	private int type, logCount = 0;
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
					Print("INFO: Out", 1, Thread.currentThread().getName() + " is started with write-level " + errLevel.name() + " (#" + errLevel.ordinal() + ")");
					if (!checkFiles()) {throw new RuntimeException("ERR: Out: Files creating is fail!");}
					
					while (enabled || !Thread.currentThread().isInterrupted()) {
						if (messageStack.isEmpty()) {
							try {Thread.sleep(sleepTime);} catch (InterruptedException ie) {Thread.currentThread().interrupt();
							} catch (Exception e) {e.printStackTrace();}
						} else {
							if (free) {
								free = false;
								
								if (messageStack.size() > 30) {LogThread.setPriority(Thread.MAX_PRIORITY);
								} else if (messageStack.size() > 15) {LogThread.setPriority(Thread.NORM_PRIORITY);
								} else {LogThread.setPriority(Thread.MIN_PRIORITY);}
								
								if (Thread.currentThread().getPriority() == 10) {System.err.println("Out trhead in Hight priority by stacks size: " + messageStack.size());}
								if (typeStack.size() != messageStack.size()) {System.err.println("WARN: Out: working: messageArray has size: " + messageStack.size() + ", but typeArray`s size: " + typeStack.size());}
								
								logHTML();
							}

							try {Thread.sleep(sleepTime);} catch (InterruptedException ie) {Thread.currentThread().interrupt();
							} catch (Exception e) {e.printStackTrace();}
						}
					}
					
					Print("INFO: Out", 1, Thread.currentThread().getName() + " was stopped.");
//					close();
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
		Print(Out.class, 1, "Out log process will be stoped by close() methode init.");
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
			type = typeStack.pop();
			address = messageStack.pop();

			osw.write(
					"<p style='font-family:arial,fixedsys,consolas;font-size:10px;font color=#000'>" + currentTime + "<br>" + 
					"<font style='font-family:consolas,arial,garamond;font-size:12px;'>");
			
			switch (type) {
				case 0: osw.write("<font color='#000'>" + logCount + ") " + address);
					break;						
				case 1: osw.write("<font color='#0094de'>" + logCount + ") " + address);
					break;						
				case 2: osw.write("<font color='#e0a800'>" + logCount + ") " + address);
					break;						
				case 3: osw.write("<font color='#bf4c28'><h3>" + logCount + ") " + address + "</h>");
					break;						
				default:	osw.write("<font color='#ff0000'><h2>" + logCount + ") " + address + "</h>");
			}
			
			osw.write("</p></font>\n");
		} catch (Exception de) {System.err.println(de.getMessage());
		} finally {
			logCount++;
			free = true;
		}
	}
	
	public synchronized static void Print(String message) {Print(Out.class, 0, message, Thread.currentThread());}	
	public synchronized static void Print(Class<?> clazz, int level, Exception e) {Print(clazz, level, e.getStackTrace());}	
	public synchronized static void Print(Class<?> clazz, int level, Object[] messages) {Print(clazz.getName(), level, messages);}	
	public synchronized static void Print(Class<?> clazz, int level, String message) {Print(clazz, level, message, Thread.currentThread());}
	public synchronized static void Print(Class<?> clazz, int level, String message, Thread srcThread) {Print(clazz.getName() + " -> " + srcThread.getStackTrace()[1].getMethodName(), level, message);}
	public synchronized static void Print(String address, int level, Object[] messages) {
		if (isEnabled()) {for (int i = 0; i < messages.length; i++) {Print(address, level, messages[i].toString());}}
	}
	@SuppressWarnings("resource")
	synchronized static void Print(String address, int level, String message) {
		if (isEnabled()) {
			if (message.endsWith("\n")) {thanNextLine = true;}
			if (message.startsWith("\n")) {System.out.println(); message = message.substring(2, message.length());}
			
			switch (level) {
				case ACCENT:		System.out.println("ATTENTION:\t" + address + ": " + message);
					break;
				case WARN: 		System.err.println("WARN:\t" + address + ": " + message);
					break;
				case ERROR:		System.err.println("ERROR:\t" + address + ": " + message);
					break;
				case CRITICAL: 	throw new RuntimeException("!!! CRITICAL ERROR !!!\n" + address + ": " + message);
				case SIMPLE:	
				default:				System.out.println(">>>\t" + address + ": " + message);
			}
			
			if (thanNextLine) {System.out.println(); thanNextLine = false;}
			
			if (LogThread == null) {new Out();}
			if (level >= errLevel.ordinal()) {
				typeStack.insertElementAt(level, 0);
				messageStack.insertElementAt(address + ": " + message, 0);
			}
		}
	}

	
	public static String getLogPath() {return HTMLdir.getPath();}
	public static File getLogFile() {return HTMLlog;}

	public static int getLogsCoutAllow() {return logsCountAllow;}
	public static void setLogsCoutAllow(int _logsCountAllow) {logsCountAllow = _logsCountAllow;}
	
	public synchronized static void setErrorLevel(levels lvl) {errLevel = lvl;}
	public static levels getErrorLevel() {return errLevel;}

	public static boolean isEnabled() {return enabled;}
	@SuppressWarnings("resource")
	public static void setEnabled(Boolean d) {
		if (enabled.equals(d)) {return;}
		enabled = d;
		
		if (LogThread == null || !LogThread.isAlive()) {new Out();}
	}
}