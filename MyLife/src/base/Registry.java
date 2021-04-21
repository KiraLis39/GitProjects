package base;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import fox.adds.InputAction;
import fox.builders.FoxFontBuilder;


public class Registry {
	private static int versionPrima = 1;
	private static int versionSecond = 0;
	private static int versionModific = 0;
	
	public static String titleAndVers 	= "MyLife v" + versionPrima + "." + versionSecond + "." + versionModific;
	
	public static FoxFontBuilder ffb = new FoxFontBuilder();
	public static InputAction inAc = new InputAction();
	
	public static Boolean isPaused = false;
	
	public static int[] experience = new int[] {
			100, 250, 500, 750, 1000
	};
	
	public static String picPath 			= "./resources/pictures/";
	public static String musPath 			= "./resources/audio/music/";
	public static String soundPath 		= "./resources/audio/sound/";
	
	public static String userSavePath 	= "./users/";
	
	public static String lastUserFile 	= "./users/last.dat";
	
	public static void render(Graphics2D g2D) {
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		
//		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
	}
}