/*
*
* @file FolderElement.java
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
* @version $Id: FolderElement.java,v 1.9 2010/04/12 11:15:09 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package org.palo.viewapi.internal;

import java.util.HashMap;

import org.palo.api.Connection;
import org.palo.api.Hierarchy;
import org.palo.api.PaloObject;
import org.palo.api.parameters.ParameterProvider;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.View;


/**
 * <code>FolderElement</code>
 * This tree node represents an element in the tree. The element can have any
 * other Object as its source object and thus can represent views, reports,
 * or anything else.
 **/
public class FolderElement extends AbstractExplorerTreeNode 
                          implements ParameterProvider, ParameterReceiver {
	public static final int FE_TYPE = 3;
	private String sourceObjectDescription;
	private final HashMap <PaloObject, String> variableMapper;
	
	/**
	 * List of all currently supported parameter names.
	 */
	private String [] parameterNames;

	/**
	 * Map of all currently valid parameter values (for each parameter name).
	 */
	private final HashMap <String, Object []> possibleValues;

	/**
	 * Receiving parameter object (a view, report, chart, or anything at all).
	 */
	private ParameterReceiver sourceObject = null;
	
	/**
	 * Map of currently used parameter values.
	 */
	private final HashMap <String, Object> parameterValue;
	
	/**
	 * Internal member method to create a FolderElement from an xml
	 * specification. Note that this method is not part of the public API
	 * of FolderElement. So, do not use this method.
	 * 
	 * @param parent parent node of this folder element. Can be null.
	 * @param id globally unique id of this element.
	 * @param name name of this element.
	 * 
	 * @return a new FolderElement according to the specified parameters.
	 */
	public static FolderElement internalCreate(ExplorerTreeNode parent, 
			String id, String name) {
		return new FolderElement(parent, id, name);
	}
	
	/**
	 * Creates a new Folder Element with a given parent (may be null), an id,
	 * and a name.
	 * This Folder Element will not yet have a source object.
	 *
	 * @param parent the parent of this Folder Element (either a dynamic or
	 * a static folder). May be null.
	 * @param id the id of this Folder Element.
 	 * @param name the name of this Folder Element.
	 */
	FolderElement(ExplorerTreeNode parent, String id, String name) {
		super(parent, id, name);
		variableMapper = new HashMap<PaloObject, String>();
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
		if (parent != null) {
			parent.addChild(this);
		}		
	}

	/**
	 * Creates a new Folder Element with a given parent (may be null).
	 * This Folder Element will not yet have a source object.
	 *
	 * @param parent the parent of this Folder Element (either a dynamic or
	 * a static folder). May be null.
	 */
	public FolderElement(ExplorerTreeNode parent) {
		super("fe_", parent);
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
		variableMapper = new HashMap<PaloObject, String>();
		if (parent != null) {
			parent.addChild(this);
		}		
	}

	/**
	 * Creates a new Folder Element with a given parent (may be null), and a
	 * given name.
	 * This Folder Element will not yet have a source object.
	 * 
	 * @param parent the parent of this Folder Element (either a dynamic or
	 * a static folder). May be null.
	 * @param name the name of this Folder Element.
	 */
	public FolderElement(ExplorerTreeNode parent, String name) {
		super("fe_", parent, name);
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
		variableMapper = new HashMap<PaloObject, String>();
		if (parent != null) {
			parent.addChild(this);
		}		
	}
	
	FolderElement(String id, String name, DynamicFolder parent, 
			FolderElement original) {
		super(parent, id, name);
		possibleValues = (HashMap<String, Object[]>) original.possibleValues.clone();
		parameterValue = new HashMap<String, Object>();
		variableMapper = new HashMap<PaloObject, String>();
		for (PaloObject key: original.variableMapper.keySet()) {
			variableMapper.put(key, original.variableMapper.get(key));
		}
		for (String key: original.parameterValue.keySet()) {
			parameterValue.put(key, original.parameterValue.get(key));
		}
		sourceObject = original.sourceObject;
		sourceObjectDescription = original.sourceObjectDescription;
	}
	
	/**
	 * Returns all valid parameter names for this Folder Element.
	 */
	public String[] getParameterNames() {
		return parameterNames;
	}

	/**
	 * Returns all possible values for a given parameter. Returns an empty array
	 * either if no values are possible or a parameter with that name is not
	 * valid for the Folder Element.
	 */
	public Object[] getPossibleValuesFor(String parameterName) {
		if (!possibleValues.containsKey(parameterName)) {
			return new Object[0];
		}
		return possibleValues.get(parameterName);
	}

	/**
	 * Returns the source object of this Folder Element.
	 */	
	public ParameterReceiver getSourceObject() {
		if (sourceObject != null) {
			for (String key: parameterValue.keySet()) {
				sourceObject.setParameter(key, parameterValue.get(key));
			}
		}
		return sourceObject;
	}

	/**
	 * Sets the valid parameter names of this Folder Element. Thus, it can be
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
	 * Sets the source object for this Folder Element.
	 */
	public void setSourceObject(ParameterReceiver sourceObject) {
		this.sourceObject = sourceObject;
	}

	/**
	 * Returns the default value for the given parameter.
	 */
	public Object getDefaultValue(String parameterName) {
		if (sourceObject == null) {
			return null;
		}
		return sourceObject.getDefaultValue(parameterName);
	}

	/**
	 * Gets the current parameter value for the specified parameter name.
	 */
	public Object getParameterValue(String parameterName) {
		return this.parameterValue.get(parameterName);
	}

	/**
	 * Returns true if and only if the source object of this Folder Element is
	 * parameterized.
	 */
	public boolean isParameterized() {
		if (sourceObject == null) {
			return false;
		}		
		return sourceObject.isParameterized();
	}

	/**
	 * Sets the specified parameter.
	 */
	public void setParameter(String parameterName, Object parameterValue) {
		this.parameterValue.put(parameterName, parameterValue);
//		sourceObject.setParameter(parameterName, parameterValue);
	}

	private final String encodeSubset2(Subset2 source) {
		StringBuffer result = new StringBuffer("subset:");
		
		Connection con = source.getDimHierarchy().getDimension().
			getDatabase().getConnection();
		result.append(con.getServer());
		result.append(":");
		result.append(con.getService());
		result.append(":");
		result.append(source.getDimHierarchy().getDimension().getDatabase().getId());
		result.append(":");
		result.append(source.getDimHierarchy().getDimension().getId());
		result.append(":");
		result.append(source.getDimHierarchy().getId());
		result.append(":");
		result.append(source.getId());
		return result.toString();
	}
	
	private final String encodeHierarchy(Hierarchy source) {
		StringBuffer result = new StringBuffer("hierarchy:");
		
		Connection con = source.getDimension().getDatabase().getConnection();
		result.append(con.getServer());
		result.append(":");
		result.append(con.getService());
		result.append(":");
		result.append(source.getDimension().getDatabase().getId());
		result.append(":");
		result.append(source.getDimension().getId());
		result.append(":");
		result.append(source.getId());
		return result.toString();
	}

	/**
	 * Returns the persistence string for this Folder Element.
	 */
	public String getPersistenceString() {
		StringBuffer result = new StringBuffer("<folderElement ");
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
		if (sourceObject != null && sourceObject instanceof View) {
			result.append("source=\"");
			View v = (View) sourceObject;			
			result.append(StaticFolder.makePersistenceString(v));
			result.append("\" ");			
//		} else if (sourceObject != null && sourceObject instanceof WSSWorkbook) {
//			result.append("book=\"");
//			WSSWorkbook wb = (WSSWorkbook) sourceObject;			
//			result.append(wb.getApplication().getConnection().getId() + ":" +
//					      wb.getApplication().getId() + ":" +
//					      wb.getId());
//			result.append("\" ");
		} else if (sourceObjectDescription != null) {
			if (sourceObjectDescription.startsWith("source")) {
				result.append("source=\"" + sourceObjectDescription.substring(6) + "\" ");
			} else {
				result.append("book=\"" + sourceObjectDescription.substring(4) + "\" ");
			}
		}
		if (variableMapper.size() > 0) {
			result.append("mappings=\"");
			for (PaloObject key: variableMapper.keySet()) {
				StringBuffer text = new StringBuffer();
				if (key instanceof Subset2) {
					text.append(encodeSubset2((Subset2) key));
				} else if (key instanceof Hierarchy) {
					text.append(encodeHierarchy((Hierarchy) key));
				} else {
					continue;
				}
				result.append(text + ", ");
				result.append(variableMapper.get(key) + ", ");
			}
			result.append("\" ");
		}		
		result.append(">\n");
		result.append("</folderElement>\n");
		return result.toString();
	}
	
	public int getType() {
		return FE_TYPE;
	}
	
	public void setSourceObjectDescription(String text) {
		sourceObjectDescription = text;
	}
	
	public String getSourceObjectDescription() {
		return sourceObjectDescription;
	}
	
	public PaloObject [] getVariableMappingKeys() {
		return variableMapper.keySet().toArray(new PaloObject[0]);
	}
	
	public String getVariableMapping(Hierarchy key) {
		return variableMapper.get(key);
	}
		
	public String getVariableMapping(Subset2 key) {
		return variableMapper.get(key);
	}

	public void setVariableMapping(Subset2 subset, String value) {
		variableMapper.put(subset, value);
	}
	
	public void setVariableMapping(Hierarchy hierarchy, String value) {
		variableMapper.put(hierarchy, value);
	}
}
