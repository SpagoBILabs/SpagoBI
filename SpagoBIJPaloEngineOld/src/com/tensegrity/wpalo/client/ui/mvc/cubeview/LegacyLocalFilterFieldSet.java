///*
//*
//* @file LocalFilterFieldSet.java
//*
//* Copyright (C) 2006-2009 Tensegrity Software GmbH
//*
//* This program is free software; you can redistribute it and/or modify it
//* under the terms of the GNU General Public License (Version 2) as published
//* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
//*
//* This program is distributed in the hope that it will be useful, but WITHOUT
//* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
//* more details.
//*
//* You should have received a copy of the GNU General Public License along with
//* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
//* Place, Suite 330, Boston, MA 02111-1307 USA
//*
//* If you are developing and distributing open source applications under the
//* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
//* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
//* and distribute their source code under the GPL, Tensegrity provides a flexible
//* OEM Commercial License.
//*
//* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
//*
//* @version $Id: LegacyLocalFilterFieldSet.java,v 1.1 2010/01/13 08:02:41 PhilippBouillon Exp $
//*
//*/
//
//package com.tensegrity.wpalo.client.ui.mvc.cubeview;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.extjs.gxt.ui.client.Events;
//import com.extjs.gxt.ui.client.Registry;
//import com.extjs.gxt.ui.client.Style.Orientation;
//import com.extjs.gxt.ui.client.Style.Scroll;
//import com.extjs.gxt.ui.client.data.LoadEvent;
//import com.extjs.gxt.ui.client.data.ModelData;
//import com.extjs.gxt.ui.client.event.BaseEvent;
//import com.extjs.gxt.ui.client.event.ComponentEvent;
//import com.extjs.gxt.ui.client.event.KeyListener;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.LoadListener;
//import com.extjs.gxt.ui.client.event.MessageBoxEvent;
//import com.extjs.gxt.ui.client.event.WindowEvent;
//import com.extjs.gxt.ui.client.util.Margins;
//import com.extjs.gxt.ui.client.widget.ComponentHelper;
//import com.extjs.gxt.ui.client.widget.Dialog;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.MessageBox;
//import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.button.ToggleButton;
//import com.extjs.gxt.ui.client.widget.form.CheckBox;
//import com.extjs.gxt.ui.client.widget.form.FieldSet;
//import com.extjs.gxt.ui.client.widget.layout.LayoutData;
//import com.extjs.gxt.ui.client.widget.layout.RowData;
//import com.extjs.gxt.ui.client.widget.layout.RowLayout;
//import com.extjs.gxt.ui.client.widget.tree.Tree;
//import com.extjs.gxt.ui.client.widget.tree.TreeItem;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Label;
//import com.tensegrity.palo.gwt.core.client.models.XObject;
//import com.tensegrity.palo.gwt.core.client.models.admin.XUser;
//import com.tensegrity.palo.gwt.core.client.models.cubeviews.XAxisHierarchy;
//import com.tensegrity.palo.gwt.core.client.models.cubeviews.XVirtualElement;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElement;
//import com.tensegrity.palo.gwt.core.client.models.palo.XElementNode;
//import com.tensegrity.wpalo.client.services.cubeview.WPaloCubeViewServiceProvider;
//import com.tensegrity.wpalo.client.ui.model.TreeNode;
//import com.tensegrity.wpalo.client.ui.model.XObjectModel;
//import com.tensegrity.wpalo.client.ui.mvc.workbench.Workbench;
//
//class LegacyLocalFilterFieldSet extends FieldSet implements Listener<ComponentEvent> {
//	static final String FILTER_ON_RIGHT = "rightFilter";
//	
//	private static final String BTN_ADD = "icons-move_right_on";
//	private static final String BTN_DELETE = "icons-delete_on";
//	private static final String BTN_MOVE_UP = "icons-move_up_on";
//	private static final String BTN_MOVE_DOWN = "icons-move_down_on";
//	private static final String BTN_CONSTRAINT = "icons-sum"; 
//
//	private static final String BTN_ADD_NODE = "icons-add_on";
//	private static final String BTN_REMOVE_NODE = "icons-remove_on"; 
//	
//	private static final String BTN_ONE = "1";
//	private static final String BTN_TWO = "2";
//	private static final String BTN_THREE = "3";
//	private static final String BTN_FOUR = "4";
//	private static final String BTN_FIVE = "5";
//	private static final String BTN_LEAVES = "B";
//	
//	private HierarchyTree sourceHierarchyTree;
//	private DnDHierarchyTree targetHierarchyTree;
//	private LayoutContainer filterPanel;
//	private XAxisHierarchy xAxisHierarchy;
//	private Button addFilterButton;
//	private CheckBox showOnRight;
//	
//	public LegacyLocalFilterFieldSet() {
//		super();
//		init();
//	}
//	
//	private final void init() {		
//		filterPanel = createLocalFilter();
//		add(filterPanel);
//	}
//	
//	public void collapse() {
//	}
//	
//	public void setExpanded(boolean expand) {
//		super.setExpanded(expand);
//		expand();
//		filterPanel.setEnabled(expand);
//	}
//	public boolean isExpanded() {
//		return filterPanel.isEnabled();
//	}
//	
//	public void setEnabled(boolean doIt) {
//		setExpanded(doIt);
//	}
//	
//	static final String getPath(TreeNode node) {
//		// TODO Root node repetitions: What happens if we have
//		// "Europe", "Europe" on the root level (need to add (2) to the
//		// second Europe).
//		if (node == null || !(node.getXObject() instanceof XElementNode)) {
//			return "";
//		}
//		StringBuffer buffer = new StringBuffer();				
//		buffer.append(((XElementNode) node.getXObject()).getElement().getId());
//		if (node.getParent() != null) {
//			int rep = 0;
//			for (TreeNode kid: node.getParent().getChildren()) {
//				if (kid.equals(node)) {
//					break;
//				}
//				if (((XElementNode) kid.getXObject()).getElement().equals(
//						((XElementNode) node.getXObject()).getElement())) {
//					rep++;
//				}					
//			}
//			if (rep != 0) {
//				buffer.append("(");
//				buffer.append(rep);
//				buffer.append(")");
//			}
//			buffer.append(":");
//			return getPath(node.getParent()) + buffer.toString();
//		} else {
//			buffer.append(":");
//		}
//		return buffer.toString();
//	}
//	
//	/**
//	 * Returns the root nodes of the selected elements tree
//	 * @return
//	 */
//	public final Object [] getVisibleElements() {
//		final List<XElementNode> roots = new ArrayList<XElementNode>();
//		final Map<TreeItem, XElementNode> parents = new HashMap<TreeItem, XElementNode>();		
//		FastMSTreeItemVisitor visitor = new FastMSTreeItemVisitor() {
//			public boolean visit(TreeItem item, TreeItem parent) {
//				XElementNode elNode = getElementNodeCopyFrom(item);
//				elNode.removeChildren();
//				parents.put(item, elNode);
//				XElementNode xParent = getParent(parent, parents); //parents.get(parent);
//				if(xParent == null)
//					roots.add(elNode);
//				else {
//					xParent.addChild(elNode);
//					elNode.setParent(xParent);
//				}
//				return true;
//			}
//		};
//		StringBuffer paths = new StringBuffer();
//
//		//if right side is empty, we take selection of left side:
//		if(targetHierarchyTree.isEmpty()) {
//			final List <String> filterPaths = new ArrayList <String>();
//			final List <TreeNode> currentSelection = sourceHierarchyTree.getSelection(); 
//			sourceHierarchyTree.traverse(new FastMSTreeItemVisitor(){
//				public boolean visit(TreeItem item, TreeItem parent) {
//					TreeNode node = (TreeNode) item.getModel();
//					if (currentSelection.contains(node)) {
//						filterPaths.add(getPath(node));
//					}
//					return item.hasChildren();
//				}
//			});
//			for (String f: filterPaths) {
//				paths.append(f);
//				paths.append(",");
//			}
//			sourceHierarchyTree.traverse(visitor);
//			
//			xAxisHierarchy.addProperty("filterPaths", paths.toString());
//		} else {
//			final List <String> filterPaths = new ArrayList <String>();
//			targetHierarchyTree.traverse(new FastMSTreeItemVisitor(){
//				public boolean visit(TreeItem item, TreeItem parent) {
//					XObjectModel node = (XObjectModel) item.getModel();
//					String path = node.get("filterPath");
//					if (path != null) {
//						filterPaths.add(path);
//					}
//					return item.hasChildren();
//				}
//			});
//			for (String f: filterPaths) {
//				paths.append(f);
//				paths.append(",");
//			}
//			targetHierarchyTree.traverse(visitor);
//			xAxisHierarchy.addProperty("filterPaths", paths.toString());
//		}
//
//		return new Object [] {roots.toArray(new XElementNode[0]), paths.toString()};
//	}
//	private final XElementNode getElementNodeCopyFrom(TreeItem item) {
//		XObject xObject; 
//		ModelData model = item.getModel();
//		if(model instanceof TreeNode)
//			xObject = ((TreeNode) model).getXObject();
//		else
//			xObject = ((XObjectModel) model).getXObject();
//		XElementNode oldXElementNode = (XElementNode) xObject;
//		// We need to return a copy of the element node in case
//		// we have multiple consolidations.
//		XElementNode newXElementNode = new XElementNode(oldXElementNode.getElement(), oldXElementNode.getAxisHierarchy());
//		return newXElementNode;
//	}
//	private final XElementNode getParent(TreeItem item, Map<TreeItem, XElementNode> parents) {
//		while(!parents.containsKey(item)) {
//			if(item == null) break;
//			item = item.getParentItem();
//		}
//		return parents.get(item);
//	}
//	public final void setInput(final XAxisHierarchy hierarchy,
//			final XElementNode[] initialSelectedElements) {
//		xAxisHierarchy = hierarchy;
////		if (initialSelectedElements != null && initialSelectedElements.length > 0) {
////			xAxisHierarchy.setVisibleElements(initialSelectedElements);
////		}
//		final String ior = xAxisHierarchy.getProperty(FILTER_ON_RIGHT);
//		final String paths = xAxisHierarchy.getProperty("filterPaths");
//		final boolean initOnRight = ior != null && Boolean.parseBoolean(ior);
//		if (showOnRight != null) {
//			showOnRight.setValue(ior != null && Boolean.parseBoolean(ior));
//		}		
////		targetHierarchyTree.applyAlias(xAxisHierarchy);
//		String sessionId = ((Workbench)Registry.get(Workbench.ID)).getUser().getSessionId();
//		WPaloCubeViewServiceProvider.getInstance().checkLocalFilter(sessionId, hierarchy,
//				initialSelectedElements, new AsyncCallback<Boolean>() {
//					public void onSuccess(Boolean result) {
//						final boolean doRight = initOnRight || !result;
//						sourceHierarchyTree.addLoadListener(new LoadListener() {			
//							public void loaderLoad(LoadEvent le) {
//								sourceHierarchyTree.apply(initialSelectedElements,
//										targetHierarchyTree, doRight, paths, xAxisHierarchy);
//								sourceHierarchyTree.removeLoadListener(this);
//							}
//						});
//						sourceHierarchyTree.setInput(hierarchy);
//					}
//					
//					public void onFailure(Throwable arg0) {
//						sourceHierarchyTree.addLoadListener(new LoadListener() {			
//							public void loaderLoad(LoadEvent le) {
//								sourceHierarchyTree.apply(initialSelectedElements,
//										targetHierarchyTree, /*initOnRight*/ true, paths, xAxisHierarchy);
//								sourceHierarchyTree.removeLoadListener(this);
//							}
//						});
//						sourceHierarchyTree.setInput(hierarchy);
//					}
//				});		
//	}
//	
////	final void activate(boolean doIt) {
////		setExpanded(doIt);
////	}
//	
//	final void reset() {
//		targetHierarchyTree.reset();
////		if(hierarchyTree != null)
////			hierarchyTree.reset();
//	}
////	private final boolean isActive() {
////		return filterPanel.isEnabled();
////	}
//
//	private final LayoutContainer createLocalFilter() {
//		LayoutContainer panel = new LayoutContainer();
//		panel.setSize(550, 280);		
//		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
//		
//		//source tree:
//		panel.add(createSourcePanel());
//		
//		//buttons:
//		RowData btnPanelData = new RowData();
//		btnPanelData.setMargins(new Margins(0, 0, 0, 5));
//		panel.add(createButtonsPanel(), btnPanelData);
//		
//		//target tree:
//		RowData data = new RowData();
//		data.setMargins(new Margins(0, 0, 0, 10));
//		LayoutContainer targetTree = createTargetPanel();
//		panel.add(targetTree, data);
//		
//		return panel;
//	}
//	private final LayoutContainer createSourcePanel() {
//		//tree panel:
//		LayoutContainer treePanel = createTreePanel();		
//		sourceHierarchyTree = new HierarchyTree();
//		Tree hTree = sourceHierarchyTree.getTree();
//		treePanel.add(hTree);
//
//		//buttons:
//		LayoutContainer buttonRow = createSelectionButtons();
//		
//		//all together
//		return arrangeCaptionTreeAndButtons(
//				"Select elements including original hierarchy", treePanel, buttonRow);
//	}
//	private final LayoutContainer createSelectionButtons() {
//		LayoutContainer panel = new LayoutContainer();		
//		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
//		panel.setSize(220, 22); //have to hard code to make it visible in FF...
//		RowData layoutData = new RowData();
//		layoutData.setMargins(new Margins(0, 5, 0, 0));
//		addButton(BTN_ONE, panel, layoutData, true, "Show/select level 1");
//		addButton(BTN_TWO, panel, layoutData, true, "Show/select level 2");
//		addButton(BTN_THREE, panel, layoutData, true, "Show/select level 3");
//		addButton(BTN_FOUR, panel, layoutData, true, "Show/select level 4");
//		addButton(BTN_FIVE, panel, layoutData, true, "Show/select level 5");
//		addButton(BTN_LEAVES, panel, layoutData, true, "Show/select leaves");
//		return panel;
//	}
//
//	private final LayoutContainer createTargetPanel() {
//		//tree panel:
//		LayoutContainer treePanel = createTreePanel();
//		targetHierarchyTree = new DnDHierarchyTree();
//		treePanel.add(targetHierarchyTree.getTree());
//
//		targetHierarchyTree.getTree().addListener(Events.SelectionChange,
//				new Listener(){
//					public void handleEvent(BaseEvent be) {
//						if (addFilterButton == null) {
//							return;
//						}
//						int selectionCount = 0;
//						if (targetHierarchyTree.getTree().getSelectedItems() != null) {
//							selectionCount = targetHierarchyTree.getTree().getSelectedItems().size();
//						}
//						if (selectionCount <= 1) {
//							addFilterButton.setEnabled(true);
//						} else {
//							addFilterButton.setEnabled(false);
//						}
//					}
//				});
//		
//		//button row:
//		LayoutContainer buttonRow = createAddRemoveButtons();
//		
//		//all together
//		return arrangeCaptionTreeAndButtons(
//				"Create customized hierarchy by D&D", treePanel, buttonRow);
//	}
//	private final LayoutContainer createAddRemoveButtons() {
//		LayoutContainer panel = new LayoutContainer();		
//		XUser user = ((Workbench)Registry.get(Workbench.ID)).getUser(); 
//		panel.setLayout(new RowLayout(Orientation.HORIZONTAL));
//		panel.setSize(220, 22); //have to hard code to make it visible in FF...
//		RowData layoutData = new RowData();
//		layoutData.setMargins(new Margins(0, 5, 0, 0));
//		showOnRight = new CheckBox();
//		showOnRight.setBoxLabel("Always show filter on right side");
//		showOnRight.setValue(true);
//		panel.add(showOnRight, layoutData);
////		addIconButton(BTN_ADD_NODE, panel, layoutData);
////		addIconButton(BTN_REMOVE_NODE, panel, layoutData);
//		return panel;
//	}
//	
//	private final LayoutContainer createTreePanel() {
//		LayoutContainer treePanel = new LayoutContainer();
//		treePanel.setSize(260, 230);
//		treePanel.setScrollMode(Scroll.AUTOY);
//		treePanel.setStyleAttribute("backgroundColor", "white");
//		return treePanel;
//	}
//	private final LayoutContainer arrangeCaptionTreeAndButtons(String caption,
//			LayoutContainer treePanel, LayoutContainer buttonRow) {
//		LayoutContainer content = new LayoutContainer();
//		content.setHeight(280);
//		content.setLayout(new RowLayout());
//		RowData layoutData = new RowData();
//		layoutData.setMargins(new Margins(5, 0, 0, 0));
//		
//		content.add(new Label(caption));
//		content.add(treePanel, layoutData);
//		content.add(buttonRow, layoutData);
//
//		return content;
//	}
//
//	
//	private final void addButton(String caption, LayoutContainer panel, LayoutData layoutData, boolean toggle, String tooltip) {
//		Button btn = toggle ? new ToggleButton(caption) : new Button(caption);
//		btn.setId(caption);
//		btn.addListener(Events.Select, this);
//		btn.setToolTip(tooltip);
//		panel.add(btn, layoutData);
//	}
//	
//	private final LayoutContainer createButtonsPanel() {
//		LayoutContainer buttons = new LayoutContainer();
//		buttons.setAutoHeight(true);
//		buttons.setLayout(new RowLayout());
//		RowData data = new RowData();
//		data.setMargins(new Margins(0, 0, 25, 6));
//		buttons.add(new Label("Or"), data);
//		
//		RowData layoutData = new RowData();
//		layoutData.setMargins(new Margins(0, 0, 5, 0));		
//		addFilterButton = addIconButton(BTN_ADD, buttons, layoutData, "Add element to filter");
//		addIconButton(BTN_DELETE, buttons, layoutData, "Remove element from filter");
//		addIconButton(BTN_MOVE_UP, buttons, layoutData, "Move element up");
//		addIconButton(BTN_MOVE_DOWN, buttons, layoutData, "Move element down");
//		RowData constraintBtnData = new RowData();
//		constraintBtnData.setMargins(new Margins(15, 0, 0, 0));
////		addIconButton(BTN_CONSTRAINT, buttons, constraintBtnData);
//		return buttons;
//	}
//	private final Button addIconButton(String icon, LayoutContainer panel, LayoutData layoutData, String tooltip) {
//		Button btn = new Button();
//		btn.setIconStyle(icon);
//		btn.setId(icon);
//		btn.setToolTip(tooltip);
//		btn.addListener(Events.Select, this);
//		panel.add(btn, layoutData);
//		return btn;
//	}
//	
//	boolean isFilterOnRight() {
//		if (showOnRight == null) {
//			return false;
//		}
//		return showOnRight.getValue();
//	}
//	
//	public void handleEvent(ComponentEvent be) {
//		String btnId = be.component.getId();
//		if (btnId.equals(BTN_ADD)) {
//			addSelection();
//		} else if (btnId.equals(BTN_DELETE)) {
//			removeSelection();
//		} else if (btnId.equals(BTN_MOVE_UP)) {
//			moveUpSelection();
//		} else if (btnId.equals(BTN_MOVE_DOWN)) {
//			moveDownSelection();
//		} else if (btnId.equals(BTN_ADD_NODE)) {
//			addNode();
//		} else if (btnId.equals(BTN_REMOVE_NODE)) {
//			removeNode();
//		} else if (btnId.equals(BTN_LEAVES)) {
//			sourceHierarchyTree.selectLeafs(((ToggleButton) be.component).isPressed());
//		} else if (isLevelSelectionButton(btnId)) {
//			sourceHierarchyTree.selectLevel(getLevel(btnId),
//				((ToggleButton) be.component).isPressed());
//		}
//	}
//	
//	private final boolean isLevelSelectionButton(String id) {
//		return id.equals(BTN_ONE) || id.equals(BTN_TWO) || id.equals(BTN_THREE)
//				|| id.equals(BTN_FOUR) || id.equals(BTN_FIVE);
//	}
//	private final int getLevel(String lvl) {
//		int level = -1;
//		try {
//			level = Integer.parseInt(lvl);
//		}catch(Exception e) { /* ignore */ }
//		return level;
//	}
//		
//	final XElement[] getSelectedElements() {
//		if(isExpanded())
//			return sourceHierarchyTree.getSelectedElements();
//		return null;
//	}
//	
//	
//	private final void addSelection() {
//		final List <TreeNode> orderedSelection = new ArrayList <TreeNode>();
//		final List <TreeNode> currentSelection = sourceHierarchyTree.getSelection(); 
//		sourceHierarchyTree.traverse(new FastMSTreeItemVisitor(){
//			public boolean visit(TreeItem item, TreeItem parent) {
//				TreeNode node = (TreeNode) item.getModel();
//				if (currentSelection.contains(node)) {
//					node.set("filterPath", getPath(node));
//					orderedSelection.add(node);
//				}
//				return item.hasChildren();
//			}
//		});
//		targetHierarchyTree.addSelection(orderedSelection);
//
////		for(TreeNode node : sourceHierarchyTree.getSelection())
////			targetHierarchyTree.add(node.getXObject());
////		targetHierarchyTree.addSelection(sourceHierarchyTree.getSelection());
//	}
//	private final void removeSelection() {
//		targetHierarchyTree.removeSelection();
//		if (addFilterButton == null) {
//			return;
//		}
//		int selectionCount = 0;
//		if (targetHierarchyTree.getTree().getSelectedItems() != null) {
//			selectionCount = targetHierarchyTree.getTree().getSelectedItems().size();
//		}
//		if (selectionCount <= 1) {
//			addFilterButton.setEnabled(true);
//		} else {
//			addFilterButton.setEnabled(false);
//		}		
//	}
//	private final void moveUpSelection() {
//		targetHierarchyTree.moveUpSelection();
//	}
//	private final void moveDownSelection() {
//		targetHierarchyTree.moveDownSelection();
//	}
//	private final void addNode() {
//		final MessageBox prompt = MessageBox.prompt("Add custom element",
//				"Please specify element name:");
//		// TODO BUG IN GXT: TextField is not attached!!!
//		// => GET NO KEYEVENTS, so do it myself...
//		ComponentHelper.doAttach(prompt.getTextBox());
//		prompt.getTextBox().addKeyListener(new KeyListener() {
//			public void componentKeyUp(ComponentEvent event) {
//				super.componentKeyUp(event);
//				if (pressedEnter(event.getKeyCode())) {
//					prompt.getDialog().close(
//							prompt.getDialog().getButtonById(Dialog.OK));
//				}
//			}
//		});
//		prompt.addCallback(new Listener<WindowEvent>() {
//			public void handleEvent(WindowEvent be) {
//				// have to detach TextField too!!!
//				ComponentHelper.doDetach(prompt.getTextBox());
//				if (be.buttonClicked != null
//						&& be.buttonClicked.getItemId().equals(Dialog.OK)) {
//					createAndAddNode(((MessageBoxEvent) be).value);
//				}
//			}
//		});
//		prompt.show();
//	}
//	
//	private final boolean pressedEnter(int keyCode) {
//		return keyCode == 13; //KeyboardListener.KEY_ENTER;
//	}
//
//	private final void createAndAddNode(String name) {
//		// ATTENTION:
//		// NOTE: the id is important!!!! otherwise xvirtualelement is not unique
//		// => many problems will result, e.g. in gxt tree and my
//		// dndhierarchytree implementation!!!
//		// THIS MUST HOLD FOR ALL XOBJECTS!!!!!
//		// IN A TREE THERE SHOULD BE NO OBJECT WITH SAME ID!!!!
//		// => IF WE ALLOW TO ADD SAME XOBJECT TWICE, WE SHOULD WRAP IT!!!
//		XVirtualElement xVirtualElement = new XVirtualElement(name);
//		XElementNode xVirtualNode = new XElementNode(xVirtualElement, xAxisHierarchy);
//		targetHierarchyTree.add(xVirtualNode);
//	}
//	private final void removeNode() {
//		targetHierarchyTree.removeSelection();
//	}
//}