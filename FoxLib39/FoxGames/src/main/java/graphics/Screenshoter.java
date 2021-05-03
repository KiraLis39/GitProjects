package graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import fox.Out;
import fox.Out.LEVEL;


public class Screenshoter {

	public void saveImage(BufferedImage image, Path saveAs) throws IOException {
		String exp = saveAs.getFileName().toString().split(".")[saveAs.getFileName().toString().split(".").length-1];
		saveImage(image, exp, saveAs);
	}
	
	public void saveImage(BufferedImage image, String extention, Path saveAs) throws IOException {
		Out.Print(getClass(), LEVEL.ACCENT, "Saving the image " + saveAs + " (" + extention + ")...");
		ImageIO.write(image, extention, saveAs.toFile());
	}
}
