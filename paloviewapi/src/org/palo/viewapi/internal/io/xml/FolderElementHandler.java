/*
*
* @file FolderElementHandler.java
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
* @version $Id: FolderElementHandler.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io.xml;

import org.palo.api.Connection;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Group;
import org.palo.viewapi.PaloAccount;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.internal.FolderElement;
import org.palo.viewapi.internal.IRoleManagement;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.services.ServiceProvider;
import org.xml.sax.Attributes;

/**
 * <code>FolderElementHandler</code>
 * Reads a folder element from the database.
 *
 * @author Philipp Bouillon
 * @version $Id: FolderElementHandler.java,v 1.13 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class FolderElementHandler implements IXMLHandler {

	public static final String XPATH = "/folder/folderElement";
		
	private FolderXMLHandler xmlHandler;

	public FolderElementHandler(FolderXMLHandler xmlHandler) {
		this.xmlHandler = xmlHandler;
	}
		
//	private final WSSWorkbook getWorkbook(String book) {
//		try {
//			if (book != null) {
//				for (Account a : xmlHandler.getUser().getAccounts()) {
//					String compare = book;
//					if (a instanceof WSSAccount) {
//						WSSConnection con = ((WSSAccount) a).login();
//						String accId = con.getId();
//						if (compare.startsWith(accId)) {
//							compare = compare.substring(accId.length() + 1);
//							String oComp = compare;
//							for (WSSApplication ap : con.getApplicationList()) {
//								if (compare.startsWith(ap.getId())) {
//									compare = compare.substring(ap.getId()
//											.length() + 1);
//									ap.select();
//									for (WSSWorkbook wbb : ap.getWorkbookList()) {
//										if (compare.equals(wbb.getId())) {
//											return wbb;
//										}
//									}
//								}
//								compare = oComp;
//							}
//						}
//						compare = book;
//					}
//				}
//			}
//		} catch (Throwable t) {
//			// Ignore any connection problems at this point...
//			t.printStackTrace();
//		}
//		return null;
//	}
	
	private final Hierarchy decodeHierarchy(String [] parts) {
		if (parts.length != 6) {
			return null;
		}
		AuthUser user = xmlHandler.getUser();
		for (Account a: user.getAccounts()) {
			if (a.getConnection().getHost().equals(parts[1]) &&
				a.getConnection().getService().equals(parts[2])) {
				if (a instanceof PaloAccount) {
					Connection con = ((PaloAccount) a).login();
					Database db = con.getDatabaseById(parts[3]);
					if (db != null) {
						Dimension dim = db.getDimensionById(parts[4]);
						if (dim != null) {
							Hierarchy hier = dim.getHierarchyById(parts[5]);
							if (hier != null) {
								return hier;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	private final Subset2 decodeSubset(String [] parts) {
		if (parts.length != 7) {
			return null;
		}
		AuthUser user = xmlHandler.getUser();
		for (Account a: user.getAccounts()) {
			if (a.getConnection().getHost().equals(parts[1]) &&
				a.getConnection().getService().equals(parts[2])) {
				if (a instanceof PaloAccount) {
					Connection con = ((PaloAccount) a).login();
					Database db = con.getDatabaseById(parts[3]);
					if (db != null) {
						Dimension dim = db.getDimensionById(parts[4]);
						if (dim != null) {
							Hierarchy hier = dim.getHierarchyById(parts[5]);
							if (hier != null) {
								Subset2 ss = 
									hier.getSubsetHandler().getSubset(parts[6], Subset2.TYPE_GLOBAL);
								if (ss == null) {
									ss = hier.getSubsetHandler().getSubset(parts[6], Subset2.TYPE_LOCAL);
								}
								if (ss != null) {
									return ss;
								}
							}
						}
					}
				}
			}
		}
		return null;		
	}
	
	private final Object decodeKey(String key) {
		String [] parts = key.split(":");
		if (parts == null || parts.length == 0) {
			return null;
		}
		if (parts[0].equals("hierarchy")) {
			return decodeHierarchy(parts);
		} else if (parts[0].equals("subset")) {
			return decodeSubset(parts);
		}
		return null;
	}
	
	private final boolean hasRole(AuthUser user, Role r) {
		for (Role rr: user.getRoles()) {
			if (rr.getId().equals(r.getId())) {
				return true;
			}
		}
		for (Group g: user.getGroups()) {
			for (Role rr: g.getRoles()) {
				if (rr.getId().equals(r.getId())) {
					return true;
				}				
			}
		}
		return false;
	}
	
	public void enter(String path, Attributes attributes) {
		if (path.startsWith("/folder/") && path.endsWith("folderElement")) {
			// required attributes:
			String id = attributes.getValue("id");
			if (id == null || id.equals("")) {
				throw new PaloAPIException("FolderElementHandler: no id defined!");
			}
			
			String name = attributes.getValue("name");
			if (name == null) {
				throw new PaloAPIException("FolderElementHandler: no name specified!");
			}
			
			// optional attributes:
			String source = attributes.getValue("source");
			View sourceView = null;
			boolean createElement = true;
			if (source != null) {
				createElement = false;
				sourceView = StaticFolderHandler.parseSourceView(xmlHandler, source);
				AuthUser user = xmlHandler.getUser();
				
				if (sourceView != null) {
					if (sourceView.isOwner(user) || ServiceProvider.isAdmin(user)) {
						createElement = true;
					} else {
						try {
							IRoleManagement roleMgmt = MapperRegistry.getInstance().getRoleManagement();
							Role viewerRole = (Role) roleMgmt.findByName("VIEWER");
							Role editorRole = (Role) roleMgmt.findByName("EDITOR");
							if (viewerRole != null && editorRole != null) {
								boolean hasView = sourceView.hasRole(viewerRole);
								boolean hasEdi  = sourceView.hasRole(editorRole);
								if (!hasView && !hasEdi) {
									createElement = false;
								} else {
									if (hasView && hasRole(user, viewerRole)) {
										createElement = true;
									}
									if (hasEdi && hasRole(user, editorRole)) {
										createElement = true;
									}
								}
							} else {
								createElement = true;
							}
						} catch (Exception e) {
							createElement = true;
						}
					}
				} else {
					createElement = false;
				}
			}
			
			String book = attributes.getValue("book");
//			WSSWorkbook wb = null;
//			if (book != null) {
//				//wb = getWorkbook(book);				
//			}
//						
			// add Folder Element to current parent
			if (createElement) {
				FolderElement e = FolderElement.internalCreate(
					xmlHandler.getCurrentParent(), id, name);
				if (sourceView != null) {
					e.setSourceObject(sourceView);
//				} else if (wb != null) {
					//				e.setSourceObject(wb);
				} else if (book != null) {
					e.setSourceObjectDescription("book" + book);
				}
				String mapping = attributes.getValue("mappings");
				if (mapping != null) {
					String [] keyValue = mapping.split(",");
					for (int i = 0; i < keyValue.length; i += 2) {
						String key = keyValue[i].trim();
						if (key.length() != 0) {
							Object o = decodeKey(key);
							if (o != null) {							
								String value = keyValue[i + 1].trim();							
								if (value.length() != 0) {
									if (o instanceof Hierarchy) {
										e.setVariableMapping((Hierarchy) o, value);
									} else if (o instanceof Subset2) {
										e.setVariableMapping((Subset2) o, value);
									}
								}
							}
						}
					}
				}
			}			
		}
	}

	public String getXPath() {
		return XPATH;
	}

	public void leave(String path, String value) {
	}	    	
}
