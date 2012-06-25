/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import it.eng.spagobi.utilities.DynamicClassLoader;
import it.eng.spagobi.utilities.SpagoBIAccessUtils;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JasperReportEngineTemplate {
	String name;
	byte[] content;
	boolean propertiesLoaded;
	
	public static final String JS_FILE_ZIP = "JS_File";
	public static final String JS_EXT_ZIP = ".zip";
	
	public JasperReportEngineTemplate(String name, byte[] content) {
		setName(name);
		setContent(content);
		setPropertiesLoaded(false);
	}

	public InputStream open(File tempDir) {
		InputStream is;
		
		is = null;
		
		try {
			if (name.indexOf(".zip") > -1) {
				SpagoBIAccessUtils util = new SpagoBIAccessUtils();
				
				File fileZip = new File(tempDir, JS_FILE_ZIP + JS_EXT_ZIP);
				FileOutputStream foZip = new FileOutputStream(fileZip);
				foZip.write(content);
				foZip.close();
				util.unzip(fileZip, tempDir);
				JarFile zipFile = new JarFile(fileZip);
				Enumeration totalZipEntries = zipFile.entries();
				File jarFile = null;
				while (totalZipEntries.hasMoreElements()) {
					ZipEntry entry = (ZipEntry) totalZipEntries.nextElement();
					if (entry.getName().endsWith(".jar")) {
						jarFile = new File(tempDir, entry.getName());
						// set classloader with jar
						ClassLoader previous = Thread.currentThread().getContextClassLoader();
						DynamicClassLoader dcl = new DynamicClassLoader(jarFile, previous);
						Thread.currentThread().setContextClassLoader(dcl);
					}
					else if (entry.getName().endsWith(".jrxml")) {
						// set InputStream with jrxml
						File jrxmlFile = new File(tempDir, entry.getName());
						InputStream isJrxml = new FileInputStream(jrxmlFile);
						byte[] templateJrxml = new byte[0];
						templateJrxml = util.getByteArrayFromInputStream(isJrxml);
						is = new ByteArrayInputStream(templateJrxml);
					}
					
					if (entry.getName().endsWith(".properties")) {
						propertiesLoaded = true;
					}					
				}
			} else {
				is = new ByteArrayInputStream( content );
			}
		} catch(Throwable t) {
			throw new JasperReportEngineRuntimeException("Impossible to load template", t);
		} finally {
			
		}
		
		return is;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}


	public boolean isPropertiesLoaded() {
		return propertiesLoaded;
	}


	protected void setPropertiesLoaded(boolean propertiesLoaded) {
		this.propertiesLoaded = propertiesLoaded;
	}
	
	
}
