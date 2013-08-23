/*
*
* @file DatabaseInfoBuilder.java
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
* @version $Id: DatabaseInfoBuilder.java,v 1.5 2010/01/29 09:33:31 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.http.builders;

import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.impl.DatabaseInfoImpl;


public class DatabaseInfoBuilder  {

	DatabaseInfoBuilder() {
		//package visibility only...
	}

	public final DatabaseInfo create(PaloInfo parent,String[] response) {
		if(response.length<6)
			throw new PaloException("Not enough information to create DatabaseInfo!!");
		
//		try {
			String id = response[0];
			int type = Integer.parseInt(response[5]);
			DatabaseInfoImpl info = new DatabaseInfoImpl(id,type);
			update(info,response);
			return info;
//		}catch(RuntimeException e) {
//			throw new PaloException(e.getLocalizedMessage(),e);
//		}
	}
	
	public final void update(DatabaseInfoImpl database, String[] response) {
		if(response.length == 4) {
			throw new PaloException(response[0], response[2] + ": " + response[3],
					response[2] + ": " + response[3]);
		}
		if(response.length<6)
			throw new PaloException("Not enough information to update DatabaseInfo!!");
		
		try {
			String name = response[1];
			int dimCount = Integer.parseInt(response[2]);
			int cubeCount = Integer.parseInt(response[3]);
			int status = Integer.parseInt(response[4]);
			int token = Integer.parseInt(response[6]);
			
			database.setName(name);
			database.setDimensionCount(dimCount);
			database.setCubeCount(cubeCount);
			database.setStatus(status);			
			database.setToken(token);
		}catch(RuntimeException e) {
			throw new PaloException(e.getLocalizedMessage(),e);
		}
	}
}
