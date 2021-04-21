package subgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import base.Registry;
import gui.GameFrame;


@SuppressWarnings("serial")
public class GamesSettingsPane extends JDialog implements ItemListener, ActionListener, ChangeListener {
	private Font f1 = Registry.ffb.setFoxFont(3, 26, true);
	
	
	public GamesSettingsPane(GameFrame owner, String title, boolean modal) {
		super(owner, title, modal);
		
		setBackground(Color.GRAY.darker());
		setForeground(Color.GREEN.brighter());
		setLayout(new BorderLayout());

		JPanel upOptions = new JPanel() {
			{
				setBorder(BorderFactory.createRaisedBevelBorder());
				setBackground(Color.DARK_GRAY);
//				setOpaque(true);
				
				JLabel optionLabel = new JLabel("ОПЦИИ ИГРЫ:") {
					{
						setFont(f1);
						setForeground(Color.YELLOW);
						setHorizontalAlignment(0);
//						setOpaque(true);
					}
				};
			
				add(optionLabel);
			}
		};
		
		JPanel downOptions = new JPanel(new BorderLayout()) {
			{
				setOpaque(true);
				setBorder(new EmptyBorder(0,0,0,0));
				
				JButton exitOption = new JButton("Применить и закрыть") {
					{
						setIconTextGap(0);
						setFocusPainted(false);
//						setFont(ffb.setFoxFont(0, true, 22));
						setBackground(Color.GRAY);
						setForeground(Color.BLACK);
						setActionCommand("close");
						addActionListener(GamesSettingsPane.this);
					}
				};
			
				add(exitOption);
			}
		};
		
		add(upOptions, BorderLayout.NORTH);
		add(downOptions, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}


	public void actionPerformed(ActionEvent e) {}

	public void stateChanged(ChangeEvent e) {}

	public void itemStateChanged(ItemEvent e) {}
}