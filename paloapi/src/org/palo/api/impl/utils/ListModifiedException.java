/*
*
* @file ListModifiedException.java
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
* @author S. Rutz
*
* @version $Id: ListModifiedException.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Copyright 2002 Tensegrity Software GmbH
 * All Rights Reserved.
 */
package org.palo.api.impl.utils;

/**
 * {@<describe>}
 * <p>
 * Thrown if an <code>ArrayList</code> iterator detects that someone else has 
 * concurrently modified a container. The concurrent modification checking is 
 * fail-fast and not synchronized. Thus it is not reliable.
 * </p>
 * {@</describe>}
 * 
 * @author  S. Rutz
 * @version $Id: ListModifiedException.java,v 1.2 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
public class ListModifiedException extends RuntimeException
{
    /**
     * Constructs a <code>ListModified</code> exception.
     */
    public ListModifiedException ()
    {
        super ("array modified concurrently while iterating over contents");
    }

    /**
     * Constructs an ArrayListModified exception.
     * @param what a detailed message for the exception.
     */
    public ListModifiedException (String what)
    {
        super (what);
    }
}
