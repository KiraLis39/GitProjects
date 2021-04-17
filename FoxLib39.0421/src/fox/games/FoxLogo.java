package games;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;

import adds.InputAction;
import adds.Out;
import adds.Out.LEVEL;


@SuppressWarnings("serial")
public class FoxLogo implements Runnable {	
	private JFrame logoFrame;
	private Thread canvasEngine;

	Graphics2D g2D;
	BufferedImage[] images;
	Color logoBackColor;
	private Raster raster;
	private Object data;
	
	int breakKey = KeyEvent.VK_ESCAPE;
	int picCounter= 0, timeOut = 0;
	private int timer = 13;
	
	boolean notBreaked = true;
	

	public FoxLogo(BufferedImage[] textureFilesMassive) {images = textureFilesMassive;}
	
	public void start() {start(images, breakKey);}
	
	public void start(BufferedImage[] textureFilesMassive) {start(textureFilesMassive, breakKey);}
	
	public void start(BufferedImage[] textureFilesMassive, int _breakKey) {
		if (textureFilesMassive == null) {throw new RuntimeException("StartLogoRenderer: start: Error. Textures massive is NULL.");}
		
		Out.Print(FoxLogo.class, LEVEL.INFO, "Set StartLogo`s breakKey to " + _breakKey, Thread.currentThread());
		breakKey = _breakKey;

		Out.Print(FoxLogo.class, LEVEL.INFO, "Load StartLogo`s images count: " + textureFilesMassive.length, Thread.currentThread());
		images = textureFilesMassive;

		canvasEngine = new Thread(this);
		canvasEngine.start();
	}
	
	public boolean isActive() {return canvasEngine.isAlive();}

	
	@Override
	public void run() {
		raster = images[picCounter].getRaster();
		data = raster.getDataElements(1, 1, null);
		logoBackColor = new Color(images[picCounter].getColorModel().getRGB(data), true);
		
		logoFrame = new JFrame() {
			{
				setUndecorated(true);
				setBackground(new Color(0,0,0,0));
				setExtendedState(Frame.MAXIMIZED_BOTH);
				setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height));
//				setBackground(logoBackColor);
//				getRootPane().setBackground(logoBackColor);
//				getContentPane().setBackground(logoBackColor);
				
				add(new JPanel() {
					@Override
				 	public void paintComponent(Graphics g) {
						if (!notBreaked) {return;}
						timeOut++;
						
						try {
							g2D = (Graphics2D) g;
											
							g2D.setColor(logoBackColor);
							g2D.fillRect(0, 0, getWidth(), getHeight());
							
					        g2D.drawImage(
					        		images[picCounter], 
					        		Toolkit.getDefaultToolkit().getScreenSize().width / 2 - images[picCounter].getWidth() / 2, 
					        		Toolkit.getDefaultToolkit().getScreenSize().height / 2 - images[picCounter].getHeight() / 2, 
					        		images[picCounter].getWidth(), 
					        		images[picCounter].getHeight(), 
					        		null);
					        
					        g2D.dispose();
						} catch (Exception e) {/* IGNORE DISPOSED */}
				        
				        if (timeOut > 200) {loadNextImage();}
					}

					{
						setFocusable(true);
//						setBackground(logoBackColor);
//						getRootPane().setBackground(logoBackColor);
//						getContentPane().setBackground(logoBackColor);
						
						InputAction.add("canva", this);
						InputAction.set("canva", "stop", breakKey, 0, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent arg0) {finalLogo();}
						});
					}
				});
				
				setLocationRelativeTo(null);
				setVisible(true);
			}
		};
		
		while (notBreaked) {
			logoFrame.repaint();
			try {Thread.sleep(timer);} catch (InterruptedException e) {/* IGNORE SLEEP */}
		}

		finalLogo();
	}
	
	void loadNextImage() {
		picCounter++;
		
		if (picCounter >= images.length) {notBreaked = false;
		} else {
			timeOut = 0;
			raster = images[picCounter].getRaster();
    		data = raster.getDataElements(1, 1, null);
    		logoBackColor = new Color(images[picCounter].getColorModel().getRGB(data), true);
    		
//    		getRootPane().setBackground(logoBackColor);
//			getContentPane().setBackground(logoBackColor);
		}
	}
	
	void finalLogo() {
		notBreaked = false;

		logoFrame.setSize(50, 50);
		logoFrame.dispose();
	}

	public void breakLogoNow() {notBreaked = false;}
}