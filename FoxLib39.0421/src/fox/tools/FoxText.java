package fox.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import fox.builders.FoxFontBuilder;


public class FoxText {
	static FoxFontBuilder ffb;
	
	public static Graphics2D draw(String text, Graphics2D g2D, Font font, Rectangle2D r, Color c) {return draw(text, g2D, font, r, c, null);}
	
	public static Graphics2D draw(String text, Graphics2D g2D, Font font, Rectangle2D r, Color c, Color c2) {
		ffb = new FoxFontBuilder();
				
		Rectangle2D fontRect = FoxFontBuilder.getStringBounds(g2D, text);
		float fontSize = font.getSize();
		g2D.setFont(font);
		
		while (fontRect.getWidth() > r.getWidth() - (r.getWidth() * 0.01D) || fontRect.getHeight() > r.getHeight() - (r.getHeight() * 0.01D)) {
			fontSize--;
			font = font.deriveFont(fontSize);
			g2D.setFont(font);
			fontRect = FoxFontBuilder.getStringBounds(g2D, text);
		}
		
		fontRect = null;
		ffb = null;
		return g2D;
	}

	public static Graphics2D draw(String text, Graphics2D g2D, Font font, Point2D p, Color c) {return draw(text, g2D, font, p, c, null);}
	
	public static Graphics2D draw(String text, Graphics2D g2D, Font font, Point2D p, Color c, Color c2) {
		g2D.setFont(font);
		
		g2D.setColor(c2 == null ? Color.DARK_GRAY : c2);
		g2D.drawString(text, (float) (p.getX() - 3D), (float) (p.getY() + 3D));
		
		g2D.setColor(c);
		g2D.drawString(text, (float) p.getX(), (float) p.getY());
		
		return g2D;
	}
}