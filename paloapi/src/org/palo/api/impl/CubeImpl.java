/*
*
* @file CubeImpl.java
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
* @author Arnd Houben
*
* @version $Id: CubeImpl.java,v 1.111 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.palo.api.Cell;
import org.palo.api.ConnectionEvent;
import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ExportContext;
import org.palo.api.ExportDataset;
import org.palo.api.Lock;
import org.palo.api.PaloAPIException;
import org.palo.api.Property;
import org.palo.api.Property2;
import org.palo.api.Rule;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.persistence.PaloPersistenceException;
import org.palo.api.persistence.PersistenceError;
import org.palo.api.persistence.PersistenceObserver;

import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.LockInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.impl.CubeInfoImpl;
import com.tensegrity.palojava.loader.PropertyLoader;
import com.tensegrity.palojava.loader.RuleLoader;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * Default implementation of the <code>Cube</code> interface.
 * 
 * @author Arnd Houben
 * @author Stepan Rutz
 * @version $Id: CubeImpl.java,v 1.111 2010/03/11 10:42:20 PhilippBouillon Exp $
 */
class CubeImpl extends AbstractPaloObject implements Cube {
	
//	// Fix for PR 6732: Jedox Server does not support rules for this version.
//	private final static int MIN_RULES_MAJOR = 1;
//	private final static int MIN_RULES_MINOR = 5;
//	private final static int MIN_RULES_BUILD = 1646;
	
    //-------------------------------------------------------------------------
    // FACTORY
	//
//	final static CubeImpl getInstance(ConnectionImpl connection,
//			Database database, CubeInfo cubeInfo) {
//		String[] dimensions = cubeInfo.getDimensions();
//		Dimension[] _dimensions = new Dimension[dimensions.length];
////		Database database = connection.getDatabaseById(cubeInfo.getDatabase()
////				.getId());
//		for (int i = 0; i < dimensions.length; ++i)
//			_dimensions[i] = database.getDimensionById(dimensions[i]);
//		return getInstance(connection, cubeInfo, _dimensions);
//	}
//	
//    final static CubeImpl getInstance(ConnectionImpl connection,
//			CubeInfo cubeInfo, Dimension[] dimensions) {
//        Map cache = connection.getCache(CubeImpl.class);
//        
//		CompoundKey key = CubeImpl.createKey(cubeInfo);
//		//check if we have cached one:
//		CubeImpl cached = (CubeImpl)cache.get(key);
//		if(cached == null) {
//			cached = new CubeImpl(connection,cubeInfo, dimensions);
//			cache.put(key,cached);
//		}
////TODO like with databases we have to do a reload here...		
//		return cached;
//    }

    final static CubeImpl create(ConnectionImpl connection, Database database, CubeInfo cubeInfo) {
//		String[] dimensions = cubeInfo.getDimensions();
//		Dimension[] _dimensions = new Dimension[dimensions.length];
//		for (int i = 0; i < dimensions.length; ++i)
//			_dimensions[i] = database.getDimensionById(dimensions[i]);
		return create(connection, database, cubeInfo, null); //_dimensions);
    }

    final static CubeImpl create(ConnectionImpl connection, Database database, CubeInfo cubeInfo, Dimension[] dimensions) {
    	return new CubeImpl(connection, database, cubeInfo, dimensions);
    }

    private boolean tryReloadingDims = true;
    
    //-------------------------------------------------------------------------
    // INSTANCE
    //    
//    private final Database database;
//    private final String id;
//    private final String name;    
//    private final Map views;
    private Dimension dimensions[];
    private ExportContext exportContext;
    private final DbConnection dbConnection;
    private final ConnectionImpl connection;
    private final CubeInfo cubeInfo;
//    private final Map views;
//    private final Map ruleInfos;
    private final Map<String, RuleImpl> loadedRules;
    private final Map <String, Property2Impl> loadedProperties;
 //   private final Map<String, HierarchyImpl> loadedHierarchies;
    
    private final Database database;
    private final CompoundKey key;
//    private HashMap viewErrors;
    private final List viewObservers;
//    private final boolean rulesSupported;
    private final CubeViewStorageHandler viewStorageHandler;
    private final RuleLoader ruleLoader;
	private final PropertyLoader propertyLoader;
//    private final HierarchyLoader hierLoader;
    
//    private boolean rulesNeedReload = true;
    
    private CubeImpl(ConnectionImpl connection, Database database, CubeInfo cubeInfo, Dimension[] dimensions) {
		// this.database = database;
		// this.id = id;
		// this.name = name;
		// this.views = new LinkedHashMap();
    	this.cubeInfo = cubeInfo;
    	this.dimensions = dimensions;
    	this.connection = connection;
    	this.dbConnection = connection.getConnectionInternal();
//    	this.views = new LinkedHashMap();
//    	this.ruleInfos = new LinkedHashMap();
    	this.loadedRules = new LinkedHashMap<String, RuleImpl>();
    	this.loadedProperties = new LinkedHashMap<String, Property2Impl>();   
    	this.viewObservers = new ArrayList();
    	this.database = database;
//    	this.rulesSupported = connection.supportsRules();
    	this.ruleLoader = dbConnection.getRuleLoader(cubeInfo);
    	this.viewStorageHandler = ((DatabaseImpl)database).getViewStorageHandler();
    	this.key = new CompoundKey(new Object[] { CubeImpl.class, connection,
				cubeInfo.getDatabase().getId(), cubeInfo.getId() });
    	propertyLoader = dbConnection.getTypedPropertyLoader(cubeInfo);
//    	this.loadedHierarchies = new LinkedHashMap<String, HierarchyImpl>();
//    	hierLoader = dbConnection.getHierarchyLoader(cubeInfo);    	
    	//    	if (connection.getType() != Connection.TYPE_XMLA) {
//    		reloadRuleInfos();
//    		rulesNeedReload = false;
//    	}
	}

	public final CubeView addCubeView(String id, String name, Property [] properties) {
		return viewStorageHandler.addCubeView(this,id, name, properties);
//		if(views.containsKey(id))
//			throw new PaloAPIException("Cube view already exists!");
//		ApiExtensionController creator = ApiExtensionController.getInstance();
//		
//		CubeView view = creator.createCubeView(id, name, this, properties);
//		views.put(view.getId(), view);
//		return view;
	}

	public final CubeView addCubeView(String name, Property [] properties) {
		return viewStorageHandler.addCubeView(this,name, properties);
//		String id = Long.toString(System.currentTimeMillis());
//		while(views.containsKey(id)) {
//			long lg = Long.parseLong(id);
//			lg++;
//			id = Long.toString(lg);
//		}
//		return addCubeView(id, name, properties);
	}

	public final void commitLog() {
		dbConnection.save(cubeInfo);
	}

	public final CubeView getCubeView(String id) throws PaloIOException {
		try {
			return viewStorageHandler.getCubeView(this,id);
		} catch (PaloPersistenceException e) {
//			e.printStackTrace();
//			System.err.println("Cube '"+getName()+"': failed to load view with id: "+id);
			//try to get the view from errors...
			PaloIOException pio = new PaloIOException("CubeView loading failed!",e);
			PersistenceError[] errors = e.getErrors();
			for(PersistenceError err : errors) {
				Object src = err.getSource();
				if(src instanceof CubeView)
					pio.setData(src);						
			}
			throw pio;
		}
//		try {
//			CubeView view = viewStorageHandler.getCubeView(this, id);
//			if(view != null)
//				notifyLoadComplete(view);
//			return view;
//		}catch(PaloPersistenceException pex) {
//			//notify observers...
//			if(pex.getType() == PaloPersistenceException.TYPE_LOAD_INCOMPLETE) {
//				PersistenceError[] errors = pex.getErrors();
//				Object view = errors.length>0? errors[0].getSource() : null;
//				notifyLoadIncomplete(view, errors);
//			} else
//				notifyLoadFailed(id, pex.getErrors());			
//		}
//		return null;
	}

	public final String[] getCubeViewIds() {
		return viewStorageHandler.getViewIds(this);
	}
	
	public final String getCubeViewName(String id) {
		return viewStorageHandler.getViewName(id);
	}
	
	public final void getCubeViews(PersistenceObserver observer) {
		String[] ids = viewStorageHandler.getViewIds(this);
		for (int i = 0; i < ids.length; ++i) {
			try {
				CubeView view = viewStorageHandler.getCubeView(this, ids[i]);
				observer.loadComplete(view);
			}catch(PaloPersistenceException pex) {
				//notify observers...
				if(pex.getType() == PaloPersistenceException.TYPE_LOAD_INCOMPLETE) {
					PersistenceError[] errors = pex.getErrors();
					Object view = errors.length>0? errors[0].getSource() : null;
					observer.loadIncomplete(view, errors);
				} else
					observer.loadFailed(ids[i], pex.getErrors());
			}
		}
	}
				
	public final CubeView[] getCubeViews() {
		ArrayList views = new ArrayList();
		String[] ids = viewStorageHandler.getViewIds(this);
		for (int i = 0; i < ids.length; ++i) {
			CubeView view = getCubeViewOld(ids[i]);
			if (view != null)
				views.add(view);
		}
		return (CubeView[]) views.toArray(new CubeView[views.size()]);
	}


	public final int getCubeViewCount() {
		return viewStorageHandler.getViewCount(this);
	}
	
//	public final void loadCubeViews(PersistenceObserver viewObserver) {
//		CubeView[] cubeViews = getCubeViews();
//		for(int i=0;i<cubeViews.length;++i)
//			loadCubeView(cubeViews[i].getId(),viewObserver);
//		//finally we check for failed views:
//		DatabaseImpl db = (DatabaseImpl) database;
//		if (db.hasFailedViews()) {
//			Map errors = db.getFailedViews();
//			for(Iterator it = errors.keySet().iterator();it.hasNext();) {
//				String id = (String)it.next();
//				PersistenceError error = (PersistenceError)errors.get(id);
//				viewObserver.loadFailed(id,new PersistenceError[]{error});
//			}
//			errors.clear();
//		}
//
////		if (viewErrors != null && !viewErrors.isEmpty()) {
////			// the rest are load fails:
////			for (Iterator it = viewErrors.keySet().iterator(); it.hasNext();) {
////				String id = (String) it.next();
////				viewObserver.loadFailed(id, (PersistenceError[]) viewErrors
////						.get(id));
////			}
////			views.clear();
////		}
//	}

	public final Object getData(String[] coordinates) {
		CellInfo cell;
		try {
			cell = dbConnection.getData(cubeInfo, getCoordinates(coordinates));
			return cell.getValue();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

	public final Object getData(Element[] coordinates) {
		try {
			CellInfo cell = dbConnection.getData(cubeInfo,
					getCoordinates(coordinates));
			return cell.getValue();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

	public final Object[] getDataArray(String[][] elements) {
		Object values[];
		try {
			ElementInfo[][] elInfos = new ElementInfo[elements.length][];
			for(int i=0;i<elInfos.length;++i) {
				elInfos[i] = getCoordinates(i,elements[i]);
			}
			CellInfo[] cells = dbConnection.getDataArea(cubeInfo,elInfos);
			values = new Object[cells.length];
			for(int i=0;i<values.length;++i)
				values[i] = cells[i].getValue();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return values;
	}

	public final Object[] getDataArray(Element[][] elements) {
		Object values[];
		try {
			ElementInfo[][] elInfos = new ElementInfo[elements.length][];
			for(int i=0;i<elInfos.length;++i) {
				elInfos[i] = getCoordinates(elements[i]);
			}
			CellInfo[] cells = dbConnection.getDataArea(cubeInfo,elInfos);
			values = new Object[cells.length];
			for(int i=0;i<values.length;++i)
				values[i] = cells[i].getValue();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return values;
	}

    public final Object[] getDataBulk(Element[][] coordinates) {
    	if(coordinates == null || coordinates.length == 0)
    		return new Object[0];
    	
    	Object[] values;
		try {
			ElementInfo[][] coords = new ElementInfo[coordinates.length][];
			for (int i = 0; i < coords.length; ++i) {
				coords[i] = getCoordinates(coordinates[i]);
			}
			CellInfo[] cells = dbConnection.getDataArray(cubeInfo,coords);
			values = new Object[cells.length];
			for(int i=0;i<values.length;++i)
				values[i] = cells[i].getValue();
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return values;
	}

	public final synchronized ExportDataset getDataExport(ExportContext context) {
		try {
			exportContext = context;
			ExportDatasetImpl dataset = new ExportDatasetImpl(this);
			dataset.start();
			return dataset;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

	public final Database getDatabase() {
		return database;
	}

	public final Dimension getDimensionAt(int index) {
        reloadDims();
		if (dimensions == null || index < 0 || index >= dimensions.length)
            return null;
        return dimensions[index];
	}

	public final Dimension getDimensionByName(String name) {
        reloadDims();
		if (dimensions == null)
            return null;
        for (int i = 0; i < dimensions.length; ++i)
        {
            Dimension dimension = dimensions[i];
            if (dimension.getName().equalsIgnoreCase(name))
                return dimension;
        }
        return null;
	}
	
	public final Dimension getDimensionById(String id) {
		reloadDims();
		if(dimensions == null)
			return null;
		for(int i=0;i<dimensions.length;++i) {
			if(dimensions[i].getId().equals(id))
				return dimensions[i];
		}
		return null;
	}

	public final int getDimensionCount() {
		return cubeInfo.getDimensions().length;		
	}

	public final Dimension[] getDimensions() {
		reloadDims();
		return dimensions == null ? null : (Dimension[]) dimensions.clone();
	}

	private void reloadDims() {
		if (dimensions == null && tryReloadingDims) {
			tryReloadingDims = false;
			String[] dims = cubeInfo.getDimensions();
			dimensions = new Dimension[dims.length];
			for (int i = 0; i < dimensions.length; ++i)
				dimensions[i] = database.getDimensionById(dims[i]);
		}		
	}
	
	public final synchronized ExportContext getExportContext() {
		if(exportContext == null)
			exportContext = new ExportContextImpl(this);
		return exportContext;
	}
	
	public final void convert(int type) {				
		int result = dbConnection.convert(cubeInfo, getInfoType(type));
		((CubeInfoImpl) cubeInfo).setType(result);
	}

	public final synchronized ExportContext getExportContext(Element[][] area) {
		if(exportContext == null)
			exportContext = new ExportContextImpl(this, area);
		exportContext.setCellsArea(area);
		return exportContext;
	}

	public final int getExtendedType() {
		return Cube.CUBEEXTENDEDTYPE_REGULAR;
	}

	public final String getName() {
		dbConnection.reload(cubeInfo);
		return cubeInfo.getName();
	}

	public final BigInteger getCellCount() {
		dbConnection.reload(cubeInfo);
		return cubeInfo.getCellCount();
	}
	
	public final BigInteger getFilledCellCount() {
		dbConnection.reload(cubeInfo);
		return cubeInfo.getFilledCellCount();		
	}
	
	public final int getStatus() {
		dbConnection.reload(cubeInfo);
		return cubeInfo.getStatus();
	}
	
	public final boolean isAttributeCube() {
		return cubeInfo.getType() == CubeInfo.TYPE_ATTRIBUTE;
	}

	public final boolean isSubsetCube() {
		return PaloObjects.isSubsetCube(this);
	}

	public final boolean isViewCube() {
		return PaloObjects.isViewsCube(this);
	}

	public final void removeCubeView(CubeView view) {
		viewStorageHandler.removeCubeView(this, view);
//		if(view != null) {
//			views.remove(view.getId());
//			ApiExtensionController.getInstance().delete(view);
//		}
	}

	public final void setData(String[] coordinates, Object value) {
		setDataInternal(getCoordinates(coordinates), value,
				Cube.SPLASHMODE_DISABLED);
	}

	public final void setData(Element[] coordinates, Object value) {
		setDataInternal(getCoordinates(coordinates), value,
				Cube.SPLASHMODE_DISABLED);
	}

    public final void addDataArray(Element[][] coordinates, Object[] values,
			int splashMode) {
    	this.setDataArray(coordinates, values, true, splashMode, true);
    }

	public final void setDataArray(Element[][] coordinates, Object[] values,
			int splashMode) {
		this.setDataArray(coordinates, values, false, splashMode, true);
	}

	public final void setDataArray(Element[][] coordinates, Object[] values, boolean add, int splashMode, boolean notifyEventProcessors) {
        if (values == null || values.length==0)
            return;
        
        try {
//        	int _splashMode = getInternalSplashMode(splashMode);
        	ElementInfo[][] coords = new ElementInfo[coordinates.length][];
        	for(int i=0;i<coordinates.length;++i) {
        		coords[i] = getCoordinates(coordinates[i]);
        	}
        	dbConnection.setDataArray(cubeInfo,coords,values, add, splashMode, notifyEventProcessors);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);        	
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}
	
	public final void setDataSplashed(Element[] coordinates, Object value) {
		//just a simple implementation, later we do it more sophisticated...
		int splashMode = SPLASHMODE_DEFAULT;
		String valStr = value.toString();
		if(valStr.startsWith("#")) {
			splashMode = SPLASHMODE_DEFAULT;
			if(valStr.endsWith("%")) {
				double newVal = Double.parseDouble(valStr.substring(1,valStr.length()-1));
				double oldValue = ((Double)getData(coordinates)).doubleValue();
				newVal = oldValue * percent(newVal);
				value = new Double(newVal);
			} else {
				value = new Double(valStr.substring(1));				
			}
		}else if(valStr.startsWith("!!")) {
			splashMode = SPLASHMODE_BASE_ADD;
			value = new Double(valStr.substring(2));
		} else if(valStr.startsWith("!")) {
			splashMode = SPLASHMODE_BASE_SET;
			value = new Double(valStr.substring(1));
		} else
			value = new Double(valStr);
		
		ElementInfo[] coords = getCoordinates(coordinates);
		setDataInternal(coords, value, splashMode);
	}
	
	public final void setDataSplashed(Element[] coordinates, Object value, NumberFormat formatter) {
		if(formatter == null)
			setDataSplashed(coordinates, value);
		//just a simple implementation, later we do it more sophisticated...
		int splashMode = SPLASHMODE_DEFAULT;
		String valStr = value.toString();
		if(valStr.startsWith("#")) {
			splashMode = SPLASHMODE_DEFAULT;
			if(valStr.endsWith("%")) {
				valStr = valStr.substring(1,valStr.length()-1);
				setPercentage(coordinates,valStr, formatter);
				return;
			} else
				valStr = valStr.substring(1);		
		}else if(valStr.startsWith("!!")) {
			splashMode = SPLASHMODE_BASE_ADD;
			valStr = valStr.substring(2);
		} else if(valStr.startsWith("!")) {
			splashMode = SPLASHMODE_BASE_SET;
			valStr = valStr.substring(1);
		} 
		
		String _value = formatValue(valStr, formatter);
		
		
		ElementInfo[] coords = getCoordinates(coordinates);
		setDataInternal(coords, new Double(_value), splashMode);
	}
	
	public final void setData(Element[] coordinates, Object value,
			NumberFormat formatter) {
		String valStr = value.toString();
		String _value = formatValue(value.toString(), formatter);
		if (valStr.equals(_value))
			setDataInternal(getCoordinates(coordinates), value,
					Cube.SPLASHMODE_DISABLED);
		else
			setDataInternal(getCoordinates(coordinates), new Double(_value),
					Cube.SPLASHMODE_DISABLED);
	}

	public final void setDataSplashed(String[] coordinates, Object value,
			int splashMode) {
		if (value == null)
			return;

		try {
			ElementInfo[] coords = getCoordinates(coordinates);
			setDataInternal(coords, value, splashMode);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

	public final void setDataSplashed(Element[] coordinates, Object value,
			int splashMode) {
		if (value == null)
			return;

		try {
			ElementInfo[] coords = getCoordinates(coordinates);
			setDataInternal(coords, value, splashMode);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

	public final String getId() {
		return cubeInfo.getId();
	}

	public final CubeInfo getInfo() {		
		dbConnection.reload(cubeInfo);
		return cubeInfo;
	}
	
	public final boolean isSystemCube() {
		return cubeInfo.getType() == CubeInfo.TYPE_SYSTEM;
	}
	
	public final boolean isUserInfoCube() {
		return cubeInfo.getType() == CubeInfo.TYPE_INFO;
	}
	
//	public final int getType() {
//		if (cubeInfo.getType() == CubeInfo.TYPE_NORMAL) {
//			return CUBETYPE_NORMAL;
//		} else if (cubeInfo.getType() == CubeInfo.TYPE_ATTRIBUTE) {
//			return CUBETYPE_ATTRIBUTE;
//		} else if (cubeInfo.getType() == CubeInfo.TYPE_SYSTEM) {
//			return CUBETYPE_SYSTEM;
//		} else if (cubeInfo.getType() == CubeInfo.TYPE_INFO) {
//			return CUBETYPE_USERINFO;
//		}
//		return -1;
//	}
	public final int getType() {
		return getType(cubeInfo);
	}
	

	public final boolean equals(Object other) {
		if(other instanceof CubeImpl) {
			return key.equals(((CubeImpl)other).key);
		}
		return false;
	}
	
	public final int hashCode() {
		return key.hashCode();
	}
	
	
	public final Rule addRule(String definition) {
		return addRule(definition, null,false,null);
//		RuleInfo rule = ruleLoader.create(definition);
//		return createRule(rule);
//		checkRuleSupport();
//		RuleInfo ruleInfo = dbConnection.createRule(cubeInfo, definition);
//		Rule rule = createRule(ruleInfo);
//		return rule;
	}
	
	public final Rule addRule(String definition, String externalIdentifier,
			boolean useIt, String comment) {
		return addRule(definition, externalIdentifier, useIt, comment, true);
		// RuleInfo rule = ruleLoader.create(definition, externalIdentifier,
		// useIt, comment);
		// Rule newRule = createRule(rule);
		// fireRulesAdded(new Rule[]{newRule});
		// return newRule;
		//
		// checkRuleSupport();
		// RuleInfo ruleInfo = dbConnection.createRule(cubeInfo,
		// definition,externalIdentifier,useIt,comment);
		// Rule rule = createRule(ruleInfo);
		// return rule;
	}
	public final Rule addRule(String definition, String externalIdentifier,
			boolean useIt, String comment, boolean activate) {
		RuleInfo rule = ruleLoader.create(definition, externalIdentifier,
				useIt, comment, activate);
		Rule newRule = createRule(rule);
		fireRulesAdded(new Rule[] { newRule });
		return newRule;
	}

	public final Rule[] getRules() {
		String[] ids = ruleLoader.getAllRuleIds();
		ArrayList<Rule> rules = new ArrayList<Rule>(); 	
		for(String id : ids) {
			RuleInfo info = ruleLoader.load(id);
			Rule rule = getRule(info);
			if(rule != null)
				rules.add(rule);
		}
		return (Rule[])rules.toArray(new Rule[rules.size()]);
//
//		if(!rulesSupported)
//			return new Rule[0];
//		if (rulesNeedReload) {
//			reloadRuleInfos();
//			rulesNeedReload = false;
//		}
//		ArrayList rules = new ArrayList();
//		Iterator it = ruleInfos.values().iterator();
//		while(it.hasNext()) {
//			Rule rule = getRule((RuleInfo)it.next());
//			if(rule != null)
//				rules.add(rule);
//		}
//		return (Rule[])rules.toArray(new Rule[rules.size()]);
	}
	
	public final Rule getRule(Element[] coordinate) {
		try {
		ElementInfo[] coord = getCoordinates(coordinate); 
		RuleInfo rule = ruleLoader.load(coord);
		return getRule(rule);
		}catch(PaloException pex) {
			throw new PaloAPIException("Failed to load rule!", pex);
		}
//		if(!rulesSupported)
//			return null;
//		if (rulesNeedReload) {
//			reloadRuleInfos();
//			rulesNeedReload = false;
//		}
//		String ruleId = dbConnection.getRule(cubeInfo,getCoordinates(coordinate));
//		if(ruleId == null)
//			return null;
//		return (Rule)getRule((RuleInfo)ruleInfos.get(ruleId)); //rules.get(ruleId);
	}

	public final boolean removeRule(Rule rule) {
		RuleInfo _rule = ((RuleImpl) rule).getInfo();
		try {
			if (ruleLoader.delete(_rule)) {
				loadedRules.remove(rule.getId());
				fireRulesRemoved(new Rule[] { rule });
				return true;
			}
		} catch (PaloException pex) {
			/* ignore */
		}
		return false;
		// if(dbConnection.delete(((RuleImpl)rule).getInfo())) {
		// loadedRules.remove(rule.getId());
		// ruleInfos.remove(rule.getId());
		// return true;
		// }
		// // ConnectionImpl cimpl = (ConnectionImpl)
		// getDatabase().getConnection();
		// // if
		// (cimpl.getConnectionInternal().deleteRule(getDatabase().getName(),
		// // getName(), rule.getId())) {
		// // rules.remove(rule.getId());
		// // return true;
		// // }
		// return false;
	}
	
	public final boolean removeRule(String ruleId) {
		if(ruleLoader.delete(ruleId)) {
			Rule rmRule = loadedRules.remove(ruleId);
			if(rmRule != null)
				fireRulesRemoved(new Rule[]{rmRule});
			return true;
		}
		return false;
	}
	
	public void rename(String newName) {
    	String oldName = getName();
    		
    	dbConnection.rename(cubeInfo,newName);

//    	cubeInfo.setName(newName);    	
//    	System.err.println("old name: "+oldName);
//    	System.err.println("new name: "+newName);
    	
        //create event:
    	fireCubeRenamed(this,oldName);
	}


	public void registerViewObserver(PersistenceObserver cubeViewObserver) {
		if(!viewObservers.contains(cubeViewObserver))
			viewObservers.add(cubeViewObserver);		
	}

	public void unregisterViewObserver(PersistenceObserver cubeViewObserver) {
		viewObservers.remove(cubeViewObserver);
	}

    //NEW CELL API WHICH REPLACES LEGACY GET_DATA API:
//    public final void copy(Cell from, Cell to) {
//    	dbConnection.copy(((CellImpl)from).getInfo(), ((CellImpl)to).getInfo());
//    }
    public final Cell getCell(Element[] coordinate) {
		try {
			CellInfo cell = 
				dbConnection.getData(cubeInfo,getCoordinates(coordinate));
			return new CellImpl(this, cell, coordinate);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
    }
    public final Cell[] getCells(Element[][] coordinates) {
		Cell[] cells;
		try {
			ElementInfo[][] coords = new ElementInfo[coordinates.length][];
			for (int i = 0; i < coords.length; ++i) {
				coords[i] = getCoordinates(coordinates[i]);
			}
			CellInfo[] _cells = dbConnection.getDataArray(cubeInfo,coords);
			cells = new Cell[_cells.length];
			for(int i=0;i<_cells.length;++i) {
				String[] coordinate =_cells[i].getCoordinate();
				if(coordinate == null) {
					cells[i] = new CellImpl(this, _cells[i], coordinates[i]);
				} else {
					cells[i] = new CellImpl(this, _cells[i], coordinates[i]);
				}
			}
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (NullPointerException ex) {
			throw new PaloAPIException("Could not match cell coordinates to cube data. Has a dimension been removed or added to this cube?", ex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return cells;
    }
    
    private final boolean isEmpty(CellInfo cell) {
		Object value = cell.getValue();
		boolean isEmpty = value == null || value.toString().equals("");
		if (isEmpty) {
			return true;
		}
		if (cell.getType() == Cell.NUMERIC) {
			try {
				Double d = Double.parseDouble(value.toString());
				if (Math.abs(d) < 0.000001) {
					isEmpty = true;
				}
			} catch (Exception e) {						
			}
		}
		return isEmpty;
    }
    
    public final Cell[] getCells(Element[][] coordinates, boolean hideEmptyCells) {
    	if (!hideEmptyCells) {
    		return getCells(coordinates);
    	}
    	ArrayList <Cell> cellList = new ArrayList<Cell>();
		try {
			ElementInfo[][] coords = new ElementInfo[coordinates.length][];
			for (int i = 0; i < coords.length; ++i) {
				coords[i] = getCoordinates(coordinates[i]);
			}
			CellInfo[] _cells = dbConnection.getDataArray(cubeInfo,coords);			
			for(int i=0;i<_cells.length;++i) {
				if (isEmpty(_cells[i])) {
					continue;
				}
				String[] coordinate =_cells[i].getCoordinate();
				if(coordinate == null) {
					cellList.add(new CellImpl(this, _cells[i], coordinates[i]));
				} else {
					cellList.add(new CellImpl(this, _cells[i], coordinates[i]));
				}
			}
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (NullPointerException ex) {
			throw new PaloAPIException("Could not match cell coordinates to cube data. Has a dimension been removed or added to this cube?", ex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return cellList.toArray(new Cell[0]);
    }
    
    public final Cell[] getCellArea(Element[][] coordinates) {
		Cell[] cells;
		try {
			ElementInfo[][] coords = new ElementInfo[coordinates.length][];
			for (int i = 0; i < coords.length; ++i) {
				coords[i] = getCoordinates(coordinates[i]);
			}
			CellInfo[] _cells = dbConnection.getDataArea(cubeInfo,coords);
			cells = new Cell[_cells.length];
			for(int i=0;i<_cells.length;++i)
				cells[i] = new CellImpl(this, _cells[i]);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
		return cells;
    }

    public final Lock requestLock(Element[][] area) {
		try {
			ElementInfo[][] coords = new ElementInfo[area.length][];
			for (int i = 0; i < coords.length; ++i) {
				coords[i] = getCoordinates(area[i]);
			}
			LockInfo lock = dbConnection.requestLock(cubeInfo, coords);
			return new LockImpl(lock);
		} catch (Exception ex) {
			/* ignore */
		}
		return null;
	}
    public final Lock[] getLocks() {    	
		try {
			LockInfo[] lockInfos = dbConnection.getLocks(cubeInfo);
			Lock[] locks = new Lock[lockInfos.length];
			for(int i=0;i<locks.length;++i)
				locks[i] = new LockImpl(lockInfos[i]);
			return locks;
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);
		} catch (Exception e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
    }
    public final boolean commit(Lock lock) {
		try {
			if (lock != null)
				return dbConnection.commit(cubeInfo, ((LockImpl)lock).getInfo());
		} catch (Exception e) {
			/* ignore */
		}
		return false;    	
    }
    public final boolean rollback(Lock lock, int steps) {    	
		try {
			if (lock != null)
				return dbConnection.rollback(cubeInfo, ((LockImpl) lock)
						.getInfo(), steps);
		} catch (Exception e) {
			/* ignore */
		}
		return false;
	}

    public final boolean save() {
    	return dbConnection.save(cubeInfo);
    }
    
	//--------------------------------------------------------------------------
	// PACKAGE INTERNAL
	//
	/**
	 * Internal method, used during reloading of database
	 */
	final void removeAllCubeViews() {
		//do not remove views on palo server, cause then the upddates are gone...
		viewStorageHandler.removeLoadedViews(this);
//		views.clear(); 
	}

	final void reload(boolean doEvents) {
		tryReloadingDims = true;
		dimensions = null;
		
		//reload from server:		
		dbConnection.reload(cubeInfo);		
		
		//reload rules:
		reloadRuleInfos(doEvents);

//		ServerInfo server = dbConnection.getServerInfo();
//		if (server.getMajor() < MIN_RULES_MAJOR) {
//			return;
//		} else if (server.getMajor() == MIN_RULES_MAJOR) {
//			if (server.getMinor() < MIN_RULES_MINOR) {
//				return;
//			} else if (server.getMinor() == MIN_RULES_MINOR) {
//				if (server.getBuildNumber() <= MIN_RULES_BUILD) {
//					return;
//				}
//			}
//		}
//		if(!rulesSupported)
//			return;
//		
//		//reload rules:
//		reloadRuleInfos();
//		try {
//			rules.clear();		
//			RuleInfo[] ruleInfos = dbConnection.getRules(cubeInfo);
//			for (int i = 0; i < ruleInfos.length; ++i) {
//				Rule rule = new RuleImpl(dbConnection,this, ruleInfos[i]);
//				rules.put(rule.getId(), rule);
//			}
//		} catch (PaloException ex) {
//			// ignore...
//		}
	}
	
//	final void addViewError(String viewId, PersistenceError error) {
//		if(viewErrors == null)
//			viewErrors = new HashMap();
//		List errors = (List)viewErrors.get(viewId);
//		if(errors == null) {
//			errors = new ArrayList();
//			viewErrors.put(viewId, errors);
//		}
//		errors.add(error);
//	}
	
	final void clearCache() {
		for(RuleImpl rule : loadedRules.values()) {
			rule.clearCache();
		}
		loadedRules.clear();
		ruleLoader.reset();

		for(Property2Impl property : loadedProperties.values()) {
			property.clearCache();
		}
		loadedProperties.clear();
		propertyLoader.reset();
	    //TODO views?
//	    private final CubeViewStorageHandler viewStorageHandler;
	}
	
	
	final void reloadRuleInfos(boolean doEvents) {
		HashMap<String, RuleImpl> oldRules = 
			new HashMap<String, RuleImpl>(loadedRules);
		HashSet<RuleImpl> addedRules = new HashSet<RuleImpl>();

		ruleLoader.reset();
		loadedRules.clear();
		
		String[] ruleIds = ruleLoader.getAllRuleIds();
		for(String id : ruleIds) {
			RuleInfo info = ruleLoader.load(id);
			RuleImpl rule = getRule(info);
			if(rule != null)
				loadedRules.put(rule.getId(), rule);
			//rule added or removed:
			if(oldRules.containsKey(id))
				oldRules.remove(id);
			else
				addedRules.add(rule);
		}
		if (doEvents) {
			if (!oldRules.isEmpty())
				fireRulesRemoved(oldRules.values().toArray(new Rule[0]));
			if (!addedRules.isEmpty())
				fireRulesAdded(addedRules.toArray(new Rule[0]));
		}
// ruleInfos.clear();
//		if(!rulesSupported) {
//			return;
//		}
//		RuleInfo[] infos = dbConnection.getRules(cubeInfo);		
//		for(int i=0;i<infos.length;++i) {
//			ruleInfos.put(infos[i].getId(),infos[i]);
//		}
	}
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	/**
	 * Checks if the corresponding rule instance to the given rule info
	 * was already loaded and returns it. If no rule instance was created 
	 * so far, this method will do it... 
	 * @param ruleInfo
	 * @return
	 */
	private final RuleImpl getRule(RuleInfo ruleInfo) {
		if(ruleInfo == null)
			return null;
		
		RuleImpl rule = loadedRules.get(ruleInfo.getId());
		if(rule == null) {
			//not loaded yet...
			rule = createRule(ruleInfo);
		}
		return rule;
	}

	/**
	 * Creates a new rule instance from the given rule info and adds it to
	 * the list of all loaded rules
	 * @param elInfo
	 * @return
	 */
	private final RuleImpl createRule(RuleInfo ruleInfo) {
		RuleImpl rule = new RuleImpl(dbConnection,this,ruleInfo);
		loadedRules.put(rule.getId(), rule);
//		ruleInfos.put(rule.getId(), ruleInfo);
		return rule;
	}

	
    private final void setDataInternal(ElementInfo[] coordinates, Object value,
			int splashMode) {
		if (value == null)
			return;

		try {
			if (value instanceof Number) {
				dbConnection.setDataNumericSplashed(cubeInfo, coordinates,
						((Number) value).doubleValue(), splashMode);
			} else {
				dbConnection.setDataString(cubeInfo, coordinates, value
						.toString());
			}
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		} catch (RuntimeException e) {
			throw new PaloAPIException(e.getMessage(), e);
		}
	}

    // TODO either deprecate or find a way to get the active hierarchy in
    // a dimension
	private final ElementInfo[] getCoordinates(String[] names) {
		ElementInfo[] coords = new ElementInfo[names.length];
		for (int i = 0; i < coords.length; ++i) {
			Dimension dimension = getDimensionAt(i);
			ElementImpl element = 
				(ElementImpl)dimension.getDefaultHierarchy().getElementByName(names[i]);
			if(element == null)
				throw new PaloAPIException("Element not found! Dimension '"+dimension.getName()+"' contains no element with name '"+names[i]+"'!!");
			coords[i] = element.getInfo();
		}
		return coords;
	}
	
    // TODO either deprecate or find a way to get the active hierarchy in
    // a dimension
	private final ElementInfo[] getCoordinates(int dimIndex, String[] names) {
		ElementInfo[] coords = new ElementInfo[names.length];
		for (int i = 0; i < coords.length; ++i) {
			Dimension dimension = getDimensionAt(dimIndex);
			coords[i] = 
				((ElementImpl) dimension.getDefaultHierarchy().getElementByName(names[i])).getInfo();
		}
		return coords;
	}
	
	private final ElementInfo[] getCoordinates(Element[] elements) {
		ElementInfo[] coords = new ElementInfo[elements.length];
		for(int i=0;i<coords.length;++i) {
			if(elements[i] instanceof VirtualElementImpl)
				coords[i] = ((VirtualElementImpl)elements[i]).getInfo();
			else
				coords[i] = ((ElementImpl)elements[i]).getInfo();
		}
		return coords;
	}
	
	
	
//	private final void loadCubeView(String id) {
//		// check database if we have failed views
//		DatabaseImpl db = (DatabaseImpl) database;
//		if (db.hasFailedView(id)) {
//			Map errors = db.getFailedViews();
//			notifyLoadFailed(
//					id,
//					new PersistenceError[] {(PersistenceError) errors.get(id)});
//			errors.remove(id);
//		}
//
//		if (viewErrors != null && viewErrors.containsKey(id)) {
//			List errors = (List) viewErrors.get(id);
//			viewErrors.remove(id);
//			notifyLoadIncomplete(views.get(id), (PersistenceError[]) errors
//					.toArray(new PersistenceError[errors.size()]));
//		} else {
//			if (views.containsKey(id))
//				notifyLoadComplete(views.get(id));
//		}
//	}
//
//	private final void loadCubeViews() {
//		CubeView[] cubeViews = (CubeView[]) views.values().toArray(
//				new CubeView[views.size()]);
//		for (int i = 0; i < cubeViews.length; ++i)
//			loadCubeView(cubeViews[i].getId());
//
//		// finally we check for failed views:
//		DatabaseImpl db = (DatabaseImpl) database;
//		if (db.hasFailedViews()) {
//			Map errors = db.getFailedViews();
//			for (Iterator it = errors.keySet().iterator(); it.hasNext();) {
//				String id = (String) it.next();
//				PersistenceError error = (PersistenceError) errors.get(id);
//				notifyLoadFailed(id, new PersistenceError[] { error });
//			}
//			errors.clear();
//		}
//	}

	private final void notifyLoadFailed(String sourceId, PersistenceError[] errors) {
		for(int i=0,n=viewObservers.size();i<n;++i) {
			PersistenceObserver observer = 
				(PersistenceObserver)viewObservers.get(i);
			observer.loadFailed(sourceId, errors);
		}		
	}
	
	private final void notifyLoadIncomplete(Object view,
			PersistenceError[] errors) {
		for (int i = 0, n = viewObservers.size(); i < n; ++i) {
			PersistenceObserver observer = (PersistenceObserver) viewObservers
					.get(i);
			observer.loadIncomplete(view, errors);
		}
	}
	
	private final void notifyLoadComplete(Object view) {
		for (int i = 0, n = viewObservers.size(); i < n; ++i) {
			PersistenceObserver observer = (PersistenceObserver) viewObservers
					.get(i);
			observer.loadComplete(view);
		}
	}

//	public CubeView addCubeView(String id, String name, boolean hideEmpty) {
//		if (!hideEmpty) {
//			return addCubeView(id, name, new Property [] {
//					new Property(CubeView.PROPERTY_ID_HIDE_EMPTY, "false")});
//		}
//		return addCubeView(id, name, new Property [] {
//				new Property(CubeView.PROPERTY_ID_HIDE_EMPTY, "true")});
//	}
//
//	public CubeView addCubeView(String name, boolean hideEmpty) {
//		if (!hideEmpty) {
//			return addCubeView(name, new Property [] {
//					new Property(CubeView.PROPERTY_ID_HIDE_EMPTY, "false")});
//		}
//		return addCubeView(name, new Property [] {
//				new Property(CubeView.PROPERTY_ID_HIDE_EMPTY, "true")});
//	}
//
//    private static final CompoundKey createKey(CubeInfo cubeInfo) {
//		return new CompoundKey(new Object[] { CubeImpl.class,
//				cubeInfo.getDatabase().getId(), 
//				cubeInfo.getId() 
//				});
//	}

	private final void setPercentage(Element[] coordinates, String valStr,
			NumberFormat formatter) {
		valStr = formatValue(valStr, formatter);
		double newVal = Double.parseDouble(valStr);
		double oldValue = ((Double) getData(coordinates)).doubleValue();
		newVal = oldValue * percent(newVal);
		Double value = new Double(newVal);
		ElementInfo[] coords = getCoordinates(coordinates);
		setDataInternal(coords, value, SPLASHMODE_DEFAULT);
		return;
	}
	
	private final double percent(double val) {
		return 1+(val/100);
	}

	public String[] getAllPropertyIds() {
		return propertyLoader.getAllPropertyIds();
	}

	public Property2 getProperty(String id) {
		PropertyInfo propInfo = propertyLoader.load(id);
		if (propInfo == null) {
			return null;
		}
		Property2 property = loadedProperties.get(propInfo.getId());
		if (property == null) {
			property = createProperty(propInfo);
		}

		return property;
	}
	
	public void addProperty(Property2 property) {
		if (property == null) {
			return;
		}
		Property2Impl _property = (Property2Impl)property;
		propertyLoader.loaded(_property.getPropInfo());
		loadedProperties.put(_property.getId(), _property);
	}
	
	public void removeProperty(String id) {
		Property2 property = getProperty(id); 
		if (property == null) {
			return;
		}
		if (property.isReadOnly()) {
			return;
		}
		loadedProperties.remove(property);
	}

	private void createProperty(Property2 parent, PropertyInfo kid) {
		Property2 p2Kid = Property2Impl.create(parent, kid);
		parent.addChild(p2Kid);		
		for (PropertyInfo kidd: kid.getChildren()) {
			createProperty(p2Kid, kidd);
		}
	}
	
	private Property2 createProperty(PropertyInfo propInfo) {
		Property2 prop = Property2Impl.create(null, propInfo);
		for (PropertyInfo kid: propInfo.getChildren()) {
			createProperty(prop, kid);
		}
		return prop;
	}

//	private final void checkRuleSupport() {
//		if(!rulesSupported) {
//			ServerInfo srvInfo = dbConnection.getServerInfo();
//			String srvVersion = srvInfo.getMajor()+"."+srvInfo.getMinor();
//			throw new PaloAPIException("Palo Server "+srvVersion+" does not support rules!");
//		}
//	}

	private final void fireCubeRenamed(Cube cube, String oldValue) {
		ConnectionEvent ev = new ConnectionEvent(getDatabase().getConnection(),
				getDatabase(),
				ConnectionEvent.CONNECTION_EVENT_CUBES_RENAMED, new Cube[]{cube});

		ev.oldValue = oldValue;
		connection.fireEvent(ev);
	}
	
	private final void fireRulesAdded(Rule[] rules) {
		ConnectionEvent ev = new ConnectionEvent(getDatabase().getConnection(),
				getDatabase(), ConnectionEvent.CONNECTION_EVENT_RULES_ADDED,
				rules);
		connection.fireEvent(ev);
	}
	
	private final void fireRulesRemoved(Rule[] rules) {
		ConnectionEvent ev = new ConnectionEvent(getDatabase().getConnection(),
				getDatabase(), ConnectionEvent.CONNECTION_EVENT_RULES_REMOVED,
				rules);
		connection.fireEvent(ev);
	}

	final void fireRuleChanged(Rule rule, Object changedValue) {
		ConnectionEvent ev = new ConnectionEvent(getDatabase().getConnection(),
				getDatabase(), ConnectionEvent.CONNECTION_EVENT_RULES_CHANGED,
				new Rule[] { rule });
		ev.oldValue = changedValue;
		connection.fireEvent(ev);
	}

//	private final String formatValue(String str, NumberFormat formatter) {				
//		int lastErrorIndex = -1;
//		int errorIndex = -1;
//		
//		ParsePosition pos;
//		Number numVal;
//		do {
//			lastErrorIndex = errorIndex;
//			pos = new ParsePosition(0);			
//			numVal = formatter.parse(str,pos);		
//			errorIndex = pos.getErrorIndex();
//			if (errorIndex >= str.length()) {
//				str = str.replaceAll(",", ".");
//			} else {
//				if (errorIndex != -1 && errorIndex != lastErrorIndex) {
//					String newStr;
//					if (str.charAt(errorIndex) == ',') {
//						newStr = str.substring(0, errorIndex) + ".";
//						if (errorIndex < (str.length() - 1)) {
//							newStr += str.substring(errorIndex + 1);
//						}
//					} else {
//						newStr = str.substring(0, errorIndex) + " " +
//							str.substring(errorIndex);
//					}
//					str = newStr;
//				}
//			}
//		} while (errorIndex != -1 && errorIndex != lastErrorIndex);
//		
//		//if we didn't pass whole string, we simply return input:
//		if(pos.getIndex()!=str.length())
//			return str;
//		return numVal.toString();
//	}

	private final String formatValue(String str, NumberFormat formatter) {				
//		try {
//			Double d = Double.parseDouble(str);
//			return d.toString();
//		} catch (NumberFormatException e) {
//			return str;
//		}
		ParsePosition pos = new ParsePosition(0);
		Number numVal = formatter.parse(str, pos);
		if(pos.getIndex()!=str.length()) {
			char [] chars = str.toCharArray();
			StringBuffer buffer = new StringBuffer();
			boolean valid = false;
			boolean hasSign = false;
			boolean hasExponent = false;
			for (int i = 0, n = chars.length; i < n; i++) {
				char c = chars[i];
				if (Character.isDigit(c) || c == '.' || c == ',' || c == '-' || c == '+' || c == 'e' || c == 'E') {
					if (c == 'e' || c == 'E' && valid) {						
						if (hasExponent) {
							valid = false;
							break;
						}
						hasExponent = true;
						hasSign = false;
						if (i >= (n - 1)) {
							valid = false;
							break;
						}
						if (chars[i + 1] == '+' || chars[i + 1] == '-') {
							if (i >= (n - 2)) {
								valid = false;
								break;
							}
							if (!Character.isDigit(chars[i + 2])) {
								valid = false;
								break;
							}
						}
					}
					if (c == '-' || c == '+' && valid) {
						if (hasSign) {
							valid = false;
							break;
						}
						hasSign = true;
					}
					if (!valid) {
						valid = true;
					}
					buffer.append(c);
				} else {
					if (valid) {
						break;
					}
				}
			}
			String res = buffer.toString().trim();
			DecimalFormat customFormatter = new DecimalFormat("#,##0.00");
			pos = new ParsePosition(0);
			numVal = customFormatter.parse(res, pos);
			if (pos.getIndex() != res.length()) {
				return str;
			}
		}
		return numVal.toString();
	}

	private final CubeView getCubeViewOld(String id)  {
		try {
			CubeView view = viewStorageHandler.getCubeView(this, id);
			if(view != null)
				notifyLoadComplete(view);
			return view;
		}catch(PaloPersistenceException pex) {
			//notify observers...
			if(pex.getType() == PaloPersistenceException.TYPE_LOAD_INCOMPLETE) {
				PersistenceError[] errors = pex.getErrors();
				Object view = errors.length>0? errors[0].getSource() : null;
				notifyLoadIncomplete(view, errors);
			} else
				notifyLoadFailed(id, pex.getErrors());			
		}
		return null;
	}

	public void clear() {
		try {
			dbConnection.clear(cubeInfo);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		}		
	}

	public void clear(Element[][] area) {
		try {
        	ElementInfo[][] coords = new ElementInfo[area.length][];
        	for(int i=0;i<coords.length;++i) {
        		coords[i] = getCoordinates(area[i]);
        	}
			dbConnection.clear(cubeInfo,coords);
		} catch (PaloException pex) {
			throw new PaloAPIException(pex);			
		}		
	}

//	public Hierarchy[] getHierarchies() {
//		String[] ids = hierLoader.getAllHierarchyIds();
//		ArrayList<Hierarchy> hierarchies= new ArrayList<Hierarchy>(); 	//to filter out null hierarchies!! => TODO better thrown an exception here???
//		for(String id : ids) {
//			HierarchyInfo info = hierLoader.load(id);
//			Hierarchy hierarchy = getHierarchy(info);
//			if(hierarchy != null)
//				hierarchies.add(hierarchy);
//		}
//		return (Hierarchy[])hierarchies.toArray(new Hierarchy[hierarchies.size()]);
//	}
//	
//	private final HierarchyImpl getHierarchy(HierarchyInfo hierInfo) {
//		if (hierInfo == null) {
//			return null;
//		}
//		HierarchyImpl hier = (HierarchyImpl) loadedHierarchies.get(hierInfo.getId());
//		if(hier== null) {
//			//not loaded yet...
//			hier = createHierarchy(hierInfo,true);
//		}
//		return hier;
//	}
	
//	/**
//	 * Creates a new dimension instance from the given dimensioninfo and adds 
//	 * it to the list of all loaded dimensions
//	 * @param dimInfo
//	 * @return
//	 */
//	private final HierarchyImpl createHierarchy(HierarchyInfo hierInfo, boolean fireEvent) {
//		HierarchyImpl hier = HierarchyImpl.create(connection, this, hierInfo, fireEvent);
//		loadedHierarchies.put(hier.getId(), hier);
////		hierInfos.put(hierInfo.getId(), hierInfo);
//		return hier;
//	}

	public boolean canBeModified() {
		return cubeInfo.canBeModified();
	}

	public boolean canCreateChildren() {
		return cubeInfo.canCreateChildren();
	}	
}
