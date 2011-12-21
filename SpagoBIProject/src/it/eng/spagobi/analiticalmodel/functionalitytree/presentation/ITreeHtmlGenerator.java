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
package it.eng.spagobi.analiticalmodel.functionalitytree.presentation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * The interface for the tree html generator.
 */
public interface ITreeHtmlGenerator {

	
	/**
	 * Make tree method.
	 * 
	 * @param objectsList the objects list
	 * @param httpRequest the http request
	 * @param initialPath the initial path
	 * @param treename the treename
	 * 
	 * @return the string buffer
	 * 
	 * @see it.eng.spagobi.analiticalmodel.functionalitytree.presentation.AdminTreeHtmlGenerator#makeTree(it.eng.spago.base.SourceBean,javax.servlet.http.HttpServletRequest)
	 */
	public StringBuffer makeTree(List objectsList, HttpServletRequest httpRequest, String initialPath, String treename);
	
	/**
	 * Make tree method.
	 * 
	 * @param objectsList the objects list
	 * @param httpRequest the http request
	 * @param initialPath the initial path
	 * 
	 * @return the string buffer
	 * 
	 * @see it.eng.spagobi.analiticalmodel.functionalitytree.presentation.AdminTreeHtmlGenerator#makeTree(it.eng.spago.base.SourceBean,javax.servlet.http.HttpServletRequest)
	 */
	public StringBuffer makeTree(List objectsList, HttpServletRequest httpRequest, String initialPath);

	/**
	 * Make tree method for accessible trees.
	 * 
	 * @param objectsList the objects list
	 * @param httpRequest the http request
	 * @param initialPath the initial path
	 * 
	 * @return the string buffer
	 * 
	 * @see it.eng.spagobi.analiticalmodel.functionalitytree.presentation.AdminTreeHtmlGenerator#makeTree(it.eng.spago.base.SourceBean,javax.servlet.http.HttpServletRequest)
	 */
	public StringBuffer makeAccessibleTree(List objectsList, HttpServletRequest httpRequest, String initialPath);
}
