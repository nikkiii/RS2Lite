package com.rs2lite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.rs2lite.Settings.Setting;
import com.rs2lite.loader.JavaAppletLoader;
import com.rs2lite.loader.ParamaterParser;
import com.rs2lite.utils.ScreenshotTool;

/**
 * Class RS2Lite, the client's main class.
 * 
 * @author Nicole <nicole@rune-server.org> This file is protected by The BSD
 *         License, You should have recieved a copy named "BSD License.txt"
 */

public class RS2Lite {

	private static ExecutorService worker = Executors.newSingleThreadExecutor();

	/**
	 * Grabs screen size (for fullscreen) at startup
	 */
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * Boolean that stores whether the fullscreen mode is toggled
	 */
	private static boolean isFullScreen = false;

	/**
	 * Fullscreen cached window
	 */
	public static Window window;

	/**
	 * The main applet panel, the rs applet is added to this, so it is not
	 * reloaded on fullscreen toggle.
	 */
	public static JPanel appletPanel = new JPanel();

	/**
	 * Simple, the frame title.
	 */
	private static String frameTitle = "RS2 Lite - v" + Constants.VERSION;

	/**
	 * The main content frame, for holding the applet itself non fullscreen
	 */
	public static JFrame frame;

	/**
	 * RS Properties storage.
	 */
	public HashMap<String, String> props;

	/**
	 * The hide menu option
	 */
	private MenuItem hide;

	private Image logo;

	/**
	 * Tray icon
	 */
	private static TrayIcon icon;

	/**
	 * RS Applet loader
	 */
	private static JavaAppletLoader loader;

	/**
	 * Main entry point
	 * 
	 * @param args
	 *            The commandline arguments
	 */
	public static void main(String args[]) {
		new RS2Lite();
	}

	/**
	 * The constructor..
	 */
	private RS2Lite() {
		/**
		 * Set it to use the system look and feel, instead of java's default,
		 * All dialogues etc will use this
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * Load from the settings file
		 */
		Settings.loadSettings();
		/**
		 * Parse the runescape params and set the field containing them
		 */
		try {
			ParamaterParser parser = new ParamaterParser();
			props = parser.parseParamaters();
		} catch (IOException e) {
			e.printStackTrace();
		}
		init();
	}

	/**
	 * Initiates the Applet
	 */
	private void init() {
		try {
			logo = Toolkit.getDefaultToolkit().getImage(
					new URL("http://rs2lite.tk/logo.gif"));
			icon = new TrayIcon(logo);
			icon.setImageAutoSize(true);
			icon.setPopupMenu(createMenu());
			icon.setToolTip("RS2Lite Runescape loader");
			SystemTray.getSystemTray().add(icon);
			double currver = this.getCurrentVersion();
			if (currver > Constants.VERSION) {
				icon.displayMessage("Update available",
						"An RS2Lite update is available! Current version: "
								+ currver + " Download it from "
								+ Constants.WEBSITE_URL, MessageType.INFO);
				frameTitle = frameTitle + " (Update available)";
			}
			frame = new JFrame(frameTitle);
			frame.setLayout(new BorderLayout());
			frame.setIconImage(logo);
			frame.setResizable(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			loader = new JavaAppletLoader(new URL(props.get("url")), "Rs2Applet", props);
			appletPanel.setLayout(new BorderLayout());
			appletPanel.add(loader.getApplet());
			appletPanel.setPreferredSize(new Dimension(765, 503));
			frame.getContentPane().add(appletPanel, BorderLayout.CENTER);
			frame.pack();
			frame.setVisible(true);
			window = new Window(frame);
			window.setBounds(0, 0, screenSize.width, screenSize.height);
			window.setFocusable(true);
			KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.addKeyEventDispatcher(new KeyListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hide/show the frame
	 */
	public void hide() {
		frame.setVisible(!frame.isVisible());
		hide.setLabel(frame.isVisible() ? "Hide" : "Show");
	}

	/**
	 * Setup the menu for the tray icon
	 * 
	 * @return The newly created menu
	 */
	public PopupMenu createMenu() {
		PopupMenu menu = new PopupMenu();
		hide = new MenuItem("Hide");
		hide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		menu.add(hide);
		MenuItem screenshot = new MenuItem("Screenshot");
		screenshot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				screenshot();
			}
		});
		MenuItem item = new MenuItem("Exit");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RS2Lite.getLoader().getApplet().destroy();
				System.exit(0);
			}
		});
		menu.add(item);
		return menu;
	}

	/**
	 * Change the upload method
	 */
	public static void changeUploadSettings() {
		switch (Settings.getIntSetting("uploadmethod")) {
		case 0:
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(Settings
					.getSetting("screenshot_loc")));
			chooser.setDialogTitle("Select a screenshot location: ");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				Settings.remove(Setting.FILE_LOCATION);
				Settings.put(Setting.FILE_LOCATION, chooser.getSelectedFile()
						.getPath() + "\\");
				JOptionPane.showMessageDialog(frame,
						"Screenshot location changed to:\n"
								+ chooser.getSelectedFile().getPath(),
						"Screenshot location changed",
						JOptionPane.INFORMATION_MESSAGE);
				Settings.writeSettings(false);
			} else {
				JOptionPane.showMessageDialog(frame,
						"No folder selection was made!", "ERROR!",
						JOptionPane.ERROR_MESSAGE);
			}
			break;
		case 1:
			String apikey = JOptionPane
					.showInputDialog("Input your Imgur api key:");
			if (apikey == null || apikey.equals("")) {
				JOptionPane.showMessageDialog(frame,
						"Invalid api key entered!", "Invalid api key",
						JOptionPane.ERROR_MESSAGE);
			} else {
				Settings.put(Setting.IMGUR_KEY, apikey);
				Settings.writeSettings(false);
			}
			break;
		}
	}

	/**
	 * Take a screenshot
	 * 
	 * @param hide
	 */
	public static void screenshot() {
		final UploadMethod uploadmethod = UploadMethod.values()[Settings
				.getIntSetting("uploadmethod")];
		if (!Settings.contains(Setting.IMGUR_KEY)
				&& uploadmethod == UploadMethod.IMGUR) {
			JOptionPane
					.showMessageDialog(
							frame,
							"You are using a method that uses an API Key, please press f11 to configure it.",
							"Error", JOptionPane.ERROR_MESSAGE);
		} else {
			worker.execute(new Runnable() {
				public void run() {
					ScreenshotTool.createScreenshot(uploadmethod);
				}
			});
		}
	}

	/**
	 * @return True, if rs2lite is running in fullscreen
	 */
	public static boolean isFullscreen() {
		return isFullScreen;
	}

	/**
	 * Get the RS Loader
	 * 
	 * @return The loader instance
	 */
	public static JavaAppletLoader getLoader() {
		return loader;
	}

	/**
	 * Get the TrayIcon instance (The system tray icon)
	 * 
	 * @return The instance
	 */
	public static TrayIcon getIcon() {
		return icon;
	}

	/**
	 * Toggle fullscreen mode
	 */
	public static void toggleFullscreen() {
		if (!isFullScreen) {
			frame.getContentPane().remove(appletPanel);
			window.add(appletPanel);
			window.setVisible(true);
			frame.setTitle("RS2 Lite - Fullscreen");
			isFullScreen = true;
		} else {
			window.remove(appletPanel);
			frame.getContentPane().add(appletPanel, BorderLayout.CENTER);
			window.setVisible(false);
			frame.pack();
			frame.setTitle(frameTitle);
			isFullScreen = false;
		}
	}

	/**
	 * Read the current version from the website
	 * 
	 * @return The current RS2Lite version
	 */
	public double getCurrentVersion() {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(
					new URL("http://rs2lite.tk/ver.txt").openStream()));
			return Double.parseDouble(r.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Constants.VERSION;
	}

	/**
	 * The enum containing the current upload types
	 * 
	 * @author Nikki
	 * 
	 */
	public enum UploadMethod {
		FILE, IMGUR
	}
}