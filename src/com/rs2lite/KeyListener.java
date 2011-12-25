package com.rs2lite;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

/**
 * Class KeyListener, the loader's listener to listen for hotkeys.
 * 
 * @author Nicole <nicole@rune-server.org> This file is protected by The BSD
 *         License, You should have recieved a copy named "BSD License.txt"
 */

public class KeyListener implements KeyEventDispatcher {

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.isControlDown() && e.getID() == KeyEvent.KEY_PRESSED) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_F9:
				Object[] options = { "File", "Imgur" };
				int n = JOptionPane
						.showOptionDialog(
								RS2Lite.frame,
								"Which screenshot method would you like?\nPlease note, Imgur requires an api key to function",
								"Upload method", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, // do not
																	// use a
																	// custom
																	// Icon
								options, // the titles of buttons
								options[0]); // default button title
				Settings.put("uploadmethod", n + "");
				Settings.writeSettings(false);
				break;
			case KeyEvent.VK_F10:
				RS2Lite.toggleFullscreen();
				break;
			case KeyEvent.VK_F11:
				RS2Lite.changeUploadSettings();
				break;
			case KeyEvent.VK_F12:
				RS2Lite.screenshot();
				break;
			}
		}
		return false;
	}
}
