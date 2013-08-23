/*
*
* @file CubeViewManager.java
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
* @version $Id: CubeViewManager.java,v 1.22 2009/12/14 12:46:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Property;
import org.palo.api.impl.AbstractController;
import org.palo.api.persistence.PaloPersistenceException;

/**
 * The <code>CubeViewManager</code> encapsulates cube view access, especially 
 * the loading and saving of cube views.
 * 
 * @author ArndHouben
 * @version $Id: CubeViewManager.java,v 1.22 2009/12/14 12:46:57 PhilippBouillon Exp $
 */
public class CubeViewManager extends AbstractController {

	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final CubeViewManager instance = new CubeViewManager();
	public static final CubeViewManager getInstance() {
		return instance;
	}

	//--------------------------------------------------------------------------
	// INSTANCE
	//	
	//private final CubeViewPersistence persister;
	private final CubeViewPersistence persister;
	private CubeViewManager() {
		persister = CubeViewPersistence.getInstance();
	}
	
	public final void init(Database database) {
		//PR 6995: palo server has some problems with user info cubes and dims
		//			so we fall back to legacy ones...
//		if(database.getType() == Database.TYPE_NORMAL)
//			createEnvironment(database);
		
//		// check if dimensions are there:
//		Dimension columns = database
//				.getDimensionByName(CubeViewPersistence.DIMENSION_VIEW_COLUMNS);
//		if (columns == null)
//			columns = database
//					.addUserInfoDimension(CubeViewPersistence.DIMENSION_VIEW_COLUMNS);
//		// view rows
//		Dimension rows = database
//				.getDimensionByName(CubeViewPersistence.DIMENSION_VIEW_ROWS);
//		if (rows == null)
//			rows = database
//					.addUserInfoDimension(CubeViewPersistence.DIMENSION_VIEW_ROWS);
//		// view cube
//		Cube cubeViews = database.getCubeByName(CubeViewPersistence.CUBE_VIEWS);
//		if (cubeViews == null)
//			database.addUserInfoCube(CubeViewPersistence.CUBE_VIEWS,
//					new Dimension[] { columns, rows });
	}
	
	/**
	 * Checks if the given cube is holds cube view definitions.
	 * @param cube
	 * @return true if the cube contains only view definitions, false otherwise
	 */
    public final boolean isViewCube(Cube cube) {
    	return persister.isViewsCube(cube);
    }
    
    /**
     * Saves the given cube view
     * @param view
     */
    final void save(CubeView view) {
    	persister.save(view);
	}
    
    /**
     * Removes the given cube view
     * @param view
     */
    final boolean delete(CubeView view) {
    	return persister.delete(view);
	}
    
    /**
     * Returns the internal used representation for the given cube view.
     * Currently the internal format is xml.
     * @param view
     * @return
     */
    final String getRawDefinition(CubeView view) {
    	String def = "";
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	try {
    		CubeViewWriter.getInstance().toXML(bout, view);
    		try {
    			def = bout.toString("UTF-8");
    		}catch(UnsupportedEncodingException e) {}
    	} finally {
    		try {
    			bout.close();
    		}catch(IOException e) {}
    	}
    	return def;
    }

	/**
	 * Creates a new CubeView. The parameters in the args array are:
	 * 1. String id, the id for the new CubeView.
	 * 2. String name, the name for the new CubeView.
	 * 3. Cube srcCube, the source cube for the new CubeView.
	 * [optional 4-n] Property prop, a property object for the new CubeView. 
	 */
    protected Object create(Class clObject, Object [] args) {
		// check if a CubeView is wanted
		if((clObject.isAssignableFrom(CubeView.class)) && args.length >= 3) {
			String id = (String)args[0];
			String name = (String)args[1];
			Cube srcCube = (Cube)args[2];
			CubeView view = new CubeViewImpl(id, name, srcCube);
			// If more than 3 args are passed, the following are
			// Property objects
			for (int i = 3, n = args.length; i < n; i++) {				
				view.addProperty((Property) args[i]);
			}
			return view;
		}
		return null;
	}

	protected boolean delete(Object obj) {
		if(obj instanceof CubeView) {
			return delete((CubeView)obj);
		}
		return false;
	}
	
	protected Object load(Database db, String id) throws PaloPersistenceException {
		return persister.load(db,id);
	}
	
	protected void load(Database db, Map cubeId2viewId, Map views)
			throws PaloPersistenceException {
		persister.load(db, cubeId2viewId, views);
	}

	final void createEnvironment(Database database) {
		if (database.getConnection().getType() == Connection.TYPE_WSS) {
			return;
		}
		// check if dimensions are there:
		Dimension columns = database.getDimensionByName(
					CubeViewPersistence.LEGACY_DIMENSION_VIEW_COLUMNS);
//					CubeViewPersistence.NEW_VIEWS_COL_DIMENSION);
		if (columns == null)
			columns = database.addDimension(
					CubeViewPersistence.LEGACY_DIMENSION_VIEW_COLUMNS);
//					CubeViewPersistence.NEW_VIEWS_COL_DIMENSION);
		if (columns.getDefaultHierarchy().getElementByName(
				CubeViewPersistence.COL_DEF) == null)
			columns.getDefaultHierarchy().addElement(
					CubeViewPersistence.COL_DEF, Element.ELEMENTTYPE_STRING);

		// view rows
		Dimension rows = database.getDimensionByName(
					CubeViewPersistence.LEGACY_DIMENSION_VIEW_ROWS);
//					CubeViewPersistence.NEW_VIEWS_ROW_DIMENSION);
		if (rows == null)
			rows = database.addDimension(
					CubeViewPersistence.LEGACY_DIMENSION_VIEW_ROWS);
//					CubeViewPersistence.NEW_VIEWS_ROW_DIMENSION);
		// view cube
		Cube cubeViews = database.getCubeByName(
					CubeViewPersistence.LEGACY_CUBE_VIEWS);
//					CubeViewPersistence.NEW_VIEWS_CUBE);
		if (cubeViews == null)
			database.addCube(CubeViewPersistence.LEGACY_CUBE_VIEWS,
					new Dimension[] { rows, columns });

//			database.addCube(CubeViewPersistence.NEW_VIEWS_CUBE,
//					new Dimension[] { rows, columns });
	}
}
