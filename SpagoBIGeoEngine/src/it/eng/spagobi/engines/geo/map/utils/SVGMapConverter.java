/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.geo.map.utils;

import it.eng.spagobi.utilities.service.IStreamEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

// TODO: Auto-generated Javadoc
/**
 * The Class SVGMapConverter.
 * 
 * @author Andrea Gioia
 */
public class SVGMapConverter implements IStreamEncoder {
	
	public void encode(InputStream inputStream,	OutputStream outputStream) throws IOException {
		SVGMapConverter.SVGToJPEGTransform(inputStream, outputStream);
	}
	
	/**
	 * Transform the svg file into a jpeg image.
	 * 
	 * @param inputStream the strema of the svg map
	 * @param outputStream the output stream where the jpeg image is written
	 * 
	 * @throws Exception raised if some errors occur during the elaboration
	 */
	public static void SVGToJPEGTransform(InputStream inputStream,	OutputStream outputStream) throws IOException {
		// create a JPEG transcoder
		JPEGTranscoder t = new JPEGTranscoder();
		
		// set the transcoding hints
		t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
		t.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(1000));
		t.addTranscodingHint(JPEGTranscoder.KEY_ALLOWED_SCRIPT_TYPES, "*");
		t.addTranscodingHint(JPEGTranscoder.KEY_CONSTRAIN_SCRIPT_ORIGIN, new Boolean(true));
		t.addTranscodingHint(JPEGTranscoder.KEY_EXECUTE_ONLOAD, new Boolean(true));
		
		
		
		
		
		// create the transcoder input
		Reader reader = new InputStreamReader(inputStream);
		TranscoderInput input = new TranscoderInput(reader);
		
		// create the transcoder output
		TranscoderOutput output = new TranscoderOutput(outputStream);
		
		// save the image
		try {
			t.transcode(input, output);
		} catch (TranscoderException e) {
			throw new IOException("Impossible to convert svg to jpeg: " + e.getCause());
		}
	}

	
}
