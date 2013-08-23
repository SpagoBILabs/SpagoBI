/*
*
* @file CubeViewWriter.java
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
* @version $Id: CubeViewWriter.java,v 1.3 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.Axis;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Property;
import org.palo.viewapi.internal.io.xml.AxisHandler;
import org.palo.viewapi.internal.io.xml.FormatHandler;
import org.palo.viewapi.internal.io.xml.PropertyHandler;
import org.palo.viewapi.uimodels.formats.Format;

/**
 * <code>CubeViewWriter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewWriter.java,v 1.3 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public class CubeViewWriter {
	
	private static CubeViewWriter instance = new CubeViewWriter();
	static final CubeViewWriter getInstance() {
		return instance;
	}

	private CubeViewWriter() {
	}

	public final void toXML(OutputStream out, CubeView view) throws PaloIOException {
		try {
			toXMLInternal(out, view);
		} catch (Exception e) {
			PaloIOException pex = 
				new PaloIOException("Writing cube view to xml failed!",e);
			pex.setData(view);
			throw pex;
		}
	}

	private final void toXMLInternal(OutputStream output, CubeView view)
			throws Exception {
		PrintWriter w = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(output, "UTF-8"))); //$NON-NLS-1$
		try {
			// preamble:
			w.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			w.write("<?palocubeview version=\"0.1\"?>\r\n");
			
			//cube view element:
			writeViewElement(w, view);
			
			// write properties:
			Property <Object> [] properties = view.getProperties();
			for (Property <Object> prop: properties) {
				String propertyXML = PropertyHandler.getPersistenceString(prop);
				w.write(propertyXML);
			}
			
			// write axis:
			Axis[] axes = view.getAxes();
			for(Axis axis : axes) {
				String axisXML = AxisHandler.getPersistenceString(axis);
				w.write(axisXML);
			}	
			
			// write formats:
			Format [] formats = view.getFormats();
			for (Format format: formats) {
				String formatXML = FormatHandler.getPersistenceString(format);
				w.write(formatXML);
			}
			
			// done
			w.write("</view>\r\n"); //$NON-NLS-1$
		} finally {
			w.close();
		}
	}

	private final void writeViewElement(PrintWriter w, CubeView view) {
		String id = view.getId();
		String name = view.getName();
		String cubeId = view.getCube().getId();		
		StringBuffer viewElement = new StringBuffer();
		viewElement.append("<view id=\"");
		viewElement.append(id);
		viewElement.append("\" name=\"");
		viewElement.append(modify(name));
		viewElement.append("\" cube=\"");
		viewElement.append(cubeId);
		viewElement.append("\">\r\n");
		w.write(viewElement.toString());
	}
	
	protected String modify(String x) {
		x = x.replaceAll("&", "&amp;");
		x = x.replaceAll("\"", "&quot;");
		x = x.replaceAll("\'", "&apos;");
		x = x.replaceAll("<", "&lt;");
		x = x.replaceAll(">", "&gt;");
		return x;
	}	
}
