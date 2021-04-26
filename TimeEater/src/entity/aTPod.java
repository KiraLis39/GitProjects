package entity;

import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class aTPod {
	protected Random r = new Random();
	protected BufferedImage tpImage;
	protected boolean isDestroyed = false;
	protected String name;
}
