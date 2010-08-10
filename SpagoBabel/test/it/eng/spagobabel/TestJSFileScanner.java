/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobabel;

import java.util.Set;

import it.eng.spagobabel.scanner.JSFileScanner;

import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class TestJSFileScanner extends TestCase {
	
    private JSFileScanner scanner;
    private Set results;
	
	public TestJSFileScanner(String name) {
		super(name);
	}

	public void setUp(){
        scanner = new JSFileScanner();
	}
	
	public void tearDown(){
		scanner = null;
	}
	
	public void test0Matches(){
        results = scanner.scan("Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.");
        assertNotNull("Results cannot be null", results);
        assertEquals("Wrong result's number", 0, results.size());
    }
	
	public void test1Matches(){
        results = scanner.scan("['AND', LN('sbi.qbe.filtergridpanel.boperators.name.and'), ");
        assertNotNull("Results cannot be null", results);
        assertEquals("Wrong result's number", 1, results.size());
    }
	
	public void test2Matches(){
        results = scanner.scan("['AND', LN('sbi.qbe.filtergridpanel.boperators.name.and'), LN('sbi.qbe.filtergridpanel.boperators.desc.and')],");
        assertNotNull("Results cannot be null", results);
        assertEquals("Wrong result's number", 2, results.size());
    }
	
	
	  
}
