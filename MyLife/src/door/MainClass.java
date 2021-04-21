 package door;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;
import base.Media;
import base.Registry;
import enums.ENUMS;
import enums.ENUMS.IOMsave;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResourceManager;
import fox.games.FoxLogo;
import gui.GameFrame;
import panels.NewUserPane;


public class MainClass {
	public static Charset codec = StandardCharsets.UTF_8;
	private FoxLogo slr;	
	
	private boolean startLogoOn = false;
	private String userName;
	private Thread logoThread;
	
	
	public MainClass() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+4"));
		Out.Print(getClass(), 0, "Кодировка системы: " + Charset.defaultCharset());
		Out.Print(getClass(), 0, "Кодировка программы: " + codec);

		IOM.setDebugOn(true);
		ResourceManager.setDebugOn(false);
		
		buildIOM();
		loadAudio();
		
		if (startLogoOn) {
			logoThread = new Thread(new Runnable() {
				@Override
				public void run() {
					startLogoRun();
				}
			});
			
		}
		
		loadResources();		
		
		if (logoThread != null) {
			try {logoThread.join();} catch (Exception e) {/* IGNORE JOINING */}
			Media.stopMusic();
		}
		
		if (IOM.getString(IOM.HEADERS.LAST_USER, "LAST").equals("none")) {
			Exit.exit(2);
		}
		
		new GameFrame(userName, IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX));
	}
	
	private void buildIOM() {
		IOM.add(IOM.HEADERS.LAST_USER, new File(Registry.lastUserFile));
		
		if (IOM.getString(IOM.HEADERS.LAST_USER, "LAST").equals("none")) {
			userName = new NewUserPane().getNewUser();
			IOM.set(IOM.HEADERS.LAST_USER, "LAST", userName);
		} else {userName = IOM.getString(IOM.HEADERS.LAST_USER, "LAST");}
		
		IOM.add(IOM.HEADERS.USER_SAVE, new File(Registry.userSavePath + userName.split(":")[0] + ".save"));
		IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX, userName.split(":")[0].equals("true") ? "MALE" : "FEMA");
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.AUTOSAVING_ON).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.AUTOSAVING_ON, "true");}
		
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.CASH).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.CASH, "0");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.AGE).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.AGE, "18");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.HEALTH).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.HEALTH, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.ENERGY).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.ENERGY, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.WEIGHT).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.WEIGHT, "60");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.MOOD).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.MOOD, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.DOSUG).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.DOSUG, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.ROMANTIC).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.ROMANTIC, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.GIGENE).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.GIGENE, "100");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.STRENGE).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.STRENGE, "30");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.HAPPYNESE).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.HAPPYNESE, "50");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.HUNGRY).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.HUNGRY, "100");}
		
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.INGAME_TIME).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.INGAME_TIME, "1");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_EXP).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_EXP, "0");}
		if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_LEVEL).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_LEVEL, "1");}
	}

	private void loadAudio() {
		Media.setMusicVolume(1f);
		
		try {
			Media.addMusic("musStartScreen", new File(Registry.musPath + "musStartScreen.wav"));
			
			Media.addMusic("musMainMenu", new File(Registry.musPath + "musEndAndDie.wav"));
			Media.addMusic("musThemeStreet", new File(Registry.musPath + "musThemeStreet.wav"));
			Media.addMusic("musOtherTheme", new File(Registry.musPath + "musOtherTheme.wav"));
			Media.addMusic("musSpringSleep", new File(Registry.musPath + "musSpringSleep.wav"));
			
			Media.addSound("soundButtonOver", new File(Registry.soundPath + "soundButtonOver.wav"));
			Media.addSound("soundButtonPress", new File(Registry.soundPath + "soundButtonPress.wav"));
		} catch (Exception e) {e.printStackTrace();}
		
		Media.setMusicEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.MUSIC_MUTE));
		Media.setSoundEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.SOUND_MUTE));
	}

	private void loadResources() {
		try {
			ResourceManager.add("exp", 								new File(Registry.picPath + "icons/exp.png"), true);
			ResourceManager.add("dieIcon", 							new File(Registry.picPath + "icons/dieIcon.png"), true);
			ResourceManager.add("FrameIconPicture", 		new File(Registry.picPath + "icons/frame.png"), true);
			ResourceManager.add("buttonWarnIcon32", 		new File(Registry.picPath + "icons/s1_32x32.png"), true);
			ResourceManager.add("buttonErrorIcon32", 		new File(Registry.picPath + "icons/s2_32x32.png"), true);
			ResourceManager.add("buttonCompliteIcon32",	new File(Registry.picPath + "icons/s3_32x32.png"), true);
			
			ResourceManager.add("waitingFrameImage", 	new File(Registry.picPath + "waitingFrameImage.png"), true);
			
			ResourceManager.add("parametersPic01", 		new File(Registry.picPath + "param01.png"), true);
			ResourceManager.add("parametersPic02", 		new File(Registry.picPath + "param02.png"), true);
			ResourceManager.add("parametersPic03", 		new File(Registry.picPath + "param03.png"), true);
			
			ResourceManager.add("aboutFon", 					new File(Registry.picPath + "aboutFon.png"), true);

			
			ResourceManager.add("needsPic01", 		new File(Registry.picPath + "needs/needs01.png"), true);
			ResourceManager.add("needsPic02", 		new File(Registry.picPath + "needs/needs02.png"), true);
			ResourceManager.add("needsPic03", 		new File(Registry.picPath + "needs/needs03.png"), true);
			ResourceManager.add("needsPic04", 		new File(Registry.picPath + "needs/needs04.png"), true);

			ResourceManager.add("needsPic05", 		new File(Registry.picPath + "needs/needs05.png"), true);
			ResourceManager.add("needsPic06", 		new File(Registry.picPath + "needs/needs06.png"), true);
			ResourceManager.add("needsPic07", 		new File(Registry.picPath + "needs/needs07.png"), true);
			ResourceManager.add("needsPic08", 		new File(Registry.picPath + "needs/needs08.png"), true);
			
			ResourceManager.add("needsPic09", 		new File(Registry.picPath + "needs/needs09.png"), true);
			ResourceManager.add("needsPic10", 		new File(Registry.picPath + "needs/needs10.png"), true);
			ResourceManager.add("needsPic11", 		new File(Registry.picPath + "needs/needs11.png"), true);
			ResourceManager.add("needsPic12", 		new File(Registry.picPath + "needs/needs12.png"), true);
			
			
			ResourceManager.add("girlsPic01", 			new File(Registry.picPath + "girls/girls01.png"), true);
			ResourceManager.add("girlsPic02", 			new File(Registry.picPath + "girls/girls02.png"), true);
			ResourceManager.add("girlsPic03", 			new File(Registry.picPath + "girls/girls03.png"), true);
			ResourceManager.add("girlsPic04", 		new File(Registry.picPath + "girls/girls04.png"), true);
			
			ResourceManager.add("girlsPic05", 			new File(Registry.picPath + "girls/girls05.png"), true);
			ResourceManager.add("girlsPic06", 		new File(Registry.picPath + "girls/girls06.png"), true);
			ResourceManager.add("girlsPic07", 			new File(Registry.picPath + "girls/girls07.png"), true);
			ResourceManager.add("girlsPic08", 		new File(Registry.picPath + "girls/girls08.png"), true);
			
			ResourceManager.add("girlsPic09", 		new File(Registry.picPath + "girls/girls09.png"), true);
			ResourceManager.add("girlsPic10", 			new File(Registry.picPath + "girls/girls10.png"), true);
			ResourceManager.add("girlsPic11", 			new File(Registry.picPath + "girls/girls11.png"), true);
			ResourceManager.add("girlsPic12", 			new File(Registry.picPath + "girls/girls12.png"), true);
			
			
			ResourceManager.add("boysPic01", 			new File(Registry.picPath + "boys/boys01.png"), true);
			ResourceManager.add("boysPic02", 		new File(Registry.picPath + "boys/boys02.png"), true);
			ResourceManager.add("boysPic03", 		new File(Registry.picPath + "boys/boys03.png"), true);
			ResourceManager.add("boysPic04", 		new File(Registry.picPath + "boys/boys04.png"), true);
			
			ResourceManager.add("boysPic05", 		new File(Registry.picPath + "boys/boys05.png"), true);
			ResourceManager.add("boysPic06", 		new File(Registry.picPath + "boys/boys06.png"), true);
			ResourceManager.add("boysPic07", 		new File(Registry.picPath + "boys/boys07.png"), true);
			ResourceManager.add("boysPic08", 		new File(Registry.picPath + "boys/boys08.png"), true);
			
			ResourceManager.add("boysPic09", 		new File(Registry.picPath + "boys/boys09.png"), true);
			ResourceManager.add("boysPic10", 			new File(Registry.picPath + "boys/boys10.png"), true);
			ResourceManager.add("boysPic11", 			new File(Registry.picPath + "boys/boys11.png"), true);
			ResourceManager.add("boysPic12", 			new File(Registry.picPath + "boys/boys12.png"), true);
			
			
			ResourceManager.add("worksPic01", 		new File(Registry.picPath + "works/works01.png"), true);
			ResourceManager.add("worksPic02", 		new File(Registry.picPath + "works/works02.png"), true);
			ResourceManager.add("worksPic03", 		new File(Registry.picPath + "works/works03.png"), true);
			ResourceManager.add("worksPic04", 		new File(Registry.picPath + "works/works04.png"), true);
			
			ResourceManager.add("worksPic05", 		new File(Registry.picPath + "works/works05.png"), true);
			ResourceManager.add("worksPic06", 		new File(Registry.picPath + "works/works06.png"), true);
			ResourceManager.add("worksPic07", 		new File(Registry.picPath + "works/works07.png"), true);
			ResourceManager.add("worksPic08", 		new File(Registry.picPath + "works/works08.png"), true);
			
			ResourceManager.add("worksPic09", 		new File(Registry.picPath + "works/works09.png"), true);
			ResourceManager.add("worksPic10", 		new File(Registry.picPath + "works/works10.png"), true);
			ResourceManager.add("worksPic11", 		new File(Registry.picPath + "works/works11.png"), true);
			ResourceManager.add("worksPic12", 		new File(Registry.picPath + "works/works12.png"), true);
			
			
			ResourceManager.add("gigiensPic01", 		new File(Registry.picPath + "gigiens/gigiens0.png"), true);
			ResourceManager.add("gigiensPic02", 	new File(Registry.picPath + "gigiens/gigiens1.png"), true);
			ResourceManager.add("gigiensPic03", 	new File(Registry.picPath + "gigiens/gigiens2.png"), true);
			ResourceManager.add("gigiensPic04", 	new File(Registry.picPath + "gigiens/gigiens3.png"), true);
			
			ResourceManager.add("gigiensPic05", 	new File(Registry.picPath + "gigiens/gigiens4.png"), true);
			ResourceManager.add("gigiensPic06", 	new File(Registry.picPath + "gigiens/gigiens5.png"), true);
			ResourceManager.add("gigiensPic07", 	new File(Registry.picPath + "gigiens/gigiens6.png"), true);
			ResourceManager.add("gigiensPic08", 	new File(Registry.picPath + "gigiens/gigiens7.png"), true);
			
			ResourceManager.add("gamesPic01", 		new File(Registry.picPath + "games/games01.png"), true);
			ResourceManager.add("gamesPic02", 		new File(Registry.picPath + "games/games02.png"), true);
			ResourceManager.add("gamesPic03", 		new File(Registry.picPath + "games/games03.png"), true);
			ResourceManager.add("gamesPic04", 		new File(Registry.picPath + "games/games04.png"), true);
			
			
			ResourceManager.add("gamesPic05", 		new File(Registry.picPath + "games/games05.png"), true);
			ResourceManager.add("gamesPic06", 		new File(Registry.picPath + "games/games06.png"), true);
			ResourceManager.add("gamesPic07", 		new File(Registry.picPath + "games/games07.png"), true);
			ResourceManager.add("gamesPic08", 		new File(Registry.picPath + "games/games08.png"), true);
			
			ResourceManager.add("gamesPic09", 		new File(Registry.picPath + "games/games09.png"), true);
			ResourceManager.add("gamesPic10", 		new File(Registry.picPath + "games/games10.png"), true);
			ResourceManager.add("gamesPic11", 		new File(Registry.picPath + "games/games11.png"), true);
			ResourceManager.add("gamesPic12", 		new File(Registry.picPath + "games/games12.png"), true);
			
			
			ResourceManager.add("buttonStartMenu01", 	new File(Registry.picPath + "buttons/bsm01.png"), true);
			ResourceManager.add("buttonStartMenu02", 	new File(Registry.picPath + "buttons/bsm02.png"), true);
			ResourceManager.add("buttonStartMenu03", 	new File(Registry.picPath + "buttons/bsm03.png"), true);

			ResourceManager.add("heroLogo", 			new File(Registry.picPath + "hLogo.png"), true);
			ResourceManager.add("tipFon", 				new File(Registry.picPath + "tipFon.png"), true);
			ResourceManager.add("startMenuFon", 	new File(Registry.picPath + "startMenuFon.png"), true);			
			ResourceManager.add("baseGameFon", 	new File(Registry.picPath + "baseGameFon.jpg"), true);

			ResourceManager.add("button01Icon", 	new File(Registry.picPath + "buttons/t1.png"), true);
			ResourceManager.add("button02Icon", 	new File(Registry.picPath + "buttons/t2.png"), true);
			ResourceManager.add("button03Icon", 	new File(Registry.picPath + "buttons/t3.png"), true);
			ResourceManager.add("button04Icon", 	new File(Registry.picPath + "buttons/t4.png"), true);
			ResourceManager.add("button05Icon", 	new File(Registry.picPath + "buttons/t5.png"), true);
			ResourceManager.add("button06Icon", 	new File(Registry.picPath + "buttons/t6.png"), true);
			ResourceManager.add("button07Icon", 	new File(Registry.picPath + "buttons/t7.png"), true);
			ResourceManager.add("button08Icon", 	new File(Registry.picPath + "buttons/t8.png"), true);
			
			
			ResourceManager.add("levelChoiseSimpleButton", 					new File(Registry.picPath + "icons/simple_norm.png"), true);
			ResourceManager.add("levelChoiseSimpleButton_rollover", 	new File(Registry.picPath + "icons/simple_roll.png"), true);
			ResourceManager.add("levelChoiseSimpleButton_pressed", 	new File(Registry.picPath + "icons/simple_press.png"), true);
			
			ResourceManager.add("levelChoiseMiddleButton", 					new File(Registry.picPath + "icons/middle_norm.png"), true);
			ResourceManager.add("levelChoiseMiddleButton_rollover", 	new File(Registry.picPath + "icons/middle_roll.png"), true);
			ResourceManager.add("levelChoiseMiddleButton_pressed", 	new File(Registry.picPath + "icons/middle_press.png"), true);
			
			ResourceManager.add("levelChoiseHardButton", 						new File(Registry.picPath + "icons/hard_norm.png"), true);
			ResourceManager.add("levelChoiseHardButton_rollover", 		new File(Registry.picPath + "icons/hard_roll.png"), true);
			ResourceManager.add("levelChoiseHardButton_pressed", 		new File(Registry.picPath + "icons/hard_press.png"), true);
			
			ResourceManager.add("levelChoiseExtreamButton", 					new File(Registry.picPath + "icons/extream_norm.png"), true);
			ResourceManager.add("levelChoiseExtreamButton_rollover", 	new File(Registry.picPath + "icons/extream_roll.png"), true);
			ResourceManager.add("levelChoiseExtreamButton_pressed", 	new File(Registry.picPath + "icons/extream_press.png"), true);
			
			ResourceManager.add("HungryBImage", 	new File(Registry.picPath + 	"other/0.png"), true);
			ResourceManager.add("EnergyBImage", 	new File(Registry.picPath + 	"other/1.png"), true);
			ResourceManager.add("DosugBImage", 	new File(Registry.picPath + 	"other/2.png"), true);
			ResourceManager.add("GigeneBImage", 	new File(Registry.picPath + 	"other/3.png"), true);
			ResourceManager.add("RomanticBImage", 	new File(Registry.picPath +"other/romantic.png"), true);
			ResourceManager.add("SportBImage", 	new File(Registry.picPath + 	"other/sport.png"), true);
			ResourceManager.add("MoodBImage", 	new File(Registry.picPath + 	"other/mood.png"), true);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void startLogoRun() {
		try {
			ResourceManager.add("tex01", 	new File(Registry.picPath + "startlogo/tex0.png"), true);
			ResourceManager.add("tex02", 	new File(Registry.picPath + "startlogo/tex1.png"), true);
			
			slr = new FoxLogo(new BufferedImage[] {ResourceManager.getBufferedImage("tex01"), ResourceManager.getBufferedImage("tex02")});
			Media.nextMusic();			
			slr.start();
		} catch (Exception e) {e.printStackTrace();}
	}

	public static void main(String[] args) {new MainClass();}
}

//g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
//g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.75f));