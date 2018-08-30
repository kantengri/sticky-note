package my.app;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SysTray {
	
	protected JPopupMenu systemTrayPopupMenu;
	private Runnable hook;
	
	
	
	public void run() throws Exception {
	    
		// Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		Image pic = ImageIO.read(Main.class.getClassLoader().getResourceAsStream("icon.png"));
		final TrayIcon trayIcon = new TrayIcon(pic);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hook != null) {
					hook.run();
				}
			}
		});
		
		final SystemTray tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		MenuItem aboutItem = new MenuItem("About");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Sticky Notes App");
			}
		});
		MenuItem displayItem = new MenuItem("Display Note");
		displayItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hook != null) {
					hook.run();
				}
			}
		});
		
		MenuItem exitItem = new MenuItem("Exit");
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

		trayIcon.setPopupMenu(popup);
		
		

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}
		
	}



	public void registerHook(Runnable runnable) {
		this.hook = runnable;
	}
}
