/*
*
* @file CubeViewConverter.java
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
* @version $Id: CubeViewConverter.java,v 1.30 2010/02/12 13:50:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Axis;
import org.palo.api.Cube;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.exceptions.PaloIOException;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Account;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.LocalFilter;
import org.palo.viewapi.Property;
import org.palo.viewapi.Role;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.OperationFailedException;
import org.palo.viewapi.internal.ConnectionPoolManager;
import org.palo.viewapi.internal.IViewManagement;
import org.palo.viewapi.internal.LocalFilterImpl;
import org.palo.viewapi.internal.dbmappers.MapperRegistry;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.services.ServiceProvider;
import org.palo.viewapi.services.ViewService;

import com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.FormatConverter;

/**
 * <code>ViewConverter</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewConverter.java,v 1.30 2010/02/12 13:50:49 PhilippBouillon Exp $
 **/
public class CubeViewConverter {

	private static final String LEGACY_ALIAS_PREFIX ="Dim2Alias#";
	private static final String OLD_LEGACY_ALIAS_PREFIX = "Alias#"; //very old ;)
	
	private static final Account getAccount(String id, AuthUser user) {
		for(Account account : user.getAccounts())
			if(account.getId().equals(id))
				return account;
		return null;
	}
	
	public static final synchronized View createDefaultView(String name, Cube cube,
			String accountId, AuthUser user, String sessionId, String externalId) throws OperationFailedException {
		View view = null;
		ViewService viewService = ServiceProvider.getViewService(user);
		if (viewService != null) {
			String viewName = getUniqueViewName(name, accountId, user);
			view = viewService.createView(viewName, cube, user, sessionId, externalId);			
			try {
				CubeViewReader.CHECK_RIGHTS = false;
				viewService.setOwner(user, view);
				CubeView newView;
				try {
					newView = view.createCubeView(user, sessionId);
				} catch (PaloIOException e) {
					throw new OperationFailedException(e.getMessage(), e);
				}
//				IRoleManagement roleMgmt = MapperRegistry.getInstance().getRoleManagement();
//				try {
//					Role ownerRole = (Role) roleMgmt.findByName(Role.OWNER);
//					viewService.add(ownerRole, view);
//				} catch (SQLException e) {
//				}			
				reset(newView, user);
				if (externalId != null) {
					newView.addProperty("paloSuiteID", externalId);
				}
				createAxesFor(newView, cube);
				viewService.save(view);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(getAccount(accountId, user), sessionId, "CubeViewConverter.createDefaultView");
				CubeViewReader.CHECK_RIGHTS = true;
			}
		}
		return view;
	}
	
	private static final void createAxesFor(CubeView cubeView, Cube cube) {
		Dimension[] dimensions = cube.getDimensions();
		//row
		org.palo.viewapi.Axis rowAxis = cubeView.addAxis(CubeView.ROW_AXIS, "row");
//		initialize(rowAxis, dimensions[0].getDefaultHierarchy());
		//column
		org.palo.viewapi.Axis columnAxis = cubeView.addAxis(CubeView.COLUMN_AXIS, "column");
//		initialize(columnAxis, dimensions[1].getDefaultHierarchy());
//		//repository
//		org.palo.viewapi.Axis rowAxis = cubeView.addAxis(CubeView._AXIS, "row");
		//selection
		org.palo.viewapi.Axis selectionAxis = cubeView.addAxis(CubeView.SELECTION_AXIS, "selection");
		for(int i = 0; i<dimensions.length; ++i)
			initialize(selectionAxis, dimensions[i].getDefaultHierarchy());
	}
	private static final void initialize(org.palo.viewapi.Axis axis, Hierarchy hierarchy) {
		AxisHierarchy axisHierarchy = axis.add(hierarchy);
		Element[] elements = hierarchy.getElementsInOrder();
		axisHierarchy.addSelectedElement(elements[0]);
	}

	
	public static final synchronized View toView(String name, org.palo.api.CubeView legacyView, AuthUser user, String sessionId) throws OperationFailedException {
		View view = null;
		CubeView newView = null;
		ViewService viewService = ServiceProvider.getViewService(user);
		try {
			if (viewService != null) {
			// we allow views with same name...
				String viewName = getUniqueViewName(name, null, user);				
			view = viewService.createView(viewName, legacyView.getCube(), user, sessionId, null);
			CubeViewReader.CHECK_RIGHTS = false;
			viewService.setOwner(user, view);
			try {
				newView = view.createCubeView(user, sessionId);
			} catch (PaloIOException e) {
				throw new OperationFailedException(e.getMessage(), e);
			} finally {
				ConnectionPoolManager.getInstance().disconnect(view.getAccount(), sessionId, "CubeViewConverter.toView");
			}
			reset(newView, user);
		
			//add properties:
			for (String prop: legacyView.getProperties()) {
				newView.addProperty(prop, legacyView.getPropertyValue(prop));
			}				
			//add all axes:		
			org.palo.api.Axis[] legacyAxes = legacyView.getAxes();
			for(Axis oldAxis : legacyAxes) {
				org.palo.viewapi.Axis newAxis = 
					newView.addAxis(oldAxis.getId(), oldAxis.getName());
				convert(oldAxis, newAxis);
			}
			//formats:
			FormatConverter.convert(legacyView, newView);
			
			//finally save it:
			viewService.save(view);
			}
		} finally {
			CubeViewReader.CHECK_RIGHTS = true;
		}
		return view;
	}


	private static final void reset(CubeView view, AuthUser user) {
		// remove all axes...
		org.palo.viewapi.Axis[] axes = view.getAxes();
		for (org.palo.viewapi.Axis axis : axes)
			view.removeAxis(axis);
		// formats...		
		try {
			view.removeAllFormats();
		} catch (Throwable t) {
			ViewService viewService = ServiceProvider.getViewService(user);
			for (Role r: user.getRoles()) {
				System.err.println(r.getName());
			}
			t.printStackTrace();
			// Ignore for now...
		}
		// view.removeAllFormatRanges();
		// and properties...
		Property<Object>[] props = view.getProperties();
		for (Property<Object> prop : props)
			view.removeProperty(prop.getId());
	}

	private static final void convert(Axis fromAxis,
			org.palo.viewapi.Axis toAxis) {
		Hierarchy [] hiers = fromAxis.getHierarchies();
		for (Hierarchy hier: hiers) {
			AxisHierarchy hierarchy = toAxis.add(hier); //new AxisHierarchy(hier);
			toAxis.add(hierarchy);
			// subset:
			Subset2 subset = fromAxis.getActiveSubset2(hier.getDimension());
			if (subset != null)
				hierarchy.setSubset(subset);
			// local filter:
			hierarchy.setLocalFilter(convertLocalFilter(fromAxis
					.getVisiblePaths(hier), hierarchy.getHierarchy()));
			// selected element:
			Element selElement = fromAxis.getSelectedElement(hier);
			if (selElement != null) {
				if(!hierarchy.contains(selElement))
					selElement = hierarchy.getRootNodes()[0].getElement();
				hierarchy.addSelectedElement(selElement);
			}
		}
		// expanded paths
		toAxis.addExpanded(fromAxis.getExpandedPaths());
    	//convert aliases:
		String[] propIDs = fromAxis.getProperties();
		for (String id : propIDs) {
			String propValue = fromAxis.getPropertyValue(id);
			if(id.startsWith(OLD_LEGACY_ALIAS_PREFIX)) {
				String dimName = getDimensionNameFrom(id);
				Hierarchy hierarchy = getHierarchyForDimensionName(dimName, fromAxis);
				if(hierarchy != null) {
					Attribute alias = hierarchy.getAttributeByName(propValue);
					AxisHierarchy axisHierarchy = toAxis.getAxisHierarchy(hierarchy);
					Property<Attribute> aliasProperty = 
						new Property<Attribute>(AxisHierarchy.USE_ALIAS, alias);
					axisHierarchy.addProperty(aliasProperty);
				}
			} else if(id.startsWith(LEGACY_ALIAS_PREFIX)) {
				String dimId = getDimensionIdFrom(id);
				Hierarchy hierarchy = getHierarchyForDimension(dimId, fromAxis);
				if(hierarchy != null) {
					Attribute alias = hierarchy.getAttribute(propValue);
					AxisHierarchy axisHierarchy = toAxis.getAxisHierarchy(hierarchy);
					Property<Attribute> aliasProperty = 
						new Property<Attribute>(AxisHierarchy.USE_ALIAS, alias);
					axisHierarchy.addProperty(aliasProperty);
				} 
			} 
		}
	}

	private static final String getDimensionNameFrom(String propId) {
		return propId.substring(OLD_LEGACY_ALIAS_PREFIX.length());
	}
	private static final Hierarchy getHierarchyForDimensionName(String name, Axis axis) {
		for(Dimension dimension : axis.getDimensions()) {
			if(dimension.getName().equals(name)) {
				return dimension.getDefaultHierarchy();
			}
		}
		return null;
	}
	private static final String getDimensionIdFrom(String propId) {
		return propId.substring(LEGACY_ALIAS_PREFIX.length());
	}
	private static final Hierarchy getHierarchyForDimension(String id, Axis axis) {
		for(Dimension dimension : axis.getDimensions()) {
			if(dimension.getId().equals(id))
				return dimension.getDefaultHierarchy();
		}
		return null;
	}
	private static final LocalFilter convertLocalFilter(
			ElementPath[] visiblePaths, Hierarchy hierarchy) {
		LocalFilter localFilter = null;
		if (visiblePaths != null && visiblePaths.length > 0) {
			visiblePaths = sort(visiblePaths, hierarchy);
			localFilter = new LocalFilterImpl();
			Map<String, ElementNode> visibleNodes = 
				new LinkedHashMap<String, ElementNode>();
			for (ElementPath path : visiblePaths) {
				Element[] part = path.getPart(hierarchy);
				if (part != null && part.length > 0)
					visibleNodes.put(keyFor(part), new ElementNode(part[part.length - 1]));
			}
			//build hierarchy:
			for(String key : visibleNodes.keySet()) {
				ElementNode node = visibleNodes.get(key);
				ElementNode parent = getParent(getParentKey(key), visibleNodes);
				if(parent == null)
					localFilter.addVisibleElement(node);
				else
					parent.forceAddChild(node);
			}
		}
		return localFilter;
	}
	private static final ElementPath[] sort(ElementPath[] paths, Hierarchy hierarchy) {
		final Map<String, ElementPath> keyToPath = new HashMap<String, ElementPath>();
		for(ElementPath path : paths) {
			Element[] part = path.getPart(hierarchy);
			keyToPath.put(keyFor(part), path);
		}
		final List<ElementPath> orderedPaths = new ArrayList<ElementPath>();
		//traverse hierarchy to get correct order:
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode elementNode, ElementNode parent) {
				String key = keyFor(elementNode, null);
				if(keyToPath.containsKey(key)) {
					orderedPaths.add(keyToPath.get(key));
				}
			}
		};
		hierarchy.visitElementTree(visitor);
		
		return orderedPaths.toArray(new ElementPath[0]);
	}
	private static final String keyFor(Element[] part) {
		if(part.length <= 0)
			return null;
		StringBuilder key = new StringBuilder();
		int index = 0;
		for(int n=part.length - 1; index<n;index++) {
			key.append(part[index].getId());
			key.append(",");
		}
		key.append(part[index].getId()); 
		return key.toString();
	}
	private static final String keyFor(ElementNode node, String suffix) {
		if(node == null)
			return suffix;
		StringBuilder key = new StringBuilder();
		if(suffix != null) {
			key.append(",");
			key.append(suffix);
		}
		key.insert(0, node.getElement().getId());
		return keyFor(node.getParent(), key.toString());
	}
	private static final String getParentKey(String key) {
		int endIndex = key.lastIndexOf(',');
		if(endIndex >0)
			return key.substring(0, endIndex);
		else
			return null;
	}
	private static final ElementNode getParent(String key, Map<String, ElementNode> allNodes) {
		ElementNode parent = allNodes.get(key);
		if(parent == null && key != null) {
			key = getParentKey(key);
			return getParent(key, allNodes);
		}
		return parent;
	}
	private static final String getUniqueViewName(String viewName, String accountId, AuthUser user) {
//		AdministrationService adminService = 
//			ServiceProvider.getAdministrationService(user);
//		Account account = adminService.getAccount(accountId);
//		Account account = null;
//		for (Account acc: user.getAccounts()) {
//			if (acc.getId().equals(accountId)) {
//				account = acc;
//			}
//		}
//		ViewService viewService = ServiceProvider.getViewService(user);
		IViewManagement viewMgmt = MapperRegistry.getInstance().getViewManagement();
		List<View> allRoleViews = new ArrayList<View>();
		try {
			allRoleViews = viewMgmt.listViews();
		} catch (SQLException e) {
		}
		return getUniqueName(viewName, allRoleViews);
	}
	private static final String getUniqueName(String name, List<View> knownViews) {
		Set<String> usedNames = getViewNames(knownViews);
		String uniqueName = name;
		int i = 1;
		while (usedNames.contains(uniqueName))
			uniqueName = name + Integer.toString(i++);
		return uniqueName;
	}
	private static final Set<String> getViewNames(List<View> views) {
		Set<String> names = new HashSet<String>();
		for(View view : views)
			names.add(view.getName());
		return names;
	}
}
