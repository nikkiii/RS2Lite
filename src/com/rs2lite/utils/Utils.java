package com.rs2lite.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;

import com.rs2lite.RS2Lite;

/**
 * Class Utils, the client's util class.
 * 
 * @author Nicole <nicole@rune-server.org>
 * @author Gnarly This file is protected by The BSD License, You should have
 *         recieved a copy named "BSD License.txt"
 */

public class Utils {

	public static enum OperatingSystem {
		LINUX, SOLARIS, WINDOWS, MAC, UNKNOWN
	}

	private static File workDir = null;

	/**
	 * Get the computer's user agent
	 * 
	 * @author Gnarly
	 */
	public static String getHttpUserAgent() {
		String os = System.getProperty("os.name").toLowerCase();
		String agent = "Mozilla/5.0 (X11; U; Linux i686; en-GB; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6";
		if (os.contains("mac")) {
			agent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/531.9 (KHTML, like Gecko) Version/4.0.3 Safari/531.9";
		} else if (os.contains("windows")) {
			agent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6";
		}
		return agent;
	}

	/**
	 * Get the current operating system
	 * @return
	 * 		The operating system
	 */
	public static OperatingSystem getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OperatingSystem.WINDOWS;
		if (osName.contains("mac"))
			return OperatingSystem.MAC;
		if (osName.contains("solaris"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("sunos"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("linux"))
			return OperatingSystem.LINUX;
		if (osName.contains("unix"))
			return OperatingSystem.LINUX;
		return OperatingSystem.UNKNOWN;
	}

	/**
	 * Get the current screen
	 * @return
	 * 		The applet location, or frame if the applet is null
	 */
	public static Rectangle getSize() {
		Rectangle size = RS2Lite.frame.getBounds();
		if (RS2Lite.getLoader() != null) {
			Point p = RS2Lite.getLoader().getApplet().getLocationOnScreen();
			Rectangle panelsize = RS2Lite.getLoader().getApplet().getBounds();
			size = new Rectangle((int) p.getX(), (int) p.getY(),
					(int) panelsize.getWidth(), (int) panelsize.getHeight());
		}
		if (RS2Lite.isFullscreen()) {
			size = RS2Lite.window.getBounds();
		}
		return size;
	}

	/**
	 * Get the working directory
	 * @return
	 * 		The working directory
	 */
	public static File getWorkingDirectory() {
		if (workDir == null)
			workDir = getWorkingDirectory("rs2lite");
		return workDir;
	}

	/**
	 * Get the working directory for an application
	 * @param applicationName
	 * 				The application name
	 * @return
	 * 		The directory
	 */
	public static File getWorkingDirectory(String applicationName) {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (getPlatform()) {
		case LINUX:
		case SOLARIS:
			workingDirectory = new File(userHome, '.' + applicationName + '/');
			break;
		case WINDOWS:
			String applicationData = System.getenv("APPDATA");
			if (applicationData != null)
				workingDirectory = new File(applicationData, "."
						+ applicationName + '/');
			else
				workingDirectory = new File(userHome,
						'.' + applicationName + '/');
			break;
		case MAC:
			workingDirectory = new File(userHome,
					"Library/Application Support/" + applicationName);
			break;
		default:
			workingDirectory = new File(userHome, applicationName + '/');
			break;
		}
		if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
			throw new RuntimeException(
					"The working directory could not be created: "
							+ workingDirectory);
		return workingDirectory;
	}

	/**
	 * Set the clipboard to the specified text
	 * @param nodeValue
	 * 			The text
	 */
	public static void setClipboard(String nodeValue) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(nodeValue);
		clipboard.setContents(transferable, null);
	}

	/**
	 * Trim the string's start/finish of the specified character
	 * @param str
	 * 			The string
	 * @param ch
	 * 			The character
	 * @return
	 * 			The trimmed string
	 */
	public static String trim(String str, final char ch) {
		if ((str == null) || str.isEmpty())
			return str;
		else if (str.length() == 1)
			return str.charAt(0) == ch ? "" : str;
		try {
			if(str.charAt(0) == ch)
				str = str.substring(1);
			final int l = str.length() - 1;
			if (str.charAt(l) == ch)
				str = str.substring(0, l);
			return str;
		} catch (final Exception e) {
			return str;
		}
	}
}
