package my.app;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;

public class DocumentForm extends JFrame {

	private Robot robot;
	private JTextArea textArea;
	File file1 = new File("memo.txt");
	private ProtectedTextSite pt;

	public DocumentForm(ProtectedTextSite pt) throws AWTException {
		super("Write a Note");
		
		this.pt = pt;
		
		robot = new Robot();
		setSize(640, 480);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - this.getWidth();
        int y = (int) rect.getMaxY() - this.getHeight();
        setLocation(x, y);
        
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		textArea = new JTextArea(); // textarea
		
		Font font = textArea.getFont();
		float size = 14;
		textArea.setFont( font.deriveFont(size) );
		
		//load();
		
		
		JMenuBar menuBar = new JMenuBar(); // menubar
		JMenu fileM = new JMenu("File"); // file menu
		JScrollPane scpane = new JScrollPane(textArea); // scrollpane and add textarea to
//										// scrollpane
		JMenuItem exitI = new JMenuItem("Exit");
		exitI.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JMenuItem closeI = new JMenuItem("Close");
		closeI.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DocumentForm.this.setVisible(false);
			}
		});
		
		JToolBar toolBar = new JToolBar();

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
//
		setJMenuBar(menuBar);
		menuBar.add(fileM);
		fileM.add(closeI);
		fileM.add(exitI);
//

		pane.add(scpane, BorderLayout.CENTER);
		pane.add(toolBar, BorderLayout.SOUTH);

	}


	
	@Override 
	public void setVisible(boolean visible) {
		if (this.isVisible() == visible) {
			return;
		}
		if (visible) {
			load();
		}

		super.setVisible(visible);
	 
		if (visible) {
			
			EventQueue.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					try {
						// remember the last location of mouse
						Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();
		 
						// simulate a mouse click on title bar of window
						robot.mouseMove(DocumentForm.this.getX() + 10, DocumentForm.this.getY() + 5);
						robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
						// move mouse to old location
						robot.mouseMove((int)oldMouseLocation.getX(), (int)oldMouseLocation.getY());
					}
					catch (Exception ex) { }
				}
			});
			
		} else {
			save();
		}
	 
	}


	private void save() {
		try {
			this.pt.save(textArea.getText());
		} catch (PostFailedException e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "Failed to save the note. All changes will be lost.");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try (FileWriter fw = new FileWriter(file1.getAbsoluteFile(), false)) {
//			textArea.write(fw);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


	private void load() {
		try {
			String text = this.pt.load();
			int oldCaretPos = textArea.getCaretPosition();
			textArea.setText(text);
			textArea.setCaretPosition(oldCaretPos);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try (FileReader fr = new FileReader(file1.getAbsoluteFile())) {
//			textArea.read(fr, file1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	
}