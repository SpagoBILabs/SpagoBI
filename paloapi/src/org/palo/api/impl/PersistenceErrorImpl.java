/*
*
* @file PersistenceErrorImpl.java
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
* @version $Id: PersistenceErrorImpl.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl;

import org.palo.api.persistence.PersistenceError;


/**
 * <code>PersistenceErrorImpl</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: PersistenceErrorImpl.java,v 1.4 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
public class PersistenceErrorImpl implements PersistenceError {
	
	private final Object src; 
	private final String msg;
	private final String srcId;
	private final Object location;
	private final String cause;
	private final int type;
	private final Object section;
	private final int sectionType;
	
	public PersistenceErrorImpl(String msg, String srcId, Object src, Object location, String cause, int type, Object section, int sectionType) {
		this.msg = msg;
		this.src = src;
		this.srcId = srcId;
		this.cause = cause;
		this.location = location;
		if(!typeIsOk(type))
			type = UNKNOWN_ERROR;
		this.type = type;
		this.section = section;
		this.sectionType = sectionType;
	}

	public final String getSourceId() {
		return srcId;
	}
	
	public final String getCause() {
		return cause;
	}
	
	public final Object getLocation() {
		return location;
	}

	public final String getMessage() {
		return msg;
	}

	public final int getType() {
		return type;
	}

	public final Object getSource() {
		return src;
	}
	
	public final Object getSection() {
		return section;
	}
	
	public int getTargetType() {
		return sectionType;
	}

	
	private final boolean typeIsOk(int type) {
		for(int i=0;i<ALL_ERROR_TYPES.length;++i)
			if(type == ALL_ERROR_TYPES[i])
				return true;
		return false;
	}
}
