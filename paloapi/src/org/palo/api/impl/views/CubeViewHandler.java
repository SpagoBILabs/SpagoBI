/*
*
* @file CubeViewHandler.java
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
* @version $Id: CubeViewHandler.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.impl.views;

import java.util.ArrayList;
import java.util.HashMap;

import org.palo.api.Axis;
import org.palo.api.CubeView;
import org.palo.api.Database;
import org.palo.api.Dimension;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.api.impl.PersistenceErrorImpl;
import org.palo.api.impl.xml.IPaloEndHandler;
import org.palo.api.impl.xml.IPaloStartHandler;
import org.palo.api.persistence.PersistenceError;

import com.tensegrity.palojava.PaloException;

/**
 * A <code>CubeViewHandler</code> defines an additional abstraction layer to 
 * the persistence handling of cube views. This provides the possibility to
 * write and read different cube view versions. Right now we have to supported
 * cube view versions which are handled by <code>{@link CubeViewHandler1_0}</code>
 * and <code>{@link CubeViewHandler1_1}</code> respectively.
 *
 * @author ArndHouben
 * @version $Id: CubeViewHandler.java,v 1.10 2009/04/29 10:21:58 PhilippBouillon Exp $
 **/
abstract class CubeViewHandler {
	
	static final String LEGACY = "legacy";
	
	private final ArrayList errors = new ArrayList();
	private final HashMap endHandlers = new HashMap();
	private final HashMap startHandlers = new HashMap();
	protected final Database database;
	protected Axis currAxis;
	protected CubeView cubeView;
	
	
	CubeViewHandler(Database database) {
		this.database = database;
		registerHandlers();
	}
	
	
	protected abstract void registerStartHandlers();
	protected abstract void registerEndHandlers();
	
	final boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	final PersistenceError[] getErrors() {
		return (PersistenceError[]) errors.toArray(new PersistenceError[errors.size()]);
	}
	
	final void addError(PersistenceError error) {
		if(!errors.contains(error))
			errors.add(error);
	}
	
    CubeView getCubeView() {
		return cubeView;
	}

	final IPaloEndHandler[] getEndHandlers() {
		return (IPaloEndHandler[]) endHandlers.values().toArray(
				new IPaloEndHandler[endHandlers.size()]);
	}

	final IPaloStartHandler[] getStartHandlers() {
		return (IPaloStartHandler[]) startHandlers.values().toArray(
				new IPaloStartHandler[startHandlers.size()]);
	}
	
    protected final void addError(String msg, String srcId, Object src,
			Object causeParent, String causeId, int type, Object section,
			int sectionType) {
		PersistenceErrorImpl err = new PersistenceErrorImpl(msg, srcId, src,
				causeParent, causeId, type, section, sectionType);
		addError(err);
	}
    

    protected final Element[] getPath(String path, Hierarchy hier, Object section, int sectionType)
			throws PaloAPIException {
		String[] paths = path.split(CubeViewPersistence.PATH_DELIMETER);
		Element[] elements = new Element[paths.length];
		for (int i = 0; i < paths.length; ++i) {
			elements[i] = hier.getElementByName(paths[i]);
			if (elements[i] == null) {
				addError("CubeViewReader: unknown element id '" + paths[i]
						+ "'!!", cubeView.getId(), cubeView, hier, paths[i],
						PersistenceError.UNKNOWN_ELEMENT, section, sectionType);
			}
		}
		return elements;
	}
    protected final Element[] getPathById(String path, Dimension dim, Hierarchy hier,
			Object section, int sectionType) throws PaloAPIException {
		String[] paths = path.split(CubeViewPersistence.PATH_DELIMETER);
		Element[] elements = new Element[paths.length];
		for (int i = 0; i < paths.length; ++i) {
			try {
				if (hier != null) {
					elements[i] = hier.getElementById(paths[i]);
				} else {
					elements[i] = dim.getElementById(paths[i]);
				}
			} catch (PaloException e) {
				elements[i] = null;
			}
			if (elements[i] == null) {
				addError("CubeViewReader: unknown element id '" + paths[i]
						+ "'!!", cubeView.getId(), cubeView, hier, paths[i],
						PersistenceError.UNKNOWN_ELEMENT, section, sectionType);
			}
		}
		return elements;
	}

    
    protected final void registerStartHandler(IPaloStartHandler startHandler) {
    	startHandlers.put(startHandler.getPath(),startHandler);
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
