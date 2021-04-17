package games;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;

import builders.FoxFontBuilder;
import builders.FoxFontBuilder.FONT;


public class FoxTipsEngine {
	public static enum iconPosition {LEFT, CENTER}
	private iconPosition ipos = iconPosition.LEFT;
	
	public static enum tipStyle {TRANSPARENT, BEAUTY, NONE}
	private tipStyle style = tipStyle.TRANSPARENT;
	
	private Container parentFrame;
	private RenderingHints renderingHints;
	private ArrayList<String> messageLines;
	private ArrayDeque<BufferedImage> tipImageDequeArray = new ArrayDeque<BufferedImage>();
	
	private Boolean complite = false;
	private char[] messageChars;
	private long timeWas = 0L;

	private static long lifeTime = 5000L;
	private float wMultiplier, hMultiplier, heightOfMessage, heightOfTip, biggestStringWidth;
	private float iconWidth = 64f, iconHeight = 64f, maxMessageWidth, fontSizeMin = 14, fontSizeMax;
	private int diviner = 1;
	
	private Point2D icoSRCpoint, labelSRCpoint, mesSRCpoint	;
	
	private String mes = "", title = "";
	private Font mesFont = FoxFontBuilder.setFoxFont(2, 22, false);
	private Font titleFont = FoxFontBuilder.setFoxFont(2, 20, true);
	private Color mesColor = Color.WHITE, titleColor = Color.YELLOW;
	
	private BufferedImage tipBackBufferImage, tipPicPlaceBufferImage, resultBufferImage, getNextBufferImage, getPreviousBufferImage, icoBuff, backBuff;
	
	
	public FoxTipsEngine(Container parent) {this(parent, lifeTime);}
	
	public FoxTipsEngine(Container parent, long lifeTimeMsec) {
		resultBufferImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		lifeTime = lifeTimeMsec;
		parentFrame = parent;
		
		wMultiplier = parentFrame.getWidth() / 100f;
		hMultiplier = parentFrame.getHeight() / 100f;
		if (wMultiplier <= 0) {wMultiplier = 8f;}
		if (hMultiplier <= 0) {hMultiplier = 6f;}		
		
		renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		renderingHints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
		renderingHints.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		renderingHints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
//		renderingHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
//		renderingHints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
//    renderingHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
//		renderingHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
//		renderingHints.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE));
	}
		
	public void createTip(BufferedImage backBase, BufferedImage imageIcon, String tipTitle, String message) {
		createTip(backBase, 
				imageIcon, null, iconWidth, iconHeight, 
				tipTitle, 	null, titleFont, Color.YELLOW, 
				message, 	null, backBase.getWidth() / 100f * 65f, mesFont, 	Color.WHITE, 
				style);
	}
	public void createTip(BufferedImage backBase, BufferedImage imageIcon, String tipTitle, String message, float maxMessageWidth, tipStyle tStyle) {
		createTip(backBase, 
				imageIcon, null, iconWidth, iconHeight, 
				tipTitle, 	null, titleFont, Color.YELLOW, 
				message, 	null, maxMessageWidth, mesFont, 	Color.WHITE, 
				tStyle);
	}
	public void createTip(BufferedImage backBase, BufferedImage imageIcon, Point2D iconSRC, String tipTitle, Point2D labelSRC, String message, float maxMessageWidth, tipStyle tStyle) {
		createTip(backBase, 
				imageIcon, iconSRC, iconWidth, iconHeight, 
				tipTitle, 	labelSRC, titleFont, Color.YELLOW, 
				message, 	null, maxMessageWidth, mesFont, Color.WHITE, 
				tStyle);
	}
	
	public void createTip(
			BufferedImage backBaseBuff, 
			BufferedImage iconBuff, Point2D iconSRC, float icoW, float icoH, 
			String tipTitle, Point2D labelSRC, Font labelFont, Color labelColor, 
			String message, Point2D mesSRC, 	float maxMesWidth, Font messageFont, Color messageColor, 
			tipStyle tStyle) {
		if (backBaseBuff == null || iconBuff == null) {throw new RuntimeException("The BufferedImage`s 'backBaseBuff' and 'icon' can`t be NULL");}
		if (tipTitle == null && labelSRCpoint == null) {throw new RuntimeException("'tipTitle' and 'labelSRCpoint' can`t be both = NULL");}
		messageLines = new ArrayList<String>(4);
		
		try {tipPicPlaceBufferImage = new BufferedImage((int) Math.floor(icoW) + 1, (int) Math.floor(icoH) + 1, BufferedImage.TYPE_INT_ARGB);
		} catch (Exception e1) {e1.printStackTrace();}
		
		try {tipBackBufferImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
		} catch (Exception e) {e.printStackTrace();}
		
		this.icoBuff = iconBuff;
		this.backBuff = backBaseBuff;
		icoSRCpoint = iconSRC;
		iconWidth = icoW;
		iconHeight = icoH;
		title = tipTitle;
		labelSRCpoint = labelSRC;
		if (labelFont != null) {titleFont = labelFont;}
		if (labelColor != null) {titleColor = labelColor;}
		mes = message;
		mesSRCpoint = mesSRC;
		if (messageFont != null) {mesFont = messageFont;}
		if (messageColor != null) {mesColor = messageColor;}
		if (tStyle != null) {style = tStyle;}
		
		if (mesSRCpoint == null) 		{mesSRCpoint	= new Point2D.Float(
				backBuff.getWidth() / 100f * 30f, 
				backBuff.getHeight() / 100f * 25f);
		}
		
		reCalculateIconAndLabelPosition();		
		
		maxMessageWidth = maxMesWidth;
		if (maxMessageWidth < backBuff.getWidth() / 3f) {maxMessageWidth = backBuff.getWidth() / 3f;}
		if (maxMessageWidth + mesSRCpoint.getX() + 20f > backBuff.getWidth()) {maxMessageWidth = (float) (backBuff.getWidth() - (mesSRCpoint.getX() + 20f));}
		
		Graphics2D g2D = tipPicPlaceBufferImage.createGraphics();
		g2D.setRenderingHints(renderingHints);
		g2D.drawImage(icoBuff, 0, 0, tipPicPlaceBufferImage.getWidth(), tipPicPlaceBufferImage.getHeight(), null);
		g2D.dispose();
		
		drawDetailed(0);
		
		tipImageDequeArray.addLast(tipBackBufferImage);
	}
	
	private void reCalculateIconAndLabelPosition() {
		float tFl = (float) (tipBackBufferImage.getWidth() / 2 - FoxFontBuilder.getStringBounds(tipBackBufferImage.createGraphics(), title).getWidth());
		if (labelSRCpoint == null) 	{labelSRCpoint 	= new Point2D.Float(
			tFl, 
			tipBackBufferImage.getHeight() / 100f * 10f);
		}
		
		if (icoSRCpoint == null) 		{icoSRCpoint 	= new Point2D.Float(
				tipBackBufferImage.getWidth() / 100f * 3.39f, 
				tipBackBufferImage.getHeight() / 100f * 12.5f);
		}
	}

	private synchronized void drawDetailed(float heightIncrease) {
		Graphics2D g2D;
		
		if (ipos == iconPosition.LEFT) {
			//background base:			
			tipBackBufferImage = new BufferedImage(tipBackBufferImage.getWidth(), (int) (tipBackBufferImage.getHeight() + heightIncrease), BufferedImage.TYPE_INT_ARGB);
//			System.out.println("Current 'width x height' of the Tip: " + tipBackBufferImage.getWidth() + "x" + tipBackBufferImage.getHeight());
			
			reCalculateIconAndLabelPosition();

			g2D = tipBackBufferImage.createGraphics();
			g2D.setRenderingHints(renderingHints);
			if (style.equals(tipStyle.BEAUTY)) {
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.72f));
			}
			
			if (style.equals(tipStyle.TRANSPARENT)) {
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
			}
			
			g2D.drawImage(backBuff, 0, 0, tipBackBufferImage.getWidth(), tipBackBufferImage.getHeight(), null);
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
						
			if (drawMes(g2D)) {//the message:
				drawTitle(g2D);		//title of tip:
				drawIcon(g2D);		//icon of tip:
			}
		} else {
			g2D = tipBackBufferImage.createGraphics();
			g2D.setRenderingHints(renderingHints);
			g2D.drawImage(icoBuff, 3, 2, tipBackBufferImage.getWidth() - 6, tipBackBufferImage.getHeight() - 4, null);
		}
		
		try {g2D.dispose();} catch (Exception e) {/* Ignore */}
	}
	
	private boolean drawMes(Graphics2D g2D) {
		if (mes.equals("")) {return true;}
		
		heightOfMessage = 0;
		heightOfTip = tipBackBufferImage.getHeight();
		
		textTuner(g2D);
		
		float heightCorector = 0.85f;
		float stringHeight = (float) FoxFontBuilder.getStringBounds(g2D, messageLines.get(0)).getHeight();
		float heightOfMessage = stringHeight * messageLines.size() + 6f;
		
		if (heightOfMessage + mesSRCpoint.getY() >= heightOfTip * heightCorector) {
			g2D.dispose();
			
//			System.out.println(
//					"drawMes: Высота сообщения (" + 
//					(heightOfMessage + mesSRCpoint.getY()) + ") больше " + (int) (heightCorector * 100f) + "% высоты подсказки (" + 
//					(heightOfTip * heightCorector) + "). Rebuilding...");
			
			float mod = (float) (heightOfMessage + mesSRCpoint.getY() - heightOfTip * heightCorector + 1f);
			drawDetailed(mod);
			return false;
		}
		
		
//		System.out.println("drawMes: ПРОЙДЕНО КОЛЬЦО, РИСУЕМ ТЕКСТ СООБЩЕНИЯ...");
		for (int i = 0; i < messageLines.size(); i++) {
			float w 	= (float) mesSRCpoint.getX();
			float h 	= (float) (mesSRCpoint.getY() + (stringHeight * i) + 10f);
			
			g2D.setFont(mesFont);
			g2D.setColor(new Color(0.2f, 0.2f, 0.2f, 0.75f));
			g2D.drawString(messageLines.get(i), w, h);
			
			g2D.setColor(mesColor);
			g2D.drawString(messageLines.get(i), w + 1f, h - 1f);
		}
		
//		System.out.println("drawMes: ОТРИСОВКА ТЕКСТА СООБЩЕНИЯ НАКОНЕЦ ЗАВЕРШЕНА!");
//		System.out.println();
		return true;
	}
	
	private void textTuner(Graphics2D g2D) {
		messageChars = mes.toCharArray(); //массив символов сообщения.
		biggestStringWidth = 0;
		complite = false;
		diviner = 1;
		
		while (!complite) {
			if (diviner > messageChars.length / 10) {throw new RuntimeException("diviner is biggest than " + (messageChars.length / 10) + " ?! Oh no...");}
			complite = true;
			
			messageLines = new ArrayList<String>();
			fontSizeMax = (100f / parentFrame.getWidth() + 100f / parentFrame.getHeight()) * 100f - 12f;

//			System.out.println("\ntextTuner: fontSizeMax = " + fontSizeMax + ".\nGo next to the messageLinesFiller()...");
			// здесь мы делим сообщение на возрастающее количество строк, вычисляя длину самой длинной из них:
			messageLinesFiller(g2D);
			
//			System.out.println("textTuner: a new fontSize = " + fontSizeMax + ".\nGo next to the messageBuilder()...");
			messageBuilder(g2D);
		}
		
//		System.out.println("textTuner: Complite and continue...");
	}
	
	private void messageLinesFiller(Graphics2D g2D) {
		int lineLenght = messageChars.length / diviner;
		int spaceSeaker = 0, backCorrector = 0;
		
		for (int i = 0; i < diviner; i++) {
			String tmp = "";
			
			for (int j = 0 + lineLenght * i + backCorrector; j < lineLenght * (i + 1); j++) {
				if (messageChars.length > j) {
					tmp += messageChars[j];
					spaceSeaker = j;
				}
			}
			
			backCorrector = 0;
			while (messageChars.length > spaceSeaker + 1 && messageChars[spaceSeaker] != ' ') {
				spaceSeaker++;
				backCorrector++;
				tmp += messageChars[spaceSeaker];
			}
			
			messageLines.add(i, tmp);
		}
		
		Rectangle2D bounds;
		for (int i = 0; i < messageLines.size(); i++) {
			bounds = FoxFontBuilder.getStringBounds(g2D, messageLines.get(0));
			if (bounds.getWidth() > biggestStringWidth) {biggestStringWidth = (float) bounds.getWidth();}
		}
		
//		System.out.println("mesLineFill: The message\n'" + mes + "'\nwas cutted on " + diviner + " strokes.\nWidth biggestStringWidth = " + biggestStringWidth);
	}

	private void messageBuilder(Graphics2D g2D) {
		Rectangle2D bounds;
		
		while (biggestStringWidth > maxMessageWidth) {
			fontSizeMax -= 1f;
			
			if (fontSizeMax < fontSizeMin) {
//				System.out.println("\nmessageBuilder: fontSizeMax (" + fontSizeMax + ") < fontSizeMin (" + fontSizeMin + "). Return...");
				diviner++;
				complite = false;
				return;
			}
			
			mesFont = FoxFontBuilder.setFoxFont(FONT.ARIAL_NARROW, fontSizeMax, false);
			g2D.setFont(mesFont);
			
			biggestStringWidth = 0;
			for (int i = 0; i < messageLines.size(); i++) {
				bounds = FoxFontBuilder.getStringBounds(g2D, messageLines.get(0));
				if (bounds.getWidth() > biggestStringWidth) {biggestStringWidth = (float) bounds.getWidth();}
			}
		}
		
//		System.out.println();
//		System.out.println("messageBuilder: Complite and continue...");
	}
	
	private void drawTitle(Graphics2D g2D) {
//		System.out.println("drawTitle: Enter into drawTitle(Graphics2D g2D);");
		if (title.equals("")) {return;}
		
		g2D.setFont(FoxFontBuilder.setFoxFont(FONT.BAHNSCHRIFT, (14f + heightOfMessage / 30f), true));
		
		float	w = (float) labelSRCpoint.getX();
		float h = (float) labelSRCpoint.getY();

		g2D.setColor(new Color(0.25f, 0.25f, 0.0f, 0.75f));
		g2D.drawString(title, w - 1, h + 1);
		
		g2D.setColor(titleColor);
		g2D.drawString(title, w, h);
	}
	
	private void drawIcon(Graphics2D g2D) {
		if (icoBuff == null || tipPicPlaceBufferImage == null) {return;}
		g2D.drawImage(tipPicPlaceBufferImage, (int) Math.floor(icoSRCpoint.getX()), (int) Math.floor(icoSRCpoint.getY()), Math.round(iconWidth), Math.round(iconHeight), null);
	}

	
	public synchronized BufferedImage getPaintBuffer() {
		if (tipImageDequeArray.isEmpty()) {
			if (System.currentTimeMillis() - timeWas > 1000 && System.currentTimeMillis() - timeWas > lifeTime) {
				resultBufferImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				return null;
			} else {return resultBufferImage;}
		}

		while (tipImageDequeArray.size() > 0) {
			getNextBufferImage = tipImageDequeArray.pollLast();
			getPreviousBufferImage = resultBufferImage;
			
			resultBufferImage = new BufferedImage(
					resultBufferImage.getWidth() + Math.abs(resultBufferImage.getWidth() - getNextBufferImage.getWidth()) - 1, 
					resultBufferImage.getHeight() + 3 + getNextBufferImage.getHeight() - 1, 
					BufferedImage.TYPE_INT_ARGB
			);
			
			Graphics2D g2D = resultBufferImage.createGraphics();
//			g2D.setRenderingHints(renderingHints);
			g2D.drawImage(getPreviousBufferImage, 0, 0, getPreviousBufferImage.getWidth(), getPreviousBufferImage.getHeight(), null);
			g2D.drawImage(getNextBufferImage, 0, getPreviousBufferImage.getHeight(), getNextBufferImage.getWidth(), getNextBufferImage.getHeight(), null);			
			g2D.dispose();
		}

		timeWas = System.currentTimeMillis();

		return resultBufferImage;
	}
}