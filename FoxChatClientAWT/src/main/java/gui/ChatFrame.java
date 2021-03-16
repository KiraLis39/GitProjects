package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import addings.ConstrainedViewPortLayout;
import addings.VerticalFlowLayout;
import door.Message.MessageDTO;
import door.Message.MessageDTO.GlobalMessageType;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResManager;
import fox.games.FoxCursor;
import gui.ChatStyler.backgroundFillType;
import gui.ChatStyler.uiStyleType;
import media.Media;
import net.NetConnector;
import net.NetConnector.localMessageType;
import registry.IOMs;
import registry.Registry;
import subGUI.BaloonPane;
import subGUI.BaloonPane.Baloon;
import subGUI.LoginFrame;


@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener, ComponentListener, MouseListener, MouseMotionListener {
//	private static SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
//	private static SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
	
	private static BufferedImage[] sendButtonSprite;

	private static JButton sendButton;
	private static ChatFrame frame;
	private static JTextArea inputArea;
	private static JScrollPane inputScroll;
	private static JScrollPane msgsScroll;
	private static JPanel chatPanel, rightPane, leftPane, downPane, midPane, correctPane;
	static JPanel basePane;
	
	private static DefaultListModel<String> usersListModel;	
	private static JList<String> usersList;

	private Point frameWas, mouseWasOnScreen;
	
	private static boolean needUpdate = true, isFullscreen, dialogPaneOpacity;

	private static Color mesColOutput = new Color(0.0f, 0.75f, 0.75f, 0.6f);
	private static Color mesColInput = new Color(0.25f, 0.75f, 0.0f, 0.6f);
	private static Color mesColWarn = new Color(1.0f, 0.0f, 0.0f, 0.6f);
	private static Color mesColSystem = new Color(1.0f, 0.35f, 0.0f, 0.6f);
	private static Color cSidePanelsBkg = new Color(0.0f, 0.0f, 0.0f, 0.6f);
	
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Registry.render(g2D);
		super.paintComponents(g2D);
		
//		g2D.drawImage(ResManager.getBImage("head"), 0, 0, getWidth(), 30, this);
//		g2D.setColor(Color.ORANGE);
//		g2D.drawString(Registry.name + " v." + Registry.verse, 10, 18);		
	}
	
	public ChatFrame() {
		frame = this;
		init();
		
		setTitle(Registry.name + " v." + Registry.verse);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(520, 600));

		basePane = new JPanel(new BorderLayout()) {
			@Override
			public void paintComponent(Graphics g) {
				Registry.render((Graphics2D) g);
				if (ResManager.getBImage("bkgDefault") == null) {return;}
				
				int bkgW = ResManager.getBImage("bkgDefault").getWidth(), bkgH = ResManager.getBImage("bkgDefault").getHeight();
				Color itemBackColor = new Color(
						ResManager.getBImage("bkgDefault").getColorModel().getRGB(
								ResManager.getBImage("bkgDefault").getRaster().getDataElements(
										bkgW - 3, bkgH / 2, null)));
				g.setColor(itemBackColor);
				g.fillRect(0, 0, getWidth(), getHeight());				
				
				if (ChatStyler.getFillType() == backgroundFillType.ASIS) {g.drawImage(ResManager.getBImage("bkgDefault"), 0, 0, bkgW, bkgH, null);
				} else if (ChatStyler.getFillType() == backgroundFillType.STRETCH) {g.drawImage(ResManager.getBImage("bkgDefault"), 0, 0, getWidth(), getHeight(), null);
				} else if (ChatStyler.getFillType() == backgroundFillType.PROPORTIONAL) {
					int w1 = getWidth() / 2 - bkgW / 2;
					int h1 = (getHeight() - 120) / 2 - bkgH / 2;					
					g.drawImage(ResManager.getBImage("bkgDefault"), w1, h1, bkgW, bkgH, null);
				} else {
					int tmpx = getWidth() / bkgW;
					int tmpy = getHeight() / bkgH;
					for (int i = 0; i < tmpy + 1; i++) {
						for (int j = 0; j < tmpx + 1; j++) {
							g.drawImage(ResManager.getBImage("bkgDefault"), bkgW * j, bkgH * i, bkgW, bkgH, null);
						}
					}
				}
			}
			
			{
				setCursor(FoxCursor.createCursor(ResManager.getBImage("cur_1"), "ansC"));
				setBorder(new EmptyBorder(1, 0, 0, 0));
				
				midPane = new JPanel(new BorderLayout()) {
					{
						setOpaque(false);
						setBorder(new EmptyBorder(0, 3, 0, 3));

						chatPanel = new JPanel(new VerticalFlowLayout(2, 0, 0)) {
							@Override
							public void paintComponent(Graphics g) {
								if (dialogPaneOpacity) {
									Registry.render((Graphics2D) g);
									g.setColor(new Color(0.4f, 0.4f, 0.5f, 0.5f));
									g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
								}
							}
							
							{
								setOpaque(false);
								setName("chatPane");
								addComponentListener(ChatFrame.this);
							}
						};
						
						msgsScroll = new JScrollPane(chatPanel) {
							{
								setViewportBorder(null);
								
								setBorder(null);
								setOpaque(false);
								getViewport().setOpaque(false);
								setAutoscrolls(true);
								
								getViewport().setLayout(new ConstrainedViewPortLayout());
								getVerticalScrollBar().setUnitIncrement(14);
								getVerticalScrollBar().setAutoscrolls(true);
								setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
							}
						};

						add(msgsScroll);
					}
				};
				
				leftPane = new JPanel() {
					@Override
					public void paintComponent(Graphics g) {
						Registry.render((Graphics2D) g);
						g.setColor(cSidePanelsBkg);
						g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
						
						g.drawImage(ResManager.getBImage("pod_0"), 3, 3, 35, 35, this);
						g.drawImage(ResManager.getBImage("pod_1"), 3, 48, 35, 35, this);
					}
					
					{
						setOpaque(false);
						setPreferredSize(new Dimension(42, 0));
						setBorder(new EmptyBorder(0, 1, 0, 0));
						setVisible(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_LEFT_PANEL));
					}
				};
				
				rightPane = new JPanel(new BorderLayout()) {
					@Override
					public void paintComponent(Graphics g) {
						Registry.render((Graphics2D) g);
						g.setColor(cSidePanelsBkg);
						g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

						g.drawImage(ResManager.getBImage("userListEdge"), 0, 0, 32, getHeight(), this);
					}
					
					{
						setOpaque(false);
						setBorder(new EmptyBorder(3, 24, 0, 3));
						setVisible(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_USERS_PANEL));
						
						usersListModel = new DefaultListModel<String>();
						usersList = new JList<String>(usersListModel) {
							@Override
		                    public int locationToIndex(Point location) {
		                        int index = super.locationToIndex(location);
		                        if (index != -1 && !getCellBounds(index, index).contains(location)) {return -1;
		                        } else {return index;}		                        
		                    }
							
							{
								setOpaque(false);
								
								setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
								setSelectionBackground(new Color(0.7f, 0.8f, 0.85f, 0.1f));
								setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
								
								setForeground(Color.WHITE);
								setFont(Registry.fUsers);
//								setVisibleRowCount(5);
								
								addMouseListener(new MouseAdapter() {
									@Override
									public void mouseReleased(MouseEvent e) {
										if (locationToIndex(e.getPoint()) != -1) {
											rightPane.repaint();
											inputArea.requestFocus();
											clearSelection();
										}
									}
									
									@Override
									public void mousePressed(MouseEvent e) {
										rightPane.repaint();
										if (getSelectedValue() == null) {return;}
										
										Out.Print(ChatFrame.class, 0, "Был выбран вариант " + getSelectedValue());
										
										if (inputArea.getText().startsWith("/to ")) {
											inputArea.setText(inputArea.getText().replace(inputArea.getText().split(": ")[0] + ": ", ""));
										}											
										inputArea.setText("/to " + getSelectedValue() + ": " + inputArea.getText());
									}
								});
							
								setFocusable(false);
								setCursor(FoxCursor.createCursor(ResManager.getBImage("cur_0"), "ansC"));
							}
						};
						
						add(usersList);
					}
				};
				
				downPane = new JPanel(new BorderLayout(3, 3)) {
					@Override
					public void paintComponent(Graphics g) {
						Registry.render((Graphics2D) g);
						super.paintComponent(g);
						
						g.drawImage(ResManager.getBImage("downBarImage"), 0, 0, getWidth(), getHeight(), this);
						g.drawImage(ResManager.getBImage("grass"), 0, getHeight() - 32, getWidth(), 32, this);
						
						g.setFont(Registry.fLabels);
						g.setColor(Color.BLACK);
						g.drawString(Registry.company, (int) (getWidth() / 2 - Registry.ffb.getStringCenterX(g, Registry.company)) - 1, getHeight() - 6);
						g.setColor(Color.WHITE);
						g.drawString(Registry.company, (int) (getWidth() / 2 - Registry.ffb.getStringCenterX(g, Registry.company)), getHeight() - 7);
					}
					
					{
						setOpaque(false);
						setBorder(BorderFactory.createCompoundBorder(
								new EmptyBorder(0, 0, 26, 0),
								BorderFactory.createCompoundBorder(
										BorderFactory.createTitledBorder(
												BorderFactory.createLineBorder(ChatStyler.getCurrentStyle() == uiStyleType.DARK ? Color.GRAY.darker() : Color.GRAY.brighter(), 1, true), 
												"- Foxy Chat -", 3, 2, Registry.fMessage, Color.GRAY.darker()),
										new EmptyBorder(-6, 0, 0, 0)
										)
								)
						);
//						setCursor(FoxCursor.createCursor(ResourceManager.getBufferedImage("cur_1"), "ansC"));
						
						correctPane = new JPanel(new FlowLayout(0, 3, 3)) {
							{
								setOpaque(false);
								setBorder(new EmptyBorder(-3, -3, -3, 0));
								setPreferredSize(new Dimension(0, 22));
								
								JButton music = new JButton("music") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("music");
										addActionListener(ChatFrame.this);
										setFocusPainted(false);
									}
								};
								JButton photo = new JButton("photo") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("photo");
										addActionListener(ChatFrame.this);
										setFocusPainted(false);
									}
								};
								JButton document = new JButton("document") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("document");
										addActionListener(ChatFrame.this);
										setFocusPainted(false);
									}
								};
								
								add(music);
								add(photo);
								add(document);
							}
						};
						
						inputArea = new JTextArea() {
							{
								setWrapStyleWord(true);
								setLineWrap(true);
								setToolTipText("<HTML>Enter - next line<br>Ctrl+Enter - send");
								setFont(Registry.fMessage);
								setBorder(new EmptyBorder(0, 3, 0, 3));
								setRequestFocusEnabled(true);
//								setBackground(cSidePanelsBkg);
							}
						};
						
						inputScroll = new JScrollPane(inputArea) {
							{
								setViewportBorder(null);
								
								setOpaque(false);
								getViewport().setOpaque(false);

								setAutoscrolls(true);
								getVerticalScrollBar().setAutoscrolls(true);

								getVerticalScrollBar().setUnitIncrement(9);
								setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
								setPreferredSize(new Dimension(0, 45));
							}
						};
						
						sendButton = new JButton("Отправить") {
							BufferedImage btnImage = sendButtonSprite[0];
							
							@Override
							public void paintComponent(Graphics g) {
								Registry.render((Graphics2D) g);
								g.setFont(Registry.fLabels);
								g.setColor(Color.WHITE);

								g.drawImage(btnImage, 0, 0, getWidth(), getHeight(), this);
								g.drawString(getText(), 
										(int) (getWidth() / 2 - Registry.ffb.getStringCenterX(g, getText())), 
										(int) (getHeight() / 2 + Registry.ffb.getStringHeight(g, getText()) / 3) - (btnImage == sendButtonSprite[2] ? 1:0));
							}
							
							{
								setOpaque(false);
								setActionCommand("send");
								setBorderPainted(false);
								addActionListener(ChatFrame.this);
								addMouseListener(new MouseAdapter() {
							         public void mouseEntered(MouseEvent me) {
							        	 btnImage = sendButtonSprite[1];
							        	 repaint();
							         }
							         public void mouseExited(MouseEvent me) {
							        	 btnImage = sendButtonSprite[0];
							        	 repaint();
							         }
							         public void mousePressed(MouseEvent e) {
							        	 btnImage = sendButtonSprite[2];
							        	 repaint();
							         }
							         public void mouseReleased(MouseEvent e) {
							        	 btnImage = sendButtonSprite[1];
							        	 repaint();
							         }
							      });
							}
						};
						
						add(correctPane, BorderLayout.NORTH);
						add(inputScroll, BorderLayout.CENTER);
						add(sendButton, BorderLayout.EAST);
					}
				};
				
//				add(upPane, BorderLayout.NORTH);
				add(midPane, BorderLayout.CENTER);
				add(leftPane, BorderLayout.WEST);
				add(rightPane, BorderLayout.EAST);
				add(downPane, BorderLayout.SOUTH);
			}
		};
		
		add(basePane);
		
		addComponentListener(this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {exitRequest();}
		});
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setupInAc();

		pack();
//		setSize(new Dimension(getWidth(), java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
		setLocationRelativeTo(null);
		setVisible(true);
		
		// поток обновления UI:
		new Thread(new Runnable() {
			@Override
			public void run() {
				Media.playSound("launched");
				
				while (true) {
					if (needUpdate) {
						needUpdate = false;
						rightPane.setPreferredSize(new Dimension(ChatFrame.this.getWidth() / 5, 0));
						repaint();
						
						try {Thread.sleep(50);} catch (InterruptedException e) {/* IGNORE SLEEP */}
						
						revalidateChatBaloonsPanel();
						repaint();
						
						inputArea.requestFocusInWindow();
						usersList.clearSelection();
					}
					try {Thread.sleep(100);} catch (InterruptedException e) {/* IGNORE SLEEP */}
				}
			}
		}) {{start();}};
	
		if (IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN).equalsIgnoreCase("none")) {new LoginFrame();
		} else {
			Registry.myNickName = IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN);
			NetConnector.reConnect(
					IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN), 
					IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_PASSWORD).toCharArray());
		}
		
		setSidePanelsBkg(cSidePanelsBkg);
	}

	private void setupInAc() {
		Registry.inAc.add("chat", this);
		Registry.inAc.set("chat", "enterText", KeyEvent.VK_ENTER, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputArea.requestFocusInWindow();
				inputArea.append("\n");
			}
		});
		Registry.inAc.set("chat", "enterText", KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addMessage(inputArea.getText(), localMessageType.OUTPUT);
				inputArea.requestFocusInWindow();
			}
		});
		Registry.inAc.set("chat", "escape", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(usersList.getSelectedValue());
				inputArea.requestFocusInWindow();
				usersList.clearSelection();
				if (inputArea.getText().startsWith("/to ")) {
					inputArea.setText(inputArea.getText().replace(inputArea.getText().split(": ")[0] + ": ", ""));
				} else {exitRequest();}
			}
		});
	}

	private void init() {
		ChatStyler.setUIStyle(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE) == -1 ? 2 : IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE));
		ChatStyler.setBackgroundFillStyle(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE) == -1 ? 0 : IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE));
	
		dialogPaneOpacity = IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_DIALOGPANE_OPACITY);
	}

	private void exitRequest() {
		Object[] options = { "Да", "Нет!" };
		int n = JOptionPane.showOptionDialog(this, "Закрыть окно?", "Подтверждение", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 	null, options, options[0]);		
		if (n == 0) {disconnectAndExit();}
	}
	
	public synchronized static void addMessage(String message, localMessageType type) {
		addMessage(
				new MessageDTO(
						GlobalMessageType.SYSINFO_MESSAGE, "System", 
						message, type == localMessageType.INFO || type == localMessageType.WARN ? Registry.myNickName : null, System.currentTimeMillis()), type);
	}
	
	public synchronized static void addMessage(MessageDTO messageDTO, localMessageType type) {
		if (messageDTO.getBody() == null || messageDTO.getBody().isBlank()) {return;}
		
		Color balloonColor = Color.WHITE;
		boolean successfulSended = false;
		
		if (messageDTO.getBody().startsWith("/to ")) {
			messageDTO.setTo(messageDTO.getBody().split(": ")[0].replace("/to ", ""));
			messageDTO.setBody(messageDTO.getBody().substring(messageDTO.getBody().indexOf(": ") + 2, messageDTO.getBody().length()));
		}
		
		if (messageDTO.getTo() == null) {
			if (type == localMessageType.INPUT) {
				messageDTO.setTo(Registry.myNickName);
				messageDTO.setMessageType(GlobalMessageType.PRIVATE_MESSAGE);
			} else {
				messageDTO.setTo("Всем");
				messageDTO.setMessageType(GlobalMessageType.PUBLIC_MESSAGE);
			}
		} else {messageDTO.setMessageType(GlobalMessageType.PRIVATE_MESSAGE);}
		
		
		if (type == localMessageType.OUTPUT) {
			balloonColor = mesColOutput;
			messageDTO.setFrom(Registry.myNickName);
			messageDTO.setTimestamp(System.currentTimeMillis());
			
			successfulSended = NetConnector.writeMessage(messageDTO);
			if (successfulSended) {
				Media.playSound("messageSend");
				inputArea.setText(null);
			} else {
				messageDTO.setBody("(Не отправлено) " + messageDTO.getBody());
				type = localMessageType.INFO;
				balloonColor = mesColSystem;
				Media.playSound("systemError");
			}
		}
		
		switch (type) {
			case WARN:		
				balloonColor = mesColWarn;
				Media.playSound("systemError");
				break;
				
			case INFO: 	
				balloonColor = mesColSystem;
				Media.playSound("systemError");
				break;
				
			case INPUT:
				balloonColor = mesColInput;				
				if (!messageDTO.getFrom().equals("SERVER") && !usersListModel.contains(messageDTO.getFrom())) {addUserToList(messageDTO.getFrom());}
				Media.playSound("messageReceive");
				break;
				
			default:	System.err.println("ChatFrame:addMessage(): Unknown type income: " + type);
		}

		addChatBaloon(type, messageDTO, balloonColor);
	}
	
	private static void addChatBaloon(localMessageType inputOrOutput, MessageDTO mesDTO, Color balloonColor) {
		if (mesDTO.getBody() == null || mesDTO.getBody().isBlank()) {return;}
		
		BaloonPane newBaloon = new BaloonPane(inputOrOutput, mesDTO, balloonColor);
		chatPanel.add(newBaloon);
		chatPanel.add(Box.createVerticalStrut(3));
		
		revalidateBaloon(newBaloon);
		
		scrollDown();
		needUpdate = true;		
	}


	private synchronized static void revalidateChatBaloonsPanel() {
		for (Component bc : chatPanel.getComponents()) {
			if (bc instanceof BaloonPane) {revalidateBaloon((BaloonPane) bc);}
		}
	}
	
	private synchronized static void revalidateBaloon(BaloonPane baloonPane) {
		Baloon baloon = baloonPane.getBaloon();
		Double baloonHeight = calculateBaloonSize(baloon, msgsScroll.getSize().getWidth() - 19D - 20D); // Registry.ffb.getStringHeight(baloon.getGraphics(), baloon.getAreaText()) + 6D
		
		baloonPane.setPreferredSize(new Dimension((int) (msgsScroll.getSize().getWidth() - 19D), baloonHeight.intValue()));
		baloonPane.revalidate();
	}
	
	private synchronized static Double calculateBaloonSize(Baloon baloon, Double maxWindowWidth) {
		baloon.setPreferredSize(new Dimension(maxWindowWidth.intValue(), baloon.getPreferredSize().height));
		
		String maxLine = "";
		String message = baloon.getAreaText();
		for (String line : message.split("\n")) {
			if (line.length() > maxLine.length()) {maxLine = line;}
		}
		
		Double headerWidthWithSpaces = Registry.ffb.getStringWidth(baloon.getGraphics(), baloon.getHeaderText()) + 28D;
		Double maxLineWidth = Registry.ffb.getStringWidth(baloon.getArea().getGraphics(), maxLine) + 38D;
		
		if (maxWindowWidth > headerWidthWithSpaces) {
			
			if (headerWidthWithSpaces > maxLineWidth) {
				baloon.setPreferredSize(new Dimension(headerWidthWithSpaces.intValue(), baloon.getPreferredSize().height));
			} else {
				
				if (maxLineWidth < maxWindowWidth) {
					baloon.setPreferredSize(new Dimension(maxLineWidth.intValue(), baloon.getPreferredSize().height));
				}
			}
			
		} else {/* НИЧЕГО МЕНЯТЬ НЕ НУЖНО */}
		
		baloon.revalidate();
		
		if (isFullscreen) {return baloon.getPreferredSize().getHeight();	
		} else {return baloon.getPreferredSize().getHeight() * 1.8D;}

//		String label = baloon.getHeaderText();
//		Double labelWidth = Registry.ffb.getStringWidth(baloon.getGraphics(), label) * 0.75D;
//		Double maxLineWidth = Registry.ffb.getStringWidth(baloon.getGraphics(), maxLine) + 36D;
//		
//		if (maxLineWidth < maxWidth) {
//			if (maxLineWidth > labelWidth) {baloon.setPreferredSize(new Dimension(maxLineWidth.intValue(), baloon.getPreferredSize().height));
//			} else {baloon.setPreferredSize(new Dimension(labelWidth.intValue(), baloon.getPreferredSize().height));}
//		}

//		Double messageWidth = Registry.ffb.getStringWidth(baloon.getGraphics(), message);
//		Double lineHeight = Registry.ffb.getStringHeight(baloon.getGraphics(), maxLine) + 1.8D;
		
//		int nlCount = message.split("\n").length - 1;
//		height += (nlCount * lineHeight);
//		height += (int) (messageWidth / maxWidth * lineHeight);
//		baloon.setPreferredSize(new Dimension(baloon.getPreferredSize().width, height.intValue()));
		
/*
Я не уверен, что есть метод лучше, чем тот, который вы упомянули. Проблема в том, что в общем случае вычитание прямоугольной 
области из другой оставит дыру где-то посередине, поэтому результат на самом деле не прямоугольник. В вашем случае вы знаете, что 
панель задач умещается точно на одной из сторон прямоугольника экрана, поэтому «лучший» способ действительно выяснить, 
с какой стороны она находится, и вычесть ширину / высоту с этой стороны.
*/
	}
	
	
	private static void scrollDown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {Thread.sleep(250);} catch (Exception e) {/* SLEEP IGNORE */}
				int current = msgsScroll.getVerticalScrollBar().getValue();
				int max = msgsScroll.getVerticalScrollBar().getMaximum();
				if (current < max) {msgsScroll.getVerticalScrollBar().setValue(max);}
			}
		}).start();
		msgsScroll.revalidate();
	}
	
	public static void addUserToList(String newUserName) {
		newUserName = newUserName.replace("[", "").replace("]", "");
		
		if (!usersListModel.contains(newUserName)) {
			usersListModel.addElement(newUserName);
			rightPane.repaint();
		}		
	}
	
	public static void disconnectAndExit() {
		NetConnector.disconnect();
		IOM.saveAll();
		System.exit(0);
	}
	
	
	// UTILITES:
	public static void saveChatToFile() {
		zaglushko();
	}
	
	public static void zaglushko() {
		JOptionPane.showConfirmDialog(frame, "Еще не реализовано...", "Прастити", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void switchLeftPaneVisible() {
		leftPane.setVisible(!leftPane.isVisible());
		needUpdate = true;
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_LEFT_PANEL, leftPane.isVisible());
	}
	
	public static void switchRightPaneVisible() {
		rightPane.setVisible(!rightPane.isVisible());
		needUpdate = true;
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_USERS_PANEL, rightPane.isVisible());
	}

	
	private void choseMusicToSend() {
		
	}
	
	private void chosePhotoToSend() {
		
	}
	
	private void choseDocumentToSend() {
		
	}
	
	
	// GETS & SETS:
	public static void setSendButtonSprite(BufferedImage[] spritelist) {sendButtonSprite = spritelist;}
	public static void setBackgroundImage(BufferedImage bkgImage, String bkgPath) {
		try {
			ResManager.remove("bkgDefault");
			ResManager.add("bkgDefault", new File(bkgPath));
		} catch (Exception e) {e.printStackTrace();}
		
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_PATH, bkgPath);
		frame.repaint();
	}
	public static void setSidePanelsBkg(Color color) {
		cSidePanelsBkg = color;
		try {usersList.setBackground(color);			
		} catch (Exception e) {cSidePanelsBkg = color;}
	}
	public static void setupMenuBar(JMenuBar mBar) {frame.setJMenuBar(mBar);}
	
	
	// LISTENERS:
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "send": addMessage(inputArea.getText(), localMessageType.OUTPUT);
				break;
				
			case "music": choseMusicToSend();
				break;
			case "photo": chosePhotoToSend();
				break;
			case "document": choseDocumentToSend();
				break;
			case "uCorrect": inputArea.replaceSelection("<u>" + inputArea.getSelectedText() + "</u>");
				break;
			default: 
		}
	}

	public void keyReleased(KeyEvent e) {}	
	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void componentResized(ComponentEvent e) {
		if (frame.getSize().getWidth() >= Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {isFullscreen = true;
		} else {isFullscreen = false;}
		
		revalidateChatBaloonsPanel();
		needUpdate = true;
	}

	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		ChatFrame.this.setLocation(
				(int) (frameWas.getX() - (mouseWasOnScreen.getX() - e.getXOnScreen())), 
				(int) (frameWas.getY() - (mouseWasOnScreen.getY() - e.getYOnScreen())));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseWasOnScreen = new Point(e.getXOnScreen(), e.getYOnScreen());
		frameWas = getLocation();
	}
	
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public static void setDialogOpacity(boolean dpOpacity) {
		dialogPaneOpacity = dpOpacity;
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_DIALOGPANE_OPACITY, dpOpacity);
		chatPanel.repaint();
	}
}