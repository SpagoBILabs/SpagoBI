/*
*
* @file AxisFlatModel.java
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
* @version $Id: AxisFlatModel.java,v 1.5 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.uimodels.axis;

import java.util.ArrayList;

import org.palo.api.Hierarchy;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;


/**
 * <code>AxisFlatModel</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: AxisFlatModel.java,v 1.5 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public class AxisFlatModel extends AxisTreeModel {
	
	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;
	
	private static final int UNDEFINED = -1;
	
	private final int orientation;
	private AxisItem[][] model;
	private final boolean reversed;
	
	/**
	 * Creates a flat model with horizontal orientation.
	 * @param axis
	 */
	public AxisFlatModel(Axis axis, boolean isReversed) {
		this(axis, HORIZONTAL, isReversed);
	}
	
	/**
	 * Creates a flat model with the given orientation. The orientation has to
	 * be either {@link #HORIZONTAL} or {@link #VERTICAL}. Specifying 
	 * <code>HORIZONTAL</code> TODO comment...
	 * If a <code>VERTICAL</code> orientation is specified TODO comment...
	 * @param axis
	 * @param orientation
	 */
	public AxisFlatModel(Axis axis, int orientation, boolean isReversed) {
		super(axis);
		model = init(orientation, isReversed);
		this.orientation = orientation;
		this.reversed = isReversed;
	}

	/**
	 * @return the flat model
	 */
	public final AxisItem[][] getModel() {
		return model;
	}
	
	protected final void notifyExpanded(AxisItem source, AxisItem[][] items) {
		int hierIndex = getIndex(source.getHierarchy());
		int hierCount = getAxisHierarchyCount() - hierIndex;
		if (hierIndex != UNDEFINED) {
			//get flatten delta...
			ArrayList<AxisItem>[] delta = getDelta(hierCount, items);
			//adjust items:
			items = new AxisItem[delta.length][];
			for(int i=0;i<delta.length;++i)
				items[i] = delta[i].toArray(new AxisItem[0]);

			// finally adjust the model:
			if (isHorizontal())
				expandHorizontal(source, hierIndex, items);
			else
				expandVertical(source, hierIndex, items);

		}
		super.notifyExpanded(source, items);
	}
	
	protected final void notifyCollapsed(AxisItem source, AxisItem[][] items) {
		int hierIndex = getIndex(source.getHierarchy());
		int hierCount = getAxisHierarchyCount() - hierIndex;
		if (hierIndex != UNDEFINED) {
			//get flatten delta...
			ArrayList<AxisItem>[] delta = getDelta(hierCount, items);
			//adjust items:
			items = new AxisItem[delta.length][];
			for(int i=0;i<delta.length;++i)
				items[i] = delta[i].toArray(new AxisItem[0]);
			
			if(isHorizontal())
				collapseHorizontal(hierIndex, items);
			else
				collapseVertical(source, hierIndex, items);
		}
		super.notifyCollapsed(source,items);
	}

	protected void notifyStructureChange() {
		long t0 = System.currentTimeMillis();
		//do a complete rebuild:
		model = init(orientation, reversed);
		long t1 = System.currentTimeMillis();
		System.err.println("AxisFlatModel#notifyStructureChange(): "+(t1-t0)+"ms");
		super.notifyStructureChange();
	}
	
	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final AxisItem[][] init(int orientation, boolean isReversed) {
		if (isReversed) {
			return reverseInit(orientation);
		} else {
			return normalInit(orientation);
		}
	}
	
	private final AxisItem[][] reverseInit(int orientation) {
		AxisItem[][] model = null;
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();		
		final ArrayList<AxisItem>[] items = new ArrayList[hierarchies.length];
		// we need a visitor:
		AxisTreeFlattenVisitor visitor = new AxisTreeFlattenVisitor() {
			public final void visit(AxisItem item, int dimIndex) {
				if (items[dimIndex] == null)
					items[dimIndex] = new ArrayList<AxisItem>();
				items[dimIndex].add(item);
			}
		};

		AxisItem[] roots = getRoots();
		for (AxisItem root : roots) {
			reverseFlatten(root, 0, visitor);
		}
		if (orientation == HORIZONTAL) {
			model = new AxisItem[hierarchies.length][];
			for (int i = 0; i < model.length; ++i)
				model[i] = items[i] != null ? items[i].toArray(new AxisItem[0])
						: new AxisItem[0];
		} else {
			int lastDim = items.length - 1;
			int lastDimSize = items[lastDim].size();
			model = new AxisItem[lastDimSize][hierarchies.length];
			for (int i = 0; i < lastDimSize; ++i) {
				AxisItem item = items[lastDim].get(i);
				model[i][lastDim] = item;
				// fill previous dims:
				AxisItem parent;
				int prevDim = lastDim - 1;
				while ((parent = item.getParentInPrevHierarchy()) != null) {
					model[i][prevDim] = parent;
					item = parent;
					prevDim--;
				}
			}
		}
		return model;		
	}
	
	private final AxisItem[][] normalInit(int orientation) {
		AxisItem[][] model = null;
		AxisHierarchy[] hierarchies = axis.getAxisHierarchies();		
		final ArrayList<AxisItem>[] items = new ArrayList[hierarchies.length];
		// we need a visitor:
		AxisTreeFlattenVisitor visitor = new AxisTreeFlattenVisitor() {
			public final void visit(AxisItem item, int dimIndex) {
				if (items[dimIndex] == null)
					items[dimIndex] = new ArrayList<AxisItem>();
				items[dimIndex].add(item);
			}
		};

		AxisItem[] roots = getRoots();
		for (AxisItem root : roots) {
			flatten(root, 0, visitor);
		}
		if (orientation == HORIZONTAL) {
			model = new AxisItem[hierarchies.length][];
			for (int i = 0; i < model.length; ++i)
				model[i] = items[i] != null ? items[i].toArray(new AxisItem[0])
						: new AxisItem[0];
		} else {
			int lastDim = items.length - 1;
			int lastDimSize = items[lastDim].size();
			model = new AxisItem[lastDimSize][hierarchies.length];
			for (int i = 0; i < lastDimSize; ++i) {
				AxisItem item = items[lastDim].get(i);
				model[i][lastDim] = item;
				// fill previous dims:
				AxisItem parent;
				int prevDim = lastDim - 1;
				while ((parent = item.getParentInPrevHierarchy()) != null) {
					model[i][prevDim] = parent;
					item = parent;
					prevDim--;
				}
			}
		}
		return model;		
	}
	
	/**
	 * Traverses hierarchy first, then calls the visitor and finally traverses
	 * the items children.
	 */
	private final void flatten(AxisItem item, int hierIndex,
			AxisTreeFlattenVisitor visitor) {
		//go down dimensions...
		if (item.hasRootsInNextHierarchy()) {
			AxisItem[] rootsInNextHier = item.getRootsInNextHierarchy();
			for (AxisItem root : rootsInNextHier)
				flatten(root, hierIndex + 1, visitor);
		}
		//visit item
		visitor.visit(item, hierIndex);		
		//visit its children...
		if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
			AxisItem[] children = item.getChildren();
			for (AxisItem child : children)
				flatten(child, hierIndex, visitor);
		}
	}

	private final void reverseFlatten(AxisItem item, int hierIndex,
			AxisTreeFlattenVisitor visitor) {
		//visit item's children...
		if (item.hasChildren() && item.hasState(AxisItem.EXPANDED)) {
			AxisItem[] children = item.getChildren();
			for (AxisItem child : children)
				reverseFlatten(child, hierIndex, visitor);
		}
		//go down dimensions...
		if (item.hasRootsInNextHierarchy()) {
			AxisItem[] rootsInNextHier = item.getRootsInNextHierarchy();
			for (AxisItem root : rootsInNextHier) {
				reverseFlatten(root, hierIndex + 1, visitor);
			}				
		}
		//visit item
		visitor.visit(item, hierIndex);				
	}

	private final int getIndex(Hierarchy hierarchy) {
		Hierarchy[] hierarchies = getHierarchies();
		for(int i=0; i<hierarchies.length;++i) {
			if(hierarchies[i].equals(hierarchy))
				return i;
		}
		return UNDEFINED;
	}
	
	private final int getIndex(AxisItem item, int inHierarchy) {
		if (inHierarchy < 0)
			return inHierarchy;

		int index = UNDEFINED;
		String itemPath = item.getPath();
		if (isHorizontal()) {
			for (int i = 0; i < model[inHierarchy].length; ++i) {
				if (model[inHierarchy][i].getPath().equals(itemPath)) {
					index = i;
					break;
				}
			}
		} else {
			// vertical
			for (int i = 0; i < model.length; ++i) {
				if (model[i][inHierarchy].getPath().equals(itemPath)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
	private final ArrayList<AxisItem>[] getDelta(int hierCount, AxisItem[][] items) {
		// to get the delta we have to flatten the affected items...
		final ArrayList<AxisItem>[] delta = new ArrayList[hierCount];
		if (items != null && items.length > 0) {
			AxisTreeFlattenVisitor visitor = new AxisTreeFlattenVisitor() {
				public final void visit(AxisItem item, int dimIndex) {
					if (delta[dimIndex] == null)
						delta[dimIndex] = new ArrayList<AxisItem>();
					delta[dimIndex].add(item);
				}
			};
			// items should be only a 1 dimensional one...
			if (reversed) {
				for (AxisItem child : items[0])
					reverseFlatten(child, 0, visitor);				
			} else {
				for (AxisItem child : items[0])
					flatten(child, 0, visitor);
			}
		}
		return delta;
	}
	
	private final boolean isHorizontal() {
		return orientation == HORIZONTAL;
	}
	
	private final void expandHorizontal(AxisItem item, int hierIndex,
			AxisItem[][] delta) {
		int index = 0;
		while (item != null && index < delta.length) {
			// insert into model:
			// int deltaSize = delta[index].size();
			int modelIndex = hierIndex + index;
			int startIndex = getIndex(item, modelIndex) + 1;
			AxisItem[] newModel = 
				new AxisItem[model[modelIndex].length + delta[index].length];
			// copy everything from old model up to item (inclusive)
			System.arraycopy(model[modelIndex], 0, newModel, 0, startIndex);
			// copy the new items behind item
			System.arraycopy(delta[index], 0, newModel, startIndex,
					delta[index].length);
			// the rest?
			int endIndex = startIndex + delta[index].length;
			if (endIndex < newModel.length) {
				// and copy the rest from old source
				System.arraycopy(model[modelIndex], startIndex, newModel,
						endIndex, model[modelIndex].length - startIndex);
			}
			model[modelIndex] = newModel;
			index++;
			item = getLastChildInNextHierarchy(item);
		}
	}
	private final void expandVertical(AxisItem item, int hierIndex, 
			AxisItem[][] delta) {
		int hierCount = getAxisHierarchyCount();
		int srcIndex = getIndex(item, hierIndex);
		int lastDim = delta.length - 1;
//		int lastDimSize = delta[lastDim].size();
		int offset = srcIndex + delta[lastDim].length;
		AxisItem[][] newModel = 
			new AxisItem[delta[lastDim].length + model.length][hierCount];
		// we first have to copy existing model into new one...
		for (int i = 0; i < model.length; ++i) {
			if (i <= srcIndex)
				newModel[i] = model[i];
			else
				newModel[i + offset] = model[i];
		}
		// now insert delta into vertical model:
		for (int i = 0; i < delta[lastDim].length; ++i) {
//			item = delta[lastDim].get(i);
			item = delta[lastDim][i];
			newModel[srcIndex+ 1 + i][lastDim] = item;
			// fill previous dims:
			AxisItem parent;
			int prevDim = lastDim - 1;
			while ((parent = item.getParentInPrevHierarchy()) != null) {
				newModel[i][prevDim] = parent;
				item = parent;
				prevDim--;
			}
		}
		// and finally change model:
		model = newModel;
	}
	
	private final void collapseHorizontal(int hierIndex,
			AxisItem[][] delta) {
		// now insert delta and adjust items:
		for (int i = 0, n = (model.length - hierIndex); i < n; ++i) {
			int modelIndex = hierIndex + i;
//			int deltaSize = delta[i].size();
			AxisItem[] newModel = 
				new AxisItem[model[modelIndex].length - delta[i].length];
			int startIndex = getIndex(delta[i][0], modelIndex);
			// copy everything from old model up to source (inclusive)
			System.arraycopy(model[modelIndex], 0, newModel, 0, startIndex);
			// the rest?
			int endIndex = 
				getIndex(delta[i][delta[i].length - 1], modelIndex) + 1;
			if (endIndex < model[modelIndex].length) {
				// and copy the rest from old source
				System.arraycopy(model[modelIndex], endIndex, newModel,
						startIndex, model[modelIndex].length - endIndex);
			}
			model[modelIndex] = newModel;
		}
	}
	
	private final void collapseVertical(AxisItem item, int hierIndex,
			AxisItem[][] delta) {
		int srcIndex = getIndex(item, hierIndex);
		int lastDim = delta.length - 1;
//		int lastDimSize = delta[lastDim].size();
		int offset = srcIndex + delta[lastDim].length;
		AxisItem[][] newModel = 
			new AxisItem[model.length - delta[lastDim].length][];
		// we first have to copy existing model into new one...
		for (int i = 0; i < model.length; ++i) {
			if (i <= srcIndex)
				newModel[i] = model[i];
			else
				newModel[i + offset] = model[i];
		}
//		// now insert delta into vertical model:
//		for (int i = 0; i < lastDimSize; ++i) {
//			item = delta[lastDim].get(i);
//			newModel[i][lastDim] = item;
//			// fill previous dims:
//			AxisItem parent;
//			int prevDim = lastDim - 1;
//			while ((parent = item.getParentInPrevHierarchy()) != null) {
//				newModel[i][prevDim] = parent;
//				item = parent;
//				prevDim--;
//			}
//		}
		// and finally change model:
		model = newModel;
	}
	
//	
// private final void dump(AxisItem[] model) {
//		System.out.println("====== DUMP ======");
//		if(model != null) {
//		for(int i=0;i<model.length;++i)
//			System.out.println(i+" - " + (model[i] == null ? "null" : model[i].getElement().getName()));
//		}
//		System.out.println("==================");
//	}
}

interface AxisTreeFlattenVisitor {
	public void visit(AxisItem item, int hierIndex);		
}
