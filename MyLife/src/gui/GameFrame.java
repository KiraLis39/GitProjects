package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import base.MidBuilder;
import base.Registry;
import door.Exit;
import enums.ENUMS;
import enums.ENUMS.IOMsave;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.FoxFontBuilder.FONT;
import fox.builders.ResourceManager;
import subgui.GamesSettingsPane;


@SuppressWarnings("serial")
public class GameFrame extends JFrame implements WindowListener, ComponentListener, WindowStateListener, ActionListener, MouseListener {
//	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	private int F_WIDTH = (int) (dim.width * 0.8f), F_HEIGHT = (int) (dim.height * 0.81f);
	
	private Font f0 = Registry.ffb.setFoxFont(FONT.BAHNSCHRIFT, 14, false);
	private Font lvlFont 	= Registry.ffb.setFoxFont(6, 14, true);
	private Font payFont = Registry.ffb.setFoxFont(5, 12, true);
	private Font FPSfont = Registry.ffb.setFoxFont(FONT.ARIAL_NARROW, 26, true);
	
	private BufferedImage heroNorm, heroMono, baseGameFon;
	private BufferedImage button01Icon, button02Icon, button03Icon, button04Icon, button05Icon, button06Icon, button07Icon, button08Icon, expIcon;

	private JPanel basePane, downPane, centerPane, infoPane, buttonsPane, leftPane, leftButtonsPane, centerHeroPane;
	private JProgressBar expBar;
	private JButton buttonMenu, buttonPause, buttonSave;
	private JButton button0, button1, button2, button3, button4, button5, button6, button7;
	private GamesSettingsPane BackMenuPane;

	private String infoText = "Some info text been here soon...";
	private String userName;
	
	private Boolean frameIsOpen = true;
	
	private long time;
	private int buttonsDim = 96;
	private int counter = 0, fpsMarker = 0;
	

 	public GameFrame(String heroName, String heroSex) {        
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
//		dateFormat.setTimeZone(TimeZone.getTimeZone("+3"));
		userName = IOM.getString(IOM.HEADERS.LAST_USER, "LAST");
		
		setTitle(Registry.titleAndVers);
		setIconImage(ResourceManager.getBufferedImage("FrameIconPicture"));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(F_WIDTH, F_HEIGHT));
		
		buildImages();
		
		BackMenuPane = new GamesSettingsPane(GameFrame.this, "Опции игры:", true);
		
		centerPane = new MidBuilder(MidBuilder.TYPE.NONE);
		
		basePane = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				Registry.render(g2D);
				
				g2D.drawImage(baseGameFon, 
						getWidth() / 2 - baseGameFon.getWidth() / 2, getHeight() / 2 - baseGameFon.getHeight() / 2, 
						baseGameFon.getWidth(), baseGameFon.getHeight(), 
						null);
				
				g2D.setColor(Registry.isPaused ? new Color(0.0f, 0.0f, 0.0f, 0.25f) : new Color(0.0f, 0.0f, 0.0f, 0.75f));
				g2D.fillRect(0, getHeight() - 64, getWidth(), 64);
				
				g2D.setStroke(new BasicStroke(3.5f));
				g2D.setColor(new Color(0.2f, 0.2f, 0.2f, 0.5f));
				g2D.drawRect(0, getHeight() - 64, getWidth(), 64);
			}
			
			{
				setOpaque(false);
				setIgnoreRepaint(true);
				
				downPane = new JPanel(new BorderLayout()) {
					{
						setOpaque(false);
						setDoubleBuffered(true);
						setIgnoreRepaint(true);
						setLayout(new BorderLayout());
						
						infoPane = new JPanel(new GridLayout(1, 3, 9, 9)) {
							@Override
							protected void paintComponent(Graphics g) {
								Graphics2D g2D = (Graphics2D) g;
								Registry.render(g2D);
								
								g2D.setColor(Registry.isPaused ? new Color(0.0f, 0.0f, 0.0f, 0.3f) : new Color(0.0f, 0.0f, 0.0f, 0.5f));
								g2D.fillRoundRect(0, 0, getWidth() - 6, getHeight(), 6, 6);
								
								g2D.setColor(new Color(0.25f, 0.25f, 0.25f, 0.5f));
								g2D.drawRoundRect(0, 0, getWidth() - 6, getHeight(), 6, 6);
								
								g2D.setColor(Color.YELLOW);
								g2D.setFont(f0);
								g2D.drawString("Information:", 9, 18);
								
								g2D.setColor(Color.WHITE);
								g2D.drawString(infoText, (int) (Registry.ffb.getStringBounds(g2D, "Information:").getWidth() + 12), 18);
							}
							
							{
								setOpaque(true);
								setIgnoreRepaint(true);
							}
						};
						
						buttonsPane = new JPanel(new GridLayout(1, 3, 9, 9)) {
							{
								setOpaque(false);
								
								buttonMenu = new JButton("Menu") {
									{
										setPreferredSize(new Dimension(64, 64));
										setBackground(Color.BLACK);
										setForeground(Color.GREEN);
										setActionCommand("backMenuClick");
										addMouseListener(GameFrame.this);
										addActionListener(GameFrame.this);
									}
								};
								
								buttonPause = new JButton("Pause") {
									@Override
									protected void paintComponent(Graphics g) {
										Graphics2D g2D = (Graphics2D) g;
										Registry.render(g2D);
										
										if (this.hasFocus()) {
											Composite comp = g2D.getComposite();
											g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
											
											g2D.setPaint(new Color(0.25f, 0.0f, 1.0f, 1.0f));
							                for (int i = 1; i <= 5; i++) {
							                	g2D.setComposite(AlphaComposite.getInstance ( AlphaComposite.SRC_OVER, ( float ) i * i / 40 ) );
							                	g2D.drawRoundRect( i - 1, i - 1, getWidth () - ( i - 1 ) * 2, getHeight () - ( i - 1 ) * 2, 22 - ( i - 1 ) * 2, 22 - ( i - 1 ) * 2 );
							                }											
								            g2D.setComposite(comp);
										}

							            RoundRectangle2D rr = new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 8, 8);
							            g2D.setPaint(new GradientPaint(0, 12, new Color(63, 63, 63), 0, getHeight() - 36, Color.BLACK));
							            g2D.fill(rr);
							            g2D.setPaint(Color.GRAY);
							            g2D.setStroke(new BasicStroke(1));
							            g2D.draw(rr);
							            
							            g2D.setFont(payFont);
							            g2D.setColor(Color.GREEN);
							            g2D.drawString("Pause", (int) (1 + getWidth() / 2D - Registry.ffb.getStringBounds(g2D, "Pause").getWidth() / 2D), (int) (getHeight() / 2D + 4D));
									}
									
									{
										setPreferredSize(new Dimension(64, 64));
										setActionCommand("pauseClick");
										addMouseListener(GameFrame.this);
										addActionListener(GameFrame.this);
									}
								};
								
								buttonSave = new JButton("Save") {
									{
										setPreferredSize(new Dimension(64, 64));
										setBackground(Color.BLACK);
										setForeground(Color.GREEN);
										setActionCommand("saveClick");
										addMouseListener(GameFrame.this);
										addActionListener(GameFrame.this);
									}
								};
							
								add(buttonPause);
								add(buttonMenu);
								add(buttonSave);
							}
						};
						
						add(infoPane, BorderLayout.CENTER);
						add(buttonsPane, BorderLayout.EAST);
					}
				};

				leftPane = new JPanel(new BorderLayout()) {
					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2D = (Graphics2D) g;
						Registry.render(g2D);
						
						g2D.setColor(Registry.isPaused ? new Color(0.0f, 0.0f, 0.0f, 0.6f) : new Color(0.0f, 0.0f, 0.0f, 0.8f));
						g2D.fillRect(0, 0, getWidth(), getHeight());
					}
					
					{
						setBorder(new EmptyBorder(6, 6, 6, 3));
						setOpaque(true);
						setIgnoreRepaint(true);
						
						leftButtonsPane = new JPanel(new GridLayout(8, 0, 3, 3)) {
							{
								setOpaque(false);
								setIgnoreRepaint(true);
								
								button0 = buttonsTuner("Окно персонажа", "showHeroPane", new ImageIcon(button01Icon));
								button1 = buttonsTuner("Окно досуга", "showDosugPane", new ImageIcon(button02Icon));
								button2 = buttonsTuner("Окно работы", "showWorkPane", new ImageIcon(button03Icon));
								button3 = buttonsTuner("Окно отношений", "showLovePane", new ImageIcon(button04Icon));
								button4 = buttonsTuner("Окно религии", "showReligionPane", new ImageIcon(button05Icon));
								button5 = buttonsTuner("Окно спорта", "showSportPane", new ImageIcon(button06Icon));
								button6 = buttonsTuner("Окно гигиены", "showGigienePane", new ImageIcon(button07Icon));
								button7 = buttonsTuner("Окно aдмина", "showAdminPane", new ImageIcon(button08Icon));
								
								add(button0);		add(button1);
								add(button2);		add(button3);
								add(button4);		add(button5);
								add(button6);		add(button7);
							}
						};
						
						centerHeroPane = new JPanel(new BorderLayout()) {
							@Override
							protected void paintComponent(Graphics g) {
								Graphics2D g2D = (Graphics2D) g;
								Registry.render(g2D);
								
								if (Registry.isPaused) {g2D.drawImage(heroMono, 0, 96, getWidth(), getHeight() - 160, null);
								} else {g2D.drawImage(heroNorm, 0, 96, getWidth(), getHeight() - 160, null);}
								
								g2D.setFont(f0);
								g2D.setColor(Color.WHITE);
								g2D.drawString(userName, 6, 24);
								
								g2D.drawImage(expIcon, 6, getHeight() - 64, 32, 32, null);
								
								g2D.drawString("Level: " + IOM.getInt(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.PLAYERS_LEVEL), 42, getHeight() - 51);
								g2D.drawString("Exp: " + 
										IOM.getInt(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.PLAYERS_EXP) 
										+ " / " + 
										Registry.experience[IOM.getInt(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.PLAYERS_LEVEL)], 
										42, getHeight() - 36);
							
								if (Registry.isPaused) {
									g2D.setFont(lvlFont);
									g2D.setColor(Color.LIGHT_GRAY);
									g2D.drawOval(30, 27, 31, 31);
									g2D.fillOval(32, 29, 28, 28);
									g2D.setColor(Color.BLACK);
									g2D.drawOval(32, 29, 28, 28);
									g2D.drawString("P", 42, 48);
								}
								
								drawFPS(g2D);
							}
							
							{
								setOpaque(true);
								setBorder(new EmptyBorder(0, 3, 6, 2));
								setIgnoreRepaint(true);
								
								expBar = new JProgressBar(0, 100) {
									{
										setPreferredSize(new Dimension(0, 18));
										setStringPainted(true);
										setFocusable(false);
										setFont(payFont);
										setIgnoreRepaint(true);
									}
								};
								
								add(expBar, BorderLayout.SOUTH);
							}
						};
						
						add(centerHeroPane, BorderLayout.CENTER);
						add(leftButtonsPane, BorderLayout.WEST);
					}
				};
				
				add(downPane, BorderLayout.SOUTH);
				add(leftPane, BorderLayout.WEST);
				add(centerPane, BorderLayout.CENTER);
			}
		};
		
		add(basePane, BorderLayout.CENTER);
		
		addWindowListener(this);
		addComponentListener(this);
		addWindowStateListener(this);
		
		setLocationRelativeTo(null);
		setVisible(true);		
		
		inacSetup();
		
		Thread gameRePainter = new Thread(new Runnable() {
			@Override
			public void run() {
				time = System.currentTimeMillis();
				
				while (true) {
					if (frameIsOpen) {
						try {			    			
			    			basePane.repaint();
//					    	Toolkit.getDefaultToolkit().sync();
//					    	if (descret <= 60) {
			    			try {Thread.sleep(33);} catch (InterruptedException e) {}
//							} else if (descret <= 72) {try {Thread.sleep(29);} catch (InterruptedException e) {}
//							} else {try {Thread.sleep(24);} catch (InterruptedException e) {}}
						} catch (Exception e) {e.printStackTrace();}
					} else {Thread.yield();}
				}
			}
		});
		gameRePainter.start();
	}
 	
 	private void drawFPS(Graphics2D g2D) {
		counter++;
		if (System.currentTimeMillis() - time >= 1000) {
			fpsMarker = counter;
			counter = 0;
			time = System.currentTimeMillis();
		}
		
		g2D.setColor(Color.ORANGE);
		g2D.setFont(FPSfont);
		g2D.drawString(fpsMarker + " FPS", 120, 30);
	}
 	
 	private JButton buttonsTuner(String tooltipText, String actionCommand, ImageIcon icon) {
		return new JButton() {
			{
				setOpaque(true);
				setPreferredSize(new Dimension(buttonsDim, buttonsDim));
		 		setIcon(icon);
		 		
		 		setBackground(Color.BLACK);
		 		setForeground(Color.GREEN);
		 		setToolTipText(tooltipText);
		 		
		 		setActionCommand(actionCommand);				
				addActionListener(GameFrame.this);
				addMouseListener(GameFrame.this);
			}
		};
	}

	private void inacSetup() {
		Out.Print(getClass(), 0, "Building control keys map (inAc)...");
		
		Registry.inAc.add("gameFrame", GameFrame.this);
		Registry.inAc.set("gameFrame", "exitMenu", KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {runExitRequest();}
		});
	}

	private void buildImages() {
		Out.Print(getClass(), 0, "Building images...");
		
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX).equals("MALE")) {
			try {
				ResourceManager.add("heroNorm", 		new File(Registry.picPath + "hero/001.png"));
				ResourceManager.add("heroMono", 		new File(Registry.picPath + "hero/002.png"));
			} catch (Exception e) {e.printStackTrace();}
		} else {
			try {
				ResourceManager.add("heroNorm", 		new File(Registry.picPath + "hero/003.png"));
				ResourceManager.add("heroMono", 		new File(Registry.picPath + "hero/004.png"));
			} catch (Exception e) {e.printStackTrace();}
		}
		
		heroNorm = ResourceManager.getBufferedImage("heroNorm");
		heroMono = ResourceManager.getBufferedImage("heroMono");
		
		baseGameFon = ResourceManager.getBufferedImage("baseGameFon");
		
		button01Icon = ResourceManager.getBufferedImage("button01Icon");
		button02Icon = ResourceManager.getBufferedImage("button02Icon");
		button03Icon = ResourceManager.getBufferedImage("button03Icon");
		button04Icon = ResourceManager.getBufferedImage("button04Icon");
		button05Icon = ResourceManager.getBufferedImage("button05Icon");
		button06Icon = ResourceManager.getBufferedImage("button06Icon");
		button07Icon = ResourceManager.getBufferedImage("button07Icon");
		button08Icon = ResourceManager.getBufferedImage("button08Icon");
		
		expIcon = ResourceManager.getBufferedImage("exp");
	}

	private void runExitRequest() {
		int close = JOptionPane.showConfirmDialog(null, "Выхотите выйти из игры?", "Внимание:", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		switch (close) {
		case 0: Exit.exit();
			default:
		}
	}	

	public void setExpBarValue(int i) {expBar.setValue(i);}
	public void addExpAdd(int i) {expBar.setValue(expBar.getValue() + i);}
	public int getExpValue() {return expBar.getValue();}
	
	public void setExpBarMaximum(int i) {expBar.setMaximum(i);}
	public int getExpBarMaximum() {return expBar.getMaximum();}

	public void setExpBarString(String string) {
		expBar.setString(string);
		expBar.setToolTipText("Текущий опыт: " + string);
	}
	
	private void setStateText(String state) {
		infoText = state;
//		infoPane.repaint();
	}
	
	
	@Override
	public void windowClosing(WindowEvent e) {runExitRequest();}
	@Override
	public void windowActivated(WindowEvent e) {requestFocus();}
	@Override
	public void windowDeiconified(WindowEvent e) {requestFocus();}
	@Override
	public void windowOpened(WindowEvent e) {requestFocus();}
	
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}


	@Override
	public void componentResized(ComponentEvent e) {
		Out.Print(getClass(), 0, "Resizing the Frame...");
		
		F_WIDTH = getWidth();
		F_HEIGHT = getHeight();
		
		leftPane.setPreferredSize(new Dimension((int) (F_WIDTH * 0.28f), F_HEIGHT - downPane.getHeight()));
		leftPane.revalidate();
		
		leftButtonsPane.setPreferredSize(new Dimension(F_WIDTH / 16, F_HEIGHT - downPane.getHeight()));
		leftButtonsPane.revalidate();
	}

	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}	
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void windowStateChanged(WindowEvent e) {
		Out.Print(getClass(), 0, "Frames state was changed to: " + e.getNewState());
		
		if (e.getNewState() != 1) {frameIsOpen = true;
		} else {frameIsOpen = false;}
		
		leftPane.setSize(new Dimension(F_WIDTH / 4, F_HEIGHT - downPane.getHeight()));		
//		System.out.println("downPane`s size now = " + downPane.getSize());
//		System.out.println("leftPane`s size now = " + leftPane.getSize());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Registry.isPaused && !e.getActionCommand().equals("pauseClick")) {return;}
		
		basePane.remove(centerPane);

		switch (e.getActionCommand()) {
		case "backMenuClick": 
			if (!BackMenuPane.isVisible()) {BackMenuPane.setVisible(true);
			} else {BackMenuPane.setVisible(false);}
			break;
		case "pauseClick": Registry.isPaused = !Registry.isPaused;
			break;
		case "saveClick":
			break;
			
		case "showHeroPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.HERO);
			break;
		case "showDosugPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.DOSUG);
			break;
		case "showWorkPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.WORK);
			break;
		case "showLovePane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.LOVE);
			break;
		case "showReligionPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.RELIGION);
			break;
		case "showSportPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.SPORT);
			break;
		case "showGigienePane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.GIGIENE);
			break;
		case "showAdminPane":	
			centerPane = new MidBuilder(MidBuilder.TYPE.ADMIN);
			break;
		default: centerPane = new MidBuilder(MidBuilder.TYPE.NONE);
		}
		
		basePane.add(centerPane, BorderLayout.CENTER);
		basePane.revalidate();
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		switch (((JButton) e.getSource()).getActionCommand()) {
		case "showHeroPane": 	setStateText("Основное окно параметров и состояния персонажа.");
			break;
		case "showDosugPane":	setStateText("Досуг и развлечения.");
			break;
		case "showReligionPane":setStateText("Религия и медитация.");
			break;
		case "showAdminPane":	setStateText("Читы, опции, моды.");
			break;
		case "showLovePane":		setStateText("Отношения и любовь.");
			break;
		case "showWorkPane":		setStateText("Работа и доход.");
			break;
		case "showSportPane":		setStateText("Спорт и искуство.");
			break;
		case "showGigienePane":	setStateText("Окно гигиены персонажа.");
			break;
			
		case "backmenu":	setStateText("Открыть меню опций игры...");
			break;
		case "save":			setStateText("Нажми, чтобы сохранить игру!");
			break;
		case "pause":		setStateText("Поставить игру на паузу или снять с паузы.");
			break;
			default: 			setStateText("Some info text been here soon...");
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {setStateText("");}
}