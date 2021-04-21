package door;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class MainClass {
	
	public static void main(String[] args) {
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		new SFrame();
	}
}
