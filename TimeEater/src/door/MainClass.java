package door;

import java.io.File;

import engine.FoxAudioProcessor;
import fox.adds.Out;
import fox.builders.ResourceManager;
import gui.GameFrame;


public class MainClass {
	private static boolean isShowLogo = false;
	
	public static void main(String[] args) {
		Out.setErrorLevel(Out.levels.INFO);
		Out.setLogsCoutAllow(3);
		ResourceManager.setDebugOn(false);
		
		buildIOM();
		
		if (isShowLogo) {
			FoxAudioProcessor.playSound("logo");
		}
		
		dirsChecker();
		
		loadResources();
		
		new GameFrame();
	}

	private static void buildIOM() {
		
	}

	private static void dirsChecker() {
		File checkFile;
		
		checkFile = new File("./res/pic/");
		if (!checkFile.exists()) {checkFile.mkdirs();}
		
		checkFile = new File("./res/aud/");
		if (!checkFile.exists()) {checkFile.mkdirs();}
	}

	private static void loadResources() {
		// pictures:
		try {
			ResourceManager.add("backg", new File("./res/pic/backg.png"));
			ResourceManager.add("grass", new File("./res/pic/grass.png"));
			ResourceManager.add("timePod", new File("./res/pic/timePod.png"));
			ResourceManager.add("stimePod", new File("./res/pic/superTimePod.png"));
			ResourceManager.add("player2", new File("./res/pic/player2.png"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// audio:
		try {
			FoxAudioProcessor.addMusic("theme", new File("./res/aud/theme.mp3"));
			
			FoxAudioProcessor.addSound("fall", new File("./res/aud/fall.mp3"));
			FoxAudioProcessor.addSound("logo", new File("./res/aud/logo.mp3"));
			FoxAudioProcessor.addSound("pick", new File("./res/aud/pick.mp3"));
			FoxAudioProcessor.addSound("start", new File("./res/aud/start.mp3"));
			FoxAudioProcessor.addSound("super", new File("./res/aud/super.mp3"));
			FoxAudioProcessor.addSound("timeout", new File("./res/aud/timeout.mp3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}