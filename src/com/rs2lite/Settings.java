package com.rs2lite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import com.rs2lite.utils.Utils;

/**
 * Class Settings, the loader's setting class
 * 
 * @author Nicole <nicole@rune-server.org> This file is protected by The BSD
 *         License, You should have recieved a copy named "BSD License.txt"
 */

public class Settings {

	public static final String DATA_SAVE_FILE = "preferences.dat";

	public static class Setting {

		public static final String RS2Lite_KEY = "rs2lite_api";
		public static final String IMGUR_KEY = "imgur_api";
		public static final String FILE_LOCATION = "screenshot_loc";

	}

	private static HashMap<String, String> settings = new HashMap<String, String>();

	/**
	 * Loads all settings into the settings map
	 */
	public static void loadSettings() {
		try {
			File datafile = new File(Utils.getWorkingDirectory(),
					DATA_SAVE_FILE);
			if (!datafile.exists()) {
				datafile.createNewFile();
				writeSettings(true);
			}
			BufferedReader reader = new BufferedReader(new FileReader(datafile));
			String line;
			while ((line = reader.readLine()) != null) {
				settings.put(line.substring(0, line.indexOf("=")),
						line.substring(line.indexOf("=") + 1));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes settings to the settings file
	 * 
	 * @param iscreated
	 *            , If the file was created or re-writing it.
	 */
	public static void writeSettings(boolean iscreated) {
		try {
			File datafile = new File(Utils.getWorkingDirectory(),
					DATA_SAVE_FILE);
			BufferedWriter writer = new BufferedWriter(new FileWriter(datafile,
					false));
			if (iscreated) {
				writer.write("screenshot_loc="
						+ new File(Utils.getWorkingDirectory(), "Screenshots/")
								.getPath());
				writer.newLine();
				writer.write("uploadmethod=0");
				writer.newLine();
			} else {
				for (String key : settings.keySet()) {
					writer.write(key + "=" + settings.get(key));
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Gets a setting from the Settings map
	 */
	public static String getSetting(String key) {
		if (!settings.containsKey(key)) {
			return null;
		}
		return settings.get(key);
	}

	public static int getIntSetting(String key) {
		return Integer.parseInt(getSetting(key));
	}

	public static void put(String string, String string2) {
		settings.put(string, string2);
	}

	public static void remove(String string) {
		settings.remove(string);
	}

	public static boolean contains(String string) {
		return settings.containsKey(string);
	}

	public static String getRS2LiteKey() {
		return settings.get(Setting.RS2Lite_KEY);
	}

	public static String getImgurKey() {
		return settings.get(Setting.IMGUR_KEY);
	}
}
