/*
*
* @file AxisImpl.java
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
* @author ArndHouben
*
* @version $Id: AxisImpl.java,v 1.16 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.palo.api.Axis;
import org.palo.api.CubeView;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.Subset;
import org.palo.api.impl.utils.ArrayListInt;
import org.palo.api.subsets.Subset2;
import org.palo.api.utils.ElementPath;

/**
 * Default implementation of the {@link Axis} interface.
 *  
 * @author ArndHouben
 * @version $Id: AxisImpl.java,v 1.16 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class AxisImpl implements Axis {

	private final Set <Hierarchy> hiers = new LinkedHashSet<Hierarchy>();
	private final Map <Hierarchy, Map <String,Element []>> expanded = 
		new HashMap<Hierarchy, Map <String, Element []>>();
	private final Set <ElementPath> expandedPaths = new HashSet<ElementPath>();
	private final Map <Hierarchy, Map <String, Element []>> hidden = new HashMap<Hierarchy, Map<String,Element[]>>();
	private final Map <Hierarchy, Set <ElementPath>>visiblePaths = new HashMap<Hierarchy, Set<ElementPath>>();
	private final Map <String, String> properties = new HashMap<String, String>();
	private final Map<Dimension, Subset> activeSubs = 
		new HashMap<Dimension, Subset>();
	private final Map<Dimension, Subset2> activeSubs2 =
		new HashMap<Dimension, Subset2>();
	private final Map <Hierarchy, Element> selectedElements = 
		new HashMap <Hierarchy, Element>();
	private final Map <Hierarchy, Map <String, ArrayListInt>> repetitions = 
		new HashMap<Hierarchy, Map<String,ArrayListInt>>();
	private final Map <Dimension, Hierarchy> dimHier = new HashMap<Dimension, Hierarchy>();
	
	private final String id;
	private final CubeView view;
	private String name;
    //for storing a key/value pair
    private String[] data = new String[0];

	
	AxisImpl(String id,String name,CubeView view) {
		this.id = id;
		this.name = name;
		this.view = view;
	}
	
	public final void add(Dimension dimension) {
		hiers.add(dimension.getDefaultHierarchy());
		dimHier.put(dimension, dimension.getDefaultHierarchy());
	}
	
	public final void add(Hierarchy hierarchy) {
		hiers.add(hierarchy);
		dimHier.put(hierarchy.getDimension(), hierarchy);
	}

	public final void addExpanded(Dimension dimension,Element[] path,int repetition) {
		addExpanded(dimension.getDefaultHierarchy(), path, repetition);		
	}

	public final void addExpanded(Hierarchy hierarchy,Element[] path,int repetition) {
		Map <String, Element []> paths = expanded.get(hierarchy);
		if(paths == null) {
			paths = new HashMap <String, Element []>(path.length);
			expanded.put(hierarchy, paths);
		}
		String key = pathToString(path);
		paths.put(key,path);
		//repetitions:
		Map <String, ArrayListInt> reps = repetitions.get(hierarchy);
		if(reps == null) {
			reps = new HashMap<String, ArrayListInt>(path.length);
			repetitions.put(hierarchy, reps);
		}
		//get repetitions for path:
		ArrayListInt repList = reps.get(key);
		if(repList == null) {
			repList = new ArrayListInt();
			reps.put(key, repList);
		}
		repList.add(repetition);
	}

	public final void addHidden(Dimension dimension, Element[] path) {
		addHidden(dimension.getDefaultHierarchy(), path);
	}
	
	public final void addHidden(Hierarchy hierarchy, Element[] path) {
		Map <String, Element []> paths = hidden.get(hierarchy);
		if(paths == null) {
			paths = new HashMap<String, Element []>(path.length);
			hidden.put(hierarchy, paths);
		}
		String key = pathToString(path);
		paths.put(key,path);
		dimHier.put(hierarchy.getDimension(), hierarchy);
	}
	
	public final void addVisible(ElementPath path) {
		Hierarchy[] hierarchies = path.getHierarchies();
		//we expect that path consist of only one dimension		
		Set <ElementPath> paths = visiblePaths.get(hierarchies[0]);
		if(paths == null) {
			paths = new HashSet<ElementPath>();
			visiblePaths.put(hierarchies[0],paths);
		}
		paths.add(path);
	}
	
	public final boolean isVisible(ElementPath path) {
		Hierarchy[] hierarchies = path.getHierarchies();
		Set <ElementPath> paths = visiblePaths.get(hierarchies[0]);
		if(paths != null) {
			return paths.contains(path);
		}
		return false;
	}
	
	public final void addProperty(String id, String value) {
		properties.put(id, value);
	}

	public final Subset getActiveSubset(Dimension dimension) {
		return (Subset)activeSubs.get(dimension);
	}

	public final Subset2 getActiveSubset2(Dimension dimension) {
		return activeSubs2.get(dimension);
	}
	
	public final Dimension[] getDimensions() {
		Hierarchy [] hierarchies = getHierarchies();
		Dimension [] result = new Dimension[hierarchies.length];
		for (int i = 0, n = hierarchies.length; i < n; i++) {
			result[i] = hierarchies[i].getDimension();
		}
		return result;
	}
	
	public final Hierarchy getHierarchy(Dimension dim) {
		if (!dimHier.containsKey(dim)) {
			return dim.getDefaultHierarchy();
		}
		return dimHier.get(dim);
	}
	
	public final Hierarchy[] getHierarchies() {
		return hiers.toArray(new Hierarchy[hiers.size()]);
	}

	public final Element[][] getExpanded(Hierarchy hierarchy) {
		Map <String, Element[]> paths = expanded.get(hierarchy);
		if(paths == null)
			return new Element[0][0];
		Element[][] expPaths = new Element[paths.size()][];
		int i=0;
		for(Iterator it = paths.values().iterator();it.hasNext();) {
			expPaths[i] = (Element[])it.next();
			i++;
		}
		return expPaths;
	}
	
	public final Element[][] getExpanded(Dimension dimension) {
		return getExpanded(dimension.getDefaultHierarchy());
	}
	
	public final int[] getRepetitionsForExpanded(Hierarchy hierarchy, Element[] path) {
		//repetitions:
		Map<String, ArrayListInt> reps = repetitions.get(hierarchy);
		if(reps == null) 
			return new int[0];
		//get repetitions for path:
		String key = pathToString(path);
		ArrayListInt repList = reps.get(key);
		if(repList == null)
			return new int[0];
		
		return repList.toArray();
	}
	
	public final int[] getRepetitionsForExpanded(Dimension dimension, Element[] path) {
		return getRepetitionsForExpanded(dimension.getDefaultHierarchy(), path);
	}
	
	public final Element[][] getHidden(Hierarchy hierarchy) {
		Map <String, Element[]> paths = hidden.get(hierarchy);
		if(paths == null)
			return new Element[0][0];
		Element[][] hiddenPaths = new Element[paths.size()][];
		int i=0;
		for(Iterator it = paths.values().iterator(); it.hasNext();) {
			hiddenPaths[i] = (Element[])it.next();
			i++;
		}
		return hiddenPaths;
	}

	public final Element[][] getHidden(Dimension dimension) {
		return getHidden(dimension.getDefaultHierarchy());
	}

	public final ElementPath[] getVisiblePaths(Hierarchy hierarchy) {
		Set <ElementPath> paths = visiblePaths.get(hierarchy);
		if(paths == null)
			return new ElementPath[0];
		return paths.toArray(new ElementPath[paths.size()]);
	}

	public final ElementPath[] getVisiblePaths(Dimension dimension) {
		return getVisiblePaths(dimension.getDefaultHierarchy());
	}
	
	
	public final String[] getProperties() {
		return properties.keySet().toArray(new String[properties.size()]);
	}

	public final String getPropertyValue(String id) {
		return properties.get(id);
	}

	public final Element getSelectedElement(Dimension dimension) {
		return getSelectedElement(dimension.getDefaultHierarchy());
	}
	
	public final Element getSelectedElement(Hierarchy hierarchy) {
		return selectedElements.get(hierarchy);
	}

	public final void remove(Dimension dimension) {
		remove(dimension.getDefaultHierarchy());
	}
	
	public final void remove(Hierarchy hier) {
		hiers.remove(hier);
		hidden.remove(hier);
		expanded.remove(hier);
		repetitions.remove(hier);
		selectedElements.remove(hier);		
		expandedPaths.clear();
		visiblePaths.remove(hier);		
	}

	public final void removeExpanded(Hierarchy hierarchy,Element[] path,int repetition) {
		Map <String, Element[]> paths = expanded.get(hierarchy);
		if(paths == null) 
			return;
		String key = pathToString(path);
		paths.remove(key);
		//remove repetition:
		Map<String, ArrayListInt> reps = repetitions.get(hierarchy);
		if(reps == null) 
			return;
		//get repetitions for path:
		ArrayListInt repList = reps.get(key);
		if(repList == null)
			return;
		repList.remove(repetition);
	}

	public final void removeExpanded(Dimension dimension,Element[] path,int repetition) {
		removeExpanded(dimension.getDefaultHierarchy(), path, repetition);
	}

	public final void removeHidden(Hierarchy hierarchy, Element[] path) {
		Map<String, Element[]> paths = hidden.get(hierarchy);
		if(paths == null) 
			return;		
		String key = pathToString(path);
		paths.remove(key);
	}

	public final void removeHidden(Dimension dimension, Element[] path) {
		removeHidden(dimension.getDefaultHierarchy(), path);
	}
	
	public final void removeVisible(ElementPath path) {
		Hierarchy[] hierarchies = path.getHierarchies();
		//expect only one dimension:		
		Set<ElementPath> paths = visiblePaths.get(hierarchies[0]);
		if(paths!=null)
			paths.remove(path);
	}
	
	public final void removeProperty(String id) {
		properties.remove(id);
	}

	public final void setActiveSubset(Dimension dimension, Subset subset) {
		activeSubs.put(dimension,subset);
	}
	
	public final void setActiveSubset2(Dimension dimension,Subset2 subset) {
		activeSubs2.put(dimension, subset);
	}
	

	public final void setSelectedElement(Dimension dimension, Element element) {
		selectedElements.put(dimension.getDefaultHierarchy(),element);
	}
	
	public final void setSelectedElement(Hierarchy hierarchy, Element element) {
		selectedElements.put(hierarchy, element);
	}

	public final String getId() {
		return id;
	}

	public final synchronized String getName() {
		return name;
	}

	public final synchronized void setName(String name) {
		this.name = name;
	}

	public final boolean equals(Object obj) {
		if (obj instanceof AxisImpl) {
			AxisImpl other = (AxisImpl) obj;
			return other.getId().equals(id)
					&& other.view.getId().equals(view.getId());
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 17;
		hc = 37 * hc  + id.hashCode();
		hc = 37 * hc + view.getId().hashCode();
		return hc;
	}
	
	public final void addExpanded(ElementPath path) {
		expandedPaths.add(path);
	}
	public final void addExpanded(ElementPath[] paths) {
		expandedPaths.addAll(Arrays.asList(paths));
	}

	public final ElementPath[] getExpandedPaths() {
		return expandedPaths
				.toArray(new ElementPath[expandedPaths.size()]);
	}

	public final void removeExpanded(ElementPath path) {
		expandedPaths.remove(path);
	}

    final void setData(String key, String data) {
		if (key == null)
			return;
		int index = 0;
		int dataLength = this.data.length;
		while (index < dataLength) {
			if (key.equals(this.data[index]))
				break;
			index += 2;
		}
		if (index >= dataLength) {
			String[] newData = new String[dataLength + 2];
			System.arraycopy(this.data, 0, newData, 0, dataLength);
			this.data = newData;
		}
		this.data[index] = key;
		this.data[index + 1] = data;
	}

	public final String getData(String key) {
		if (key != null) {
			int index = 0;
			int dataLength = this.data.length;
			while (index < dataLength) {
				if (key.equals(this.data[index]))
					break;
				index += 2;
			}
			if (index < dataLength)
				return data[index + 1];
		}
		return null;
	}

	private final String pathToString(Element[] path) {
		StringBuffer _path = new StringBuffer();
		int max = path.length-1;
		for(int i=0;i<path.length;++i) {
			_path.append(path[i]);
			if(i<max)
				_path.append("///");
		}
		return _path.toString();
	}
}