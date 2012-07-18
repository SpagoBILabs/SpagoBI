/*
*
* @file WPaloPropertyServiceImpl.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: WPaloPropertyServiceImpl.java,v 1.8 2010/03/11 10:43:45 PhilippBouillon Exp $
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
