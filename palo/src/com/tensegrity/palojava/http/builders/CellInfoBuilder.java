/*
*
* @file CellInfoBuilder.java
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
* @version $Id: CellInfoBuilder.java,v 1.9 2010/02/16 09:18:53 PhilippBouillon Exp $
*
*/

/*
 * (c) 2007 Tensegrity Software GmbH
 * All rights reserved
 */
package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.CellInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.impl.CellInfoImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author ArndHouben
 * @version $Id: CellInfoBuilder.java,v 1.9 2010/02/16 09:18:53 PhilippBouillon Exp $
 */
public class CellInfoBuilder {

	CellInfoBuilder() {
		//package visibility only...
	}
	
	public CellInfo create(PaloInfo parent, String[] response) {
		if (response.length < 3) {
//			// workaround for palo bug: Simply return null here, and filter
//			// the cell out later...
//			return null;
			throw new PaloException(
					"Not enough information to create CellInfo!!");
		}

		int type = Integer.parseInt(response[0]);
		boolean exists = Boolean.getBoolean(response[1]);
		Object value = response[2];
		if (type == CellInfo.TYPE_NUMERIC && !value.equals(""))
			value = new Double(response[2]);
		CellInfoImpl cell = new CellInfoImpl(type, exists, value);
		setCoordinate(cell, response);
		setRuleId(cell, response);
		return cell;
	}

	private final void setCoordinate(CellInfoImpl cell, String[] response) {
		if(response.length > 3) {
			//coordinate is at third position
			String[] pathIds = BuilderUtils.getIDs(response[3]);
			//a cell has at least 2 coordinate components:
			if(pathIds.length > 1)
				cell.setCoordinate(pathIds);
		}
	}
	private final void setRuleId(CellInfoImpl cell, String[] response) {
		//we have a rule id only on response length greater or equal to 4
		if(response.length > 3) {
			//rule id is either at 3
			String ruleId = getRuleId(response[3]);
			if(ruleId == null && response.length > 4)
				ruleId = getRuleId(response[4]); //or at 4
			if(ruleId != null)
				cell.setRule(ruleId);
		}
	}
	private final String getRuleId(String response) {
		String[] id = BuilderUtils.getIDs(response);
		if(id.length == 1)
			return id[0];
		return null;
	}
}
