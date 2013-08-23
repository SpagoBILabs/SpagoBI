/*
*
* @file SubsetReaderErrorHandler.java
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
* @version $Id: SubsetReaderErrorHandler.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.subsets.io;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <code>SubsetReaderErrorHandler</code>
 * Implementation of the {@link ErrorHandler} interface which handles all errors
 * and warnings which occur during parsing of subset xml definition 
 *
 * @author ArndHouben
 * @version $Id: SubsetReaderErrorHandler.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
class SubsetReaderErrorHandler implements ErrorHandler {

	private boolean isValid = true;
	
	public final void error(SAXParseException exception) throws SAXException {
		//PR 7075: we ignore wrong values for parameter values...
		//since we cannot ask exception what went wrong we have to rely on
		//message parsing :(
		String msg = exception.getMessage();
		int valIndex = msg.indexOf("''"); 	//search for empty values...
		if(valIndex > 0) {
			if((msg.indexOf("'boolean'", valIndex) > 0) 
				|| (msg.indexOf("'integer'", valIndex) > 0) 
				|| (msg.indexOf("'decimal'", valIndex) > 0)
				|| (msg.indexOf("'string'", valIndex) > 0)
				|| (msg.indexOf("'value'", valIndex) > 0))
				return; //* ignore *
		}
		
		isValid = false;
		System.err.println("SUBSET READER ERROR_MSG: " + exception.getMessage());
	}

	public final void fatalError(SAXParseException exception) throws SAXException {
		isValid = false;
//		System.err.println("SUBSET READER FATAL ERROR_MSG: " 
//				+ exception.getMessage());
	}

	public final void warning(SAXParseException exception) throws SAXException {
		System.err.println("SUBSET READER WARNING_MSG: " + exception.getMessage());
	}
	
	/**
	 * Checks if any errors or fatal errors occurred during subset parsing 
	 * @return <code>true</code> if any errors occurred during parsing, 
	 * <code>false</code> otherwise
	 */
	final boolean hasErrors() {
		return !isValid;
	}
}
