/*
*
* @file ReportFolderChildLoader.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: ReportFolderChildLoader.java,v 1.16 2010/02/12 13:49:50 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.server.childloader;

import org.palo.viewapi.internal.FolderElement;

import com.tensegrity.palo.gwt.core.client.models.XObject;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
import com.tensegrity.palo.gwt.core.client.models.reports.XDynamicReportFolder;
import com.tensegrity.palo.gwt.core.client.models.reports.XStaticReportFolder;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

public class ReportFolderChildLoader implements ChildLoader {
	public boolean accepts(XObject parent) {
		return parent instanceof XDynamicReportFolder ||
		       parent instanceof XStaticReportFolder;
	}

//	private final static WSSWorkbook getWorkbook(AuthUser user, String book) {
//		if (user == null) {
//		}
//		try {
//			if (book != null) {
//				for (Account a : user.getAccounts()) {
//					String compare = book;
//					if (a instanceof WSSAccount) {
////						WSSConnection con = ((WSSAccount) a).login();
////						String accId = con.getId();
////						if (compare.startsWith(accId)) {
////							compare = compare.substring(accId.length() + 1);
////							String oComp = compare;
////							for (WSSApplication ap : con.getApplicationList()) {
////								if (compare.startsWith(ap.getId())) {
////									compare = compare.substring(ap.getId()
////											.length() + 1);
////									ap.select();
////									for (WSSWorkbook wbb : ap.getWorkbookList()) {
////										if (compare.equals(wbb.getId())) {
////											return wbb;
////										}
////									}
////								}
////								compare = oComp;
////							}
////							WSSApplication ap = con.getSystemApplication();
////							if (compare.startsWith(ap.getId())) {
////								compare = compare.substring(ap.getId()
////										.length() + 1);
////								ap.select();
////								for (WSSWorkbook wbb : ap.getWorkbookList()) {
////									if (compare.equals(wbb.getId())) {
////										return wbb;
////									}
////								}	
////								compare = oComp;
////							}
////						}
////						compare = book;
//					}
//				}
//			}
//		} catch (Throwable t) {
//			// Ignore any connection problems at this point...
//			t.printStackTrace();
//		}
//		return null;
//	}
	
	public static final Object getSourceObjectFromElement(XUser user, FolderElement fe) {
//		if (fe.getSourceObject() == null) {
//			if (fe.getSourceObjectDescription() == null) {
//				return null;
//			} else {
//				String desc = fe.getSourceObjectDescription();
//				if (desc.startsWith("book")) {							
//					fe.setSourceObject(getWorkbook((AuthUser) XObjectMatcher.getNativeObject(user), 
//							desc.substring(4)));
//				}
//			}
//			if (fe.getSourceObject() == null) {
//			}
//		}
//		return fe.getSourceObject();
		return null;
	}
	
	public XObject[] loadChildren(XObject parent, UserSession userSession) {
//		boolean calculated = parent instanceof XReportFolder &&
//			((XReportFolder) parent).doReturnComputedKids();
//		ExplorerTreeNode parentObject =
//			(ExplorerTreeNode) XObjectMatcher.getNativeObject(parent);
//		XUser user = parent instanceof XDynamicReportFolder ?
//				((XDynamicReportFolder) parent).getUser() :
//				((XStaticReportFolder) parent).getUser();
//				
//		ExplorerTreeNode [] kids;
//		if (calculated && parentObject instanceof DynamicFolder) {
//			kids = ((DynamicFolder) parentObject).getCalculatedChildren();
//		} else {
//			kids = parentObject.getChildren();
//		}
//		if (kids == null || kids.length == 0) {
//			return new XObject[0];
//		}
//		
//		XObject [] ret = new XObject[kids.length];
//		for (int i = 0, n = kids.length; i < n; i++) {
//			if (kids[i] instanceof DynamicFolder) {
//				DynamicFolder temp = (DynamicFolder) kids[i];
//				ExplorerTreeNode [] ks = temp.getChildren();
//				Hierarchy hier = temp.getSourceHierarchy();
//				Subset2 subset = temp.getSourceSubset();
//				XHierarchy xHier = null;
//				XSubset sub = null;
//				if (hier != null) {
//					xHier = (XHierarchy) XConverter.createX(hier);
////					xHier = WPaloServiceImpl.createXHierarchy(hier, user,
////							(AuthUser) XObjectMatcher.getNativeObject(user));
//				}
//				if (subset != null) {
//					sub = (XSubset)XConverter.createX(subset);
////					sub = WPaloServiceImpl.createXSubset(subset, user, 
////							(AuthUser) XObjectMatcher.getNativeObject(user));
//				}
//				ret[i] = new XDynamicReportFolder(
//						temp.getName(), temp.getId(),
//						ks != null && ks.length > 0, calculated,
//						xHier, sub, user);
//				XObjectMatcher.put(ret[i], temp);
//			} else if (kids[i] instanceof StaticFolder) {
//				StaticFolder temp = (StaticFolder) kids[i];
//				ExplorerTreeNode [] ks = temp.getChildren();				
//				ret[i] = new XStaticReportFolder(temp.getName(), temp.getId(), 
//						ks != null && ks.length > 0, calculated, user);
//				XObjectMatcher.put(ret[i], temp);
//			} else if (kids[i] instanceof FolderElement) {
//				FolderElement temp = (FolderElement) kids[i];
//				String type;
//				Object o = getSourceObjectFromElement(user, (FolderElement) kids[i]);
//				if (o instanceof View) {
//					type = XConstants.TYPE_FOLDER_ELEMENT_VIEW;
//				} else {
//					type = XConstants.TYPE_FOLDER_ELEMENT_SHEET;
//				}
//				AuthUser usr = (AuthUser) XObjectMatcher.getNativeObject(user);
//				List <XElement> elements = new ArrayList<XElement>();
////					WPaloServiceImpl.retrieveElements(temp, user, usr);
//				ret[i] = new XReport(temp.getName(), temp.getId(), 
//						type, elements.toArray(new XElement[0]));
//				XObjectMatcher.put(ret[i], temp);
//			}
//		}
//		return ret;
		return new XObject[0];
	}	
}
