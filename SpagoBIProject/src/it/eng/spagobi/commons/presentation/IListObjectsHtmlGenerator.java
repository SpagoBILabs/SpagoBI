/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.presentation;

import it.eng.spago.paginator.basic.ListIFace;

import javax.servlet.http.HttpServletRequest;

/**
 * The interface for classes generating the HTML list objects.
 * 
 * @author sulis
 */
public interface IListObjectsHtmlGenerator {
	
	/**
	 * The interface for the <code>makeList</code> method.
	 * 
	 * @param list the interface object list at input
	 * @param httpRequest The request http
	 * @param listPage String for paging navigation
	 * 
	 * @return the string buffer with HTML code
	 */
	public StringBuffer makeList(ListIFace list, HttpServletRequest httpRequest, String listPage);
	
}
