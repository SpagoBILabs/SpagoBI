/*
*
* @file SubsetStateBuilder.java
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
* @version $Id: SubsetStateBuilder.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 20056. All rights reserved.
 */
package org.palo.api.impl.subsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Attribute;
import org.palo.api.Element;
import org.palo.api.SubsetState;
import org.palo.api.utils.ElementUtilities;

/**
 * An internally used builder to create {@link SubsetState}s. The main usage of 
 * this builder is during the restore of persistent subsets. Therefore this
 * builder is for internal usage only.
 * <p>
 * <b>Note:</b> this builder is for internal usage only!
 * </p>
 
 * @author ArndHouben
 * @version $Id: SubsetStateBuilder.java,v 1.8 2009/04/29 10:21:57 PhilippBouillon Exp $
 */
public class SubsetStateBuilder {
	
	private String id;
	private String name;
	private String expression;
	private Attribute searchAttribute;
	private final Set elements = new LinkedHashSet();
	private final Map elPaths = new HashMap();
	private final Map elPos = new HashMap();
	
	public final void setId(String id) {
		this.id = id;
	}

	final void setName(String name) {
		this.name = name;
	}
	
	final void setExpression(String expression) {
		this.expression = expression;
	}
	
	final void setSearchAttribute(Attribute searchAttribute) {
		this.searchAttribute = searchAttribute;
	}
	
	final void addElement(Element element) {
		if(element != null)
			elements.add(element);
	}
	
	final void setPaths(Element element,String paths) {
		if(element == null)
			return;
		//PR 6725: take default paths of element if no paths are given...
		if(paths == null)
			paths = ElementUtilities.getPaths(element);
		elPaths.put(element, paths);
	}
		
	final void setPositions(Element element, String positions) {
		if(element == null || positions == null)
			return;
		elPos.put(element,positions);
	}
	
	public final SubsetState createState() {
		SubsetStateImpl state = new SubsetStateImpl(id);
		if(name != null)
			state.setName(name);
		if(expression != null)
			state.setExpression(expression);
		if (searchAttribute != null)
			state.setSearchAttribute(searchAttribute);
		if(!elements.isEmpty()) {
			for(Iterator it = elements.iterator();it.hasNext();) {
				Element element = (Element)it.next();
				state.addVisibleElment(element);
				state.setPaths(element, (String)elPaths.get(element));
				state.setPosition(element, (String)elPos.get(element));
			}
		}
		return state;
	}
}
