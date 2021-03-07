package net;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import gui.ChatFrame;
import gui.ChatFrame.messageType;
import media.Media;
import registry.Registry;
import subGUI.MenuBar;


public class NetConnector extends Thread {
	public enum connState {DISCONNECTED, CONNECTING, CONNECTED}
	private static connState state = connState.DISCONNECTED;
	
	private static Socket socket;
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	
	public NetConnector() {
		super(new Runnable() {
			@Override
			public void run() {
				setCurrentState(connState.CONNECTING);
				
				try {
					socket = new Socket(MenuBar.getIP(), MenuBar.getPort());
					System.out.println("Client socket up");
					
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());
					dos.writeUTF("/ADD " + Registry.myNickName + " " + MenuBar.getIP() + ":" + MenuBar.getPort());
					setCurrentState(connState.CONNECTED);					
					
					String message;
					while (true) {
						message = dis.readUTF();
						System.out.println("Client recieve: " + message);						
						ChatFrame.sendMessage(message, messageType.INPUT); // /from SERVER: 123
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
				} catch (IOException e) {
					e.printStackTrace();
//					setCurrentState(connState.DISCONNECTED);
				}
				
			}
		});
	}
	
	public static void reConnect() {
//		Out.Print(ChatFrame.class, 1, "NetConnector.reConnect(): Try to connect...");
		new NetConnector().start();
	}
	
	
	public static boolean writeMessage(String message) {
		if (dos == null) {return false;}
		
		try {
//			dos.writeUTF("/ADD " + Registry.myNickName + " " + MenuBar.getIP() + ":" + MenuBar.getPort());
			dos.writeUTF(message);
			return true;
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Не удалось отправить сообщение по причине: " + e.getMessage());
			return false;
		}
	}
	
	
	private static void setCurrentState(connState _state) {
		state = _state;
		
		if (state == connState.CONNECTED) {
			Media.playSound("connect");
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.25f, 0.5f, 0.5f) : Color.BLACK, Color.GREEN, "Connected!");
			ChatFrame.sendMessage("Соединение с сервером успешно установлено!", messageType.SYSTEM);
		} else if (state == connState.CONNECTING) {
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.75f, 0.25f, 0.0f) : Color.DARK_GRAY, Color.BLACK, "Connecting");
		} else {
			Media.playSound("disconnect");
			MenuBar.setReconnectButton(MenuBar.textColor == Color.BLACK ? new Color(0.75f, 0.5f, 0.5f) : Color.GRAY, Color.RED, "Disconnected");
			ChatFrame.sendMessage("Соединение с сервером отсутствует!", messageType.SYSTEM);
		}
	}

	public static void incomeNewUser(String newUserName) {
		ChatFrame.addUserToList(newUserName);
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
}

//int tmp2 = 0;
//while(true) {
//	String test = "Connecting";
//	for (int i = 0; i < tmp2; i++) {test += ".";}
//	MenuBar.setConnBtnText(test);
//	tmp2++;
//	if (tmp2 > 3) {tmp2 = 0;}
//	try {Thread.sleep(1000);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
//}
