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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JRJpegExporter extends JRImageExporter {

	public void exportReport() throws JRException {
		
		byte[] bytes;
		List bufferedImages;
		
		try {
			bytes = new byte[0];
			JasperReport report = (JasperReport)getParameter(JRImageExporterParameter.JASPER_REPORT);
			JasperPrint jasperPrint = (JasperPrint)getParameter(JRExporterParameter.JASPER_PRINT);
			bufferedImages = generateReportImages(report, jasperPrint);
			
			// calculate dimension of the final page
			Iterator iterImgs = bufferedImages.iterator();
			int totalHeight = 0;
			int totalWidth = 0;
			while(iterImgs.hasNext()){
				BufferedImage image = (BufferedImage)iterImgs.next();
				int hei = image.getHeight();
				int wid = image.getWidth();
				totalHeight += hei;
				totalWidth = wid;
			}
			// create an unique buffer image
			BufferedImage finalImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D finalGr2 = finalImage.createGraphics();
			// append all images to the final
			iterImgs = bufferedImages.iterator();
			int y = 0;
			int x = 0;
			while(iterImgs.hasNext()){
				BufferedImage image = (BufferedImage)iterImgs.next();
				int hei = image.getHeight();
				finalGr2.drawImage(image, new AffineTransform(1f,0f,0f,1f,x,y), null);
				y += hei;
			}
			// gets byte of the jpeg image 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
			JPEGEncodeParam encodeParam = encoder.getDefaultJPEGEncodeParam(finalImage);
			encodeParam.setQuality(1.0f, true);
			encoder.setJPEGEncodeParam(encodeParam);
			encoder.encode(finalImage);
			bytes = baos.toByteArray();
			baos.close();

			OutputStream out = (OutputStream)getParameter(JRExporterParameter.INPUT_STREAM);
			out.write(bytes);
		} catch (Throwable t) {
			throw new RuntimeException("Error while producing jpg image of the report", t);
		}
	}

}
