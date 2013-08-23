/*
*
* @file Right.java
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
* @version $Id: Right.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>Right</code>
 * <p>
 * This enumeration defines all rights which are used within this api.
 * Currently the rights are topologically sorted, like this
 * {@link #GRANT} > {@link #CREATE} > {@link #DELETE} > {@link #WRITE} > 
 * {@link #READ} > {@link #NONE}.
 * This means that the higher right includes all lower rights, e.g. the 
 * permission {@link #WRITE} includes the rights {@link #READ} and {@link #NONE}.
 *  
 * </p>
 *
 * @author ArndHouben
 * @version $Id: Right.java,v 1.3 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public enum Right {
	NONE(0, "N"),
	READ(1, "R"),
	WRITE(2, "W"),
	DELETE(4, "D"),
	CREATE(8, "C"),
	GRANT(16, "G");

	private static final Map<String, Right> stringToRight = 
			new HashMap<String, Right>();
	static {
		for(Right right : values())
			stringToRight.put(right.toString(), right);
	}
	
	private final String tag;
	private final int priority;
	
	private Right(int priority, String tag) {
		this.tag = tag;
		this.priority = priority;
	}
	
	/**
	 * Returns the internally used right priority.
	 * @return the internally used right priority
	 */
	public final int getPriority() {
		return priority;
	}
	
	public final String toString() {
		return tag;
	}
	
	/**
	 * Returns the <code>Right</code> instance which corresponds to the given
	 * right string or <code>null</code> if the string is not valid. A valid 
	 * string for a certain <code>Right</code> is return by its
	 * {@link #toString()} method.  
	 * @param right a valid <code>Right</code> string
	 * @return the corresponding <code>Right</code> instance
	 */
	public static final Right fromString(String right) {
		return stringToRight.get(right);
	}
}
