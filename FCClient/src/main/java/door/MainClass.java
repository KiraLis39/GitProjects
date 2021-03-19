package door;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.TimeZone;

import javax.swing.JOptionPane;

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
		
		new ChatFrame();
	}

	private static void checkFilesExists() {
		File[] ownDirectories = new File[] {
				new File("./resources/images/backgrounds/"),
				new File("./resources/images/DEFAULT/"),
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
		
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_IP, "localhost");
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.LAST_PORT, 13900);
		
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.RENDER_ON, true);
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_UI_STYLE, true);
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE, 2);
		
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.ANIMATION_ENABLED, true);
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUNDS_ENABLED, true);
		
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.USE_DIALOGPANE_OPACITY, true);		
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_USERS_PANEL, true);
		IOM.setIfNotExist(IOM.HEADERS.CONFIG, IOMs.CONFIG.SHOW_LEFT_PANEL, false);

		Media.setSoundEnabled(IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.SOUNDS_ENABLED));
	}
	
	private static void loadResources() {
		try {
			ResManager.add("requestImage", new File("./resources/images/requestImage.png"));
			
			ResManager.add("cur_0", new File("./resources/images/0.png"));
			ResManager.add("cur_1", new File("./resources/images/1.png"));
			
			ResManager.add("grass", new File("./resources/images/grass.png"));
			ResManager.add("userListEdge", new File("./resources/images/userListEdge.png"));
			ResManager.add("onlineImage", new File("./resources/images/onlineImage.png"));
			ResManager.add("offlineImage", new File("./resources/images/offlineImage.png"));
			ResManager.add("resetIPButtonImage", new File("./resources/images/resetIPButtonImage.png"));
			ResManager.add("sendButtonImage", new File("./resources/images/DEFAULT/btn.png"));
			
			ResManager.add("switchOffImage", new File("./resources/images/switchOff.png"));
			ResManager.add("switchOffoverImage", new File("./resources/images/switchOffover.png"));
			ResManager.add("switchOnImage", new File("./resources/images/switchOn.png"));
			ResManager.add("switchOnoverImage", new File("./resources/images/switchOnover.png"));
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(null, "<HTML>Произошла ошибка<br>при загрузке ресурсов!<br>", e.getMessage(), 
					JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
//			e.printStackTrace();
			System.exit(11);
		}
		
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