package gui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Server implements Runnable {
	private static Map<String, ClientHandler> clientsMap = new LinkedHashMap<String, ClientHandler>();
	private static Thread serverConnectionThread;
	private static ServerSocket sSocket;
	
	private static String lastRecieved;
	
	
	public Server() {
		serverConnectionThread = new Thread(this) {
			{
				setDaemon(true);
				setName("SERVER");				
			}
		};
		serverConnectionThread.start();
	}
	
	public static void echo(DataOutputStream dos, String forwardBack) {
		try {
			Thread.sleep(100);
			dos.writeUTF("/from SERVER: " + forwardBack);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {Thread.currentThread().interrupt();}
	}
	
	public static void serverStop() {
		disconnect();
		serverConnectionThread.interrupt();
		MonitorFrame.toConsole("Server is shutdown.");
	}
	
	public static void disconnect() {
		resetConnections();
		MonitorFrame.toConsole("Closing server socket...");
		if (sSocket != null) {
			try {sSocket.close();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static void resetConnections() {
		MonitorFrame.toConsole("Kick all clients...");
		
		for (Entry<String, ClientHandler> entry : clientsMap.entrySet()) {
			if (entry.getValue().isConnected()) {entry.getValue().kick();}			
			clientsMap.remove(entry.getKey());
		}
	}
	
	public static void reviewConnections() {
		MonitorFrame.toConsole("See for out clients...");
		
		for (Entry<String, ClientHandler> entry : clientsMap.entrySet()) {
			if (entry.getValue().isConnected()) {entry.getValue().kick();}			
			clientsMap.remove(entry.getKey());
		}
	}
	
	
	public static Set<Entry<String, ClientHandler>> getConnections() {
		return clientsMap.entrySet();
	}
	
	public static int getConnectionsCount() {return clientsMap.size();}
	
	public static boolean getConnectionAlive() {return serverConnectionThread != null && serverConnectionThread.isAlive() && !serverConnectionThread.isInterrupted();}

	public static String getLastRecievedMessage() {return lastRecieved;}

	
	@Override
	public void run() {
		MonitorFrame.toConsole("Server thread starts...");
		
		try {
//			sSocket = new ServerSocket(13900, 3, InetAddress.getByName("localhost"));
			sSocket = new ServerSocket(13900);
			MonitorFrame.toConsole("Server up on " + sSocket.getInetAddress() + "/" + sSocket.getLocalPort());
	
			while (!sSocket.isClosed()) {
				MonitorFrame.toConsole("Server awaits for a new connection...");
				ClientHandler ch = new ClientHandler(sSocket.accept());
				
				MonitorFrame.toConsole("Приглашение получено: " + ch.getWelcome());
				if (ch.getWelcome() != null && ch.getWelcome().startsWith("/ADD ")) {
					MonitorFrame.toConsole("Клиенту " + ch.getUserName() + " разрешено продолжение работы.");				
					clientsMap.put(ch.getWelcome().split(" ")[1], ch);
					ch.setCorrect(true);
				} else {
					MonitorFrame.toConsole("Отказ! Приглашение не верно!");
					ch.setCorrect(false);
				}
			}
		} catch (UnknownHostException e) {
			MonitorFrame.toConsole("Server has strange UnknownHostException: '" + e.getMessage() + "'.");
			e.printStackTrace();
		} catch (SocketException e) {
			if (!e.getMessage().equals("Socket closed")) {
				MonitorFrame.toConsole("Server has strange SocketException: '" + e.getMessage() + "'.");
				e.printStackTrace();
			}
		} catch (IOException e) {
			MonitorFrame.toConsole("Server has IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {MonitorFrame.toConsole("Server is shut down.");}
	}
}