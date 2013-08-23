/*
*
* @file LockInfoBuilder.java
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
* @version $Id: LockInfoBuilder.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.LockInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.impl.LockInfoImpl;

/**
 * <code>LockInfoBuilder</code>
 * TODO DOCUMENT ME
 *
 * @author ArndHouben
 * @version $Id: LockInfoBuilder.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 **/
public class LockInfoBuilder {

	LockInfoBuilder() {
		//package visibility only...
	}
	
	public final LockInfo create(PaloInfo parent,String[] response) {
		if(response.length<4) {
			throw new PaloException("Not enough information to create LockInfo!!");
		}
		try {
			String id = response[0];
			String user = response[2];
			LockInfoImpl lock = new LockInfoImpl(id,user);
			setArea(lock, response[1]);
			if(response[3].length()>0)
				lock.setSteps(Integer.parseInt(response[3]));
			return lock;
		}catch(RuntimeException e) {
			throw new PaloException(e.getLocalizedMessage(),e);
		}
	}
	
	private final void setArea(LockInfoImpl lock, String response) {
		String[] elements = response.split(",");
		String[][] area = new String[elements.length][];
		for(int i=0;i<area.length;++i) {
			area[i] = elements[i].split(":");
		}
		lock.setArea(area);
	}
}
