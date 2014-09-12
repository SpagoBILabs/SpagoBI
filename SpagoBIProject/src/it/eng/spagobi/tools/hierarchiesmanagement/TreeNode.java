/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.hierarchiesmanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;
import java.util.Vector;

/**
 * A <code>TreeNode</code> is a general-purpose node in a tree data structure.
 * 
 * <p>
 * 
 * A tree node may have at most one parent and 0 or more children.
 * <code>TreeNode</code> provides operations for examining and modifying a
 * node's parent and children and also operations for examining the tree that
 * the node is a part of. A node's tree is the set of all nodes that can be
 * reached by starting at the node and following all the possible links to
 * parents and children. A node with no parent is the root of its tree; a node
 * with no children is a leaf. A tree may consist of many subtrees, each node
 * acting as the root for its own subtree.
 * <p>
 * This class provides iterator for efficiently traversing a tree or subtree in
 * various orders or for following the path between two nodes. A
 * <code>TreeNode</code> may also hold a reference to a user object, the use of
 * which is left to the user. Asking a <code>TreeNode</code> for its string
 * representation with <code>toString()</code> returns the string representation
 * of its user object.
 * <p>
 * <b>This is not a thread safe class.</b>If you intend to use a TreeNode (or a
 * tree of TreeNodes) in more than one thread, you need to do your own
 * synchronizing. A good convention to adopt is synchronizing on the root node
 * of a tree.
 * 
 * <p>
 * Most source code is copied from <code>DefaultMutableTreeNode</code> by Rob
 * Davis.
 * 
 * 
 * @author Yifan Peng
 */
@Deprecated
public class TreeNode implements Iterable<TreeNode> {

	final class BreadthFirstIterator implements Iterator<TreeNode> {

		protected Queue<Iterator<TreeNode>> queue;

		public BreadthFirstIterator(TreeNode rootNode) {
			super();
			Vector<TreeNode> v = new Vector<TreeNode>(1);
			v.addElement(rootNode); // PENDING: don't really need a vector
			queue = new LinkedList<Iterator<TreeNode>>();
			queue.offer(v.iterator());
		}

		public boolean hasNext() {
			return (!queue.isEmpty() && queue.peek().hasNext());
		}

		public TreeNode next() {
			Iterator<TreeNode> enumer = queue.peek();
			TreeNode node = enumer.next();
			Iterator<TreeNode> children = node.childrenIterator();

			if (!enumer.hasNext()) {
				queue.poll();
			}
			if (children.hasNext()) {
				queue.offer(children);
			}
			return node;
		}

		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported.");
		}
	}

	final class LeavesIterator implements Iterator<TreeNode> {

		Iterator<TreeNode> depthFirstItr;
		TreeNode nextLeaf;

		LeavesIterator(TreeNode rootNode) {
			depthFirstItr = rootNode.depthFirstIterator();
		}

		public boolean hasNext() {
			nextLeaf = null;
			while (depthFirstItr.hasNext()) {
				TreeNode next = depthFirstItr.next();
				if (next.isLeaf()) {
					nextLeaf = next;
					return true;
				}
			}
			return false;
		}

		public TreeNode next() {
			return nextLeaf;
		}

		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported.");
		}
	}

	final class PostorderIterator implements Iterator<TreeNode> {

		protected TreeNode root;
		protected Iterator<TreeNode> children;
		protected Iterator<TreeNode> subtree;

		public PostorderIterator(TreeNode rootNode) {
			super();
			root = rootNode;
			children = root.childrenIterator();
			subtree = EMPTY_ITERATOR;
		}

		public boolean hasNext() {
			return root != null;
		}

		public TreeNode next() {
			TreeNode retval;

			if (subtree.hasNext()) {
				retval = subtree.next();
			} else if (children.hasNext()) {
				subtree = new PostorderIterator(children.next());
				retval = subtree.next();
			} else {
				retval = root;
				root = null;
			}

			return retval;
		}

		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported.");
		}

	}

	private final class PreorderIterator implements Iterator<TreeNode> {

		private final Stack<Iterator<TreeNode>> stack = new Stack<Iterator<TreeNode>>();

		PreorderIterator(TreeNode rootNode) {
			super();
			List<TreeNode> l = new Vector<TreeNode>(1);
			l.add(rootNode); // PENDING: don't really need a vector
			stack.push(l.iterator());
		}

		public boolean hasNext() {
			return (!stack.empty() && stack.peek().hasNext());
		}

		public TreeNode next() {
			Iterator<TreeNode> itr = stack.peek();
			TreeNode node = itr.next();
			Iterator<TreeNode> childrenItr = node.childrenIterator();

			if (!itr.hasNext()) {
				stack.pop();
			}
			if (childrenItr.hasNext()) {
				stack.push(childrenItr);
			}
			return node;
		}

		public void remove() {
			throw new UnsupportedOperationException("remove() is not supported.");
		}

	}

	// ******************************************

	private Object obj;

	private TreeNode parent;

	private String key;

	private List<TreeNode> children;

	private final boolean allowsChildren;

	private final List<String> childrenKeys;

	/**
	 * An iterator that is always empty. This is used when an iterator of a leaf
	 * node's children is requested.
	 */
	static private final Iterator<TreeNode> EMPTY_ITERATOR = Collections.<TreeNode> emptyList().iterator();
	static private final List<TreeNode> EMPTY_LIST = Collections.emptyList();

	/**
	 * Creates a tree node that has no parent and no children, but which allows
	 * children.
	 */
	public TreeNode() {
		this(null, null);
	}

	/**
	 * Creates a tree node with no parent, no children, but which allows
	 * children, and initializes it with the specified user object.
	 * 
	 * @param obj
	 *            an Object provided by the user that constitutes the node's
	 *            data
	 */
	public TreeNode(Object obj, String key) {
		this.obj = obj;
		parent = null;
		allowsChildren = true;
		childrenKeys = new ArrayList<String>();
	}

	/**
	 * Removes <code>newChild</code> from its present parent (if it has a
	 * parent), sets the child's parent to this node, and then adds the child to
	 * this node's child array at index <code>childIndex</code>.
	 * <code>newChild</code> must not be null and must not be an ancestor of
	 * this node.
	 * 
	 * @param newChild
	 *            the MutableTreeNode to insert under this node
	 * @param childIndex
	 *            the index in this node's child array where this node is to be
	 *            inserted
	 * @exception ArrayIndexOutOfBoundsException
	 *                if <code>childIndex</code> is out of bounds
	 * @exception IllegalArgumentException
	 *                if <code>newChild</code> is null or is an ancestor of this
	 *                node
	 * @exception IllegalStateException
	 *                if this node does not allow children
	 * @see #isNodeDescendant
	 */
	public void add(int childIndex, TreeNode newChild) {
		if (!allowsChildren) {
			throw new IllegalStateException("node does not allow children");
		} else if (newChild == null) {
			throw new IllegalArgumentException("new child is null");
		} else if (isNodeAncestor(newChild)) {
			throw new IllegalArgumentException("new child is an ancestor");
		}

		TreeNode oldParent = newChild.getParent();

		if (oldParent != null) {
			oldParent.remove(newChild);
		}
		newChild.setParent(this);
		if (children == null) {
			children = new LinkedList<TreeNode>();
		}
		children.add(childIndex, newChild);
	}

	// added by MC
	public void add(int childIndex, TreeNode newChild, String childKey) {
		add(childIndex, newChild);
		childrenKeys.add(childKey);
	}

	/**
	 * Removes <code>newChild</code> from its parent and makes it a child of
	 * this node by adding it to the end of this node's child array.
	 * 
	 * @see #insert
	 * @param newChild
	 *            node to add as a child of this node
	 * @exception IllegalArgumentException
	 *                if <code>newChild</code> is null
	 * @exception IllegalStateException
	 *                if this node does not allow children
	 */
	public void add(TreeNode newChild) {
		if (newChild != null && newChild.getParent() == this) {
			add(getChildCount() - 1, newChild);
		} else {
			add(getChildCount(), newChild);
		}
	}

	// Added by MC
	public void add(TreeNode newChild, String key) {
		if (newChild != null && newChild.getParent() == this) {
			add(getChildCount() - 1, newChild, key);
		} else {
			add(getChildCount(), newChild, key);
		}
	}

	// public void addChildren(List<TreeNode> children) {
	// for (TreeNode child : children) {
	// add(child);
	// }
	// }

	/**
	 * Creates and returns an iterator that traverses the subtree rooted at this
	 * node in breadth-first order. The first node returned by the iterator's
	 * <code>next()</code> method is this node.
	 * <P>
	 * 
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterators created before the modification.
	 * 
	 * @see #depthFirstIterator
	 * @return an iterator for traversing the tree in breadth-first order
	 */
	public Iterator<TreeNode> breadthFirstIterator() {
		return new BreadthFirstIterator(this);
	}

	public List<TreeNode> breadthFirstList() {
		List<TreeNode> list = new LinkedList<TreeNode>();
		Iterator<TreeNode> breadthFirstItr = breadthFirstIterator();
		while (breadthFirstItr.hasNext()) {
			list.add(breadthFirstItr.next());
		}
		return list;
	}

	/**
	 * 
	 * @return a list of this node's children, or empty list if it is a leaf
	 */
	public List<TreeNode> children() {
		if (children == null) {
			return EMPTY_LIST;
		} else {
			return children;
		}
	}

	public List<String> getChildrensKeys() {
		return childrenKeys;
	}

	/**
	 * Creates and returns a forward-order iterator of this node's children.
	 * Modifying this node's child array invalidates any child iterators created
	 * before the modification.
	 * 
	 * @return an iterator of this node's children
	 */
	public Iterator<TreeNode> childrenIterator() {
		if (children == null) {
			return EMPTY_ITERATOR;
		} else {
			return children.iterator();
		}
	}

	/**
	 * Makes a deep copy of not only the Tree structure but of the user object
	 * as well.
	 * 
	 * @return A deep copy of the tree structure and its user object.
	 */
	public TreeNode deepCopy() {
		TreeNode dst = new TreeNode(this.getObject(), key);
		for (TreeNode child : children()) {
			dst.add(child.deepCopy());
		}
		return dst;
	}

	/**
	 * Creates and returns an iterator that traverses the subtree rooted at this
	 * node in depth-first order. The first node returned by the iterator's
	 * <code>next()</code> method is the leftmost leaf. This is the same as a
	 * postorder traversal.
	 * <P>
	 * 
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterators created before the modification.
	 * 
	 * @see #breadthFirstIterator
	 * @see #postorderIterator
	 * @return an iterator for traversing the tree in depth-first order
	 */
	public Iterator<TreeNode> depthFirstIterator() {
		return postorderIterator();
	}

	/**
	 * Returns the child at the specified index in this node's child array.
	 * 
	 * @param index
	 *            an index into this node's child array
	 * @exception ArrayIndexOutOfBoundsException
	 *                if <code>index</code> is out of bounds
	 * @return the TreeNode in this node's child array at the specified index
	 */
	public TreeNode getChild(int index) {
		if (children == null) {
			throw new ArrayIndexOutOfBoundsException("node has no children");
		}
		return children.get(index);
	}

	/**
	 * Returns the child in this node's child array that immediately follows
	 * <code>aChild</code>, which must be a child of this node. If
	 * <code>aChild</code> is the last child, returns null. This method performs
	 * a linear search of this node's children for <code>aChild</code> and is
	 * O(n) where n is the number of children; to traverse the entire array of
	 * children, use an enumeration instead.
	 * 
	 * @see #children
	 * @exception IllegalArgumentException
	 *                if <code>aChild</code> is null or is not a child of this
	 *                node
	 * @return the child of this node that immediately follows
	 *         <code>aChild</code>
	 */
	public TreeNode getChildAfter(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = indexOf(aChild); // linear search

		if (index == -1) {
			throw new IllegalArgumentException("node is not a child");
		}

		if (index < getChildCount() - 1) {
			return getChild(index + 1);
		} else {
			return null;
		}
	}

	/**
	 * Returns the child in this node's child array that immediately precedes
	 * <code>aChild</code>, which must be a child of this node. If
	 * <code>aChild</code> is the first child, returns null. This method
	 * performs a linear search of this node's children for <code>aChild</code>
	 * and is O(n) where n is the number of children.
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>aChild</code> is null or is not a child of this
	 *                node
	 * @return the child of this node that immediately precedes
	 *         <code>aChild</code>
	 */
	public TreeNode getChildBefore(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = indexOf(aChild); // linear search

		if (index == -1) {
			throw new IllegalArgumentException("argument is not a child");
		}

		if (index > 0) {
			return getChild(index - 1);
		} else {
			return null;
		}
	}

	/**
	 * Returns the number of children of this node.
	 * 
	 * @return an int giving the number of children of this node
	 */
	public int getChildCount() {
		if (children == null) {
			return 0;
		} else {
			return children.size();
		}
	}

	/**
	 * Returns the depth of the tree rooted at this node -- the longest distance
	 * from this node to a leaf. If this node has no children, returns 0. This
	 * operation is much more expensive than <code>getLevel()</code> because it
	 * must effectively traverse the entire tree rooted at this node.
	 * 
	 * @see #getLevel
	 * @return the depth of the tree whose root is this node
	 */
	public int getDepth() {
		TreeNode last = null;
		Iterator<TreeNode> itr = breadthFirstIterator();

		while (itr.hasNext()) {
			last = itr.next();
		}

		if (last == null) {
			throw new Error("nodes should be null");
		}

		return last.getLevel() - getLevel();
	}

	/**
	 * Returns this node's first child. If this node has no children, throws
	 * NoSuchElementException.
	 * 
	 * @return the first child of this node
	 * @exception NoSuchElementException
	 *                if this node has no children
	 */
	public TreeNode getFirstChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChild(0);
	}

	/**
	 * Finds and returns the first leaf that is a descendant of this node --
	 * either this node or its first child's first leaf. Returns this node if it
	 * is a leaf.
	 * 
	 * @see #isLeaf
	 * @see #isNodeDescendant
	 * @return the first leaf in the subtree rooted at this node
	 */
	public TreeNode getFirstLeaf() {
		TreeNode node = this;

		while (!node.isLeaf()) {
			node = node.getFirstChild();
		}

		return node;
	}

	/**
	 * Returns this node's last child. If this node has no children, throws
	 * NoSuchElementException.
	 * 
	 * @return the last child of this node
	 * @exception NoSuchElementException
	 *                if this node has no children
	 */
	public TreeNode getLastChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChild(getChildCount() - 1);
	}

	/**
	 * Finds and returns the last leaf that is a descendant of this node --
	 * either this node or its last child's last leaf. Returns this node if it
	 * is a leaf.
	 * 
	 * @see #isLeaf
	 * @see #isNodeDescendant
	 * @return the last leaf in the subtree rooted at this node
	 */
	public TreeNode getLastLeaf() {
		TreeNode node = this;

		while (!node.isLeaf()) {
			node = node.getLastChild();
		}

		return node;
	}

	/**
	 * returns the leaves in a Tree in the order by the natural left to right.
	 * 
	 * @return the leaves in a Tree in the order by the natural left to right.
	 */
	public List<TreeNode> getLeaves() {
		List<TreeNode> list = new LinkedList<TreeNode>();
		Iterator<TreeNode> leavesItr = leavesIterator();
		while (leavesItr.hasNext()) {
			list.add(leavesItr.next());
		}
		return list;
	}

	/**
	 * Gets a List of the data in the tree's leaves. The Object of all leaf
	 * nodes is returned as a list ordered by the natural left to right order of
	 * the leaves. Null values, if any, are inserted into the list like any
	 * other value.
	 * 
	 * @return a List of the data in the tree's leaves.
	 */
	public List<Object> getLeafObjects() {
		List<Object> list = new LinkedList<Object>();
		Iterator<TreeNode> leavesItr = leavesIterator();
		while (leavesItr.hasNext()) {
			list.add(leavesItr.next().getObject());
		}
		return list;
	}

	/**
	 * Returns the number of levels above this node -- the distance from the
	 * root to this node. If this node is the root, returns 0.
	 * 
	 * @see #getDepth
	 * @return the number of levels above this node
	 */
	public int getLevel() {
		TreeNode ancestor = this;
		int levels = 0;

		while ((ancestor = ancestor.getParent()) != null) {
			levels++;
		}

		return levels;
	}

	/**
	 * Returns the next sibling of this node in the parent's children array.
	 * Returns null if this node has no parent or is the parent's last child.
	 * This method performs a linear search that is O(n) where n is the number
	 * of children; to traverse the entire array, use the parent's child
	 * enumeration instead.
	 * 
	 * @see #children
	 * @return the sibling of this node that immediately follows this node
	 */
	public TreeNode getNextSibling() {
		TreeNode retval;

		TreeNode myParent = getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = myParent.getChildAfter(this); // linear search
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}

	/**
	 * Returns this node's user object.
	 * 
	 * @return the Object stored at this node by the user
	 * @see #setObject
	 * @see #toString
	 */
	public Object getObject() {
		return obj;
	}

	/**
	 * Returns this node's parent or null if this node has no parent.
	 * 
	 * @return this node's parent TreeNode, or null if this node has no parent
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Returns the path from this node to the root. The first element in the
	 * path is this node.
	 * 
	 * @return a list of TreeNode objects giving the path, where the first
	 *         element in the path is this node and the last element is the
	 *         root.
	 */
	public List<TreeNode> getPathToRoot() {
		List<TreeNode> elderList = new LinkedList<TreeNode>();
		for (TreeNode p = this; p != null; p = p.getParent()) {
			elderList.add(p);
		}
		return elderList;
	}

	/**
	 * Returns the path from the root, to get to this node. The last element in
	 * the path is this node.
	 * 
	 * @return a list of TreeNode objects giving the path, where the first
	 *         element in the path is the root and the last element is this
	 *         node.
	 */
	public List<TreeNode> getPathFromRoot() {
		List<TreeNode> elderList = getPathToRoot();
		Collections.reverse(elderList);
		return elderList;
	}

	public boolean hasNextSiblingNode() {
		return getNextSibling() != null;
	}

	/**
	 * Returns the index of the specified child in this node's child array. If
	 * the specified node is not a child of this node, returns <code>-1</code>.
	 * This method performs a linear search and is O(n) where n is the number of
	 * children.
	 * 
	 * @param aChild
	 *            the TreeNode to search for among this node's children
	 * @exception IllegalArgumentException
	 *                if <code>aChild</code> is null
	 * @return an int giving the index of the node in this node's child array,
	 *         or <code>-1</code> if the specified node is a not a child of this
	 *         node
	 */
	public int indexOf(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		if (!isNodeChild(aChild)) {
			return -1;
		}
		return children.indexOf(aChild); // linear search
	}

	/**
	 * Returns true if this node has no children. To distinguish between nodes
	 * that have no children and nodes that <i>cannot</i> have children (e.g. to
	 * distinguish files from empty directories), use this method in conjunction
	 * with <code>getAllowsChildren</code>
	 * 
	 * @see #getAllowsChildren
	 * @return true if this node has no children
	 */
	public boolean isLeaf() {
		return (getChildCount() == 0);
	}

	/**
	 * Returns true if <code>anotherNode</code> is an ancestor of this node --
	 * if it is this node, this node's parent, or an ancestor of this node's
	 * parent. (Note that a node is considered an ancestor of itself.) If
	 * <code>anotherNode</code> is null, this method returns false. This
	 * operation is at worst O(h) where h is the distance from the root to this
	 * node.
	 * 
	 * @see #isNodeDescendant
	 * @see #getSharedAncestor
	 * @param anotherNode
	 *            node to test as an ancestor of this node
	 * @return true if this node is a descendant of <code>anotherNode</code>
	 */
	public boolean isNodeAncestor(TreeNode anotherNode) {
		if (anotherNode == null) {
			return false;
		}

		TreeNode ancestor = this;

		do {
			if (ancestor == anotherNode) {
				return true;
			}
		} while ((ancestor = ancestor.getParent()) != null);

		return false;
	}

	/**
	 * Returns true if <code>aNode</code> is a child of this node. If
	 * <code>aNode</code> is null, this method returns false.
	 * 
	 * @return true if <code>aNode</code> is a child of this node; false if
	 *         <code>aNode</code> is null
	 */
	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;

		if (aNode == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}

		return retval;
	}

	/**
	 * Returns true if <code>anotherNode</code> is a sibling of (has the same
	 * parent as) this node. A node is its own sibling. If
	 * <code>anotherNode</code> is null, returns false.
	 * 
	 * @param anotherNode
	 *            node to test as sibling of this node
	 * @return true if <code>anotherNode</code> is a sibling of this node
	 */
	public boolean isNodeSibling(TreeNode anotherNode) {
		boolean retval;

		if (anotherNode == null) {
			retval = false;
		} else if (anotherNode == this) {
			retval = true;
		} else {
			TreeNode myParent = getParent();
			retval = (myParent != null && myParent == anotherNode.getParent());

			if (retval && !(getParent()).isNodeChild(anotherNode)) {
				throw new Error("sibling has different parent");
			}
		}

		return retval;
	}

	/**
	 * Returns true if this node is the root of the tree. The root is the only
	 * node in the tree with a null parent; every tree has exactly one root.
	 * 
	 * @return true if this node is the root of its tree
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	public Iterator<TreeNode> iterator() {
		return preorderIterator();
	}

	public Iterator<TreeNode> leavesIterator() {
		return new LeavesIterator(this);
	}

	/**
	 * Creates and returns an iterator that traverses the subtree rooted at this
	 * node in postorder. The first node returned by the iterator's
	 * <code>next()</code> method is the leftmost leaf. This is the same as a
	 * depth-first traversal.
	 * <P>
	 * 
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterators created before the modification.
	 * 
	 * @see #depthFirstIterator
	 * @see #preorderIterator
	 * @return an iterator for traversing the tree in postorder
	 */
	public Iterator<TreeNode> postorderIterator() {
		return new PostorderIterator(this);
	}

	public List<TreeNode> postorderList() {
		List<TreeNode> list = new LinkedList<TreeNode>();
		Iterator<TreeNode> preorderItr = postorderIterator();
		while (preorderItr.hasNext()) {
			list.add(preorderItr.next());
		}
		return list;
	}

	/**
	 * Creates and returns an iterator that traverses the subtree rooted at this
	 * node in preorder. The first node returned by the iterator's
	 * <code>next()</code> method is this node.
	 * <P>
	 * 
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 * 
	 * @see #postorderIterator
	 * @return an iterator for traversing the tree in preorder
	 */
	public Iterator<TreeNode> preorderIterator() {
		return new PreorderIterator(this);
	}

	public List<TreeNode> preorderList() {
		List<TreeNode> list = new LinkedList<TreeNode>();
		Iterator<TreeNode> preorderItr = preorderIterator();
		while (preorderItr.hasNext()) {
			list.add(preorderItr.next());
		}
		return list;
	}

	/**
	 * Removes the child at the specified index from this node's children and
	 * sets that node's parent to null. The child node to remove must be a
	 * <code>MutableTreeNode</code>.
	 * 
	 * @param childIndex
	 *            the index in this node's child array of the child to remove
	 * @exception ArrayIndexOutOfBoundsException
	 *                if <code>childIndex</code> is out of bounds
	 */
	public void remove(int childIndex) {
		TreeNode child = getChild(childIndex);
		children.remove(childIndex);
		child.setParent(null);
	}

	/**
	 * Removes <code>aChild</code> from this node's child array, giving it a
	 * null parent.
	 * 
	 * @param aChild
	 *            a child of this node to remove
	 * @exception IllegalArgumentException
	 *                if <code>aChild</code> is null or is not a child of this
	 *                node
	 */
	public void remove(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		if (!isNodeChild(aChild)) {
			throw new IllegalArgumentException("argument is not a child");
		}
		remove(indexOf(aChild)); // linear search
	}

	public void reversal() {
		if (children == null) {
			return;
		} else {
			Collections.reverse(this.children);
			for (TreeNode child : children) {
				child.reversal();
			}
		}
	}

	/**
	 * Sets the user object for this node to <code>obj</code>.
	 * 
	 * @param obj
	 *            the Object that constitutes this node's user-specified data
	 * @see #getObject
	 * @see #toString
	 */
	public void setObject(Object obj) {
		this.obj = obj;
	}

	/**
	 * Sets this node's parent to <code>newParent</code> but does not change the
	 * parent's child array. This method is called from <code>insert()</code>
	 * and <code>remove()</code> to reassign a child's parent, it should not be
	 * messaged from anywhere else.
	 * 
	 * @param newParent
	 *            this node's new parent
	 */
	public void setParent(TreeNode newParent) {
		parent = newParent;
	}

	/**
	 * Returns true if <code>anotherNode</code> is dominated by this node.
	 * Object equality (==) rather than .equals() is used to determine
	 * domination. t.dominates(t) returns false.
	 */
	public boolean dominates(TreeNode anotherNode) {
		return !(getDominationPath(anotherNode).isEmpty());
	}

	/**
	 * Returns the path of nodes leading down to a dominated node, including
	 * <code>this</code> and the dominated node itself. Returns null if t is not
	 * dominated by <code>this</code>. Object equality (==) is the relevant
	 * criterion. t.dominationPath(t) returns emptyList.
	 */
	public List<TreeNode> getDominationPath(TreeNode t) {
		TreeNode[] result = getDominationPath(t, 0);
		if (result == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(result);
	}

	private TreeNode[] getDominationPath(TreeNode t, int depth) {
		if (this == t) {
			TreeNode[] result = new TreeNode[depth + 1];
			result[depth] = this;
			return result;
		}
		List<TreeNode> kids = children();
		for (int i = kids.size() - 1; i >= 0; i--) {
			TreeNode t1 = kids.get(i);
			if (t1 == null) {
				return null;
			}
			TreeNode[] result;
			if ((result = t1.getDominationPath(t, depth + 1)) != null) {
				result[depth] = this;
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the result of sending <code>toString()</code> to this node's user
	 * object, or null if this node has no user object.
	 * 
	 * @see #getObject
	 */
	@Override
	public String toString() {
		if (obj == null) {
			return null;
		} else {
			return obj.toString();
		}
	}
}
