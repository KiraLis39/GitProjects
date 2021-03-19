package registry;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import fox.adds.IOM;
import fox.builders.FoxFontBuilder;
import fox.games.FoxSpritesCombiner;


public class Registry {
	public static String name = "FoxyChat"; 
	public static String verse = "0.0.2.1"; 
	public static String autor = "KiraLis39";
	public static String mail = "AngelicaLis39@mail.ru";
	public static String company = "@Multiverse_39 group, 2021";
	
	public static FoxSpritesCombiner fsc = new FoxSpritesCombiner();
	
	public static Font fLabels = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 14, true);
	public static Font fMessage = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 16, true);
	public static Font fUsers = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 16, true);
	
	public static Font fMenuBar = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 12, false);
	public static Font fMenuBarBig = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 16, true);
	public static Font fBigSphere = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.SEGOE_UI_SYMBOL, 18, true);
	
	public static void render(Graphics2D g2D) {
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.RENDER_ON)) {
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			
	//		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		}
	} 
}

//Choice choice = new Choice();
//choice.addItem("First");
//choice.addItem("Second");
//choice.addItem("Third");

//	Полезные методы класса Choice:
//countItems() - считать количество пунктов в списке; 
//	getItem(int) - возвратить строку с определенным номером в списке; 
//	select(int) - выбрать строку с определенным номером; 
//	select(String) - выбрать определенную строку текста из списка. 
