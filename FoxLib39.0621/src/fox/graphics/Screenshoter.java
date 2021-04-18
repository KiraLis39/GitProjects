package graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import adds.Out;
import adds.Out.LEVEL;


public class Screenshoter {

	public void saveImage(BufferedImage image, File saveAs) throws IOException {
		String exp = saveAs.getName().split(".")[saveAs.getName().split(".").length-1];
		saveImage(image, exp, saveAs);
	}
	
	public void saveImage(BufferedImage image, String extention, File saveAs) throws IOException {
//	 	Save as new image:	ImageIO.write(BufferedImage, "PNG", new File(path, "combined.png"));
		Out.Print(getClass(), LEVEL.ACCENT, "Saving the image " + saveAs + " (" + extention + ")...");
		ImageIO.write(image, extention, saveAs);
	}
}
