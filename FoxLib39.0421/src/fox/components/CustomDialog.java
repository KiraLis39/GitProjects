package fox.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;


@SuppressWarnings("serial")
public class CustomDialog {
	JDialog dialog;
	JPanel contentPanel;
	Timer timer;
	
	Color border = new Color(157, 157, 157);
	Color top = new Color(250, 250, 250);
	Color bottom = new Color(238, 238, 238);
	
	float opacity = 0.1f;
	

	public CustomDialog(JComponent source) {
		if (dialog != null) {dialog.dispose();}

		JTextField field = new JTextField();
		
		contentPanel = new JPanel() {
			{
				setOpaque(false);
				setLayout(new BorderLayout(4, 4));
				setBorder(BorderFactory.createEmptyBorder(6, 10, 15, 10));
				
				add(new JLabel("<html>"
						+ "Ввведите, пожалуйста, свое имя и фамилию:"
						+ "<br>"
						+ "<font color=gray size=8px>"
						+ "Строго в именительном падеже и с заглавных букв"
						+ "</font></html>"), BorderLayout.CENTER);
				add(new JPanel(new BorderLayout(0, 0)) {
					{
						setOpaque(false);

						add(new JComponent() {
							boolean mouseOver = false;

							{
								setOpaque(false);
								addMouseListener(new MouseAdapter() {
									public void mouseEntered(MouseEvent e) {
										mouseOver = true;
										repaint();
									}

									public void mouseExited(MouseEvent e) {
										mouseOver = false;
										repaint();
									}

									public void mousePressed(MouseEvent e) {closeDialog(dialog, contentPanel);}
								});
							}

							private Color out = new Color(162, 162, 162);
							private Color over = new Color(122, 122, 122);
							private Stroke stroke = new BasicStroke(2f);

							public void paint(Graphics g) {
								super.paint(g);

								Graphics2D g2d = (Graphics2D) g;
								g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
								g2d.setStroke(stroke);
								g2d.setPaint(mouseOver ? over : out);
								g2d.drawLine(1, 1, getWidth() - 2, getHeight() - 2);
								g2d.drawLine(getWidth() - 2, 1, 1, getHeight() - 2);
							}

							public Dimension getPreferredSize() {
								return new Dimension(10, 10);
							}
						}, BorderLayout.NORTH);
					}
				}, BorderLayout.EAST);
				add(field, BorderLayout.SOUTH);
			}

			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

				GeneralPath gp = new GeneralPath(Path2D.WIND_EVEN_ODD);
				gp.moveTo(5, 5);
				gp.quadTo(5, 0, 10, 0);
				gp.lineTo(getWidth() - 11, 0);
				gp.quadTo(getWidth() - 6, 0, getWidth() - 6, 5);
				gp.lineTo(getWidth() - 6, getHeight() - 16);
				gp.quadTo(getWidth() - 6, getHeight() - 11, getWidth() - 11, getHeight() - 11);
				gp.lineTo(getWidth() / 2 + 10, getHeight() - 11);
				gp.lineTo(getWidth() / 2, getHeight() - 1);
				gp.lineTo(getWidth() / 2 - 10, getHeight() - 11);
				gp.lineTo(10, getHeight() - 11);
				gp.quadTo(5, getHeight() - 11, 5, getHeight() - 16);
				gp.lineTo(5, 5);

				g2d.setPaint(new GradientPaint(0, 0, top, 0, getHeight() - 11, bottom));
				g2d.fill(gp);

				g2d.setPaint(border);
				g2d.draw(gp);

				super.paint(g);
			}
		};
		
		dialog = new JDialog() {
			{
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				setLayout(new BorderLayout());
				setUndecorated(true);
				setBackground(new Color(0,0,0,0));
				
				addWindowFocusListener(new WindowFocusListener() {
					public void windowGainedFocus(WindowEvent e) {
						field.requestFocus();
						field.requestFocusInWindow();
					}

					public void windowLostFocus(WindowEvent e) {
						closeDialog(dialog, contentPanel);
					}
				});

				addComponentListener(new ComponentAdapter() {
					public void componentShown(ComponentEvent e) {
						if (timer != null && timer.isRunning()) {timer.stop();}
						opacity = 0.1f;
						timer = new Timer(1000 / 24, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								opacity += 0.1f;
								if (opacity >= 1f) {
									opacity = 1f;
									timer.stop();
								}

								repaint();
							}
						});

						timer.start();
					}
				});
				
				add(contentPanel, BorderLayout.CENTER);
				
				pack();
				setLocation(source.getLocationOnScreen().x + source.getWidth() / 2 - getWidth() / 2, source.getLocationOnScreen().y - getHeight());
				setVisible(true);
			}
		};
	}

	void closeDialog(final JDialog dialog, final JPanel contentPanel) {
		if (timer != null && timer.isRunning()) {timer.stop();}
		opacity = 1f;

		timer = new Timer(1000 / 24, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				opacity -= 0.1f;
				if (opacity <= 0.1f) {
					opacity = 0.1f;
					dialog.setVisible(false);
					timer.stop();
				}
				contentPanel.repaint();
			}
		});

		timer.start();
	}
}