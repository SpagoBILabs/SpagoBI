/*
*
* @file SubsetXMLHandler.java
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
* @version $Id: SubsetXMLHandler.java,v 1.16 2009/11/23 15:40:17 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io.xml;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.validation.TypeInfoProvider;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.PaloAPIException;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetFilter;
import org.palo.api.subsets.filter.AliasFilter;
import org.palo.api.subsets.filter.AttributeFilter;
import org.palo.api.subsets.filter.DataFilter;
import org.palo.api.subsets.filter.HierarchicalFilter;
import org.palo.api.subsets.filter.PicklistFilter;
import org.palo.api.subsets.filter.SortingFilter;
import org.palo.api.subsets.filter.TextFilter;
import org.palo.api.subsets.impl.SubsetHandlerImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code>Subset2XMLReader</code>
 * <p><b>- API INTERNAL - </b></p>
 * XML Handler for storing and loading subsets
 *
 * @author ArndHouben
 * @version $Id: SubsetXMLHandler.java,v 1.16 2009/11/23 15:40:17 PhilippBouillon Exp $
 **/
public class SubsetXMLHandler extends DefaultHandler {

	private Stack<String> absPath = new Stack<String>();
	private SubsetFilterHandler filter;
	private Subset2 subset;
	private String version;
	private final SubsetHandlerImpl subsetHandler;
	private final int type;
	private final String defName;
	private final HashMap<String, Class <? extends SubsetFilterHandler>> filterHandlers = new HashMap<String, Class <? extends SubsetFilterHandler>>();
	private final StringBuffer value = new StringBuffer();
	
	public SubsetXMLHandler(TypeInfoProvider typeProvider, SubsetHandlerImpl subsetHandler, String defName, int type) {
		this.type = type;
		this.defName = defName;
		this.subsetHandler = subsetHandler;
		filterHandlers.put(AliasFilterHandler.XPATH, AliasFilterHandler.class);
		filterHandlers.put(TextFilterHandler.XPATH, TextFilterHandler.class);
		filterHandlers.put(AttributeFilterHandler.XPATH, AttributeFilterHandler.class);
		filterHandlers.put(DataFilterHandler.XPATH, DataFilterHandler.class);
		filterHandlers.put(HierarchicalFilterHandler.XPATH, HierarchicalFilterHandler.class);
		filterHandlers.put(PicklistHandler.XPATH, PicklistHandler.class);
		filterHandlers.put(SortingFilterHandler.XPATH, SortingFilterHandler.class);
	}

	public final Subset2 getSubset() {
		return subset;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		value.delete(0,value.length());
		absPath.push(qName);
		String xPath = getXPath();
		if (xPath.equals("/subset"))
			subset = createSubset(attributes);

		if (filterHandlers.containsKey(xPath)) {
			filter = createFilter(xPath);
		} else if (filter != null)
			filter.enter(xPath);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		String xPath = getXPath();
		if(xPath.equals("/subset/indent/value")) {
			if (version != null && version.equals("1.0rc2")) {
				subset.setIndent(2); //LEVEL INDENT
			} else {
				String indent = value.toString();
				try {
					subset.setIndent(Integer.parseInt(indent));
				} catch (Exception e) {
					subset.setIndent(2);
				}
			}
		}
		else if (filter != null) {
			if(xPath.equals(filter.getXPath())) {
				subset.add(filter.createFilter(subset.getDimension()));
				filter = null;
			} else 
				filter.leave(xPath, value.toString());
		}
		if (absPath.size() > 0)
			absPath.pop();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		value.append(ch, start, length);
	}

	
	public void processingInstruction(String target, String data)
			throws SAXException {
		if (target.equals("palosubset")) {
			version = data.substring(9, data.length() - 1).trim();
		}
		super.processingInstruction(target, data);
	}

	//--------------------------------------------------------------------------
	// STATIC UTILITIES METHODS
	//
	public static final double getDouble(String value) {
		value = value.trim();
		if(value == null || value.equals(""))
			return 0;
		return Double.parseDouble(value);
	}

	public static final boolean getBoolean(String value) {
		if(value == null || value.equals(""))
			return false;
		return Boolean.parseBoolean(value);
	}

	public static final int getInteger(String value) {
		value = value.trim();
		if(value == null || value.equals(""))
			return 0;
		return Integer.parseInt(value);
	}

	public static final String getFilterXMLExpression(SubsetFilter filter) {
		String xmlStr = null;
		switch(filter.getType()) {
		case SubsetFilter.TYPE_ATTRIBUTE:
			xmlStr = AttributeFilterHandler.getPersistenceString(
					(AttributeFilter)filter);
			break;
		case SubsetFilter.TYPE_DATA:
			xmlStr = DataFilterHandler.getPersistenceString(
					(DataFilter)filter);
			break;
		case SubsetFilter.TYPE_HIERARCHICAL:
			xmlStr = HierarchicalFilterHandler.getPersistenceString(
					(HierarchicalFilter)filter);
			break;
		case SubsetFilter.TYPE_PICKLIST:
			xmlStr = PicklistHandler.getPersistenceString(
					(PicklistFilter)filter);
			break;
		case SubsetFilter.TYPE_SORTING:
			xmlStr = SortingFilterHandler.getPersistenceString(
					(SortingFilter)filter);
			break;
		case SubsetFilter.TYPE_TEXT:
			xmlStr = TextFilterHandler.getPersistenceString(
					(TextFilter)filter);
			break;
		case SubsetFilter.TYPE_ALIAS:
			xmlStr = AliasFilterHandler.getPersistenceString(
					(AliasFilter)filter);
			break;
		default:
			throw new PaloAPIException(
					"Couldn't store subset! Unsupported filter '"
							+ filter.getClass().getName() + "'!");

		}
		return xmlStr;
	}
	
	
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final String getXPath() {
		Enumeration<String> allPaths = absPath.elements();
		StringBuffer path = new StringBuffer();
		while (allPaths.hasMoreElements()) {
			path.append("/");
			path.append(allPaths.nextElement());
		}
		return path.toString();
	}

	private final SubsetFilterHandler createFilter(String xPath) {
		Class<? extends SubsetFilterHandler> filter = filterHandlers.get(xPath);
		if (filter != null) {
			try {
				SubsetFilterHandler filterHandler = 
					filter.getConstructor().newInstance();
				filterHandler.setSubsetVersion(version);
				return filterHandler;
			} catch (Exception e) {
//				String msg = "Error creating subset filter"; //$NON-NLS-1$
				e.printStackTrace();
			}
		}
		return null;
	}

	private final Subset2 createSubset(Attributes attributes)
			throws SAXException {
		// required subset attributes:
		String id = attributes.getValue("id");
		if(!isValidId(id))
			throw new SAXException("No subset id defined!");
		String srcDimensionId = attributes.getValue("sourceDimensionId");
//		if(!isValidId(srcDimensionId))
//			throw new SAXException("No source dimension id defined!");

		//optional attributes:
		String name = attributes.getValue("name");
//		String indent = attributes.getValue("Indent");
//		String alias1 = attributes.getValue("Alias1");
//		String alias2 = attributes.getValue("Alias2");
		Database database = subsetHandler.getDimension().getDatabase();
		// We ignore sourceDimensionID for now, because Jedox fills it with
		// wrong data...
		Dimension dimension = subsetHandler.getDimension(); //database.getDimensionById(srcDimensionId);
		if(!defName.equals(name))
			name = defName;
		Subset2 subset = subsetHandler.create(id, name, dimension.getDefaultHierarchy(), type);
//		if(indent != null && !indent.equals(""))
//			subset.setIndent(Integer.parseInt(indent));
//		else
//			subset.setIndent(0);
//		if (alias1 != null || alias2 != null) {
//			AliasFilterSetting setting = new AliasFilterSetting();
//			setting.setAlias(1, alias1);
//			setting.setAlias(2, alias2);
//			AliasFilter aliasFilter = 
//				new AliasFilter(subset.getDimension(), setting);
//			subset.add(aliasFilter);
//		}

		return subset;
	}
	
	private final boolean isValidId(String id)  {
		if(id == null || id.equals(""))
			return false;
		return true;
	}
}
