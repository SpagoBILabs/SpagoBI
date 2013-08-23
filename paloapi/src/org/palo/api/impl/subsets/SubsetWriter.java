/*
*
* @file SubsetWriter.java
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
* @author Stepan Rutz, Arnd Houben
*
* @version $Id: SubsetWriter.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 20056. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.Subset;
import org.palo.api.SubsetState;
import org.palo.api.impl.xml.XMLUtil;

/**
 * <code>SubsetWriter</code>, writes subsets and their corresponding states
 * to xml.
 *
 * @author Stepan Rutz, Arnd Houben
 * @version $Id: SubsetWriter.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class SubsetWriter {
	
	private static SubsetWriter instance = new SubsetWriter();
	static final SubsetWriter getInstance() {
		return instance;
	}

	private SubsetWriter() {
	}

	final void toXML(OutputStream output, Subset subset) {
		try {
			toXMLInternal(output, subset);
		} catch (Exception e) {
			System.err.println(
					"SubsetWriter.toXML: " + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}

	private final void toXMLInternal(OutputStream output, Subset subset)
			throws Exception {
		PrintWriter w = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(output, "UTF-8"))); //$NON-NLS-1$
		try {
			w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //$NON-NLS-1$
			w.write("<?palosubset version=\"1.1\"?>\r\n"); //$NON-NLS-1$
			w.write("<subset\r\n"); //$NON-NLS-1$
			w.write("  id=\"" + XMLUtil.printQuoted(subset.getId()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			w.write("  name=\"" + XMLUtil.printQuoted(subset.getName()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			w.write("  description=\"" + XMLUtil.printQuoted(subset.getDescription()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			w.write("  sourceDimensionId=\"" + XMLUtil.printQuoted(subset.getDimension().getId()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			if (subset.getAlias() != null)
				w.write("  alias=\"" + XMLUtil.printQuoted(subset.getAlias().getName()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
			SubsetState activeState = subset.getActiveState();
			String activeStateId = activeState == null ? "" : activeState.getId(); 
			w.write("  activeStateId=\"" + XMLUtil.printQuoted(activeStateId) + "\">\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

			//----------- STATEs -----------
			SubsetState[] states = subset.getStates();
			for(int i=0;i<states.length;i++) {
				SubsetState state = states[i];
				w.write("<state\r\n");
				w.write("  id=\""+XMLUtil.printQuoted(state.getId())+"\"\r\n"); //$NON-NLS-1$
				String name = state.getName();
				if(name == null)
					name = "";
				w.write("  name=\""+XMLUtil.printQuoted(name)+"\">\r\n"); //$NON-NLS-1$
				writeExpression(w, state.getExpression());
				if (state.getSearchAttribute() != null)
					writeSearchAttribute(w, state.getSearchAttribute());
				writeElements(w, state); //.getVisibleElements());
				w.write("</state>\r\n");
			}
			w.write("</subset>\r\n"); //$NON-NLS-1$
		} finally {
			w.close();
		}
	}

	private final void writeExpression(PrintWriter w,String expression) {
		w.write("<expression expr=\""+XMLUtil.printQuoted(expression)+"\"/>\r\n"); //$NON-NLS-1$
	}
	
	private final void writeSearchAttribute(PrintWriter w, Attribute searchAttribute) {
		w.write("<search attribute=\""+XMLUtil.printQuoted(searchAttribute.getName())+"\"/>\r\n"); //$NON-NLS-1$
	}
	
	private final void writeElements(PrintWriter w, SubsetState state) {
		Element[] elements = state.getVisibleElements();
		StringBuffer elTag = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			// w.write("<element id=\"" +
			// XMLUtil.printQuoted(elements[i].getName()) + "\"/>\r\n");
			// //$NON-NLS-1$ //$NON-NLS-2$
			elTag.append("<element id=\""); //$NON-NLS-1$
			elTag.append(XMLUtil.printQuoted(elements[i].getId()));
			elTag.append("\" "); //$NON-NLS-1$
			String elPaths = 
				((SubsetStateImpl)state).getPathsAsString(elements[i]);
			if (elPaths != null) {
				elTag.append("paths=\""); //$NON-NLS-1$
				elTag.append(XMLUtil.printQuoted(elPaths));
				elTag.append("\" "); //$NON-NLS-1$
			}
			String positions = 
				((SubsetStateImpl)state).getPositionsAsString(elements[i]);
			if(positions != null && positions.length() > 0) {
				elTag.append("pos=\"");
				elTag.append(XMLUtil.printQuoted(positions));
				elTag.append("\" ");
			}
			elTag.append("/>\r\n"); //$NON-NLS-1$
		}
		w.write(elTag.toString());
	}
}
