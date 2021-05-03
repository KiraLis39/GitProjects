package door;

import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import fox.IOM;
import fox.Out;
import gui.AniFrame;
import registry.Registry;


public class MainClass {
	public static Thread disclaimerThread;
	
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
//			SwingUtilities.updateComponentTreeUI(frame);
	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		Out.setLogsCoutAllow(3);
		Out.setErrorLevel(Out.LEVEL.INFO);
		
		try {Registry.messageIcon = new ImageIcon(ImageIO.read(new File("./res/pic/bim0.png")));
		} catch (Exception e) {/* IGNORE ABSENT ICON */}
		
		disclaimerThread = new Thread(new Runnable() {
			public void run() {
				Out.Print(MainClass.class, Out.LEVEL.DEBUG, "Отображение начального отказа об ответственности...");
				
				try {
					JOptionPane.showConfirmDialog(null, 
							"<html><H3>Все расчеты соответствуют<br>официально заявленным дозам<br>в инструкциях препаратов.", "Внимание!", 
							JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, Registry.messageIcon);
				} catch (Exception e) {
					Out.Print(MainClass.class, Out.LEVEL.WARN, "Что-то пошло не так при начале работы. Ошибка при отображении стартового предупреждения.");
					e.printStackTrace();
				}
			}
		});
		disclaimerThread.start();
		
		Out.Print(MainClass.class, Out.LEVEL.DEBUG, "Старт программы. Проверка наличия директорий и файлов...");
		checkFiles();

		Out.Print(MainClass.class, Out.LEVEL.DEBUG, "Чтение конфигурации...");
		buildIOM();
		
		Out.Print(MainClass.class, Out.LEVEL.INFO, "Загрузка UI...");
		new AniFrame();
	}

	private static void buildIOM() {
		IOM.add(IOM.HEADERS.CONFIG, new File("./data/config.txt"));
		
		Registry.isAdminModeAllow = IOM.getBoolean(IOM.HEADERS.CONFIG, "isAdminAllowed");
		Registry.isResizeAllow = IOM.getBoolean(IOM.HEADERS.CONFIG, "isResizeAllow");
		Registry.isRenderOn = IOM.getBoolean(IOM.HEADERS.CONFIG, "isRenderOn");
		
		try {
			Registry.frameWidth = IOM.getInt(IOM.HEADERS.CONFIG, "frameWidth");
			Registry.frameHeight = IOM.getInt(IOM.HEADERS.CONFIG, "frameHeight");
		} catch (Exception e) {
			Registry.frameWidth = 800;
			Registry.frameHeight = 600;
			
			IOM.set(IOM.HEADERS.CONFIG, "frameWidth", 800);
			IOM.set(IOM.HEADERS.CONFIG, "frameHeight", 600);
		}
	}

	private static void checkFiles() {
		try {
			if (!new File("./data/").exists()) {
				new File("./data/").mkdirs();
				Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Не найдена дирректория базы данных!");
			}			
			if (!Registry.photoDir.exists()) {Registry.photoDir.mkdir();}			
			if (!new File("./data/db.db").exists()) {
				Out.Print(MainClass.class, Out.LEVEL.ACCENT, "Отсутствует файл базы данных! Будет создан новый, если возиожно.");
			}
		} catch (Exception e) {
			Out.Print(MainClass.class, Out.LEVEL.ERROR, "Провал проверки наличия необходимых файлов!");
			e.printStackTrace();
			exit(46);
		}		
	}

	public static void exit(int i) {
		IOM.saveAll();
		
		Out.Print(MainClass.class, Out.LEVEL.WARN, "Выход из программы с кодом " + i);
		System.exit(i);
	}
}