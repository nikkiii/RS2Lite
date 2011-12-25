package com.rs2lite.loader;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Class GameAppletLoader, loads the runescape applet.
 * 
 * @author Nicole <nicole@rune-server.org> This file is protected by The BSD
 *         License, You should have recieved a copy named "BSD License.txt"
 */

public class JavaAppletLoader implements AppletStub {

	private URL base;
	private Applet app;
	private Map<String, String> props;

	public JavaAppletLoader(URL jarUrl, String className) {
		this(jarUrl, className, new HashMap<String, String>());
	}
	
	public JavaAppletLoader(URL jarUrl, String className, Map<String, String> props) {
		this.props = props;
		try {
			base = new URL(props.get("base"));
			URLClassLoader localURLClassLoader = new URLClassLoader(
					new URL[] { jarUrl });
			app = (Applet) localURLClassLoader.loadClass(className)
					.newInstance();
			app.setStub(this);
			app.setVisible(true);
			app.init();
			app.start();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public Applet getApplet() {
		return app;
	}

	@Override
	public boolean isActive() {
		return app.isActive();
	}

	@Override
	public void appletResize(int arg0, int arg1) {

	}

	@Override
	public AppletContext getAppletContext() {
		return null;
	}

	@Override
	public URL getDocumentBase() {
		return base;
	}

	@Override
	public URL getCodeBase() {
		return base;
	}

	@Override
	public String getParameter(String req) {
		return props.get(req);
	}
}