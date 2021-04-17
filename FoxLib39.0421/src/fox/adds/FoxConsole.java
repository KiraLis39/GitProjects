package adds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import builders.FoxFontBuilder;


@SuppressWarnings("serial")
public class FoxConsole extends JDialog implements KeyListener {
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	KeyListener kList;
	
	private JFrame parentFrame;
	JPanel upTactAndClockPane;
	private JPanel foxConsolePanel;
	JScrollPane consoleScroll;
	private JTextArea consoleArea;
	JTextField inputArea;
	JLabel oClock;
	
	private Boolean clockIsOn = false;
	
	
	public FoxConsole(JFrame parent) {this(parent, "Console", true, null);}
	
	public FoxConsole(JFrame parent, String consoleTitle, Boolean isModal) {this(parent, consoleTitle, isModal, null);}
	
	public FoxConsole(JFrame parent, String consoleTitle, Boolean isModal, KeyListener kList) {
		super(parent, consoleTitle, isModal);
//		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
//	    } catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}

		this.parentFrame = parent;
		
		if (kList != null) {this.kList = kList;} else {this.kList = this;}
		
		setLayout(new BorderLayout());
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setUndecorated(true);
		setVisible(false);
		
		JComponent testFrameComponent = (JComponent) this.parentFrame.getContentPane();
		testFrameComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), "onoff");
		testFrameComponent.getActionMap().put("onoff", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(">> " + e.getID() + " - " + e.getModifiers());
				visibleChanger();
			}
		});

		createNewConsole();
	}

	void visibleChanger() {
		oClock.setText("" + dateFormat.format(System.currentTimeMillis()));
		
		setSize(new Dimension(parentFrame.getWidth(), parentFrame.getHeight() / 3 * 2));
		setLocation(parentFrame.getX(), parentFrame.getY());		
		setVisible(!isVisible());
	}

	private void createNewConsole() {
		setOpacity(0.85f);
		
		upTactAndClockPane = new JPanel() {
			{
				setBackground(new Color(0.0f, 0.0f, 0.5f, 0.75f));
				setBorder(new EmptyBorder(3, 10, 5, 10));
			}
		};

		oClock = new JLabel();
		oClock.setText("" + dateFormat.format(System.currentTimeMillis()));
		oClock.setFont(FoxFontBuilder.setFoxFont(0, 14, false));
		oClock.setForeground(Color.GRAY.brighter());

		upTactAndClockPane.add(oClock);
		if (!clockIsOn) {	upTactAndClockPane.setVisible(false);}

		consoleArea = new JTextArea() {
			{
				setBackground(Color.BLACK);
				setForeground(Color.GREEN);
				setFont(FoxFontBuilder.setFoxFont(0, 14, false));
				setBorder(new EmptyBorder(5, 5, 5, 5));
				setEditable(false);
				setFocusable(true);
				setLineWrap(true);
				setWrapStyleWord(true);
				setText("***CONSOLE OUT*** \n");
			}
		};

		consoleScroll = new JScrollPane(consoleArea) {
			{
				setAutoscrolls(true);
				setWheelScrollingEnabled(true);
				setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			}
		};

		inputArea = new JTextField() {
			{
				setBackground(Color.BLACK);
				setForeground(Color.ORANGE);
				setFont(FoxFontBuilder.setFoxFont(0, 14, false));
				setBorder(new EmptyBorder(5, 5, 5, 5));
				setEditable(true);
				setFocusable(true);
				setAutoRequestFocus(true);
				
				addKeyListener(kList);
			}
		};
		
		foxConsolePanel = new JPanel(new BorderLayout()) {
			{
				setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
				setForeground(Color.GREEN);
				setBorder(new EmptyBorder(3, 5, 3, 5));
				
				add(upTactAndClockPane, BorderLayout.NORTH);
				add(consoleScroll, BorderLayout.CENTER);
				add(inputArea, BorderLayout.SOUTH);
				
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						inputArea.requestFocus();
						inputArea.grabFocus();
						oClock.setText("" + dateFormat.format(System.currentTimeMillis()));
					}
				});
			}
		};
		
		add(foxConsolePanel);
		
		setMinimumSize(new Dimension(parentFrame.getWidth() / 2, parentFrame.getHeight() / 3 * 2));
		setLocation(parentFrame.getX(), parentFrame.getY());
	}
	
	public void setClockVisible(Boolean onOff) {upTactAndClockPane.setVisible(onOff);}
	
	
	public void setTextareaBackgroundColor(Color color) {consoleArea.setBackground(color);}
	public void setTextareaForegroundColor(Color color) {consoleArea.setForeground(color);}
	public void setInputareaBackgroundColor(Color color) {inputArea.setBackground(color);}
	public void setInputareaForegroundColor(Color color) {inputArea.setForeground(color);}
	
	public void setConsoleClockText(String time) {oClock.setText(time);}
	public void setConsoleClockFont(Font font) {oClock.setFont(font);}
	public void setConsoleClockBackground(Color color) {upTactAndClockPane.setBackground(color);}
	
	public void setConsoleAreaFont(Font newFont) {consoleArea.setFont(newFont);}

	public void appendToConsole(String string) {consoleArea.append("\n" + string);}

	public void setText(String str) {consoleArea.setText(str);}

	public void clear() {consoleArea.setText("");}

	public void changeInputAreaText(String text) {
		if (text == null) {
			inputArea.setText("");
			return;
		}
		
		inputArea.setText(text);
	}
	
	public void setFocusInArea() {
		inputArea.setRequestFocusEnabled(true);
		inputArea.requestFocus();
	}
	
	
	@Override
	public void keyPressed(KeyEvent key) {
		if (key.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!inputArea.getText().isEmpty()) {
				consoleArea.append("\n" + inputArea.getText());
				consoleArea.setText(consoleArea.getText().trim());
				consoleArea.setCaretPosition(consoleArea.getText().length());
				
				inputArea.setText("");
				inputArea.requestFocus();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
			if (isVisible()) {dispose();} else {setFocusInArea();}
		}
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
			if (isVisible()) {dispose();} else {setFocusInArea();}
		}
	}
}