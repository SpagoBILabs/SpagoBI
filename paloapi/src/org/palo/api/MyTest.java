/*
*
* @file MyTest.java
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
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: MyTest.java,v 1.2 2010/02/09 11:44:57 PhilippBouillon Exp $
*
*/

package org.palo.api;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.tensegrity.palojava.ElementInfo;

public class MyTest {
	interface ElementNodeVisitor {
		void visit(ElementNode elementNode, ElementNode parent);
	}
	
    public static interface ElementVisitor {
        void visit(Element element, Element parent);
    }
	
	class Element {
		private final Object [] objs;
		private ArrayList <Element> kids;
		
		Element(String con, String db, String dim, String id) {
			objs = new Object[] {Element.class, con, db, dim, id};
			kids = new ArrayList<Element>();
		}
		
		public String getId() {
			return (String) objs[4];
		}
		
		public int hashCode() {
			int hc = 23;
			for (int i = 0; i < objs.length; ++i) {
				if (objs[i] != null)
					hc += 37 * objs[i].hashCode();
			}
			return hc;
		}
		
		public Element [] getChildren() {
			return kids.toArray(new Element[0]);
		}
		
		public final boolean equals(Object other) {
			if (other instanceof Element) {
				Element e = (Element) other;
				if (objs.length == e.objs.length) {
					for (int i = 0; i < objs.length; i++) {
						if (!objs[i].equals(e.objs[i])) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}		
		
		public void addChild(Element e) {
			kids.add(e);
		}
	}
	
	class ElementNode {
		private ElementNode parent;
		private final Element element;
		private final LinkedHashSet <ElementNode> children;
		
		ElementNode(Element element) {
			this.element = element;
			children = new LinkedHashSet<ElementNode>();
		}
		
		public Element getElement() {
			return element;
		}
		
		ElementNode getParent() {
			return parent;
		}
		
		public void setParent(ElementNode newParent) {
			parent = newParent;
		}
		
	    public final synchronized void addChild(ElementNode child)
	    {
	    	if (!children.contains(child)) {
	    		child.setParent(this);
	    		children.add(child);				
			}
	    }
		
	    public ElementNode [] getChildren() {
	    	return children.toArray(new ElementNode[0]);
	    }
	    
	    public final synchronized void removeChild(ElementNode child)
	    {
	    	if(children.remove(child)) {
	        	child.setParent(null);
	        }
	    }	
	    
	    public final boolean equals(Object obj)
	    {
	        if (!(obj instanceof ElementNode))
	            return false;
	        
	        ElementNode other = (ElementNode) obj;
	        
	        boolean eq = element.equals(other.getElement());
	        if (parent != null && other.getParent() != null)
	        {
	            eq &= parent.getElement().equals(other.getParent().getElement());
	        }
	        else
	        {
	            eq &= parent == null && other.getParent() == null;
	        }
	        
	        return eq;
	    }
	    
	    public final int hashCode() {
			int hc = 3;
			hc += 3 * element.hashCode();
			if (parent != null)
				hc += 3 * parent.hashCode();

			return hc;
		}	    
	}
	
	Element [] e;
	
	public final ElementNode[] getElementsTree() {
		Element [] roots = new Element [] {e[0], e[1], e[2], e[3]};
		
		final ArrayList rootnodes = new ArrayList();
		ElementNodeVisitor visitor = new ElementNodeVisitor() {
			public void visit(ElementNode node, ElementNode parent) {
				if (parent == null)
					rootnodes.add(node);
			}
		};		
		if (roots != null) {
			for (int i = 0; i < roots.length; ++i) {
				ElementNode rootNode = new ElementNode(roots[i]);
				traverse(rootNode, visitor);
			}
		}
		return (ElementNode[]) rootnodes.toArray(new ElementNode[0]);
	}

    public void traverse(Element e, ElementVisitor v) {
    	traverse(e, null, v);
    }
    
    void traverse(Element e, Element p, ElementVisitor v) {
        v.visit(e, p);
        Element children[] = e.getChildren();
        if (children == null)        	
        	return;        
        for (int i = 0; i < children.length; ++i) {
            traverse(children[i], e, v);
        }
    }
    
    //-------------------------------------------------------------------------
    
    public void traverse(ElementNode n, ElementNodeVisitor v) {
        traverse(n, null, v);
    }
    
    void traverse(ElementNode n, ElementNode p, ElementNodeVisitor v)
    {
        v.visit(n, p);
        Element children[] = n.getElement().getChildren();
        if (children == null)
            return;
        for (int i = 0; i < children.length; ++i)
        {
        	if(children[i] == null)
        		continue;
            ElementNode child = new ElementNode(children[i]);
            n.addChild(child);
            traverse(child, n, v);
        }
    }
	
    private void trav(ElementNode [] nodes) {
    	if (nodes == null) {
    		return;
    	}
    	for (ElementNode n: nodes) {
			if (n.getElement().getId().equals("e3")) {
				for (ElementNode nn: n.getChildren()) {
					n.removeChild(nn);
				}
			}			
			trav(n.getChildren());
    	}
    }
    
	MyTest() {
		e = new Element[4];
		
		for (int i = 0; i < 4; i++) {
			e[i] = new Element("localhost", "palo", "d1", "e" + i);
		}
		e[3].addChild(e[1]);
		e[3].addChild(e[2]);
		ElementNode [] nodes = getElementsTree();
		for (ElementNode n: nodes) {
			if (n.getElement().getId().equals("e3")) {
				for (ElementNode nn: n.getChildren()) {
					n.removeChild(nn);
				}
			}						
		}
		//trav(nodes);
	}
	
	public static void main(String ...args) {
		new MyTest();
	}
}
