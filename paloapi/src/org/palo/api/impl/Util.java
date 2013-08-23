/*
*
* @file Util.java
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
package org.palo.api.impl;

import org.palo.api.Element;

import com.tensegrity.palojava.ElementInfo;

/**
 * <code></code>
 *
 * @author Stepan Rutz
 * @version $ID$
 */
class Util {
    public static void noopWarning() {
		System.err.println("no-op");
	}

    public static final ElementInfo[] getElementInfos(Element[] elements) {
    	int index = 0;
    	ElementInfo[] infos = new ElementInfo[elements.length];    	
    	for(Element el : elements)
    		infos[index++] = ((ElementImpl)el).getInfo();
    	return infos;
    }
    
    public static final ElementInfo[][] getElementInfos(Element[][] elements) {
    	ElementInfo[][] infos = new ElementInfo[elements.length][];
    	for(int index = 0; index < elements.length; index++)
    		infos[index] = getElementInfos(elements[index]);
    	return infos;
    }
}
