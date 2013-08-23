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
* @author Stepan Rutz
*
* @version $Id: CubeViewWriter.java,v 1.20 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 20057. All rights reserved.
 */
package org.palo.api.impl.views;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.palo.api.Axis;
import org.palo.api.CubeView;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.Subset;
import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;

/**
 * <code>CubeViewWriter</code>, writes cube views to xml.
 *
 * @author Stepan Rutz
 * @author Arnd Houben
 * @version $Id: CubeViewWriter.java,v 1.20 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class CubeViewWriter {
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
    private static CubeViewWriter instance = new CubeViewWriter();    
    static CubeViewWriter getInstance() {
		return instance;
	}
    
    private CubeViewWriter() {
	}
    

    public void toXML(OutputStream output, CubeView view) {
		try {
			toXMLInternal(output, view);
		} catch (Exception e) {
			System.err
					.println("CubeViewWriter.toXML: " + e.getLocalizedMessage()); //$NON-NLS-1$
		}
	}
        
    private void toXMLInternal(OutputStream output, CubeView view) throws Exception {
        PrintWriter w = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(output, "UTF-8")),true); //$NON-NLS-1$
        try {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"); //$NON-NLS-1$
            w.write("<?paloview version=\"1.4\"?>\r\n"); //$NON-NLS-1$
            w.write("<view\r\n"); //$NON-NLS-1$
            w.write("  id=\"" + XMLUtil.printQuoted(view.getId()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            w.write("  name=\"" + XMLUtil.printQuoted(view.getName()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            w.write("  description=\"" + XMLUtil.printQuoted(view.getDescription()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            w.write("  cube=\"" + XMLUtil.printQuoted(view.getCube().getId()) + "\"\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            w.write(">\r\n"); //$NON-NLS-1$
            String [] keys = view.getProperties();
            for (int i = 0, n = keys.length; i < n; i++) {
           		w.write(" <property id=\"" + XMLUtil.printQuoted(keys[i]) + "\" value=\"" +
           				      XMLUtil.printQuoted(view.getPropertyValue(keys[i])) + "\"/>\r\n");
           	}
            
            Axis[] axes = view.getAxes();
            writeAxes(w,axes);
            w.write("</view>\r\n"); //$NON-NLS-1$
        }
        finally
        {
            w.close();
        }
    }
    
    
    private final void writeAxes(Writer w, Axis[] axes) throws IOException {
		for (int i = 0; i < axes.length; ++i) {
			Axis axis = axes[i];
			w.write("<axis id=\"" + XMLUtil.printQuoted(axis.getId())
					+ "\" name=\"" + XMLUtil.printQuoted(axis.getName())
					+ "\">\r\n"); //$NON-NLS-1$
			Hierarchy [] hierarchies = axis.getHierarchies();
			Dimension [] dimensions = axis.getDimensions();
			writeHierarchies(w, hierarchies);
			writeSelectedElements(w, hierarchies, axis);
			writeActiveSubsets(w, dimensions, axis);
			writeExpandedPaths(w, dimensions, axis);
//			writeHiddenPaths(w, dimensions, axis);
			writeVisiblePaths(w, hierarchies, axis);
			writeProperties(w,axis);
			w.write("</axis>\r\n"); //$NON-NLS-1$
		}
	}
    
//    private final void writeDimensions(Writer w, Dimension[] dimensions)
//			throws IOException {
//    	if(dimensions.length == 0)
//    		return;
//    	w.write("  <dimensions ids=\"");
//    	int lastDim = dimensions.length - 1;
//		for (int d = 0; d < dimensions.length; d++) {
//			w.write(XMLUtil.printQuoted(dimensions[d].getId()));
//			if(d<lastDim)
//				w.write(CubeViewPersistence.DELIMITER);
//		}		
//		w.write("\" />\r\n");
//	}
    
    private final void writeHierarchies(Writer w, Hierarchy [] hierarchies)
			throws IOException {
		if (hierarchies.length == 0)
			return;
		w.write("  <dimensions ids=\"");
		int lastHier = hierarchies.length - 1;
		for (int d = 0; d < hierarchies.length; d++) {
			w.write(XMLUtil.printQuoted(hierarchies[d].getDimension().getId()));
			if (d < lastHier)
				w.write(CubeViewPersistence.DELIMITER);
		}		
		w.write("\" hierarchyIds=\"");
		for (int d = 0; d < hierarchies.length; d++) {
			w.write(XMLUtil.printQuoted(hierarchies[d].getId()));
			if (d < lastHier)
				w.write(CubeViewPersistence.DELIMITER);
		}				
		w.write("\" />\r\n");
	}

//    private final void writeSelectedElements(Writer w, Dimension[] dimensions,
//			Axis axis) throws IOException {
//		for (int d = 0; d < dimensions.length; d++) {
//			Element element = axis.getSelectedElement(dimensions[d]);
//			if (element != null) {
//				w.write("  <selected element=\""
//						+ XMLUtil.printQuoted(element.getId())
//						+ "\" dimension=\""
//						+ XMLUtil.printQuoted(dimensions[d].getId())
//						+ "\" />\r\n");
//			}
//		}
//	}
    
    private final void writeSelectedElements(Writer w, Hierarchy [] hierarchies,
			Axis axis) throws IOException {
		for (int d = 0; d < hierarchies.length; d++) {
			Element element = axis.getSelectedElement(hierarchies[d]);
			if (element != null) {
				w.write("  <selected element=\""
						+ XMLUtil.printQuoted(element.getId())
						+ "\" dimension=\""
						+ XMLUtil.printQuoted(hierarchies[d].getDimension().getId())
						+ "\" />\r\n");
			}
		}
	}

    private final void writeActiveSubsets(Writer w, Dimension[] dimensions,
			Axis axis) throws IOException {
		for (int d = 0; d < dimensions.length; d++) {
			Subset activeSub = axis.getActiveSubset(dimensions[d]);
			if (activeSub != null) {
				w.write("  <active subset=\""
						+ XMLUtil.printQuoted(activeSub.getId())
						+ "\" dimension=\""
						+ XMLUtil.printQuoted(dimensions[d].getId()) 
						+ "\" />\r\n");
			}
			//check new:
			Subset2 activeSub2 = axis.getActiveSubset2(dimensions[d]);
			if (activeSub2 != null) {
				w.write("  <active subset2=\""
						+ XMLUtil.printQuoted(activeSub2.getId())
						+ "\" type=\""
						+ XMLUtil.printQuoted(activeSub2.getType())
						+ "\" dimension=\""
						+ XMLUtil.printQuoted(dimensions[d].getId()) 
						+ "\" />\r\n");
			}

		}
	}
    
    private final void writeExpandedPaths(Writer w, Dimension[] dimensions,
			Axis axis) throws IOException {
		ElementPath[] paths = axis.getExpandedPaths();
		for (int i = 0; i < paths.length; ++i)
			w.write("  <expanded paths=\""
					+ XMLUtil.printQuoted(paths[i].toString()) + "\" />\r\n");
	}

        
//    private final void writeVisiblePaths(Writer w, Dimension[] dimensions,
//			Axis axis) throws IOException {
//		for (int d = 0; d < dimensions.length; d++) {
//			ElementPath[] paths = axis.getVisiblePaths(dimensions[d]);
//			for (int i = 0; i < paths.length; ++i) {
//				w.write("  <visible path=\""
//						+ XMLUtil.printQuoted(paths[i].toString())
//						+ "\" dimension=\""
//						+ XMLUtil.printQuoted(dimensions[d].getId())
//						+ "\" />\r\n");
//			}
//		}
//	}
    
    private final void writeVisiblePaths(Writer w, Hierarchy[] hierarchies,
			Axis axis) throws IOException {
		for (int d = 0; d < hierarchies.length; d++) {
			ElementPath[] paths = axis.getVisiblePaths(hierarchies[d]);
			for (int i = 0; i < paths.length; ++i) {
				w.write("  <visible path=\""
						+ XMLUtil.printQuoted(paths[i].toString())
						+ "\" dimension=\""
						+ XMLUtil.printQuoted(hierarchies[d].getDimension().getId())
						+ "\" />\r\n");
			}
		}
	}

    private final void writeProperties(Writer w, Axis axis) throws IOException {
    	String[] propIds = axis.getProperties();
    	for(int i=0;i<propIds.length;++i) {
			w.write("  <property id=\"" + XMLUtil.printQuoted(propIds[i])
					+ "\" value=\""
					+ XMLUtil.printQuoted(axis.getPropertyValue(propIds[i]))
					+ "\" />\r\n");    		
    	}
    }    
}
