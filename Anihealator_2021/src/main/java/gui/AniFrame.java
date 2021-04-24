package gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import adds.InputAction;
import adds.Out;
import builders.FoxFontBuilder;
import components.VerticalFlowLayout;
import door.MainClass;
import engine.DataBase;
import engine.NewDataItem;
import games.FoxSpritesCombiner;
import registry.Registry;
import subGUI.ItemCard;


@SuppressWarnings("serial")
public class AniFrame extends JFrame implements ActionListener, WindowListener, ComponentListener {	
	public static RenderingHints d2DRender;
	
	private static Font f2b = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL, 18, false);
	private Font f3 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 24, true);
	
	private Color color0 = new Color(0.95f, 1.0f, 1.0f);
	private Color color1 = new Color(0.4f, 0.6f, 0.7f);
	private Color color2 = new Color(0.6f, 0.8f, 0.9f);
			
	private static JPanel leftPane, midPane, onScrollPane;
	private JTextField seatchField;
	private JButton adminButton;
	
	private BufferedImage backImage;
	private static BufferedImage[] buttons;
	private static Graphics2D g2D;
	private static AniFrame af;
	private static JScrollPane listScroll;
	
	private static String typeName, linaHelp = "Разработано совместно с ветклиникой ''Бион''";
	private static int midButtonsHeight = 32;
	private static ArrayList<String> resultList, types;
	
	
	public AniFrame() {
		try {setIconImage(new ImageIcon(ImageIO.read(new File("./res/pic/title.png"))).getImage());
		} catch (IOException e) {/* IGNORE ICON ABSENT */}
		af = this;
		
		preinit();
		
		setTitle(Registry.progName + " " + Registry.verse);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(Registry.isResizeAllow);
		setMinimumSize(new Dimension(Registry.frameWidth, Registry.frameHeight));
		
		JPanel upPane = new JPanel(new BorderLayout(6, 6)) {
			{
				setBackground(color2);
				setBorder(new EmptyBorder(6, 3, 6, 0));
				
				seatchField = new JTextField() {
					{
						setFont(f3);
						setBackground(color0);
						addKeyListener(new KeyAdapter() {
							@Override
							public void keyReleased(KeyEvent e) {
								searchingThe(getText().trim());
							}
						});
					}
				};
				
				JPanel buttonsPane = new JPanel(new FlowLayout(1, 3, 0)) {
					{
						setOpaque(false);
						setFocusable(true);
						
						JButton searchBut = new JButton("Найти") {
							{
								setPreferredSize(new Dimension(128, 48));
								setFont(f3);
								setBackground(color1);
								setForeground(Color.WHITE);
								setFocusPainted(false);
								setActionCommand("search");
								addActionListener(AniFrame.this);
							}
						};

						JButton resetBut = new JButton("Сбросить") {
							{
								setPreferredSize(new Dimension(128, 48));
								setFont(f3);
								setBackground(color1);
								setForeground(Color.WHITE);
								setFocusPainted(false);
								setActionCommand("reset");
								addActionListener(AniFrame.this);
							}
						};
						
						adminButton = new JButton("+") {
							{
								setBackground(Color.GREEN);
								setForeground(Color.WHITE);
								setFont(f2b);
								setVisible(false);
								
								setActionCommand("admin");
								addActionListener(AniFrame.this);
							}
						};

						add(searchBut);
						add(resetBut);
						add(adminButton);
					}
				};
				
				add(seatchField, BorderLayout.CENTER);
				add(buttonsPane, BorderLayout.EAST);
			}
		};
		
		JPanel baseMidPane = new JPanel(new BorderLayout()) {
			{
				setBackground(color2);

				midPane = new JPanel() {
					@Override
					public void paintComponent(Graphics g) {
						g2D = (Graphics2D) g;
						g2D.setColor(color0);
						g2D.fillRect(0, 0, getWidth(), getHeight());
						if (backImage != null) {
							g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
//							g2D.drawImage(backImage, 32, getHeight() - backImage.getHeight() - 32, backImage.getWidth(), backImage.getHeight(), this);
							g2D.drawImage(backImage, 0, 0, getWidth(), getHeight(), this);
							g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
						}
						
						g2D.setColor(color1);
						g2D.drawString(linaHelp, (int) (getWidth() - FoxFontBuilder.getStringBounds(g2D, linaHelp).getWidth() - 8), getHeight() - 8);
					}
					
					{
						setLayout(new BorderLayout());
						
						onScrollPane = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 3, 3)) {{setBackground(color0);}};
						
						listScroll = new JScrollPane(onScrollPane) {
							{
								setBorder(null);
								setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
								setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
								getVerticalScrollBar().setUnitIncrement(midButtonsHeight / 2);
							}
						};
						
						addComponentListener(AniFrame.this);
					}
				};
				
				leftPane = new JPanel(new FlowLayout(1, 3, 3)) {
					{
						setBackground(color2);
						setPreferredSize(new Dimension(192, 0));
					}
				};
				
				add(midPane, BorderLayout.CENTER);
				add(leftPane, BorderLayout.WEST);
			}
		};

		add(upPane, BorderLayout.NORTH);
		add(baseMidPane, BorderLayout.CENTER);

		addWindowListener(this);
		
		inAcBuild();
		Registry.db.load();
		
		if (MainClass.disclaimerThread.isAlive()) {
			Out.Print(AniFrame.class, Out.LEVEL.DEBUG, "Загрузка завершена! Ожидание закрытия дисклеймера пользователем...");
			try {MainClass.disclaimerThread.join();
		} catch (InterruptedException e1) {}}
		
		Out.Print(AniFrame.class, Out.LEVEL.INFO, "Программа запущена и готова к работе.");
		setLocationRelativeTo(null);
		setVisible(true);
		
		reloadLeftPanel();
	}

	private void preinit() {
		Registry.db = new DataBase();
		
		d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
		d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		
		BufferedImage buttonsImage;
		try {
			buttonsImage = ImageIO.read(new File("./res/pic/butList.png"));
			buttons = FoxSpritesCombiner.addSpritelist("buttons", buttonsImage, 1, 2);
		} catch (IOException e1) {
			Out.Print(AniFrame.class, Out.LEVEL.WARN, "Спрайтлист кнопок не может быть прочитан.");
			e1.printStackTrace();
		}
		
		try {
			backImage = ImageIO.read(new File("./res/pic/bim.png"));
		} catch (IOException e) {
			Out.Print(AniFrame.class, Out.LEVEL.WARN, "Водяной знак не может быть прочитан.");
			e.printStackTrace();
		}
	}

	private void inAcBuild() {
		InputAction.add("frame", this);
		InputAction.set("frame", "adminSwitch", KeyEvent.VK_BACK_QUOTE, 1, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Registry.isAdminModeAllow) {
					if (!Registry.isAdminModeOn) {
						String pass = JOptionPane.showInputDialog(AniFrame.this, "Enter pass:", "Security:", JOptionPane.WARNING_MESSAGE);
						if (pass == null || pass.equals("") || !pass.equals(Registry.verse.replace(".", "") + "lina")) {return;}						
						Registry.isAdminModeOn = true;
					}
					
					Out.Print(AniFrame.class, Out.LEVEL.ACCENT, "Вход в Админ-панель!");
					new NewDataItem(AniFrame.this, DataBase.getTypeList());
					if (!seatchField.getText().isBlank()) {seatchField.setText(seatchField.getText().substring(0, seatchField.getText().length() - 1));}
				}
			}
		});
	}

	
	public static void reloadLeftPanel() {
		types = DataBase.getTypeList();
		if (types == null) {return;}

		leftPane.removeAll();
		
		for (int i = 0; i < types.size(); i++) {
			typeName = types.get(i);
			leftPane.add(
					new JButton() {
						BufferedImage bImage = buttons[0];
						
						@Override
						public void paintComponent(Graphics g) {
							if (buttons != null) {
								g2D = (Graphics2D) g;
								if (Registry.isRenderOn) {g2D.addRenderingHints(d2DRender);}
								g2D.drawImage(bImage, 0, 0, getWidth(), getHeight(), null, null);
								g2D.drawString(getName(), (int) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g, getName()).getWidth() / 2D), getHeight() / 2 + 6);
								g2D.dispose();
							} else {super.paintComponent(g);}
						};
						
						{
							setName(typeName);
							setText(typeName);
							setPreferredSize(new Dimension(190, 40));
							setFont(f2b);
							setForeground(Color.BLACK);
							
							setActionCommand("show_" + typeName);
							addActionListener(af);
							addMouseListener(new MouseAdapter() {
						         public void mouseEntered(MouseEvent me) {
						        	 bImage = buttons[1];
						        	 repaint();
						         }
						         public void mouseExited(MouseEvent me) {
						        	 bImage = buttons[0];
						        	 repaint();
						         }
						      });
						}
					}
			);
		}
		
		types.clear();
		types = null;
		leftPane.revalidate();
	}
	
	private void reloadMidPanel(String typeChosen) {
		midPane.removeAll();
		midPane.setLayout(new BorderLayout());
		if (onScrollPane != null) {onScrollPane.removeAll();}

		resultList = new ArrayList<String>();
		
		Out.Print(getClass(), Out.LEVEL.DEBUG, "Requested to db ElementsOfType");
		ArrayList<String> tElem = DataBase.getElementsOfType(typeChosen);
		Out.Print(getClass(), Out.LEVEL.DEBUG, "ElementsOfType`s size: " + tElem.size());
		for (String elem : tElem) {
			resultList.add(elem);
		}
		
		Collections.sort(resultList);
//		resultList.sort(Comparator.naturalOrder());
		
		for (int i = 0; i < resultList.size(); i++) {
			typeName = resultList.get(i);
			
//			System.out.println("Adding to midPane a button: " + typeName);
			onScrollPane.add(
					new JButton(typeName) {
						{
							setFont(f2b);
							setBackground(color0);
							setForeground(Color.BLACK);
//							setFocusPainted(false);
							setBorderPainted(false);
							setToolTipText(typeName);
							setMinimumSize(new Dimension(150, 60));
							setPreferredSize(new Dimension(250, 90));
							
							setActionCommand("chose_" + typeName);
							addActionListener(AniFrame.this);
						}
					}
			);
		}
		
		updateMidButtons();
		midPane.add(listScroll);
	}
	
	private void searchingThe(String searchText) {
		if (searchText.equals("")) {
			updateFinderTo(null);
			return;
		}
		
		searchText = searchText.toLowerCase();
		ArrayList<String> resultList = new ArrayList<String>();		
		for (String aidName : DataBase.getIndexKeySet()) {
			if(aidName.toLowerCase().lastIndexOf(searchText) != -1) {resultList.add(aidName);}
		}
		
		updateFinderTo(resultList);
	}

	private void updateFinderTo(ArrayList<String> resultList) {
		midPane.removeAll();
		midPane.repaint();
		
		if (resultList != null) {
			midPane.setLayout(new FlowLayout(1, 3, 3));

			for (String findedItem : resultList) {
				midPane.add(new JButton(findedItem) {
					{
						setFont(f2b);
						setBackground(color0);
						setForeground(Color.BLACK);
						setFocusPainted(false);
						setBorderPainted(false);
						
						setActionCommand("chose_" + findedItem);
						addActionListener(AniFrame.this);
					}
				});
			}
			
			midPane.revalidate();
		}
	}

	private void updateMidButtons() {
		int a = 0;
		if (resultList !=null) {
			onScrollPane.setPreferredSize(new Dimension(midPane.getWidth() - 15, midButtonsHeight * resultList.size() + 3));
			a = AniFrame.this.getWidth() - leftPane.getWidth() - (midButtonsHeight * resultList.size() > midPane.getHeight() ? 32 : 20);
		}
		
		for (Component c : onScrollPane.getComponents()) {
			if (c instanceof JButton) {c.setPreferredSize(new Dimension(a, midButtonsHeight));}
		}

		onScrollPane.revalidate();
		midPane.revalidate();
		midPane.repaint();
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("show_")) {
			Out.Print(AniFrame.class, Out.LEVEL.DEBUG, "Pressed the type-button " + e.getActionCommand().replace("show_", ""));
			reloadMidPanel(e.getActionCommand().replace("show_", ""));
		}
		
		if (e.getActionCommand().startsWith("chose_")) {
			midPane.removeAll();
			midPane.setLayout(new BorderLayout());
			
			String chosenItem = e.getActionCommand().replace("chose_", "");
			Out.Print(getClass(), Out.LEVEL.DEBUG, "Pressed the " + chosenItem);

			midPane.add(new ItemCard(DataBase.getElement(chosenItem)));
			
			midPane.repaint();
			midPane.revalidate();
		}
		
		if (e.getActionCommand().equals("search")) {
			searchingThe(seatchField.getText().trim());
		}
		
		if (e.getActionCommand().equals("reset")) {
			seatchField.setText("");
			updateFinderTo(null);
			reloadLeftPanel();
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {Registry.db.CloseDB();
		} catch (ClassNotFoundException e1) {
			Out.Print(AniFrame.class, Out.LEVEL.ERROR, e1);
			e1.printStackTrace();
		} catch (SQLException e1) {
			Out.Print(AniFrame.class, Out.LEVEL.ERROR, e1);
			e1.printStackTrace();
		}
		
		MainClass.exit(0);
	}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {updateMidButtons();}

	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
}