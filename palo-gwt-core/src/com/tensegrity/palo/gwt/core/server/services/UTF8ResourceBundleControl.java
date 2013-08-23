package com.tensegrity.palo.gwt.core.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UTF8ResourceBundleControl extends ResourceBundle.Control {
	public List<String> getFormats(String basename) {
		if (basename == null)
			throw new NullPointerException();

		return Arrays.asList("properties");
	}

	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {

		if (baseName == null || locale == null || format == null
				|| loader == null)
			throw new NullPointerException();
		ResourceBundle bundle = null;
		if (format.equals("properties")) {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format);
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();

					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}

			if (stream != null) {
				InputStreamReader is = new InputStreamReader(stream, "UTF-8");
				bundle = new PropertyResourceBundle(is);
				is.close();
			}
		}
		return bundle;
	}
}