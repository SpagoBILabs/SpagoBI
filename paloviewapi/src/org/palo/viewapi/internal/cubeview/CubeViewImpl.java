/*
*
* @file CubeViewImpl.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: CubeViewImpl.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007. All rights reserved.
 */
package org.palo.viewapi.internal.cubeview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palo.api.Cube;
import org.palo.api.Element;
import org.palo.api.Hierarchy;
import org.palo.api.PaloAPIException;
import org.palo.viewapi.AuthUser;
import org.palo.viewapi.Axis;
import org.palo.viewapi.AxisHierarchy;
import org.palo.viewapi.CubeView;
import org.palo.viewapi.Property;
import org.palo.viewapi.Right;
import org.palo.viewapi.View;
import org.palo.viewapi.exceptions.NoPermissionException;
import org.palo.viewapi.internal.ViewImpl;
import org.palo.viewapi.internal.io.CubeViewReader;
import org.palo.viewapi.internal.util.CompoundKey;
import org.palo.viewapi.uimodels.formats.Format;
import org.palo.viewapi.uimodels.formats.FormatImpl;
import org.palo.viewapi.uimodels.formats.FormatRangeInfo;

/**
 * Default implementation of the {@link CubeView} interface
 * 
 * @version $Id: CubeViewImpl.java,v 1.10 2009/12/17 16:14:08 PhilippBouillon Exp $
 */
class CubeViewImpl implements CubeView {
	
	/**
	 * The axes for this <code>CubeView</code>. Usually "rows", "cols" and
	 * "selected".
	 */
	private final Set<Axis> axes = new HashSet<Axis>();
	/**
	 * The <code>properties</code> hash map stores all properties for this
	 * <code>CubeView</code>. Its keys are Strings representing the id of the
	 * specified property, its values are Strings denoting the value of the
	 * respective property.
	 */
	private final HashMap <String, Property <Object>> properties;	
	private final Map <String, Format> formats;
	private final List <FormatRangeInfo> formatRanges;
	private final View view;
	private final AuthUser authUser;
	/** The source cube for which this view is defined.	 */
	private final Cube srcCube;
	private final CompoundKey key;
	
	private String description;
	
	
	CubeViewImpl(View view, Cube srcCube, AuthUser user, String externalId) {
		//TODO maybe we get the authUser view view.getAccount().getUser()
		this.view = view;
		this.authUser = user; //view.getAccount().getUser();
		this.srcCube = srcCube;
		
		this.properties = new HashMap<String, Property <Object>>();
		if (externalId != null) {
			addProperty("paloSuiteID", externalId);
		}
		this.formats = new LinkedHashMap<String, Format>();
		this.formatRanges = new ArrayList<FormatRangeInfo>();
		
		this.key = new CompoundKey(new Object[] { CubeViewImpl.class,
				view.getId(), srcCube.getId(), srcCube.getDatabase().getId() });
	}
	
	/** copy constructor */
	private CubeViewImpl(CubeViewImpl cView) {
		this(cView.view, cView.srcCube, cView.authUser, (String) cView.getPropertyValue("paloSuiteID"));
		this.description = cView.description;
		
		//deep copy all members		
		for(Axis axis : cView.axes) {
			axes.add(axis.copy());
		}
		
		for(Format format : cView.formats.values()) {
			Format fmCopy = format.copy();
			this.formats.put(fmCopy.getId(), fmCopy);
		}
		for(FormatRangeInfo rangeInfo : cView.formatRanges)
			formatRanges.add(rangeInfo.copy());
		
		//TODO deep copy properties...
//		this.properties = new HashMap<String, Property>();		
		properties.putAll(cView.properties);
	}

	
	/**
	 * Adds an axis to this view if it doesn't exist, yet. If the axis already
	 * exists in the view, a PaloAPIExceptin is thrown.
	 * @return the new created axis
	 * @throws PaloAPIException if an axis with the given name exists already
	 * @throws NoPermissionException if user has not enough rights to change this view
	 */
	public final Axis addAxis(String id, String name) {
		checkPermission(Right.WRITE);
		AxisImpl axis = new AxisImpl(id, name, view);
		if (axes.contains(axis))
			throw new PaloAPIException("Axis already exist!");
		axes.add(axis);
		return axis;
	}

	public final Format addFormat(String id) {
		checkPermission(Right.WRITE);
		if (id == null) {
			return null;
		}
		Format format = new FormatImpl(id);
		formats.put(id, format);
		return format;
	}

	public final void addFormat(Format format) {
		checkPermission(Right.WRITE);
		if (format == null) {
			return;
		}
		formats.put(format.getId(), format);		
	}

	public final Property<Object> addProperty(String id, Object value) {
		checkPermission(Right.WRITE);
		Property <Object> prop = new Property<Object>(id, value);
		properties.put(id, prop);
		return prop;
	}

	public final CubeView copy() {
		return new CubeViewImpl(this);
	}

	public final Axis[] getAxes() {
		return (Axis[])axes.toArray(new Axis[axes.size()]);
	}

	public final Axis getAxis(String id) {
		for(Iterator<Axis> it = axes.iterator();it.hasNext(); ) {
			Axis axis = it.next();
			if(axis.getId().equals(id))
				return axis;
		}
		return null;
	}

	public final Cube getCube() {
		return srcCube;
	}

	public final String getDescription() {
		return description;
	}

	public final Format getFormat(String formatId) {
		return formats.get(formatId);
	}

	public final Format[] getFormats() {
		return formats.values().toArray(new Format[0]);
	}

	public final String getId() {
		return view.getId();
	}

	@SuppressWarnings("unchecked")
	public Property <Object> [] getProperties() {
		return properties.values().toArray(new Property[0]);
	}

	public Property <Object> getProperty(String id) {
		return properties.get(id);
	}	

	public Object getPropertyValue(String id) {
		Property <Object >prop = getProperty(id);
		if (prop == null) {
			return null;
		}
		return prop.getValue();
	}

	public final boolean hasFormats() {
		return !formats.isEmpty();
	}

	public final void removeAllFormats() {
		checkPermission(Right.WRITE);
		formats.clear();
	}

	public final void removeAxis(Axis axis) {
		checkPermission(Right.WRITE);
		((AxisImpl)axis).setView(null);
		axes.remove(axis);
	}

	public final void removeFormat(String formatId) {
		checkPermission(Right.WRITE);
		formats.remove(formatId);
	}

	public final void removeProperty(String id) {
		checkPermission(Right.WRITE);
		properties.remove(id);
	}

	public final void setDescription(String description) {
		checkPermission(Right.WRITE);
		this.description = description;
	}

	public final void setName(String name) {
		checkPermission(Right.WRITE);
		((ViewImpl)view).setName(name);
	}

	public final String getName() {
		return view.getName();
	}

	public final Object getDefaultValue(String parameterName) {
		if (parameterName.equals(CubeView.PARAMETER_ELEMENT)) {
			for (Axis a: getAxes()) {
				for (Hierarchy h: a.getHierarchies()) {
					if (h.getElementCount() > 0) {
						return h.getElementAt(0);
					}
				}
			}
		}
		return null;
	}

	public final String[] getParameterNames() {
		return new String [] { CubeView.PARAMETER_ELEMENT };
	}

	public final Object getParameterValue(String parameterName) {
		if (parameterName.equals(CubeView.PARAMETER_ELEMENT)) {
			Property <Object> prop = properties.get("VAR_" + parameterName);
			if (prop != null) {
				return prop.getValue();
			}
		}
		return null;
	}

	public boolean isParameterized() {
		return properties.get("VAR_" + CubeView.PARAMETER_ELEMENT) != null;
	}

	public final void setParameter(String parameterName, Object parameterValue) {
		checkPermission(Right.WRITE);
		if (!parameterName.equals(CubeView.PARAMETER_ELEMENT)) {
			return;
		}
		if (!(parameterValue instanceof Element [])) {
			return;
		}
		Element [] pars = (Element []) parameterValue;
		Axis a = getAxis("selected");
		if (a == null) {
			return;
		}
		for (Element e: pars) {
			if (e == null) {
				continue;
			}
			AxisHierarchy ah;
			if ((ah = a.getAxisHierarchy(e.getHierarchy())) != null) {
				ah.clearSelectedElements();
				ah.addSelectedElement(e);
			}
		}
	}

	public void addParameterValue(String parameterName, Object parameterValue) {
		if (parameterName.equals(CubeView.PARAMETER_ELEMENT)) {
			Object o = getParameterValue(parameterName);
			if (o == null) {
				setParameter(parameterName, parameterValue);
			} else if (o instanceof Element [] && parameterValue instanceof Element) {
				Element [] result = (Element []) o;
				Element [] nVal = new Element[result.length + 1];
				for (int i = 0; i < result.length; i++) {
					nVal[i] = result[i];
				}
				nVal[result.length] = (Element) parameterValue;
				setParameter(parameterName, nVal);				
			}
		}
	}
	
	public final void setParameterNames(String[] parameterNames) {
		checkPermission(Right.WRITE);
	}

	public final boolean equals(Object obj) {
		if (obj instanceof CubeView) {
			CubeViewImpl other = (CubeViewImpl) obj;
			return key.equals(other.key);
		}
		return false;
	}
	
	public final int hashCode() {
		int hc = 17;
		hc += 23 * key.hashCode();
		return hc;

	}

	
	/**
	 * Clears all defines axes of this CubeView and restores it thus to an
	 * empty state.
	 */
	final void reset() {
		axes.clear();
	}

	
	//--------------------------------------------------------------------------
	// PRIVATE METHODS
	//
	private final void checkPermission(Right right) {
		if (CubeViewReader.CHECK_RIGHTS) {
			if(!authUser.hasPermission(right, view)) {
				throw new NoPermissionException("Not enough rights!", view,
						authUser, Right.WRITE);
			}
		}
	}
}
