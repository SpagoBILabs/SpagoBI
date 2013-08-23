/*
*
* @file SubsetReader.java
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
* @version $Id: SubsetReader.java,v 1.13 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.palo.api.Database;
import org.palo.api.Subset;

/**
 * <code>SubsetReader</code>, reads subsets and their corresponding states 
 * from xml.
 *
 * @author Stepan Rutz, Arnd Houben
 * @version $Id: SubsetReader.java,v 1.13 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
class SubsetReader {
	private static SubsetReader instance = new SubsetReader();

	static final SubsetReader getInstance() {
		return instance;
	}

	private SubsetReader() {
	}

	final Subset fromXML(InputStream input, String key, Database database) {
		// dump(input);
		try {
			SAXParserFactory sF = SAXParserFactory.newInstance();
			SAXParser parser = null;
			SubsetXMLHandler xmlHandler = new SubsetXMLHandler(database, key);
			if(database.getConnection().isLegacy())
					xmlHandler.useLegacy();
			parser = sF.newSAXParser();
			parser.parse(input, xmlHandler);
			return xmlHandler.getSubset();
//			XMLSubsetHandler xmlHandler = null;
//			if(database.getConnection().isLegacy())
//					xmlHandler = new LegacySubsetXMLHandler(database, key);
//			else 
//				xmlHandler = new SubsetXMLHandler(database, key);
//					
//			parser = sF.newSAXParser();
//			parser.parse(input, xmlHandler);
//			return xmlHandler.getSubset();
					
//			if (database.getConnection().isLegacy()) {
//				// go on with legacy palo server
//				LegacySubsetXMLHandler legacyHandler = new LegacySubsetXMLHandler(
//						database, key);
//				parser = sF.newSAXParser();
//				parser.parse(input, legacyHandler);
//				return legacyHandler.getSubset();
//			} else {
//				SubsetXMLHandler defaultHandler = new SubsetXMLHandler(
//						database, key);
//				parser = sF.newSAXParser();
//				parser.parse(input, defaultHandler);
//				return defaultHandler.getSubset();
//			}
		} catch (Exception ex) {
			System.err
					.println("XML Exception during subset loading: " + ex.getMessage()); //$NON-NLS-1$
			return null;
		}
	}
}

//abstract class XMLSubsetHandler extends BaseXMLHandler {	
//	abstract Subset getSubset();
//}
//
//class SubsetXMLHandler extends XMLSubsetHandler {
//	private final Database database;
//	private SubsetBuilder subsetBuilder;
//	private SubsetStateBuilder stateBuilder;
//	
//	SubsetXMLHandler(Database db, final String key) {
//		this.database = db;
//
//		putStartHandler("subset", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				subsetBuilder = new SubsetBuilder();
//				subsetBuilder.setId(attributes.getValue("id")); //$NON-NLS-1$
//				subsetBuilder.setName(attributes.getValue("name"));
//				subsetBuilder.setDescription(attributes.getValue("description")); //$NON-NLS-1$
//				subsetBuilder.setActiveState(attributes.getValue("activeStateId")); //$NON-NLS-1$
//				String srcDimensionId = 
//						attributes.getValue("sourceDimensionId"); //$NON-NLS-1$
//				Dimension srcDim = 
//						database.getDimensionByName(srcDimensionId);
//				if(srcDim == null)
//					throw new PaloAPIException("Cannot find source dimension '"+srcDimensionId+"'!!");
//				try {
//					// can not save attributes in legacy mode
//					if(!srcDim.getDatabase().getConnection().isLegacy())
//						subsetBuilder.setAlias(getAttributeByName(srcDim,
//								attributes.getValue("alias")));
//				} catch (PaloException pe) {
//					System.err
//							.println("SubsetReader: cannot read attributes - "
//									+ pe.getMessage());
//				}
//				subsetBuilder.setSourceDimension(srcDim);
//			}
//		});
//		//--------------- STATEs ---------------
//		putStartHandler("subset/state", new StartHandler() {
//			public void startElement(String uri, String localName, 
//					String qName, Attributes attributes) {
//				//get a fresh builder
//				stateBuilder = new SubsetStateBuilder();
//				stateBuilder.setId(attributes.getValue("id"));
//				stateBuilder.setName(attributes.getValue("name"));
//			}
//		});
//		putStartHandler("subset/state/expression", new StartHandler() {
//			public void startElement(String uri, String localName, 
//					String qName, Attributes attributes) {
//				if(stateBuilder == null) {
//					throw new PaloAPIException("Cannot create SubsetState in node description");
//				}
//				stateBuilder.setExpression(attributes.getValue("expr"));
//			}
//		});
//		putStartHandler("subset/state/search", new StartHandler() {
//			public void startElement(String uri, String localName, 
//					String qName, Attributes attributes) {
//				if(stateBuilder == null) {
//					throw new PaloAPIException("Cannot create SubsetState in node description");
//				}
////				attributes.getValue("attribute");
////				stateBuilder.setSearchAttribute(getAttributeByName(subsetBuilder.getSourceDimension(), 
////						attributes.getValue("attribute")));
//				Attribute attr = getAttributeByName(subsetBuilder
//						.getSourceDimension(), attributes.getValue("attribute"));
//				if(attr != null)
//					stateBuilder.setSearchAttribute(attr);
//			}
//		});
//		putStartHandler("subset/state/element", new StartHandler() {
//			public void startElement(String uri, String localName, 
//					String qName, Attributes attributes) {
//				if(stateBuilder == null || subsetBuilder == null) {
//					throw new PaloAPIException("Cannot create SubsetState in node element");					
//				}
//				String elementId = attributes.getValue("id");
//				String paths = attributes.getValue("paths");
//				String positions = attributes.getValue("pos");
//				Dimension srcDim = subsetBuilder.getSourceDimension();
//				Element element = srcDim.getElementByName(elementId);
//				stateBuilder.addElement(element);
//				stateBuilder.setPaths(element, paths);
//				stateBuilder.setPositions(element, positions);
//			}
//		});
//		putEndHandler("subset/state", new EndHandler() {
//			public void endElement(String uri, String localName, String qName) {
//				if(subsetBuilder == null || stateBuilder == null) {
//					throw new PaloAPIException("Cannot create subset state!!");					
//				}
//				SubsetState state = stateBuilder.createState();
//				subsetBuilder.addState(state);
//				stateBuilder = null;
//			}
//		});		
//	}
//	
//	private Attribute getAttributeByName(Dimension srcDim, String value)
//	{
//		if (srcDim == null || srcDim.getDatabase().getConnection().isLegacy())
//			return null;
//		
//		Attribute[] attrs = srcDim.getAttributes();
//	
//		for (int i = 0; i < attrs.length; i++)
//		{
//			if (attrs[i].getName().equals(value))
//			{
//				return attrs[i]; 
//			}
//		}
//		
//		return null;
//	}
//	
//	public Subset getSubset() {
//		return subsetBuilder.createSubset();
//	}
//}

//class LegacySubsetXMLHandler extends XMLSubsetHandler {
//	private final Database database;
//	private SubsetBuilder subsetBuilder;
//	private SubsetStateBuilder stateBuilder;
//	
//	LegacySubsetXMLHandler(Database db, final String key) {
//		this.database = db;
//		putStartHandler("subset", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				subsetBuilder =  new SubsetBuilder();
//				subsetBuilder.setId(key);
//				String name = attributes.getValue("name");				
//				subsetBuilder.setName(name);
//				subsetBuilder.setDescription(attributes.getValue("description"));
//				subsetBuilder.setActiveState(attributes.getValue("activestrategy"));
//				String srcDimId = 
//					attributes.getValue("sourceDimensionName"); //$NON-NLS-1$
//				Dimension srcDim = 
//					database.getDimensionByName(srcDimId);
//				if(srcDim == null)
//					throw new PaloAPIException("Cannot find source dimension '"+srcDimId+"'!!");
//				subsetBuilder.setSourceDimension(srcDim);
//			}
//		});
//        
//		putStartHandler("subset/state", new StartHandler() {
//			public void startElement(String uri, String localName, 
//					String qName, Attributes attributes) {
//				//get a fresh builder
//				stateBuilder = new SubsetStateBuilder();
//				stateBuilder.setId(attributes.getValue("id"));
//				stateBuilder.setName(attributes.getValue("name"));
//			}
//		});
//
//		//------ REGULAR EXPRESSION --------------------------------------------		
//		putStartHandler("subset/regularexpression", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				//get a fresh builder
//				stateBuilder = new SubsetStateBuilder();
//				stateBuilder.setId("regularexpression");
//				stateBuilder.setName("Regular Expression");
//				stateBuilder.setExpression(attributes.getValue("expression"));				
//			}
//		});
//		putEndHandler("subset/regularexpression", new EndHandler() {
//			public void endElement(String uri, String localName, String qName) {
//				if(subsetBuilder == null || stateBuilder == null) {
//					throw new PaloAPIException("Cannot create subset state!!");
//				}
//				SubsetState state = stateBuilder.createState();
//				subsetBuilder.addState(state);
//				stateBuilder = null;
//			}
//		});		
//
//		//------ FLAT HIERARCHY ------------------------------------------------
//		putStartHandler("subset/flat", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				//get a fresh builder
//				stateBuilder = new SubsetStateBuilder();
//				stateBuilder.setId("flat");
//				stateBuilder.setName("Flat");
//			}
//		});
//		putStartHandler("subset/flat/element", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				if(stateBuilder == null || subsetBuilder == null) {
//					throw new PaloAPIException("Cannot add elements to flat subset state!!");
//				}
//				String elementName = attributes.getValue("name");
//				Dimension srcDim = subsetBuilder.getSourceDimension();
//				Element element = srcDim.getElementByName(elementName);
//				stateBuilder.addElement(element);
//			}
//		});
//		putEndHandler("subset/flat", new EndHandler() {
//			public void endElement(String uri, String localName, String qName) {
//				if(subsetBuilder == null || stateBuilder == null) {
//					throw new PaloAPIException("Cannot create subset state!!");
//				}
//				SubsetState state = stateBuilder.createState();
//				subsetBuilder.addState(state);
//				stateBuilder = null;
//			}
//		});	
//		//------ HIERARCHICAL --------------------------------------------------
//		putStartHandler("subset/hierarchical", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				//get a fresh builder
//				stateBuilder = new SubsetStateBuilder();
//				stateBuilder.setId("hierarchical");
//				stateBuilder.setName("Hierarchical");
//			}
//		});
//		putStartHandler("subset/hierarchical/element", new StartHandler() {
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				if(stateBuilder == null || subsetBuilder == null) {
//					throw new PaloAPIException("Cannot add elements to hierarchical subset state!!");
//				}
//				String elementName = attributes.getValue("name");
//				Dimension srcDim = subsetBuilder.getSourceDimension();
//				Element element = srcDim.getElementByName(elementName);
//				stateBuilder.addElement(element);
//			}
//		});
//		putEndHandler("subset/hierarchical", new EndHandler() {
//			public void endElement(String uri, String localName, String qName) {
//				if(subsetBuilder == null || stateBuilder == null) {
//					throw new PaloAPIException("Cannot create subset state!!");
//				}
//				SubsetState state = stateBuilder.createState();
//				subsetBuilder.addState(state);
//				stateBuilder = null;
//			}
//		});	
//	}
//
//	public Subset getSubset() {
//		return subsetBuilder.createSubset();
//	}
//}