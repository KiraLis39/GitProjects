package base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import enums.ENUMS;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.FoxFontBuilder.FONT;
import fox.builders.ResourceManager;


@SuppressWarnings("serial")
public class MidBuilder extends JPanel {
	public enum TYPE {NONE, HERO, DOSUG, WORK, LOVE, RELIGION, SPORT, GIGIENE, ADMIN};
	private TYPE currentType = TYPE.NONE;
	
	private static Color backColor = new Color(0.0f, 0.0f, 0.0f, 0.7f);
	private Color[] gradColors = new Color[] {Color.RED, Color.YELLOW, Color.GREEN, Color.RED, Color.YELLOW, Color.GREEN};
	
	private int cr, cg, cb, ca;
	private int x, y, infX;
	private float ftWidth, ftHeight, widthSpace, heightSpace;
	private float[] gradDots = new float[] {0.0f, 0.1f, 0.25f, 0.75f, 0.9f, 1.0f};
	
	private BufferedImage HungryBImage, EnergyBImage, MoodBImage, DosugBImage, RomanticBImage, GigeneBImage, SportBImage;
	
	private Font titleFont = Registry.ffb.setFoxFont(FONT.GEORGIA, 24, true);
	private Font textFont = Registry.ffb.setFoxFont(FONT.GEORGIA, 14, false);
	
	
	public MidBuilder(TYPE type) {
		setOpaque(true);
		setIgnoreRepaint(true);
		changeType(type);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;		
		Registry.render(g2D);
		
		switch (currentType) {
		case HERO:	HEROPANE(g2D);
			break;
		case DOSUG:	DOSUGPANE(g2D);
			break;
		case WORK:	WORKPANE(g2D);
			break;
		case LOVE:		LOVEPANE(g2D);
			break;
		case RELIGION:RELIGIONPANE(g2D);
			break;
		case SPORT:		SPORTPANE(g2D);
			break;
		case GIGIENE:	GIGIENEPANE(g2D);
			break;
		case ADMIN:	ADMINPANE(g2D);
			break;
		default: 
			changeType(TYPE.NONE);
			drawBackColor(g2D);
		}
		
		g2D.dispose();
	}

	/* */
	private void HEROPANE(Graphics2D g2D) {
		if (HungryBImage == null) {buildImages();}
		drawBackColor(g2D);
		
		ftWidth = getWidth() / 2f - 21f;
		ftHeight = getHeight() / 4f - 27f;
		
		widthSpace = (getWidth() - 21) % (ftWidth * 2f);
		heightSpace = (getHeight() - 27) % (ftHeight * 4f);
		
		g2D.setStroke(new BasicStroke(1));
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				x = (int) (widthSpace / 2f + j * ftWidth + (j * 21));
				y = (int) (heightSpace / 4f + i * ftHeight + (i * 24));
				infX = x + 12;
				
				drawNextField(g2D, getCurrentLevelName(i * 2 + j), getCurrentValueLevel(i * 2 + j), getCurrentFieldImage(i * 2 + j), "");
			}
		}
	}
	
	private void drawNextField(Graphics2D g2D, String levelName, Double valueLevel, BufferedImage fieldImage, String string) {
		// главный ободок каждого мини-поля информации;
		g2D.setColor(Color.WHITE);
		g2D.drawRoundRect(x, y, (int) ftWidth, (int) ftHeight, 6, 6);				

		// линия тайтла:
		g2D.setColor(Color.GRAY);
		g2D.drawRoundRect(x + 6, y + 6, (int) (ftWidth - 12f), (int) (ftHeight / 8f), 6, 6);
		
		g2D.setColor(Color.WHITE);
		if (fieldImage == null) {
			// пишем общее инфо в последней форме:
			g2D.drawString("Info:", (float) (x + (ftWidth - 12f) / 2f - Registry.ffb.getStringBounds(g2D, levelName).getWidth() / 2D), y + ftHeight / 8.5f);
			
			g2D.drawString("Вес: " + getCurrentLevelName(7), infX, y + ftHeight * 0.3f);
			g2D.drawString("Возраст: " + getCurrentLevelName(8), infX, y + ftHeight * 0.4f);
			g2D.drawString("Деньги: " + getCurrentLevelName(9), infX, y + ftHeight * 0.5f);
			g2D.drawString("Счастье: " + getCurrentLevelName(10), infX, y + ftHeight * 0.6f);
			g2D.drawString("Здоровье: " + getCurrentLevelName(11), infX, y + ftHeight * 0.7f);
			g2D.drawString("В игре: " + getCurrentLevelName(12), infX, y + ftHeight * 0.8f);
			return;
		} else {g2D.drawString(levelName, (float) (x + (ftWidth - 12f) / 2f - Registry.ffb.getStringBounds(g2D, levelName).getWidth() / 2D), y + ftHeight / 8.5f);}
		
		
		// уголок изображения:
		g2D.setColor(Color.GRAY);
		g2D.drawRoundRect(x + 6, (int) (y + (ftHeight / 8f + 12f)), (int) (ftWidth / 5f), (int) (ftHeight / 1.75f), 6, 6);
//		g2D.setClip(new RoundRectangle2D.Float(x + 7f, y + (ftHeight / 8f + 12f) + 1f, ftWidth / 5f - 2f, ftHeight / 1.75f - 2f, 6, 6));
		g2D.drawImage(fieldImage, x + 8, (int) (y + (ftHeight / 8f + 12f)) + 1, (int) (ftWidth / 5f) - 2, (int) (ftHeight / 1.75f) - 2, this);
//		g2D.setClip(null);
		
		// поле информации:
		g2D.drawRoundRect(
				(int) (x + 12f + ftWidth / 5f), (int) (y + (ftHeight / 8f + 12f)), 
				(int) (ftWidth - ftWidth / 5f - 18f), (int) (ftHeight / 1.75f), 6, 6);
		
		// прогресс-бар:
		g2D.drawRoundRect(x + 6, (int) (y + ftHeight * 0.8f), (int) (ftWidth - 12f), (int) (ftHeight * 0.15f), 3, 3);
		g2D.setPaint(new LinearGradientPaint(x, y, ftWidth * 5, y, gradDots, gradColors));
		g2D.fillRoundRect(x + 9, (int) (y + ftHeight * 0.8f) + 3, (int) ((ftWidth - 15.5f) / 100f * valueLevel), (int) (ftHeight * 0.15f) - 6, 3, 3);
		
		g2D.setPaint(valueLevel >= 50 ? Color.BLACK : Color.RED);
		g2D.drawString(valueLevel + " %", x + (ftWidth - 15.5f) / 2f, y + ftHeight * 0.9f);
	}

	private void buildImages() {
		Out.Print(getClass(), 0, "Building images...");
		
		HungryBImage = ResourceManager.getBufferedImage("HungryBImage");
		EnergyBImage = ResourceManager.getBufferedImage("EnergyBImage");
		MoodBImage = ResourceManager.getBufferedImage("MoodBImage");
		DosugBImage = ResourceManager.getBufferedImage("DosugBImage");
		RomanticBImage = ResourceManager.getBufferedImage("RomanticBImage");
		GigeneBImage = ResourceManager.getBufferedImage("GigeneBImage");
		SportBImage = ResourceManager.getBufferedImage("SportBImage");
	}

	private BufferedImage getCurrentFieldImage(int index) {
		switch (index) {
			case 0: return HungryBImage;
			case 1: return EnergyBImage;
			case 2: return MoodBImage;
			case 3: return DosugBImage;
			case 4: return RomanticBImage;
			case 5: return GigeneBImage;
			case 6: return SportBImage;
			
			default: return null;
		}
	}

	private String getCurrentLevelName(int index) {
		switch (index) {
			case 0: return "Сытость:";
			case 1: return "Энергия:";
			case 2: return "Настроение:";
			case 3: return "Хобби:";
			case 4: return "Отношения:";
			case 5: return "Гигиена:";
			case 6: return "Физическая форма:";
			
			case 7: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.WEIGHT);
			case 8: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.AGE);
			case 9: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.CASH);
			case 10: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.HAPPYNESE);
			case 11: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.HEALTH);
			case 12: return IOM.getString(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.INGAME_TIME);
			
			default: return "Info:";
		}
	}

	private Double getCurrentValueLevel(int index) {
		switch (index) {
		case 0: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.HUNGRY);
		case 1: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.ENERGY);
		case 2: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.MOOD);
		case 3: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.DOSUG);
		case 4: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.ROMANTIC);
		case 5: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.GIGENE);
		case 6: return IOM.getDouble(IOM.HEADERS.USER_SAVE, ENUMS.IOMsave.STRENGE);

		default: return 0D;
		}
	}

	
	private void DOSUGPANE(Graphics2D g2D) {
//		if (Food0BImage == null) {buildImages();}		
		drawBackColor(g2D);
		
		ftWidth = getWidth() / 3f - 9f;
		ftHeight = getHeight() / 7f;
		
		widthSpace = (getWidth() - 9) % (ftWidth * 3f);
		heightSpace = (getHeight() - 21) % (ftHeight * 5f);
		
		// draw title:
		g2D.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
		g2D.fillRoundRect(6, 3, getWidth() - 12, (int) (getHeight() * 0.045f), 3, 3);
		g2D.setColor(Color.WHITE);
		g2D.setFont(titleFont);
		g2D.drawString("Окно потребностей:", 
				(float) (getWidth() / 2f - Registry.ffb.getStringBounds(g2D, "Окно потребностей:").getWidth() / 2D), 
				(int) (getHeight() * 0.034f));
		
		g2D.setFont(textFont);
		g2D.setStroke(new BasicStroke(1));
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				x = (int) (widthSpace / 3f + j * ftWidth + (j * 6));
				y = (int) (heightSpace / 5f + i * ftHeight + (i * 9));
				infX = x + 12;

				// главный ободок каждого мини-поля информации;
				g2D.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
				g2D.fillRoundRect(x, y, (int) ftWidth, (int) ftHeight, 6, 6);
				g2D.setColor(Color.WHITE);
				g2D.drawRoundRect(x, y, (int) ftWidth, (int) ftHeight, 6, 6);				

				// линия тайтла:
				g2D.setColor(Color.GRAY);
				g2D.drawRoundRect(x + 6, y + 6, (int) (ftWidth - 12f), (int) (ftHeight / 8f), 6, 6);
				
				g2D.setColor(Color.WHITE);
				g2D.drawString("NA", (float) (x + (ftWidth - 12f) / 2f - Registry.ffb.getStringBounds(g2D, "NA").getWidth() / 2D), y + ftHeight / 7f);
				
				// уголок изображения:
				g2D.setColor(Color.GRAY);
				g2D.drawRoundRect(x + 6, (int) (y + (ftHeight / 8f + 12f)), (int) (ftWidth / 5f), (int) (ftHeight / 1.75f), 6, 6);
//				g2D.drawImage(fieldImage, x + 8, (int) (y + (ftHeight / 8f + 12f)) + 1, (int) (ftWidth / 5f) - 2, (int) (ftHeight / 1.75f) - 2, this);
				
				// поле информации:
				g2D.drawRoundRect(
						(int) (x + 12f + ftWidth / 5f), (int) (y + (ftHeight / 8f + 12f)), 
						(int) (ftWidth - ftWidth / 5f - 18f), (int) (ftHeight / 1.75f), 6, 6);
			}
		}
		
		g2D.setColor(new Color(0.2f, 0.2f, 0.2f, 0.7f));
		g2D.fillRoundRect(6, (int) (getHeight() * 0.66f), getWidth() - 15, (int) (getHeight() * 0.33f), 3, 3);
		g2D.setColor(Color.WHITE);
		g2D.drawRoundRect(6, (int) (getHeight() * 0.66f), getWidth() - 15, (int) (getHeight() * 0.33f), 3, 3);
	}
	
	private void WORKPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	
	private void LOVEPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	
	private void RELIGIONPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	
	private void SPORTPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	
	private void GIGIENEPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	
	private void ADMINPANE(Graphics2D g2D) {
		drawBackColor(g2D);
	}
	/* */
	
	
	private void colorCorrector(Color bCol) {
		cr = bCol.getRed() + 16;
		cg = bCol.getGreen() + 16;
		cb = bCol.getBlue() + 16;
		ca = bCol.getAlpha() - 64;
		
		if (cr < 0) {cr = 0;} else if (cr > 255) {cr = 255;}
		if (cg < 0) {cg = 0;} else if (cg > 255) {cg = 255;}
		if (cb < 0) {cb = 0;} else if (cb > 255) {cb = 255;}
		if (ca < 0) {ca = 0;} else if (ca > 255) {ca = 255;}
	}
	
	private void drawBackColor(Graphics2D g2D) {
		g2D.setColor(Registry.isPaused ? new Color(cr, cg, cb, ca) : backColor);
		g2D.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 5, 5);
	}

	
	public void changeType(TYPE type) {
		currentType = type;
		
		switch (currentType) {
			case HERO:		backColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);		break;
			case DOSUG: 		backColor = new Color(0.0f, 0.0f, 1.0f, 0.8f);		break;
			case WORK:		backColor = new Color(0.2f, 0.5f, 0.0f, 0.8f);		break;
			case LOVE:			backColor = new Color(0.5f, 0.0f, 0.0f, 0.8f);		break;
			case RELIGION:backColor = new Color(1.0f, 1.0f, 0.0f, 0.8f);		break;
			case SPORT:		backColor = new Color(0.7f, 0.7f, 0.7f, 0.8f);		break;
			case GIGIENE:	backColor = new Color(0.0f, 1.0f, 1.0f, 0.8f);		break;
			case ADMIN:		backColor = new Color(0.0f, 1.0f, 0.0f, 0.8f);		break;
			default: backColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);
		}
		
		colorCorrector(backColor);
	}
	
	public Object getType() {return currentType;}
}