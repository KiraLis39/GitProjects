package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import fox.builders.ResourceManager;
import fox.games.FoxSpritesCombiner;


public class Player {
	private FoxSpritesCombiner fsc = new FoxSpritesCombiner();
	private BufferedImage[] plImages;
	private int x, y, jumper = 0, spriteCounter = 0;
	
	public Player() {
		plImages = fsc.addSpritelist("player2", ResourceManager.getBufferedImage("player2"), 4, 1);
		x = 64;
		y = 100;
	}
	
	public void draw(Graphics2D g2D, boolean move) {
		if (move) {
			y = y + 3 - jumper;
			
			if (jumper > 0) {spriteCounter += 2;
			} else if (jumper < 0) {spriteCounter = 0;
			} else {spriteCounter++;}
			
			if (spriteCounter >= 15) {spriteCounter = 0;}
		} else {spriteCounter = 16;}
		
		g2D.drawImage(plImages[spriteCounter / 5], x, y, 128, 128, null);
		
		if (jumper > 0) {jumper--;} else if (jumper < 0) {jumper++;}
	}

	public void jump() {
		if (y > 64) {jumper = 15;}
	}

	public void fall() {
		jumper = -15;
	}
	
	public Rectangle getRectangle() {
		return new Rectangle(x, y, 128, 128);
	}
}