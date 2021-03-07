package subGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import gui.ChatFrame.messageType;
import registry.Registry;


@SuppressWarnings("serial")
public class BaloonPane extends JPanel {
	private SimpleDateFormat format = new SimpleDateFormat("(dd.MM HH:mm:ss)"); // "dd.MM.yyyy HH:mm:ss"
	private JTextArea baloonTextArea;
	private JPanel baloonPane;
	private String from, to;
	
	
	public BaloonPane(final messageType type, final String message, final String _from, final String _to, final Color color) {
//		System.out.println("A new baloon pane with: (" + type + ") " + message + "; from = " + from + "; to = " + to + ".");		
		setLayout(new BorderLayout());
		setOpaque(false);
		
		this.from = _from;
		this.to = _to;
		
		baloonPane = new JPanel(new BorderLayout(3, 3)) {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				Registry.render(g2D);
				g2D.setColor(color);
				g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

				g2D.setColor(color);
				g2D.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
				
				String strStart = "От: " + from + " кому";
				g2D.setColor(Color.BLACK);
				g2D.setFont(Registry.fLabels);
				g2D.drawString(strStart, 8, 16);
				
				g2D.setColor(Color.BLACK);
				g2D.drawString(to, (int) (11D + Registry.ffb.getStringWidth(g2D, strStart)) - 1, 17);
				g2D.setColor(Color.WHITE);
				g2D.drawString(to, (int) (11D + Registry.ffb.getStringWidth(g2D, strStart)), 16);
			}
			
			{
				setName("baloon");
				setOpaque(false);
				setBorder(new EmptyBorder(20, 9, 3, 9));

				baloonTextArea = new JTextArea(message) {
					@Override
					public void paintComponent(Graphics g) {
						Graphics2D g2D = (Graphics2D) g;
						Registry.render(g2D);
						g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.35f));
						g2D.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
						
						g2D.setColor(color);
						g2D.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
						
						g2D.setColor(Color.DARK_GRAY);
						g2D.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
						
						super.paintComponent(g2D);
					}
					
					{
//						setOpaque(false);
						setBorder(new EmptyBorder(3, 6, 0, 6));
						setBackground(new Color(0,0,0,0));
						setForeground(Color.WHITE);
						
						setLineWrap(true);
						setWrapStyleWord(true);
					
						setEditable(false);						
						setFont(Registry.fMessage);						
					}
				};

				add(baloonTextArea, BorderLayout.CENTER);
				add(new JLabel(format.format(System.currentTimeMillis())) {{setHorizontalAlignment(RIGHT); setFont(Registry.fLabels);}}, BorderLayout.SOUTH);
			}
		};
	
		add(baloonPane, type == messageType.INPUT ? BorderLayout.EAST : BorderLayout.WEST);
	}


	public String getHeaderText() {return "От: " + from + " кому" + to;}
}