/*
*
* @file CubeViewHandler1_3.java
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
* @version $Id: CubeViewHandler1_3.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.Subset;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.api.impl.xml.XMLUtil;
import org.palo.api.persistence.PersistenceError;
import org.palo.api.subsets.Subset2;
import org.palo.api.subsets.SubsetHandler;
import org.xml.sax.Attributes;

import com.tensegrity.palojava.PaloException;

/**
 * <code>CubeViewHandler1_3</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read cube views which are stored using
 * version 1.3. This version is completely based on ids. 
 *
 * @author ArndHouben
 * @version $Id: CubeViewHandler1_3.java,v 1.15 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewHandler1_3 extends CubeViewHandler1_2 {

	CubeViewHandler1_3(Database database) {
		super(database);
	}

	protected void registerStartHandlers() {
    	super.registerStartHandlers();
    	    	
    	// add new view handling:
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				CubeViewBuilder viewBuilder = new CubeViewBuilder();
				viewBuilder.setId(attributes.getValue("id"));
				viewBuilder.setName(attributes.getValue("name"));
				viewBuilder.setDescription(XMLUtil.dequoteString(attributes
						.getValue("description")));
				String str = attributes.getValue("hideempty");
				if (str != null && str.equalsIgnoreCase("true"))
					viewBuilder.addProperty(CubeView.PROPERTY_ID_HIDE_EMPTY,
							Boolean.toString(true));
				String cubeId = attributes.getValue("cube");
				Cube srcCube = database.getCubeById(cubeId);
				if(srcCube == null) {
					System.err.println("view("+attributes.getValue("id")+"): unknown source cube '"+cubeId+"' in database '"+database.getName()+"'");
					throw new PaloAPIException("CubeView creation failed! No source cube found with id: "+cubeId);
				}
				if (cubeId != null)
					viewBuilder.setCube(database.getCubeById(cubeId));
				cubeView = viewBuilder.createView(CubeViewHandler1_3.this);
				//we abort loading if creation failed...
				if(cubeView == null)
					throw new PaloAPIException("CubeView creation failed!");
			}
		});
    	
//    	registerStartHandler(new IPaloStartHandler() {
//			public String getPath() {
//				return "view/axis/dimension";
//			}
//
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				String dimId = attributes.getValue("id");
//				dimId = CubeViewReader.getLeafName(dimId);
//				Dimension dim = database.getDimensionById(dimId);
//				if (dim == null) {
//					addError("CubeViewReader: unknown dimension id '" + dimId
//							+ "'!!", cubeView.getId(), cubeView, database,
//							dimId, PersistenceError.UNKNOWN_DIMENSION,
//							currAxis, PersistenceError.TARGET_GENERAL);
//				}
//				currAxis.add(dim);
//			}
//		});

    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/selected";
			}
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String element = attributes.getValue("element");
				String dimId = attributes.getValue("dimension");
				String hierId = attributes.getValue("hierarchy");
//				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim;
				if (dimId == null && hierId != null) {
					// Old solution had dim~~~hier in hierarchyId.
					// Read it here.
					String [] allIds = 
						hierId.split(CubeViewPersistence.DIM_HIER_DELIMITER);
					dimId = allIds[0];
					hierId = allIds[1];
				}
				dim = database.getDimensionById(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				Hierarchy hier = null;
				if (hierId != null && dim != null) {
					hier = dim.getHierarchyById(hierId);
					if (hier == null) {
						addError("CubeViewReader: unknown hierarchy id '" + hierId
								+ "'!!", cubeView.getId(), cubeView, database,
								hierId, PersistenceError.UNKNOWN_DIMENSION,
								currAxis, PersistenceError.TARGET_GENERAL);						
					}
				} else if (dim != null) {
					hier = currAxis.getHierarchy(dim);
				}
				Element selected = null;
				try {
					if (hier != null) {
						selected = hier.getElementById(element);
					} else {
						selected = dim.getDefaultHierarchy().getElementById(element);
					}
				} catch (PaloException e) {
					e.printStackTrace();
					selected = null;
				}				
				if (selected == null) {
//					String [] propKeys = cubeView.getProperties();
//					for (String p: propKeys) {
//						System.out.println(p + ": " + cubeView.getPropertyValue(p));
//					}
//					for (Hierarchy hier: dim.getHierarchies()) {
//						hier.getElements();
//					}
//					selected = dim.getElementById(element);
//					if (selected == null) {
						addError("CubeViewReader: unknown element id '" + element
								+ "'!!", cubeView.getId(), cubeView, dim, element,
								PersistenceError.UNKNOWN_ELEMENT,
								currAxis, PersistenceError.TARGET_SELECTED);
//					}
				}
				if (hier == null) {
					currAxis.setSelectedElement(dim, selected);
				} else {
					currAxis.setSelectedElement(hier, selected);
				}
			}
		});

    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/active";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String subset = attributes.getValue("subset");
				String subset2 = attributes.getValue("subset2");
				String dimId = attributes.getValue("dimension");
//				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionById(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_GENERAL);
				}
				if(subset2 != null && subset2.length()>0) {
					SubsetHandler subHandler = dim.getSubsetHandler();
					String attrType = attributes.getValue("type");
					int type =Subset2.TYPE_LOCAL;
					try {
						type = Integer.parseInt(attrType);
					}catch(NumberFormatException nfe) {
						/* ignore */
					}
					// PR 7017: subset loading can fail, but view loading should
					// continue...
					Subset2 activeSub2 = null;
					try {
						activeSub2 = subHandler.getSubset(subset2, type);
					} catch (Exception ex) {
						/* ignore */
					}
					if (activeSub2 == null) {
						//PR 6875: we remember an unknown subset here
						//=> this will and has to change with new view definition and loading!!!
						((AxisImpl) currAxis).setData(
								"com.tensegrity.palo.unknown_subset_"+dimId, 
								(dim!=null? dim.getName():dimId)
										+ "," + subset2 + "," + type);
						addError("CubeViewReader: unknown subset id '" + subset
								+ "'!!", cubeView.getId(), cubeView, dim,
								subset, PersistenceError.UNKNOWN_SUBSET,
								currAxis, PersistenceError.TARGET_SUBSET);
					}
					
					currAxis.setActiveSubset2(dim, activeSub2);
				} else {
					Subset activeSub = dim.getSubset(subset);
					if (activeSub == null) {
						addError("CubeViewReader: unknown subset id '" + subset
								+ "'!!", cubeView.getId(), cubeView, dim,
								subset, PersistenceError.UNKNOWN_SUBSET,
								currAxis, PersistenceError.TARGET_SUBSET);
					}
					currAxis.setActiveSubset(dim, activeSub);
				}
			}
		});

//    	registerStartHandler(new IPaloStartHandler() {
//			public String getPath() {
//				return "view/axis/expanded";
//			}
//
//			public void startElement(String uri, String localName,
//					String qName, Attributes attributes) {
//				String path = attributes.getValue("path");
//				String dimId = attributes.getValue("dimension");
//				String reps = attributes.getValue("repetitions");
//				dimId = CubeViewReader.getLeafName(dimId);
//				Dimension dim = database.getDimensionById(dimId);
//				int[] repetitions = CubeViewReader.getRepetitions(reps);
//				if (dim == null) {
//					addError("CubeViewReader: unknown dimension id '" + dimId
//							+ "'!!", cubeView.getId(), cubeView, database,
//							dimId, PersistenceError.UNKNOWN_DIMENSION,
//							currAxis, PersistenceError.TARGET_EXPANDED_PATH);
//				}
//				Element[] expPath = CubeViewHandler1_3.this.getPathById(path, dim,
//						currAxis, PersistenceError.TARGET_EXPANDED_PATH);
//				for (int i = 0; i < repetitions.length; ++i)
//					currAxis.addExpanded(dim, expPath, repetitions[i]);
//			}
//		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/hidden";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String path = attributes.getValue("path");
				String dimId = attributes.getValue("dimension");
				String hierId = attributes.getValue("hierarchy");
//				dimId = CubeViewReader.getLeafName(dimId);
				Dimension dim = database.getDimensionById(dimId);
				if (dim == null) {
					addError("CubeViewReader: unknown dimension id '" + dimId
							+ "'!!", cubeView.getId(), cubeView, database,
							dimId, PersistenceError.UNKNOWN_DIMENSION,
							currAxis, PersistenceError.TARGET_HIDDEN_PATH);
				}
				Hierarchy hier = null;
				if (dim != null) {
					if (hierId == null) {
						hier = currAxis.getHierarchy(dim);
						if (hier == null) {
							hier = dim.getDefaultHierarchy(); 
						}
					} else {
						hier = dim.getHierarchyById(hierId);
					}
				}	
				Element[] hiddenPath = CubeViewHandler1_3.this.getPathById(
						path, dim, hier, currAxis, PersistenceError.TARGET_HIDDEN_PATH);
				currAxis.addHidden(dim, hiddenPath);
			}
		});


	}

}
