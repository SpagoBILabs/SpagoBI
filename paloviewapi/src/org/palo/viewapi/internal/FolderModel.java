/*
*
* @file FolderModel.java
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
* @author Philipp Bouillon
*
* @version $Id: FolderModel.java,v 1.8 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.palo.api.PaloAPIException;
import org.palo.api.exceptions.PaloIOException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Role;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.FolderService;
import org.palo.viewapi.services.ServiceProvider;

/**
 * The <code>FolderModel</code> class is responsible for loading and saving
 * of the folder structure.
 * 
 * @author Philipp Bouillon
 */
public class FolderModel {
	/**
	 * The shared instance.
	 */
	private static final FolderModel instance = new FolderModel();
	
	private static final int FOLDER_DESC_IDX = 2;
	
	/**
	 * Returns the shared instance of the FolderModel.
	 * @return the shared instance of the FolderModel.
	 */
	public static FolderModel getInstance() {
		return instance;
	}
	
	/**
	 * The private constructor prevents access from the outside.
	 */
	private FolderModel() {		
	}
	
	/**
	 * Saves the folder structure specified for the given user.
	 * 
	 * @param user the user for which the folder structure is saved.
	 * @param rootFolder the root folder of the structure.
	 * @throws PaloIOException if the structure could not be saved.
	 */
	public synchronized void save(AuthUser user, ExplorerTreeNode rootFolder) 
	throws PaloIOException {		
		createTableIfNotExists();
		long time = System.currentTimeMillis();
		String xmlDef = null;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				FolderWriter.getInstance().toXML(bout, rootFolder);
				xmlDef = bout.toString("UTF-8");
			} finally {
				bout.close();
			}
			if (xmlDef == null) {
				throw new PaloIOException("Could not store folder '"
						+ rootFolder.getName() + "' !!");				
			}
			writeFolder(user, xmlDef);
		} catch (PaloIOException pex) {
			throw new PaloIOException("Could not store folder '"
					+ rootFolder.getName() + "'!!", pex);
		} catch (PaloAPIException pex) {
			throw new PaloIOException("Could not store folder '"
					+ rootFolder.getName() + "'!!", pex);
		} catch (IOException ioe) {
			throw new PaloIOException("Could not store folder '"
					+ rootFolder.getName() + "'!!", ioe);
		}		
	}
	
	/**
	 * Loads the folder structure for the specified user.
	 * 
	 * @param user the user loading the structure.
	 * @return the root of the user's folder structure.
	 * @throws PaloIOException if loading the folders fails.
	 */
	public synchronized ExplorerTreeNode load(AuthUser user) 
		throws PaloIOException {
		ExplorerTreeNode root = null;
		MapperRegistry.getInstance().getFolderManagement().setUser(user);
		String xmlDef = loadFolder(user);
		if (xmlDef == null) {
			return null;
//			root = new StaticFolder(null, user.getLoginName());
//			FolderModel.getInstance().save(user, root);		
//			return root;
		}
		return load(user, xmlDef);
	}
		
	ExplorerTreeNode loadPure(AuthUser user) throws PaloIOException {
		ExplorerTreeNode root = null;
		String xmlDef = loadFolder(user);
		if (xmlDef == null) {
			return null;
//			root = new StaticFolder(null, user.getLoginName());
//			FolderModel.getInstance().save(user, root);		
//			return root;
		}
		return loadPure(user, xmlDef);		
	}
	
	ExplorerTreeNode load(AuthUser user, String xmlDef) throws PaloIOException {
		ExplorerTreeNode root = null;
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(xmlDef
					.getBytes("UTF-8")); //$NON-NLS-1$
			try {
				root = FolderReader.getInstance().fromXML(user, bin);
				FolderService fs = ServiceProvider.getFolderService(user);
				assignRights(root, fs);
			} finally {
				bin.close();
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			throw new PaloIOException("failed to load folders for user '"
					+ user + "'", e);
		} catch (PaloIOException e) {
			e.printStackTrace();
			throw new PaloIOException("failed to load folders for user '"
					+ user + "'", e);
		}		
	}
	
	ExplorerTreeNode loadPure(AuthUser user, String xmlDef) throws PaloIOException {
		ExplorerTreeNode root = null;
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(xmlDef
					.getBytes("UTF-8")); //$NON-NLS-1$
			try {
				root = FolderReader.getInstance().fromXML(user, bin);
			} finally {
				bin.close();
			}
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			throw new PaloIOException("failed to load folders for user '"
					+ user + "'", e);
		} catch (PaloIOException e) {
			e.printStackTrace();
			throw new PaloIOException("failed to load folders for user '"
					+ user + "'", e);
		}		
	}
	
	private final void assignRights(ExplorerTreeNode root, FolderService fs) {
		if (root == null) {
			return;
		}
		ExplorerTreeNode etn = fs.getTreeNode(root.getId());
		if (etn == null) {			
			System.err.println("Did not find " + root.getId());
		} else {
			for (Role role: etn.getRoles()) {
				((AbstractExplorerTreeNode) root).add(role);
			}
			((AbstractExplorerTreeNode) root).setOwner(etn.getOwner());			
			((AbstractExplorerTreeNode) root).setConnectionId(etn.getConnectionId());
		}
		for (ExplorerTreeNode kid: root.getChildren()) {
			assignRights(kid, fs);
		}
	}
	
	/**
	 * Issues the sql command that actually writes the folder structure to
	 * the database.
	 * 
	 * @param user the user for which to write the folder data.
	 * @param xmlDef the folder data in xml format.
	 * @return true if writing succeeded, false otherwise.
	 */
	private final boolean writeFolder(AuthUser user, String xmlDef) {
		Connection conn = DbService.getConnection();
		if (!executeTest(DbService.getQuery("Folder.tableExists"), conn)) {
			if (update(DbService.getQuery("Folder.createTable"), conn) == 0) {
				return false;
			}
		}
		int rowCount;
		if ((rowCount = update(DbService.getQuery("Folder.update",
				user.getId(), xmlDef), conn)) == 0) {
			rowCount = update(DbService.getQuery("Folder.insert", user.getId(),
					xmlDef), conn);
		}
		return rowCount == 1;
	}
	
	private final void createTableIfNotExists() {
		if (!executeTest(DbService.getQuery("Folder.tableExists"),
				DbService.getConnection())) {
			update(DbService.getQuery("Folder.createTable"), DbService.getConnection());
		}		
	}
	
	/**
	 * Issues the SQL command that actually loads the xml definition of the
	 * folders for the specified user.
	 * 
	 * @param user the user for which the folder structure is being loaded.
	 * @return the xml definition stored for the user or an empty string if
	 * no definition exists.
	 */
	private final String loadFolder(AuthUser user) {
		createTableIfNotExists();
		ResultSet result = query(
				DbService.getQuery("Folder.load", user.getId()), 
				DbService.getConnection());
		try {
			if (result != null && result.next()) {
				return result.getString(FOLDER_DESC_IDX);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private final ResultSet query(String query, Connection connection) {
		if (connection == null) {
			return null;
		}
		Statement statement = null;
		ResultSet results = null;
		try {
			statement = connection.createStatement();
			results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} 	
	}
		
	private final int update(String query, Connection connection) {
		if (connection == null) {
			return 0;
		}
		Statement statement = null;
		int rowCount = 0;
		try {
			statement = connection.createStatement();
			rowCount = statement.executeUpdate(query);
			return rowCount;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
		}
	}
	
	private final boolean executeTest(String command, Connection connection) {
		if (connection == null) {
			return false;
		}
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeQuery(command);
		} catch (SQLException e) {
			return false;
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
		}
		return true;						
	}	

}
