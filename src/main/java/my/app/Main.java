package my.app;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Main {
	
	  static {
	      LogManager.getLogManager().reset();
	      SLF4JBridgeHandler.install();
	  }	

	public static void main(String[] args) throws Exception {
		
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Don't forget to disable the parent handlers.
		//logger.setUseParentHandlers(false);
		
		Config conf = new Config();
		try {
			conf = Config.load();
		} catch (Exception e) {
			conf.save();
		}
		
		ProtectedTextSite pt = new ProtectedTextSite(conf);
		
		System.out.println("trying to load memo from site");
		
		pt.load(); // prefetch

		System.out.println("finish loading memo from site");
		System.out.println("press Alt-Shift-1 to open popup window");

		
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
