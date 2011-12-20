/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.tree;

// TODO: Auto-generated Javadoc
/**
 * Interface of classes that can build a java script dTree object. By contract class
 * of this type have only to create the tree and build its structure. The iclusion
 * of the dTree.js script and all of css files used to render it should be handled
 * separatly by the caller.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public interface IQbeTree {
	
	/**
	 * Creates the tree.
	 */
	public void createTree();
	
	/**
	 * Creates the tree.
	 * 
	 * @param treeName the tree name
	 */
	public void createTree(String treeName);
	
	/**
	 * Adds the node.
	 * 
	 * @param id the id
	 * @param pid the pid
	 * @param nname the nname
	 * @param url the url
	 * @param title the title
	 * @param target the target
	 * @param icon the icon
	 * @param iconOpen the icon open
	 * @param open the open
	 * @param onclick the onclick
	 * @param checkName the check name
	 * @param checkValue the check value
	 * @param checked the checked
	 */
	void addNode(String id, 
			 String pid, 
			 String nname, 
			 String url, 
			 String title, 
			 String target, 
			 String icon, 
			 String iconOpen, 
			 String open, 
			 String onclick, 
			 String checkName, 
			 String checkValue, 
			 String checked);
	


	/**
	 * Gets the tree constructor script.
	 * 
	 * @return the tree constructor script
	 */
	public String getTreeConstructorScript();
}
