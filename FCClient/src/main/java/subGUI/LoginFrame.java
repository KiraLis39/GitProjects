package subGUI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import door.Exit;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.FoxFontBuilder;
import net.NetConnector;
import registry.IOMs;
import registry.Registry;


@SuppressWarnings("serial")
public class LoginFrame extends JDialog {
	private static LoginFrame loginFrame;
	private static JPasswordField passField;
	private static JTextField loginField;
	private Graphics2D g2D;
	
	
	@Override
	public void paint(Graphics g) {
//		g2D = (Graphics2D) g;
		
//		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.0f));
		super.paint(g);
//		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
		
//		ResManager.getFilesLink("requestImage").getPath() // KiraLis39
	}
	
	public LoginFrame() {
		loginFrame = this;
		
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(400, 250));
		setBackground(new Color(0, 0, 0, 0));
//		setOpacity(0.95f);
		
		JPanel basePane = new JPanel(new GridLayout(3, 0, 0, 6)) {
			@Override
			protected void paintComponent(Graphics g) {
				g2D = (Graphics2D) g;
				Registry.render(g2D, true);
				
				g2D.setColor(Color.DARK_GRAY);
				g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 19, 19);
				
				g2D.setStroke(new BasicStroke(2));
				g2D.setColor(Color.WHITE);
				g2D.drawRoundRect(3, 4, getWidth() - 8, getHeight() - 8, 16, 16);
				g2D.setColor(Color.GRAY);
				g2D.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 9, 16, 16);
				
				g2D.setFont(Registry.fBigSphere);
				g2D.setColor(Color.BLACK);
				g2D.drawString("-= LOG-IN =-", (int) (getWidth() / 2 - FoxFontBuilder.getStringCenterX(g2D, "-= LOG-IN =-")) - 1, 29);
				g2D.setColor(Color.GRAY);
				g2D.drawString("-= LOG-IN =-", (int) (getWidth() / 2 - FoxFontBuilder.getStringCenterX(g2D, "-= LOG-IN =-")), 28);
			}			
			
			{
				setBorder(new EmptyBorder(24, 3, 3, 3));
				
				JPanel loginPane = new JPanel(new BorderLayout()) {
					@Override
					public void paintComponent(Graphics g) {
//						super.paintComponent(g);
						
						g2D = (Graphics2D) g;
						g2D.setColor(Color.DARK_GRAY);
						g2D.fillRoundRect(32, 0, 70, 15, 3, 3);
												
						g2D.setFont(Registry.fMessage);
						g2D.setColor(Color.GRAY.brighter());
						g2D.drawString("log-in:", 28, 27);
					}
					
					{
						setOpaque(false);
						setBorder(new EmptyBorder(9, 18, 3, 18));
//						setBorder(BorderFactory.createCompoundBorder(
//								new EmptyBorder(9, 18, 3, 18),
//								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true), "Логин:", 1, 2, Registry.fMessage, Color.GRAY.brighter())
//						));
						
						loginField = new JTextField(IOM.getString(IOM.HEADERS.LAST_USER, IOMs.LUSER.LAST_USER)) {
							@Override
							public void paintComponent(Graphics g) {
								super.paintComponent(g);
								
								Graphics2D g2D = (Graphics2D) g;

//								Area area = new Area(new Rectangle(1, 1, getWidth() - 2, getHeight() - 2));
//								area.subtract(new Area(new Rectangle(12, 0, 70, 15)));
//								g2D.draw(area);
								
								g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
								g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 19, 19);
								
								g2D.setStroke(new BasicStroke(1));
								g2D.setColor(Color.WHITE);
								g2D.drawRoundRect(2, 2, getWidth() - 6, getHeight() - 5, 16, 16);
								g2D.setColor(Color.GRAY);
								g2D.drawRoundRect(3, 2, getWidth() - 6, getHeight() - 6, 16, 16);
							}
							
							{
								setOpaque(false);
								setBorder(new EmptyBorder(9, 12, 1, 0));
								setForeground(Color.WHITE);
								setFont(Registry.fMessage);
								setCaretColor(Color.GREEN);
								addMouseListener(new MouseAdapter() {
									@Override
									public void mousePressed(MouseEvent e) {selectAll();}

									@Override
									public void mouseClicked(MouseEvent e) {selectAll();}
								});
							}
						};
						
						add(loginField);
					}
				};
				
				JPanel passPane = new JPanel(new BorderLayout()) {
					@Override
					public void paintComponent(Graphics g) {
//						super.paintComponent(g);
						
						g2D = (Graphics2D) g;
						g2D.setColor(Color.DARK_GRAY);
						g2D.fillRoundRect(32, 0, 70, 15, 3, 3);
												
						g2D.setFont(Registry.fMessage);
						g2D.setColor(Color.GRAY.brighter());
						g2D.drawString("password:", 28, 22);
					}
					
					{
						setOpaque(false);
						setBorder(new EmptyBorder(3, 18, 9, 18));
//						setBorder(BorderFactory.createCompoundBorder(
//								new EmptyBorder(3, 18, 9, 18),
//								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true), "Пароль:", 1, 2, Registry.fMessage, Color.GRAY.brighter())
//						));
						
						passField = new JPasswordField() {
							@Override
							public void paintComponent(Graphics g) {
								super.paintComponent(g);
								
								Graphics2D g2D = (Graphics2D) g;

//								Area area = new Area(new Rectangle(1, 1, getWidth() - 2, getHeight() - 2));
//								area.subtract(new Area(new Rectangle(12, 0, 70, 15)));
//								g2D.draw(area);
								
								g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
								g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 19, 19);
								
								g2D.setStroke(new BasicStroke(1));
								g2D.setColor(Color.WHITE);
								g2D.drawRoundRect(2, 2, getWidth() - 6, getHeight() - 5, 16, 16);
								g2D.setColor(Color.GRAY);
								g2D.drawRoundRect(3, 2, getWidth() - 6, getHeight() - 6, 16, 16);
							}
							
							{
								setOpaque(false);
//								setBackground(new Color(0.0f, 0.0f, 0.0f, 0.3f));
								setBorder(new EmptyBorder(9, 12, 1, 0));
								setForeground(Color.WHITE);
								setFont(Registry.fMessage);
								setCaretColor(Color.GREEN);
								addKeyListener(new KeyAdapter() {									
									@Override
									public void keyReleased(KeyEvent e) {
//										repaint();
									}									
									
									@Override
									public void keyPressed(KeyEvent e) {
//										repaint();
									}
								});
							}
						};
						
						add(passField);
					}
				};
				
				
				JPanel buttonsPane = new JPanel(new GridLayout(0, 2, 6, 0)) {
					{
						setOpaque(false);
						setBorder(new EmptyBorder(6, 24, 21, 24));
						
						JButton okButton = new JButton("=OK=") {
							{
								setBackground(Color.DARK_GRAY.darker());
								setForeground(Color.WHITE);
								setFocusPainted(false);
								setFont(Registry.fMenuBarBig);
								addActionListener(new ActionListener() {									
									@Override
									public void actionPerformed(ActionEvent e) {onOkButtonClick();}
								});
							}
						};
						
						JButton cancelButton = new JButton("CANCEL") {
							{
								setBackground(Color.DARK_GRAY.darker());
								setForeground(Color.WHITE);
								setFocusPainted(false);
								setFont(Registry.fMenuBarBig);
								addActionListener(new ActionListener() {									
									@Override
									public void actionPerformed(ActionEvent e) {onExitButtonClick();}
								});
							}
						};
						
						add(okButton);
						add(cancelButton);
					}
				};

				
				add(loginPane);
				add(passPane);
				add(buttonsPane);
			}
		};
						
		add(basePane);
		
		pack();
		setLocationRelativeTo(null);
		setModal(true);
		setVisible(true);
	}

	private static Boolean requestServerAccess(String login, char[] password) {
		NetConnector.reConnect(login, password);

		while(NetConnector.getThread() == null) {Thread.yield();}

		if (NetConnector.getCurrentState() == NetConnector.connState.DISCONNECTED) {
			try {NetConnector.getThread().join(3000);} catch (InterruptedException e) {e.printStackTrace();} 
		}
		if (NetConnector.getCurrentState() == NetConnector.connState.CONNECTING) {
			try {NetConnector.getThread().join(7000);} catch (InterruptedException e) {e.printStackTrace();} 
		}
		return NetConnector.getCurrentState() == NetConnector.connState.CONNECTED;
	 }
	
	
	private static void onOkButtonClick() {
		if (!loginField.getText().isBlank()) {

			if (passField.getPassword().length == 0) {
				Out.Print(LoginFrame.class, 1, "Autorization of user '" + loginField.getText() + "' with empty password...");
			} else {
				IOM.set(IOM.HEADERS.LAST_USER, "LAST_USER", loginField.getText());
				IOM.set(IOM.HEADERS.LAST_USER, IOMs.LUSER.LAST_PASSWORD, new String(passField.getPassword()));
				Out.Print(LoginFrame.class, 1, "Autorization of user '" + loginField.getText() + "' with password '" + new String(passField.getPassword()) + "'...");
			}
			
			// send user data to server for a checking and awaits for response...
			if (requestServerAccess(loginField.getText(), passField.getPassword())) {
				IOM.set(IOM.HEADERS.LAST_USER, IOMs.LUSER.LAST_USER, loginField.getText());
				IOM.save(IOM.HEADERS.LAST_USER.name());
		
				loginFrame.dispose();
			} else {showDeniedDialog();}
		} else {showInfoDialog();}
	 }

	private static void onExitButtonClick() {
		NetConnector.disconnect();
		Exit.exit(0);
	 }

	
	private static void showInfoDialog() {
		JOptionPane.showConfirmDialog(null, 
				"<html>Вы не поняли.<hr>Необходимо указать своё имя.", "Вы не поняли?", 
				JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE);
	}
	
	private static void showDeniedDialog() {
		JOptionPane.showConfirmDialog(null, 
				"<html>Доступ отсутствует!<hr>Возможно, нет доступа к серверу.", "Access denied", 
				JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	}
}
