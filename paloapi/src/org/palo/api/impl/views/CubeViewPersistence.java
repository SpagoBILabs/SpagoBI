/*
*
* @file CubeViewPersistence.java
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
* @version $Id: CubeViewPersistence.java,v 1.45 2009/12/14 12:46:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.palo.api.Connection;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.impl.PersistenceErrorImpl;
import org.palo.api.persistence.PaloPersistenceException;
import org.palo.api.persistence.PersistenceError;

import com.tensegrity.palo.xmla.ext.views.SQLConnection;

/**
 * <code>CubeViewPersistence</code>, API and access point for view persistence.
 *
 * @author Stepan Rutz
 * @author ArndHouben
 * @version $Id: CubeViewPersistence.java,v 1.45 2009/12/14 12:46:57 PhilippBouillon Exp $
 */
class CubeViewPersistence {
	
	//LEGACY VIEW MANAGING
	private static final String SYSTEM_PREFIX = "#"; //$NON-NLS-1$
	static final String LEGACY_DIMENSION_VIEW_COLUMNS = SYSTEM_PREFIX + "viewcolumns"; //$NON-NLS-1$
	static final String LEGACY_DIMENSION_VIEW_ROWS = SYSTEM_PREFIX + "viewrows"; //$NON-NLS-1$
	static final String LEGACY_CUBE_VIEWS = SYSTEM_PREFIX + "views"; //$NON-NLS-1$

	//INBETWEEN MANAGING
	private static final String USER_INFO_PREFIX = "##";	
	private static final String DIMENSION_VIEW_COLUMNS = USER_INFO_PREFIX + "view_columns"; //$NON-NLS-1$
	private static final String DIMENSION_VIEW_ROWS = USER_INFO_PREFIX + "view_rows"; //$NON-NLS-1$
	private static final String CUBE_VIEWS = USER_INFO_PREFIX + "view_cubes"; //$NON-NLS-1$

//once again back to legacy...	
//	//INBETWEEN NEW MANAGING ;) (PR 7021)
//	static final String NEW_VIEWS_COL_DIMENSION = SYSTEM_PREFIX + "view_items"; //$NON-NLS-1$
//	static final String NEW_VIEWS_ROW_DIMENSION = SYSTEM_PREFIX + "view_ids"; //$NON-NLS-1$
//	static final String NEW_VIEWS_CUBE = SYSTEM_PREFIX + "view_definitions"; //$NON-NLS-1$
	
	static final String COL_DEF = "Def"; //$NON-NLS-1$
    
	static final String PATH_DELIMETER = "///";
	static final String DELIMITER = ",";
	// For legacy reasons, it only gets _read_; never written anymore.
	static final String DIM_HIER_DELIMITER = "~~~";
	static final String GROUP_DELIMITER = ":";
	
    //FACTORY
    private static CubeViewPersistence instance = new CubeViewPersistence();    
    public static CubeViewPersistence getInstance() {
		return instance;
	}

    //--------------------------------------------------------------------------
    //INSTANCE
    //
    private CubeViewPersistence() {
	}
    
    final boolean isViewsCube(Cube cube) {
		String name = cube.getName();
		return name.equals(CUBE_VIEWS) || name.equals(LEGACY_CUBE_VIEWS);
	}
    
    final void save(CubeView view) {
		try {
			saveInternal(view);
		} catch (Exception e) {
			throw new PaloAPIException("Could not save view '"+ view.getName()+"'!",e);
		}
	}
    
    final Object load(Database database, String id) {
		try {
			ArrayList errors = new ArrayList();
			// load it:
			Cube viewsCube = database.getCubeByName(LEGACY_CUBE_VIEWS); //NEW_VIEWS_CUBE);
			return loadCubeView(database, viewsCube, id, errors);
		} catch (Exception e) {
			System.err.println("CubeViewPersistence.load: " + e); //$NON-NLS-1$
		}
		return null;
	}
        
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
    
    final String[] getIDs(Database db) {
    	Cube viewsCube = db.getCubeByName(LEGACY_CUBE_VIEWS); //NEW_VIEWS_CUBE);
    	if(viewsCube == null)
    		return new String[0];
    	
    	Dimension viewIds = viewsCube.getDimensionByName(LEGACY_DIMENSION_VIEW_ROWS); //NEW_VIEWS_ROW_DIMENSION);
    	Element[] elViewIds = viewIds.getDefaultHierarchy().getElements();
    	String[] ids = new String[elViewIds.length];
		for(int i=0;i<elViewIds.length;++i)
			ids[i] = elViewIds[i].getName();
		return ids;
    }
    
    final boolean delete(CubeView view) {
		try {
			deleteInternal(view);
			return true;
		} catch (Exception e) {
			System.err.println("CubeQueryPersistence.delete: " + e); //$NON-NLS-1$
		}
		return false;
	}
    
    private final void deleteInternal(CubeView view) {
		Database database = view.getCube().getDatabase();
		if (database.getConnection().getType() == Connection.TYPE_XMLA) {
			String host = database.getConnection().getServer();
			String service = database.getConnection().getService();
			String user = database.getConnection().getUsername();
			SQLConnection sqlCon = new SQLConnection();
			try {
				sqlCon.deleteView(host, service, user, database.getId(), 
					view.getId());
			} finally {
				sqlCon.close();
			}
			return;
		}
		deleteFrom(LEGACY_DIMENSION_VIEW_ROWS, view.getId(),database);
//			NEW_VIEWS_ROW_DIMENSION, view.getId(),database);
	}
    
    private final void saveInternal(CubeView view) throws Exception {
    	if (view.getCube().getDatabase().getConnection().getType() == Connection.TYPE_XMLA) {

    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
        	try {
        		CubeViewWriter.getInstance().toXML(bout, view);
        	} finally {
        		bout.close();
        	}
        	SQLConnection sqlCon = new SQLConnection();
        	try {
    			Database database = view.getCube().getDatabase();
        		String host = database.getConnection().getServer();
    			String service = database.getConnection().getService();
    			String user = database.getConnection().getUsername();

        		if (!sqlCon.writeView(host, service, user, 
        				view.getCube().getDatabase().getId(), view.getId(),
        				bout.toString("UTF-8"))) {
        			System.err.println("Error when writing XMLA view...");
        		}
        	} finally {
        		sqlCon.close();
        	}
    		return;
    	}
    	
    	
    	Cube viewsCube = 
    		view.getCube().getDatabase().getCubeByName(LEGACY_CUBE_VIEWS); //NEW_VIEWS_CUBE);
    	if (viewsCube == null) {
    		CubeViewManager.getInstance().createEnvironment(view.getCube().getDatabase());
    		viewsCube = 
        		view.getCube().getDatabase().getCubeByName(LEGACY_CUBE_VIEWS); //NEW_VIEWS_CUBE);
    		if (viewsCube == null) {
    			throw new PaloIOException("Cannot save view '" + view.getName()
					+ "'!! No cube for storing views exists!");
    		}
    	}
    	
    	//go on and create coordinate:
    	Dimension[] viewsDims = viewsCube.getDimensions();
		// check dimensions:
		int rowIndex = 
			viewsDims[0].getName().equals(LEGACY_DIMENSION_VIEW_ROWS) ? 0 : 1;
//				viewsDims[0].getName().equals(NEW_VIEWS_ROW_DIMENSION) ? 0 : 1;
		int colIndex = rowIndex == 0 ? 1 : 0;
		Element col = 
			viewsDims[colIndex].getDefaultHierarchy().getElementByName(COL_DEF);
    	
    	// now save
		Element idElement = viewsDims[rowIndex].getDefaultHierarchy()
				.getElementByName(view.getId());
		if (idElement == null)
			idElement = viewsDims[rowIndex].getDefaultHierarchy().addElement(
					view.getId(), Element.ELEMENTTYPE_STRING);

		//coordinate:
		Element[] coordinate = new Element[2];
		coordinate[rowIndex] = idElement;
		coordinate[colIndex] = col;
		
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	try {
    		CubeViewWriter.getInstance().toXML(bout, view);
    	} finally {
    		bout.close();
    	}
    	//store the xml code into cubeViews cube
    	viewsCube.setData(coordinate, bout.toString("UTF-8"));
	}
    
    
    private final CubeView loadCubeView(Database database, Cube viewsCube,
			String viewId, Collection errors) throws IOException {
    	if(viewsCube == null) {
    		errors.add(new PersistenceErrorImpl(
					"No cube for views handling found!", viewId,
					null, database, viewId,
					PersistenceError.LOADING_FAILED, null,
					PersistenceError.TARGET_GENERAL));
    		return null;
    	}
		CubeView view = null;
		String viewDef = null;
		Element[] coordinate = getViewCoordinate(viewsCube, viewId);
		try {
			viewDef = viewsCube.getData(coordinate).toString();
		} catch (PaloAPIException pex) {
			/* handled later */
		}

		if (viewDef == null) {
			PersistenceError error = new PersistenceErrorImpl(
					"Failed to load cube view", viewId, null,
					database, viewId,
					PersistenceError.LOADING_FAILED, null,
					PersistenceError.TARGET_GENERAL);
			errors.add(error);
			return null;
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(viewDef
				.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			view = CubeViewReader.getInstance().fromXML(bin, viewId, database,
					errors);
			// } catch (PaloPersistenceException pex) {
			// errors.addAll(Arrays.asList(pex.getErrors()));
		} finally {
			bin.close();
		}
		return view;
	}
    
    private final CubeView loadCubeViewXMLA(SQLConnection sqlCon, String id, Database db) 
    	throws IOException {
		CubeView view = null;
		String viewDef = null;
		try {
			String host = db.getConnection().getServer();
			String service = db.getConnection().getService();
			String user = db.getConnection().getUsername();
			viewDef = sqlCon.loadView(host, service, user, db.getId(), id);
		} catch (PaloAPIException pex) {
			/* handled later */
		}
		
		if (viewDef == null) {
			return null;
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(viewDef
				.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			view = CubeViewReader.getInstance().fromXML(bin,
					id, db, new HashSet());
		} finally {
			bin.close();
		}
		return view;
	}
    
    final void load(Database database, Map cubeId2viewId, Map views)
			throws PaloPersistenceException {
    	if (database.getConnection().getType() == Connection.TYPE_XMLA) {
    		SQLConnection sqlCon = new SQLConnection();
        	try {
    			String host = database.getConnection().getServer();
    			String service = database.getConnection().getService();
    			String user = database.getConnection().getUsername();
        		String [] viewIds = sqlCon.getAllViewIds(
        				host, service, user, database.getId());
        		for (int i = 0; i < viewIds.length; i++) {
        			CubeView view = null;
					try {
						view = loadCubeViewXMLA(sqlCon, viewIds[i], database);
					} catch (IOException e) {
					}
        			if (view != null) {
        				if (view.getCube() != null) {
    						Set _viewIds = getViewIds(cubeId2viewId, view.getCube());
    						_viewIds.add(view.getId());
    						views.put(view.getId(), view);        					
        				}
        			}
        		}
        	} finally {
        		sqlCon.close();
        	}
			return;
		}
    	
    	ArrayList errors = new ArrayList();
    	//load and transform views...
//    	loadViewsFrom(NEW_VIEWS_CUBE, NEW_VIEWS_ROW_DIMENSION, database,
//				cubeId2viewId, views, errors, false);
		loadViewsFrom(LEGACY_CUBE_VIEWS, LEGACY_DIMENSION_VIEW_ROWS, database,
				cubeId2viewId, views, errors, false);
		loadViewsFrom(CUBE_VIEWS, DIMENSION_VIEW_ROWS, database, cubeId2viewId,
				views, errors, true);
//		loadViewsFrom(LEGACY_CUBE_VIEWS, LEGACY_DIMENSION_VIEW_ROWS, database,
//				cubeId2viewId, views, errors, true);
    	
		// check nested errors...
		if (!errors.isEmpty())
			throw new PaloPersistenceException((PersistenceError[]) errors
					.toArray(new PersistenceError[errors.size()]),
					"Errors during loading of cube views!!");
	}
    
    private final Set getViewIds(Map cubeId2viewId,Cube cube) {
    	Set views = (Set)cubeId2viewId.get(cube.getId());
    	if(views == null) {
    		views = new LinkedHashSet();
    		cubeId2viewId.put(cube.getId(), views);
    	}
    	return views;
    }
    
    private final void deleteFrom(String views_dim, String elName, Database db) {
		Dimension rows = db.getDimensionByName(views_dim);
		if (rows != null) {
			Element viewId = rows.getDefaultHierarchy()
					.getElementByName(elName);
			if (viewId != null)
				rows.getDefaultHierarchy().removeElement(viewId);
		}
	}
    
    private final Element[] getViewCoordinate(Cube viewsCube, String viewId) {
		if (viewsCube == null)
			return null;

		Dimension[] viewsDims = viewsCube.getDimensions();
		// check dimensions:
		int rowIndex = viewsDims[0].getName().equals(
				CubeViewPersistence.DIMENSION_VIEW_ROWS) ? 0 : 1;
		int colIndex = rowIndex == 0 ? 1 : 0;

		Element def = 
			viewsDims[colIndex].getDefaultHierarchy().getElementByName(COL_DEF);
		Element view = 
			viewsDims[rowIndex].getDefaultHierarchy().getElementByName(viewId);
		Element[] coordinate = new Element[2];
		coordinate[rowIndex] = view;
		coordinate[colIndex] = def;
		return coordinate;
	}
    
    private final void loadViewsFrom(String cubeName, String rows, Database db,
			Map<String, String> cubeId2viewId, Map<String, CubeView> views,
			Collection errors,
			boolean transformed) throws PaloPersistenceException {
    	Cube viewsCube = db.getCubeByName(cubeName);
    	if(viewsCube == null)
    		return;
    	
    	//construct coordinates:
    	Dimension[] viewsDims = viewsCube.getDimensions();
    	//check dimensions:
    	int rowIndex = viewsDims[0].getName().equals(rows) ? 0 : 1;
		int colIndex = rowIndex == 0 ? 1 : 0;
		
		Element col = 
			viewsDims[colIndex].getDefaultHierarchy().getElementByName(COL_DEF);
		Element[] viewIds = 
			viewsDims[rowIndex].getDefaultHierarchy().getElements();
		//construct coordinates:
		Element[][] coordinates = new Element[viewIds.length][];
		for(int i=0;i<viewIds.length;++i) {
			coordinates[i] = new Element[2];
			coordinates[i][rowIndex] = viewIds[i];
			coordinates[i][colIndex] = col;
		}
		
		//load all view definitions:
		Object[] values = viewsCube.getDataBulk(coordinates);
		
		Set<CubeView> transformedViews = new HashSet<CubeView>();
		for(int i = 0; i < values.length; ++i) {
			boolean addError = false;
			if(values[i] != null) {
				String viewDef = values[i].toString();
				if (!viewDef.equals("")) {
					try {
						CubeView view = loadViewFromDefinition(viewDef,
								viewIds[i].getName(), db, errors);
						if (view != null) {
							if (view.getCube() != null) {
								Set _viewIds = getViewIds(cubeId2viewId, view
										.getCube());
								_viewIds.add(view.getId());
								views.put(view.getId(), view);
								transformedViews.add(view);
							}
						} else
							addError = true;
					} catch (IOException ex) {
						System.err.println("Failed to cube view: "
								+ ex.getMessage());
					}
				} else
					addError = true;
			} else
				addError = true;
			

			//errors occurred?
			if (addError) {
				PersistenceError error = new PersistenceErrorImpl(
						"Failed to load cube view", viewIds[i].getName(), null,
						db, viewIds[i].getName(),
						PersistenceError.LOADING_FAILED, null,
						PersistenceError.TARGET_GENERAL);
				errors.add(error);
			}
		}
		
		//PR 7021: we should delete old cube/dims if they are empty, even
		//if no views were actually transformed
		if (transformed) {
			Hierarchy rowHierarchy = viewsDims[rowIndex].getDefaultHierarchy(); 
			// save and delete transformed views...
			for (CubeView view : transformedViews) {
				view.save();
				// delete transformed ones...
				Element viewEl = rowHierarchy.getElementByName(view.getId());
				if (viewEl != null)
					rowHierarchy.removeElement(viewEl);
			}
			//can we delete complete view?
			if(rowHierarchy.getElementCount() == 0) {
				//remove cube and dimensions:
				db.removeCube(viewsCube);
				for(Dimension dim : viewsDims)
					db.removeDimension(dim);
			}
		} else
			transformedViews.clear();
	}
    
    private final CubeView loadViewFromDefinition(String xmlDef,
			String viewId, Database db, Collection errors) throws IOException {
		CubeView view = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(xmlDef
				.getBytes("UTF-8")); //$NON-NLS-1$
		try {
			view = CubeViewReader.getInstance().fromXML(bin, viewId, db,
					errors);
		} finally {
			bin.close();
		}
		return view;
	}


}
