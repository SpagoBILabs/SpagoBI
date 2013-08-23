/*
*
* @file XMLAConnection.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: XMLAConnection.java,v 1.50 2010/02/22 11:38:54 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.ParserConfigurationException;

import com.tensegrity.palo.xmla.builders.BuilderRegistry;
import com.tensegrity.palo.xmla.loader.XMLACubeLoader;
import com.tensegrity.palo.xmla.loader.XMLADatabaseLoader;
import com.tensegrity.palo.xmla.loader.XMLADimensionLoader;
import com.tensegrity.palo.xmla.loader.XMLAElementLoader;
import com.tensegrity.palo.xmla.loader.XMLAFunctionLoader;
import com.tensegrity.palo.xmla.loader.XMLAHierarchyLoader;
import com.tensegrity.palo.xmla.loader.XMLAPropertyLoader;
import com.tensegrity.palo.xmla.loader.XMLARuleLoader;
import com.tensegrity.palo.xmla.parsers.XMLADimensionRequestor;
import com.tensegrity.palo.xmla.parsers.XMLAHierarchyRequestor;
import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.ConnectionInfo;
import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.DbConnection;
import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.ExportContextInfo;
import com.tensegrity.palojava.HierarchyInfo;
import com.tensegrity.palojava.LockInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.PropertyInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.ServerInfo;
import com.tensegrity.palojava.events.ServerListener;
import com.tensegrity.palojava.impl.ConnectionInfoImpl;
import com.tensegrity.palojava.impl.PropertyInfoImpl;
import com.tensegrity.palojava.loader.CubeLoader;
import com.tensegrity.palojava.loader.DatabaseLoader;
import com.tensegrity.palojava.loader.DimensionLoader;
import com.tensegrity.palojava.loader.ElementLoader;
import com.tensegrity.palojava.loader.FunctionLoader;
import com.tensegrity.palojava.loader.HierarchyLoader;
import com.tensegrity.palojava.loader.PropertyLoader;
import com.tensegrity.palojava.loader.RuleLoader;

public class XMLAConnection implements DbConnection {
	public static final String PROPERTY_SAP_VARIABLES = "SAP_VARIABLES";
	public static final String PROPERTY_SAP_VARIABLE_DEFINITION = "SAP_VARIABLE_DEF";
	public static final String PROPERTY_SAP_VARIABLE_INSTANCE = "SAP_VAR";
	public static final String PROPERTY_SAP_VAR_SELECTED_VALUES = "SELECTEDVALUES";
	public static final String PROPERTY_SAP_VAR_ID = "ID";
	public static final String PROPERTY_SAP_VAR_UID = "UID";
	public static final String PROPERTY_SAP_VAR_NAME = "NAME";
	public static final String PROPERTY_SAP_VAR_ORDINAL = "ORDINAL";            
	public static final String PROPERTY_SAP_VAR_TYPE = "TYPE";               	
	public static final String PROPERTY_SAP_VAR_DATATYPE = "DATATYPE";
	public static final String PROPERTY_SAP_VAR_CHARMAXLENGTH = "CHARMAXLENGTH";     
	public static final String PROPERTY_SAP_VAR_PROCESSINGTYPE = "PROCESSINGTYPE";   
	public static final String PROPERTY_SAP_VAR_SELECTIONTYPE = "SELECTIONTYPE";  
	public static final String PROPERTY_SAP_VAR_ENTRYTYPE = "ENTRYTYPE";   
	public static final String PROPERTY_SAP_VAR_REFERENCEDIMENSION = "REFERENCEDIMENSION"; 
	public static final String PROPERTY_SAP_VAR_REFERENCEHIERARCHY = "REFERENCEHIERARCHY";
	public static final String PROPERTY_SAP_VAR_DEFAULTLOW = "DEFAULTLOW";
	public static final String PROPERTY_SAP_VAR_DEFAULTLOWCAP = "DEFAULTLOWCAP";
	public static final String PROPERTY_SAP_VAR_DEFAULTHIGH = "DEFAULTHIGH";
	public static final String PROPERTY_SAP_VAR_DEFAULTHIGHCAP = "DEFAULTHIGHCAP";
	public static final String PROPERTY_SAP_VAR_DESCRIPTION = "DESCRIPTION";
	public static final String PROPERTY_SAP_VAR_ELEMENTS = "ELEMENTS";
	                
	private static final boolean CACHE_CUBES = true;
	
	private final ConnectionInfoImpl connectionInfo;
	private XMLAClient xmlaClient = null;
	private boolean connected;
	private final HashMap <XMLADatabaseInfo, XMLACubeInfo []> cachedCubes;
	
	private XMLADatabaseLoader databaseLoader = null;
	private final HashMap <XMLADatabaseInfo, XMLACubeLoader> cubeLoaders;
	private final HashMap <XMLADatabaseInfo, XMLADimensionLoader> dimensionLoaders;
	private final HashMap <XMLADimensionInfo, XMLAElementLoader> elementLoaders;
	private final HashMap <XMLAHierarchyInfo, XMLAElementLoader> hElementLoaders;
	private XMLAFunctionLoader functionLoader = null;
	private final HashMap <XMLADimensionInfo, XMLAHierarchyLoader> hierarchyLoaders;
	private final HashMap <XMLACubeInfo, XMLARuleLoader> ruleLoaders;	
	private final HashMap <XMLACubeInfo, RuleInfo []> loadedRules;
	
	private XMLAPropertyLoader propertyLoader = null;
	private final HashMap <PaloInfo, XMLAPropertyLoader> propertyLoaders;
	
	XMLAConnection(String host, String service, String user, String pass) {
		connectionInfo = new ConnectionInfoImpl(host, service, user, pass);
		cachedCubes = new HashMap <XMLADatabaseInfo, XMLACubeInfo[]> ();
		cubeLoaders = new HashMap <XMLADatabaseInfo, XMLACubeLoader> ();
		dimensionLoaders = new HashMap <XMLADatabaseInfo, XMLADimensionLoader> ();
		elementLoaders = new HashMap <XMLADimensionInfo, XMLAElementLoader> ();
		hElementLoaders = new HashMap <XMLAHierarchyInfo, XMLAElementLoader> ();
		hierarchyLoaders = new HashMap <XMLADimensionInfo, XMLAHierarchyLoader> ();
		ruleLoaders = new HashMap <XMLACubeInfo, XMLARuleLoader> ();
		propertyLoaders = new HashMap <PaloInfo, XMLAPropertyLoader>();
		loadedRules = new HashMap <XMLACubeInfo, RuleInfo[]> ();
		
		try {
			ResourceBundle rb = ResourceBundle.getBundle("deploy", Locale.ITALIAN);
			String isSSL = rb.getString("is.ssl");

			xmlaClient = new XMLAClient(host, service, user, pass, Boolean.parseBoolean(isSSL));
			connected = true;			
		} catch (ParserConfigurationException e) {		
			e.printStackTrace();
		}
	}
	
 	public void addConsolidations(ElementInfo element, ElementInfo[] children,
			double[] weights) {
	}

	public CubeInfo addCube(DatabaseInfo database, String name,
			DimensionInfo[] dimensions) {
		throw new PaloException("XMLAConnections cannot add cubes.");
	}

	public DatabaseInfo addDatabase(String database, int type) {
		throw new PaloException("XMLAConnections cannot add databases.");
	}
	
	public DimensionInfo addDimension(DatabaseInfo database, String name) {
		DimensionInfo [] dims = getDimensions(database);
		for (int i = 0; i < dims.length; i++) {
			if (dims[i].getName().equals(name)) {
				return dims[i];
			}
		}
		return null;
	}

	public ElementInfo addElement(DimensionInfo dimension, String name,
			int type, ElementInfo[] children, double[] weights) {
		throw new PaloException("XMLAConnections cannot add elements.");
	}

	public ElementInfo addElement(HierarchyInfo hierarchy, String name,
			int type, ElementInfo[] children, double[] weights) {
		throw new PaloException("XMLAConnections cannot add elements.");
	}

	public void clear(DimensionInfo dimension) {
		throw new PaloException("XMLAConnections cannot clear dimensions.");
	}

	public RuleInfo createRule(CubeInfo cube, String definition) {
		throw new PaloException("XMLAConnections cannot create rules.");
	}

	public boolean delete(ElementInfo element) {
		throw new PaloException("XMLAConnections cannot delete elements.");
	}

	public boolean delete(CubeInfo cube) {
		throw new PaloException("XMLAConnections cannot delete cubes.");
	}

	public boolean delete(DatabaseInfo database) {
		throw new PaloException("XMLAConnections cannot delete databases.");
	}

	public boolean delete(DimensionInfo dimension) {
		throw new PaloException("XMLAConnections cannot delete dimensions.");
	}

	public boolean delete(RuleInfo rule) {
		throw new PaloException("XMLAConnections cannot delete rules.");
	}
	public boolean delete(String rule, CubeInfo cube) {
		throw new PaloException("XMLAConnections cannot delete rules.");
	}

	public CubeInfo getAttributeCube(DimensionInfo dimension) {
		return null;
	}

	public DimensionInfo getAttributeDimension(DimensionInfo dimension) {
		return null;
	}

	public CubeInfo[] getCubes(DatabaseInfo database) {
		CubeLoader cl = getCubeLoader(database);
		String [] cubeIds = cl.getAllCubeIds();
		CubeInfo [] cubeInfos = new CubeInfo[cubeIds.length];
		for (int i = 0, n = cubeIds.length; i < n; i++) {
			cubeInfos[i] = cl.load(cubeIds[i]);
		}
		return cubeInfos;
		//		if (CACHE_CUBES) {
//			if (cachedCubes.containsKey(database)) {
//				return cachedCubes.get(database);
//			}
//		}
//		XMLACubeInfo [] cubeInfo = 
//			BuilderRegistry.getInstance().getCubeInfoBuilder().
//				getCubeInfo(xmlaClient, (XMLADatabaseInfo) database);
//		if (CACHE_CUBES) {
//			cachedCubes.put((XMLADatabaseInfo) database, cubeInfo);
//		}
//		return cubeInfo;
	}

	public CubeInfo[] getCubes(DimensionInfo dimension) {
		CubeInfo [] allCubes = getCubes(dimension.getDatabase());
		List list = new ArrayList();
		for (int i = 0, n = allCubes.length; i < n; i++) {
			String [] dimIds = allCubes[i].getDimensions();
			for (int j = 0, m = dimIds.length; j < m; j++) {
				if (dimIds[j].equals(dimension.getId())) {
					list.add(allCubes[i]);
					break;
				}
			}
		}
		return (CubeInfo []) list.toArray(new CubeInfo[0]);
	}
	
	public CellInfo getData(CubeInfo cube, ElementInfo[] coordinate) {
		int n = coordinate.length;
		XMLAElementInfo [] els = new XMLAElementInfo[n];
		for (int i = 0; i < n; i++) {
			els[i] = (XMLAElementInfo) coordinate[i];
		}
		return BuilderRegistry.getInstance().getCubeInfoBuilder().
				getData(xmlaClient.getConnections()[0].getName(),
						xmlaClient, (XMLACubeInfo) cube, els, this);
	}

	public CellInfo[] getDataArea(CubeInfo cube, ElementInfo[][] coordinates) {
		int n = coordinates.length;
		XMLAElementInfo [][] els = new XMLAElementInfo[n][];
		for (int i = 0; i < n; i++) {
			int m = coordinates[i].length;
			els[i] = new XMLAElementInfo[m];
			for (int j = 0; j < m; j++) {
				els[i][j] = (XMLAElementInfo) coordinates[i][j];
			}
		}
		return BuilderRegistry.getInstance().getCubeInfoBuilder().
				getDataArray(xmlaClient.getConnections()[0].getName(),
						xmlaClient, (XMLACubeInfo) cube, els, this);
	}

	public CellInfo[] getDataArray(CubeInfo cube, ElementInfo[][] coordinates) {
		int n = coordinates.length;		
		XMLAElementInfo [][] els = new XMLAElementInfo[n][];
		for (int i = 0; i < n; i++) {
			int m = coordinates[i].length;
			els[i] = new XMLAElementInfo[m];
			for (int j = 0; j < m; j++) {
				els[i][j] = (XMLAElementInfo) coordinates[i][j];
			}
		}		
		return BuilderRegistry.getInstance().getCubeInfoBuilder().
				getDataBulk(xmlaClient.getConnections()[0].getName(),
						xmlaClient, (XMLACubeInfo) cube, els, this);
	}
	
	public Object getWholeCube(CubeInfo cube) {
		return BuilderRegistry.getInstance().getCubeInfoBuilder().
					readWholeCube(xmlaClient.getConnections()[0].getName(),
							xmlaClient, (XMLACubeInfo) cube, this);
	}
	
	class CoordinateStorage {
		private XMLAElementInfo [] coords;
		
		public CoordinateStorage(XMLAElementInfo [] coords) {
			this.coords = coords;
		}
		
		public XMLAElementInfo [] getCoords() {
			return coords;
		}
	}

	private final CellInfo [] performCopy(ArrayList <CoordinateStorage> allCoords, DimensionInfo [] origDims, CubeInfo cube, boolean ignoreEmptyCells) {
		XMLAElementInfo [][] coordinates = new XMLAElementInfo[allCoords.size()][origDims.length];
		int counter = 0;
		for (CoordinateStorage cst: allCoords) {
			coordinates[counter++] = cst.getCoords();
		}
		ArrayList <CellInfo> filteredResult = new ArrayList<CellInfo>();
		CellInfo [] result = getDataArray(cube, coordinates);		
		for (CellInfo r: result) {
			counter++;
			if (ignoreEmptyCells) {
				if (r == null || r.getValue().toString().equals("0.0") || r.getValue().toString().equals("")) {
					continue;
				}
			}
			try {
				Double.parseDouble(r.getValue().toString());
			} catch (NumberFormatException e) {
				continue;
			}
			filteredResult.add(r);
		}
		return filteredResult.toArray(new CellInfo[0]);
	}

	private final CellInfo [] collectCoordinates(CubeInfo cube, ExportContextInfo context) {
		int cellCounter = 0, totalCellCounter = 0;
		
		String [][] elementIds = context.getCellsArea();
		XMLAElementInfo [][] members;
		int [] elemPos;		
		DimensionInfo [] origDims = getDimensions(cube);
		boolean readWholeCube = false;
		if (elementIds == null || elementIds.length == 0) {
			readWholeCube = true;
		} else {
			boolean found = false;
			for (int i = 0; i < elementIds.length; i++) {
				if (elementIds[i].length != 0) {
					found = true;
					break;
				}
			}
			if (!found) {
				readWholeCube = true;
			}
		}
		if (readWholeCube) {					
			// Read whole cube...		
			members = new XMLAElementInfo[origDims.length][];
			elemPos = new int[origDims.length];
			for (int i = 0; i < origDims.length; i++) {
				elemPos[i] = 0;
				ElementLoader loader = getElementLoader(origDims[i]);
				String [] ids = loader.getAllElementIds();
				members[i] = new XMLAElementInfo[ids.length];
				int counter = 0;
				for (String id: ids) {
					members[i][counter++] = (XMLAElementInfo) loader.load(id);
				}
			}
		} else {
			if (elementIds.length != origDims.length) {
				System.err.println("Must specify at least one element per dimension.");
				return new CellInfo[0];
			}
			members = new XMLAElementInfo[origDims.length][];
			elemPos = new int[origDims.length];
			for (int i = 0; i < elementIds.length; i++) {
				elemPos[i] = 0;
				ElementLoader loader = getElementLoader(origDims[i]);
				members[i] = new XMLAElementInfo[elementIds[i].length];				
				int counter = 0;
				for (String id: elementIds[i]) {
					members[i][counter++] = (XMLAElementInfo) loader.load(id);
				}
			}
		}
		elemPos[origDims.length - 1] = -1;
		boolean finished = false;
		
		ArrayList <CellInfo> completeResult = new ArrayList<CellInfo>();
		int cp = origDims.length - 1;
		ArrayList <CoordinateStorage> allCoords = 
			new ArrayList<CoordinateStorage>();
		while (!finished) {
			boolean posFound = false;
			while (!posFound) {
				elemPos[cp]++;
				if (elemPos[cp] >= members[cp].length) {
					elemPos[cp] = 0;
					cp--;
					if (cp == -1) {
						finished = true;
						break;
					} 
				} else {
					boolean anotherRound = false;
					if (context.isBaseCellsOnly()) {
						while (members[cp][elemPos[cp]].getChildrenCount() != 0) {
							elemPos[cp]++;
							if (elemPos[cp] >= members[cp].length) {
								elemPos[cp] = 0;
								cp--;
								if (cp == -1) {
									finished = true;
									break;
								}
								anotherRound = true;
							}						
						}
					}
					if (!anotherRound && !finished) {
						cp = origDims.length - 1;
						posFound = true;
						for (int i = 0; i < origDims.length; i++) {
							if (members[i][elemPos[i]].getChildrenCount() != 0
									&& context.isBaseCellsOnly()) {
								posFound = false;
							}
						}
					}
				}				
			}
			if (!finished) {
				XMLAElementInfo [] els = new XMLAElementInfo[origDims.length];
				for (int i = 0; i < origDims.length; i++) {
					els[i] = members[i][elemPos[i]]; 
				}
				allCoords.add(new CoordinateStorage(els));
				cellCounter++;
				totalCellCounter++;
				if (cellCounter >= 10000) {
					completeResult.addAll(Arrays.asList(performCopy(
							allCoords, origDims, cube, context.ignoreEmptyCells())));
					allCoords.clear();
					cellCounter = 0;
				}				
			}
		}
		if (allCoords.size() > 0) {
			completeResult.addAll(Arrays.asList(performCopy(
					allCoords, origDims, cube, context.ignoreEmptyCells())));
		}
		return completeResult.toArray(new CellInfo[0]);
	}
	
	public CellInfo[] getDataExport(CubeInfo cube, ExportContextInfo context) {
		context.setProgress(1.0);
		return collectCoordinates(cube, context);
	}

	public DatabaseInfo [] getDatabases() {
		DatabaseLoader dl = getDatabaseLoader();
		String [] ids = dl.getAllDatabaseIds();
		DatabaseInfo [] databaseInfos = new DatabaseInfo[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			databaseInfos[i] = dl.load(ids[i]); 
		}
		return databaseInfos;
//		return BuilderRegistry.getInstance().
//					getDatabaseInfoBuilder().getDatabaseInfo(xmlaClient);
	}

	public DimensionInfo[] getDimensions(DatabaseInfo database) {
		DimensionLoader dl = getDimensionLoader(database);
		String [] ids = dl.getAllDimensionIds();
		DimensionInfo [] dimensionInfos = new DimensionInfo[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			dimensionInfos[i] = dl.load(ids[i]);
		}
		return dimensionInfos;
//		CubeInfo [] cubeInfo = getCubes(database);
//		ArrayList dimensions = new ArrayList();		
//		for (int i = 0, n = cubeInfo.length; i < n; i++) {
//			XMLADimensionInfo [] dims = BuilderRegistry.getInstance().getDimensionInfoBuilder().
//				getDimensionInfo(xmlaClient, (XMLADatabaseInfo) database,
//						(XMLACubeInfo) cubeInfo[i]);			
//			dimensions.addAll(Arrays.asList(dims));			
//		}
//		XMLADimensionInfo [] dims =
//			(XMLADimensionInfo []) dimensions.toArray(new XMLADimensionInfo[0]);
//		return dims;
	}
	
	public DimensionInfo [] getDimensions(CubeInfo cube) {
		DimensionLoader dl = getDimensionLoader(cube.getDatabase());
		String [] ids = ((XMLADimensionLoader) dl).getAllDimensionIdsForCube(cube);
		DimensionInfo [] dimensionInfos = new DimensionInfo[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			dimensionInfos[i] = dl.load(ids[i]);
		}
		return dimensionInfos;
	}
	
	public ElementInfo getElementAt(DimensionInfo dimension, int position) {
		return getElements(dimension)[position];
	}

	public ElementInfo getElementAt(HierarchyInfo hierarchy, int position) {
		return getElements(hierarchy)[position];
	}

	public ElementInfo[] getElements(DimensionInfo dimension) {		
		ElementLoader el = getElementLoader(dimension);
		String [] ids = el.getAllElementIds();
		ElementInfo [] elementInfos = new ElementInfo[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			elementInfos[i] = el.load(ids[i]);
		}
		return elementInfos;
		
		//		XMLAElementInfo [] elementInfo =
//			BuilderRegistry.getInstance().getElementInfoBuilder().
//			getElements(this, xmlaClient, ((XMLADimensionInfo) dimension).getCubeId(), (XMLADimensionInfo) dimension);
		/*BuilderRegistry.getInstance().getDimensionInfoBuilder().
			updateMaxLevelAndDepth(xmlaClient, (XMLADatabaseInfo) dimension.getDatabase(), 
					((XMLADimensionInfo) dimension).getCube());*/
//		return elementInfo;
	}

	public ElementInfo[] getElements(HierarchyInfo hierarchy) {		
		ElementLoader el = getElementLoader(hierarchy);
		String [] ids = el.getAllElementIds();
		ElementInfo [] elementInfos = new ElementInfo[ids.length];
		for (int i = 0, n = ids.length; i < n; i++) {
			elementInfos[i] = el.load(ids[i]);
		}
		return elementInfos;
	}

	public XMLAElementInfo [] getCubeElements(XMLACubeInfo cube, XMLADimensionInfo dimension) {
		ElementLoader el = getElementLoader(dimension.getDefaultHierarchy());
		String [] ids = el.getAllElementIds();
		ArrayList <XMLAElementInfo> cubeElements = 
			new ArrayList<XMLAElementInfo>();
		for (int i = 0, n = ids.length; i < n; i++) {
			XMLAElementInfo elInfo = (XMLAElementInfo) el.load(ids[i]);
			if (((XMLADimensionInfo) elInfo.getDimension()).getCubeId().equals(cube.getId())) {
				cubeElements.add(elInfo);
			}
		}
		return cubeElements.toArray(new XMLAElementInfo[0]);
		
//		XMLAElementInfo [] elementInfo =
//			BuilderRegistry.getInstance().getElementInfoBuilder().
//			getElements(this, xmlaClient, cube.getId(), dimension);
//		return elementInfo;
	}
	
	public ConnectionInfo getInfo() {
		return connectionInfo;
	}

	public CubeInfo[] getNormalCubes(DatabaseInfo database) {
		// All cubes are normal cubes for XMLA, hence:
		return getCubes(database);
	}

	public DatabaseInfo[] getNormalDatabases() {
		// All databases are normal databases for XMLA:
		return getDatabases();
	}

	public DimensionInfo[] getNormalDimensions(DatabaseInfo database) {
		return getDimensions(database);
	}

	public String getRule(CubeInfo cube, ElementInfo[] coordinate) {
		return BuilderRegistry.getInstance().
			getRuleInfoBuilder().getRule((XMLACubeInfo) cube, coordinate);
	}

	public RuleInfo[] getRules(CubeInfo cube) {
		return BuilderRegistry.getInstance().
				getRuleInfoBuilder().getRules(this, xmlaClient, (XMLACubeInfo) cube);
		
		/*System.out.println("Getting rules for " + cube.getName());
		if (!ruleInfoMap.containsKey(cube.getId())) {			 		
			ArrayList ruleInfos = new ArrayList();
			DimensionInfo [] dimInfo = getDimensions(cube.getDatabase());
			String [] dimIds = cube.getDimensions();
			List dimensions = new ArrayList();
			for (int i = 0, n = dimIds.length; i < n; i++) {
				for (int j = 0, m = dimInfo.length; j < m; j++) {
					if (dimInfo[j].getId().equals(dimIds[i])) {
						dimensions.add(dimInfo[j]);
						break;
					}
				}
			}
			for (int i = 0, n = dimensions.size(); i < n; i++) {
				ElementInfo [] elInfo = 
					getElements((DimensionInfo) dimensions.get(i));
				for (int j = 0, m = elInfo.length; j < m; j++) {
					XMLAElementInfo info = (XMLAElementInfo) elInfo[j];
					if (info.isCalculated()) {
						ruleInfos.add(info.getRule());
					}
				}
			}
			ruleInfoMap.put(cube.getId(), ruleInfos);
		}
		RuleInfoImpl [] rules = (RuleInfoImpl []) ((ArrayList) ruleInfoMap.get(
				cube.getId())).toArray(new RuleInfoImpl[0]); 
		System.out.println("All rules for " + cube.getName());
		for (int i = 0; i < rules.length; i++) {
			System.out.println("  " + rules[i].getId());
		}
		return rules;*/
	}

	public ServerInfo getServerInfo() {
		// TODO Return only the first connection? Or all of them?
		ServerInfo [] cons = xmlaClient.getConnections(); 
		if (cons == null || cons.length < 1) {
			throw new PaloException("Could not login to xmla server '"
					+ xmlaClient.getServer() + "' as user '" +
					xmlaClient.getUsername() + "'!!");
		}
		return cons[0];
	}

	public CubeInfo[] getSystemCubes(DatabaseInfo database) {
		return new XMLACubeInfo[0];
	}

	public DatabaseInfo[] getSystemDatabases() {
		return new XMLADatabaseInfo[0];
	}

	public boolean isConnected() {
		return connected;
	}

	public String listFunctions() {
		return BuilderRegistry.getInstance().
				getRuleInfoBuilder().getFunctions(xmlaClient);
	}

	public String listFunctionNames() {
		return BuilderRegistry.getInstance().
			getRuleInfoBuilder().getFunctionNames(xmlaClient);		
	}
	
	public void load(CubeInfo cube) {
	}

	public void load(DatabaseInfo database) {
	}

	public boolean login(String user, String password) {
		// Username and password are implicitly needed when doing a query for
		// the database. Then, username and password are transmitted via the
		// HTTP connection. So, no "login" exists.
		return true;
	}

	public void move(ElementInfo element, int newPosition) {
		throw new PaloException("XMLAConnections cannot move elements.");
	}

	public String parseRule(CubeInfo cube, String ruleDefinition) {
		return parseRule(cube, ruleDefinition, null);
	}
	
	public String parseRule(CubeInfo cube, String ruleDefinition, String functions) {		
		RuleInfo [] rules = getRules(cube);
		RuleInfo rule = null;
		for (int i = 0, n = rules.length; i < n; i++) {
			if (rules[i].getDefinition().equals(ruleDefinition)) {
				rule = rules[i];
				break;
			}
		}
		if (rule == null) {
			return "";
		}
		DimensionInfo [] dimensions = getDimensions(cube);
		StringBuffer definition = new StringBuffer("[");		
		for (int i = 0; i < dimensions.length; i++) {
			definition.append("'" + dimensions[i].getId().replaceAll("'", "''") + "':''");
			if (i < (dimensions.length - 1)) {
				definition.append(",");
			}
		}
		String cleanDef = ruleDefinition.replaceAll("\\{", "").replaceAll("\\}", "");
		cleanDef = cleanDef.replaceAll("\\[", "\"[").replaceAll("\\]", "]\"");
		cleanDef = cleanDef.replaceAll("\"\\.\"", "\\.");		
		definition.append("]=" + cleanDef);
		return definition.toString();
	}

	public RuleInfo createRule(CubeInfo cube, String definition,
			String externalIdentifier, boolean useIt, String comment,
			boolean activate) {
		throw new PaloException("Cannot create rules in XMLA.");
	}

	public void update(RuleInfo rule, String definition,
			String externalIdentifier, boolean useIt, String comment,
			boolean activate) {
		throw new PaloException("Cannot update rules in XMLA.");
	}

	public void ping() throws PaloException {
	}

	public void reload(CubeInfo cube) {
	}

	public void reload(DatabaseInfo database) {
	}

	public void reload(DimensionInfo dimension) {
	}

	public void reload(ElementInfo element) {
	}

	public void addServerListener(ServerListener listener) {
	}

	public void removeServerListener(ServerListener listener) {
	}	

	public void rename(ElementInfo element, String newName) {
		throw new PaloException("XMLAConnections cannot rename elements.");
	}

	public void rename(DimensionInfo dimension, String newName) {
		throw new PaloException("XMLAConnections cannot rename dimensions.");
	}

	public void rename(DatabaseInfo database, String newName) {
		throw new PaloException("XMLAConnections cannot rename databases.");
	}

	public boolean save(DatabaseInfo database) {
		throw new PaloException("XMLAConnections cannot save databases.");
	}

	public boolean save(ServerInfo server) {
		throw new PaloException("XMLAConnections cannot save connections.");
	}

	public boolean save(CubeInfo cube) {
		throw new PaloException("XMLAConnections cannot save cubes.");
	}

	public void setDataArray(CubeInfo cube, ElementInfo[][] coordinates,
			Object[] values, boolean add, int splashMode,
			boolean notifyEventProcessors) {
		throw new PaloException("XMLAConnections cannot write data.");
	}

	public void setDataNumericSplashed(CubeInfo cube, ElementInfo[] coordinate,
			double value, int splashMode) {
		throw new PaloException("XMLAConnections cannot write data.");
	}

	public void setDataString(CubeInfo cube, ElementInfo[] coordinate,
			String value) {
		throw new PaloException("XMLAConnections cannot write data.");
	}

	public void unload(CubeInfo cube) {
	}

	public void update(ElementInfo element, int type, String[] childrenIds,
			double[] weights, ServerInfo serverInfo) {
	}

	public synchronized void disconnect() {
		xmlaClient.disconnect();
		connected = false;
	}
	
	public final void rename(CubeInfo cube, String newName) {
		throw new PaloException("XMLAConnections cannot rename cubes.");
	}

//	public HierarchyInfo [] getHierarchies(DatabaseInfo database) {
//		CubeInfo [] cubeInfo = getCubes(database);
//		ArrayList hierarchies = new ArrayList();		
//		for (int i = 0, n = cubeInfo.length; i < n; i++) {
//			XMLAHierarchyInfo [] hiers = BuilderRegistry.getInstance().getDimensionInfoBuilder().
//				getHierarchyInfo(xmlaClient, (XMLADatabaseInfo) database,
//						(XMLACubeInfo) cubeInfo[i]);			
//			hierarchies.addAll(Arrays.asList(hiers));			
//		}
//		XMLAHierarchyInfo [] hiers =
//			(XMLAHierarchyInfo []) hierarchies.toArray(new XMLAHierarchyInfo[0]);
//		return hiers;
//	}
//	
//	public HierarchyInfo [] getHierarchies(CubeInfo cube) {
//		return BuilderRegistry.getInstance().getDimensionInfoBuilder().
//			getHierarchyInfo(xmlaClient, (XMLADatabaseInfo) cube.getDatabase(),
//					(XMLACubeInfo) cube);
//	}
//	
//	public DimensionInfo [] getDimensions(HierarchyInfo hierarchy) {
//		return ((XMLAHierarchyInfo) hierarchy).getDimensions();
//	}
	
//	public VariableInfo [] getVariables(CubeInfo cube) {
//		return ((XMLACubeInfo) cube).getVariables();
//	}

	public CubeInfo getCube(DatabaseInfo database, String id) {
		return getCubeLoader(database).loadByName(id);
	}

	public CubeLoader getCubeLoader(DatabaseInfo database) {
		XMLACubeLoader cl;
		if ((cl = cubeLoaders.get(database)) == null) {
			cl = new XMLACubeLoader(this, xmlaClient, database, this);
			cubeLoaders.put((XMLADatabaseInfo) database, cl);
		}
		return cl;
	}

	public DatabaseInfo getDatabase(String id) {
		return getDatabaseLoader().loadByName(id);
	}

	public DatabaseLoader getDatabaseLoader() {
		if (databaseLoader == null) {
			databaseLoader = new XMLADatabaseLoader(this, xmlaClient);
		}
		return databaseLoader;
	}

	public DimensionInfo getDimension(DatabaseInfo database, String id) {		
		// At this point, it is ensured that the dimension object has
		// _not_ yet been loaded, so it is ok to issue a request.
		String cubeId = XMLADimensionInfo.getCubeNameFromId(id);
		XMLACubeInfo cubeInfo = (XMLACubeInfo) getCube(database, cubeId);
		XMLADimensionRequestor req = new XMLADimensionRequestor(cubeInfo, this);
		req.setCatalogNameRestriction(database.getId());
		req.setCubeNameRestriction(cubeInfo.getId());
		req.setDimensionUniqueNameRestriction(XMLADimensionInfo.getDimIdFromId(id));
		XMLADimensionInfo [] result = req.requestDimensions(xmlaClient);
		if (result == null || result.length < 1) {
			return null;
		}
		return result[0];
//		DimensionInfo dimInfo =
//			BuilderRegistry.getInstance().getDimensionInfoBuilder().
//			       getDimensionInfo(xmlaClient, (XMLADatabaseInfo) database, id, this);
//		if (dimInfo != null) {
//			getDimensionLoader(database).loaded(dimInfo);
//		}
//		return dimInfo;
	}

	public DimensionLoader getDimensionLoader(DatabaseInfo database) {
		XMLADimensionLoader dl;
		if ((dl = dimensionLoaders.get(database)) == null) {
			dl = new XMLADimensionLoader(this, xmlaClient, database);
			dimensionLoaders.put((XMLADatabaseInfo) database, dl);
		}
		return dl;
	}

	public ElementInfo getElement(DimensionInfo dimension, String id) {
		return getElementLoader(dimension).loadByName(id);
	}

	public ElementInfo getElement(HierarchyInfo hierarchy, String id) {
		return getElementLoader(hierarchy).loadByName(id);
	}

	public ElementLoader getElementLoader(DimensionInfo dimension) {
		XMLAElementLoader el;
		if ((el = elementLoaders.get(dimension)) == null) {
			el = new XMLAElementLoader(this, xmlaClient, dimension);
			elementLoaders.put((XMLADimensionInfo) dimension, el);
		}
		return el;
	}
	
	public ElementLoader getElementLoader(HierarchyInfo hierarchy) {
		XMLAElementLoader el;
		if ((el = hElementLoaders.get(hierarchy)) == null) {
			el = new XMLAElementLoader(this, xmlaClient, hierarchy);
			hElementLoaders.put((XMLAHierarchyInfo) hierarchy, el);
		}
		return el;
	}

	public FunctionLoader getFunctionLoader() {
		if (functionLoader == null) {
			functionLoader = new XMLAFunctionLoader(this);
		}
		return functionLoader;
	}

//	public HierarchyInfo getHierarchy(DatabaseInfo database, String id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	public HierarchyInfo getHierarchy(CubeInfo cube, String id) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public HierarchyLoader getHierarchyLoader(DimensionInfo dimension) {
		XMLAHierarchyLoader hl;
		if ((hl = hierarchyLoaders.get(dimension)) == null) {
			hl = new XMLAHierarchyLoader(this, dimension);
			hierarchyLoaders.put((XMLADimensionInfo) dimension, hl);
		}
		return hl;
	}
	
	public RuleInfo getRule(CubeInfo cube, String id) {		
		RuleInfo [] rules = loadedRules.get(cube); 
		if (rules == null) {
			rules = BuilderRegistry.getInstance().getRuleInfoBuilder().
				getRules(this, xmlaClient, (XMLACubeInfo) cube);
			loadedRules.put((XMLACubeInfo) cube, rules);
		}
		if (rules != null) {
			for (RuleInfo rule: rules) {
				if (rule.getId().equals(id)) {
					return rule;
				}
			}
		}
		return null;
	}

	public RuleLoader getRuleLoader(CubeInfo cube) {		
		XMLARuleLoader rl;
		if ((rl = ruleLoaders.get(cube)) == null) {
			rl = new XMLARuleLoader(this, xmlaClient, cube);
			ruleLoaders.put((XMLACubeInfo) cube, rl);
		}
		return rl;
	}

	public PropertyLoader getPropertyLoader() {
		if (propertyLoader == null) {
			propertyLoader = new XMLAPropertyLoader(this);
		}
		return propertyLoader;
	}
	
	public PropertyLoader getTypedPropertyLoader(PaloInfo infoObject) {
		XMLAPropertyLoader pl;
		if ((pl = propertyLoaders.get(infoObject)) == null) {
			pl = new XMLAPropertyLoader(this, infoObject);
			propertyLoaders.put(infoObject, pl);
		}
		return pl;		
	}
	
	public PropertyInfo getProperty(String id) {		
		if (id.equals(PROPERTY_SAP_VARIABLES)) {
			boolean isSap = xmlaClient.isSAP((XMLAServerInfo) getServerInfo());
			PropertyInfo info = new PropertyInfoImpl(
					id, Boolean.toString(isSap), null,
					PropertyInfoImpl.TYPE_BOOLEAN, true);
			return info;
		}
		return null;
	}
	
	public boolean supportsRules() {
		return true;
	}
	
	public String [] getAllKnownPropertyIds() {
		return new String [] {PROPERTY_SAP_VARIABLES};
	}

	public PropertyInfo createNewProperty(String id, String value,
			PropertyInfo parent, int type, boolean readOnly) {
		return new PropertyInfoImpl(id, value, parent, type, readOnly);
	}

	public void clear(CubeInfo cube) {
		throw new PaloException("Not supported by xmla server");
	}

	public void clear(CubeInfo cube, ElementInfo[][] area) {
		throw new PaloException("Not supported by xmla server");
	}

	public DimensionInfo addDimension(DatabaseInfo database, String name,
			int type) {
		throw new PaloException("XMLAConnections cannot add dimensions.");
	}

	public CubeInfo[] getUserInfoCubes(DatabaseInfo database) {
		return new XMLACubeInfo[0];	
	}

	public DimensionInfo[] getUserInfoDimensions(DatabaseInfo database) {
		return new XMLADimensionInfo[0];
	}

	public CubeInfo addCube(DatabaseInfo database, String name,
			DimensionInfo[] dimensions, int type) {
		throw new PaloException("XMLAConnections cannot add cubes.");
	}

	public CubeInfo[] getCubes(DatabaseInfo database, int typeMask) {
		return getCubes(database);
	}

	public DimensionInfo[] getDimensions(DatabaseInfo database, int typeMask) {
		return getDimensions(database);
	}
	
	public final boolean usedByWPalo() {
		Object isWPalo = connectionInfo.getData("com.tensegrity.palo.wpalo");
		if(isWPalo != null && isWPalo instanceof Boolean)
			return ((Boolean)isWPalo).booleanValue();
		return false;
	}

	public final LockInfo[] getLocks(CubeInfo cube) {
		throw new PaloException("Not supported by xmla server");
	}
	public final LockInfo requestLock(CubeInfo cube, ElementInfo[][] area) {
		throw new PaloException("Not supported by xmla server");	
	}
	public final boolean rollback(CubeInfo cube, LockInfo lock, int steps) {
		throw new PaloException("Not supported by xmla server");
	}
	public final boolean commit(CubeInfo cube, LockInfo lock) {
		throw new PaloException("Not supported by xmla server");
	}

	public HierarchyInfo[] getHierarchies(DimensionInfo dimension) {
		XMLAHierarchyRequestor req =
			new XMLAHierarchyRequestor((XMLADimensionInfo) dimension,
					(XMLADatabaseInfo) dimension.getDatabase(), this);
		req.setCubeNameRestriction(((XMLADimensionInfo) dimension).getCubeId());
		req.setCatalogNameRestriction(((XMLADimensionInfo) dimension).getDatabase().getId());
		req.setDimensionUniqueNameRestriction(
				((XMLADimensionInfo) dimension).getDimensionUniqueName());
		return req.requestHierarchies(xmlaClient);
	}

	public HierarchyInfo getHierarchy(DimensionInfo dimension, String id) {
		XMLAHierarchyRequestor req =
			new XMLAHierarchyRequestor((XMLADimensionInfo) dimension,
					(XMLADatabaseInfo) dimension.getDatabase(), this);
		req.setCubeNameRestriction(((XMLADimensionInfo) dimension).getCubeId());
		req.setCatalogNameRestriction(((XMLADimensionInfo) dimension).getDatabase().getId());
		req.setDimensionUniqueNameRestriction(
				((XMLADimensionInfo) dimension).getDimensionUniqueName());
//		req.setHierarchyUniqueNameRestriction(XMLADimensionInfo.transformId(
//				XMLADimensionInfo.getDimIdFromId(id)));
		XMLAHierarchyInfo [] hiers = req.requestHierarchies(xmlaClient);
		if (hiers != null && hiers.length > 0) {
			for (XMLAHierarchyInfo hier: hiers) {
				if (hier != null) {
					if (hier.getId().equals(id)) {
						return hier;
					}	
				}
			}
		}
		return null;
	}
	
	public void testElementMDX(String databaseId, String dimensionId, String hierarchyId) {
		/*BuilderRegistry.getInstance().getElementInfoBuilder().
			getElementsTest(this, xmlaClient, databaseId, dimensionId, hierarchyId);*/	
	}

	public boolean addElements(DimensionInfo dimension, String[] names,
			int type, ElementInfo[][] children, double[][] weights) {
		throw new PaloException("XMLAConnections cannot add elements.");
	}

	public boolean delete(ElementInfo[] elements) {
		throw new PaloException("XMLAConnections cannot delete elements.");
	}

	public boolean addElements(DimensionInfo dimension, String[] names,
			int[] types, ElementInfo[][] children, double[][] weights) {
		throw new PaloException("XMLAConnections cannot add elements.");
	}

	public boolean replaceBulk(DimensionInfo dimInfo, ElementInfo[] elements,
			int type, ElementInfo[][] children, Double[][] weights) {
		throw new PaloException("XMLAConnections cannot update consolidations.");
	}

	public int convert(CubeInfo cube, int type) {
		throw new PaloException("XMLAConnections cannot convert cubes.");
	}
}
