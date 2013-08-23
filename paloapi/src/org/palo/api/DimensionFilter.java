/*
*
* @file DimensionFilter.java
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

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

/**
 * A <code>DimensionFilter</code> is used to decide which <code>Element</code>s
 * of a base <code>Dimension</code> are visible in its associated virtual
 * dimension. 
 *
 * @author Stepan Rutz
 * @version $ID$
 * @deprecated use {@link HierarchyFilter} instead.
 */
public interface DimensionFilter
{
    /**
     * Inits the filter and passes a reference to the virtual dimension
     * owning the filter.
     * @param dimension the owning virtual dimension of the filter.
     */
    void init(Dimension dimension);
    
    /**
     * Return <code>true</code> if the given {@link Element} passes
     * the filter, otherwise return <code>false</code>.
     * @param element the {@link Element} that is being filtered.
     * @return <code>true</code> to pass, <code>false</code> to filter out.
     */
    boolean acceptElement(Element element);
    
    /**
     * Turns this dimension into a flat dimension loosing and hierarchies
     * and consolidations.
     * @return <code>true</code> if the dimension is flat.
     */
    boolean isFlat();
    
    /**
     * If and only if the dimension is flat, this method can be used
     * to post-process the root element nodes.
     * 
     * @param rootNodes the original root nodes.
     * @return a new array of root nodes or <code>null</code> if the 
     * original root nodes should be used.
     */
    ElementNode[] postprocessRootNodes(ElementNode rootNodes[]);

}
