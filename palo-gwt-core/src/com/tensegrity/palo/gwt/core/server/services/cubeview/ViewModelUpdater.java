/*
*
* @file ViewModelUpdater.java
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
* @version $Id: ViewModelUpdater.java,v 1.20 2010/03/11 10:42:20 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.palo.api.ElementNode;
import org.palo.api.Hierarchy;
import org.palo.api.utils.ElementPath;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.Property;
import org.palo.viewapi.uimodels.axis.AxisItem;
import org.palo.viewapi.uimodels.axis.AxisModel;

import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxis;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisItem;
import com.tensegrity.palo.gwt.core.client.models.cubeviews.XViewModel;

/**
 * <code>ViewModelUpdater</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: ViewModelUpdater.java,v 1.20 2010/03/11 10:42:20 PhilippBouillon Exp $
 **/
public class ViewModelUpdater {

	static final void updateNative(ViewModel viewModel, XViewModel xViewModel, boolean updateLocalFilter) {
		updateNativeAxes(viewModel, xViewModel, true, true); //updateLocalFilter);
	}
	
	static final void updateNativeWithoutTable(ViewModel viewModel, XViewModel xViewModel) {
		updateNativeAxes(viewModel, xViewModel, false, true);
	}

	private static final void updateNativeAxes(ViewModel viewModel, XViewModel xViewModel, boolean withTable, boolean updateLocalFilter) {
		Map<String, AxisHierarchy> allAxisHierarchies = collectAxisHierarchies(viewModel);
		if (withTable) {
			updateNativeAxis(viewModel.getRowAxis(), xViewModel.getRowAxis(), allAxisHierarchies, !xViewModel.needsRestore());
			updateNativeAxis(viewModel.getColumnAxis(), xViewModel.getColumnAxis(), allAxisHierarchies, !xViewModel.needsRestore());
		} 
		updateNativeAxis(viewModel.getSelectionAxis(), xViewModel.getSelectionAxis(), allAxisHierarchies, !xViewModel.needsRestore());
		updateNativeAxis(viewModel.getRepositoryAxis(), xViewModel.getRepositoryAxis(), allAxisHierarchies, !xViewModel.needsRestore());
		if (withTable) {
			updateExpandedItems(viewModel, xViewModel);
		}
	}
	private static final Map<String, AxisHierarchy> collectAxisHierarchies(ViewModel viewModel) {
		Map<String, AxisHierarchy> allHierarchies = new HashMap<String, AxisHierarchy>();
		add(viewModel.getRowAxis().getAxisHierarchies(), allHierarchies);
		add(viewModel.getColumnAxis().getAxisHierarchies(), allHierarchies);
		add(viewModel.getSelectionAxis().getAxisHierarchies(), allHierarchies);
		add(viewModel.getRepositoryAxis().getAxisHierarchies(), allHierarchies);
		return allHierarchies;
	}
	private static final void add(AxisHierarchy[] axisHierarchies, Map<String, AxisHierarchy> toCollection) {
		for(AxisHierarchy axisHierarchy : axisHierarchies)
			toCollection.put(axisHierarchy.getHierarchy().getId(), axisHierarchy);
	}
	private static final void updateNativeAxis(AxisModel axisModel, XAxis xAxis, Map<String, AxisHierarchy> allAxisHierarchies, boolean updateLocalFilter) {
		updateNativeAxis(axisModel.getAxis(), xAxis, allAxisHierarchies, updateLocalFilter);
		axisModel.refresh();
	}
	private static final void updateNativeAxis(Axis axis, XAxis xAxis, Map<String, AxisHierarchy> allAxisHierarchies, boolean updateLocalFilter) {
		axis.removeAll();
		updateNativeProperties(axis, xAxis);
		List<XAxisHierarchy> xAxisHierarchies = xAxis.getAxisHierarchies();
		for(XAxisHierarchy xAxisHierarchy : xAxisHierarchies) {
			AxisHierarchy axisHierarchy = allAxisHierarchies.get(xAxisHierarchy.getId());
			if (updateLocalFilter) {
				AxisHierarchyUpdater.update(axisHierarchy, xAxisHierarchy);
			}
			axis.add(axisHierarchy);
		}
	}
	private static final void updateNativeProperties(Axis axis, XAxis xAxis) {
		axis.removeAllProperties();
		for(String id : xAxis.getPropertyIDs()) {
			String propValue = xAxis.getProperty(id);
			Property<String> property = new Property<String>(id, propValue);
			axis.addProperty(property);
		}
	}
	private static final void updateExpandedItems(ViewModel viewModel, XViewModel xViewModel) {
		Map<String, List<XAxisItem>> expandedItems = collectExpandedItems(xViewModel);
		updateExpandedItems(viewModel.getRowAxis(), expandedItems);
		updateExpandedItems(viewModel.getColumnAxis(), expandedItems);
	}
	private static final Map<String, List<XAxisItem>> collectExpandedItems(XViewModel xViewModel) {
		Map<String, List<XAxisItem>> expandedItems = new HashMap<String, List<XAxisItem>>();
		addExpandedItemsOf(xViewModel.getRowAxis(), expandedItems);
		addExpandedItemsOf(xViewModel.getColumnAxis(), expandedItems);
		return expandedItems;
	}
	private static final void addExpandedItemsOf(XAxis axis, Map<String, List<XAxisItem>> toMap) {
		for(XAxisItem item : axis.getExpandedItems()) {
			List<XAxisItem> items = getItemsList(item.getHierarchyId(), toMap);
			items.add(item);
		}
	}
	private static final List<XAxisItem> getItemsList(String id, Map<String, List<XAxisItem>> fromMap) {
		List<XAxisItem> items = fromMap.get(id);
		if(items == null) {
			items = new ArrayList<XAxisItem>();
			fromMap.put(id, items);			
		}
		return items;
	}
	private static final void updateExpandedItems(AxisModel inAxis, Map<String, List<XAxisItem>> items) {
		int index = 0;
		for(Hierarchy hierarchy : inAxis.getHierarchies()) {
			String hierarchyID = hierarchy.getId();
			if(items.containsKey(hierarchyID)) {
				List<XAxisItem> expandedItems = items.get(hierarchyID);

				AxisHierarchy axisHierarchy = inAxis.getAxisHierarchy(hierarchyID);
				HashSet <String> ids = new HashSet<String>();
				if (axisHierarchy != null && axisHierarchy.getLocalFilter() != null) {
					addAllIds(axisHierarchy.getLocalFilter().getVisibleElements(), ids);
				}
				for(XAxisItem item : expandedItems) {
					String path = cutOffPrefix(item.getPath(), index);
					expandItem(path, item.index, inAxis, hierarchyID, ids, index);
					try {
						ElementPath elementPath = ElementPath.restore(inAxis.getAxis().getHierarchies(), item.getPath());
						inAxis.getAxis().addExpanded(elementPath);
					} catch (Throwable t) {
						// ignore
					}
				}
			}
			index++;
		}
	}
	private static final String cutOffPrefix(String path, int index) {
		String[] parts = path.split(ElementPath.DIMENSION_DELIM);
		if(parts == null || parts.length <= index + 1)
			return path;
		StringBuilder newPath = new StringBuilder();
		int last = parts.length - 1;
		for(int i=index+1; i<parts.length; ++i) {
			newPath.append(parts[i]);
			if(i<last)
				newPath.append(ElementPath.DIMENSION_DELIM);
		}
		return newPath.toString();
	}
	
	private static final void addAllIds(ElementNode [] roots, HashSet <String> ids) {
		if (roots == null) {
			return;
		}
		for (ElementNode n: roots) {
			ids.add(n.getElement().getId());
			addAllIds(n.getChildren(), ids);
		}
	}
	private static final String modifyPath(String path, HashSet <String> ids, int axisIndex) {
		int size = ids.size();
		String [] segments = path.split(":");
		StringBuffer np = new StringBuffer();
		for (int i = 0, n = segments.length; i < n; i++) {
			if (i == axisIndex) {
				String [] pIds = segments[i].split(",");
				StringBuffer subPath = new StringBuffer();
				for (String s: pIds) {
					if (size == 0 || ids.contains(s)) {
						subPath.append(s);
						subPath.append(",");
					}
				}
				if (subPath.length() != 0 && subPath.charAt(subPath.length() - 1) == ',') {
					String sp = subPath.substring(0, subPath.length() - 1);
					np.append(sp);
					np.append(":");
				}
			} else {
				np.append(segments[i]);
				np.append(":");
			}
		}
		
		path = np.toString();
		if (path != null && path.length() > 0) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}
	
	private static final void expandItem(String path, int index, AxisModel axis, String hierarchyId, HashSet <String> ids, int axisIndex) {
		if(path == null || path.equals(""))
			return;
		path = checkPath(path);
		
		path = modifyPath(path, ids, axisIndex);
		if (path.isEmpty()) {
			return;
		}
		
		AxisItem item = axis.findItem(".*"+path, -1, hierarchyId);
//		if (item == null) {
//			System.err.println("Did not find item " + path);
//		}
		if(item != null)
			axis.expand(item);
	}
	private static String checkPath(String path) {
		//workaround for XMLAs strange IDs which do not work with regex!!!!!
		path = path.replace(".", "\\.");
		path = path.replace("(", "\\(");
		return path.replace(")", "\\)");
	}
}
