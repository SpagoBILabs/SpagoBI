/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.utility;

import java.sql.Connection;



	// TODO: Auto-generated Javadoc
/**
	 * The Interface IDBSpaceChecker.
	 */
	public interface IDBSpaceChecker {
	
		/**
		 * Gets the percentage of free space.
		 * 
		 * @param aConnection - The sql connection provide by qbe, pay attention this class must keep the connection open
		 * 
		 * @return a numeber representing the available space for the given connection in db
		 */
		public int getPercentageOfFreeSpace(Connection aConnection);
	}
