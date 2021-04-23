package engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adds.Out;
import builders.FoxFontBuilder;
import registry.Registry;


@SuppressWarnings("serial")
public class NewDataItem extends JDialog {
	private Font f1 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 18, false);
	private Font f2 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 14, false);
	private Font f3 = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CANDARA, 16, true);
	
	private JTextField itemNameField, picturesNameField;
	private JTextField valueOnWeightFieldKPC, valueOnWeightFieldMPC, valueOnWeightFieldCats, valueOnWeightFieldDogz, valueOnWeightFieldBird, valueOnWeightFieldPAni, valueOnWeightFieldPig, valueOnWeightFieldHorse;
	private JTextPane descriptionArea;
	private JPanel rightImagePane, basePane;
	private BufferedImage photo;
	private JComboBox<String> typesBox;
	private String wasID;
	private Boolean justCloseSilently = false;
	
	public NewDataItem(JFrame f, ArrayList<String> types) {
		super(f, "Управление карточками:", true);
		
		setPreferredSize(new Dimension(900, 700));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				
		basePane = new JPanel(new BorderLayout()) {
			{
				setBorder(new EmptyBorder(3, 3, 3, 3));
				
				JPanel centerPane = new JPanel(new BorderLayout(3, 3)) {
					{
						JPanel titlesPane = new JPanel(new GridLayout(3, 1)) {
							{
								setBorder(new EmptyBorder(0, 3, 0, 0));
								
								add(new JLabel("Тип лекарств. препарата:"));
								add(new JLabel("Название новой единицы:"));
								add(new JLabel("Картинка (*.расширение):"));
							}
						};
						
						JPanel fieldsPane = new JPanel(new GridLayout(3, 1)) {
							{
								
								JPanel typesPane = new JPanel(new BorderLayout()) {
									{
										
										typesBox = new JComboBox<String>();
										for (String string : types) {typesBox.addItem(string);}
										
										JPanel ButtsPane = new JPanel(new GridLayout(1, 0, 0, 0)) {
											{
												
												JButton chBut = new JButton("+") {
													{
														setToolTipText("<html>Добавить новый<br>тип препарата");
														setFont(f3);
														setFocusable(false);
														setBackground(Color.DARK_GRAY);
														setForeground(Color.GREEN);
														setPreferredSize(new Dimension(36, 36));
														addActionListener(new ActionListener() {															
															@Override
															public void actionPerformed(ActionEvent e) {addType();}
														});
													}
												};
												
												JButton rmBut = new JButton("-") {
													{
														setToolTipText("Удалить препарат");
														setFont(f3);
														setFocusable(false);
														setBackground(Color.DARK_GRAY);
														setForeground(Color.RED);
														setPreferredSize(new Dimension(36, 36));
														addActionListener(new ActionListener() {
															@Override
															public void actionPerformed(ActionEvent e) {
																String newType = JOptionPane.showInputDialog(typesBox, 
																		"Название удаляемого препарата:", "Удалить:", 
																		JOptionPane.PLAIN_MESSAGE);
																
																if (newType != null && !newType.equals("")) {
																	DataBase.removeFromDB(newType);
																	justCloseSilently = true;
																	dispose();
																}
															}
														});
													}
												};
												
												JButton rdBut = new JButton("O") {
													{
														setToolTipText("Изменить препарат");
														setFont(f3);
														setFocusable(false);
														setBackground(Color.DARK_GRAY);
														setForeground(Color.ORANGE);
														setPreferredSize(new Dimension(36, 36));
														addActionListener(new ActionListener() {
															@Override	public void actionPerformed(ActionEvent e) {toChangeTheAid();}
														});
													}
												};
												
												add(chBut);
												add(rdBut);
												add(rmBut);
											}
										};
								
										add(typesBox, BorderLayout.CENTER);
										add(ButtsPane, BorderLayout.EAST);
									}
								};
								
								itemNameField = new JTextField();
								
								JPanel picChoserPane = new JPanel(new BorderLayout()) {
									{
										
										picturesNameField = new JTextField() {
											{
												setEditable(false);
												setFocusable(false);
											}
										};
										
										JButton chBut = new JButton("...") {
											{
												setFont(f2);
												setBackground(Color.DARK_GRAY);
												setForeground(Color.GREEN);
												setPreferredSize(new Dimension(36, 36));
												addActionListener(new ActionListener() {
													@Override
													public void actionPerformed(ActionEvent e) {
														new JFileChooser(new File("./data/photo/")) {
															{
																setDialogTitle("Выберите изображение:");
																setDialogType(JFileChooser.OPEN_DIALOG);
																setFileSelectionMode(JFileChooser.FILES_ONLY);
																setMultiSelectionEnabled(false);
																int result = showOpenDialog(basePane);
																if (result == JFileChooser.APPROVE_OPTION) {
																	picturesNameField.setText(getSelectedFile().getName());
																	
																	try {
																		photo = ImageIO.read(getSelectedFile());
																		float pfw = getSelectedFile().length();
																		
																		if (pfw > 4000000f) {
																			JOptionPane.showConfirmDialog(getParent(), 
																					"<html>Размер изображения превышает норму <b>более чем в 10 раз</b>!<br>"
																							+ "Пожалуйста, уменьшите размер изображения<br>"
																							+ "во избежание замедления работы программы и её веса!",
																					"Внимание!", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
																		}
																		
																		if (pfw > 400000f) {
																			String formattedDouble = String.format("%.2f", (pfw / (1024f*1024f)));
																			JOptionPane.showConfirmDialog(getParent(), 
																					"<html>Размер изображения " + formattedDouble + "мб.<br>"
																							+ "Это больше, чем рекомендуется для сохранения оптимальной скорости<br>"
																							+ "обработки информации и веса базы данных.",
																					"Внимание!", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
																		}
																	} catch (IOException e1) {
																		photo = null;
																		picturesNameField.setText("");
																	}
																	
																	rightImagePane.repaint();
													            }
															}
														};
													}
												});
											}
										};
										
										add(picturesNameField, BorderLayout.CENTER);
										add(chBut, BorderLayout.EAST);
									}
								}; 
								
								add(typesPane);
								add(itemNameField);
								add(picChoserPane);
							}
						};
						
						add(titlesPane, BorderLayout.WEST);
						add(fieldsPane, BorderLayout.CENTER);
					}
				};
				
				rightImagePane = new JPanel() {
					@Override
					public void paintComponent(Graphics g) {
						if (photo == null) {super.paintComponent(g);
						} else {
							super.paintComponent(g);
							g.drawImage(photo, 0, 8, 256, 264, this);
							g.dispose();
						}
					}
					
					{
						setPreferredSize(new Dimension(256, 0));
					}
				};
				
				JPanel downPane = new JPanel(new BorderLayout()) {
					{
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(
										BorderFactory.createLineBorder(Color.BLACK), "Описание (главное и общее):", 0, 2, f2, Color.BLACK), 
										new EmptyBorder(0, 3, 0, 3)));

						descriptionArea = new JTextPane() {
							{
//										setLineWrap(true);
//										setWrapStyleWord(true);
								setFont(f1);
								setContentType("text/html");
							}
						};
						
						add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
					}
				};

				JPanel calcsPane = new JPanel(new GridLayout(4, 2, 3, 3)) {
					{
						JPanel КРСPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("КРС "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldKPC = new JTextField(10);										
										add(valueOnWeightFieldKPC, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel MРСPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("MРС "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldMPC = new JTextField(10);										
										add(valueOnWeightFieldMPC, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel HrsPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("Horse "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldHorse = new JTextField(10);										
										add(valueOnWeightFieldHorse, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel PgsPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("Pig "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldPig = new JTextField(10);										
										add(valueOnWeightFieldPig, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						
						JPanel PAnPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("PAnimal "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldPAni = new JTextField(10);										
										add(valueOnWeightFieldPAni, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel BrdPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("Birds "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldBird = new JTextField(10);										
										add(valueOnWeightFieldBird, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel DgzPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("Dog "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldDogz = new JTextField(10);										
										add(valueOnWeightFieldDogz, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
						JPanel CtsPane = new JPanel(new FlowLayout()) {
							{
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("Cat "), BorderLayout.WEST);										
										JTextField onWeightArea = new JTextField(10) {{setText("1");	setEditable(false);setEnabled(false);}};										
										add(onWeightArea, BorderLayout.CENTER);
									}
								});
							
								add(new JPanel(new BorderLayout()) {
									{
										add(new JLabel("кг => "), BorderLayout.WEST);										
										valueOnWeightFieldCats = new JTextField(10);										
										add(valueOnWeightFieldCats, BorderLayout.CENTER);										
										add(new JLabel("ед."), BorderLayout.EAST);
									}
								});
							}
						};
					
						add(КРСPane);		add(MРСPane);			add(HrsPane);
						add(PgsPane);		add(PAnPane);			add(BrdPane);
						add(DgzPane);		add(CtsPane);
					}
				};
				
				add(centerPane, BorderLayout.NORTH);
				add(rightImagePane, BorderLayout.EAST);
				add(downPane, BorderLayout.CENTER);
				add(calcsPane, BorderLayout.SOUTH);
			}
		};
		
		add(basePane);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (((String) typesBox.getSelectedItem()).equals("")) {
					showMessage("Не заполнен тип элемента!");
				} else if (itemNameField.getText().equals("")) {
					showMessage("Не заполнено название элемента!");
				} else if (picturesNameField.getText().equals("")) {
					showMessage("Не выбрано фото элемента!");
				} else if (descriptionArea.getText().equals("")) {
					showMessage("Не заполнено описание элемента!");
				} else if (picturesNameField.getText().equals("")) {
					
				} else {
					try {
//								"'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts'
						Double.valueOf(valueOnWeightFieldKPC.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldMPC.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldHorse.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldPig.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldPAni.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldBird.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldDogz.getText().replace(",", "."));
						Double.valueOf(valueOnWeightFieldCats.getText().replace(",", "."));
						showMessage("Завершить редактирование и сохранить карточку?");
					} catch (Exception e2) {showMessage("В поле модификатора на вес требуется ввести число!");}
				}
			}

			private void showMessage(String string) {
				int req = JOptionPane.showConfirmDialog(
						basePane, "<html><b><h2 color='RED'>" + string + "</h2></b><br>Попытаться сохранить элемент?", "Внимание!",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, Registry.messageIcon);

				if (req == 0) {dispose();
				} else if (req == 1) {
					itemNameField.setText("");
					descriptionArea.setText(""); 
					valueOnWeightFieldKPC.setText("");
					valueOnWeightFieldMPC.setText("");
					valueOnWeightFieldHorse.setText("");
					valueOnWeightFieldPig.setText("");
					valueOnWeightFieldPAni.setText("");
					valueOnWeightFieldBird.setText("");
					valueOnWeightFieldDogz.setText(""); 
					valueOnWeightFieldCats.setText("");
					picturesNameField.setText("");
					dispose();
				}
			}
		});
				
		pack();
		setLocationRelativeTo(null);
		
		System.out.println("types = " + Arrays.toString(types.toArray()) + "; " + types.size());
		if (types.size() == 0) {addType();}

		itemNameField.requestFocus();
		itemNameField.requestFocusInWindow();
		
		pack();
		setVisible(true);
		
		
		if (!justCloseSilently) {
			DataBase.addNewData(
				new String[] {
					((String) typesBox.getSelectedItem()).trim(), 
					itemNameField.getText().trim(), 
					descriptionArea.getText().trim(), 
					picturesNameField.getText().trim(),
					wasID,
					
//					"'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts'
					valueOnWeightFieldKPC.getText().trim(), 
					valueOnWeightFieldMPC.getText().trim(), 
					valueOnWeightFieldHorse.getText().trim(), 
					valueOnWeightFieldPig.getText().trim(), 
					valueOnWeightFieldPAni.getText().trim(), 
					valueOnWeightFieldBird.getText().trim(), 
					valueOnWeightFieldDogz.getText().trim(), 
					valueOnWeightFieldCats.getText().trim()
					}
			);
		}
	}
	
	String newType;
	private void toChangeTheAid() {		
		new JDialog(NewDataItem.this, "Изменить карточку препарата:", true) {
			{
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setPreferredSize(new Dimension(400, 650));
				getContentPane().setLayout(new BorderLayout(3, 3));
				
				DefaultListModel<String> listModel = new DefaultListModel<String>();
				JList<String> list = new JList<String>(listModel);
				listModel.addAll(DataBase.getAidsList());				
				
				JScrollPane scroll = new JScrollPane(list);
				
				add(scroll);
				
//				list.addListSelectionListener(new ListSelectionListener() {
//					@Override
//					public void valueChanged(ListSelectionEvent e) {
//						
//						
//					}
//				});
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() >=2) {
//							System.out.println(SwingUtilities.isLeftMouseButton(list));
//							if (MouseInfo.getPointerInfo().toString().equals("java.awt.PointerInfo@6aa00e9b")) {return;}
							newType = list.getSelectedValue();
							
							if (newType != null && !newType.equals("")) {
								String[] data = DataBase.getElement(newType);
								itemNameField.setText(newType);
								descriptionArea.setText(data[3]);
								picturesNameField.setText(data[4]);
								
//								"'modificКРС', 'modificМРС', 'modificHrs', 'modificPig', 'modificPAn', 'modificBrd', 'modificDgz', 'modificCts'
								valueOnWeightFieldKPC.setText(data[5]);
								valueOnWeightFieldMPC.setText(data[6]);
								valueOnWeightFieldHorse.setText(data[7]);
								valueOnWeightFieldPig.setText(data[8]);
								valueOnWeightFieldPAni.setText(data[9]);
								valueOnWeightFieldBird.setText(data[10]);
								valueOnWeightFieldDogz.setText(data[11]);
								valueOnWeightFieldCats.setText(data[12]);
								
								wasID = data[0];
							}
						}
					}
				});
				
				pack();
				setLocationRelativeTo(null);
				setVisible(true);
			}
		};
	}
	
	private void addType() {
		String newType = "";
		
		while (newType.equals("")) {
			newType = JOptionPane.showInputDialog(this, 
					"Введите название нового типа:", "Новый тип:", 
					JOptionPane.PLAIN_MESSAGE);
			if (newType == null) {
				dispose();
				return;
			}
		}
		
		Out.Print(DataBase.class, Out.LEVEL.ACCENT, "Тип '" + newType + "' добавляется...");
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\db.db")) {
			Statement statmt = conn.createStatement();	
			statmt.execute("INSERT INTO 'type' ('typename') VALUES ('" + newType + "');");						
			statmt.close();
			
			Out.Print(DataBase.class, Out.LEVEL.INFO, "Тип '" + newType + "' добавлен.");
			typesBox.addItem(newType);
			typesBox.setSelectedItem(newType);
			new File(Registry.photoDir + "/" + newType).mkdir();
		} catch (Exception e) {e.printStackTrace();}
	}
}