package my.app;

import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {
	
	private boolean altPressed;
	private boolean shiftPressed;
	private Runnable hook;
	private Runnable escapeHook;
	
	public void registerHook(Runnable hook) {
		this.hook = hook;
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		//System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		switch (e.getKeyCode()) {
		case NativeKeyEvent.VC_ALT_L: altPressed = true; break; 
		case NativeKeyEvent.VC_SHIFT_L: shiftPressed = true; break; 
		case NativeKeyEvent.VC_ESCAPE:  
			if (escapeHook != null) {
				SwingUtilities.invokeLater(escapeHook);
			}
			break;
		case NativeKeyEvent.VC_1:
			if (altPressed && shiftPressed) {
				if (hook != null) {
					SwingUtilities.invokeLater(hook);
				}
			}
			break;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		//System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		switch (e.getKeyCode()) {
		case NativeKeyEvent.VC_ALT_L: altPressed = false; break; 
		case NativeKeyEvent.VC_SHIFT_L: shiftPressed = false; break; 
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		//System.out.println("Key Typed: " + (int)e.getKeyChar());
		//System.out.printf("Key Typed: %c, alt=%b ,shift=%b", e.getKeyChar(), altPressed, shiftPressed);
	}

	public void init() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					GlobalScreen.unregisterNativeHook();
				} catch (NativeHookException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		GlobalScreen.addNativeKeyListener(this);
	}

	public void setEscapeHook(Runnable escapeHook) {
		this.escapeHook = escapeHook;
	}
}