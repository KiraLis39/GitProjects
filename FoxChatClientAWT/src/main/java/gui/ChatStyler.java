package gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResManager;
import registry.IOMs;
import registry.Registry;
import subGUI.MenuBar;


public class ChatStyler {
	public enum uiStyleType {DEFAULT, GOLD, DARK}
	private static uiStyleType uiStyle = uiStyleType.DEFAULT;
	public enum backgroundFillType {STRETCH, FILL, ASIS, PROPORTIONAL}
	private static backgroundFillType bFillType = backgroundFillType.STRETCH;
	
	
	public static void setBackgroundFillStyle(int bkgStyleIndex) {
		bFillType = backgroundFillType.values()[bkgStyleIndex];
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_DRAW_STYLE, bFillType.ordinal());
//		frame.repaint();
	}

	public static void setUIStyle(uiStyleType style) {setUIStyle(style.ordinal());}

	public static void setUIStyle(int styleIndex) {
		uiStyle = uiStyleType.values()[styleIndex];
		
		String themeDirName = uiStyleType.values()[styleIndex].name();
		System.out.println("Setts up ui_style: '" + themeDirName + "'");
		try {
			ResManager.add("menuBarImage", new File("./resources/images/" + themeDirName + "/menuBarImage.png"), true);
			ResManager.add("downBarImage", new File("./resources/images/" + themeDirName + "/downBarImage.png"), true);	
			ResManager.add("sendButtonImage", new File("./resources/images/" + themeDirName + "/btn.png"), true);
			ResManager.add("pod_0", new File("./resources/images/" + themeDirName + "/pod_0.png"), true);
			ResManager.add("pod_1", new File("./resources/images/" + themeDirName + "/pod_1.png"), true);
		} catch (Exception e) {
			Out.Print(ChatFrame.class, 3, "UI_STYLE is not correct: " + uiStyle);
			e.printStackTrace();
		}
		
		try {
			ChatFrame.setBackgroundImage(ImageIO.read(new File("./" + IOM.getString(IOM.HEADERS.CONFIG, IOMs.CONFIG.BKG_PATH))));
		} catch (IOException e) {
			try {ChatFrame.setBackgroundImage(ImageIO.read(new File("./resources/images/bkgDefault.png")));
			} catch (IOException e1) {e1.printStackTrace();};
		}
		
		uiStyle = uiStyleType.values()[uiStyle.ordinal()];
		IOM.set(IOM.HEADERS.CONFIG, IOMs.CONFIG.UI_STYLE, uiStyle.ordinal());
		
		ChatFrame.setupMenuBar(null);
		if (uiStyle == uiStyleType.DEFAULT) {
			ChatFrame.setupMenuBar(new MenuBar(Color.BLACK).getMenu());
			ChatFrame.setSidePanelsBkg(new Color(1.0f, 1.0f, 1.0f, 0.4f));
			ChatFrame.setUsersListBackground(new Color(1.0f, 1.0f, 1.0f, 0.4f));
		} else {
			ChatFrame.setupMenuBar(new MenuBar(Color.WHITE).getMenu());
			ChatFrame.setSidePanelsBkg(new Color(0.0f, 0.0f, 0.0f, 0.6f));
			ChatFrame.setUsersListBackground(new Color(0.0f, 0.0f, 0.0f, 0.6f));
		}
		
		ChatFrame.setSendButtonSprite(Registry.fsc.addSpritelist("sendButtonSprite", ResManager.getBImage("sendButtonImage"), 1, 3));
	}

	public static void loadUIStyle() {
		
	}

	public static backgroundFillType getFillType() {return bFillType;}	
}