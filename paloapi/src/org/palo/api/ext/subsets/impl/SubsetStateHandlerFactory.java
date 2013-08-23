/*
*
* @file SubsetStateHandlerFactory.java
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
* @version $Id: SubsetStateHandlerFactory.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets.impl;

import org.palo.api.Dimension;
import org.palo.api.DimensionFilter;
import org.palo.api.Hierarchy;
import org.palo.api.HierarchyFilter;
import org.palo.api.ext.subsets.SubsetHandler;
import org.palo.api.ext.subsets.SubsetStateHandler;
import org.palo.api.ext.subsets.states.FlatState;
import org.palo.api.ext.subsets.states.HierarchicalState;
import org.palo.api.ext.subsets.states.RegExState;

/**
 * The <code>SubsetStateHandlerFactory</code> creates the default 
 * <code>SubsetStateHandler</code>s as well as the default 
 * <code>SubsetHandler</code>. 
 * 
 * 
 * @author ArndHouben
 * @version $Id: SubsetStateHandlerFactory.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
public class SubsetStateHandlerFactory {

	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final SubsetStateHandlerFactory instance = new SubsetStateHandlerFactory();
	public static final SubsetStateHandlerFactory getInstance() {
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final DefaultSubsetHandler handler = new DefaultSubsetHandler();
	private SubsetStateHandlerFactory() {
	}
	
	public final SubsetStateHandler create(String handlerID) {
		if(handlerID.equals(FlatState.ID))
			return new FlatStateHandler();
		else if(handlerID.equals(HierarchicalState.ID))
			return new HierarchicalStateHandler();
		else if(handlerID.equals(RegExState.ID))
			return new RegExStateHandler();
		return null;
	}

	public final SubsetHandler create(SubsetStateHandler stateHandler) {
		Hierarchy hier = stateHandler.getSubset().getHierarchy();		
		if(hier == null)
			return null;
		HierarchyFilter filter = stateHandler.createHierarchyFilter(hier);
		if(filter == null)
			return null;
		
		handler.use(hier, filter,stateHandler.getSubsetState());
		return handler;
	}
}
