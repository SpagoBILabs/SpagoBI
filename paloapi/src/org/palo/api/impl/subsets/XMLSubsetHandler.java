/*
*
* @file XMLSubsetHandler.java
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
* @version $Id: XMLSubsetHandler.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.subsets;

import java.util.HashMap;

import org.palo.api.Database;
import org.palo.api.Subset;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;

/**
 * A <code>XMLSubsetHandler</code> defines an additional abstraction layer to 
 * the persistence handling of subsets. This provides the possibility to
 * write and read different subset versions. Right now we have to support the
 * subset versions which are handled by <code>{@link XMLSubsetHandler1_0}</code>,
 * <code>{@link XMLSubsetHandler1_1}</code> and 
 * <code>{@link XMLSubsetHandlerLegacy}</code> respectively.
 *
 * @author ArndHouben
 * @version $Id: XMLSubsetHandler.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
abstract class XMLSubsetHandler {

	private final HashMap endHandlers = new HashMap();
	private final HashMap startHandlers = new HashMap();
	protected SubsetBuilder subsetBuilder;
	protected SubsetStateBuilder stateBuilder;
	
	XMLSubsetHandler(Database database) {
		registerHandlers(database);
	}

	/**
	 * Creates and return the read in <code>{@link Subset}</code>
	 * @return read in <code>{@link Subset}</code>
	 */
	final Subset getSubset() {
		if(subsetBuilder != null)
			return subsetBuilder.createSubset();
		return null;
	}
	
	/**
	 * Returns all defined <code>{@link IPaloStartHandler}</code> for reading
	 * in <code>{@link Subset}</code>
	 * @return defined <code>{@link IPaloStartHandler}</code>
	 */
	final IPaloStartHandler[] getStartHandlers() {
		return (IPaloStartHandler[]) startHandlers.values().toArray(
				new IPaloStartHandler[startHandlers.size()]);
	}

	/**
	 * Returns all defined <code>{@link IPaloEndHandler}</code> for reading
	 * in <code>{@link Subset}</code>
	 * @return defined <code>{@link IPaloEndHandler}</code>
	 */
	final IPaloEndHandler[] getEndHandlers() {
		return (IPaloEndHandler[]) endHandlers.values().toArray(
				new IPaloEndHandler[endHandlers.size()]);
	}

	/**
	 * Returns all defined <code>{@link IPaloStartHandler}</code> for reading
	 * in <code>{@link Subset}</code>. This method has to be implemented by
	 * sublcasses.
	 * @return defined <code>{@link IPaloStartHandler}</code>
	 */
	abstract IPaloStartHandler[] getStartHandlers(Database database);
	/**
	 * Returns all defined <code>{@link IPaloEndHandler}</code> for reading
	 * in <code>{@link Subset}</code>. This method has to be implemented by
	 * sublcasses.
	 * @return defined <code>{@link IPaloEndHandler}</code>
	 */
	abstract IPaloEndHandler[] getEndHandlers(Database database);

	
	private final void registerHandlers(Database database) {
		IPaloStartHandler[] stHandlers = getStartHandlers(database);
		for(int i=0;i<stHandlers.length;++i) {
			startHandlers.put(stHandlers[i].getPath(), stHandlers[i]);
		}
		IPaloEndHandler[] enHandlers = getEndHandlers(database);
		for(int i=0;i<enHandlers.length;++i) {
			endHandlers.put(enHandlers[i].getPath(), enHandlers[i]);
		}
	}
}
