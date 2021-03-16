package server;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import door.Message.MessageDTO;
import door.Message.MessageDTO.GlobalMessageType;
import gui.MonitorFrame;


public class ClientHandler implements Runnable {
	private MessageDTO welcomeRequest = new MessageDTO(GlobalMessageType.AUTH_REQUEST, "SERVER", "", System.currentTimeMillis());
	private MessageDTO welcomeRequestDenied = new MessageDTO(GlobalMessageType.REJECT_MESSAGE, "SERVER", "Пользователь с таким именем уже в системе!", System.currentTimeMillis());
	
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private Thread cHandThread;
	private String clientName;
	
	private boolean isAccessGranted;
	
	
	public ClientHandler(Socket _socket) {
		this.socket = _socket;
		
		cHandThread = new Thread(this);
		cHandThread.setDaemon(true);
		cHandThread.start();
	}
	
	public boolean isConnected() {return socket != null && cHandThread != null && !socket.isClosed() && !cHandThread.isInterrupted();}

	public void say(MessageDTO mesDTO) {
		MonitorFrame.toConsole("Say to '" + clientName + "': " + (mesDTO.getMessageType() == GlobalMessageType.USERLIST_MESSAGE ? "<UList>" : mesDTO.getBody()));
		try {
			dos.writeUTF(mesDTO.convertToJson());
			dos.flush();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void kick() {
		if (socket == null || socket.isClosed()) {return;}
		
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Server.getAccess().onClientDisconnect(clientName);
			cHandThread.interrupt();
			Server.getAccess().reviewConnections();
		}		
	}
	
	@Override
	public String toString() {return socket.toString();}

	public DataInput getInputStream() {return dis;}

	public void setAccessGranted(boolean b) {this.isAccessGranted = b;}

	public String getUserName() {return this.clientName;}

	
	@Override
	public void run() {
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			
			MonitorFrame.toConsole("ClientHandler created. Sending a welcomeRequest...");
			say(welcomeRequest);
			
			while (true) {onRecieveMessage(MessageDTO.convertFromJson(dis.readUTF()));}
		} catch (SocketException e) {
			MonitorFrame.toConsole("\nПохоже, соединение было внезапно сброшено: " + e.getMessage() + "." + clientName + " will be kicked than.");
			kick();
//			e.printStackTrace();
		} catch (EOFException e) {
			MonitorFrame.toConsole("\nEOFException на сервере. Причина: " + e.getMessage() + ".\nClient " + clientName + " was disconnected and will be kicked than.");
//			e.printStackTrace();
			kick();
		} catch (IOException e) {
			MonitorFrame.toConsole("\nIOException на сервере. Причина: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// recieve the message fron client:
	private void onRecieveMessage(MessageDTO incomeDTO) {
		if (clientName == null) {clientName = incomeDTO.getFrom();}
		
		if (isAccessGranted) {
			MonitorFrame.toConsole(
					Server.getAccess().getFormatTime(incomeDTO.getTimestamp()) + " (" +  getUserName() + " -> " + incomeDTO.getTo() + ") " + incomeDTO.getBody());
//			> <13.03.2021> 17:49:24 (KiraLis39 -> Всем) 123
			if (incomeDTO.getMessageType() == GlobalMessageType.PUBLIC_MESSAGE) {
				Server.getAccess().broadcast(GlobalMessageType.PUBLIC_MESSAGE, this, incomeDTO.getBody(), true);				
			} else if (incomeDTO.getMessageType() == GlobalMessageType.PRIVATE_MESSAGE) {
				if (Server.getAccess().containsClient(incomeDTO.getTo())) {Server.getAccess().getClient(incomeDTO.getTo()).say(incomeDTO);				
				} else {say(new MessageDTO(GlobalMessageType.PRIVATE_MESSAGE, "SERVER", "Получатель не в сети. Повторите позже...", System.currentTimeMillis()));}				
			} else {System.err.println("ClientHandler: onRecieveMessage(): Unknown message type income: " + incomeDTO.getMessageType() + ".");}						
		} else {
			if (incomeDTO.getMessageType() == GlobalMessageType.AUTH_REQUEST) {
				MonitorFrame.toConsole("Приглашение получено: " + incomeDTO);
				
				if (!Server.getAccess().containsClient(incomeDTO.getFrom())) {
					// проверяем пароль клиента...
					
					MonitorFrame.toConsole("Клиенту " + incomeDTO.getFrom() + " разрешено продолжение работы.");				
					Server.getAccess().addClient(clientName, this);
					
					setAccessGranted(true);
				} else {
					MonitorFrame.toConsole("Отказ! Такой пользователь уже зарегистрирован на сервере!");
					say(welcomeRequestDenied);
					setAccessGranted(false);
				}
			} else {say(welcomeRequest);}
		}
	}
}
