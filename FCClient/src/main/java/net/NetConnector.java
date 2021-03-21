package net;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import door.Message.MessageDTO;
import door.Message.MessageDTO.GlobalMessageType;
import fox.adds.IOM;
import fox.adds.Out;
import gui.ChatFrame;
import media.Media;
import registry.IOMs;
import subGUI.MenuBar;


public class NetConnector extends Thread {
	public enum localMessageType {OUTPUT, INPUT, INFO, WARN}
	public enum connState {DISCONNECTED, CONNECTING, CONNECTED}
	private static connState state = connState.DISCONNECTED;
	
	private static Thread self;
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	private static String tmp_login;
	private static char[] tmp_pass;
	private static boolean isClientAFK;
	
	
	public NetConnector(String login, char[] pass) {
		super(new Runnable() {
			@Override
			public void run() {
				setCurrentState(connState.CONNECTING);
				
				try {
					Out.Print(NetConnector.class, 1, "Trying to create socket with data: " + MenuBar.getIP() + ": " + MenuBar.getPort());
					socket = new Socket(MenuBar.getIP(), MenuBar.getPort());
					System.out.println("Client socket up");
					
					Out.Print(NetConnector.class, 1, "Trying to create data IO-streams by clients socket...");
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());

					setCurrentState(connState.CONNECTED);
					
					// waiting for new income message:
					String income;
					while (true) {
						income = dis.readUTF();
						onMessageRecieved(MessageDTO.convertFromJson(income));
					}
				} catch (ConnectException e) {
					System.out.println("\nНет соединения с сервером на клиенте (" + e.getMessage() + ").");
//					e.printStackTrace();
					disconnect();
				} catch (SocketException e) {
					System.out.println("\nCокет сообщил о проблеме на клиенте (" + e.getMessage() + ").");
//					e.printStackTrace();
				} catch (EOFException e) {
					System.out.println("\nПотеря соединения с сервером на клиенте (" + e.getMessage() + ").");
//					ChatFrame.sendMessage("Потеряно соединения с сервером!", messageType.WARN);
					disconnect();
//					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
//					setCurrentState(connState.DISCONNECTED);
				} catch (IOException e) {
					e.printStackTrace();
//					setCurrentState(connState.DISCONNECTED);
				}
			}
		});
	
		tmp_login = login;
		tmp_pass = pass;
		self = this;
	}
	
	public static void reConnect(String login, char[] pass) {
		Out.Print(ChatFrame.class, 1, "NetConnector.reConnect(): Try to connect with login '" + login + "' and pass '" + new String(pass) + "'...");
		new NetConnector(login, pass).start();
	}
	
	
	public static boolean writeMessage(MessageDTO message) {
		if (dos == null || socket == null || socket.isClosed()) {			
			try {
				System.out.println("Reconnecting...");
				reConnect(tmp_login, tmp_pass);
			} catch (Exception e) {
				System.out.println("Не удалось отправить сообщение по причине: " + e.getMessage());
				return false;
			}
		}
		
		if (dos == null || socket == null || socket.isClosed()) {return false;}
		
		try {
			dos.writeUTF(message.convertToJson());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void onMessageRecieved(MessageDTO incomeDTO) {
		System.out.println("Client: NetConnector().onMessageRecieved(): message = " + incomeDTO.toString());

		if (incomeDTO.getMessageType() == GlobalMessageType.AUTH_REQUEST) {
			try {dos.writeUTF(new MessageDTO(GlobalMessageType.AUTH_REQUEST, tmp_login, new String(tmp_pass), "SERVER", System.currentTimeMillis()).convertToJson());
			} catch (IOException e) {e.printStackTrace();}	
		} else if (incomeDTO.getMessageType() == GlobalMessageType.CONFIRM_MESSAGE) {ChatFrame.addMessage(incomeDTO, localMessageType.INPUT);
		} else if (incomeDTO.getMessageType() == GlobalMessageType.REJECT_MESSAGE) {
			ChatFrame.addMessage(incomeDTO, localMessageType.INPUT);
			setCurrentState(connState.DISCONNECTED);
		}
		
		if (incomeDTO.getMessageType() == GlobalMessageType.USERLIST_MESSAGE) {
//			System.out.println("Users list: " + Arrays.toString(userList));
			for (String userName : incomeDTO.getBody().split(",")) {
				if (userName.equals(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN))) {continue;}
				ChatFrame.addUserToList(userName);
			}
			return;
		}
		
//		System.out.println("NetConnector: onMessageRecieved(): Client recieve: " + incomeDTO.toString());
		ChatFrame.addMessage(incomeDTO, localMessageType.INPUT);	
	}

	
	public static connState getCurrentState() {return state;}
	private static void setCurrentState(connState _state) {
		if (state == _state) {return;}
		Out.Print(NetConnector.class, 1, "Change connections state to " + _state);
		state = _state;
		
		if (state == connState.CONNECTED) {
			Media.playSound("connect");
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.25f, 0.5f, 0.5f) : Color.BLACK, Color.GREEN, "On-Line");
			ChatFrame.addMessage("Соединение с сервером успешно установлено!", localMessageType.INFO);
			
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_IP, MenuBar.getIP());
			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_PORT, MenuBar.getPort());
//			IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN, Registry.myNickName);
		} else if (state == connState.CONNECTING) {
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.75f, 0.25f, 0.0f) : Color.DARK_GRAY, Color.BLACK, "Connect..");
		} else {
			Media.playSound("disconnect");
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? null : new Color(0.45f, 0.2f, 0.2f), Color.RED, "Off-Line"); // new Color(0.75f, 0.5f, 0.5f)
			ChatFrame.addMessage("Соединение с сервером отсутствует!", localMessageType.WARN);
		}
	}

	public static void disconnect() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			}
			if (dis != null) {dis.close();}
			if (dos != null) {dos.close();}
		} catch (IOException e) {e.printStackTrace();}
		
		if (Thread.currentThread().isAlive()) {Thread.currentThread().interrupt();}
		setCurrentState(connState.DISCONNECTED);
	}

	public static connState getNetState() {return state;}

	public static Thread getThread() {return self;}
	
	public static boolean isAfk() {return isClientAFK;}
	public static void setAfk(boolean afk) {
		if (isClientAFK != afk && getCurrentState() == connState.CONNECTED) {
			isClientAFK = afk;
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.25f, 0.5f, 0.5f) : Color.BLACK, Color.GREEN, "On-Line");
			ChatFrame.addMessage("*** AFK " + (afk ? "ON" : "OFF") + " ***", localMessageType.INFO);
			writeMessage(new MessageDTO(GlobalMessageType.SYSINFO_MESSAGE, IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN), 
					"AFK=" + NetConnector.isAfk(), System.currentTimeMillis()));
		}
	}
}