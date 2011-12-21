/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.childloader;

import org.palo.api.Connection;
import org.palo.api.ConnectionConfiguration;
import org.palo.api.ConnectionFactory;
import org.palo.api.Cube;
import org.palo.api.Database;
import org.palo.api.Dimension;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.folders.XFolder;
import com.tensegrity.palo.gwt.core.client.models.palo.XCube;
import com.tensegrity.palo.gwt.core.client.models.palo.XDatabase;
import com.tensegrity.palo.gwt.core.client.models.palo.XDimension;
import com.tensegrity.palo.gwt.core.client.models.palo.XServer;
import com.tensegrity.palo.gwt.core.server.converter.XConverter;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

public class ModellerChildLoader implements ChildLoader {

	public static String TYPE_STATIC_FOLDER_CUBES = "cubes";
	public static String TYPE_STATIC_FOLDER_DIMENSIONS = "dimensions";
	public static String TYPE_STATIC_FOLDER_SYSTEMDIMENSIONS = "sysdims";
	public static String TYPE_STATIC_FOLDER_ATTRIBUTEDIMENSIONS = "attdims";
	public static String TYPE_STATIC_FOLDER_SYSTEMCUBES = "syscubes";
	public static String TYPE_STATIC_FOLDER_ATTRIBUTECUBES = "attcubes";
	
	public int idCounter = 10000;			// temporary, must be replaced by id path
	
	
	public boolean accepts(XObject parent) {
		return  parent instanceof XServer ||
				parent instanceof XDatabase ||
				parent instanceof XCube ||
				parent instanceof XFolder ||
				parent instanceof XUser;
	}

	public XObject[] loadChildren(XObject parent, UserSession userSession) {

		XObject [] ret = null;

//		AuthUser user =
//			(AuthUser) XObjectMatcher.getNativeObject(((XModellerObject)parent).getUser());
//
//		if (parent instanceof XRoot) {
//
//			int paloAccounts = 0;
//			
//			for (Account a: user.getAccounts())
//			{
//				if (a instanceof PaloAccount) {
//					paloAccounts++;
//				}
//			}			
//			
//			ret = new XServer[paloAccounts];
//
//			for (int i = 0, n = user.getAccounts().size(); i < n; i++)
//			{
//				Account a = user.getAccounts().get(i); 
//				if (a instanceof PaloAccount) {
//					Connection conn = ((PaloAccount)a).login();
//					XServer server = new XServer();
//					server.setName(conn.getServer());
//					setIdToXObject(server);
//					server.setHasChildren(true);
//					ret[i] = server;
//				}
//			}
//		}
//		else if (parent instanceof XServer) {
//			
//			Connection connection = getConnection();
//
//			ret = new XDatabase[connection.getDatabaseCount()];
//
//			for (int i = 0; i < connection.getDatabaseCount(); i++) 
//			{
//				Database db = connection.getDatabaseAt(i);
//				XDatabase xdb = new XDatabase();
//				xdb.setName(db.getName());
//				setIdToXObject(xdb);
//				xdb.setDbId(db.getId());
//				xdb.setHasChildren(true);
//				ret[i] = xdb;
//			}
//
//			connection.disconnect();
//		}
//		else if (parent instanceof XDatabase) {
//			XDatabase xdb = (XDatabase)parent;
//			ret = new XFolder[2];
//
//			ret[0] = addStaticFolder(parent, "Dimensions", xdb.getDbId(), TYPE_STATIC_FOLDER_DIMENSIONS);
//			ret[1] = addStaticFolder(parent, "Cubes", xdb.getId(), TYPE_STATIC_FOLDER_CUBES);
//		}
//		else if (parent instanceof XFolder) {
//			XFolder folder = (XFolder)parent;
//			
//			Connection connection = getConnection();
//			Database db = connection.getDatabaseById(folder.getDbId());
//
//			if (folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_CUBES) ||
//					folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMCUBES) || 
//					folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTECUBES)) {
//				ret = addCubes(folder, db);
//			} else if (folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_DIMENSIONS) || 
//					folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMDIMENSIONS) || 
//					folder.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTEDIMENSIONS)) {
//				ret = addDimensions(folder, db);
//			}
//			
//			connection.disconnect();
//		}
		
		return ret;
	}

	private Connection getConnection()
	{
		ConnectionConfiguration config = ConnectionFactory.getInstance().getConfiguration("localhost", 
				"7777", "admin", "admin");
		config.setLoadOnDemand(true);
		return ConnectionFactory.getInstance().newConnection(config);
	}

	private XFolder addStaticFolder(XObject parent, String label, String dbId, String folderType)
	{
		XFolder folder = new XFolder();
		folder.setName(label);
		setIdToXObject(folder);
		folder.setFolderType(folderType);
		folder.setHasChildren(true);
//		folder.setDbId(dbId);
		idCounter++;
		
		return folder;
	}

	private XObject[] addCubes(XFolder parent, Database db)
	{
		int allCubeCnt = db.getCubeCount();
		int cubeCnt = 0;
		XObject	ret[];

		for (int i = 0; i < allCubeCnt; i++) {
			Cube cube = db.getCubeAt(i);
			if ((parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_CUBES) && isDataCube(cube)) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMCUBES) && cube.isSystemCube()) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTECUBES) && cube.isAttributeCube())) {
				cubeCnt++;
			}
		}
		
		if (parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_CUBES)) {
			cubeCnt += 2;							// for additional subfolders
		}
			
		ret = new XObject[cubeCnt];
		int curCube = 0;
	
		for (int i = 0; i < allCubeCnt; i++) 
		{
			Cube cube = db.getCubeAt(i);
			if ((parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_CUBES) && isDataCube(cube)) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMCUBES) && cube.isSystemCube()) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTECUBES) && cube.isAttributeCube())) {
				ret[curCube] = new XCube();
				ret[curCube].setName(cube.getName());
				setIdToXObject(ret[curCube]);
//				ret[curCube].setId(cube.getId());
				curCube++;
			}
		}

		if (parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_CUBES)) {
			ret[curCube] = addStaticFolder(parent, "Attribute Cubes", db.getId(), TYPE_STATIC_FOLDER_ATTRIBUTECUBES);
			ret[curCube + 1] = addStaticFolder(parent, "System Cubes", db.getId(), TYPE_STATIC_FOLDER_SYSTEMCUBES);
		}
		
		return ret;
	}

	private boolean isDataCube(Cube cube)
	{
		return (!cube.isSystemCube() && !cube.isAttributeCube() && !cube.isUserInfoCube());
	}
	
	private boolean isDataDimension(Dimension dim)
	{
		return (!dim.isSystemDimension() && !dim.isAttributeDimension() && !dim.isUserInfoDimension());
	}
	
	private XObject[] addDimensions(XFolder parent, Database db)
	{
		int allDimCnt = db.getDimensionCount();
		int dimCnt = 0;
		XObject	ret[];

		for (int i = 0; i < allDimCnt; i++) {
			Dimension dim = db.getDimensionAt(i);
			if ((parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_DIMENSIONS) && isDataDimension(dim)) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMDIMENSIONS) && dim.isSystemDimension()) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTEDIMENSIONS) && dim.isAttributeDimension())) {
				dimCnt++;
			}
		}
		
		if (parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_DIMENSIONS)) {
			dimCnt += 2;							// for additional subfolders
		}
			
		ret = new XObject[dimCnt];
		int curDim = 0;
	
		for (int i = 0; i < allDimCnt; i++) 
		{
			Dimension dim = db.getDimensionAt(i);
			if ((parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_DIMENSIONS) && isDataDimension(dim)) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_SYSTEMDIMENSIONS) && dim.isSystemDimension()) ||
				(parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_ATTRIBUTEDIMENSIONS) && dim.isAttributeDimension())) {
				XDimension xdim = (XDimension) XConverter.createX(dim);
//				xdim.setName(dim.getName());
//				xdim.setDbId(db.getId());
//				xdim.setDimId(dim.getId());
				setIdToXObject(xdim);
				ret[curDim] = xdim;
				curDim++;
			}
		}

		if (parent.getFolderType().equalsIgnoreCase(TYPE_STATIC_FOLDER_DIMENSIONS)) {
			ret[curDim] = addStaticFolder(parent, "Attribute Dimension", db.getId(), TYPE_STATIC_FOLDER_ATTRIBUTEDIMENSIONS);
			ret[curDim + 1] = addStaticFolder(parent, "System Dimension", db.getId(), TYPE_STATIC_FOLDER_SYSTEMDIMENSIONS);
		}
		
		return ret;
	}

	private void setIdToXObject(XObject obj) {
		obj.setId("" + idCounter);
		idCounter++;
	}

}
