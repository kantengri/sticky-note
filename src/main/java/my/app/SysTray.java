package my.app;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SysTray {
	
	
	
	public void run() {
		
	    SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run () {
	            try {
	                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                go();
	            } catch (Exception e) {
	                System.out.println("Not using the System UI defeats the purpose...");
	                e.printStackTrace();
	            }
	        }

	    });
	    
	}
	
	private void go() throws Exception {
	    
		// Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final JPopupMenu popup = new JPopupMenu();
		Image pic = ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png"));
		final TrayIcon trayIcon = new TrayIcon(pic);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Sticky Notes App");
				
			}
		});
		
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Sticky Notes App");
			}
		});
		JMenuItem displayItem = new JMenuItem("Display Note");
		displayItem.setMnemonic(KeyEvent.VK_A); 
		displayItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK | ActionEvent.SHIFT_MASK));
		displayItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "Sticky Notes App");
			}
		});
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Add components to pop-up menu
		popup.add(aboutItem);
		popup.add(displayItem);
		popup.add(exitItem);

		//trayIcon.setPopupMenu(popup);
		
		trayIcon.addMouseListener (new MouseAdapter () {
            @Override
            public void mouseReleased (MouseEvent me) {
                if (me.isPopupTrigger()) {
                    popup.setLocation(me.getX(), me.getY());
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                }
            }
        });		

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}
		
	}
}
