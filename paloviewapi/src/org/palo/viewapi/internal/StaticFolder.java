/*
*
* @file StaticFolder.java
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
* @author Philipp Bouillon
*
* @version $Id: StaticFolder.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.palo.api.parameters.ParameterProvider;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.viewapi.View;


/**
 * <code>StaticFolder</code>
 * This tree node represents a static folder, i.e. a folder which contains
 * FolderElements or other folders (static or dynamic).
 * 
 * This class implements ParameterProvider and ParameterReceiver so it can
 * be used to transport (arbitrary) parameters.
 * 
 * @author Philipp Bouillon
 * @version $Id: StaticFolder.java,v 1.6 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public class StaticFolder extends AbstractExplorerTreeNode 
                          implements ParameterProvider, ParameterReceiver {
	public static final int SF_TYPE = 2;
	
	/**
	 * List of all currently supported parameter names.
	 */
	private String [] parameterNames;

	private final HashMap <String, Object> parameterValue;
	
	/**
	 * Map of all currently valid parameter values (for each parameter name).
	 */
	private final HashMap <String, Object []> possibleValues;

	/**
	 * Receiving parameter object (that's usually a view).
	 */
//	private ParameterReceiver sourceObject = null;
	
	/**
	 * Internal member method to create a StaticFolder from an xml
	 * specification. Note that this method is not part of the public API
	 * of StaticFolder. So, do not use this method.
	 * 
	 * @param parent parent node of this static folder. Can be null.
	 * @param id globally unique id of this folder.
	 * @param name name of this folder.
	 * 
	 * @return a new StaticFolder according to the specified parameters.
	 */
	public static StaticFolder internalCreate(ExplorerTreeNode parent, 			
			String id, String name) {
		return new StaticFolder(parent, id, name);
	}

	/**
	 * Creates a new Static Folder with the specified parent, the given
	 * name, and the given id.
	 * 
	 * @param parent the parent of this folder; may be null.
	 * @param id the id of this folder.
	 * @param name the name of this folder.
	 */
	StaticFolder(ExplorerTreeNode parent, String id, String name) {
		super(parent, id, name);
		if (parent != null) {
			parent.addChild(this);
		}		
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
	}
	
	/**
	 * Creates a new Static Folder with the specified parent (which may be
	 * null).
	 * 
	 * @param parent the parent of this folder; may be null.
	 */
	public StaticFolder(ExplorerTreeNode parent) {
		super("sf_", parent);
		if (parent != null) {
			parent.addChild(this);
		}		
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
	}

	/**
	 * Creates a new Static Folder with the specified parent and the given
	 * name.
	 * 
	 * @param parent the parent of this folder; may be null.
	 * @param name the name of this folder.
	 */
	public StaticFolder(ExplorerTreeNode parent, String name) {		
		super("sf_", parent, name);
		if (parent != null) {
			parent.addChild(this);
		}		
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
	}
	
	StaticFolder(String id, String name, DynamicFolder parent, StaticFolder original) {
		super(parent, id, name);
		possibleValues = (HashMap<String, Object[]>) original.possibleValues.clone();
		children = new ArrayList<ExplorerTreeNode>();
		for (ExplorerTreeNode kid: original.children) {
			if (kid instanceof FolderElement) {
				children.add(new FolderElement(kid.getId(), kid.getName(), parent, (FolderElement) kid));
			} else {
				children.add(new DynamicFolder(kid.getId(), kid.getName(), parent, (DynamicFolder) kid));
			}
		}
		//(LinkedHashSet<ExplorerTreeNode>) original.children.clone();		
		parameterValue = new HashMap<String, Object>();
		for (String key: original.parameterValue.keySet()) {
			Object value = original.parameterValue.get(key);
			parameterValue.put(key, value);
			for (ExplorerTreeNode kid: children) {
				kid.setParameter(key, value);
			}
		}
		//sourceObject = original.sourceObject;
	}
	
	/**
	 * Returns all valid parameter names for this static folder.
	 */
	public String[] getParameterNames() {
		return parameterNames;
	}

	/**
	 * Returns all possible values for a given parameter. Returns an empty array
	 * either if no values are possible or a parameter with that name is not
	 * valid for the static folder.
	 */
	public Object[] getPossibleValuesFor(String parameterName) {
		if (!possibleValues.containsKey(parameterName)) {
			return new Object[0];
		}
		return possibleValues.get(parameterName);
	}

	/**
	 * Returns the source object of this Static Folder. Usually a view.
	 */
	public ParameterReceiver getSourceObject() {
		return null; //sourceObject;
	}

	/**
	 * Sets the valid parameter names of this Static Folder. Thus, it can be
	 * used not only as a provider for views, but also for more than that.
	 */
	public void setParameterNames(String[] parameterNames) {
		this.parameterNames = parameterNames;
	}

	/**
	 * Sets the possible values for a given parameter name.
	 */
	public void setPossibleValuesFor(String parameterName,
			Object[] possibleValues) {
		this.possibleValues.put(parameterName, possibleValues);
	}

	/**
	 * Sets the source object for this Static Folder.
	 */
	public void setSourceObject(ParameterReceiver sourceObject) {
		//this.sourceObject = sourceObject;
	}

	/**
	 * Returns the default value for a given parameter. 
	 * The default element is the sourceObject's default value, or null
	 * if no source object has been set.
	 */	
	public Object getDefaultValue(String parameterName) {
//		if (sourceObject == null) {
//			return null;
//		}
//		return sourceObject.getDefaultValue(parameterName);
		return null;
	}

	/**
	 * Returns the current value of the specified parameter.
	 */
	public Object getParameterValue(String parameterName) {
		return this.parameterValue.get(parameterName);
	}
	
	/**
	 * Returns true if and only if the source object is parameterized.
	 */
	public boolean isParameterized() {
//		if (sourceObject == null) {
//			return false;
//		}
//		return sourceObject.isParameterized();
		return true;
	}

	/**
	 * Sets the given value for the specified parameter.
	 */
	public void setParameter(String parameterName, Object parameterValue) {
		this.parameterValue.put(parameterName, parameterValue);
		if (children.isEmpty()) {
			return;
		}
		for (ExplorerTreeNode node: children) {
			node.setParameter(parameterName, parameterValue);	
		}		
	}
	
	/**
	 * Creates a persistence String to identify a view.
	 * 
	 * @param v the view to be identified.
	 * @return a String describing the view.
	 */
	final static String makePersistenceString(View v) {
		String viewId  = v.getId();
		return viewId;
	}
	 
	/**
	 * Returns the persistence string for this static folder.
	 */
	public String getPersistenceString() {
		StringBuffer result = new StringBuffer("<staticFolder ");
		if (getId() != null) {
			result.append("id=\"");
			result.append(getId());
			result.append("\" ");
		}
		if (name != null) {
			result.append("name=\"");
			result.append(modify(name));
			result.append("\" ");
		}
//		if (sourceObject != null && sourceObject instanceof View) {
//			result.append("source=\"");			
//			View v = (View) sourceObject;			
//			result.append(makePersistenceString(v));
//			result.append("\" ");			
//		}
		result.append(">\n");
		for (ExplorerTreeNode kid: getChildren()) {
			result.append(kid.getPersistenceString());
		}
		result.append("</staticFolder>\n");
		return result.toString();
	}
	
	public int getType() {
		return SF_TYPE;
	}
}
