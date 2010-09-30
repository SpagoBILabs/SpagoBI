/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.kpi.ou.bo;

import java.util.Iterator;
import java.util.List;


/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class Tree<E> {

	public static final String NODES_PATH_SEPARATOR = "/";
	
	private Node<E> root;
	
	public Tree(){}
	
	public Tree(Node<E> node){
		this.root = node;
	}
	
	public Node<E> getRoot() {
		return root;
	}
	
	public boolean containsPath(String path) {
		return containsPath(root, path);
	}
	
	private boolean containsPath(Node node, String path) {
		if (node.getPath().equals(path)) {
			return true;
		}
		if (path.startsWith(node.getPath() + NODES_PATH_SEPARATOR)) {
			List<Node<E>> children = node.getChildren();
			Iterator<Node<E>> it = children.iterator();
			while (it.hasNext()) {
				Node<E> aChild = it.next();
				if (containsPath(aChild, path))
					return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Tree [root=" + root + "]";
	}

}
