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
package it.eng.spagobi.engines.qbe.crosstable;

import junit.framework.TestCase;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class TestCrosstab extends TestCase{

	private CrossTab cs;
	
	protected void setUp() throws Exception {
		super.setUp();
		cs = new CrossTab();
		Node root = new Node("Root");
		root.buildSubTree(1, 2);
		cs.dataMatrix = buildMatrix(2, 16);

		System.out.println("");
	}
	
	public void doTests() {
		//cs.calculateCF("field[0]+field[1]+(7*field[1])", root, true, 1, "A+B");
		
	}
	
	/**
	 * TEST
	 * @param rows
	 * @param columns
	 * @return
	 */
	private static String[][] buildMatrix(int rows, int columns){
		String[][] m = new String[rows][columns];
		for(int i=0; i<rows; i++){
			for(int j=0; j<columns; j++){
				m[i][j]=""+i;
			}
		}
		return m;
	}
}
