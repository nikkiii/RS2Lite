package com.rs2lite.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rs2lite.Constants;
import com.rs2lite.utils.Utils;

/**
 * Class ParamaterParser, opens Jagex's non-javascript page and grabs
 * paramaters.
 * 
 * @author Nicole <nicole@rune-server.org>
 * This file is protected by The BSD License, You should have
 *         recieved a copy named "BSD License.txt"
 */

public class ParamaterParser {

	public ParamaterParser() {
	}

	public String readPage(URL url) {
		try {
			URLConnection connection = url.openConnection();
			connection
					.addRequestProperty(
							"Accept",
							"text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			connection.addRequestProperty("Accept-Charset",
					"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			connection.addRequestProperty("Accept-Encoding", "gzip,deflate");
			connection.addRequestProperty("Accept-Language", "en-gb,en;q=0.5");
			connection.addRequestProperty("Connection", "keep-alive");
			connection.addRequestProperty("Host", "www.runescape.com");
			connection.addRequestProperty("Keep-Alive", "300");
			connection.addRequestProperty("User-Agent",
					Utils.getHttpUserAgent());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			StringBuilder contents = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				contents.append(line).append("\n");
			}
			return contents.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<String, String> parseParamaters() throws MalformedURLException {
		HashMap<String, String> params = new HashMap<String, String>();
		String contents = readPage(new URL(Constants.GAME_URL));
		Pattern pattern = Pattern.compile("src\\=\"(.*?)\"");
		Matcher serverMatcher = pattern.matcher(contents);
		if(!serverMatcher.find()) {
			throw new RuntimeException("Could not find the runescape server url!");
		}
		String server = serverMatcher.group(1);
		contents = readPage(new URL(serverMatcher.group(1)));
		pattern = Pattern
				.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">");
		Matcher matcher = pattern.matcher(contents);
		while (matcher.find()) {
			String param_name = Utils.trim(matcher.group(1), '\"');
			String param_value = Utils.trim(matcher.group(2), '\"');
			params.put(param_name, param_value);
		}
		if(params.containsKey("haveie6")) {
			params.remove("haveie6");
		}
		pattern = Pattern.compile("archive=(.*?)\\.jar");
		matcher = pattern.matcher(contents);
		if(!matcher.find()) {
			throw new RuntimeException("Could not find the archive!");
		}
		params.put("base", server);
		params.put("url", server.substring(0, server.lastIndexOf('/')+1) + matcher.group(1)+".jar");
		return params;
	}
}
