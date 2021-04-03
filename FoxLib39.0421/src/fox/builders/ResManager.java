package fox.builders;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javax.imageio.ImageIO;
import fox.adds.Out;


public class ResManager {
	private static Map<String, File> resourseLinksMap = Collections.synchronizedMap(new LinkedHashMap<String, File> ());
	//and:
	private static Map<String, byte[]> cash = Collections.synchronizedMap(new LinkedHashMap<String, byte[]> ());
	//or:
	private static Map<String, BufferedImage> imageBuffer = Collections.synchronizedMap(new LinkedHashMap<String, BufferedImage> ());
	
	private static int HQ = 0, MIN_ELEMENTS_CASH_COUNT_TO_CLEARING = 100;
	private final static long MAX_MEMORY = Runtime.getRuntime().maxMemory() - 1;
	private static long USED_MEMORY, MAX_LOADING;
	
	private static float memGCTrigger = 0.85f;
	private static Boolean logEnable = true;
	
	
	// may be overrides:
	public static void memoryControl() {
		USED_MEMORY = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		MAX_LOADING = (long) (MAX_MEMORY * memGCTrigger);
		
		if (USED_MEMORY >= MAX_LOADING) {
			log("ResourceManager: Memory control activation! (USED " + (USED_MEMORY / 1048576L) + " > " + ((int) (memGCTrigger * 100)) + "% from MAX " + (MAX_MEMORY / 1048576L) + ")\nClearing...");
			
			try {
				int clearedCount = 0;
				if (cash.size() > MIN_ELEMENTS_CASH_COUNT_TO_CLEARING) {
					for (Entry<String, byte[]> entry : cash.entrySet()) {
						if (entry.getValue().length > MAX_LOADING / 10) {
							cash.remove(entry.getKey());
							clearedCount++;
						}
					}
					
					log(1, "clearCash: was removed " + clearedCount + " elements from cash.");
				} else {
					log(1, "clearCash: cash has only " + cash.size() + "elements (MIN_ELEMENTS = " + MIN_ELEMENTS_CASH_COUNT_TO_CLEARING + "), than been full-cleared.");
					cash.clear();
				}
			} catch (Exception e) {
				log(2, "Was catched memory overlap! Hooray! usingLong > " + (USED_MEMORY / 1048576L) + " / " + (MAX_MEMORY / 1048576L) + " >> " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	
	public synchronized static void add(Object index, File file) throws Exception {add(index, file, false);}
	
	public synchronized static void add(Object index, BufferedImage bImage) throws Exception {add(index, bImage, false);}
	
	public synchronized static void add(Object index, URL fileURL) throws Exception {add(index, fileURL, false);}
	
	public synchronized static void add(Object index, URL fileURL, Boolean isImage) throws Exception {add(index, new File(fileURL.getFile()), isImage);}
		
	public synchronized static void add(Object index, Object file, Boolean isImage) throws Exception {
		log("Try to adding the resource '" + index + "'...");		
		String name = String.valueOf(index);
		if (file instanceof BufferedImage) {
			imageBuffer.put(name, (BufferedImage) file);
			return;
		} else if (file instanceof File) {		
			resourseLinksMap.put(name, (File) file);
			if (!isImage) {cash.put(name, Files.readAllBytes(((File) file).toPath()));
			} else {imageBuffer.put(name, ImageIO.read((File) file));}
		}
		
		memoryControl();
	}
	
	public synchronized static void remove(Object index) {
		String name = String.valueOf(index);
		if (cash.containsKey(name)) {cash.remove(name);}
		if (imageBuffer.containsKey(name)) {imageBuffer.remove(name);}
		if (resourseLinksMap.containsKey(name)) {resourseLinksMap.remove(name);}
	}

	
	public synchronized static BufferedImage getBImage(Object index) {
		try {
			return getBImage(
					index, 
					true, 
					GraphicsEnvironment
					.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice()
					.getDefaultConfiguration()
					);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized static BufferedImage getBImage(Object index, Boolean transparensy) {return getBImage(index, true, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());}
	
	public synchronized static BufferedImage getBImage(Object index, Boolean transparensy, GraphicsConfiguration gconf) {
		Objects.requireNonNull(index);
		String name = String.valueOf(index);
		if (name.equals("")) {throw new RuntimeException("Index of image is NULL or empty!");}
		log("Getting the cashed BufferedImage of " + name);
		
		if (imageBuffer.containsKey(name)) {return imageBuffer.get(name);
		} else {
			ImageIO.setUseCache(false);
			BufferedImage tmp;
			
			// OPAQUE = 1; BITMASK = 2; TRANSLUCENT = 3;
			HQ = 3;
			
			if (cash.containsKey(name)) {
				try (ByteArrayInputStream bais = new ByteArrayInputStream(cash.get(name))) {
					BufferedImage imRb = ImageIO.read(bais);
//					bais.close();
					
					tmp = gconf.createCompatibleImage(imRb.getWidth(), imRb.getHeight(), HQ);				
					
					if (transparensy) {
						Graphics2D g2D = (Graphics2D) tmp.getGraphics();
						g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);						
						g2D.drawImage(imRb, 0, 0, null);
						g2D.dispose();
						
						imageBuffer.put(name, tmp);
					} else {imageBuffer.put(name, imRb);}
					
					cash.remove(name);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				if (resourseLinksMap.containsKey(name)) {
					try {
						tmp = gconf.createCompatibleImage(
								ImageIO.read(resourseLinksMap.get(name)).getWidth(), 
								ImageIO.read(resourseLinksMap.get(name)).getHeight(),
								HQ
						);

						if (transparensy) {
							Graphics2D g2D = (Graphics2D) tmp.getGraphics();
							g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
							g2D.drawImage(ImageIO.read(resourseLinksMap.get(name)), 0, 0, null);
							g2D.dispose();
							
							imageBuffer.put(name, tmp);
						} else {imageBuffer.put(name, ImageIO.read(resourseLinksMap.get(name)));}
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				} else {
					log(3, "BufferedImage '" + name + "' not exist into ResourceManager!");
					return null;
				}
			}
			
			return imageBuffer.get(name);
		}
	}
	
	public synchronized static Dimension getBImageDim(Object index) {
		try {return new Dimension(imageBuffer.get(index.toString()).getWidth(), imageBuffer.get(index.toString()).getHeight());
		} catch (Exception e) {return null;}
	}
	
	public synchronized static byte[] getBytes(Object index) {
		String name = String.valueOf(index);
		if (cash.containsKey(name)) {return cash.get(name);
		} else if (resourseLinksMap.containsKey(name)) {
			try {add(name, resourseLinksMap.get(name), true);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return cash.get(name);
		} else {
			log("RM.getBytes(): File with name '" + name + "' dont exist into BytesMap!");
			return null;
		}
	}
	
	public synchronized static File getFilesLink(Object index) {
		String name = String.valueOf(index);
		try {return resourseLinksMap.get(name);
		} catch (Exception e) {
			if (resourseLinksMap.isEmpty()) {
				log("The LinksMap was cleaned already. It was You?..");
				e.printStackTrace();
			} else {
				log("The link of file '" + name + "' dont exist into LinksMap. Sorry!");
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	public synchronized static Set<Entry<String, File>> getEntrySet() {return resourseLinksMap.entrySet();}
	
	public synchronized static Set<String> getKeySet() {return resourseLinksMap.keySet();}
	public synchronized static Collection<File> getValuesSet() {return resourseLinksMap.values();}

	
	private static void log(String message) {log(0, message);}
	
	private static void log(int i, String message) {
		if (logEnable) {Out.Print(ResManager.class, i, message);}
	}
	
	
	public synchronized static void clearImages() {imageBuffer.clear();}
	public synchronized static void clearAll() {
		cash.clear();
		imageBuffer.clear();
		resourseLinksMap.clear();
	}
	
	public synchronized static int getCashSize() {return cash.size();}
	public synchronized static int getImagesSize() {return imageBuffer.size();}
	public synchronized static int getLinksSize() {return resourseLinksMap.size();}
	
	public synchronized static long getCashVolume() {
		long bytesMapSize = 0L;		
		for (int i = 0; i < cash.size(); i++) {bytesMapSize += cash.get(String.valueOf(i)).length;}		
		return bytesMapSize;		
	}

	
	public static Boolean isDebugOn() {return logEnable;}
	public static void setDebugOn(Boolean enabled) {logEnable = enabled;}
}