/*
*
* @file RuleInfoBuilder.java
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
* @author Arnd Houben
*
* @version $Id: RuleInfoBuilder.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.RuleInfo;
import com.tensegrity.palojava.impl.RuleImpl;

/**
 * <code></code>
 * TODO DOCUMENT ME
 * 
 * @author Arnd Houben
 * @version $Id: RuleInfoBuilder.java,v 1.7 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public class RuleInfoBuilder {

	public final RuleInfo create(PaloInfo parent, String[] response) {
		if(response.length<2)
			throw new PaloException("Not enough information to create RuleInfo");
		CubeInfo cube = (CubeInfo)parent;
		String id = response[0];
		RuleImpl rule = new RuleImpl(cube,id);
		update(rule,response);
		return rule;
	}
	
	public final void update(RuleImpl rule, String[] response) {
		rule.setDefinition(response[1]);
		//set optional parameter if we have...
		switch(response.length) {
		case 6: //active
			rule.setActive(response[5].equals("1"));
		case 5: //timestamp in milliseconds!!!
			rule.setTimestamp(Long.parseLong(response[4])*1000);
		case 4: //comment 
			rule.setComment(response[3]);			
		case 3: //external_identifier 
			rule.setExternalIdentifier(response[2]);
		}
	}
}
