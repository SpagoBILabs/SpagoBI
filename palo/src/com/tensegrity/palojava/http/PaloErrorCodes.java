/*
*
* @file PaloErrorCodes.java
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
* @version $Id: PaloErrorCodes.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
*
*/

/**
 * (c) Copyright 2006 Tensegrity Software
 * All rights reserved.
 */
package com.tensegrity.palojava.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.tensegrity.palojava.PaloException;

/**
 * <p>
 * Registration of all known error codes which are defined by the palo server
 * </p>
 *
 * @author ArndHouben
 * @version $Id: PaloErrorCodes.java,v 1.2 2009/04/29 10:35:49 PhilippBouillon Exp $
 */
public final class PaloErrorCodes {
	
	private static final ArrayList codes = new ArrayList();
	static {
		//read in errorCodes.txt
		InputStream is = 
			PaloErrorCodes.class.getResourceAsStream("resources/errorCodes.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while((line=reader.readLine())!=null) {
				//everything between = and ',' is an error code
				int start = line.indexOf('=')+1;
				int end = line.indexOf(',');
				if(start<end) {
					String errCode = line.substring(start,end);
					codes.add(errCode.trim());
				}
			}
		} catch (IOException e) {
			throw new PaloException("Could not read in error codes!!",e);
		}
	}
	
	/**
	 * Checks if the given code is an error code
	 * @param code a possible error code
	 * @return true if the given code is an error code, false if not
	 */
	static final boolean contains(String errorCode) {
		return codes.contains(errorCode);
	}
}

