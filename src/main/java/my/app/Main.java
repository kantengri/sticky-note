package my.app;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Don't forget to disable the parent handlers.
		logger.setUseParentHandlers(false);
		
		Config conf = new Config();
		try {
			conf = Config.load();
		} catch (Exception e) {
			conf.save();
		}
		
		ProtectedTextSite pt = new ProtectedTextSite();
		
		final DocumentForm docForm = new DocumentForm(pt);
		docForm.setAlwaysOnTop(true);
		
		GlobalKeyListener globalKeyListener = new GlobalKeyListener();
		final Runnable showAction = new Runnable() {
			@Override
			public void run() {
				docForm.setVisible(true);
			}
		};
		globalKeyListener.registerHook(showAction);
		globalKeyListener.setEscapeHook(new Runnable() {
			
			@Override
			public void run() {
				docForm.setVisible(false);
			}
		});
		globalKeyListener.init();
		SysTray sysTray = new SysTray();
		sysTray.registerHook(showAction);
		sysTray.run();
	}

}
