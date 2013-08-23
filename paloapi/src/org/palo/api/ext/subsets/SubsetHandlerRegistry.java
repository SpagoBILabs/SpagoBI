/*
*
* @file SubsetHandlerRegistry.java
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
* @version $Id: SubsetHandlerRegistry.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.subsets;

import java.util.HashMap;
import java.util.Map;

import org.palo.api.Dimension;
import org.palo.api.Subset;
import org.palo.api.SubsetState;
import org.palo.api.ext.subsets.impl.SubsetStateHandlerFactory;
import org.palo.api.ext.subsets.states.FlatState;
import org.palo.api.ext.subsets.states.HierarchicalState;
import org.palo.api.ext.subsets.states.RegExState;

/**
 * A <code>SubsetHandlerRegistry</code> administers <code>SubsetStateHandler</code>s
 * which are used to determine the visible <code>Element</code>s from a certain
 * <code>Subset</code> and its <code>SubsetState</code>. A <code>SubsetStateHandler</code>
 * should not be used directly to compute the visible elements. Instead the
 * registry returns a <code>SubsetHandler</code> which provides convenience 
 * methods for this task.
 * <p>
 * The API provides three default <code>SubsetStateHandler</code>s which are
 * registered already, namely:
 * <ui>
 * <li>FlatStateHandler which handles the <code>{@link FlatState}</code></li>
 * <li>HierarchicalStateHandler which handles the <code>{@link HierarchicalState}</code></li>
 * <li>RegExStateHandler which handles the <code>{@link RegExState}</code></li>
 * </ui> 
 * </p>
 * </br>
 * <p>
 * Here is a code snippet which shows an example usage of the 
 * <code>SubsetHandlerRegistry</code>
 * <p><code>
 *      ... </br>
 *      SubsetHandlerRegistry handlerReg = SubsetHandlerRegistry.getInstance();</br>
 *      //get visible elements for the currently active SubsetState </br>
 *      Element[] visibleElements = handlerReg.getHandler(aSubset).getVisibleElements(); </br>
 *		//get visible elements for a certain SubsetState </br>
 *		visibleElements = handlerReg.getHandler(aSubset,RegExState.ID).getVisibleElements(); </br>
 *		...
 * </code></p> 
 * </p>
 * 
 * 
 * @author ArndHouben
 * @version $Id: SubsetHandlerRegistry.java,v 1.6 2009/04/29 10:21:58 PhilippBouillon Exp $
 * @deprecated Legacy subsets are not supported anymore! 
 * Please use {@link Dimension#getSubsetHandler()} instead! 
 */
public class SubsetHandlerRegistry {
	
	//--------------------------------------------------------------------------
	// FACTORY
	//
	private static final SubsetHandlerRegistry instance = new SubsetHandlerRegistry();
	/**
	 * Returns the sole registry instance
	 * @return
	 */
	public static final SubsetHandlerRegistry getInstance() {
		return instance;
	}
	
	//--------------------------------------------------------------------------
	// INSTANCE
	//
	private final Map stateHandlers = new HashMap();
	private final SubsetStateHandlerFactory stateFactory = 
			SubsetStateHandlerFactory.getInstance();
	
	private SubsetHandlerRegistry() {
		//we register our default handlers:
		register(FlatState.ID,stateFactory.create(FlatState.ID));
		register(HierarchicalState.ID,stateFactory.create(HierarchicalState.ID));
		register(RegExState.ID,stateFactory.create(RegExState.ID));
	}
	
	/**
	 * Returns the {@link SubsetHandler} for the given <code>Subset</code>.
	 * <code>null</code> is returned if no <code>SubsetHandler</code> could be 
	 * found or if no active <code>SubsetState</code> is set.
	 * @param subset the <code>Subset</code> to create the handler for
	 * @return a <code>SubsetHandler</code> or <code>null</code> if no handler
	 * could be found
	 */
	public final SubsetHandler getHandler(Subset subset) {
		return getHandler(subset, subset.getActiveState());
	}
	
	/**
	 * Returns the {@link SubsetHandler} for the given <code>SubsetState</code>.
	 * If no <code>SubsetHandler</code> could be found <code>null</code> is
	 * returned.
	 * @param subset the <code>Subset</code> to create the handler for
	 * @param subsetState the <code>SubsetState</code> to use
	 * @return a <code>SubsetHandler</code> or <code>null</code> if no handler
	 * could be found
	 */
	public final SubsetHandler getHandler(Subset subset,SubsetState subsetState) {
		if (subsetState == null)
			return null;
		
		return getHandler(subset, subsetState.getId());
	}
	
	/**
	 * Returns the {@link SubsetHandler} for the given <code>Subset</code> and
	 * its <code>SubsetState</code> defined by the given id.
	 * If no <code>SubsetHandler</code> could be found <code>null</code> is
	 * returned.
	 * @param subset the <code>Subset</code> to create the handler for
	 * @param stateId a valid subset state identifier
	 * @return a <code>SubsetHandler</code> or <code>null</code> if no handler
	 * could be found
	 */
	public final SubsetHandler getHandler(Subset subset, String stateId) {
		if (subset == null || stateId == null)
			return null;
		SubsetStateHandler stateHandler = 
				(SubsetStateHandler)stateHandlers.get(stateId);		
		if(stateHandler == null)
			return null;
		
		//initialize state handler:
		stateHandler.use(subset,subset.getState(stateId));
		return stateFactory.create(stateHandler);
	}

	/**
	 * Returns the internally used <code>SubsetStateHandler</code> which is 
	 * registered under the given subset state id. <B>Note:</B> it is not
	 * recommended to use a <code>SubsetStateHandler</code> directly, rather
	 * utilize the provided {@link #getHandler(Subset)} method. 
	 * @param stateId as subset state id
	 * @return the corresponding subset state handler or null if no handler was
	 * registered for this state id.  
	 */
	public final SubsetStateHandler getStateHandler(String stateId) {
		return (SubsetStateHandler)stateHandlers.get(stateId);
	}
	
	/**
	 * Returns all registered <code>SubsetStateHandler</code>s
	 * @return
	 */
	public final SubsetStateHandler[] getAllStateHandler() {
		return (SubsetStateHandler[]) stateHandlers.values().toArray(
				new SubsetStateHandler[stateHandlers.size()]);
	}
	
	/**
	 * Registers the given handler for the <code>SubsetState</code> specified
	 * by given state id.
	 * @param id  
	 * @param handler
	 */
	public final void register(String stateId, SubsetStateHandler handler) {
		if(stateId==null)
			return;
		stateHandlers.put(stateId,handler);
	}
}
