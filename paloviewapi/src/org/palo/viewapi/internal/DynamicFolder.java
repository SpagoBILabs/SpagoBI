/*
*
* @file DynamicFolder.java
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
* @version $Id: DynamicFolder.java,v 1.9 2010/04/12 11:15:09 PhilippBouillon Exp $
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
import java.util.List;

import org.palo.api.Connection;
import org.palo.api.Consolidation;
import org.palo.api.Element;
import org.palo.api.ElementNode;
import org.palo.api.ElementNodeVisitor;
import org.palo.api.Hierarchy;
import org.palo.api.parameters.ParameterProvider;
import org.palo.api.parameters.ParameterReceiver;
import org.palo.api.subsets.Subset2;
import org.palo.viewapi.CubeView;


/**
 * <code>DynamicFolder</code>
 * This tree node represents a dynamic folder, i.e. a folder which contains
 * generated element or other dynamic folders.
 * 
 * Dynamic Folders take an existing view, a hierarchy, and (optionally) a subset
 * of this hierarchy and then replace the currently selected element of the
 * reference view with all elements of the (subset of the) hierarchy and thus
 * create "virtual" children of the folder.
 * 
 * Dynamic Folders can also have other Dynamic Folders as children. In this
 * case, the resulting views will receive their parameters from all their
 * parents.
 * 
 * Here's an example:
 * 
 * Hierarchy h1 has the elements h1e1, h1e2.
 * Hierarchy h2 has the elements h2e1, h2e2.
 * Hierarchy h3 has the elements h3e1, h3e2, h3e3.
 * Hierarchy h4 has the elements h4e1, h4e2.
 * Hierarchy h5 has the elements h5e1, h5e2, h5e3.
 * 
 * View v1 has h1 on the "rows" axis,
 *             h2 on the "columns" axis, and
 *             h3, h4, h5 on the "selected" axis, where every first element
 *             is selected (i.e. h3e1, h4e1, and h5e1).
 *             
 * Dynamic Folder "DF1" references the view v1 and the hierarchy h3 (no subset).
 * DF1 then has three children:
 * - v1 (h3e1)
 * - v1 (h3e2)
 * - v1 (h3e3)
 * 
 * They all reference the same view, but when opened, have a different element
 * selected in h3.
 * 
 * Now, we create Dynamic Folder "DF2". It references the view v2 and the
 * hierarchy h4 (no subset). We add DF2 as a child to DF1. We get:
 * 
 * DF1
 *  |-- DF2 (h3e1)
 *  |    |-- v1 (h3e1, h4e1)
 *  |    |-- v1 (h3e1, h4e2)
 *  |-- DF2 (h3e2)
 *  |    |-- v1 (h3e2, h4e1)
 *  |    |-- v1 (h3e2, h4e2)
 *  |-- DF2 (h3e3)
 *       |-- v1 (h3e3, h4e1)
 *       |-- v1 (h3e3, h4e2) 
 *
 * Note that although DF1 has three sub folders here, only _one_ is returned
 * (namely df2). So, you can create dynamic folders and then add one to another
 * _once_. You do not have to add them more times. The program will create the
 * appropriate number of children.
 *  
 * This class implements ParameterProvider and ParameterReceiver so it can
 * be used to transport (arbitrary) parameters. 
 * 
 * @author Philipp Bouillon
 * @version $Id: DynamicFolder.java,v 1.9 2010/04/12 11:15:09 PhilippBouillon Exp $
 **/
public class DynamicFolder extends AbstractExplorerTreeNode 
                          implements ParameterProvider {
	public static final int DF_TYPE = 1;
	
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
	//private ParameterReceiver sourceObject = null;
	
	/**
	 * The hierarchy which is used to generate children for this folder.
	 */
	private Hierarchy sourceHierarchy;
	
	/**
	 * The subset which is used to generate children for this folder (may be
	 * null, in which case all elements of the hierarchy are used).
	 */
	private Subset2 sourceSubset;	
	
	/**
	 * List of all sub folders of this dynamic folder.
	 */
	//private final LinkedHashSet <ExplorerTreeNode> subFolders;
	
	/**
	 * The element that is set in this folder (if this folder is a child of
	 * another folder).
	 */
	private Element variableElement;
	
//	/**
//	 * Cache for all children.
//	 */
//	private ExplorerTreeNode [] allKidsArray = null;
		
	private DynamicFolder parentDynamicFolder = null;
	
	/**
	 * Internal member method to create a DynamicFolder from an xml
	 * specification. Note that this method is not part of the public API
	 * of DynamicFolder. So, do not use this method.
	 * 
	 * @param parent parent node of this dynamic folder. Can be null.
	 * @param sourceHierarchy the hierarchy from which this folder takes its
	 * elements. Must not be null.
	 * @param sourceSubset the subset of elements from which this folder takes
	 * its elements. May be null.
	 * @param id globally unique id of this folder.
	 * @param name name of this folder.
	 * 
	 * @return a new DynamicFolder according to the specified parameters.
	 */
	public static DynamicFolder internalCreate(ExplorerTreeNode parent, 
			Hierarchy sourceHierarchy, Subset2 sourceSubset, 
			String id, String name) {
		return new DynamicFolder(parent, sourceHierarchy, sourceSubset, id, name);
	}
		
	/**
	 * Creates a new Dynamic Folder.
	 * 
	 * @param parent parent node of this dynamic folder. Can be null.
	 * @param sourceHierarchy the hierarchy from which this folder takes its
	 * elements. Must not be null.
	 * @param sourceSubset the subset of elements from which this folder takes
	 * its elements. May be null.
	 * @param id globally unique id of this folder.
	 * @param name name of this folder.
	 */
	private DynamicFolder(ExplorerTreeNode parent, Hierarchy sourceHierarchy,
			Subset2 sourceSubset, String id, String name) {
		super(id, true);
		this.name = name;
		this.sourceHierarchy = sourceHierarchy;
		this.sourceSubset = sourceSubset;
		setParent(parent);
		if (parent != null) {
			parent.addChild(this);
		}
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
	}
	
	/**
	 * Creates a new Dynamic Folder. An id will automatically be generated.
	 * 
	 * @param parent parent node of this dynamic folder. Can be null.
	 * @param sourceHierarchy the hierarchy from which this folder takes its
	 * elements. Must not be null.
	 * @param sourceSubset the subset of elements from which this folder takes
	 * its elements. May be null.
	 */
	public DynamicFolder(ExplorerTreeNode parent, 
			Hierarchy sourceHierarchy, Subset2 sourceSubset) {
		super("df_", parent);
		this.sourceHierarchy = sourceHierarchy;
		this.sourceSubset = sourceSubset;
		if (parent != null) {
			parent.addChild(this);
		}
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();	
		parameterValue = new HashMap<String, Object>();
	}

	/**
	 * Creates a new Dynamic Folder with the given name. An id will 
	 * automatically be generated.
	 * 
	 * @param parent parent node of this dynamic folder. Can be null.
	 * @param sourceHierarchy the hierarchy from which this folder takes its
	 * elements. Must not be null.
	 * @param sourceSubset the subset of elements from which this folder takes
	 * its elements. May be null.
	 * @param name the name of the folder.
	 */
	public DynamicFolder(ExplorerTreeNode parent, 
			Hierarchy sourceHierarchy, Subset2 sourceSubset, 
			String name) {
		super("df_", parent, name);
		this.sourceHierarchy = sourceHierarchy;
		this.sourceSubset = sourceSubset;
		if (parent != null) {
			parent.addChild(this);
		}
		parameterNames = new String[0];
		possibleValues = new HashMap<String, Object[]>();
		parameterValue = new HashMap<String, Object>();
	}

	DynamicFolder(String id, String name, DynamicFolder parent, DynamicFolder original) {
		super(parent, id, name);
		this.sourceHierarchy = original.sourceHierarchy;
		this.sourceSubset = original.sourceSubset;
		possibleValues = (HashMap<String, Object[]>) original.possibleValues.clone();
		parameterNames = original.parameterNames.clone();
		children = (ArrayList<ExplorerTreeNode>) original.children.clone();
		parameterValue = new HashMap<String, Object>();
		for (String key: original.parameterValue.keySet()) {
			parameterValue.put(key, original.parameterValue.get(key));
		}
	}
	
	/**
	 * Internal constructor. Used to create dynamic folders as children of
	 * other dynamic folders.
	 * 
	 * @param parent parent node of this dynamic folder. Can be null.
	 * @param sourceHierarchy the hierarchy from which this folder takes its
	 * elements. Must not be null.
	 * @param sourceSubset the subset of elements from which this folder takes
	 * its elements. May be null.
	 * @param name the name of the folder.
	 * @param internal not used, only to mark it as internal constructor.
	 */
//	DynamicFolder(ExplorerTreeNode parent, Hierarchy sourceHierarchy,
//			Subset2 sourceSubset, String name, boolean internal) {
//		super("df_");
//		this.sourceHierarchy = sourceHierarchy;
//		this.sourceSubset = sourceSubset;
//		this.parent = parent;
//		this.name = name;
//		parameterNames = new String[0];
//		possibleValues = new HashMap<String, Object[]>();		
//		this.subFolders = new LinkedHashSet<ExplorerTreeNode>();	
//		if (parent != null && parent instanceof AbstractExplorerTreeNode) {
//			((AbstractExplorerTreeNode) parent).children.add(this);
//			if (parent instanceof DynamicFolder) {
//				setSourceObject(((DynamicFolder) parent).getSourceObject());
//			}
//		}		
//	}
	
	/**
	 * Returns all valid parameter names for this dynamic folder.
	 */
	public String[] getParameterNames() {
		return parameterNames;
	}

	/**
	 * Returns all possible values for a given parameter. Returns an empty array
	 * either if no values are possible or a parameter with that name is not
	 * valid for the dynamic folder.
	 */
	public Object[] getPossibleValuesFor(String parameterName) {
		if (!possibleValues.containsKey(parameterName)) {
			return new Object[0];
		}
		return possibleValues.get(parameterName);
	}

	/**
	 * Returns the source object of this Dynamic Folder. Usually a view.
	 */
	public ParameterReceiver getSourceObject() {
		return null; //sourceObject;
	}

	/**
	 * Sets the valid parameter names of this Dynamic Folder. Thus, it can be
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
	 * Sets the source object for this Dynamic Folder.
	 */
	public void setSourceObject(ParameterReceiver sourceObject) {
		//this.sourceObject = sourceObject;
//		allKidsArray = null;
	}
	
	/**
	 * Returns the source hierarchy for this Dynamic Folder.
	 * @return the source hierarchy for this Dynamic Folder.
	 */
	public Hierarchy getSourceHierarchy() {
		return sourceHierarchy;
	}
	
	/**
	 * Returns the source subset for this Dynamic Folder.
	 * @return the source subset for this Dynamic Folder.
	 */
	public Subset2 getSourceSubset() {
		return sourceSubset;
	}
	
	/**
	 * Returns the variable element if this Dynamic Folder is a child of
	 * another Dynamic Folder. The variable element is then the element at the
	 * position of this child. ;) [i.e. if this folder is the third child of its
	 * parent, the variable element is the third element of the element list of
	 * its parent].
	 * 
	 * @return the variable element.
	 */
	public Element getVariableElement() {
		return variableElement;
	}
	
	/**
	 * Sets the variable element.
	 * 
	 * @param e the variable element.
	 */
	void setVariableElement(Element e) {
		variableElement = e;
	}

	/**
	 * Returns the default value for a given parameter. 
	 * The default element is the first element of the folder's element list,
	 * or null, if no such element exists.
	 */
	public Object getDefaultValue(String parameterName) {
		if (sourceSubset != null) {
			Element [] els = sourceSubset.getElements();
			if (els != null && els.length > 0) {
				return els[0];				
			}
			return null;
		}
		if (sourceHierarchy.getElementCount() > 0) {
			return sourceHierarchy.getElementAt(0);
		}
		return null;
	}

	/**
	 * Returns the current value of the specified parameter (i.e. the source
	 * hierarchy).
	 */
	public Object getParameterValue(String parameterName) {		
		if (parameterName.equals("Hierarchy")) {
			return sourceHierarchy;
		} else if (parameterName.equals("Subset")) {
			return sourceSubset;
		}
		return this.parameterValue.get(parameterName);
	}

	/**
	 * Returns true.
	 */
	public boolean isParameterized() {
		return true;
	}

	/**
	 * Sets a new value for the specified parameter.
	 */
	public void setParameter(String parameterName, Object parameterValue) {
		if (parameterName.equals("Hierarchy") && parameterValue instanceof Hierarchy) {
			sourceHierarchy = (Hierarchy) parameterValue;			
		} else if (parameterName.equals("Subset") && parameterValue instanceof Subset2) {
			sourceSubset = (Subset2) parameterValue;
		}
		this.parameterValue.put(parameterName, parameterValue);
	}	
		
//	/**
//	 * Returns all children of this Dynamic Folder. Note that this method
//	 * returns the _generated_ children. If you want to have a list of all
//	 * Dynamic Folders that are children of this Folder, use 
//	 * {@link DynamicFolder#getSubFolders()} instead.
//	 */
//	public ExplorerTreeNode [] getChildren() {		
//		return getSubFolders();
//	}
	
	private final String getElementPath(ElementNode e) {
		StringBuffer buffer = new StringBuffer(e.getElement().getName());
		ElementNode p = e.getParent();
		while (p != null) {
			buffer.insert(0, p.getElement().getName() + "/");
			p = p.getParent();
		}
		return buffer.toString();
	}
	
    private static void traverse(ElementNode n, ElementNodeVisitor v)
    {
        traverse(n, null, v);
    }
    
    static void traverse(ElementNode n, ElementNode p, ElementNodeVisitor v)
    {
        v.visit(n, p);
        Element children[] = n.getElement().getChildren();
        Consolidation consolidations[] = n.getElement().getConsolidations();
        if (children == null)
            return;
        for (int i = 0; i < children.length; ++i)
        {
        	if(children[i] == null)
        		continue;
            ElementNode child = new ElementNode(children[i], consolidations[i]);
            n.forceAddChild(child);
            traverse(child, n, v);
        }
    }

	private final ElementNode [] getAllElementNodes(Subset2 subset) {
		final ArrayList <ElementNode> allnodes = new ArrayList<ElementNode>();
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode node, ElementNode parent) {
				allnodes.add(node);
			}
		};
		ElementNode [] roots = subset.getRootNodes();
		if (roots != null) {
			for (int i = 0; i < roots.length; ++i) {
				traverse(roots[i], visitor);
			}
		}
		return allnodes.toArray(new ElementNode[0]);
	}
	
	public ExplorerTreeNode [] getCalculatedChildren() {
		if (children.size() == 0) {
			return new ExplorerTreeNode[0];
		}
		if (sourceHierarchy == null && sourceSubset == null) {
			return children.toArray(new ExplorerTreeNode[0]);
		}
		
		ElementNode [] elems;		
		if (sourceSubset == null) {
			elems = sourceHierarchy.getAllElementNodes();				
		} else {
			elems = getAllElementNodes(sourceSubset);
		}
		if (elems == null || elems.length == 0) {
			return children.toArray(new ExplorerTreeNode[0]);
		}
		
		List <ExplorerTreeNode> allKids = new ArrayList<ExplorerTreeNode>();
		for (ExplorerTreeNode kid: children) {
			for (ElementNode e: elems) {
				ExplorerTreeNode n;				
				String elementPathName = getElementPath(e);
				String newId = kid.getId() + "_" + e.getElement().getId() + elementPathName;
				String newName = kid.getName() + " (" + elementPathName + ")";
				Object o = kid.getParameterValue(CubeView.PARAMETER_ELEMENT);
				kid.setParameter(CubeView.PARAMETER_ELEMENT, getParameterValue(CubeView.PARAMETER_ELEMENT));
				kid.addParameterValue(CubeView.PARAMETER_ELEMENT, o);
				kid.addParameterValue(CubeView.PARAMETER_ELEMENT, e.getElement());
				if (kid instanceof FolderElement) {
					n = new FolderElement(newId, newName, this, (FolderElement) kid);
				} else if (kid instanceof StaticFolder) {
					n = new StaticFolder(newId, newName, this, (StaticFolder) kid);
				} else {
					n = new DynamicFolder(newId, newName, this, (DynamicFolder) kid);
				}
				kid.setParameter(CubeView.PARAMETER_ELEMENT, o);
				allKids.add(n);
			}
		}
		
		return allKids.toArray(new ExplorerTreeNode[0]);
	}
	
//	public ExplorerTreeNode [] getCalculatedChildren() {
////		if (allKidsArray != null) {
////			return allKidsArray;
////		}
//		
//		DynamicFolder node = this;
//		
//		ArrayList <Element> allVars = new ArrayList<Element>();
//		while (node.getParent() != null && 
//			   node.getParent() instanceof DynamicFolder) {
//			DynamicFolder parent = (DynamicFolder) node.getParent();								
//			allVars.add(node.getVariableElement());
//			node = parent;
//	    }
//		Element [] allElems = new Element[allVars.size() + 1];
//		System.arraycopy(allVars.toArray(new Element[0]), 0, allElems,
//				0, allVars.size());
//		Element [] elems;		
//		if (sourceSubset == null) {
//			if (sourceHierarchy == null) {
//				return new ExplorerTreeNode[0];
//			} else {
//				elems = sourceHierarchy.getElementsInOrder();				
//			}
//		} else {
//			elems = sourceSubset.getElements();
//		}
//		int free = allVars.size();
//		ArrayList <ExplorerTreeNode> allKids = new ArrayList<ExplorerTreeNode>();
//
//		if (subFolders.size() == 0) {
//			for (Element e: elems) {
//				FolderElement fe = new FolderElement(this,
//						e.getName());
//				fe.setSourceObject(getSourceObject());
//				allElems[free] = e;
//				fe.setParameter(CubeView.PARAMETER_ELEMENT, allElems);
//				allKids.add(fe);
//				allElems = allElems.clone();
//			}
////			allKidsArray = allKids.toArray(new FolderElement[0]);
//			return allKids.toArray(new FolderElement[0]);
//		} else {
//			for (ExplorerTreeNode etn: subFolders) {
//				for (Element e: elems) {
//					if (etn instanceof DynamicFolder) {
//						DynamicFolder df = (DynamicFolder) etn;
//						DynamicFolder ddf = new DynamicFolder(this, df.getSourceHierarchy(),
//								df.getSourceSubset(),							 
//								df.getName() + " (" + e.getName() + ")", false);
//						ddf.subFolders.addAll(df.subFolders);
//						ddf.setSourceObject(getSourceObject());
//						ddf.setParentDynamicFolder(df);
//						ddf.setVariableElement(e);
//						allElems[free] = e;
//						ddf.setParameter(CubeView.PARAMETER_ELEMENT, allElems);
//						allKids.add(ddf);
//						allElems = allElems.clone();
//					} else if (etn instanceof StaticFolder) {
//						StaticFolder sf = (StaticFolder) etn;
//						StaticFolder ssf = new StaticFolder(this, sf.getName() + " (" + e.getName() + ")");
//						ssf.children.addAll(sf.children);
//						ssf.setSourceObject(getSourceObject());
//						allElems[free] = e;
//						ssf.setParameter(CubeView.PARAMETER_ELEMENT, allElems);
//						allKids.add(ssf);
//						allElems = allElems.clone();
////						allKids.add(etn);
//					} else if (etn instanceof FolderElement) {
//						FolderElement fe = (FolderElement) etn;
//						FolderElement ffe = new FolderElement(this, fe.getName() + " (" + e.getName() + ")");
//						ffe.setSourceObject(getSourceObject());
//						allElems[free] = e;
//						ffe.setParameter(CubeView.PARAMETER_ELEMENT, allElems);
//						allKids.add(ffe);
//						allElems = allElems.clone();
//					}
//				}				
//			}
//			return allKids.toArray(new ExplorerTreeNode[0]);
//		}
//	}

	void setParentDynamicFolder(DynamicFolder df) {
		parentDynamicFolder = df;
	}
	
	public DynamicFolder getParentDynamicFolder() {
		return parentDynamicFolder;
	}
	
	/**
	 * Returns the index of the specified child or -1 if no such child exists.
	 * 
	 * @param child the child of which the index is to be returned.
	 * @return the index of the child or -1 if no such child exists.
	 * @deprecated please do not use anymore
	 */
	public final int getChildIndex(ExplorerTreeNode child) {
//		if (allKidsArray == null) {
//			getChildren();
//		}
//		int index = 0;
//		for (ExplorerTreeNode n: allKidsArray) {
//			if (n == child) {
//				return index;
//			}
//			index++;
//		}
		return -1;
	}
			
	/**
	 * Returns an array of all sub folders of this Dynamic Folder.
	 * @return an array of all sub folders of this Dynamic Folder.
	 */
//	public ExplorerTreeNode [] getSubFolders() {
//		return subFolders.toArray(new ExplorerTreeNode[0]);
//	}
	
	/**
	 * Adds a child to this Dynamic Folder. If the child is not a Dynamic
	 * Folder, the call will fail (i.e. return false).
	 */
//	public boolean addChild(ExplorerTreeNode child) {
////		if (!(child instanceof DynamicFolder)) {
////			return false;
////		}
//		if (child instanceof DynamicFolder) {
//			((DynamicFolder) child).parent = this;
//		} else if (child instanceof StaticFolder) {
//			((StaticFolder) child).parent = this;
//		}
//		return subFolders.add(child);
//	}
	
	/**
	 * Removes the specified child from this Dynamic Folder.
	 */
//	public boolean removeChild(ExplorerTreeNode child) {
////		if (!(child instanceof DynamicFolder)) {
////			return false;
////		}
//		return subFolders.remove(child);
//	}
	
	/**
	 * Removes the specified child from this Dynamic Folder.
	 */
//	public boolean removeChildById(String id) {
//		ExplorerTreeNode removeNode = null;
//		for (ExplorerTreeNode node: subFolders) {
//			if (node.getId().equals(id)) {
//				removeNode = node;
//				break;
//			}
//		}
//		if (removeNode != null) {
//			return subFolders.remove(removeNode);
//		}
//		return false;
//	}
	
	/**
	 * Removes the specified child from this Dynamic Folder.
	 */
//	public boolean removeChildByName(String name) {
//		ExplorerTreeNode removeNode = null;
//		for (ExplorerTreeNode node: subFolders) {
//			if (node.getName().equals(name)) {
//				removeNode = node;
//				break;
//			}
//		}
//		if (removeNode != null) {
//			return subFolders.remove(removeNode);
//		}
//		return false;		
//	}
	
	/**
	 * Clears all children (i.e. all sub folders).
	 */
//	public boolean clearAllChildren() {
//		subFolders.clear();
//		return true;
//	}

	private final String tag(String tagName, String value) {
		return tagName + "=\"" + value + "\" ";
	}
	
	/**
	 * Returns the persistence string for this Dynamic Folder.
	 */
	public String getPersistenceString() {
		StringBuffer result = new StringBuffer("<dynamicFolder ");
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
//			result.append(StaticFolder.makePersistenceString(v));
//			result.append("\" ");			
//		}
		if (sourceHierarchy != null) {
			Connection con = sourceHierarchy.getDimension().getDatabase().getConnection();
			result.append(tag("connectionServer", con.getServer()));
			result.append(tag("connectionService", con.getService()));
			result.append(tag("databaseId", sourceHierarchy.getDimension().getDatabase().getId()));
			result.append(tag("dimensionId", sourceHierarchy.getDimension().getId()));
			result.append(tag("hierarchyId", sourceHierarchy.getId()));
		}
		if (sourceSubset != null) {
			result.append("subset=\"");
			result.append(sourceSubset.getDimHierarchy().getDimension().getId());
			result.append("@_@");
			result.append(sourceSubset.getDimHierarchy().getId());
			result.append("@_@");
			result.append(sourceSubset.getId());
			result.append("@_@");
			result.append(sourceSubset.getType());
			result.append("\" ");
		}
		result.append(">\n");
		for (ExplorerTreeNode kid: children) {
			result.append(kid.getPersistenceString());
		}
		result.append("</dynamicFolder>\n");
		return result.toString();
	}
	
//	protected void finalize() throws Throwable {
//		super.finalize();
//		
//		subFolders.clear();
//	}
	
	public Hierarchy setSourceHierarchy(Hierarchy sourceHierarchy) {
//		allKidsArray = null;
		Hierarchy old = this.sourceHierarchy;
		this.sourceHierarchy = sourceHierarchy;
		return old;
	}
	
	public Subset2 setSourceSubset(Subset2 sourceSubset) {
		Subset2 old = this.sourceSubset;
		this.sourceSubset = sourceSubset;
		return old;
	}
	
	public int getType() {
		return DF_TYPE;
	}
}
