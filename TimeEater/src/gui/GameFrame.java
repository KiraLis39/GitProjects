package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import engine.FoxAudioProcessor;
import entity.Player;
import entity.TimePod;
import fox.adds.InputAction;
import fox.builders.FoxFontBuilder;
import fox.builders.ResourceManager;
import registry.Registry;


@SuppressWarnings("serial")
public class GameFrame extends JFrame implements KeyListener {
	private Random r = new Random();
	private InputAction inAc = new InputAction();
	private static BufferedImage frameIcon, backgImage, grassImage, superPod;
	
	private static Map<Integer, TimePod> timePodsMap = new HashMap<Integer, TimePod>();
	private static Player player;
	
	private static Font f0 = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.CANDARA, 18, false);
	private static Font f1 = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 12, false);
	private static Font f2 = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 28, true);
	private static Font f3 = Registry.ffb.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 20, true);
	
	private static JProgressBar timeProgress;
	private static boolean isGameOver = false, isUpPressed = false, isDownPressed = false;
	private static int grassX = 0, podsCount = 6;
	private static float metersFlying = 0, lastmetersFlying = 0;
	
	private JLabel searchLabel;
	
	private TimePod sPod;
	
	
	public GameFrame() {
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		preLoad();
		
		try {
			frameIcon = ImageIO.read(new File("./res/pic/title.png"));
			if (frameIcon != null) {setIconImage(new ImageIcon(frameIcon).getImage());}
		} catch (IOException e) {/* IGNORE ICON ABSENT */}
		
		setTitle(Registry.gameName + " (" + Registry.gameVerse + ")");
		setPreferredSize(new Dimension(1024, 800));
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel basePanel = new JPanel(new BorderLayout(0, 6)) {
			{
				setOpaque(true);
				setBackground(Color.DARK_GRAY);
				setBorder(new EmptyBorder(6, 3, 3, 3));
				
				JPanel upPane = new JPanel(new BorderLayout()) {
					{
						setBackground(Color.BLACK);
						setBorder(new EmptyBorder(0, 6, 0, 0));
						
						searchLabel = new JLabel("Best score: " + lastmetersFlying) {
							{
								setFont(f1);
								setForeground(Color.GREEN);
							}
						};
						
						add(searchLabel, BorderLayout.WEST);
					}
				};
				
				JPanel midPane = new JPanel(new BorderLayout()) {
					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2D = (Graphics2D) g;
						
						render(g2D);
						drawBackground(g2D);
						
						if (!isGameOver) {
							drawPlayer(g2D, true);
							drawTimePods(g2D, true);
						} else {
							drawPlayer(g2D, false);
							drawTimePods(g2D, true);
							drawGameOver(g2D);
						}
						
						drawGUI(g2D);
						
						g2D.dispose();
					}

					private void drawGameOver(Graphics2D g2D) {
						g2D.setColor(Color.RED);
						g2D.setFont(f2);
						g2D.drawString("CONGRATULATIONS!!!", (int) (getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "CONGRATULATIONS!!!").getWidth() / 2), getHeight() / 2);
						g2D.drawString("YOU LOSE!", (int) (getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "YOU LOSE!").getWidth() / 2), getHeight() / 2 + 30);
					
						g2D.setColor(Color.BLACK);
						g2D.setFont(f3);
						g2D.drawString("SCORE:  " + metersFlying + "  miles.", 
								(int) (getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "SCORE:  " + metersFlying + "  miles.").getWidth() / 2), getHeight() / 2 + 60);
																		
						g2D.setFont(f0);
						g2D.drawString("(press ENTER to replay)", 
								(int) (getWidth() / 2 - Registry.ffb.getStringBounds(g2D, "(press ENTER to replay)").getWidth() / 2), getHeight() / 2 + 90);
					}

					private void render(Graphics2D g2D) {
						g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
						g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
						g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//						g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//						g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//						g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//						g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
						
//						g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));						
					}

					private void drawBackground(Graphics2D g2D) {
						g2D.drawImage(backgImage, 0, 0, getWidth(), getHeight(), this);						
						g2D.drawImage(grassImage, grassX, getHeight() - grassImage.getHeight(), grassImage.getWidth(), grassImage.getHeight(), this);
					}

					private void drawPlayer(Graphics2D g2d, boolean move) {
						if (isUpPressed) {
							if (timeProgress.getValue() > 0) {player.jump();}
						}
						if (isDownPressed) {
							if (timeProgress.getValue() > 0) {player.fall();}
						}
						player.draw(g2d, move);
					}
					
					private void drawTimePods(Graphics2D g2D, boolean move) {
						for (TimePod timePod : timePodsMap.values()) {
							if (timePod.isDestroyed()) {timePod.recreate();
							} else if (checkCollision(timePod)) {
								if (timeProgress.getValue() <= 990) {timeProgress.setValue(timeProgress.getValue() + timePod.getTimeVolume());}
								if (timePod.getName().startsWith("S")) {FoxAudioProcessor.playSound("super");
								} else {FoxAudioProcessor.playSound("pick");}
								timePod.recreate();
							} else {timePod.draw(g2D, move);}
						}
					}
					
					private boolean checkCollision(TimePod timePod) {
						if (player.getRectangle().intersects(timePod.getRectangle())) {return true;}
						return false;
					}

					private void drawGUI(Graphics2D g2D) {
						
					}
					

					{
						setBackground(Color.GRAY);
						setBorder(new EmptyBorder(3, 3, 3, 3));
						
						add(new JLabel("The game plane") {{setFont(f0); setHorizontalAlignment(0);}});
					}
				};

				JPanel timePane = new JPanel(new BorderLayout()) {
					{
						setBackground(Color.BLACK);
//						setBorder(new EmptyBorder(3, 3, 3, 3));
						
						JLabel timeLabel = new JLabel("TIME") {
							{
								setFont(f1);
								setForeground(Color.LIGHT_GRAY);
								setHorizontalAlignment(0);
							}
						};
						
						timeProgress = new JProgressBar(0, 1000) {
							{
								setPreferredSize(new Dimension(GameFrame.this.getWidth(), 60));
								setValue(1000);
							}
						};
						
						add(timeLabel, BorderLayout.NORTH);
						add(timeProgress, BorderLayout.CENTER);
					}
				};
				
				add(upPane, BorderLayout.NORTH);
				add(midPane, BorderLayout.CENTER);
				add(timePane, BorderLayout.SOUTH);
			}
		};
		
		add(basePanel);
		
		addKeyListener(this);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		setupInAc();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				FoxAudioProcessor.nextMusic();
				
				while(true) {
					repaint();
					
					if (!isGameOver) {
						if (player.getRectangle().y + player.getRectangle().height > getHeight() - 160) {
							isGameOver = true; 
							FoxAudioProcessor.playSound("fall");
						}
						
						if (timeProgress.getValue() > 0) {
							timeProgress.setValue(timeProgress.getValue() - 1);
							if (timeProgress.getValue() <= 1) {FoxAudioProcessor.playSound("timeout");}
						}						
						if (grassX <= getWidth() - grassImage.getWidth()) {grassX = 0;} else {grassX -= 8;}
						
						int rand = r.nextInt(20);
						if (rand == 0) {
							if (sPod == null) {sPod = new TimePod("STP#" + timePodsMap.size(), superPod, 50);}
							if (!sPod.isDestroyed()) {timePodsMap.put(timePodsMap.size() - 1, sPod);}
						}
						
						metersFlying += 0.25f;
					}
					
					try {Thread.sleep(32);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}).start();
	}

	private void setupInAc() {
		inAc.add("frame", this);
		inAc.set("frame", "replay", KeyEvent.VK_ENTER, 0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isGameOver) {preLoad();}
			}
		});
	}

	private void preLoad() {
		backgImage = ResourceManager.getBufferedImage("backg");
		grassImage = ResourceManager.getBufferedImage("grass");
		superPod = ResourceManager.getBufferedImage("stimePod");
		
		timePodsMap.clear();
		
		for (int i = 0; i < podsCount; i++) {
			timePodsMap.put(i, new TimePod("TP#" + i, null, 10));
		}
		
		player = new Player();
		
		if (metersFlying > lastmetersFlying) {
			lastmetersFlying = metersFlying;
			searchLabel.setText("Best score: " + lastmetersFlying + " miles.");
		}
		
		metersFlying = 0;
		if (timeProgress != null) {timeProgress.setValue(1000);}
		
		isGameOver = false;
		FoxAudioProcessor.playSound("start");
	}



	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {isUpPressed = true;}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {isDownPressed = true;}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {isUpPressed = false;}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {isDownPressed = false;}
	}
	
	public void keyTyped(KeyEvent e) {}
}