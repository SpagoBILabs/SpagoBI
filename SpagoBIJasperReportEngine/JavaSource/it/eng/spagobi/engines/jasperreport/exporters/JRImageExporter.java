/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport.exporters;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class JRImageExporter implements JRExporter {

	Map<JRExporterParameter, Object> parameters;
	
	public JRImageExporter() {
		parameters = new HashMap<JRExporterParameter, Object>();
	}
	
	public abstract void exportReport() throws JRException;

	public Object getParameter(JRExporterParameter parameter) {
		return parameters.get(parameter);
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameter(JRExporterParameter parameter, Object value) {
		parameters.put(parameter, value);
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
	
	protected List generateReportImages(JasperReport report, JasperPrint jasperPrint) {
		List bufferedImages = new ArrayList();
		try{
			int height = report.getPageHeight();
			int width = report.getPageWidth();
			boolean export = true;
			int index = 0;
			while(export==true){
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D gr2 = image.createGraphics();
				JRExporter exporter = new JRGraphics2DExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, gr2 );
				exporter.setParameter(JRGraphics2DExporterParameter.PAGE_INDEX, new Integer(index));
				try{
					exporter.exportReport();
				} catch(Exception e) {
					export = false;
					continue;
				}
				index++;
				bufferedImages.add(image);	
			}
		} catch (Throwable t) {
			throw new RuntimeException("Impossible to generate image", t);
		}
		return bufferedImages;
	}

}
