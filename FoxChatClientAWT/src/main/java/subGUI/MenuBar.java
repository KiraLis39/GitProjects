package subGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import fox.adds.IOM;
import fox.builders.ResManager;
import gui.ChatFrame;
import gui.ChatStyler;
import net.NetConnector;
import net.NetConnector.connState;
import registry.IOMs;
import registry.Registry;


@SuppressWarnings("serial")
public class MenuBar extends JMenuBar implements ActionListener {
	private Icon connectButtonIcon;
	
	public static Color textColor;
	
	private static JTextField fieldIP, fieldPort;
	private static JButton reConnectButton;
	private JRadioButtonMenuItem styleDefault, styleGold, styleDark;
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(ResManager.getBImage("menuBarImage"), 0, 0, getWidth(), getHeight(), this);
	}
	
	public MenuBar(Color color) {
		{
			if (color != null) {textColor = color;}
			connectButtonIcon = new ImageIcon(ResManager.getFilesLink("connectButtonImage").getPath());
			
			setOpaque(false);
//			setBorder(new EmptyBorder(6, 6, 3, 6));
			
			JMenu file = new JMenu("Чат") {
				{
					setOpaque(false);
					setIcon(new ImageIcon("./resources/images/file.png"));
					setBorder(new EmptyBorder(3, 3, 0, 6));
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
					setBorder(new EmptyBorder(3, 3, 0, 6));
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
					setBorder(new EmptyBorder(3, 3, 0, 6));
					setForeground(textColor);
				}
			};
	        
			add(file);
			add(viewMenu);
			add(optMenu);
			
			add(Box.createHorizontalStrut(15));
			add(Box.createHorizontalGlue());
						
			
			JPanel connectPane = new JPanel(new FlowLayout()) {
				{
					setOpaque(false);
					setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(
							BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true), "Address:", 1, 2, Registry.fMenuBar, textColor), 
							new EmptyBorder(-12, 0, -6, 0)));
					
					JPanel ipPane = new JPanel(new BorderLayout()) {
						{
							setOpaque(false);
							
							JLabel ipLabel = new JLabel("IP: ") {
								{
									setFont(Registry.fMenuBarBig);
									setForeground(textColor);
									setHorizontalAlignment(JLabel.RIGHT);
								}
							};
							fieldIP = new JTextField("127.0.0.1", 10); // localhost
							
							add(ipLabel);
							add(fieldIP, BorderLayout.EAST);
						}
					};
					
					JPanel portPane = new JPanel(new BorderLayout()) {
						{
							setOpaque(false);
//							setAlignmentX(CENTER_ALIGNMENT);
							
							JLabel portLabel = new JLabel("PORT: ") {
								{
									setFont(Registry.fMenuBarBig);
									setForeground(textColor);
									setHorizontalAlignment(JLabel.RIGHT);
								}
							};
							fieldPort = new JTextField("13900", 5);
							
							add(portLabel);
							add(fieldPort, BorderLayout.EAST);
						}
					};

					JPanel conButPane = new JPanel(new BorderLayout()) {
						{
							setOpaque(false);
//							setBorder(new EmptyBorder(9, 0, 9, 0));
							
							reConnectButton = new JButton(connectButtonIcon) {
								{
									setBackground(textColor == Color.BLACK ? Color.GRAY : Color.BLACK);
									setActionCommand("connect");
									addActionListener(MenuBar.this);
								}
							};
							
							add(reConnectButton, BorderLayout.EAST);
						}
					};

					add(ipPane);		
					add(portPane);
					add(conButPane);
				}
			};
			
			add(connectPane);
			add(Box.createHorizontalStrut(15));
			add(Box.createHorizontalGlue());				
			
			 JMenu helpMenu = new JMenu("Help") {
					{
						setOpaque(false);
						setIcon(new ImageIcon("./resources/images/help.png"));
						setBorder(new EmptyBorder(3, 3, 0, 6));
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
		}
	}

	public JMenuBar getMenu() {return new MenuBar(null);}

	public static String getIP() {return fieldIP.getText();}

	public static int getPort() {return Integer.parseInt(fieldPort.getText());}
	
	private void choseNewBackgroundImage() {
		
	}
	
	public static void setConnBtnText(String text) {reConnectButton.setText(text);}

	public static void setReconnectButton(Color bkg, Color frg, String text) {
		reConnectButton.setBackground(bkg);
		reConnectButton.setForeground(frg);
		setConnBtnText(text);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "connect": 
				if (NetConnector.getNetState() == connState.DISCONNECTED) {NetConnector.reConnect();
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
			case "stlDefault": ChatStyler.setUIStyle(0);
				break;
			case "stlGold": 		ChatStyler.setUIStyle(1);
				break;
			case "stlEvening":ChatStyler.setUIStyle(2);
				break;
			default: 
		}
	}

}