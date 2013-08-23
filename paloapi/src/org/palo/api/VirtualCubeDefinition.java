/*
*
* @file VirtualCubeDefinition.java
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
 * A so called virtual cube is used to make a certain cube view state persistent. 
 * And hence a virtual cube cannot have real dimensions instead it has virtual
 * dimensions. This is because virtual cubes and virtual dimensions are treated
 * specially by the palo server 
 * 
 * @author Stepan Rutz
 * @version $Id$
 * @deprecated please use <code>CubeView</code>s and <code>Axis</code>s to 
 * persist a certain cube state
 */
public interface VirtualCubeDefinition
{
	/**
	 * Returns the source cube of this virtual cube. The source cube is nothing
	 * else than the cube which current state is saved.
	 * @return the source <code>Cube</code>
	 */
    Cube getSourceCube();
    
    /**
     * Returns the name to use for the virtual cube
     * <b>NOTE:</b> the virtual cube name must fulfill the constraints regarding
     * a cube name, namely its uniqueness within its containing database 
     * @return the virtual cube name
     */
    String getName();
    
    /**
     * Returns the virtual dimension definitions which build up this virtual
     * cube
     * @return the <code>VirtualDimensionDefinition</code>s
     */
    VirtualDimensionDefinition[] getVirtualDimensionDefinitions();
}
