/*
*
* @file PaloObject.java
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
* @version $Id: PaloObject.java,v 1.7 2010/02/22 11:38:55 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api;


/**
 * <code>PaloObject</code> 
 * <p>
 * A palo object extends the {@link NamedEntity} interface by adding an 
 * identifier to it. This identifier could be used to reference the palo object 
 * within the scope of its parent.
 * </p> 
 * <p><b>NOTE:</b>
 * The id is only unique within the scope of the parent of this palo object, 
 * e.g. an {@link Element} id is only unique within its {@link Dimension} but 
 * not {@link Database} wide. Furthermore note that an id could be reused that 
 * means, in case of a deletion of a palo object its corresponding id could 
 * later be assigned to a newly created one!
 * </p>
 * 
 * @author ArndHouben
 * @version $Id: PaloObject.java,v 1.7 2010/02/22 11:38:55 PhilippBouillon Exp $
 */
public interface PaloObject extends NamedEntity, Writable {

	/* some predefined type constants... */
	public static final int TYPE_NORMAL = 1 << 1;
	public static final int TYPE_SYSTEM = 1 << 2;
	public static final int TYPE_ATTRIBUTE = 1 << 3;
	public static final int TYPE_USER_INFO = 1 << 4;
	public static final int TYPE_GPU = 1 << 5;
	
	/**
	 * Returns the unique identifier of this palo object. 
	 * <p><b>Important note:</b> for a correct usage please note that the 
	 * returned id is only unique within the parent scope of this palo object. 
	 * Furthermore in case of deletion the id is reused, i.e. if a palo object 
	 * is removed its id could be given to a newly created palo object afterwards!!
	 * </p>
	 * @return id of the palo object.
	 */
	String getId();
	
	/**
	 * TODO please comment :)
	 * 
	 * @return the type of this palo object
	 */
	int getType();

}
