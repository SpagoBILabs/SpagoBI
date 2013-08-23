/*
*
* @file DynamicFolderHandler.java
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
* @version $Id: DynamicFolderHandler.java,v 1.17 2010/02/12 13:51:05 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import java.util.HashMap;

import org.palo.api.Connection;
import org.palo.api.ConnectionConfiguration;
import org.palo.api.ConnectionFactory;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Account;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.PaloConnection;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.DynamicFolder;
import org.palo.viewapi.services.AdministrationService;
import org.palo.viewapi.services.ServiceProvider;
import org.xml.sax.Attributes;

/**
 * <code>DynamicFolderHandler</code> Reads a dynamic folder from the database.
 * 
 * @author Philipp Bouillon
 * @version $Id: DynamicFolderHandler.java,v 1.7 2008/08/18 09:42:02
 *          PhilippBouillon Exp $
 */
public class DynamicFolderHandler implements IXMLHandler {
	public static final String XPATH = "/folder/dynamicFolder";
	public static final HashMap <Account, Connection> conMap = new
		HashMap<Account, Connection>();
	private FolderXMLHandler xmlHandler;

	public DynamicFolderHandler(FolderXMLHandler xmlHandler) {
		this.xmlHandler = xmlHandler;
	}

	public void enter(String path, Attributes attributes) {
		if (path.startsWith("/folder/") && path.endsWith("dynamicFolder")) {
			// required attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException(
						"DynamicFolderHandler: no id defined!");
			}

			String name = attributes.getValue("name");
			if (name == null) {
				throw new PaloAPIException(
						"DynamicFolderHandler: no name specified!");
			}
			String source = attributes.getValue("source");
			
			View sourceView = null;
			if (source != null) {
				sourceView = StaticFolderHandler.parseSourceView(xmlHandler,
						source);
			}

			String hierarchyId = attributes.getValue("hierarchyId");
			Hierarchy h = null;
			if (hierarchyId != null) {
				String connectionServer = attributes
						.getValue("connectionServer");
				if (connectionServer.startsWith("http://")) {
					connectionServer = connectionServer.substring(7);
				}
				String connectionService = attributes
						.getValue("connectionService");
				String databaseId = attributes.getValue("databaseId");
				String dimensionId = attributes.getValue("dimensionId");
				Dimension d = null;
												
				// if (sourceView != null) {
				// d =
				// sourceView.createCubeView().getCube().getDimensionById(dimensionId);
				// if (d != null) {
				// h = d.getHierarchyById(hierarchyId);
				// }
				// t2 = System.currentTimeMillis();
				// }
				if (d == null) {
//					try {
						AdministrationService adminService = ServiceProvider
								.getAdministrationService(xmlHandler.getUser());
						for (PaloConnection pc : adminService.getConnections()) {							
							if (pc.getHost().equals(connectionServer)
									&& pc.getService()
											.equals(connectionService)) {
								for (Account acc : xmlHandler.getUser()
										.getAccounts()) {
									if (acc.getConnection().equals(pc)) {
//										long t10 = System.currentTimeMillis();
										Connection con = conMap.get(acc);
										if (con == null && acc instanceof PaloAccount) {
											con = ((PaloAccount) acc).login();
											conMap.put(acc, con);
										}
//										if ((t11 - t10) > maxLength) {
//											maxLength = t11 - t10;
//										}
//										if ((t11 - t10) < minLength) {
//											minLength = t11 - t10;
//										}
										Database db = con
												.getDatabaseById(databaseId);
										if (db == null) {
											continue;
										}
										Dimension dim = db
												.getDimensionById(dimensionId);
										if (dim == null) {
											continue;
										}
										h = dim.getHierarchyById(hierarchyId);
										if (h == null) {
											continue;
										}
										break;
									}
								}
								if (h != null) {
									break;
								}
							}
						}
//
//					} catch (AuthenticationFailedException e) {
//						e.printStackTrace();
//					}
				}
			}
			String subset = attributes.getValue("subset");
			Subset2 s = null;
			if (subset != null) {
				String[] ids = subset.split("@_@");
				Hierarchy hier = null;
				if (sourceView != null) {
//					Dimension d = sourceView.createCubeView().getCube()
//							.getDimensionById(ids[0]);
//					hier = d.getHierarchyById(ids[1]);
				}
				int type = Integer.parseInt(ids[3]);
				if (hier == null) {
					hier = h;
				}
				if (hier != null) {
					s = hier.getSubsetHandler().getSubset(ids[2], type);
				} 			
			}
//			// add Dynamic Folder to current parent
			DynamicFolder f = DynamicFolder.internalCreate(xmlHandler
					.getCurrentParent(), h, s, id, name);
			if (sourceView != null) {
				f.setSourceObject(sourceView);
			}
			xmlHandler.pushParent(f);
		}
	}

	public String getXPath() {
		return XPATH;
	}
	
	public static void clear() {
		conMap.clear();
	}

	public void leave(String path, String value) {
		if (path.startsWith("/folder/")
				&& (path.endsWith("dynamicFolder"))) {
			xmlHandler.popParent();
		}
	}
}
