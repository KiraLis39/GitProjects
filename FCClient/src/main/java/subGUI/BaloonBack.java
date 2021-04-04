package subGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import door.Message.MessageDTO;
import door.Message.MessageDTO.GlobalMessageType;
import fox.adds.IOM;
import fox.builders.FoxFontBuilder;
import net.NetConnector.localMessageType;
import registry.IOMs;
import registry.Registry;


@SuppressWarnings("serial")
public class BaloonBack extends JPanel {
	private static SimpleDateFormat format = new SimpleDateFormat("(dd.MM HH:mm:ss)"); // "dd.MM.yyyy HH:mm:ss"
	private static Graphics2D g2D;
	
	private JTextArea baloonTextArea;
	private Baloon baloon;
	private String header;
	private GridBagConstraints outGBC, incomeGBC, otherGBC;

	
	@Override
	public void paintComponent(Graphics g) {
		if (IOM.getBoolean(IOM.HEADERS.CONFIG, IOMs.CONFIG.DEBUG_GRAPHICS)) {
			g2D = (Graphics2D) g;
			g2D.setColor(getBackground());
			g2D.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
		}
	}
	
	public BaloonBack(localMessageType inputOutput, GlobalMessageType globalType, String from, String to, String body,	String footer) {
		setLayout(new GridBagLayout());
		setBackground(new Color(0.0f, 0.0f, 0.1f, 0.25f));
		
		baloon = new Baloon(inputOutput, from, to, body, footer);
		

		outGBC = new GridBagConstraints() {
			{
				this.insets = new Insets(0, 0, 0, 60);
				this.anchor = GridBagConstraints.WEST;
				this.fill = GridBagConstraints.NONE;

				this.weightx = 1;	
			}
		};
		
		incomeGBC = new GridBagConstraints() {
			{
				this.anchor = GridBagConstraints.EAST;
				this.fill = GridBagConstraints.NONE;
				
				this.weightx = 1;
			}
		};
		
		otherGBC = new GridBagConstraints() {
			{
				this.anchor = GridBagConstraints.CENTER;
				this.fill = GridBagConstraints.BOTH;
				this.weightx = 1;
			}
		};
		
		if (inputOutput == localMessageType.OUTPUT) {add(baloon, outGBC);
		} else if (inputOutput == localMessageType.INPUT) {add(baloon, incomeGBC);
		} else {add(baloon, otherGBC);}
	}
	
	public BaloonBack(localMessageType inputOutput, MessageDTO mesDTO) {
		setLayout(new GridBagLayout());
		setBackground(new Color(0.0f, 0.0f, 0.1f, 0.25f));

		if (mesDTO.getTo() == null) {mesDTO.setTo("Всем");}
		
		baloon = new Baloon(inputOutput, mesDTO.getFrom(), mesDTO.getTo(), mesDTO.getBody());
		
		outGBC = new GridBagConstraints() {
			{
				this.insets = new Insets(0, 0, 0, 60);
				this.anchor = GridBagConstraints.WEST;
				this.fill = GridBagConstraints.NONE;

				this.weightx = 1;	
			}
		};
		
		incomeGBC = new GridBagConstraints() {
			{
				this.anchor = GridBagConstraints.EAST;
				this.fill = GridBagConstraints.NONE;
				
				this.weightx = 1;
			}
		};
		
		otherGBC = new GridBagConstraints() {
			{
				this.anchor = GridBagConstraints.CENTER;
				this.fill = GridBagConstraints.BOTH;
				this.weightx = 1;
			}
		};
		
		if (inputOutput == localMessageType.OUTPUT) {add(baloon, outGBC);
		} else if (inputOutput == localMessageType.INPUT) {add(baloon, incomeGBC);
		} else {add(baloon, otherGBC);}
	}

	public class Baloon extends JPanel {
		private final int LAYOUT_SPACING = 3;
		private final String from, body, to, strStart;
		private final JLabel downDataLabel;
		private final Color color;
		private final Color mesColSystem = new Color(1.0f, 0.35f, 0.0f, 0.6f);
		private final Color mesColOutput = new Color(0.0f, 0.75f, 0.75f, 0.6f);
		private final Color mesColInput = new Color(0.25f, 0.75f, 0.0f, 0.6f);
		private final Color mesColWarn = new Color(1.0f, 0.0f, 0.0f, 0.6f);
		
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
			g2D.drawString(to, (int) (10D + FoxFontBuilder.getStringWidth(g2D, strStart)) - 1, 17);
			g2D.setColor(Color.WHITE);
			g2D.drawString(to, (int) (10D + FoxFontBuilder.getStringWidth(g2D, strStart)), 16);
		}
		
		public Baloon(localMessageType type, String _from, String _to, String _body) {
			this(type, _from, _to, _body, null);
		}
		
		public Baloon(localMessageType type, String _from, String _to, String _body, String footer) {
			switch (type) {
				case OUTPUT: color = mesColOutput;
					break;					
				case INPUT: color = mesColInput;
					break;				
				case INFO: color = mesColSystem;
					break;				
				case WARN: color = mesColWarn;
					break;			
				default: color = Color.GRAY;
			}
			
			this.from = _from;
			this.body = _body;
			this.to = _to;
			
			this.strStart = " От: " + from + " кому ";
			header = strStart + to + " ";
			
			setOpaque(false);
			setBorder(new EmptyBorder(20, 9, 3, 9));
			
			setLayout(new BorderLayout(LAYOUT_SPACING, LAYOUT_SPACING));			
			
			baloonTextArea = new JTextArea(body) {
				@Override
				public void paintComponent(Graphics g) {
					g2D = (Graphics2D) g;
					Registry.render(g2D);
					
					g2D.setFont(getFont());
					g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.35f));
					g2D.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
					
					g2D.setColor(color);
					g2D.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
					
					g2D.setColor(Color.DARK_GRAY);
					g2D.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
					
					super.paintComponent(g2D);
				}
				
				{
					setBorder(new EmptyBorder(3, 6, 3, 6));
					setBackground(new Color(0,0,0,0));
					setForeground(Color.WHITE);
					
					setLineWrap(true);
					setWrapStyleWord(true);
				
					setEditable(false);						
					setFont(Registry.fMessage);
				}
			};
			
			downDataLabel = new JLabel(footer == null ? format.format(System.currentTimeMillis()) : footer) {
				{
					setHorizontalAlignment(RIGHT);
					setFont(Registry.fLabels);
				}
			};
			
			add(baloonTextArea, BorderLayout.CENTER);
			add(downDataLabel, BorderLayout.SOUTH);
		}

		public JTextArea getArea() {return baloonTextArea;}
		
		public String getHeaderText() {return header;}
		public String getAreaText() {return body;}
		public String getFooterText() {return downDataLabel.getText();}
		
		public JLabel getDataLabel() {return downDataLabel;}

		public int getVerticalShiftsSum() {
			return getBorder().getBorderInsets(baloon).top + getBorder().getBorderInsets(baloon).bottom 
					+ baloonTextArea.getBorder().getBorderInsets(baloonTextArea).top + baloonTextArea.getBorder().getBorderInsets(baloonTextArea).bottom
					+ LAYOUT_SPACING
					+ downDataLabel.getHeight();
		}

	}

	public Baloon getBaloon() {return baloon;}
}