package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import fox.builders.FoxFontBuilder;
import fox.builders.FoxFontBuilder.FONT;


@SuppressWarnings("serial")
public class MonitorFrame extends JFrame {	
	private static FoxFontBuilder ffb = new FoxFontBuilder();	
	
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
		}
	};
	
	private static JLabel connectsLabel, statusLabel, lastRecMes;
	private static JTextArea console;
	private static JTextField inputField;
	
	private Font consoleFont = ffb.setFoxFont(FONT.CONSOLAS, 14, false);	
	
	
	public MonitorFrame() {
		setTitle("FChat server monitor:");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setMinimumSize(new Dimension(300, 130));
		
		JPanel upPane = new JPanel(new BorderLayout()) {
			{

				JPanel infoPane = new JPanel(new GridLayout(3, 4)) {
					{
						setBorder(new EmptyBorder(3, 3, 3, 3));
						setBackground(Color.DARK_GRAY);
						
						add(new JLabel("On-Line:") {{setForeground(Color.WHITE);}});
						statusLabel = new JLabel("false") {{setHorizontalAlignment(JLabel.LEFT); setForeground(Color.WHITE);}};
						add(statusLabel);
						add(new JLabel(/* RESERVED */));
						add(new JLabel(/* RESERVED */));
						
						add(new JLabel("Connections:") {{setForeground(Color.WHITE);}});
						connectsLabel = new JLabel("" + Server.getConnectionsCount()) {{setHorizontalAlignment(JLabel.LEFT); setForeground(Color.WHITE);}};
						add(connectsLabel);
						add(new JLabel(/* RESERVED */));
						add(new JLabel(/* RESERVED */));
						
						lastRecMes = new JLabel(Server.getLastRecievedMessage()) {{setHorizontalAlignment(JLabel.LEFT); setForeground(Color.WHITE);}};
						add(new JLabel("Last recieved:") {{setForeground(Color.WHITE);}});
						add(lastRecMes);						
						add(new JLabel(/* RESERVED */));
						add(new JLabel(/* RESERVED */));
					}
				};
				
				JPanel downButtonsPane = new JPanel(new BorderLayout()) {
					{
						setBackground(Color.DARK_GRAY);
						setBorder(new EmptyBorder(3, 0, 0, 0));
						
						JButton resetLineBtn = new JButton("Отключить всех!") {
							{
								setFocusPainted(false);
								setBackground(new Color(0.0f, 0.25f, 0.75f, 1.0f));
								setForeground(Color.WHITE);
								addActionListener(new ActionListener() {
									@Override	
									public void actionPerformed(ActionEvent e) {resetRequest();}
								});
							}
						};
						
						JButton switchStateBtn = new JButton("О/I") {
							{
								setFocusPainted(false);
								setBackground(new Color(0.5f, 0.0f, 0.0f, 1.0f));
								setForeground(Color.WHITE);
								addActionListener(new ActionListener() {
									@Override	
									public void actionPerformed(ActionEvent e) {
										if (Server.getConnectionAlive()) {stopRequest();
										} else {new Server();}
									}
								});
							}
						};
						
						JButton connViewBtn = new JButton("VIEW") {
							{
								setFocusPainted(false);
								setBackground(new Color(0.25f, 0.5f, 0.1f, 1.0f));
								setForeground(Color.WHITE);
								
								addActionListener(new ActionListener() {
									@Override	
									public void actionPerformed(ActionEvent e) {printClientsList();}
								});
							}
						};
						
						add(connViewBtn, BorderLayout.WEST);
						add(resetLineBtn, BorderLayout.CENTER);
						add(switchStateBtn, BorderLayout.EAST);
					}
				};
					
				add(infoPane, BorderLayout.CENTER);
				add(downButtonsPane, BorderLayout.NORTH);
			}
		};
		
		console = new JTextArea() {
			{
				setBorder(new EmptyBorder(3,3,3,3));
				setPreferredSize(new Dimension(700, 300));
				setBackground(Color.BLACK);
				setForeground(Color.GREEN);
				setFont(consoleFont);
				setLineWrap(true);
				setWrapStyleWord(true);
				setCaretColor(Color.YELLOW);
				getCaret().setBlinkRate(250);
				setEditable(false);
			}
		};
		
		JScrollPane conScroll = new JScrollPane(console) {
			{
				setBorder(null);
			}
		};
		
		JPanel inputPane = new JPanel(new BorderLayout()) {
			{
				setBorder(new EmptyBorder(1,1,1,1));
				setBackground(Color.DARK_GRAY);
				
				inputField = new JTextField() {
					{
						setBorder(new EmptyBorder(1,3,0,3));
						setBackground(Color.BLACK);
						setForeground(Color.GREEN);
						setFont(consoleFont);
						setPreferredSize(new Dimension(300, 30));
						
						addKeyListener(new KeyAdapter() {
							@Override
							public void keyPressed(KeyEvent e) {
								if (e.getKeyCode() == KeyEvent.VK_ENTER) {
									String cmd = getText();
									if (cmd.startsWith("/")) {
										cmd = cmdEngine(cmd);
										if (cmd != null) {toConsole(cmd);}
										setText(null);
									}
								}
							}
							
							private String cmdEngine(String cmd) {
								if (cmd.equalsIgnoreCase("/help") || cmd.equalsIgnoreCase("/?")) {
									printCommandsList();
									return null;
								} else if (cmd.equalsIgnoreCase("/stop")) {
									if (Server.getConnectionAlive()) {stopRequest();
									} else {return "Сервер уже остановлен!";}
									return null;
								} else if (cmd.equalsIgnoreCase("/exit")) {
									exitRequest();
									return null;
								} else if (cmd.equalsIgnoreCase("/view") || cmd.equalsIgnoreCase("/show")) {
									printClientsList();
									return null;
								} else if (cmd.equalsIgnoreCase("/reset")) {
									resetRequest();
									return null;
								} else if (cmd.startsWith("/bc ")) {
									// broadcasting entered message...
								} else if (cmd.startsWith("/say ")) {
									// send to <client> entered message...
								} else if (cmd.equalsIgnoreCase("/start")) {
									if (!Server.getConnectionAlive()) {new Server();
									} else {return "Сервер уже запущен!";}
									return null;
								} else {return "Команда " + cmd + " не зарегистрирована.";}
								
								return cmd;
							}

							private void printCommandsList() {
								toConsole("\n*** *** *** ***");
								toConsole("COMMANDS LISTING:");
								for (Entry<String, String> comItem : comsMap.entrySet()) {
									toConsole(comItem.getKey() + "\t (" + comItem.getValue() + ");");
								}
								toConsole("*** *** *** ***\n");
							}
						});
					}
				};
				
				add(inputField);
			}
		};
		
		add(upPane, BorderLayout.NORTH);
		add(conScroll, BorderLayout.CENTER);
		add(inputPane, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {exitRequest();}
		});
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		new Thread(new Runnable() {
			@Override	public void run() {
				while (true) {
					updateOnlineStatus();
					updateConnectionsCount();
					updateLastMessageText();
					try {	Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}) {{setDaemon(true);}}.start();
	}

	
	private void exitRequest() {
		Object[] options = { "Да", "Нет!" };
		int n = JOptionPane.showOptionDialog(this, 
						"<HTML>Завершить работу сервера,<br>разорвав все активные соединения?<br>(активных: " + Server.getConnectionsCount() + ")", 
						"Внимание!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);				
		if (n == 0) {
			Server.disconnect();
			System.exit(0);
		}
	}
	
	private void stopRequest() {
		Object[] options = {"Да", "Нет!"};
		int n = JOptionPane.showOptionDialog(MonitorFrame.this, 
						"<HTML>Остановить работу сервера?<br>(активных: " + Server.getConnectionsCount() + ")", 
						"Внимание!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);	
		
		if (n == 0) {Server.serverStop();}
	}
	
	private void resetRequest() {
		Object[] options = {"Да", "Нет!"};
		int n = JOptionPane.showOptionDialog(MonitorFrame.this, 
						"<HTML>Разорвать все активные соединения?<br>(активных: " + Server.getConnectionsCount() + ")", 
						"Внимание!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);	
		
		if (n == 0) {Server.resetConnections();}
	}
	
	
	private void printClientsList() {
		toConsole("Clients on-line:");
		for (Entry<String, ClientHandler> client : Server.getConnections()) {
			toConsole(client.getKey() + ": " + client.getValue().toString());
		}
		toConsole("*** *** ***");
	}
	
	public static void toConsole(String string) {
//		console.append("> " + string + "\n");
		Document doc = console.getDocument();
		if (doc != null) {
		    try {doc.insertString(doc.getLength(), "> " + string + "\n", null);
		    } catch (BadLocationException e) {}
		}
	}

	
	public static void updateOnlineStatus() {
		statusLabel.setText("" + Server.getConnectionAlive());
		statusLabel.setForeground(Server.getConnectionAlive() ? Color.GREEN : Color.RED);
	}
	public static void updateConnectionsCount() {connectsLabel.setText("" + Server.getConnectionsCount());}
	public static void updateLastMessageText() {lastRecMes.setText("" + Server.getLastRecievedMessage());}
}