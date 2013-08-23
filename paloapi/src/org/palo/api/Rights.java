/*
*
* @file Rights.java
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
* @author PhilippBouillon
*
* @version $Id: Rights.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

package org.palo.api;

/**
 *
 * @author PhilippBouillon
 * @deprecated Do not use, subject to change.
 */
public interface Rights {
	boolean maySplash(PaloObject object);	
	boolean mayDelete(PaloObject object);
	boolean mayWrite(PaloObject object);
	boolean mayRead(PaloObject object);
	
	boolean maySplash(Class <? extends PaloObject> object);
	boolean mayDelete(Class <? extends PaloObject> object);
	boolean mayWrite(Class <? extends PaloObject> object);
	boolean mayRead(Class <? extends PaloObject> object);
	
	void allowSplash(String group, PaloObject object);
	void allowDelete(String group, PaloObject object);
	void allowWrite(String group, PaloObject object);
	void allowRead(String group, PaloObject object);
	void preventAccess(String group, PaloObject object);
	
	void allowSplash(String role, Class <? extends PaloObject> object);
	void allowDelete(String role, Class <? extends PaloObject> object);
	void allowWrite(String role, Class <? extends PaloObject> object);
	void allowRead(String role, Class <? extends PaloObject> object);
	void preventAccess(String role, Class <? extends PaloObject> object);	
}
