package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import interfaces.iServerConnector;
import java.util.Set;
import javax.swing.SwingUtilities;
import door.Message.MessageDTO;
import door.Message.MessageDTO.GlobalMessageType;
import gui.MonitorFrame;


@SuppressWarnings("serial")
public class Server implements iServerConnector, Runnable {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("<dd.MM.yyyy HH:mm:ss>");
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static Map<String, ClientHandler> clientsMap = new LinkedHashMap<String, ClientHandler>();
	private static LinkedHashMap<String, String> comsMap = new LinkedHashMap<String, String> () {
		{
			put("/?", "Выводит список всех доступных в консоли команд (см. 'help')");
			put("/help", "Выводит список всех доступных в консоли команд (см. '?')");	
			
			put("/view", "Выводит список всех активных подлючений (клиентов) (см. 'show')");
			put("/show", "Выводит список всех активных подлючений (клиентов) (см. 'view')");
			
			put("/reset", "Отключает всех клиентов и очищает список их подключений");
			put("/exit", "Полностью останавливает и закрывает приложение сервера");
			put("/stop", "Отключает всех клиентов и останавливает выполнение сервера (не закрывая его)");
			put("/start", "Запускает сервер (если он был остановлен)");
			
			put("/bc <MESSAGE>", "Отправка глобального сообщения");	
			put("/say <MESSAGE>", "Отправка сообщения пользователю (клиенту)");	
			
			put("/info", "Выводит информацию о сервере и подключении");	
		}
	};

	private static Thread serverConnectionThread;
	private static ServerSocket sSocket;	
	private static Server server;
	
	private static final int PORT = 13900;
	private static final int MAX_CLIENTS = 32;
	
	
	public Server() {server = this;}

	public static Server getAccess() {return server;}

	
	public synchronized void resetConnections() {
		MonitorFrame.toConsole("Kick all clients...");
		
		for (Entry<String, ClientHandler> entry : clientsMap.entrySet()) {
			if (entry.getValue().isConnected()) {
				entry.getValue().kick();
			}
			
			clientsMap.remove(entry.getKey());
		}
	}
	
	public synchronized void reviewConnections() {
		SwingUtilities.invokeLater(new Runnable() {		
			@Override
			public void run() {
				MonitorFrame.toConsole("Looking for outdated clients...");
			}
		});
		
		for (Entry<String, ClientHandler> entry : clientsMap.entrySet()) {
			if (!entry.getValue().isConnected()) {
				entry.getValue().kick();
				clientsMap.remove(entry.getKey());
			}			
		}
	}
	
	
	public synchronized void broadcast(GlobalMessageType type, final ClientHandler srcClient, final String brdcstmsg, boolean isSelfExclude) {
		/*
		 * isSelfExclude = true (Сообщения транслируются всем, кроме самого отправителя).
		 * isSelfExclude = false (Сообщение оправляется лишь самому отправителю).
		 */
		if (clientsMap.size() > 0) {
			for (ClientHandler handler : clientsMap.values()) {
				if (handler.equals(srcClient) && isSelfExclude) {continue;
				} else {handler.say(new MessageDTO(type, (srcClient == null ? "SERVER" :  srcClient.getUserName()), brdcstmsg, System.currentTimeMillis()));}
			}
		}
	}
		
	
	@Override
	public void run() {
//		MonitorFrame.toConsole("Server thread starts...");		
		try {
			sSocket = new ServerSocket(PORT);
			MonitorFrame.toConsole("Server up on " + sSocket.getInetAddress() + "/" + sSocket.getLocalPort());
	
			while (!sSocket.isClosed()) {
				MonitorFrame.toConsole("\nServer awaits for a new connection...");
				onClientConnection(new ClientHandler(sSocket.accept()));
			}
		} catch (UnknownHostException e) {onServerException(e);
		} catch (SocketException e) {onServerException(e);
		} catch (IOException e) {onServerException(e);
		} finally {MonitorFrame.toConsole("Server is shut down.");}
	}
	
	@Override
	public synchronized void start() {
		serverConnectionThread = new Thread(this) {
			{
				setDaemon(true);
				setName("SERVER");				
			}
		};
		serverConnectionThread.setDaemon(true);
		serverConnectionThread.start();
	}

	@Override
	public synchronized void stop() {
		resetConnections();
		MonitorFrame.toConsole("Closing server socket...");
		if (sSocket != null) {
			try {sSocket.close();
			} catch (IOException e) {e.printStackTrace();}
		}
		serverConnectionThread.interrupt();
		MonitorFrame.toConsole("Server is shutdown.");
	}

	@Override
	public synchronized void onClientConnection(ClientHandler ch) {
		MonitorFrame.toConsole("Клиент пытается выполнить подключение...");		
	}

	@Override
	public synchronized void onClientDisconnect(String clientName) {
		// add runlater
		MonitorFrame.toConsole("Server: onClientDisconnect(): " + clientName);
	}

	@Override
	public synchronized void onServerException(Exception e) {
		if (!e.getMessage().equals("Socket closed")) {
			MonitorFrame.toConsole("Server has Exception: '" + e.getMessage() + "' (" + e.getCause() + ").");
			e.printStackTrace();
		}
	}

	
	public static synchronized String getIP() {
		try {return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {return null;}
	}
	
	public static synchronized String getHostName() {
		try {return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {return null;}
	}
	
	public static synchronized int getPort() {return sSocket.getLocalPort();}
	
	public static synchronized boolean isNetAccessible() {
		try(Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80), 3000);
            return true;
        } catch(UnknownHostException unknownHost) {return false;
        } catch (IOException e) {return false;}
	}
	
	public static synchronized int getMaxClientsAllowed() {return MAX_CLIENTS;}

	public static synchronized String getFormatTime(long millis) {return dateFormat.format(millis);}

	public int getConnectionsCount() {return clientsMap.size();}	
	public static boolean isConnectionAlive() {return serverConnectionThread != null && serverConnectionThread.isAlive() && !serverConnectionThread.isInterrupted();}

	public Set<Entry<String, ClientHandler>> getConnections() {return clientsMap.entrySet();}
	public static Set<Entry<String, String>> getCommandsMapSet() {return comsMap.entrySet();}
	
	public synchronized void addClient(String clientName, ClientHandler handler) {
		clientsMap.put(clientName, handler);
		handler.say(new MessageDTO(GlobalMessageType.USERLIST_MESSAGE, "SERVER", Arrays.toString(clientsMap.keySet().toArray()), System.currentTimeMillis()));
	}
	public synchronized ClientHandler getClient(String clientName) {return clientsMap.get(clientName);}
	public static synchronized boolean containsClient(String clientName) {return clientsMap.containsKey(clientName);}
}