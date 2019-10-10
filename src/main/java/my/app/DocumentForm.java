package my.app;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.commons.lang.SystemUtils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;

public class DocumentForm extends JFrame {

	private Robot robot;
	private JTextArea textArea;
	File file1 = new File("memo.txt");
	private ProtectedTextSite pt;
	private JProgressBar progressBar;
	private ExecutorService executor;
	private JToolBar toolBar;
	private boolean firstLoad = true;
	UndoManager undoManager;

	public DocumentForm(ProtectedTextSite pt) throws AWTException {
		super("Write a Note");
		
		this.pt = pt;
		
		robot = new Robot();
		setSize(640, 480);
		
		executor = Executors.newFixedThreadPool(1);
		
      //size of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //height of the task bar
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;

        //available size of the screen 
        setLocation(screenSize.width - getWidth() - 5, screenSize.height - taskBarSize - getHeight() - 5);        
        
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
		
		toolBar = new JToolBar();
		toolBar.setVisible(false);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setString("updating notes");
		progressBar.setStringPainted(true);
		toolBar.add(progressBar);

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
// Undo/Redo
		
	    JButton undo = new JButton("Undo");
	    JButton redo = new JButton("Redo");
	    
	    KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(
	            KeyEvent.VK_Z, Event.CTRL_MASK);
	    KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(
	            KeyEvent.VK_Y, Event.CTRL_MASK);

	    undoManager = new UndoManager();

	    Document document = textArea.getDocument();
	    document.addUndoableEditListener(new UndoableEditListener() {
	        @Override
	        public void undoableEditHappened(UndoableEditEvent e) {
	            undoManager.addEdit(e.getEdit());
	        }
	    });

	    // Add ActionListeners
	    undo.addActionListener((ActionEvent e) -> {
	        try {
	            undoManager.undo();
	        } catch (CannotUndoException cue) {}
	    });
	    redo.addActionListener((ActionEvent e) -> {
	        try {
	            undoManager.redo();
	        } catch (CannotRedoException cre) {}
	    });

	    // Map undo action
	    textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
	            .put(undoKeyStroke, "undoKeyStroke");
	    textArea.getActionMap().put("undoKeyStroke", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                undoManager.undo();
	             } catch (CannotUndoException cue) {}
	        }
	    });
	    // Map redo action
	    textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
	            .put(redoKeyStroke, "redoKeyStroke");
	    textArea.getActionMap().put("redoKeyStroke", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            try {
	                undoManager.redo();
	             } catch (CannotRedoException cre) {}
	        }
	    });
		
	    JToolBar toolBarTop = new JToolBar("Still draggable");
	    toolBarTop.add(undo);
	    toolBarTop.add(redo);
//
		setJMenuBar(menuBar);
		menuBar.add(fileM);
		fileM.add(closeI);
		fileM.add(exitI);
//

		pane.add(scpane, BorderLayout.CENTER);
		pane.add(toolBar, BorderLayout.SOUTH);
	    pane.add(toolBarTop, BorderLayout.NORTH);

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
			stealFocus();
			
		} else {
			save();
		}
	 
	}


	static interface User32 extends com.sun.jna.platform.win32.User32 {

	    User32 INSTANCE = (User32) Native.loadLibrary(User32.class,
	            W32APIOptions.DEFAULT_OPTIONS);

	    boolean SystemParametersInfo(
	            int uiAction,
	            int uiParam,
	            Object pvParam, // Pointer or int
	            int fWinIni
	    );
	}
	

	private void stealFocus() {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					Integer mouseSpeed = null;
					if (SystemUtils.IS_OS_WINDOWS) {
					    Pointer mouseSpeedPtr = new Memory(4);
					    mouseSpeed = User32.INSTANCE.SystemParametersInfo(0x0070, 0, mouseSpeedPtr, 0)
					            ? mouseSpeedPtr.getInt(0) : null;
					}

				    					
					// remember the last location of mouse
					Point oldMouseLocation = MouseInfo.getPointerInfo().getLocation();
 
					// simulate a mouse click on title bar of window
					robot.mouseMove(DocumentForm.this.getX() + 10, DocumentForm.this.getY() + 5);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					// move mouse to old location
					robot.mouseMove((int)oldMouseLocation.getX(), (int)oldMouseLocation.getY());
					
					if (mouseSpeed != null) {
				        User32.INSTANCE.SystemParametersInfo(0x0071, 0, mouseSpeed, 0x02);
				    }					
				}
				catch (Exception ex) { }
			}
		});
	}


	private void save() {
		try {
			this.pt.save(textArea.getText());
		} catch (Exception e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, "Failed to save the note. All changes will be lost. Error=" + e.getMessage());
				}
			});
			e.printStackTrace();
		}
		
//		try (FileWriter fw = new FileWriter(file1.getAbsoluteFile(), false)) {
//			textArea.write(fw);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}


	private void load() {
		toolBar.setVisible(true);
		textArea.setEnabled(false);
		progressBar.setValue(0);
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					String text = DocumentForm.this.pt.load();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							int oldCaretPos = textArea.getCaretPosition();
							textArea.setText(text);
							DocumentForm.this.undoManager.discardAllEdits();
							toolBar.setVisible(false);
							progressBar.setValue(100);
							textArea.setEnabled(true);
							textArea.setCaretPosition(Math.min(oldCaretPos, text.length()));
							textArea.requestFocus();

						}
					});
				} catch (Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(null, "Failed to load the note. Error=" + e.getMessage());
						}
					});
					e.printStackTrace();
				}
			}
		});
		
//		try (FileReader fr = new FileReader(file1.getAbsoluteFile())) {
//			textArea.read(fr, file1);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	
}