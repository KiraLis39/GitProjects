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

import door.Message.MessageDTO;
import fox.builders.FoxFontBuilder;
import net.NetConnector.localMessageType;
import registry.Registry;


@SuppressWarnings("serial")
public class BaloonPane extends JPanel {
	private SimpleDateFormat format = new SimpleDateFormat("(dd.MM HH:mm:ss)"); // "dd.MM.yyyy HH:mm:ss"
	private JTextArea baloonTextArea;
	private Baloon baloon;
	private String header;
	private static Graphics2D g2D;
	
	
	@Override
	public void paintComponent(Graphics g) {
//		g2D = (Graphics2D) g;
//		g2D.setColor(getBackground());
//		g2D.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
	}
	
	public BaloonPane(final localMessageType inputOutput, MessageDTO mesDTO, final Color color) {
//		System.out.println("A new baloon pane with: (" + type + ") " + message + "; from = " + from + "; to = " + to + ".");		
		setLayout(new BorderLayout());
//		setOpaque(false);
		setBackground(new Color(0.0f, 0.0f, 0.1f, 0.5f));
//		setPreferredSize(new Dimension(freeSpaceDim.width, freeSpaceDim.height));

		if (mesDTO.getTo() == null) {mesDTO.setTo("Всем");}
		
		baloon = new Baloon(color, mesDTO.getFrom(), mesDTO.getTo(), mesDTO.getBody());
	
		add(baloon, inputOutput == localMessageType.INPUT ? BorderLayout.EAST : BorderLayout.WEST);
	}
	
	public class Baloon extends JPanel {
		private static final int LAYOUT_SPACING = 3;
		Color color;
		String from, body, to, strStart;
		JLabel downDataLabel;
		
		@Override
		public void paintComponent(Graphics g) {
			g2D = (Graphics2D) g;
			Registry.render(g2D);
			g2D.setColor(color);
			g2D.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

			g2D.setColor(color);
			g2D.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);

			g2D.setColor(Color.BLACK);
			g2D.setFont(Registry.fLabels);
			g2D.drawString(strStart, 8, 16);
			
			g2D.setColor(Color.BLACK);
			g2D.drawString(to, (int) (11D + FoxFontBuilder.getStringWidth(g2D, strStart)) - 1, 17);
			g2D.setColor(Color.WHITE);
			g2D.drawString(to, (int) (11D + FoxFontBuilder.getStringWidth(g2D, strStart)), 16);
		}
		
		public Baloon(Color _color, String _from, String _to, String _body) {
			color = _color;
			from = _from;
			body = _body;
			to = _to;
			
			strStart = " От: " + from + " кому";
			header = strStart + " " + to + " ";
			
			setOpaque(false);
			setLayout(new BorderLayout(LAYOUT_SPACING, LAYOUT_SPACING));
			setBorder(new EmptyBorder(20, 9, 3, 9));
			
			baloonTextArea = new JTextArea(body) {
				@Override
				public void paintComponent(Graphics g) {
					g2D = (Graphics2D) g;
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
//					setPreferredSize(new Dimension(freeSpaceDim.width, baloonTextArea.getPreferredSize().height));

					setBorder(new EmptyBorder(3, 6, 0, 6));
					setBackground(new Color(0,0,0,0));
					setForeground(Color.WHITE);
					
					setLineWrap(true);
					setWrapStyleWord(true);
				
					setEditable(false);						
					setFont(Registry.fMessage);						
				}
			};
			
			downDataLabel = new JLabel(format.format(System.currentTimeMillis())) {
				{
					setHorizontalAlignment(RIGHT);
					setFont(Registry.fLabels);
				}
			};
			
			add(baloonTextArea, BorderLayout.CENTER);
			add(downDataLabel, BorderLayout.SOUTH);
		}
		
		public JTextArea getArea() {return baloonTextArea;}
		public String getAreaText() {return body;}		
		public String getHeaderText() {return header;}

		public int getVerticalShiftsSum() {
			return getBorder().getBorderInsets(baloon).top + getBorder().getBorderInsets(baloon).bottom 
					+ baloonTextArea.getBorder().getBorderInsets(baloonTextArea).top + baloonTextArea.getBorder().getBorderInsets(baloonTextArea).bottom
					+ LAYOUT_SPACING
					+ downDataLabel.getHeight();
		}		
	}

	public Baloon getBaloon() {return baloon;}
}