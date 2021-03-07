package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import addings.ConstrainedViewPortLayout;
import addings.VerticalFlowLayout;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResManager;
import fox.games.FoxCursor;
import gui.ChatStyler.backgroundFillType;
import media.Media;
import net.NetConnector;
import net.NetConnector.connState;
import registry.IOMs;
import registry.Registry;
import subGUI.BaloonPane;


@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener, ComponentListener, MouseListener, MouseMotionListener {
	public enum messageType {OUTPUT, INPUT, SYSTEM, WARN}

	private static BufferedImage[] sendButtonSprite;
	private static BufferedImage bkgDefault;

	private static JButton sendButton;
	private static ChatFrame frame;
	private static JTextArea inputArea;
	private static JScrollPane inputScroll;
	private static JScrollPane msgsScroll;
	private static JPanel chatPanel, rightPane, leftPane;	
	private JPanel midPane, basePane, correctPane;
	
	private static DefaultListModel<String> usersListModel;	
	private static JList<String> usersList;

	private Point frameWas, mouseWasOnScreen;
	
	private static boolean needUpdate = true;

	private static Color mesColOutput = new Color(0.0f, 0.75f, 0.75f, 0.6f);
	private static Color mesColInput = new Color(0.25f, 0.75f, 0.0f, 0.6f);
	private static Color mesColWarn = new Color(1.0f, 0.0f, 0.0f, 0.6f);
	private static Color mesColSystem = new Color(1.0f, 0.35f, 0.0f, 0.6f);
	private static Color cSidePanelsBkg = new Color(0.0f, 0.0f, 0.0f, 0.6f);
	private static Color uListBackColor;
	
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Registry.render(g2D);
		super.paintComponents(g2D);
		
//		g2D.drawImage(ResManager.getBImage("head"), 0, 0, getWidth(), 30, this);
//		g2D.setColor(Color.ORANGE);
//		g2D.drawString(Registry.name + " v." + Registry.verse, 10, 18);		
	}
	
	public ChatFrame(String nickName) {
		frame = this;
		Registry.myNickName = nickName;
		init();
		
		setTitle(Registry.name + " v." + Registry.verse);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(800, 920));

		basePane = new JPanel(new BorderLayout()) {
			@Override
			public void paintComponent(Graphics g) {
				Registry.render((Graphics2D) g);
				if (ChatStyler.getFillType() == backgroundFillType.ASIS) {g.drawImage(bkgDefault, 0, 0, bkgDefault.getWidth(), bkgDefault.getHeight(), null);
				} else if (ChatStyler.getFillType() == backgroundFillType.STRETCH) {g.drawImage(bkgDefault, 0, 0, getWidth(), getHeight(), null);
				} else if (ChatStyler.getFillType() == backgroundFillType.PROPORTIONAL) {
					int w1 = getWidth() / 2 - bkgDefault.getWidth() / 2;
					int h1 = (getHeight() - 120) / 2 - bkgDefault.getHeight() / 2;					
					g.drawImage(bkgDefault, w1, h1, bkgDefault.getWidth(), bkgDefault.getHeight(), null);
				} else {
					int tmpx = getWidth() / bkgDefault.getWidth();
					int tmpy = getHeight() / bkgDefault.getHeight();
					for (int i = 0; i < tmpy + 1; i++) {
						for (int j = 0; j < tmpx + 1; j++) {
							g.drawImage(bkgDefault, 
									bkgDefault.getWidth() * j, bkgDefault.getHeight() * i, 
									bkgDefault.getWidth(), bkgDefault.getHeight(), null);
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
								Registry.render((Graphics2D) g);
								g.setColor(new Color(0.8f, 0.8f, 1.0f, 0.25f));
								g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
							}
							
							{
								setOpaque(false);
								setName("chatPane");
								addComponentListener(ChatFrame.this);
							}
						};
						
						msgsScroll = new JScrollPane(chatPanel) {
							{
//								setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, 1, true), new EmptyBorder(0, 3, 3, 3)));
								setViewportBorder(null);
								
								setBorder(null);
								setOpaque(false);
								getViewport().setOpaque(false);
								setAutoscrolls(true);
								
								getViewport().setLayout(new ConstrainedViewPortLayout());
								getVerticalScrollBar().setUnitIncrement(14);
								getVerticalScrollBar().setAutoscrolls(true);
								setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
								getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
							        public void adjustmentValueChanged(AdjustmentEvent e) {
//							            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
//							        	needUpdate = true;
							        }
							    }); 
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
					{
						setOpaque(false);
						setBorder(new EmptyBorder(0, 0, 0, 1));
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
								setBorder(new EmptyBorder(3, 3, 3, 3));
								
								setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
								setSelectionBackground(new Color(1.0f, 1.0f, 1.0f, 0.2f));
								setSelectionForeground(new Color(1.0f, 1.0f, 0.0f, 1.0f));
								
								setBackground(uListBackColor);
								setForeground(Color.WHITE);
								setFont(Registry.fUsers);
//								setVisibleRowCount(5);
								
								addMouseListener(new MouseAdapter() {
									@Override
									public void mouseReleased(MouseEvent e) {
										if (locationToIndex(e.getPoint()) != -1) {
											rightPane.repaint();
											inputArea.requestFocus();
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
				
				JPanel downPane = new JPanel(new BorderLayout(3, 3)) {
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
								new EmptyBorder(3, 3, 24, 3),
								BorderFactory.createTitledBorder(null, "Ввод:", 1, 2, Registry.fMessage, Color.GRAY)
								)
						);
//						setCursor(FoxCursor.createCursor(ResourceManager.getBufferedImage("cur_1"), "ansC"));
						
						correctPane = new JPanel(new FlowLayout(0, 3, 3)) {
							{
								setOpaque(false);
								setBorder(new EmptyBorder(-5, 0, -5, -5));
								setPreferredSize(new Dimension(0, 22));
								
								JButton music = new JButton("music") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("music");
										addActionListener(ChatFrame.this);
									}
								};
								JButton photo = new JButton("photo") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("photo");
										addActionListener(ChatFrame.this);
									}
								};
								JButton document = new JButton("document") {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setPreferredSize(new Dimension(100, 24));
										setActionCommand("document");
										addActionListener(ChatFrame.this);
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
//								getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//							        public void adjustmentValueChanged(AdjustmentEvent e) {
//							            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
//							        	needUpdate = true;
//							        }
//							    }); 
								setPreferredSize(new Dimension(0, 50));
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
			public void windowClosing(WindowEvent e) {
				Object[] options = { "Да", "Нет!" };
				int n = JOptionPane.showOptionDialog(
								e.getWindow(), "Закрыть окно?", "Подтверждение", 
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
				
				if (n == 0) {disconnectAndExit();}
			}
		});
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setupInAc();
		ChatStyler.loadUIStyle();

		pack();
		setSize(new Dimension(getWidth(), java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
		setLocationRelativeTo(null);
		setVisible(true);

		NetConnector.incomeNewUser(Registry.myNickName);
		
		// поток обновления UI:
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (needUpdate) {
						needUpdate = false;
						rightPane.setPreferredSize(new Dimension(ChatFrame.this.getWidth() / 5, 0));
						repaint();
						
//						try {Thread.sleep(50);} catch (InterruptedException e) {/* IGNORE SLEEP */}
						
						revalidateChatBaloonsPanel();						
						repaint();
						
						inputArea.requestFocusInWindow();
						usersList.clearSelection();
					}
					try {Thread.sleep(100);} catch (InterruptedException e) {/* IGNORE SLEEP */}
				}
			}
		}) {{start();}};
		
		// поток сетевого подключения:
		NetConnector.reConnect();
		
		Media.playSound("launched");
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
				sendMessage(inputArea.getText(), messageType.OUTPUT);
				inputArea.requestFocusInWindow();
			}
		});
	}

	private void init() {
		try {
			ResManager.add("grass", new File("./resources/images/grass.png"));
			ResManager.add("connectButtonImage", new File("./resources/images/connectButtonImage.png"));
			ResManager.add("sendButtonImage", new File("./resources/images/DEFAULT/btn.png"));
		} catch (Exception e) {e.printStackTrace();}
		
		ChatStyler.setUIStyle(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE) == -1 ? 0 : IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE));
		ChatStyler.setBackgroundFillStyle(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE));
	}

	
	public static void addUserToList(String newUserName) {usersListModel.addElement(newUserName);}

	public static void sendMessage(String message, messageType type) {
		if (message.isEmpty() || message.isBlank()) {return;}
		String from = "System", to = Registry.myNickName;
		Color balloonColor = Color.WHITE;
		boolean successfulSended = false;

		switch (type) {
			case WARN: 		
				balloonColor = mesColWarn;
				Media.playSound("systemError");
				break;
			case SYSTEM: 	
				balloonColor = mesColSystem;
				Media.playSound("systemError");
				break;
			case INPUT: // /from SERVER: 123
				if (message.startsWith("/from ")) {
					if (message.split(": ").length > 1) {
						from = message.split(": ")[0].replace("/from ", "");
						message = message.split(": ")[1];
					}
				}
				balloonColor = mesColInput;
				Media.playSound("messageReceive");
				break;
			case OUTPUT: 	
				balloonColor = mesColOutput;
				
				if (NetConnector.getNetState() == connState.CONNECTED) {
					if (message.startsWith("/to ")) {
						if (message.split(": ").length > 1) {
							to = message.split(": ")[0].replace("/to ", "");
							message = message.split(": ")[1];
						}
					} else {to = "Всем";}

					inputArea.setText(null);
					successfulSended = NetConnector.writeMessage(message);					
				}
				
				Media.playSound("messageSend");
				break;
			default:	
		}

		if ((type == messageType.OUTPUT && successfulSended) || type == messageType.INPUT) {addChatBaloon(type, message, from, to, balloonColor);
		} else {
			if (type == messageType.SYSTEM || type == messageType.WARN) {addChatBaloon(type, message, from, to, balloonColor);				
			} else {addChatBaloon(messageType.WARN, "Не отправлено: " + message, from, to, mesColWarn);}
		}

		scrollDown();
		
		needUpdate = true;
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
	
	private static void addChatBaloon(messageType type, String message, String from, String to, Color balloonColor) {
		chatPanel.add(new BaloonPane(type, message, from, to, balloonColor));
		chatPanel.add(Box.createVerticalStrut(3));	
	}
	
	private void revalidateChatBaloonsPanel() {
		for (Component bc : chatPanel.getComponents()) {
			BaloonPane bPane;
			Double baloonHeight = 75D;
			
			if (bc instanceof BaloonPane) {
				bPane = (BaloonPane) bc;
				
				for (Component baloon : bPane.getComponents()) {
					if (baloon instanceof JPanel && baloon.getName().equals("baloon")) {
						
						// набираем начальные переменные:
						String areaText = null, labelText = null;
						Double lineHeight = 0D;
						JTextArea a = null;
						
						for (Component c : ((JPanel) baloon).getComponents()) {
							if (c instanceof JTextArea) {
								a = ((JTextArea) c);
								areaText = a.getText();
								lineHeight = Registry.ffb.getStringHeight(a.getGraphics(), areaText) + 6D;
							} else if (c instanceof JLabel) {
								labelText = ((JLabel) c).getText();
								labelText += bPane.getHeaderText();
							}
						}
						
						// рассчитываем размеры баллона:						
						if (areaText != null && labelText != null) {
							// устанавливаем размеры панели баллона:
							baloonHeight = calculateBaloonSize(baloon, msgsScroll.getSize().getWidth() - 19D - 20D, a, areaText, labelText, lineHeight);
							baloon.revalidate();
						}
					}
				}
				
				bPane.setPreferredSize(new Dimension((int) (msgsScroll.getSize().getWidth() - 19D), baloonHeight.intValue()));
//				bPane.setOpaque(true);
//				bPane.setBackground(Color.BLUE);
//				repaint();
			}
		}
	}
	
	private Double calculateBaloonSize(Component baloon, Double maxWidth, JTextArea area, String message, String label, Double textHeight) {
		int height = 72;
		int nlCount = message.split("\n").length - 1;
		 // по умолчанию, ширина: длина баллона на все окно, высота: на одну строку текста + два лейбла.
		baloon.setPreferredSize(new Dimension(maxWidth.intValue(), height));
		
		String maxLine = "";
		for (String line : message.split("\n")) {
			if (line.length() > maxLine.length()) {maxLine = line;}
		}
		
		// WIDTH:
		Double labelWidth = Registry.ffb.getStringWidth(area.getGraphics(), label) * 0.75D;
		Double maxLineWidth = Registry.ffb.getStringWidth(area.getGraphics(), maxLine) + 36D;
		if (maxLineWidth < maxWidth) {
			if (maxLineWidth > labelWidth) {baloon.setPreferredSize(new Dimension(maxLineWidth.intValue(), height));
			} else {baloon.setPreferredSize(new Dimension(labelWidth.intValue(), height));}
		}
		
		// HEIGHT:
		Double messageWidth = Registry.ffb.getStringWidth(area.getGraphics(), message);
		Double lineHeight = Registry.ffb.getStringHeight(area.getGraphics(), maxLine) + 1.8D;
		
		height += (nlCount * lineHeight);
		height += (int) (messageWidth / maxWidth * lineHeight);
		baloon.setPreferredSize(new Dimension(baloon.getPreferredSize().width, height));
/*
Player models can also be requested by the server, just set your "model" console variable to the desired model to download and wait for 2 map changes.
If you already have set it before connecting it will only take one map change. If the server has the requested player model your client will download it.

Я не уверен, что есть метод лучше, чем тот, который вы упомянули. Проблема в том, что в общем случае вычитание прямоугольной 
области из другой оставит дыру где-то посередине, поэтому результат на самом деле не прямоугольник. В вашем случае вы знаете, что 
панель задач умещается точно на одной из сторон прямоугольника экрана, поэтому «лучший» способ действительно выяснить, 
с какой стороны она находится, и вычесть ширину / высоту с этой стороны.
*/
		// возвращаем новую высоту родительского baloonPane: 
		return (double) height;
	}

	public static void disconnectAndExit() {
		NetConnector.disconnect();
		IOM.saveAll();
		System.exit(0);
	}
	
	
	// UTILITES:
	public static void saveChatToFile() {
		
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
	public static void setSendButtonSprite(BufferedImage[] spritelist) {
		sendButtonSprite = spritelist;
//		sendButton.repaint();
	}
	public static void setBackgroundImage(BufferedImage bkgImage) {
		bkgDefault = bkgImage;
		frame.repaint();
	}
	public static void setSidePanelsBkg(Color color) {
		cSidePanelsBkg = color;
//		rightPane.repaint();
//		leftPane.repaint();
	}
	public static void setupMenuBar(JMenuBar mBar) {frame.setJMenuBar(mBar);}
	public static void setUsersListBackground(Color color) {
		try {usersList.setBackground(color);			
		} catch (Exception e) {uListBackColor = color;}
	}
	
	
	// LISTENERS:
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "send": sendMessage(inputArea.getText(), messageType.OUTPUT);
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
	public void componentResized(ComponentEvent e) {needUpdate = true;}

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
}