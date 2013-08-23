/*
*
* @file AbstractPaloObject.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: AbstractPaloObject.java,v 1.3 2010/02/22 11:38:55 PhilippBouillon Exp $
*
*/

package org.palo.api.impl;

import org.palo.api.PaloObject;

import com.tensegrity.palojava.PaloConstants;
import com.tensegrity.palojava.PaloInfo;

abstract class AbstractPaloObject implements PaloObject {

	protected final int getType(PaloInfo info) {
		switch (info.getType()) {
		case PaloConstants.TYPE_ATTRIBUTE:
			return TYPE_ATTRIBUTE;
		case PaloConstants.TYPE_INFO:
			return TYPE_USER_INFO;
		case PaloConstants.TYPE_NORMAL:
			return TYPE_NORMAL;
		case PaloConstants.TYPE_SYSTEM:
			return TYPE_SYSTEM;
		case PaloConstants.TYPE_GPU:
			return TYPE_GPU;
		}
		return -1;
	}
	
	protected final int getInfoType(int type) {
		switch(type) {
		case TYPE_ATTRIBUTE:
			return PaloConstants.TYPE_ATTRIBUTE;
		case TYPE_NORMAL:
			return PaloConstants.TYPE_NORMAL;
		case TYPE_SYSTEM:
			return PaloConstants.TYPE_SYSTEM;
		case TYPE_USER_INFO:
			return PaloConstants.TYPE_INFO;
		case TYPE_GPU:
			return PaloConstants.TYPE_GPU;
		}
		return -1;
	}
}
