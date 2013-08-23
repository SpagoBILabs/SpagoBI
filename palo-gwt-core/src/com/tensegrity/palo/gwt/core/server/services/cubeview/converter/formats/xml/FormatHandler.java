/*
*
* @file FormatHandler.java
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
* @version $Id: FormatHandler.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
*
* @file FormatHandler.java
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
* @version $Id: FormatHandler.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services.cubeview.converter.formats.xml;

import java.util.HashMap;

import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;

/**
 * A <code>FormatHandler</code> defines an additional abstraction layer to 
 * the persistence handling of cube views. This provides the possibility to
 * write and read different cube view versions. Right now we have to supported
 * cube view versions which are handled by <code>{@link FormatHandler1_0}</code>
 * and <code>{@link CubeViewHandler1_1}</code> respectively.
 *
 * @author ArndHouben
 * @version $Id: FormatHandler.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
 **/
abstract class FormatHandler {
	private final HashMap <String, IPaloEndHandler> endHandlers = 
		new HashMap <String, IPaloEndHandler>();
	private final HashMap <String, IPaloStartHandler> startHandlers = 
		new HashMap <String, IPaloStartHandler>();
	
	FormatHandler() {
		registerHandlers();
	}
		
	protected abstract void registerStartHandlers();
	protected abstract void registerEndHandlers();
		
	final IPaloEndHandler[] getEndHandlers() {
		return endHandlers.values().toArray(new IPaloEndHandler[0]);
	}

	final IPaloStartHandler[] getStartHandlers() {
		return startHandlers.values().toArray(new IPaloStartHandler[0]);
	}
	    
    protected final void registerStartHandler(IPaloStartHandler startHandler) {
    	startHandlers.put(startHandler.getPath(), startHandler);
    }
    
    protected final void unregisterStartHandler(String handlerPath) {
    	startHandlers.remove(handlerPath);
    }
    
    protected final void clearStartHandlers() {
    	startHandlers.clear();
    }
    
    protected final void registerEndHandler(IPaloEndHandler endHandler) {
    	endHandlers.put(endHandler.getPath(),endHandler);
    }
    
    protected final void unregisterEndHandler(String handlerPath) {
    	endHandlers.remove(handlerPath);
    }
    
    protected final void clearEndHandlers() {
    	endHandlers.clear();
    }
    
    private final void registerHandlers() {
		registerStartHandlers();
		registerEndHandlers();
    }
}
