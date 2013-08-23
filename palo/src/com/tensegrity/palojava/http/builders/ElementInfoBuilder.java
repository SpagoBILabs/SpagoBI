/*
*
* @file ElementInfoBuilder.java
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
* @version $Id: ElementInfoBuilder.java,v 1.5 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.DimensionInfo;
import com.tensegrity.palojava.ElementInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.impl.ElementInfoImpl;


public class ElementInfoBuilder {
	
	ElementInfoBuilder() {
		//package visibility only...
	}

	public final ElementInfo create(PaloInfo parent,String[] response) {
		if (response.length < 12) {
			throw new PaloException(getExceptionMessage(
					"Not enough information to create ElementInfo", response));
		}
		try {
			String id = response[0];
			
			ElementInfoImpl info = 
				new ElementInfoImpl((DimensionInfo)parent,id);
			update(info,response);
			return info;
		}catch(RuntimeException e) {
			throw new PaloException(e.getLocalizedMessage(),e);
		}
	}

	public final void update(ElementInfoImpl element, String[] response) {
		if(response.length<12) {
			throw new PaloException(getExceptionMessage(
					"Not enough information to update ElementInfo", response));
		}

		String name = response[1];
		int position = Integer.parseInt(response[2]);
		int level = Integer.parseInt(response[3]);
		int indent = Integer.parseInt(response[4]);
		int depth = Integer.parseInt(response[5]);
		int type = Integer.parseInt(response[6]);
//		int parentCount = Integer.parseInt(response[7]);
		String[] parentIds = BuilderUtils.getIDs(response[8]);
		int childrenCount = Integer.parseInt(response[9]);
		String[] childrenIds;
		double[] weights;
		if (childrenCount == 0) {
			childrenIds = new String[0];
			weights = new double[0];
		} else {
			childrenIds = BuilderUtils.getIDs(response[10]);
			weights = BuilderUtils.getWeights(response[11]);
		}
		element.setName(name);
		element.setType(type);
		element.setPosition(position);
		element.setLevel(level);
		element.setIndent(indent);
		element.setDepth(depth);
//		element.setParentCount(parentCount);
		element.setParents(parentIds);
//		element.setChildrenCount(childrenCount);
		element.setChildren(childrenIds,weights);
	}
	
	private final String getExceptionMessage(String message, String[] response) {
		StringBuffer msg = new StringBuffer();
		msg.append(message);
		if(response.length>= 2) {
			msg.append(" '");
			msg.append(response[1]);
			msg.append("' (id: ");
			msg.append(response[0]);
			msg.append(")");
		}
		msg.append("!!");
		return msg.toString();
	}
}
