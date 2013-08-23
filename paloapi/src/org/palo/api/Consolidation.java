/*
*
* @file Consolidation.java
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
* @version $Id: Consolidation.java,v 1.7 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2005. All rights reserved.
 */
package org.palo.api;

/**
 * <code>Consolidation</code>. <p>A instance of this class represents
 * a consolidation of a PALO {@link org.palo.api.Element}.
 * </p>
 * <p>
 * <code>Consolidation</code>s can be constructed by invoking
 * {@link org.palo.api.Dimension#newConsolidation(Element, Element, double)}
 * and are also returned from consolidated elements when calling
 * {@link org.palo.api.Element#getConsolidations()} or
 * {@link org.palo.api.Element#getConsolidationAt(int)}.
 * </p>
 *
 * @author Stepan Rutz
 * @version $ID$
 * 
 * @see org.palo.api.PaloAPIException
 */
public interface Consolidation
{
    /**
     * Returns the parent {@link Element} of this consolidation.
     * @return the parent {@link Element} of this consolidation.
     */
    Element getParent();
    
    /**
     * Returns the child {@link Element} of this consolidation.
     * @return the child {@link Element} of this consolidation.
     */
    Element getChild();
    
    /**
     * The consolidation weight. This weight is used as a factor on the 
     * element's value when the consolidated value is computed.
     * @return the consolidation weight.
     */
    double getWeight();
}
