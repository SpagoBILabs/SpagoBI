/*
*
* @file CubeViewBuilder.java
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
* @version $Id: CubeViewBuilder.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.api.impl.views;

import java.util.HashMap;

import org.palo.api.Cube;
import org.palo.api.CubeView;
import org.palo.api.Property;
import org.palo.api.impl.PersistenceErrorImpl;
import org.palo.api.persistence.PersistenceError;

/**
 * An internally used builder to create {@link CubeView}s. The main usage of 
 * this builder is during the restore of persistent cube views from their xml
 * representation. Therefore it provides a setter method for each cube view
 * field.
 * 
 * @author ArndHouben
 * @version $Id: CubeViewBuilder.java,v 1.12 2009/04/29 10:21:58 PhilippBouillon Exp $
 */
class CubeViewBuilder {
	
	//required fields:
	private String id;
	private String name;
	private HashMap properties = new HashMap();
	private Cube cube;
	
	//optional:
	private String description;
	
	final synchronized CubeView createView(CubeViewHandler viewHandler) {
		if (id == null || name == null || cube == null) {
			PersistenceError error = new PersistenceErrorImpl(
					"Could not create cube view, insufficient information", id,
					cube, null, null, PersistenceError.LOADING_FAILED, null,
					PersistenceError.TARGET_GENERAL);
			viewHandler.addError(error);
			return null;
			// throw new PaloAPIException(
			// "Cannot create cube view, insufficient information");
		}
		int n = properties.size();
		Object[] params = new Object[3 + n];
		params[0] = id;
		params[1] = name;
		params[2] = cube;
		Property[] props = null;
		if (n > 0) {
			props = new Property[n];
			String[] keys = (String[]) properties.keySet().toArray(
					new String[properties.size()]);
			for (int i = 0; i < n; i++) {
				props[i] = new Property(keys[i], (String) properties
						.get(keys[i]));
				params[3 + i] = props[i];
			}
		}
		CubeViewManager creator = CubeViewManager.getInstance();
		CubeView view = (CubeView) creator.create(CubeView.class, params);
		// reset view to perform an update:
		((CubeViewImpl) view).reset();

		if (description != null)
			view.setDescription(description);
		return view;
	}
	
	final synchronized void setCube(Cube cube) {
		this.cube = cube;
	}
	final synchronized void setDescription(String description) {
		this.description = description;
	}
	final synchronized void setId(String id) {
		this.id = id;
	}
	final synchronized void setName(String name) {
		this.name = name;
	}
	final synchronized void addProperty(String id, String value) {
		properties.put(id, value);
	}
	final synchronized void addProperty(Property property) {
		if (property == null) {
			return;
		}
		properties.put(property.getId(), property.getValue());
	}
}
