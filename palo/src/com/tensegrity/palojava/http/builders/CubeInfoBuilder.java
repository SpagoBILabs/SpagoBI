/*
*
* @file CubeInfoBuilder.java
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
* @version $Id: CubeInfoBuilder.java,v 1.6 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

package com.tensegrity.palojava.http.builders;

import java.math.BigInteger;

import com.tensegrity.palojava.CubeInfo;
import com.tensegrity.palojava.DatabaseInfo;
import com.tensegrity.palojava.PaloException;
import com.tensegrity.palojava.PaloInfo;
import com.tensegrity.palojava.impl.CubeInfoImpl;

public class CubeInfoBuilder {

	CubeInfoBuilder() {
		//package visibility only...
	}

	public final CubeInfo create(PaloInfo parent,String[] response) {
		if(response.length<8)
			throw new PaloException("CubeInfoBuilder: not enough information to create CubeInfo!!");
		
//		try {
			
			String id = response[0];			
			String[] dims = BuilderUtils.getIDs(response[3]);
			int type = Integer.parseInt(response[7]);
			CubeInfoImpl info = new CubeInfoImpl((DatabaseInfo)parent,id,type,dims);
			update(info,response);
			return info;
//		} catch(RuntimeException e) {
//			throw new PaloException(e.getLocalizedMessage(),e);
//		}
	}
	
	public final void update(CubeInfoImpl cube, String[] response) {
		if(response.length<8)
			throw new PaloException("Not enough information to update CubeInfo!!");
		
		try {
			String name = response[1];
			int dimCount = Integer.parseInt(response[2]);
			BigInteger cellCount = new BigInteger(response[4]);
			BigInteger filledCellCount = new BigInteger(response[5]);
//			int cellCount = Integer.parseInt(response[4]);
//			int filledCellCount = Integer.parseInt(response[5]);
			int status = Integer.parseInt(response[6]);
			int token = Integer.parseInt(response[8]);
			//now set the rest:
			cube.setName(name);
			cube.setDimensionCount(dimCount);
			cube.setCellCount(cellCount);
			cube.setFilledCellCount(filledCellCount);
			cube.setStatus(status);
			cube.setToken(token);
		} catch(RuntimeException e) {
			throw new PaloException(e.getLocalizedMessage(),e);
		}
	}
}
