package com.rs2lite.utils;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rs2lite.RS2Lite;
import com.rs2lite.RS2Lite.UploadMethod;
import com.rs2lite.Settings;
import com.rs2lite.Settings.Setting;

public class ScreenshotTool {

	// The robot instance used for Screenshots
	private static Robot robot;

	// The date format used for Screenshots
	private static SimpleDateFormat format = new SimpleDateFormat(
			"MM-dd-yyyy HH.mm.ss a");

	/**
	 * Creates a screenshot at the specified directory
	 * 
	 * @return Returns the path of the newly created screenshot
	 */
	public static String createScreenshot(UploadMethod uploadMethod) {
		try {
			if (robot == null) {
				robot = new Robot();
			}
			Rectangle size = Utils.getSize();
			BufferedImage image = robot.createScreenCapture(size);
			switch (uploadMethod) {
			case FILE:
				String filename = format.format(new Date()) + ".png";
				File f = new File(Settings.getSetting(Setting.FILE_LOCATION));
				if (!f.exists()) {
					f.mkdirs();
				}
				f = new File(Settings.getSetting(Setting.FILE_LOCATION)
						+ filename);
				ImageIO.write(image, "PNG", f);
				RS2Lite.getIcon().displayMessage(
						"Screenshot saved successfully!",
						"Screenshot saved to " + f.getPath(), MessageType.INFO);
				break;
			case IMGUR:
				/**
				 * Create the output stream and input stream
				 */
				URL url = new URL("http://api.imgur.com/2/upload.xml");
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ImageIO.write(image, "PNG", output);
				/**
				 * Encode the image into a base64 string using apache commons
				 * codec
				 */
				String data = URLEncoder.encode("image", "UTF-8")
						+ "="
						+ URLEncoder
								.encode(Base64.encodeBase64String(output
										.toByteArray()), "UTF-8");
				data += "&"
						+ URLEncoder.encode("key", "UTF-8")
						+ "="
						+ URLEncoder
								.encode(Settings.getSetting(Setting.IMGUR_KEY),
										"UTF-8");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				/**
				 * Write the image data and api key
				 */
				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());
				writer.write(data);
				writer.flush();
				writer.close();
				/**
				 * Parse the URL from the response
				 */
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.parse(connection.getInputStream());
				NodeList nodeList = d.getElementsByTagName("original").item(0)
						.getChildNodes();
				Node n = (Node) nodeList.item(0);
				/**
				 * Set the system clipboard to the url, and display a message.
				 */
				Utils.setClipboard(n.getNodeValue());
				RS2Lite.getIcon().displayMessage("Uploaded successfully!",
						"Image uploaded to " + n.getNodeValue(),
						MessageType.INFO);
				break;
			}
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							RS2Lite.frame,
							"Error uploading/saving your image, please check your settings",
							"Error!", JOptionPane.ERROR_MESSAGE);
		}
		return "";
	}
}
