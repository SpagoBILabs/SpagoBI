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
* @author ArndHouben
*
* @version $Id: SubsetWriter.java,v 1.11 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.io.xml.SubsetXMLHandler;


/**
 * <code>SubsetWriter</code>
 * <p>
 * Singleton for storing <code>Subset2</code> to xml.
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: SubsetWriter.java,v 1.11 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class SubsetWriter {

	private static SubsetWriter instance = new SubsetWriter();

	static final SubsetWriter getInstance() {
		return instance;
	}

	private SubsetWriter() {
	}

	/**
	 * Writes the xml representation of the given <code>Subset2</code> instance 
	 * to the given output stream 
	 * @param output
	 * @param subset
	 */
	final void toXML(OutputStream output, Subset2 subset) throws PaloIOException {
		try {
			toXMLInternal(output, subset);
		} catch (Exception e) {
			PaloIOException pex = 
				new PaloIOException("Writing subset to xml failed!",e);
			pex.setData(subset);
			throw pex;
		}
	}

	private final void toXMLInternal(OutputStream output, Subset2 subset)
			throws Exception {
		PrintWriter w = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(output, "UTF-8"))); //$NON-NLS-1$
		try {
			// preamble:
			w.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			w.write("<!--!DOCTYPE subset SYSTEM \"filters.dtd\" -->\r\n"); 
			w.write("<?palosubset version=\"1.0\"?>\r\n");
			
			// subset element:
			writeSubsetElement(w, subset);
			// write subset filters:
			SubsetFilter[] filters = subset.getFilters();
			filters = sort(filters);
			for (int i = 0; i < filters.length; ++i) {
				String xmlExpr = 
					SubsetXMLHandler.getFilterXMLExpression(filters[i]);
				if(xmlExpr != null)
					w.write(xmlExpr);
			}
			// done
			w.write("</subset>\r\n"); //$NON-NLS-1$
		} finally {
			w.close();
		}
	}

	private final void writeSubsetElement(PrintWriter w, Subset2 subset) {
		String id = subset.getId();
		String srcDimId = subset.getDimension().getId();		
//		String[] aliases = subset.getAliases();
		StringBuffer subsetElement = new StringBuffer();
		subsetElement.append("<subset id=\"");
		subsetElement.append(id);
		subsetElement.append("\" sourceDimensionId=\"");
		subsetElement.append(srcDimId);
		subsetElement.append("\" \r\n");
		
//		subsetElement.append("\" Indent=\"");
//		subsetElement.append(indent);
//		// now we add optional aliases:
//		if(subset.isActive(SubsetFilter.TYPE_ALIAS)) {
//			AliasFilter aliasFilter = 
//				(AliasFilter)subset.getFilter(SubsetFilter.TYPE_ALIAS);
//			AliasFilterSetting setting = 
//				(AliasFilterSetting)aliasFilter.getSettings();
//			String alias1 = setting.getAlias(1);
//			if (alias1 != null && alias1.length() > 0) {
//				subsetElement.append("\" Alias1=\"");
//				subsetElement.append(alias1);
//			}
//			String alias2 = setting.getAlias(2);
//			if(alias2 != null && alias2.length() > 0) {
//				subsetElement.append("\" Alias2=\"");
//				subsetElement.append(alias2);
//			}
//		}
//		subsetElement.append("\" version=\"");
//		subsetElement.append("1.0");
//		subsetElement.append("\" \r\n");
		
		// the standard rest:
		subsetElement.append("xmlns=\"http://www.jedox.com/palo/SubsetXML\">\r\n");		
//		subsetElement
//				.append("xmlns=\"http://www.w3schools.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \r\n");
//		subsetElement
//				.append("xsi:schemaLocation=\"http://www.w3schools.com subset.xsd\">\r\n");
		
		//write indent
		int indent = subset.getIndent();
		subsetElement.append("<indent><value>");
		subsetElement.append(indent);
		subsetElement.append("</value></indent>\r\n"); //NO PARAMETER YET...
		w.write(subsetElement.toString());
	}


	private final SubsetFilter[] sort(SubsetFilter[] filters) {
		// we have to sort the filters since the xsd expect them in a certain
		// sequence:
		HashMap<Integer, SubsetFilter> allFilters = new HashMap<Integer, SubsetFilter>();
		for (int i = 0; i < filters.length; ++i)
			allFilters.put(new Integer(filters[i].getType()), filters[i]);
		ArrayList<SubsetFilter> sortedFilters = new ArrayList<SubsetFilter>();
		// the filter sequence defined in subset.xsd		
		addFilter(SubsetFilter.TYPE_ALIAS, sortedFilters, allFilters);
		addFilter(SubsetFilter.TYPE_HIERARCHICAL, sortedFilters, allFilters);
		addFilter(SubsetFilter.TYPE_TEXT, sortedFilters, allFilters);		
		addFilter(SubsetFilter.TYPE_ATTRIBUTE, sortedFilters, allFilters);
		addFilter(SubsetFilter.TYPE_DATA, sortedFilters, allFilters);
		addFilter(SubsetFilter.TYPE_PICKLIST, sortedFilters, allFilters);
		addFilter(SubsetFilter.TYPE_SORTING, sortedFilters, allFilters);
		return sortedFilters.toArray(new SubsetFilter[sortedFilters.size()]);
	}

	private final void addFilter(int type, List<SubsetFilter> sorted,
			HashMap<Integer, SubsetFilter> allFilters) {
		SubsetFilter filter = allFilters.get(new Integer(type));
		if (filter != null)
			sorted.add(filter);
	}

}
