/*
*
* @file SubsetHandler.java
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
* @version $Id: SubsetHandler.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets;

import java.util.List;

import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.Subset;


/**
 * A <code>SubsetHandler</code> provides methods for fetching the visible 
 * <code>Element</code>s of a <code>Dimension</code> which are determined
 * by its active <code>Subset</code>. To influence a <code>SubsetHandler</code>s
 * output for a certain subset state, implement a <code>SubsetStateHandler</code> 
 * and register it to the <code>SubsetHandlerRegistry</code>.
 * 
 * @author ArndHouben
 * @version $Id: SubsetHandler.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
 * @deprecated Legacy subsets are not supported anymore! 
 * Please use {@link Dimension#getSubsetHandler()} instead! 
 */
public interface SubsetHandler {

	/**
	 * Determines and returns the visible elements 
	 * @return all visible elements
	 */
	public Element[] getVisibleElements();
	
	/**
	 * Determines and returns the visible elements
	 * @return a list which contains all visible elements
	 */
	public List getVisibleElementsAsList();
	
	/**
	 * Determines and returns the visible root <code>{@link ElementNode}</code>s. 
	 * @return all root <code>{@link ElementNode}</code>s
	 */
	public ElementNode[] getVisibleRootNodes();
	
	/**
	 * Determines and returns the visible root <code>{@link ElementNode}</code>s. 
	 * @return a list containing all visible root 
	 * <code>{@link ElementNode}</code>s
	 */
	public List getVisibleRootNodesAsList();
	
	/**
	 * Signals if the determined visible <code>Element</code>s or 
	 * <code>ElementNode</code>s respectively, should be handled as a flat
	 * structure, i.e. those <code>Element</code>s or <code>ElementNode</code>s 
	 * define root nodes. 
	 * @return <code>true</code> if the visible <code>Element</code>s define a 
	 * flat structure, <code>false</code> otherwise.
	 */
	public boolean isFlat();
	
	/**
	 * Checks if the given <code>{@link Element}</code> is visible within 
	 * active <code>{@link Subset}</code>. Returns <code>true</code> if active 
	 * <code>Subset</code> contains the given <code>Element</code>, 
	 * <code>false</code> otherwise or if no active <code>Subset</code> is set.
	 * @param element the <code>{@link Element}</code> to check 
	 * @return <code>true</code> if it is visible, <code>false</code> otherwise 
	 */
	public boolean isVisible(Element element);
}
