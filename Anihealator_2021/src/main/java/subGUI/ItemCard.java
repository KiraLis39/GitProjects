package subGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import components.ConstrainedViewPortLayout;
import fox.FoxFontBuilder;
import fox.FoxFontBuilder.FONT;
import fox.Out;
import fox.Out.LEVEL;
import gui.AniFrame;
import registry.Registry;


@SuppressWarnings("serial")
public class ItemCard extends JPanel implements ComponentListener {
	private Double modificКРС, modificМРС, modificHrs, modificPig, modificPAn, modificBrd, modificDgz, modificCts;
	private Double[] mods;
	private int photoW = 0, photoH = 0, itemNameFontSize = 28;
	
	private Font f0 = FoxFontBuilder.setFoxFont(FONT.CANDARA, 18, false);
	private Font f3 = FoxFontBuilder.setFoxFont(FONT.TIMES_NEW_ROMAN, itemNameFontSize, true);
	
	private Color color0 = new Color(0.95f, 1.0f, 1.0f);
	private Color color1 = new Color(0.4f, 0.6f, 0.7f);
	
	private BufferedImage photo;
	
	private JTextPane descriptionArea;
	
	private JPanel photoPane, textPane, calculatorPane;
	private JComboBox<String> animalBox;
	private JTextField weightField, resultField_1;
	private JScrollPane tmpScroll;
	
	private Color itemBackColorUp, itemBackColorDown;
	private HTMLDocument doc;
	
	
	public ItemCard(String[] itemData) {
		Out.Print(ItemCard.class, LEVEL.INFO, "Открытие карточки: " + itemData[2]);
		setLayout(new BorderLayout());
		setBackground(color0);
		
		prepareModData(itemData);
		
		File photoFile = new File("./data/photo/" + itemData[1] + "/" + itemData[4]);
		Out.Print(ItemCard.class, LEVEL.DEBUG, "Try to read the photo from: " + photoFile);
		try {photo = ImageIO.read(photoFile);
		} catch (Exception e2) {Out.Print(ItemCard.class, LEVEL.WARN, "Фото " + photoFile + " не найдено или не указано!");}
		
		textPane = new JPanel(new BorderLayout(9, 6)) {
			{
				float maxPicWidth = 256f;
				float maxPicHeight = 128f;
				
				if (photo != null) {
					photoW = photo.getWidth();
					photoH = photo.getHeight();
					
					if (photo != null && photo.getHeight() > maxPicHeight || photo.getWidth() > maxPicWidth) {
						float percent;
						
						if (photo.getWidth() > maxPicWidth) {
							percent = maxPicWidth / photo.getWidth();
//							System.out.println("Width %" + percent);
							photoW = (int) maxPicWidth;
							photoH = (int) (photo.getHeight() * percent);
						}
					}					
				}
				
				setBorder(new EmptyBorder(0, 3, 0, 0));
				setBackground(color0);

				photoPane = new JPanel(new BorderLayout()) {
					Boolean useBlueFont, useLightFont;
					
					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						
						Graphics2D g2D = (Graphics2D) g;
						if (Registry.isRenderOn) {g2D.addRenderingHints(AniFrame.d2DRender);}
						
						if (photo != null) {
							itemBackColorUp = new Color(
											photo.getColorModel().getRGB(
													photo.getRaster().getDataElements(3, 3, null)));
							itemBackColorDown = new Color(
									photo.getColorModel().getRGB(
											photo.getRaster().getDataElements(3, photo.getHeight() - 3, null)));
							
							g2D.setPaint(new GradientPaint(
									getWidth() / 2, 0, itemBackColorUp, 
									getWidth() / 2, getHeight() + 6, itemBackColorDown));
							
							g2D.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
							g2D.drawImage(photo, getWidth() - photoW - 4, 4, photoW, photoH, null);
							
							useBlueFont = (itemBackColorUp.getRed() >= 127 && itemBackColorUp.getGreen() < 127 && itemBackColorUp.getBlue() < 127);
							useLightFont = (itemBackColorUp.getRed() <= 170 && itemBackColorUp.getGreen() <= 170 && itemBackColorUp.getBlue() <= 170);
						}
						
						
						g2D.setFont(f3);
						g2D.setColor(useLightFont == null ? Color.DARK_GRAY : (useLightFont ? Color.WHITE : Color.DARK_GRAY));
						g2D.drawString(itemData[2], 5, itemNameFontSize);
						
						try {
							System.out.println("R:" + itemBackColorUp.getRed() + "; G:" + itemBackColorUp.getGreen() + "; B:" + itemBackColorUp.getBlue());
							g2D.setColor(useBlueFont == null ? Color.ORANGE.darker() : (useBlueFont ? Color.BLUE.darker() : (useLightFont ? Color.ORANGE : Color.ORANGE.darker())));
						} catch (Exception e) {g2D.setColor(Color.ORANGE.darker());}
						g2D.drawString(itemData[2], 6, itemNameFontSize - 1);
						
						g2D.dispose();
					}
					
					{
						setPreferredSize(new Dimension(0, photo == null ? 40 : photoH + 8));
					}
				};

				descriptionArea = new JTextPane() {
					{
						setEditable(false);
						setBackground(color0);
						setContentType("text/html");
					}
				};
				
				tmpScroll = new JScrollPane(descriptionArea) {
					{
						setBackground(color0);
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(color1, 1, true), new EmptyBorder(0, 3, 3, 3)));
						getViewport().setBackground(color0);
						getViewport().setLayout(new ConstrainedViewPortLayout());
						getVerticalScrollBar().setUnitIncrement(21);
					}
				};
				
				add(photoPane, BorderLayout.NORTH);
				add(tmpScroll, BorderLayout.CENTER);
			}
		};
		
		try {
			HTMLEditorKit kit = new HTMLEditorKit();
			doc = new HTMLDocument();
			
			descriptionArea.setEditorKit(kit);
			descriptionArea.setDocument(doc);
			
		    kit.insertHTML(doc, doc.getLength(), prepareDescr(itemData[3]), 0, 0, null);

		    SimpleAttributeSet background = new SimpleAttributeSet();
		    StyleConstants.setBackground(background, color0);
		    descriptionArea.getStyledDocument().setParagraphAttributes(0, descriptionArea.getDocument().getLength(), background, false);
		    descriptionArea.setBorder(BorderFactory.createEmptyBorder());
		} catch (Exception e) {e.printStackTrace();}
		
		calculatorPane = new JPanel(new FlowLayout()) {
			{
				setBackground(color1);
				animalBox = new  JComboBox<String>(new String[] {"КРС", "МРС", "Лошади", "Свиньи", "Пушнина", "Птицы", "Собаки", "Кошки"});
				animalBox.addActionListener(new ActionListener() {					
					@Override public void actionPerformed(ActionEvent e) {recalculate(mods);}
				});
				
				JLabel weightLabel = new JLabel("Вес: ") {
					{
						setFont(f0);
						setForeground(Color.WHITE);
					}
				};
				
				weightField = new JTextField() {
					{
						setText("1");
						setColumns(10);
						
						addKeyListener(new KeyAdapter() {									
							@Override
							public void keyReleased(KeyEvent e) {recalculate(mods);}
						});
					}
				};
				
				JLabel equ = new JLabel("кг. = ") {
					{
						setFont(f0);
						setForeground(Color.WHITE);
					}
				};
				
				
				JPanel resultPane = new JPanel(new BorderLayout(3 ,3)) {
					{
						setOpaque(false);
						
						JPanel r0 = new JPanel(new GridLayout(1, 1, 3 ,3)) {
							{
								setOpaque(false);
								
								resultField_1 = new JTextField() {
									{
										setColumns(10);
										setEditable(false);
										setBackground(color1);
									}
								};

								add(resultField_1);
							}
						};
						
						JPanel r1 = new JPanel(new GridLayout(1, 1, 3 ,3)) {
							{
								setOpaque(false);
								
								add(new JLabel("мл"));
							}
						};
						
						add(r0, BorderLayout.CENTER);
						add(r1, BorderLayout.EAST);
					}
				};

				JLabel dataLabel = new JLabel(" кол-во/вес") {
					{
						setFont(f0);
						setForeground(Color.WHITE);
					}
				};
				
				add(animalBox);
				add(weightLabel);
				add(weightField);
				add(equ);
				add(resultPane);
				add(dataLabel);
			}
		};
		
		add(photoPane, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
		add(calculatorPane, BorderLayout.SOUTH);
		
		addComponentListener(this);
		
		Out.Print(ItemCard.class, Out.LEVEL.DEBUG, "Открытие карточки " + itemData[2] + " успешно завершено!");
		recalculate(mods);
	}
	
	private void prepareModData(String[] itemData) {
		try {
			modificКРС = Double.parseDouble(itemData[5]);
			modificМРС = Double.parseDouble(itemData[6]);
			modificHrs = Double.parseDouble(itemData[7]);
			modificPig = Double.parseDouble(itemData[8]);
			modificPAn = Double.parseDouble(itemData[9]);
			modificBrd = Double.parseDouble(itemData[10]);
			modificDgz = Double.parseDouble(itemData[11]);
			modificCts = Double.parseDouble(itemData[12]);
			
			mods = new Double[] {
					modificКРС,
					modificМРС,
					modificHrs,
					modificPig,
					modificPAn,
					modificBrd,
					modificDgz,
					modificCts
			};
		} catch (Exception e2) {
			Out.Print(ItemCard.class, Out.LEVEL.ERROR, "Модификатор не указан! Рассчеты невозможны! (" + itemData[2] + ")");
			e2.printStackTrace();
		}
	}

	private String prepareDescr(String description) {
		description = description.replace("<p>", "</p><br><p>");
		
		description = description.replace("<strong>", "<b>");
		description = description.replace("</strong>", "</b>");
		
		return description;
	}
	
	private void recalculate(Double[] mods) {
		Out.Print(ItemCard.class, Out.LEVEL.DEBUG, "Пересчет...");
//		modificКРС, modificМРС, modificHrs, modificPig, modificPAn, modificBrd, modificDgz, modificCts;
		try {
			Double weight = Double.parseDouble(weightField.getText().replace(",", ".")) * mods[animalBox.getSelectedIndex()];
			String formattedDouble = String.format("%.2f", weight);
			resultField_1.setForeground(Color.BLACK);
			resultField_1.setText(formattedDouble);
		} catch (Exception e) {
			resultField_1.setForeground(Color.RED);
			resultField_1.setText("ERR");
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
//		descriptionArea.setPreferredSize(new Dimension(0, descriptionArea.getText().length() / 27));
//		descriptionArea.setPreferredSize(new Dimension(0, descriptionArea.getHeight()));
//		descriptionArea.revalidate();
		
//		tmpScroll.setSize(new Dimension(tmpScroll.getWidth(), descriptionArea.getHeight()));
//		tmpScroll.revalidate();
		
		Out.Print(getClass(), Out.LEVEL.DEBUG, "\ncomponentResized(): Size ItemCard: " + getWidth() + "x" + getHeight());
		Out.Print(getClass(), Out.LEVEL.DEBUG, "Size descriptionArea: " + descriptionArea.getWidth() + "x" + descriptionArea.getHeight());
		Out.Print(getClass(), Out.LEVEL.DEBUG, "Size textPane: " + textPane.getWidth() + "x" + textPane.getHeight());
		Out.Print(getClass(), Out.LEVEL.DEBUG, "Size tmpScroll: " + tmpScroll.getWidth() + "x" + tmpScroll.getHeight() + "\n");
	}

	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
}