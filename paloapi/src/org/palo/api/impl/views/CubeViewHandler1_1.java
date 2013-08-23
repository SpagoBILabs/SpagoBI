/*
*
* @file CubeViewHandler1_1.java
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
* @version $Id: CubeViewHandler1_1.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.impl.xml.EndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.impl.xml.StartHandler;
import org.palo.api.persistence.PersistenceError;
import org.palo.api.utils.ElementPath;
import org.xml.sax.Attributes;

/**
 * <code>CubeViewHandler1_1</code>
 * Defines <code>{@link StartHandler}</code>s and 
 * <code>{@link EndHandler}</code>s to read cube views which are stored using
 * version 1.1
 *
 * @author ArndHouben
 * @version $Id: CubeViewHandler1_1.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class CubeViewHandler1_1 extends CubeViewHandler1_0 {
    
    CubeViewHandler1_1(Database database) {
    	super(database);
    }
    
    protected void registerStartHandlers() {
    	super.registerStartHandlers();
    	
    	//substitute this...
    	unregisterStartHandler("view/axis/dimension");
    	//with this...
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/dimensions";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String ids = attributes.getValue("ids");
				String[] dimIds = ids.split(CubeViewPersistence.DELIMITER);
				String hIds = attributes.getValue("hierarchyIds");
				String [] hierIds = null;
				if (hIds != null) {
					hierIds = hIds.split(CubeViewPersistence.DELIMITER);
					if (hierIds.length != dimIds.length) {
						hierIds = null;
					}
				}
				for (int i = 0; i < dimIds.length; ++i) {
					Dimension dimension = database.getDimensionById(dimIds[i]);
					if (dimension == null) {
						addError("CubeViewReader: unknown dimension id '"
								+ dimIds[i] + "' in database '"
								+ database.getName() + "'!!", cubeView.getId(),
								cubeView, database, dimIds[i],
								PersistenceError.UNKNOWN_DIMENSION,
								currAxis, PersistenceError.TARGET_GENERAL);						
					}					
					if (dimension != null && hierIds != null) {
						Hierarchy hier = dimension.getHierarchyById(hierIds[i]);
						if (hier == null) {
							addError("CubeViewReader: unknown hierarchy id '"
									+ hierIds[i] + "' in database '"
									+ database.getName() + "'!!", cubeView.getId(),
									cubeView, database, hierIds[i],
									PersistenceError.UNKNOWN_DIMENSION,
									currAxis, PersistenceError.TARGET_GENERAL);													
						}
						currAxis.add(hier);							
					} else {
						currAxis.add(dimension);
					}
				}
			}
		});
    	
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/hierarchies";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String ids = attributes.getValue("ids");
				String hIds = attributes.getValue("hierarchyIds");
				if (ids.indexOf(CubeViewPersistence.DIM_HIER_DELIMITER) != -1) {
					String[] dimHierIds = ids.split(CubeViewPersistence.DELIMITER);
					for (int i = 0; i < dimHierIds.length; ++i) {
						String [] bothIds = 
							dimHierIds[i].split(CubeViewPersistence.DIM_HIER_DELIMITER);
						Dimension dimension = database.getDimensionById(bothIds[0]);
						if (dimension == null) {
							addError("CubeViewReader: unknown dimension id '"
									+ bothIds[0] + "' in database '"
									+ database.getName() + "'!!", cubeView.getId(),
									cubeView, database, bothIds[0],
									PersistenceError.UNKNOWN_DIMENSION,
									currAxis, PersistenceError.TARGET_GENERAL);						
						} else {
							Hierarchy hier = dimension.getHierarchyById(bothIds[1]);
							if (hier == null) {
								addError("CubeViewReader: unknown hierarchy id '"
										+ bothIds[1] + "' in database '"
										+ database.getName() + "'!!", cubeView.getId(),
										cubeView, database, bothIds[1],
										PersistenceError.UNKNOWN_DIMENSION,
										currAxis, PersistenceError.TARGET_GENERAL);													
							}
							currAxis.add(hier);	
						}					
					}					
				} else {
					String[] dimIds = ids.split(CubeViewPersistence.DELIMITER);
					String[] hierIds = null;
					if (hIds != null) {
						hierIds = hIds.split(CubeViewPersistence.DELIMITER);
						if (hierIds.length != dimIds.length) {
							hierIds = null;
						}
					}
					for (int i = 0; i < dimIds.length; ++i) {
						Dimension dimension = database.getDimensionById(dimIds[i]);
						if (dimension == null) {
							addError("CubeViewReader: unknown dimension id '"
									+ dimIds[i] + "' in database '"
									+ database.getName() + "'!!", cubeView.getId(),
									cubeView, database, dimIds[i],
									PersistenceError.UNKNOWN_DIMENSION,
									currAxis, PersistenceError.TARGET_GENERAL);						
						} else {							
							if (hierIds != null) {
								Hierarchy hier = dimension.getHierarchyById(hierIds[i]);							
								if (hier == null) {
									addError("CubeViewReader: unknown hierarchy id '"
											+ hierIds[i] + "' in database '"
											+ database.getName() + "'!!", cubeView.getId(),
											cubeView, database, hierIds[i],
											PersistenceError.UNKNOWN_DIMENSION,
											currAxis, PersistenceError.TARGET_GENERAL);													
								}
								currAxis.add(hier);
							}
						}					
					}										
				}
			}
		});

    	//overwrite expanded handling...
    	registerStartHandler(new IPaloStartHandler() {
			public String getPath() {
				return "view/axis/expanded";
			}

			public void startElement(String uri, String localName,
					String qName, Attributes attributes) {
				String paths = attributes.getValue("paths");
				Hierarchy[] hierarchies = currAxis.getHierarchies();
				try {
					ElementPath expandedPath = ElementPath.restore(hierarchies,
							paths);
					currAxis.addExpanded(expandedPath);
				} catch (PaloAPIException ex) {
					addError(ex.getMessage(), cubeView.getId(), cubeView,
							((Object[]) ex.getData())[0], ((Object[]) ex
									.getData())[1].toString(),
							PersistenceError.UNKNOWN_ELEMENT, currAxis,
							PersistenceError.TARGET_EXPANDED_PATH);
				}
			}
		});
    	
	}

}
