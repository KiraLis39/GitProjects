package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import base.Registry;
import door.Exit;
import fox.adds.IOM;


@SuppressWarnings("serial")
public class NewUserPane extends JDialog implements ActionListener, FocusListener {
	private JRadioButton rbMale, rbFemale;
	private JButton compliteButton, abortButton;
	private JPanel maleFemale, basePane;
	private JButton levelButton01, levelButton02, levelButton03, levelButton04;
	private JTextField T01;
	
	private Font labelFont0 = Registry.ffb.setFoxFont(12, 28, true);
	private Font subLabelFont = Registry.ffb.setFoxFont(8, 22, true);
	private Font buttonsFont = Registry.ffb.setFoxFont(12, 24, true);
	private Font textFont = Registry.ffb.setFoxFont(11, 20, true);
	
	private String level = "lvl0", userName;
	
	
	public NewUserPane() {
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		setModal(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setBackground(Color.DARK_GRAY);
		
		setLayout(new BorderLayout());
		
			JPanel upPanel = new JPanel(new FlowLayout()) {
				{
					setBorder(new EmptyBorder(6, 90, 0, 90));
					setBackground(Color.DARK_GRAY);
			
						JLabel upLabel = new JLabel("Укажи данные своего персонажа:");
						upLabel.setForeground(Color.WHITE);
						upLabel.setFont(labelFont0);
			
					add(upLabel);
				}
			};

			JPanel basePanel = new JPanel() {
				{
					setBorder(new EmptyBorder(0, 15, 6, 15));
					setLayout(new GridLayout(3, 0, 9, 9));
					setBackground(Color.DARK_GRAY);
			
					T01 = new JTextField() {
						{
							setName("nameF");
							setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1), "Имя:", 0, 2, subLabelFont, Color.YELLOW));
							setCursor(new Cursor(Cursor.HAND_CURSOR));
							setFont(textFont);
							setText(IOM.getString(IOM.HEADERS.LAST_USER, "LAST"));
							setBackground(new Color(0,0,0,0));
							setHorizontalAlignment(0);
							addFocusListener(NewUserPane.this);
						}
					};

					maleFemale = new JPanel() {
						{
							setName("maleFemale");
							setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1), "Пол:", 0, 2, subLabelFont, Color.YELLOW));
							setLayout(new GridLayout(1, 2, 0, 6));
							setBackground(Color.DARK_GRAY);

								rbMale = new JRadioButton() {
									{
										setBorder(new EmptyBorder(-10, 10, 0, 0));
										setHorizontalAlignment(JRadioButton.CENTER);
										setFont(textFont);
										setCursor(new Cursor(Cursor.HAND_CURSOR));
										setFocusPainted(false);
										setText("Парень");
										setSelected(false);
										
										addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent arg0) {
												rbMale.setSelected(true);
												rbFemale.setSelected(false);
												maleFemale.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1), "Пол:", 0, 2, subLabelFont, Color.YELLOW));
											}
										});
									}
								};
				
								rbFemale = new JRadioButton() {
									{
										setBorder(new EmptyBorder(-10, 10, 0, 0));
										setHorizontalAlignment(0);
										setFont(textFont);
										setCursor(new Cursor(Cursor.HAND_CURSOR));
										setFocusPainted(false);
										setText("Девушка");
										setSelected(false);
										
										addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent e) {
												rbMale.setSelected(false);
												rbFemale.setSelected(true);
//												maleFemale.setBackground(Color.MAGENTA);
												maleFemale.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.MAGENTA, 1), "Пол:", 0, 2, subLabelFont, Color.YELLOW));
											}
										});
									}
								};
								
							add(rbMale);
							add(rbFemale);
							
							addFocusListener(NewUserPane.this);
						}
					};
						
					basePane = new JPanel(new GridLayout(1,4,0,10)) {
						{
							setName("basePane");
							setFocusable(true);
							setBackground(Color.DARK_GRAY);
							setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1), "Сложность:", 0, 2, subLabelFont, Color.YELLOW));
							
							levelButton01 = new JButton("Лёгкий уровень сложн.");
//							levelButton01.setBackground(new Color(100,255,255));
//							levelButton01.setIcon(Library.levelChoiseSimpleButton);
//							levelButton01.setRolloverIcon(Library.levelChoiseSimpleButton_rollover);
//							levelButton01.setPressedIcon(Library.levelChoiseSimpleButton_pressed);
							levelButton01.setActionCommand("lvl0");
							levelButton01.addActionListener(NewUserPane.this);
							
							levelButton02 = new JButton("Средний уровень сложн.");
//							levelButton02.setBackground(new Color(100,255,100));
//							levelButton02.setIcon(Library.levelChoiseMiddleButton);
//							levelButton02.setRolloverIcon(Library.levelChoiseMiddleButton_rollover);
//							levelButton02.setPressedIcon(Library.levelChoiseMiddleButton_pressed);
							levelButton02.setActionCommand("lvl1");
							levelButton02.addActionListener(NewUserPane.this);
							
							levelButton03 = new JButton("Тяжелый уровень сложн.");
//							levelButton03.setBackground(new Color(255,255,100));
//							levelButton03.setIcon(Library.levelChoiseHardButton);
//							levelButton03.setRolloverIcon(Library.levelChoiseHardButton_rollover);
//							levelButton03.setPressedIcon(Library.levelChoiseHardButton_pressed);
							levelButton03.setActionCommand("lvl2");
							levelButton03.addActionListener(NewUserPane.this);
							
							levelButton04 = new JButton("Адский уровень сложн.");
//							levelButton04.setBackground(new Color(255,100,100));
//							levelButton04.setIcon(Library.levelChoiseExtreamButton);
//							levelButton04.setRolloverIcon(Library.levelChoiseExtreamButton_rollover);
//							levelButton04.setPressedIcon(Library.levelChoiseExtreamButton_pressed);
							levelButton04.setActionCommand("lvl3");
							levelButton04.addActionListener(NewUserPane.this);
							
							add(levelButton01);	add(levelButton02);
							add(levelButton03);	add(levelButton04);
							
							addFocusListener(NewUserPane.this);
						}
					};
					
					add(T01);
					add(maleFemale);
					add(basePane);
				}
			};
			
			JPanel downPane = new JPanel(new BorderLayout()) {
				{
					setBorder(new EmptyBorder(9, 6, 6, 6));
					setBackground(Color.DARK_GRAY);
					
						compliteButton = new JButton("Готово!") {
							{
								setBackground(Color.DARK_GRAY);
//								setForeground(Color.GREEN);
								setFont(buttonsFont);
								setActionCommand("acomplish");
								addActionListener(NewUserPane.this);
							}
						};

						abortButton = new JButton("Отмена") {
							{
								setBackground(Color.DARK_GRAY.darker());
//								setForeground(Color.GREEN);
								setFont(buttonsFont);
								setActionCommand("abort");
								addActionListener(NewUserPane.this);
							}
						};
						
					add(compliteButton, BorderLayout.CENTER);
					add(abortButton, BorderLayout.AFTER_LINE_ENDS);
				}
			};

		add(upPanel, BorderLayout.NORTH);
		add(basePanel, BorderLayout.CENTER);
		add(downPane, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		T01.setSelectionStart(0);
		T01.setSelectionEnd(T01.getText().length()-1);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "lvl0": 
			colorSetter("lvl0");
			level = "light";
			break;
			
		case "lvl1": 
			colorSetter("lvl1");
			level = "med";
			break;
			
		case "lvl2": 
			colorSetter("lvl2");
			level = "hard";
			break;
			
		case "lvl3": 
			colorSetter("lvl3");
			level = "extrahard";
			break;
			
			
		case "acomplish": 
			if (T01.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Не введен ник персонажа!", "Ошибка:", JOptionPane.ERROR_MESSAGE);
			} else if (!rbMale.isSelected() && !rbFemale.isSelected()) {
				JOptionPane.showMessageDialog(null, "Укажи пол персонажа.", "Ошибка:", JOptionPane.ERROR_MESSAGE);
			}  else if (level.equals("none")) {
				JOptionPane.showMessageDialog(null, "Выбери уровень сложности.", "Ошибка:", JOptionPane.ERROR_MESSAGE);
			} else {
				userName = T01.getText() + ":" + rbMale.isSelected();
//				System.out.println("PSex setted up to: " + IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX));
//				if (IOM.getString(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX).equals("none")) {IOM.set(IOM.HEADERS.USER_SAVE, IOMsave.PLAYERS_SEX, "MALE");}
				dispose();
			}
			break;
			
		case "abort": Exit.exit(2);
			break;
		default:
		}
	}

	private void colorSetter(String aCom) {
		JButton[] jbm = new JButton[4];
		jbm[0] = levelButton01;
		jbm[1] = levelButton02;
		jbm[2] = levelButton03;
		jbm[3] = levelButton04;
		
		for (int i = 0; i < jbm.length; i++) {
			if (!jbm[i].getActionCommand().equals(aCom)) {jbm[i].setBackground(null);
			} else {jbm[i].setBackground(Color.YELLOW);}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			System.out.println("Field focus gained");
			JTextField tmp = (JTextField) e.getSource();
			tmp.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.YELLOW, 1), "Имя:", 0, 2, subLabelFont, Color.YELLOW));
			tmp.setForeground(Color.GRAY);
			
			if (((JTextField)e.getSource()).getName().equals("nameF")) {
				T01.selectAll();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			JTextField tmp = (JTextField) e.getSource();
			tmp.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLUE, 1), "Имя:", 0, 2, subLabelFont, Color.YELLOW));
			tmp.setForeground(Color.GRAY);
		}
	}

	
	public String getNewUser() {return userName;}
}