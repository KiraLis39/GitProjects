package door;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class SFrame extends JFrame {
	private BufferedImage spiteImage, srcImage;
	private BufferedImage[] spriteArray;
	private JPanel optionsPane, viewPane, downPane, sliderPane;
	private JCheckBox resizeByFrame, rotateImageReading;
	private JSpinner columnsSpin, rowsSpin;
	private JProgressBar progrBar;
	private Thread aniThread;
	private boolean isPlayed, isPause, isRotateRead, needUpdate; 
	private JSlider speedSlider;
	private JButton animationPauseButton;
	private JRadioButton whiteFon, blackFon;
	private static BufferedImage[] sArray;
	private static BufferedImage tmp;
	private static Graphics2D g2D;
	private int nextFrame;
	

	public SFrame() {
		setTitle("Sprite viewer");
		setPreferredSize(new Dimension(620, 420));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout());
		
		optionsPane = new JPanel(new BorderLayout(0, 0)) {
			{
//				setBackground(Color.DARK_GRAY);
				setBorder(new EmptyBorder(0, 6, 3, 6));
				
				JPanel boxesPane = new JPanel(new GridLayout(1, 3, 3, 3)) {
					{
						setOpaque(false);
						setBackground(Color.DARK_GRAY);
						setBorder(new EmptyBorder(-3, 0, -6, 0));
						
						resizeByFrame = new JCheckBox("По размеру окна", null, false) {
							{
								setFocusPainted(false);
								addItemListener(new ItemListener() {							
									@Override
									public void itemStateChanged(ItemEvent e) {
										checkFrameSize();
									}
								});
							}
						};
						
						rotateImageReading = new JCheckBox("Повернуть", null, false) {
							{
								setFocusPainted(false);
								addItemListener(new ItemListener() {							
									@Override
									public void itemStateChanged(ItemEvent e) {
										isRotateRead = isSelected();
									}
								});
							}
						};
						
						JPanel radioPane = new JPanel(new GridLayout(2, 1, 0, 0)) {
							{
								
								whiteFon = new JRadioButton("Белый") {
									{
										addItemListener(new ItemListener() {											
											@Override
											public void itemStateChanged(ItemEvent e) {
												if (isSelected()) {blackFon.setSelected(false);}
											}
										});
									}
								};
								blackFon = new JRadioButton("Черный") {
									{
										setSelected(true);
										addItemListener(new ItemListener() {											
											@Override
											public void itemStateChanged(ItemEvent e) {
												if (isSelected()) {whiteFon.setSelected(false);}
											}
										});
									}
								};
								
								add(whiteFon);
								add(blackFon);
							}
						};
						
						
						add(resizeByFrame);
						add(rotateImageReading);
						add(radioPane);
					}
				};
								
				JPanel spinnersPane = new JPanel() {
					{
//						setBackground(Color.GRAY);
						setBorder(new EmptyBorder(0, 0, -3, 0));
						
						JPanel spinnerColumnsPane = new JPanel() {
							{
								JLabel colLabel = new JLabel("Колонок:");
								
								columnsSpin = new JSpinner() {
									{
										setValue(1);
										addChangeListener(new ChangeListener() {									
											@Override
											public void stateChanged(ChangeEvent e) {
												if ((int) ((JSpinner) e.getSource()).getValue() <= 0) {((JSpinner) e.getSource()).setValue(1);}
											}
										});
									}
								};
								
								add(colLabel);
								add(columnsSpin, BorderLayout.EAST);
							}
						};
						
						JPanel spinnerRowsPane = new JPanel() {
							{
								JLabel rowLabel = new JLabel("Строк:");
								
								rowsSpin = new JSpinner() {
									{
										setValue(1);
										addChangeListener(new ChangeListener() {									
											@Override
											public void stateChanged(ChangeEvent e) {
												if ((int) ((JSpinner) e.getSource()).getValue() <= 0) {((JSpinner) e.getSource()).setValue(1);}
											}
										});
									}
								};
								
								add(rowLabel);
								add(rowsSpin, BorderLayout.EAST);
							}
						};
					
						add(spinnerColumnsPane);
						add(spinnerRowsPane);
					}
				};
				
				sliderPane = new JPanel(new BorderLayout()) {
					{
						setOpaque(false);
//						setBorder(BorderFactory.createCompoundBorder(
//								BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true), "Delay: " + (speedSlider.getValue() * 10) + " ms.", 1, 0, Font.getFont("Arial"), Color.BLACK),
//								new EmptyBorder(0, 0, 0, 0)
//								));
						
						speedSlider = new JSlider(0, 100, 50) {
							{
//								setValue(20);
								setPaintTicks(true);
//								setMajorTickSpacing(25);
								setMinorTickSpacing(2);
								setSnapToTicks(true);
								addChangeListener(new ChangeListener() {							
									@Override
									public void stateChanged(ChangeEvent e) {
										sliderPane.setBorder(BorderFactory.createCompoundBorder(
												BorderFactory.createTitledBorder(
														BorderFactory.createLineBorder(Color.GRAY, 1, true), "Delay: " + (getValue() * 10) + " ms.", 0, 2, Font.getFont("Arial"), Color.BLACK),
												new EmptyBorder(-3, 0, -6, 0)
												));
									}
								});
							}
						};
						
						add(speedSlider);
					}
				};
				
				add(boxesPane, BorderLayout.CENTER);
				add(spinnersPane, BorderLayout.EAST);
				add(sliderPane, BorderLayout.SOUTH);
			}
		};
		
		viewPane = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g2D = (Graphics2D) g;
				g2D.setColor(blackFon.isSelected() ? Color.BLACK : Color.WHITE);
				g2D.fillRect(0, 0, getWidth(), getHeight());
				
				if (spiteImage == null) {
					super.paintComponents(g2D);
					return;
				}
				
				if (resizeByFrame.isSelected()) {g2D.drawImage(spiteImage, 0, 0, getWidth(), getHeight(), this);
				} else {g2D.drawImage(spiteImage, getWidth() / 2 - spiteImage.getWidth() / 2, getHeight() / 2 - spiteImage.getHeight() / 2, spiteImage.getWidth(), spiteImage.getHeight(), this);}
			}
		};
		
		downPane = new JPanel(new BorderLayout(1, 1)) {
			{
//				setBackground(Color.DARK_GRAY);
				setBorder(new EmptyBorder(1, 0, 1, 0));
				
				JPanel downButtonsPane = new JPanel(new GridLayout(1, 4, 3, 3)) {
					{
//						setBackground(Color.DARK_GRAY);
						
						JButton openNewImageButton = new JButton("Открыть") {
							{
								setBackground(new Color(1.0f, 1.0f, 0.5f, 1.0f));
								setFocusPainted(false);
								addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										chooseImage();
									}
								});
							}
						};
						
						JButton animationPlayButton = new JButton("Играть") {
							{
								setBackground(new Color(0.5f, 1.0f, 0.5f, 1.0f));
								setFocusPainted(false);
								addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										if (spiteImage == null) {	return;}
										play();
									}
								});
							}
						};
						
						animationPauseButton = new JButton("Пауза") {
							{
								setBackground(new Color(0.5f, 0.5f, 1.0f, 1.0f));
								setFocusPainted(false);
								addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										if (spiteImage == null) {	return;}
										if (getText().equals("Пауза")) {
											pause();
											setText("Возобновить...");
										} else {
											play();
											setText("Пауза");
										}
									}
								});
							}
						};
						
						JButton animationStopButton = new JButton("Стоп") {
							{
								setBackground(new Color(1.0f, 0.5f, 0.5f, 1.0f));
								setFocusPainted(false);
								addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										stop();
									}
								});
							}
						};
						
						add(openNewImageButton);
						add(animationPlayButton);
						add(animationPauseButton);
						add(animationStopButton);
					}
				};
				
				JPanel downProgressPane = new JPanel(new BorderLayout()) {
					{
						setBackground(Color.DARK_GRAY);
						
						progrBar = new JProgressBar();
						
//						speedSlider = new JSlider(0, 100, 50);						
//						add(speedSlider);
						add(progrBar, BorderLayout.SOUTH);
					}
				};
				
				add(downButtonsPane);
				add(downProgressPane, BorderLayout.SOUTH);
			}
		};
		
		add(optionsPane, BorderLayout.NORTH);
		add(viewPane, BorderLayout.CENTER);
		add(downPane, BorderLayout.SOUTH);
		
		setInAc();
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		speedSlider.setValue(20);
	}

	private void setInAc() {
		((JComponent) SFrame.this.getContentPane())
			.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "frameNext");
		
		((JComponent) SFrame.this.getContentPane()).getActionMap().put("frameNext", new AbstractAction() {
			@Override	public void actionPerformed(ActionEvent e) {needUpdate = true;}
		});
		
		((JComponent) SFrame.this.getContentPane())
			.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "framePrev");
	
		((JComponent) SFrame.this.getContentPane())
			.getActionMap()
			.put("framePrev", new AbstractAction() {
			@Override	public void actionPerformed(ActionEvent e) {
				if (nextFrame >= 2) {nextFrame -= 2;}
				needUpdate = true;
			}
		});
		
		
		((JComponent) SFrame.this.getContentPane())
			.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "speedDown");
	
		((JComponent) SFrame.this.getContentPane())
			.getActionMap()
			.put("speedDown", new AbstractAction() {			
			@Override	public void actionPerformed(ActionEvent e) {
				if (speedSlider.getValue() > 0) {speedSlider.setValue(speedSlider.getValue() - 1);}
			}
		});
	
		((JComponent) SFrame.this.getContentPane())
		.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
		.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "speedUp");

		((JComponent) SFrame.this.getContentPane())
			.getActionMap()
			.put("speedUp", new AbstractAction() {			
			@Override	public void actionPerformed(ActionEvent e) {
				if (speedSlider.getValue() < speedSlider.getMaximum()) {speedSlider.setValue(speedSlider.getValue() + 1);}
			}
		});
	}

	private void play() {
		stop();
		if (spriteArray == null || ((int) columnsSpin.getValue() * (int) rowsSpin.getValue() != spriteArray.length)) {spriteArray = rebuildSptitesArray();}
		
		// запуск\возобновление потока анимации...
		aniThread = new Thread(new Runnable() {
			@Override
			public void run() {
				nextFrame = 0;
				isPlayed = true;
				
				while(isPlayed) {
					if (isPause && !needUpdate) {
						Thread.yield();
						continue;
					}
					
					spiteImage = spriteArray[nextFrame];
					progrBar.setValue(nextFrame + 1);
					nextFrame++;
					if (nextFrame >= spriteArray.length) {nextFrame = 0;}
					viewPane.repaint();
					
					if (!isPlayed) {break;
					} else if (!needUpdate) {
						try {Thread.sleep(speedSlider.getValue() * 10);
						} catch (InterruptedException e) {/* IGNORE SLEEPING */}
					}
					
					needUpdate = false;
				}
			}
		});
		aniThread.start();
	}
	
	private BufferedImage[] rebuildSptitesArray() {
//		System.out.println("Rebuilding spritesArray...");
		sArray = new BufferedImage[(int) columnsSpin.getValue() * (int) rowsSpin.getValue()];
		progrBar.setMaximum(sArray.length);
		srcImage = spiteImage;
		int spriteWidth = spiteImage.getWidth() / (int) columnsSpin.getValue();
		int spriteHeight = spiteImage.getHeight() / (int) rowsSpin.getValue();
		
		System.out.println("Нарезаем изображение на " + columnsSpin.getValue() + " колонок и " + rowsSpin.getValue() + " строк.");
		System.out.println("Лист размером: " + spiteImage.getWidth() + "х" + spiteImage.getHeight() + "; Спрайт: " + spriteWidth + "х" + spriteHeight);
		// нарезка spiteImage на спрайты и заливка в массив...
		int counter = 0;
		if (isRotateRead) {
			for (int i = 0; i < (int) columnsSpin.getValue(); i++) {
				for (int k = 0; k < (int) rowsSpin.getValue(); k++) {
					tmp = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
					g2D = tmp.createGraphics();
					
					g2D.drawImage(spiteImage, 
							0, 0, 
							spriteWidth, spriteHeight, 
							
							spriteWidth * i, spriteHeight * k, 
							spriteWidth * i + spriteWidth, spriteHeight * k + spriteHeight, 
							
							null);
					
					g2D.dispose();

//					g2D.setColor(Color.RED);
//					g2D.drawRect(spriteWidth * i, spriteHeight * k, spriteWidth, spriteHeight);
//					g2D.drawString("" + i + "x" + k, spriteWidth * i + 9, spriteHeight * k + 9);
//					g2D.setColor(Color.GREEN);
//					g2D.drawRect(0, 0, spriteWidth, spriteHeight);

					sArray[counter] = tmp;
					counter++;
				}
			}
		} else {
			for (int k = 0; k < (int) rowsSpin.getValue(); k++) {
				for (int i = 0; i < (int) columnsSpin.getValue(); i++) {
					tmp = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);
					g2D = tmp.createGraphics();
					
					g2D.drawImage(spiteImage, 
							0, 0, 
							spriteWidth, spriteHeight, 
							
							spriteWidth * i, spriteHeight * k, 
							spriteWidth * i + spriteWidth, spriteHeight * k + spriteHeight, 
							
							null);
					
					g2D.dispose();

//					g2D.setColor(Color.RED);
//					g2D.drawRect(spriteWidth * i, spriteHeight * k, spriteWidth, spriteHeight);
//					g2D.drawString("" + i + "x" + k, spriteWidth * i + 9, spriteHeight * k + 9);
//					g2D.setColor(Color.GREEN);
//					g2D.drawRect(0, 0, spriteWidth, spriteHeight);

					sArray[counter] = tmp;
					counter++;
				}
			}
		}

		return sArray;
	}

	private void pause() {isPause = true;}
	
	private void stop() {
		// завершение потока анимации и очистка массива...
		isPlayed = false;
		isPause = false;
		if (aniThread != null) {aniThread.interrupt();}
		if (srcImage != null) {spiteImage = srcImage;}
		animationPauseButton.setText("Пауза");
		progrBar.setValue(0);
		viewPane.repaint();		
	}

	private void chooseImage() {
		stop();
		progrBar.setIndeterminate(true);
		
//      UIManager.put("FileChooser.saveButtonText", "Сохранить");
		UIManager.put("FileChooser.cancelButtonText", "Отмена");
		UIManager.put("FileChooser.openButtonText", "Выбрать");
//      UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
//      UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
//      UIManager.put("FileChooser.lookInLabelText", "Директория");
//      UIManager.put("FileChooser.saveInLabelText", "Сохранить в директории");
//      UIManager.put("FileChooser.folderNameLabelText", "Путь директории");
      
		JFileChooser chooser = new JFileChooser("../") {
			{
				setDialogTitle("Выбери спрайт-лист:");
				setDialogType(JFileChooser.OPEN_DIALOG);
				setFileSelectionMode(JFileChooser.FILES_ONLY);
				setMultiSelectionEnabled(false);
			}
		};
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION ) {
			System.out.println("Chousen file: " + chooser.getSelectedFile());
			try {
				srcImage = spiteImage = ImageIO.read(chooser.getSelectedFile());
				spriteArray = null;
			} catch (IOException e) {
				System.err.println("Chousen file has an ERROR!");
				e.printStackTrace();
			} finally {checkFrameSize();}
		}
		
		progrBar.setIndeterminate(false);
	}

	private void checkFrameSize() {
		if (spiteImage == null || viewPane == null) {return;}
 		if (!resizeByFrame.isSelected() && (spiteImage.getWidth() > viewPane.getWidth() || spiteImage.getHeight() > viewPane.getHeight())) {
			setSize(new Dimension(spiteImage.getWidth() + 30, spiteImage.getHeight() + 170));
			setLocationRelativeTo(null);
		}
 		viewPane.repaint();
	}

}