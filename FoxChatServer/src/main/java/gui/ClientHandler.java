package gui;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class ClientHandler implements Runnable {
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private Thread cHandThread;
	
	private String clientName, welcome;
	
	private boolean isCorrect;
	
	
	public ClientHandler(Socket _socket) {
		this.socket = _socket;
		
		cHandThread = new Thread(this);
		cHandThread.setDaemon(true);
		cHandThread.start();
	}
	
	public boolean isConnected() {return socket != null && cHandThread != null && !socket.isClosed() && !cHandThread.isInterrupted();}

	public void kick() {
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cHandThread.interrupt();
			Server.reviewConnections();
		}
	}
	
	@Override
	public String toString() {return "Не написал ты тут ниче еще.. Расслабься.";}

	public DataInput getInputStream() {return dis;}

	public void setCorrect(boolean b) {this.isCorrect = b;}

	public String getUserName() {return this.clientName;}

	public String getWelcome() {
		int awaits = 6;
		while (this.welcome == null && awaits > 0) {
			awaits--;
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		}
		if (this.welcome != null && !this.welcome.isBlank()) {
			clientName = this.welcome.split(" ")[1];
			cHandThread.setName("Handler-Thread of " + clientName);
		}
		
		return this.welcome;
	}

	
	@Override
	public void run() {
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			welcome = dis.readUTF();
			
			MonitorFrame.toConsole("Starts ClientHandler`s cycle...");
			while (true) {
				String msg = dis.readUTF();
				MonitorFrame.toConsole("ClientHandler recieve the message: " + msg);
				if (isCorrect) {Server.echo(dos, msg);
				} else {
					dos.writeUTF("Access denied!");
					dos.flush();
				}
			}
		} catch (SocketException e) {
			MonitorFrame.toConsole("\nПохоже, соединение было внезапно сброшено: " + e.getMessage() + ". Client " + clientName + " will be kicked than.");
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
}