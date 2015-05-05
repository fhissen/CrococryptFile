package org.fhissen.utils.ui;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class SwingHacks {
	private static boolean HTML_BUTTONS = false;
	public static final void makeHTMLButtonSpacable(){
		if(HTML_BUTTONS) return;
		HTML_BUTTONS = true;
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent ke) {
				if(ke.getID() == KeyEvent.KEY_RELEASED) return false;
				if(ke.getKeyCode() != KeyEvent.VK_ENTER && ke.getKeyCode() != KeyEvent.VK_SPACE) return false;
					
				if(ke.getSource() instanceof JButton){
					JButton jb = (JButton) ke.getSource();
					try {
						for(MouseListener ml: jb.getMouseListeners())
							ml.mouseReleased(new MouseEvent(jb, 1, 1, 1, -1, -1, 1, false, MouseEvent.BUTTON1));
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}

				return false;
			}
		});
	}
}
