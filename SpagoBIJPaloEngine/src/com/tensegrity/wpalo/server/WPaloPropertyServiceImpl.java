/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import com.tensegrity.palo.gwt.core.server.services.BasePaloServiceServlet;
import com.tensegrity.wpalo.client.WPaloPropertyService;

public class WPaloPropertyServiceImpl extends BasePaloServiceServlet implements WPaloPropertyService {

	/** generated */
	private static final long serialVersionUID = 3961958345532639833L;
	
	
	private Properties wpaloProperties;
	private String buildNumber;
	
	public WPaloPropertyServiceImpl() {
		wpaloProperties = loadProperties();
	}

	public String getBuildNumber() {
		return buildNumber;
	}
	
	public boolean getBooleanProperty(String name, boolean defaultValue) {
//		System.err.print("Getting bool property '" + name + "': ");
		String value = wpaloProperties.getProperty(name);
		if (value != null) {
			try {
				boolean result = Boolean.parseBoolean(value);
//				System.err.println(result);
				return result;
			} catch (Exception ex) { /* ignore, return default value on error */ }
		}
//		System.err.println("Not found or invalid, thus setting to " + defaultValue);
		return defaultValue;
	}
	public String getStringProperty(String name) {
		String value = wpaloProperties.getProperty(name);
		if (value != null) {
			return value;
		}
		return "";
	}	
	public int getIntProperty(String name, int defaultValue) {
//		System.err.print("Getting int  property '" + name + "': ");
		String value = wpaloProperties.getProperty(name);
		if (value != null) {
			try {				
				int result = Integer.parseInt(value);
//				System.err.println(result);
				return result;
			} catch (Exception ex) { /* ignore, return default value on error */ }
		}
//		System.err.println("Not found or invalid, thus setting to " + defaultValue);
		return defaultValue;
	}

	private final Properties loadProperties() {
		String file = "/wpalo.properties";
		Properties props = new Properties();
		try {
			InputStream propsIn = getClass().getResourceAsStream(file);
			BufferedInputStream bis = new BufferedInputStream(propsIn);
			if (bis != null) {
				props.load(bis);
				bis.close();
			}
		} catch (IOException e) {
			System.err.println("couldn't read properties from '" + file +" !!");
			throw new RuntimeException("Couldn't read properties from '" + file
					+ "' !!");
		}
		file = "/build.number";
		try {
			InputStream bnIn = getClass().getResourceAsStream(file);
			InputStreamReader reader = new InputStreamReader(bnIn);
			char [] buffer = new char[4096];
			StringBuffer sBuffer = new StringBuffer();
			int read;
			while ((read = reader.read(buffer)) != -1) {
				sBuffer.append(buffer, 0, read);
			}
			reader.close();
			if ((read = sBuffer.indexOf("build.number=")) != -1) {
				buildNumber = sBuffer.substring(read + 13).trim();
				if ((read = buildNumber.indexOf("\n")) != -1) {
					buildNumber = buildNumber.substring(0, read).trim();
				}
			}
		} catch (Exception e) {
			// Ignore
		}
		return props;		
	}

	public String[] getCurrentBuildNumber() {
		try {
			URL url = new URL("http://www.jpalo.com/Download/palo-pivot-update-info.txt");
			URLConnection con = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				buf.append(line + "\n");
			}
			br.close();
			return buf.toString().trim().split(",");
		} catch (Throwable t) {
		}
		return null;
	}

}
