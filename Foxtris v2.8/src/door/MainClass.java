package door;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResourceManager;
import gui.StartMenuFrame;
import media.FoxAudioProcessor;


public class MainClass {
	private static JFrame logoFrame;
	private static float frameOpacity = 0.1f;
	private static Image im;
	
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice grDev = ge.getDefaultScreenDevice();
	private static GraphicsConfiguration grConf;
	private static Thread logoThread;
	
	
	public static void main(String[] args) {
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		grConf = grDev.getDefaultConfiguration();
		
		Out.Print(MainClass.class, 0, "Запуск программы.");
		loadUserData();
	
		ResourceManager.setDebugOn(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "ResourceManagerDebugLogEnabled"));
		IOM.setDebugOn(IOM.getBoolean(IOM.HEADERS.LAST_USER, "IOMDebugLogEnabled"));
		Out.setEnabled(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "LogEnabled(global)"));

		
		if (IOM.getBoolean(IOM.HEADERS.USER_SAVE, "showStartLogo")) {
			logoThread = new Thread(new Runnable() {
				@Override
				public void run() {
					showLogo();
					Out.Print(MainClass.class, 0, "Logo has ended.");
				}
			});
			logoThread.start();
		}
		
		loadResourses();
		controlsRegistration();

		if (logoThread != null) {while (logoThread.isAlive()) {try {logoThread.join();} catch (InterruptedException e) {e.printStackTrace();}}}
		
		Out.Print(MainClass.class, 0, "Launch the StartMenu...");
		new StartMenuFrame(grConf);
	}

	@SuppressWarnings("serial")
	private static void showLogo() {
		Out.Print(MainClass.class, 0, "Showing Logo...");
		FoxAudioProcessor.playSound("launchSound", 0.05D);
		
		logoFrame = new JFrame();
		logoFrame.setUndecorated(true);
		logoFrame.setBackground(new Color(0,0,0,0));
		logoFrame.setOpacity(frameOpacity);

		try {im = new ImageIcon("./resourse/pictures/logo0").getImage();
		} catch (Exception e) {
			Out.Print(MainClass.class, 3, "ERROR: Logo image not ready.");
			e.printStackTrace();
		}
			
		logoFrame.add(new JPanel() {
			{setPreferredSize(new Dimension(im.getWidth(null), im.getHeight(null)));}
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				
				logoFrame.setPreferredSize(new Dimension(im.getWidth(null), im.getHeight(null)));
				logoFrame.pack();
				logoFrame.setLocationRelativeTo(null);
				g2D.drawImage(im, 0, 0, im.getWidth(null), im.getHeight(null), null);				
			}
		});
		
		logoFrame.pack();
		logoFrame.setLocationRelativeTo(null);
		logoFrame.setVisible(true);
		
		while (frameOpacity < 1.0f) {
			Thread.yield();
			frameOpacity += 0.002f;
			if (frameOpacity > 1.0f) {frameOpacity = 1.0f;}
			logoFrame.setOpacity(frameOpacity);
		}
		
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		
		while (frameOpacity > 0.0f) {
			Thread.yield();
			frameOpacity -= 0.006f;
			if (frameOpacity < 0.0f) {frameOpacity = 0.0f;}
			logoFrame.setOpacity(frameOpacity);
		}
		
		logoFrame.dispose();
	}
	
	private static void loadUserData() {
		Out.Print(MainClass.class, 0, "Load IOM...");
		
		IOM.add(IOM.HEADERS.LAST_USER, new File("./user/data"));
		if (!IOM.getBoolean(IOM.HEADERS.LAST_USER, "lastUser")) {IOM.set(IOM.HEADERS.LAST_USER, "lastUser", "NonameUser");}
		
		IOM.add(IOM.HEADERS.USER_SAVE, new File("./user/" + IOM.getString(IOM.HEADERS.LAST_USER, "lastUser") + ".conf"));
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "gameTheme")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "gameTheme", "HOLO");}

		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "showStartLogo(global)"))	{IOM.set(IOM.HEADERS.USER_SAVE, "showStartLogo", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "LogEnabled(global)"))	 		{IOM.set(IOM.HEADERS.USER_SAVE, "LogEnabled(global)", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "OutDebugLogEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "OutDebugLogEnabled", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "OutErrorLevelName")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "OutErrorLevelName", Out.levels.FULL.name());}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "ResourceManagerDebugLogEnabled")) {IOM.set(IOM.HEADERS.USER_SAVE, "ResourceManagerDebugLogEnabled", "true");}	
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "IOMDebugLogEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "IOMDebugLogEnabled", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "nextFigureShow"))				{IOM.set(IOM.HEADERS.USER_SAVE, "nextFigureShow", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "specialBlocksEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "specialBlocksEnabled", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "AutoChangeMelody"))			{IOM.set(IOM.HEADERS.USER_SAVE, "AutoChangeMelody", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "hardcoreMode"))					{IOM.set(IOM.HEADERS.USER_SAVE, "hardcoreMode", "false");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "Lightcore"))							{IOM.set(IOM.HEADERS.USER_SAVE, "Lightcore", "false");}
		
		FoxAudioProcessor.setMusicEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "musicMute"));
		FoxAudioProcessor.setSoundEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "soundMute"));		
	}

	private static void controlsRegistration() {
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_LEFT")) 					{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT", KeyEvent.VK_LEFT);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_RIGHT")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT", KeyEvent.VK_RIGHT);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_DOWN")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN", KeyEvent.VK_DOWN);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_STUCK")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK", KeyEvent.VK_UP);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD", 0);}
		
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_ROTATE")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE", KeyEvent.VK_Z);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_PAUSE")) 					{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE", KeyEvent.VK_ESCAPE);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE"))				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE", KeyEvent.VK_BACK_QUOTE);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN"))			{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN", KeyEvent.VK_F);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD")) {IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD", 0);}
	}
	
	private static void loadResourses() {
		Out.Print(MainClass.class, 0, "Loading media resourses....");
		
		try {
			// pictures and icons (cashed):
			ResourceManager.add("hardcore", 			new File("./resourse/pictures/icons/hardcore.png"), false);
			ResourceManager.add("hardcore_off", 	new File("./resourse/pictures/icons/hardcore_off.png"), false);
			
			ResourceManager.add("spec", 			new File("./resourse/pictures/icons/spec.png"), false);
			ResourceManager.add("spec_off",	new File("./resourse/pictures/icons/spec_off.png"), false);
			
			ResourceManager.add("tips", 			new File("./resourse/pictures/icons/tips.png"), false);
			ResourceManager.add("tips_off", 	new File("./resourse/pictures/icons/tips_off.png"), false);
			
			ResourceManager.add("life", 			new File("./resourse/pictures/icons/life.png"), false);
			ResourceManager.add("bonus", 		new File("./resourse/pictures/icons/bonus.png"), false);
			
			ResourceManager.add("autoMusic",			new File("./resourse/pictures/icons/autoMusic.png"), false);
			ResourceManager.add("autoMusic_off", 	new File("./resourse/pictures/icons/autoMusic_off.png"), false);
			
			ResourceManager.add("lightcore", 			new File("./resourse/pictures/icons/lightcore.png"), false);
			ResourceManager.add("lightcore_off", 	new File("./resourse/pictures/icons/lightcore_off.png"), false);
			
			ResourceManager.add("gameIcon", 		new File("./resourse/pictures/gameIcon"), false);
			
			ResourceManager.add("backAbout", 	new File("./resourse/pictures/about/000"), false);
			ResourceManager.add("starsAbout", 	new File("./resourse/pictures/about/001"), false);
			ResourceManager.add("bAbout", 			new File("./resourse/pictures/about/002"), false);
			
			ResourceManager.add("buttonProto", 			new File("./resourse/pictures/buttonProto"), false);
			ResourceManager.add("buttonProtoOver", 	new File("./resourse/pictures/buttonProtoOver"), false);
			ResourceManager.add("buttonProtoPress", 	new File("./resourse/pictures/buttonProtoPress"), false);
			
			ResourceManager.add("victoryImage", 		new File("./resourse/pictures/victoryImage"), false);
			ResourceManager.add("gameoverImage", 	new File("./resourse/pictures/gameoverImage"), false);
			ResourceManager.add("pauseImage", 		new File("./resourse/pictures/pauseImage"), false);
			ResourceManager.add("finalWinImage", 	new File("./resourse/pictures/finalImage"), false);

			ResourceManager.add("switchOff", 			new File("./resourse/pictures/switchOff"), false);
			ResourceManager.add("switchOn", 			new File("./resourse/pictures/switchOn"), false);
			ResourceManager.add("stageLabel", 			new File("./resourse/pictures/stage"), false);
			
			ResourceManager.add("logoFoxList", 		new File("./resourse/pictures/sprites/logoFoxList"), false);
			ResourceManager.add("MBSL", 					new File("./resourse/pictures/sprites/MBSL"), false);
			ResourceManager.add("unibutton", 			new File("./resourse/pictures/sprites/unibutton"), false);
			ResourceManager.add("numbers", 				new File("./resourse/pictures/sprites/numbers"), false);

//			if (!TinySound.isInitialized()) {Media.init();}
			File[] musics = new File("./resourse/music").listFiles();
			
			for (File file : musics) {
				FoxAudioProcessor.addMusic(file.getName(), file);
			}
			
		} catch (Exception e) {
			Out.Print(MainClass.class, 3, "ERROR: with ResourseManager by cause: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		Out.Print(MainClass.class, 0, "Loading media resourses has accomplish!");
	}

	public static GraphicsConfiguration getGraphicConfig() {return grConf;}
	public static GraphicsDevice getGraphicDevice() {return grDev;}
}