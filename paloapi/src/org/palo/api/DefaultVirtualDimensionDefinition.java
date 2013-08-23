/*
*
* @file DefaultVirtualDimensionDefinition.java
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
* @author Stepan Rutz
*
* @version $Id$
*
*/

package org.palo.api;

/**
 * <code>DefaultVirtualDimensionDefinition</code>.
 *
 * @author Stepan Rutz
 * @version $ID$
 */

public class DefaultVirtualDimensionDefinition implements
		VirtualDimensionDefinition {
	
	private final DimensionFilter filter;
	private final Dimension sourceDimension;
	private final boolean isFlat;
	private final Element[] elements;
	private final ElementNode[] rootNodes;
	private final String activeSubset;
	private Hierarchy activeHierarchy;
	
	/**
	 * Creates a new <code>DefaultVirtualDimensionDefinition</code> based on the
	 * given source <code>Dimension</code> and requires a <code>DimensionFilter</code>
	 * 
	 * @param sourceDimension the <code>Dimension</code> on which this 
	 * virtual dimension is based
	 * @param filter a <code>DimensionFilter</code> 
	 */
	public DefaultVirtualDimensionDefinition(Dimension sourceDimension,
			DimensionFilter filter,String activeSubset) {
		if (sourceDimension == null)
			throw new IllegalArgumentException("sourceDimension cannot be null");
		this.sourceDimension = sourceDimension;
		this.filter = filter;
		this.isFlat = filter.isFlat();
		elements = null;
		rootNodes = null;
		this.activeSubset = activeSubset;
		activeHierarchy = sourceDimension.getDefaultHierarchy();
	}
	
	public DefaultVirtualDimensionDefinition(Dimension sourceDimension,Element[] elements, ElementNode[] rootNodes,boolean isFlat,String activeSubset) {
		this.isFlat = isFlat;
		this.elements = elements;
		this.rootNodes = rootNodes;		
		this.sourceDimension = sourceDimension;
		filter = null;
		this.activeSubset = activeSubset;
		this.activeHierarchy = sourceDimension.getDefaultHierarchy();
	}
	
	public void setActiveHierarchy(Hierarchy hier) {
		activeHierarchy = hier;
	}
	
	public Dimension getSourceDimension() {
		return sourceDimension;
	}

	public DimensionFilter getFilter() {
		return filter;
	}

	public boolean isFlat() {
		return isFlat;
	}

	public Element[] getElements() {
		return elements;
	}

	public ElementNode[] getRootElements() {
		return rootNodes;
	}
	
	public final String getActiveSubset() {
		return activeSubset;
	}
	
	public final Hierarchy getActiveHierarchy() {
		return activeHierarchy;
	}
}
