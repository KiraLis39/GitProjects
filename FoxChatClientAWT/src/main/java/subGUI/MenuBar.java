package subGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import fox.adds.IOM;
import fox.builders.ResManager;
import gui.ChatFrame;
import gui.ChatStyler;
import media.Media;
import net.NetConnector;
import net.NetConnector.connState;
import registry.IOMs;
import registry.Registry;


@SuppressWarnings("serial")
public class MenuBar extends JMenuBar implements ActionListener {
	private static Icon onlineIcon, offlineIcon;
	private static Icon resetIPButtonIcon;
	private static Icon switchOnIcon, switchOnOverIcon;
	private static Icon switchOffIcon, switchOffOverIcon;
	
	public static Color textColor;
	
	private static JTextField fieldIP, fieldPort;
	private static JLabel connectLabel;
	private static JCheckBox box1, box2;
	private JRadioButtonMenuItem styleDefault, styleGold, styleDark;
	private JCheckBoxMenuItem dpOpacityBox;
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(ResManager.getBImage("menuBarImage"), 0, 0, getWidth(), getHeight(), this);
	}
	
	public MenuBar(Color color) {
		{
			if (color != null) {textColor = color;}
			onlineIcon = new ImageIcon(ResManager.getFilesLink("onlineImage").getPath());
			offlineIcon = new ImageIcon(ResManager.getFilesLink("offlineImage").getPath());
			resetIPButtonIcon = new ImageIcon(ResManager.getFilesLink("resetIPButtonImage").getPath());
			
			switchOnIcon = new ImageIcon(ResManager.getFilesLink("switchOnImage").getPath());
			switchOnOverIcon = new ImageIcon(ResManager.getFilesLink("switchOnoverImage").getPath());
			switchOffIcon = new ImageIcon(ResManager.getFilesLink("switchOffImage").getPath());
			switchOffOverIcon = new ImageIcon(ResManager.getFilesLink("switchOffoverImage").getPath());
			
			setOpaque(false);
			setBorder(new EmptyBorder(9, 0, 9, 0));
			
			JMenu file = new JMenu("Чат") {
				{
					setOpaque(false);
					setIcon(new ImageIcon("./resources/images/file.png"));
					setBorder(new EmptyBorder(0, 3, 0, 6));
					setForeground(textColor);
					
					JMenuItem open = new JMenuItem("Сохранить в файл...") {
						{
							setOpaque(true);
							setActionCommand("save");
							addActionListener(MenuBar.this);
							setIcon(new ImageIcon("./resources/images/save.png"));
						}
					};
					JMenuItem exit = new JMenuItem("Выход") {
						{
							setOpaque(true);
							setActionCommand("exit");
							addActionListener(MenuBar.this);
						}
					};
					
					add(open);
					addSeparator();
					add(exit);
				}
			};
			
			JMenu viewMenu = new JMenu("Вид") {
				{
					setOpaque(false);
					setIcon(new ImageIcon("./resources/images/view.png"));
					setBorder(new EmptyBorder(0, 3, 0, 6));
					setForeground(textColor);

			        add(new JCheckBoxMenuItem("Левая панель", null, true) {
						{
							setOpaque(true);
							setActionCommand("leftPaneVis");
							addActionListener(MenuBar.this);
							setSelected(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_LEFT_PANEL));
						}
					});
			        add(new JCheckBoxMenuItem("Список юзеров", null, true) {
						{
							setOpaque(true);
							setActionCommand("rightPaneVis");
							addActionListener(MenuBar.this);
							setSelected(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_USERS_PANEL));
						}
					});
			        add(new JSeparator());
			        
			        add(new JMenuItem("Фон сменить") {
						{
							setActionCommand("bckChose");
							addActionListener(MenuBar.this);
						}
					});
			        
			        JMenu h1 = new JMenu("Фон:");
			        final JRadioButtonMenuItem bckBySize = new JRadioButtonMenuItem("Вписать") {
		        		{
		        			setActionCommand("bckBySize");
							addActionListener(MenuBar.this);
							setSelected(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE) == 0);
		        		}
		        	};
		        	final JRadioButtonMenuItem bkgFill = new JRadioButtonMenuItem("Замостить") {
		        		{
		        			setActionCommand("bkgFill");
							addActionListener(MenuBar.this);
							setSelected(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE) == 1);
		        		}
		        	};
		        	final JRadioButtonMenuItem bkgAsIs =  new JRadioButtonMenuItem("Как есть") {
		        		{
		        			setActionCommand("bkgAsIs");
							addActionListener(MenuBar.this);
							setSelected(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE) == 2);
		        		}
		        	};
		        	final JRadioButtonMenuItem bkgProp = new JRadioButtonMenuItem("По центру") {
		        		{
		        			setActionCommand("bkgProp");
							addActionListener(MenuBar.this);
							setSelected(IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE) == 3);
		        		}
		        	};

			        new ButtonGroup() {
			        	{
			        		add(bckBySize);
					        add(bkgFill);
					        add(bkgAsIs);
					        add(bkgProp);
			        	}
			        };
			        
			        h1.add(bckBySize);
			        h1.add(bkgFill);
			        h1.add(bkgAsIs);
			        h1.add(bkgProp);
			        
			        add(h1);
			        
			        dpOpacityBox = new JCheckBoxMenuItem("Фильтр окна диалога") {
						{
							setOpaque(true);
							setActionCommand("dialogOpasity");
							addActionListener(MenuBar.this);
							setSelected(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_DIALOGPANE_OPACITY));
						}
					};
			        add(dpOpacityBox);
			        add(new JSeparator());
			        
			        styleDefault = new JRadioButtonMenuItem("Стиль Утро", null, IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE) == 0) {
						{
							setActionCommand("stlDefault");
							addActionListener(MenuBar.this);
						}
					};
					styleGold = new JRadioButtonMenuItem("Смешанный", null, IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE) == 1) {
						{
							setActionCommand("stlGold");
							addActionListener(MenuBar.this);
						}
					};
					styleDark = new JRadioButtonMenuItem("Стиль Вечер", null, IOM.getInt(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE) == 2) {
						{
							setActionCommand("stlEvening");
							addActionListener(MenuBar.this);
						}
					};
					 new ButtonGroup() {
				        	{
				        		add(styleDefault);
						        add(styleGold);
						        add(styleDark);
				        	}
				        };
				     add(styleDefault);
				     add(styleGold);
				     add(styleDark);
				}
			};
	        
	        JMenu optMenu = new JMenu("Настройки") {
				{
					setOpaque(false);
					setIcon(new ImageIcon("./resources/images/options.png"));
					setBorder(new EmptyBorder(0, 3, 0, 0));
					setForeground(textColor);
					
					add(new JMenuItem("Подключение") {
						{
							setActionCommand("tune");
							addActionListener(MenuBar.this);
						}
					});
				}
			};
	        
			add(file);
			add(viewMenu);
			add(optMenu);
			
//			add(Box.createHorizontalStrut(15));
			add(Box.createHorizontalGlue());
			
			connectLabel = new JLabel("On-line:", offlineIcon, JLabel.CENTER) {
				{
//					setFocusPainted(false);
					setPreferredSize(new Dimension(120, 24));
					setBackground(textColor == Color.BLACK ? Color.GRAY : Color.BLACK);
//					setActionCommand("connect");
//					addActionListener(MenuBar.this);
				}
			};
			add(connectLabel);
			
			add(Box.createHorizontalStrut(15));
//			add(Box.createHorizontalGlue());				
			
			 JMenu helpMenu = new JMenu("Help") {
					{
						setOpaque(false);
						setIcon(new ImageIcon("./resources/images/help.png"));
						setBorder(new EmptyBorder(0, 3, 0, 6));
						setForeground(textColor);
						
							JMenu h1 = new JMenu("Обратная связь:");
					        	JMenuItem h2 = new JMenuItem("AngelicaLis39@mail.ru");
					        	JMenuItem h3 = new JMenuItem("https://vk.com/anestorf");
				        	h1.add(h2);
				        	h1.add(h3);
				        	
		        		add(h1);
					}
				};
			add(helpMenu);
			
			fieldIP = new JTextField("127.0.0.1", 10) {
				{
					setFont(Registry.fBigSphere);
					setBackground(Color.BLACK);
					setForeground(Color.GREEN);
					setCaretColor(Color.YELLOW);
					setHorizontalAlignment(0);
					setBorder(null);
					addFocusListener(new FocusAdapter() {
						@Override
						public void focusGained(FocusEvent e) {selectAll();}
					});
					addKeyListener(new KeyAdapter() {
						@Override
						public void keyReleased(KeyEvent e) {
							if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
								if (getText().length() == 3 || getText().length() == 7) {setText(getText() + ".");}
							}
						}
					});
				}
			};
			
			fieldPort = new JTextField("13900", 5) {
				{
					setFont(Registry.fBigSphere);
					setBackground(Color.BLACK);
					setForeground(new Color(0, 127, 255));
					setCaretColor(Color.YELLOW);
					setBorder(null);
					setHorizontalAlignment(0);
					addFocusListener(new FocusAdapter() {										
						@Override
						public void focusGained(FocusEvent e) {selectAll();}
					});
				}
			};
			
			fieldIP.setText(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_IP));
			fieldPort.setText(IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_PORT));
		}
	}

	public JMenuBar getMenu() {return new MenuBar(null);}

	public static String getIP() {return fieldIP.getText().replace(",", ".");}

	public static int getPort() {
		try {return Integer.parseInt(fieldPort.getText());			
		} catch (Exception e) {return 0;}
	}
	
	private void choseNewBackgroundImage() {
//		UIManager.put("FileChooser.saveButtonText", "Сохранить");
//		UIManager.put("FileChooser.cancelButtonText", "Отмена");
//     UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
//     UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
//     UIManager.put("FileChooser.lookInLabelText", "Директория");
//     UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
//     UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
       
		JFileChooser bkgImageChooser = new JFileChooser("./resources/images/backgrounds/") {
			{
				setDialogTitle("Новый бэкграунд:");
				setFileFilter(new FileNameExtensionFilter("Images", "PNG", "JPG"));
				setFileHidingEnabled(false);
				setFileSelectionMode(JFileChooser.FILES_ONLY);
				
			}
		};
		
		int result = bkgImageChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File newBkgFile = bkgImageChooser.getSelectedFile();
			BufferedImage newBkgImage;
			try {
				newBkgImage = ImageIO.read(newBkgFile);
				ChatFrame.setBackgroundImage(newBkgImage, newBkgFile.getCanonicalPath());
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(this, "<HTML>Произошла ошибка<br>при открытии файла<br>" + newBkgFile, e.getMessage(), 
						JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		}		
	}
	
	public static void setConnLabelText(String text) {connectLabel.setText(text);}

	public static void setReconnectButton(Color bkg, Color frg, String text) {
		connectLabel.setBackground(bkg);
		connectLabel.setForeground(frg);
		connectLabel.setIcon(text.equals("On-Line") ? onlineIcon : offlineIcon);
		setConnLabelText(text);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "connect": 
				if (NetConnector.getNetState() == connState.DISCONNECTED) {
					if (IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN).equalsIgnoreCase("none")) {
						new LoginFrame();
					} else {
						NetConnector.reConnect(
								IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_LOGIN), 
								IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.USER_PASSWORD).toCharArray());
					}
				} else {NetConnector.disconnect();}				
				break;
			case "exit": ChatFrame.disconnectAndExit();
				break;
			case "save": ChatFrame.saveChatToFile();
				break;
				
			case "leftPaneVis": ChatFrame.switchLeftPaneVisible();
				break;
			case "rightPaneVis": ChatFrame.switchRightPaneVisible();
				break;
				
			case "bckChose": choseNewBackgroundImage();
				break;
			case "bckBySize": 	ChatStyler.setBackgroundFillStyle(0);
				break;
			case "bkgFill": 		ChatStyler.setBackgroundFillStyle(1);
				break;
			case "bkgAsIs": 	ChatStyler.setBackgroundFillStyle(2);
				break;
			case "bkgProp": 	ChatStyler.setBackgroundFillStyle(3);
				break;
			case "dialogOpasity": ChatFrame.setDialogOpacity(dpOpacityBox.isSelected());
				break;
				
			case "stlDefault": ChatStyler.setUIStyle(0);
				break;
			case "stlGold": 		ChatStyler.setUIStyle(1);
				break;
			case "stlEvening":ChatStyler.setUIStyle(2);
				break;
				
			case "tune": new OptionsDialog();
				break;
			default: 
		}
	}

	
	public static class OptionsDialog extends JDialog implements ActionListener {
		
		public OptionsDialog() {
			setTitle("Настройки чата:");
//			setAlwaysOnTop(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setMinimumSize(new Dimension(440, 200));
			
			JPanel connectPane = new JPanel(new BorderLayout(3, 3)) {
				{
					setBackground(Color.DARK_GRAY);
					
					JPanel ipAndPortPane = new JPanel(new FlowLayout(1, 9, 0)) {
						{
							setOpaque(false);
							setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
									BorderFactory.createLineBorder(textColor, 1, true), "Address:", 0, 2, Registry.fMenuBar, textColor), 
									new EmptyBorder(0, 0, 0, 0)));
							
							JPanel ipPane = new JPanel(new BorderLayout(3, 3)) {
								{
									setOpaque(false);
									
									JLabel ipLabel = new JLabel("IP: ") {
										{
											setFont(Registry.fMenuBarBig);
											setForeground(textColor);
											setHorizontalAlignment(JLabel.RIGHT);
										}
									};
									
									JButton resetIPButton = new JButton(resetIPButtonIcon) {
										{
											setBackground(textColor == Color.BLACK ? null : Color.BLACK);
											setActionCommand("resetIP");
											setToolTipText("Reset to localhost");
											setPreferredSize(new Dimension(32, 32));
											setFocusPainted(false);
//											setBorderPainted(false);
//											setBorder(BorderFactory.createRaisedBevelBorder());
											addActionListener(OptionsDialog.this);
										}
									};
									
									add(ipLabel, BorderLayout.WEST);
									add(fieldIP, BorderLayout.CENTER);
									add(resetIPButton, BorderLayout.EAST);
								}
							};
							
							JPanel portPane = new JPanel(new BorderLayout(3, 3)) {
								{
									setOpaque(false);
									
									JLabel portLabel = new JLabel("PORT: ") {
										{
											setFont(Registry.fMenuBarBig);
											setForeground(textColor);
											setHorizontalAlignment(JLabel.RIGHT);
										}
									};
									JButton resetPortButton = new JButton(resetIPButtonIcon) {
										{
											setBackground(textColor == Color.BLACK ? null : Color.BLACK);
											setActionCommand("resetPort");
											setToolTipText("Reset to default port");
											setPreferredSize(new Dimension(32, 32));
											setFocusPainted(false);
//											setBorderPainted(false);
//											setBorder(BorderFactory.createRaisedBevelBorder());
											addActionListener(OptionsDialog.this);
										}
									};

									add(portLabel, BorderLayout.WEST);
									add(fieldPort, BorderLayout.CENTER);
									add(resetPortButton, BorderLayout.EAST);
								}
							};
							
							add(ipPane);
							add(portPane);
						}
					};

					JPanel switchPane = new JPanel(new GridLayout(2, 1)) {
						{
							setOpaque(false);
							setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
									BorderFactory.createLineBorder(textColor, 1, true), "Options:", 0, 2, Registry.fMenuBar, textColor), 
									new EmptyBorder(0, 0, 0, 0)));
							
							box1 = new JCheckBox("Звуковые оповещения", switchOffIcon, false) {
								{
									setOpaque(false);
									setFocusPainted(false);
									setForeground(Color.WHITE);
									setFont(Registry.fLabels);
									setSelectedIcon(switchOnIcon);
									addItemListener(new ItemListener() {										
										@Override
										public void itemStateChanged(ItemEvent e) {
											IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUNDS_ENABLED, isSelected());
											Media.setSoundEnabled(isSelected());
										}
									});
									addMouseListener(new MouseAdapter() {
										@Override
										public void mouseExited(MouseEvent e) {
											if (isSelected()) {setIcon(switchOnIcon);
											} else {setIcon(switchOffIcon);}
										}

										@Override
										public void mouseEntered(MouseEvent e) {
											if (isSelected()) {setIcon(switchOnOverIcon);
											} else {setIcon(switchOffOverIcon);}
										}
									});
								}
							};
							
							box2 = new JCheckBox("Разрешить анимацию", switchOffIcon, false) {
								{
									setOpaque(false);
									setFocusPainted(false);
									setForeground(Color.WHITE);
									setFont(Registry.fLabels);
									setSelectedIcon(switchOnIcon);
									addItemListener(new ItemListener() {										
										@Override
										public void itemStateChanged(ItemEvent e) {
											IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.ANIMATION_ENABLED, isSelected());
											if (isSelected()) {ChatFrame.zaglushko();}
										}
									});
									addMouseListener(new MouseAdapter() {
										@Override
										public void mouseExited(MouseEvent e) {
											if (isSelected()) {setIcon(switchOnIcon);
											} else {setIcon(switchOffIcon);}
										}

										@Override
										public void mouseEntered(MouseEvent e) {
											if (isSelected()) {setIcon(switchOnOverIcon);
											} else {setIcon(switchOffOverIcon);}
										}
									});
								}
							};
							
							add(box1);
							add(box2);
						}
					};
					
					add(ipAndPortPane, BorderLayout.NORTH);
					add(switchPane, BorderLayout.CENTER);
				}
			};
			
			add(connectPane);
			
			pack();
			setLocationRelativeTo(null);
			setVisible(true);
			
			box1.setSelected(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUNDS_ENABLED));
			box2.setSelected(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.ANIMATION_ENABLED));
		}

		
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
				case "resetIP": fieldIP.setText("127.0.0.1");
					break;
				case "resetPort": fieldPort.setText("13900");
					break;
				default:
			}
		}
	}
}