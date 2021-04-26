package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import fox.builders.ResourceManager;


public class TimePod extends aTPod {
	private int x, y, tpDim = 32, speed = 0, volume;

	
	public TimePod(String tpName, BufferedImage podImage, int timeVolume) {
		name = tpName;
		volume = timeVolume;
		
		if (podImage == null) {tpImage = ResourceManager.getBufferedImage("timePod");
		} else {tpImage = podImage;}
		
		recreate();
	}
	
	public void draw(Graphics2D g2D, boolean move) {
//		System.out.println(name + " position: " + x + "x" + y);
		g2D.drawImage(tpImage, x, y, tpDim, tpDim, null);
		if (move) {x -= speed;}
		if (x < -tpDim) {isDestroyed = true;}
	}
	
	public boolean isDestroyed() {return isDestroyed;}

	public void recreate() {
		if (isDestroyed && name.startsWith("S")) {return;}
//		System.out.println("Creating the pod named " + name);		
		speed = 4 + r.nextInt(4);
		x = 1024 + tpDim + r.nextInt(512);
		y = tpDim + r.nextInt(450);
		isDestroyed = false;
	}

	public String getName() {return name;}

	public Rectangle getRectangle() {
		return new Rectangle(x, y, tpDim, tpDim);
	}

	public int getTimeVolume() {return volume;}
}
