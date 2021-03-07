package door;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.TimeZone;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResManager;
import gui.ChatFrame;
import media.Media;
import registry.IOMs;


public class MainClass {

	
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
		
		Out.setEnabled(true);
		Out.setLogsCoutAllow(3);
		
		IOM.setDebugOn(false);
		IOM.setDefaultEmptyString("NONE");
		
		ResManager.setDebugOn(false);
		
		checkFilesExists();
		buildIOM();
		loadResources();
		
//		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e){System.err.println("Couldn't get specified look and feel, for some reason.");}
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_UI_STYLE)) {
			try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
		    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		}
		
		String user = null;
		while (user == null || user.isBlank()) {
			user = getUserData();
			if (user == null || user.isBlank()) {
				JOptionPane.showConfirmDialog(null, 
						"<html>Вы не поняли.<br>Здесь необходимо указать своё имя.<br>Или хотя бы никнейм..", "Вы не поняли?", 
						JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE);
			}
		}
		new ChatFrame(user);
	}

	private static String getUserData() {
		return JOptionPane.showInputDialog(null, "Введите свой никнейм:", "Данные пользователя:", JOptionPane.QUESTION_MESSAGE);
	}

	private static void checkFilesExists() {
		File[] ownDirectories = new File[] {
				new File("./resources/images/"),
				new File("./resources/sounds/"),
				new File("./resources/user/")
		};
		for (int i = 0; i < ownDirectories.length; i++) {
			if (!ownDirectories[i].exists()) {
				ownDirectories[i].mkdirs();
			}
		}
		
		File configFile = new File("./resources/user/config.cfg");
		if (!configFile.exists()) {
			try {configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(22); // #22 - ошибка создания файла конфигурации пользователя при запуске программы
			}
		}
	}

	private static void buildIOM() {
		IOM.add(IOM.HEADERS.CONFIG, new File("./resources/user/config.cfg"));
		
//		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.RENDER_ON, value);
		
	}
	
	private static void loadResources() {
		try {
			ResManager.add("cur_0", new File("./resources/images/0.png"));
			ResManager.add("cur_1", new File("./resources/images/1.png"));		
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			File[] sounds = new File("./resources/sounds/").listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.isFile() && pathname.getName().endsWith(".mp3")) {return true;}
					return false;
				}
			});
			for (int i = 0; i < sounds.length; i++) {
				Media.addSound(sounds[i].getName().substring(0, sounds[i].getName().length() - 4), sounds[i]);				
			}
		} catch (Exception e) {e.printStackTrace();}
	}
}