/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
