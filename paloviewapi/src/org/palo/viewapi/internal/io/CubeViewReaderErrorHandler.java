/*
*
* @file CubeViewReaderErrorHandler.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: CubeViewReaderErrorHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal.io;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <code>CubeViewReaderErrorHandler</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: CubeViewReaderErrorHandler.java,v 1.2 2009/12/17 16:14:08 PhilippBouillon Exp $
 **/
public class CubeViewReaderErrorHandler implements ErrorHandler {

	private boolean isValid = true;
	
	public final void error(SAXParseException exception) throws SAXException {
		isValid = false;
		System.err.println("CUBE VIEW READER ERROR_MSG: " + exception.getMessage());
	}

	public final void fatalError(SAXParseException exception) throws SAXException {
		isValid = false;
		System.err.println("CUBE VIEW READER FATAL ERROR_MSG: " 
				+ exception.getMessage());
	}

	public final void warning(SAXParseException exception) throws SAXException {
		System.err.println("CUBE VIEW READER WARNING_MSG: " + exception.getMessage());
	}
	
	/**
	 * Checks if any errors or fatal errors occurred during parsing of cube view 
	 * @return <code>true</code> if any errors occurred during parsing, 
	 * <code>false</code> otherwise
	 */
	final boolean hasErrors() {
		return !isValid;
	}
}
