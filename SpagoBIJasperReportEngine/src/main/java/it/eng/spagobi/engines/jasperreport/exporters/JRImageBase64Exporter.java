/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.jasperreport.exporters;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import sun.misc.BASE64Encoder;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JRImageBase64Exporter extends JRImageExporter {

	@Override
	public void exportReport() throws JRException {
		byte[] bytes;
		List bufferedImages;
		try {
			bytes = new byte[0];
			String message = "<IMAGES>";
			JasperReport report = (JasperReport) getParameter(JRImageExporterParameter.JASPER_REPORT);
			JasperPrint jasperPrint = (JasperPrint) getParameter(JRExporterParameter.JASPER_PRINT);

			bufferedImages = generateReportImages(report, jasperPrint);
			Iterator iterImgs = bufferedImages.iterator();
			int count = 1;
			while (iterImgs.hasNext()) {
				message += "<IMAGE page=\"" + count + "\">";
				BufferedImage image = (BufferedImage) iterImgs.next();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
				ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
				imageWriter.setOutput(ios);
				IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
				ImageWriteParam par = imageWriter.getDefaultWriteParam();
				par.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
				par.setCompressionQuality(1.0f);
				imageWriter.write(imageMetaData, new IIOImage(image, null, null), par);

				// JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baos);
				// JPEGEncodeParam encodeParam = encoder.getDefaultJPEGEncodeParam(image);
				// encodeParam.setQuality(1.0f, true);
				// encoder.setJPEGEncodeParam(encodeParam);
				// encoder.encode(image);

				byte[] byteImg = baos.toByteArray();

				baos.close();
				BASE64Encoder encoder64 = new BASE64Encoder();
				String encodedImage = encoder64.encode(byteImg);

				message += encodedImage;
				message += "</IMAGE>";
				count++;

				imageWriter.dispose();
			}
			message += "</IMAGES>";
			bytes = message.getBytes();

			OutputStream out = (OutputStream) getParameter(JRExporterParameter.OUTPUT_STREAM);
			out.write(bytes);
		} catch (Throwable t) {
			throw new RuntimeException("Error while producing byte64 encoding of the report images", t);
		}
	}

	@Override
	public ReportContext getReportContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfiguration(ReportExportConfiguration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setConfiguration(ExporterConfiguration arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExporterInput(ExporterInput arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExporterOutput(ExporterOutput arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReportContext(ReportContext arg0) {
		// TODO Auto-generated method stub

	}

}
